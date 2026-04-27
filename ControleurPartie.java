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

/**
 * Contrôleur principal d'une partie en cours.
 * Il gère la boucle d'animation, les entrées clavier et le retour au menu après victoire.
 */
public class ControleurPartie {
    private static final Color FOND = Color.web("#ece8dc");

    private final Stage stage;
    private final Scene sceneMenu;
    private final Plateau plateau;
    private final ControleurAnimation controleurAnimation;
    private final FeuArtifice feuArtifice;
    private final Canvas canvas;
    private final Scene scene;
    private final AnimationTimer timer;
    private final Image imageFond;
    private boolean retourMenuDemande;
    private boolean sauvegardeAutoEffectuee;

    public ControleurPartie(Stage stage, Scene sceneMenu, Plateau plateau) {
        this.stage = stage;
        this.sceneMenu = sceneMenu;
        this.plateau = plateau;
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

                if (plateau.estGagne() && !sauvegardeAutoEffectuee) {
                    sauvegarderPartieAutomatique();
                    sauvegardeAutoEffectuee = true;
                }

                feuArtifice.mettreAJour(plateau.estGagne(), scene.getWidth(), scene.getHeight(), maintenantNs);
                if (plateau.estGagne() && feuArtifice.doitFermer(maintenantNs) && !retourMenuDemande) {
                    stop();
                    retournerAuMenu();
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

        if (plateau.estGagne()) {
            return;
        }

        GestionEntreeJeu.gererTouche(evenementTouche, plateau, controleurAnimation, maintenantNs);
        redessiner(maintenantNs);
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