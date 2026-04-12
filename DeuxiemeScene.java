import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class DeuxiemeScene {
    private static final Color FOND = Color.web("#ece8dc");

    public static Scene creerScene(Stage stage, Scene scenePrecedente, Plateau plateau) {
        Canvas canvas = new Canvas(900, 700);
        StackPane racine = new StackPane(canvas);
        racine.setStyle("-fx-background-color: #ece8dc;");
        Scene scene = new Scene(racine, 900, 700);
        scene.setFill(FOND);

        ControleurAnimation controleurAnimation = new ControleurAnimation();
        FeuArtifice feuArtifice = new FeuArtifice();
        boolean[] fermetureDemandee = {false};

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long maintenantNs) {
                controleurAnimation.initialiserSiNecessaire(maintenantNs);
                controleurAnimation.mettreAJour(plateau.estGagne(), maintenantNs);

                feuArtifice.mettreAJour(plateau.estGagne(), scene.getWidth(), scene.getHeight(), maintenantNs);
                if (plateau.estGagne() && feuArtifice.doitFermer(maintenantNs) && !fermetureDemandee[0]) {
                    fermetureDemandee[0] = true;
                    stop();
                    Platform.exit();
                    return;
                }

                RenduPlateau.redessiner(canvas, scene.getWidth(), scene.getHeight(), plateau, controleurAnimation, feuArtifice, maintenantNs);
            }
        };
        timer.start();

        scene.widthProperty().addListener((obs, oldVal, newVal) -> RenduPlateau.redessiner(canvas, scene.getWidth(), scene.getHeight(), plateau, controleurAnimation, feuArtifice, System.nanoTime()));
        scene.heightProperty().addListener((obs, oldVal, newVal) -> RenduPlateau.redessiner(canvas, scene.getWidth(), scene.getHeight(), plateau, controleurAnimation, feuArtifice, System.nanoTime()));
        RenduPlateau.redessiner(canvas, scene.getWidth(), scene.getHeight(), plateau, controleurAnimation, feuArtifice, System.nanoTime());

        scene.setOnKeyPressed(ev -> {
            if (plateau.estGagne()) {
                return;
            }

            long maintenantNs = System.nanoTime();
            GestionEntreeJeu.gererTouche(ev, plateau, controleurAnimation, maintenantNs);
            RenduPlateau.redessiner(canvas, scene.getWidth(), scene.getHeight(), plateau, controleurAnimation, feuArtifice, maintenantNs);
        });

        canvas.setOnMouseClicked(ev -> {
            // retour a l'accueil desactive pour cette branche
        });
        canvas.setFocusTraversable(true);
        canvas.requestFocus();
        return scene;
    }
}
