import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class interface_main extends Application {

    @Override
    public void start(Stage stage) {
            BorderPane root = new BorderPane();

            Image image = new Image("https://www.meme-arsenal.com/memes/7de1cccd58be2b01261e613c08663e9a.jpg");
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(250);
            imageView.setPreserveRatio(true);

            Label labelAvecImage = new Label ();
            labelAvecImage.setGraphic(imageView);
            labelAvecImage.setStyle("-fx-background-color: transparent;");
            
            Button bouton = new Button("Commencer");

            root.setAlignment( labelAvecImage, Pos.TOP_CENTER);
            root.setTop( labelAvecImage );
            root.setCenter(bouton);
            
            StackPane stack = new StackPane();
            Rectangle fond = new Rectangle(1000, 800,Color.LIGHTGREEN);
            stack.getChildren().addAll(fond, root);

            Scene scene = new Scene ( stack, 1000, 800);

            bouton.setOnAction(e -> {
                Scene scene2 = DeuxiemeScene.creerScene(stage, scene); 
                stage.setScene(scene2);
            });
            
            stage . setTitle ( " Ma fenetre " );
            stage . setScene ( scene );
            stage . show ();
    }

    public static void main(String[] args) {
        launch(args);
    }
}