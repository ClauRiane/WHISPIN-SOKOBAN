import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Supplier;

/**
 * Contrôleur principal d'une partie en cours.
 * Il gère la boucle d'animation, les entrées clavier et le retour au menu après victoire.
 */
public class ControleurPartie {
    private static final Color FOND = Color.web("#ece8dc");

    public record NiveauSuivant(Plateau plateau, Supplier<NiveauSuivant> apres) {}
    private final Stage stage;
    private final Scene sceneMenu;
    private final Plateau plateau;
    private final ControleurAnimation controleurAnimation;
    private final FeuArtifice feuArtifice;
    private final Canvas canvas;
    private final Scene scene;
    private final AnimationTimer timer;
    private final Image imageFond;
    private final Supplier<NiveauSuivant> niveauSuivantFournisseur;
    private boolean retourMenuDemande;
    private boolean sauvegardeAutoEffectuee;

    // ── Déplacement automatique par clic souris (A*) ──────────────────────
    /** File des directions à exécuter automatiquement. */
    private final Queue<Direction> cheminAutomate = new ArrayDeque<>();
    /** Instant (ns) du dernier pas automatique exécuté. */
    private long dernierPasAutomateNs = 0;
    /** Délai entre deux pas automatiques : 120 ms. */
    private static final long DELAI_PAS_NS = 120_000_000L;

    public ControleurPartie(Stage stage, Scene sceneMenu, Plateau plateau, Supplier<NiveauSuivant> niveauSuivant) {
        this.stage = stage;
        this.sceneMenu = sceneMenu;
        this.plateau = plateau;
        this.niveauSuivantFournisseur = niveauSuivant;
        this.controleurAnimation = new ControleurAnimation();
        this.feuArtifice = new FeuArtifice();
        this.canvas = new Canvas(900, 700);

        Image img = null;
        try {
            var url = ControleurPartie.class.getResource("/fond_principale_ecran-frame0.png");
            if (url != null) img = new Image(url.toExternalForm());
        } catch (Exception ignored) {}
        this.imageFond = img;

        StackPane racine = new StackPane(canvas);
        racine.setStyle("-fx-background-color: #1a2b26;");
        this.scene = new Scene(racine, 900, 700);
        this.scene.setFill(FOND);
        this.timer = creerBouclePrincipale();

        configurerScene();
    }

    public Scene getScene() {
        return scene;
    }

    public void demarrer() {
        timer.start();
        canvas.requestFocus();
    }

    private AnimationTimer creerBouclePrincipale() {
        return new AnimationTimer() {
            @Override
            public void handle(long maintenantNs) {
                controleurAnimation.initialiserSiNecessaire(maintenantNs);
                controleurAnimation.mettreAJour(plateau.estGagne(), maintenantNs);

                // ── Avancer d'un pas dans le chemin automatique (clic souris) ──
                if (!cheminAutomate.isEmpty() && !plateau.estGagne()) {
                    if (maintenantNs - dernierPasAutomateNs >= DELAI_PAS_NS) {
                        Direction dir = cheminAutomate.poll();
                        boolean vaPousser = plateau.vaPousserBoite(dir);
                        boolean ok = plateau.deplacer(dir);
                        if (ok) {
                            controleurAnimation.notifierDeplacementReussi(
                                dir, vaPousser, plateau.estGagne(), maintenantNs);
                        } else {
                            // Obstacle inattendu (boîte déplacée entre-temps, etc.)
                            cheminAutomate.clear();
                        }
                        dernierPasAutomateNs = maintenantNs;
                    }
                }

                if (plateau.estGagne() && !sauvegardeAutoEffectuee) {
                    sauvegarderPartieAutomatique();
                    sauvegardeAutoEffectuee = true;
                }

                feuArtifice.mettreAJour(plateau.estGagne(), scene.getWidth(), scene.getHeight(), maintenantNs);
                if (plateau.estGagne() && feuArtifice.doitFermer(maintenantNs) && !retourMenuDemande) {
                    stop();
                    passerAuNiveauSuivantOuMenu();
                    return;
                }

                redessiner(maintenantNs);
            }
        };
    }

    private void configurerScene() {
        scene.widthProperty().addListener((obs, oldVal, newVal) -> redessiner(System.nanoTime()));
        scene.heightProperty().addListener((obs, oldVal, newVal) -> redessiner(System.nanoTime()));
        scene.setOnKeyPressed(evenementTouche -> gererTouche(evenementTouche, System.nanoTime()));

        canvas.setFocusTraversable(true);
        canvas.setOnMouseClicked(e -> gererClic(e.getX(), e.getY(), System.nanoTime()));
        redessiner(System.nanoTime());
    }

    private void gererTouche(javafx.scene.input.KeyEvent evenementTouche, long maintenantNs) {
        // Une touche clavier annule tout déplacement automatique en cours
        cheminAutomate.clear();

        if (evenementTouche.isControlDown() && evenementTouche.getCode() == KeyCode.S) {
            sauvegarderPartieNommee();
            redessiner(maintenantNs);
            return;
        }

        if (evenementTouche.getCode() == KeyCode.ESCAPE) {
            sauvegarderAvantRetour();
            retournerAuMenu();
            return;
        }

        if (plateau.estGagne()) {
            return;
        }

        GestionEntreeJeu.gererTouche(evenementTouche, plateau, controleurAnimation, maintenantNs);
        redessiner(maintenantNs);
    }

