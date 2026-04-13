import javafx.scene.Scene;
import javafx.stage.Stage;

public class DeuxiemeScene {
    public static Scene creerScene(Stage stage, Scene scenePrecedente, Plateau plateau) {
        ControleurPartie controleurPartie = new ControleurPartie(stage, scenePrecedente, plateau);
        controleurPartie.demarrer();
        return controleurPartie.getScene();
    }
}
