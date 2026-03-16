import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class DeuxiemeScene {

    public static Scene creerScene(Stage stage, Scene scenePrecedente, Carte<Case> carte) {
        GridPane grid = new GridPane();
        int tailleCase = 80; // largeur et hauteur d'une case

        for (int i = 0; i < carte.getCarte().size(); i++) {
            for (int j = 0; j < carte.getCarte().get(i).size(); j++) {
                Case e = carte.getCarte().get(i).get(j);
                Rectangle rect = new Rectangle(tailleCase, tailleCase);

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
                        rect.setFill(Color.BROWN); // boîte normale
                    }
                } 
                else if (e instanceof Personnage joueur) {
                    if (joueur.estSurCible()) {
                        rect.setFill(Color.LIGHTBLUE); // joueur sur cible
                    } else {
                        rect.setFill(Color.BLUE); // joueur normal
                    }
                } 
                else {
                    rect.setFill(Color.RED); // symbole inconnu
                }

                rect.setStroke(Color.BLACK);
                grid.add(rect, j, i); // colonne = j, ligne = i
            }
        }

        // Clic droit pour revenir à la scène précédente
        grid.setOnMouseClicked(ev -> stage.setScene(scenePrecedente));

        return new Scene(grid);
    }
}
