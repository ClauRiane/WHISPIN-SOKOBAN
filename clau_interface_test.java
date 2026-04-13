import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class clau_interface_test extends Application {

    // ── À MODIFIER selon ta sprite sheet ──────────────────
    //private static final String IMAGE_PATH   = "/image/moving_bee.pngimage/";
    private static final int    FRAME_WIDTH  = 64;  // largeur d'une frame (px)
    private static final int    FRAME_HEIGHT = 64;  // hauteur d'une frame (px)
    private static final int    FRAME_COUNT  = 2;   // nombre de frames au total
    private static final int    COLUMNS      =28;   // frames par ligne
    private static final long   FRAME_DELAY  = 100_000_000L; // ~10 fps (en nanosecondes)
    // ──────────────────────────────────────────────────────

    private int  currentFrame = 0;
    private long lastFrameTime = 0;

    @Override
    public void start(Stage stage) {

        Image image = new Image("file:moving_bee.png");
        ImageView view = new ImageView(image);

        // Affiche la première frame au démarrage
        view.setViewport(new Rectangle2D(0, 0, FRAME_WIDTH, FRAME_HEIGHT));

        // Agrandit le sprite × 3 (optionnel)
        view.setFitWidth(FRAME_WIDTH  * 3);
        view.setFitHeight(FRAME_HEIGHT * 3);
        view.setPreserveRatio(true);

        // Boucle d'animation principale
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastFrameTime >= FRAME_DELAY) {
                    lastFrameTime = now;

                    // Calcule la position x/y dans la sprite sheet
                    int col = currentFrame % COLUMNS;
                    int row = currentFrame / COLUMNS;
                    double x = col * FRAME_WIDTH;
                    double y = row * FRAME_HEIGHT;

                    view.setViewport(new Rectangle2D(x, y, FRAME_WIDTH, FRAME_HEIGHT));

                    // Passe à la frame suivante (boucle)
                    currentFrame = (currentFrame + 1) % FRAME_COUNT;
                }
            }
        }.start();

        StackPane root = new StackPane(view);
        Scene scene = new Scene(root, 300, 300);
        stage.setTitle("Test Sprite Animation");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