    /**
     * Gère un clic souris sur le canvas.
     *
     * Calcule la case cliquée, lance A* depuis la position du personnage
     * jusqu'à cette case (en évitant murs et boîtes), puis enfile les
     * directions dans {@code cheminAutomate} pour exécution progressive.
     */
    private void gererClic(double mouseX, double mouseY, long maintenantNs) {
        if (plateau.estGagne()) return;

        // 1. Convertir le clic en coordonnées de case (col=x, row=y)
        int[] caseCliquee = RenduPlateau.pixelVersCase(
            mouseX, mouseY,
            scene.getWidth(), scene.getHeight(),
            plateau.getHauteur(), plateau.getLargeur()
        );
        if (caseCliquee == null) return;

        int colCible = caseCliquee[0]; // axe X
        int rowCible = caseCliquee[1]; // axe Y

        // 2. Vérifier que la case cible est traversable (pas un mur ni une boîte)
        Case caseCible = plateau.getCase(colCible, rowCible);
        if (!caseCible.estTraversable() && !caseCible.estPersonnageCible()) return;

        // 3. Construire la grille int[][] pour A*
        //    grid[row][col] : 1 = traversable, 0 = mur ou boîte
        int rows = plateau.getHauteur();
        int cols = plateau.getLargeur();
        int[][] grid = new int[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Case cas = plateau.getCase(c, r);
                // Traversable = cases vides, cibles, ou position du personnage
                grid[r][c] = (cas.estTraversable() || cas.estPersonnageCible()) ? 1 : 0;
            }
        }

        // 4. Lancer A*
        Position posPerso = plateau.getPositionPersonnage();
        Pair src  = new Pair(posPerso.gety(), posPerso.getx()); // Pair(row, col)
        Pair dest = new Pair(rowCible, colCible);

        AAsterix astar = new AAsterix();
        List<Pair> chemin = astar.aStarSearch(grid, rows, cols, src, dest);

        if (chemin == null || chemin.isEmpty()) return;

        // 5. Convertir la liste de positions en liste de directions
        cheminAutomate.clear();
        Pair courant = src;
        for (Pair suivant : chemin) {
            int dr = suivant.getFirst()  - courant.getFirst();
            int dc = suivant.getSecond() - courant.getSecond();
            Direction dir;
            if      (dr == -1) dir = Direction.HAUT;
            else if (dr ==  1) dir = Direction.BAS;
            else if (dc == -1) dir = Direction.GAUCHE;
            else               dir = Direction.DROITE;
            cheminAutomate.add(dir);
            courant = suivant;
        }

        // Déclencher le premier pas immédiatement
        dernierPasAutomateNs = maintenantNs - DELAI_PAS_NS;
        canvas.requestFocus();
    }

    private void redessiner(long maintenantNs) {
        RenduPlateau.redessiner(canvas, scene.getWidth(), scene.getHeight(), plateau, controleurAnimation, feuArtifice, imageFond, maintenantNs);
    }

    private void sauvegarderPartieAutomatique() {
        Path cheminAuto = ServicePersistance.creerCheminSauvegardeAuto();
        sauvegarderVersChemin(cheminAuto, "Auto-sauvegarde de fin de partie", "auto-sauvegarde");
    }

    private void sauvegarderPartieNommee() {
        TextInputDialog dialogue = new TextInputDialog("partie_1");
        dialogue.setTitle("Sauvegarde");
        dialogue.setHeaderText("Nommer la sauvegarde");
        dialogue.setContentText("Nom:");

        Optional<String> resultat = dialogue.showAndWait();
        if (resultat.isEmpty()) {
            return;
        }

        Path cheminNomme = ServicePersistance.creerCheminSauvegardeNommee(resultat.get());
        sauvegarderVersChemin(cheminNomme, "Sauvegarde nommee", "sauvegarde nommee");
    }

    private void sauvegarderVersChemin(Path chemin, String libelleSucces, String libelleErreur) {
        try {
            ServicePersistance.sauvegarderPlateauDansFichierTexte(chemin, plateau.getGrille(), 'A');
            System.out.println("[Persistance] " + libelleSucces + ": " + chemin);
        } catch (Exception e) {
            System.err.println("[Persistance] Echec " + libelleErreur + ": " + e.getMessage());
        }
    }

    private void sauvegarderAvantRetour() {
        Path chemin = ServicePersistance.creerCheminSauvegardeAuto();
        sauvegarderVersChemin(chemin, "Sauvegarde avant retour menu", "sauvegarde avant retour");
    }

    private void passerAuNiveauSuivantOuMenu() {
        retourMenuDemande = true;
        timer.stop();
        if (niveauSuivantFournisseur != null) {
            NiveauSuivant ns = niveauSuivantFournisseur.get();
            if (ns != null) {
                stage.setScene(DeuxiemeScene.creerScene(stage, sceneMenu, ns.plateau(), ns.apres()));
                return;
            }
        }
        stage.setScene(sceneMenu);
        sceneMenu.getRoot().requestFocus();
    }

    private void retournerAuMenu() {
        if (retourMenuDemande) {
            return;
        }
        retourMenuDemande = true;
        timer.stop();
        stage.setScene(sceneMenu);
        sceneMenu.getRoot().requestFocus();
    }
}