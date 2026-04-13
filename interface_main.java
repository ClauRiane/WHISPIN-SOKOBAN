import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class interface_main extends Application {
    private Jeu jeu;
    private MenuPrincipal menuPrincipal;

    @Override
    public void start(Stage stage) {
        jeu = new Jeu();

        menuPrincipal = new MenuPrincipal(sceneMenu -> {
            jeu = new Jeu();
            Scene sceneJeu = DeuxiemeScene.creerScene(stage, sceneMenu, jeu.getPlateau());
            stage.setScene(sceneJeu);
        });

        stage.setTitle("Sokoban");
        stage.setScene(menuPrincipal.getScene());
        stage.show();
        menuPrincipal.reprendreFocus();
    }

    public static void main(String[] args) {
        launch(args);
    }
}