import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class interface_main extends Application {
    private static final double LARGEUR = 1000;
    private static final double HAUTEUR = 700;
    private static final Color FOND = Color.web("#17352d");
    private Jeu jeu;

    @Override
    public void start(Stage stage) {
        jeu = new Jeu();

        Canvas canvas = new Canvas(LARGEUR, HAUTEUR);
        StackPane racine = new StackPane(canvas);
        racine.setStyle("-fx-background-color: #17352d;");
        Scene scene = new Scene(racine, LARGEUR, HAUTEUR);
        scene.setFill(FOND);

        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            redessinerAccueil(canvas, scene.getWidth(), scene.getHeight());
        });
        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            redessinerAccueil(canvas, scene.getWidth(), scene.getHeight());
        });
        redessinerAccueil(canvas, scene.getWidth(), scene.getHeight());

        canvas.setOnMouseClicked(e -> {
            Scene scene2 = DeuxiemeScene.creerScene(stage, scene, jeu.getPlateau());
            stage.setScene(scene2);
        });

        stage.setTitle("Sokoban");
        stage.setScene(scene);
        stage.show();
    }

    private void redessinerAccueil(Canvas canvas, double largeur, double hauteur) {
        canvas.setWidth(Math.max(largeur, 1));
        canvas.setHeight(Math.max(hauteur, 1));
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(FOND);
        gc.fillRect(0, 0, largeur, hauteur);

        double panneauLargeur = largeur * 0.7;
        double panneauHauteur = hauteur * 0.45;
        double panneauX = (largeur - panneauLargeur) / 2.0;
        double panneauY = (hauteur - panneauHauteur) / 2.0;

        gc.setFill(Color.web("#23483d"));
        gc.fillRoundRect(panneauX, panneauY, panneauLargeur, panneauHauteur, 24, 24);

        gc.setFill(Color.web("#e8f3ee"));
        gc.setFont(Font.font("SansSerif", FontWeight.BOLD, Math.max(28, largeur * 0.05)));
        gc.fillText("WHISPIN SOKOBAN", panneauX + panneauLargeur * 0.12, panneauY + panneauHauteur * 0.38);

        gc.setFont(Font.font("SansSerif", Math.max(16, largeur * 0.02)));
        gc.fillText("Cliquez pour commencer", panneauX + panneauLargeur * 0.24, panneauY + panneauHauteur * 0.62);
    }

    public static void main(String[] args) {
        launch(args);
    }
}