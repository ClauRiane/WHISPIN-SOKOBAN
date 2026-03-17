import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.ArrayList;

public class DeuxiemeScene {

    public static Scene creerScene(Stage stage, Scene scenePrecedente, Carte<Case> carte) {
        GridPane grid = new GridPane();
        int tailleCase = 80; // largeur et hauteur d'une case
        Image boiteImage = new Image(DeuxiemeScene.class.getResource("/INTERFACE/boite.jpg").toExternalForm());

        for (int i = 0; i < carte.getCarte().size(); i++) {
            for (int j = 0; j < carte.getCarte().get(i).size(); j++) {
                Case e = carte.getCarte().get(i).get(j);
                Rectangle rect = new Rectangle(tailleCase, tailleCase);
                rect.setStroke(Color.BLACK);

                StackPane cellule = new StackPane();
                cellule.getChildren().add(rect);
                // Couleurs en fonction du type de case
                if (e instanceof CaseMur) {
                    rect.setFill(Color.GRAY);
                } 
                else if (e instanceof CaseVide) {
                    rect.setFill(Color.BEIGE);
                } 
                else if (e instanceof CaseCible) {
                    rect.setFill(Color.LIGHTGREEN);
                } 
                else if (e instanceof CaseBoite boite) {
                    if (boite.estSurCible()) {
                        rect.setFill(Color.GOLD); // boîte sur cible
                    } else {
                        //rect.setFill(Color.BROWN); // boîte normale
                        ImageView imageView = new ImageView(boiteImage);
                        imageView.setFitWidth(tailleCase);
                        imageView.setFitHeight(tailleCase);
                        cellule.getChildren().add(imageView);
                    }
                } 
                else if (e instanceof Personnage joueur) {
                    if (joueur.estSurCible()) {
                        rect.setFill(Color.LIGHTBLUE); // joueur sur cible
                    } else {
                        //rect.setFill(Color.BLUE); // joueur normal
                        Image image = new Image("https://www.meme-arsenal.com/memes/7de1cccd58be2b01261e613c08663e9a.jpg");
                        ImageView imageView = new ImageView(image);
                        imageView.setFitWidth(tailleCase);
                        imageView.setFitHeight(tailleCase);

                        cellule.getChildren().add(imageView);
                    }
                } 
                else {
                    rect.setFill(Color.RED); // symbole inconnu
                }

                grid.add(cellule, j, i); // colonne = j, ligne = i
            }
        }

        // GridPane prend juste la taille de ses enfants
        grid.setMaxSize(GridPane.USE_PREF_SIZE, GridPane.USE_PREF_SIZE);

        // StackPane centre automatiquement ses enfants
        StackPane root = new StackPane();
        root.getChildren().add(grid);

        // Clic droit sur la scène pour revenir
        root.setOnMouseClicked(ev -> stage.setScene(scenePrecedente));

        // Scene de taille fixe (ou adapte à ton écran)
        return new Scene(root, 1000, 800);
    }
}
