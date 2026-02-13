import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.geometry.Pos;
import javafx.stage.Stage;

public class DeuxiemeScene {

    public static Scene creerScene(Stage stage, Scene sceneRetour) {

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);

        Label label = new Label("Bienvenue dans la deuxième scène");
        Button retour = new Button("Retour au menu");

        retour.setOnAction(e -> stage.setScene(sceneRetour));

        root.getChildren().addAll(label, retour);

        return new Scene(root, 1000, 800);
    }
}

