import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

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
    private boolean retourMenuDemande;

    public ControleurPartie(Stage stage, Scene sceneMenu, Plateau plateau) {
        this.stage = stage;
        this.sceneMenu = sceneMenu;
        this.plateau = plateau;
        this.controleurAnimation = new ControleurAnimation();
        this.feuArtifice = new FeuArtifice();
        this.canvas = new Canvas(900, 700);

        StackPane racine = new StackPane(canvas);
        racine.setStyle("-fx-background-color: #ece8dc;");
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

                feuArtifice.mettreAJour(plateau.estGagne(), scene.getWidth(), scene.getHeight(), maintenantNs);
                if (plateau.estGagne() && feuArtifice.doitFermer(maintenantNs) && !retourMenuDemande) {
                    retourMenuDemande = true;
                    stop();
                    stage.setScene(sceneMenu);
                    sceneMenu.getRoot().requestFocus();
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
        if (plateau.estGagne()) {
            return;
        }

        GestionEntreeJeu.gererTouche(evenementTouche, plateau, controleurAnimation, maintenantNs);
        redessiner(maintenantNs);
    }

    private void redessiner(long maintenantNs) {
        RenduPlateau.redessiner(canvas, scene.getWidth(), scene.getHeight(), plateau, controleurAnimation, feuArtifice, maintenantNs);
    }
}