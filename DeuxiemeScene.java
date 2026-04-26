import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.ArrayList;

public class DeuxiemeScene {
    public static Scene creerScene(Stage stage, Scene scenePrecedente, Plateau plateau) {
        ControleurPartie controleurPartie = new ControleurPartie(stage, scenePrecedente, plateau);
        controleurPartie.demarrer();
        return controleurPartie.getScene();
    }
}
