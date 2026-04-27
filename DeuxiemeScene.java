import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.function.Supplier;

public class DeuxiemeScene {
    public static Scene creerScene(Stage stage, Scene scenePrecedente, Plateau plateau, Supplier<ControleurPartie.NiveauSuivant> niveauSuivant) {
        ControleurPartie controleurPartie = new ControleurPartie(stage, scenePrecedente, plateau, niveauSuivant);
        controleurPartie.demarrer();
        return controleurPartie.getScene();
    }
}
