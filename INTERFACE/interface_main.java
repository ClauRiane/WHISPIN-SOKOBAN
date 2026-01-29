import  javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javax.swing.border.Border;

public class interface_main extends Application {

    @Override
    public void start(Stage stage) {
            BorderPane root = new BorderPane();
            
            /*Label etiquette = new Label ( " Je suis un label textuel " );
            etiquette . setFont ( Font . font ( " Cambria " , 18));
            etiquette . setTextFill ( Color . DARKCYAN );
            root . setTop ( etiquette );*/

            Image image = new Image("https://www.meme-arsenal.com/memes/7de1cccd58be2b01261e613c08663e9a.jpg");
            ImageView imageView = new ImageView(image);
            root . setCenter ( new Button ( " Commencer "));
            // ajout du c ont eneur comme racine
            imageView.setFitWidth(250);
            imageView.setPreserveRatio(true);
            Label labelAvecImage = new Label ();
            labelAvecImage . setGraphic (imageView);

            root.setTop ( labelAvecImage );

            Scene scene = new Scene ( root, 1000, 800 );
            stage . setTitle ( " Ma fenetre " );
            stage . setScene ( scene );
            stage . show ();
    }

    public static void main(String[] args) {
        launch(args);
    }
}