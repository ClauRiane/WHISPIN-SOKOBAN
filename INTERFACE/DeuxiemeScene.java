package INTERFACE;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import MODELE.CaseVide;
import MODELE.Element;
import MODELE.Mur;

public class DeuxiemeScene {

    public static Scene creerScene(Stage stage, Scene scenePrecedente) {
        // Récupérer la carte test
        carte<Element> carte = new Jeu().creerCarteDeTest();

        GridPane grid = new GridPane();
        int tailleCase = 80; // largeur et hauteur d'une case

        for (int i = 0; i < carte.getCarte().size(); i++) {
            for (int j = 0; j < carte.getCarte().get(i).size(); j++) {
                Element e = carte.getCarte().get(i).get(j);
                Rectangle rect = new Rectangle(tailleCase, tailleCase);

                if (e instanceof Mur) {
                    rect.setFill(Color.GRAY);
                } else if (e instanceof CaseVide) {
                    rect.setFill(Color.WHITE);
                } else {
                    rect.setFill(Color.RED);
                }

                rect.setStroke(Color.BLACK);
                grid.add(rect, j, i); // colonne = j, ligne = i
            }
        }

        // Optionnel : revenir à la scène précédente avec clic droit
        grid.setOnMouseClicked(ev -> stage.setScene(scenePrecedente));

        Scene scene = new Scene(grid);
        return scene;
    }
}
