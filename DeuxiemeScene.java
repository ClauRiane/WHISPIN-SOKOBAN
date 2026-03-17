import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.nio.file.Path;

public class DeuxiemeScene {
    private static final Color FOND = Color.web("#ece8dc");
    private static final int MARGE = 20;
    private static final int ESPACE_TEXTE = 40;
    private static final Image IMAGE_MUR = new Image(Path.of("mur.jpg").toUri().toString());
    private static final Image IMAGE_BLOCK = new Image(Path.of("block.jpg").toUri().toString());
    private static final Image IMAGE_BUT = new Image(Path.of("but.jpg").toUri().toString());
    private static final Image IMAGE_SOL = new Image(Path.of("sol.jpg").toUri().toString());
    private static final Image IMAGE_PERSONNAGE = new Image(Path.of("gojo.jpg").toUri().toString());

    public static Scene creerScene(Stage stage, Scene scenePrecedente, Carte<Case> carte) {
        Canvas canvas = new Canvas(900, 700);
        StackPane racine = new StackPane(canvas);
        racine.setStyle("-fx-background-color: #ece8dc;");
        Scene scene = new Scene(racine, 900, 700);
        scene.setFill(FOND);

        scene.widthProperty().addListener((obs, oldVal, newVal) -> redessiner(canvas, scene.getWidth(), scene.getHeight(), carte));
        scene.heightProperty().addListener((obs, oldVal, newVal) -> redessiner(canvas, scene.getWidth(), scene.getHeight(), carte));
        redessiner(canvas, scene.getWidth(), scene.getHeight(), carte);

        canvas.setOnMouseClicked(ev -> stage.setScene(scenePrecedente));
        return scene;
    }

    private static void redessiner(Canvas canvas, double largeur, double hauteur, Carte<Case> carte) {
        canvas.setWidth(Math.max(largeur, 1));
        canvas.setHeight(Math.max(hauteur, 1));
        dessinerPlateau(canvas.getGraphicsContext2D(), carte);
    }

    private static void dessinerPlateau(GraphicsContext gc, Carte<Case> carte) {
        double largeur = gc.getCanvas().getWidth();
        double hauteur = gc.getCanvas().getHeight();

        int nombreLignes = carte.getCarte().size();
        int nombreColonnes = carte.getCarte().isEmpty() ? 0 : carte.getCarte().get(0).size();

        double largeurDisponible = Math.max(largeur - 2 * MARGE, 1);
        double hauteurDisponible = Math.max(hauteur - 2 * MARGE - ESPACE_TEXTE, 1);
        double tailleCase = Math.min(
            largeurDisponible / Math.max(nombreColonnes, 1),
            hauteurDisponible / Math.max(nombreLignes, 1)
        );

        double largeurPlateau = nombreColonnes * tailleCase;
        double hauteurPlateau = nombreLignes * tailleCase;
        double origineX = (largeur - largeurPlateau) / 2.0;
        double origineY = ESPACE_TEXTE + MARGE + (hauteurDisponible - hauteurPlateau) / 2.0;

        gc.setFill(FOND);
        gc.fillRect(0, 0, largeur, hauteur);

        gc.setFill(Color.web("#473728"));
        gc.setFont(Font.font("SansSerif", FontWeight.BOLD, Math.max(16, largeur * 0.02)));
        gc.fillText("Plateau Sokoban - cliquez pour revenir", MARGE, ESPACE_TEXTE - 12);

        for (int i = 0; i < nombreLignes; i++) {
            for (int j = 0; j < nombreColonnes; j++) {
                double x = origineX + j * tailleCase;
                double y = origineY + i * tailleCase;
                Case element = carte.getCarte().get(i).get(j);

                gc.setFill(couleurFond(element));
                gc.fillRect(x, y, tailleCase - 1, tailleCase - 1);
                gc.setStroke(Color.web("#2d241c"));
                gc.strokeRect(x, y, tailleCase - 1, tailleCase - 1);

                if (element instanceof CaseMur) {
                    gc.drawImage(IMAGE_MUR, x + 1, y + 1, tailleCase - 2, tailleCase - 2);
                    continue;
                }

                if (element instanceof CaseBoite) {
                    gc.drawImage(IMAGE_BLOCK, x + 1, y + 1, tailleCase - 2, tailleCase - 2);
                    continue;
                }

                if (element instanceof CaseCible) {
                    gc.drawImage(IMAGE_BUT, x + 1, y + 1, tailleCase - 2, tailleCase - 2);
                    continue;
                }

                if (element instanceof CaseVide) {
                    gc.drawImage(IMAGE_SOL, x + 1, y + 1, tailleCase - 2, tailleCase - 2);
                    continue;
                }

                if (element instanceof Personnage) {
                    gc.drawImage(IMAGE_PERSONNAGE, x + 2, y + 2, tailleCase - 4, tailleCase - 4);
                    continue;
                }

                char symbole = element.getSymbole();
                if (symbole != ' ') {
                    gc.setFill(Color.web("#1f1f1f"));
                    gc.setFont(Font.font("Monospaced", FontWeight.BOLD, Math.max(12, tailleCase * 0.45)));
                    gc.fillText(String.valueOf(symbole), x + tailleCase * 0.32, y + tailleCase * 0.68);
                }
            }
        }
    }

    private static Color couleurFond(Case element) {
        if (element instanceof CaseMur) {
            return Color.web("#6c757d");
        }
        if (element instanceof CaseVide) {
            return Color.web("#e9d8a6");
        }
        if (element instanceof CaseCible) {
            return Color.web("#95d5b2");
        }
        if (element instanceof CaseBoite boite) {
            return boite.estSurCible() ? Color.web("#ffb703") : Color.web("#b5651d");
        }
        if (element instanceof Personnage joueur) {
            return joueur.estSurCible() ? Color.web("#8ecae6") : Color.web("#219ebc");
        }
        return Color.web("#d62828");
    }
}
