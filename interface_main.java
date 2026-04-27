import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class interface_main extends Application {
    private MenuPrincipal menuPrincipal;

    @Override
    public void start(Stage stage) {
        menuPrincipal = new MenuPrincipal((sceneMenu, plateauCharge, multivers, niveauSuivant) -> {
            if (plateauCharge == null) {
                return;
            }
            Scene sceneJeu = DeuxiemeScene.creerScene(stage, sceneMenu, plateauCharge, multivers, niveauSuivant);
            stage.setScene(sceneJeu);
        });

        stage.setTitle("Whispin Parabox");
        stage.setScene(menuPrincipal.getScene());
        stage.show();
        menuPrincipal.reprendreFocus();
    }

    public static void main(String[] args) {
        launch(args);
    }
}