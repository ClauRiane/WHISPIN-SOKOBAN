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
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Contrôleur principal d'une partie en cours.
 * Il gère la boucle d'animation, les entrées clavier et le retour au menu après victoire.
 */
public class ControleurPartie {
    private static final Color FOND = Color.web("#ece8dc");

    public record NiveauSuivant(Plateau plateau, Multivers multivers, Supplier<NiveauSuivant> apres) {}
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
    private Multivers multivers;

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

    /** Associe un Multivers à cette partie (pour le rendu des boîtes-mondes). */
    public void setMultivers(Multivers multivers) {
        this.multivers = multivers;
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
                Plateau racine = plateau;
                Plateau courant = (multivers != null) ? multivers.getPlateauCourant() : racine;
                boolean gagne = (multivers != null) ? multivers.estGagne() : racine.estGagne();

                controleurAnimation.initialiserSiNecessaire(maintenantNs);
                controleurAnimation.mettreAJour(gagne, maintenantNs);

                if (gagne && !sauvegardeAutoEffectuee) {
                    sauvegarderPartieAutomatique();
                    sauvegardeAutoEffectuee = true;
                }

                feuArtifice.mettreAJour(gagne, scene.getWidth(), scene.getHeight(), maintenantNs);
                if (gagne && feuArtifice.doitFermer(maintenantNs) && !retourMenuDemande) {
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
        redessiner(System.nanoTime());
    }

    private void gererTouche(javafx.scene.input.KeyEvent evenementTouche, long maintenantNs) {
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

        boolean gagne = (multivers != null) ? multivers.estGagne() : plateau.estGagne();
        if (gagne) {
            return;
        }

        if (multivers != null) {
            GestionEntreeJeu.gererTouche(evenementTouche, multivers, controleurAnimation, maintenantNs);
        } else {
            GestionEntreeJeu.gererTouche(evenementTouche, plateau, controleurAnimation, maintenantNs);
        }
        redessiner(maintenantNs);
    }

    private void redessiner(long maintenantNs) {
        Plateau aAfficher = (multivers != null) ? multivers.getPlateauCourant() : plateau;
        RenduPlateau.redessiner(canvas, scene.getWidth(), scene.getHeight(), aAfficher, multivers, controleurAnimation, feuArtifice, imageFond, maintenantNs);
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
            if (multivers != null) {
                ServicePersistance.sauvegarderMultivers(chemin, multivers);
            } else {
                ServicePersistance.sauvegarderPlateauDansFichierTexte(chemin, plateau.getGrille(), 'A');
            }
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
                stage.setScene(DeuxiemeScene.creerScene(stage, sceneMenu, ns.plateau(), ns.multivers(), ns.apres()));
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