import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
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

    public static Scene creerScene(Stage stage, Scene scenePrecedente, Plateau plateau) {
        Canvas canvas = new Canvas(900, 700);
        StackPane racine = new StackPane(canvas);
        racine.setStyle("-fx-background-color: #ece8dc;");
        Scene scene = new Scene(racine, 900, 700);
        scene.setFill(FOND);

        scene.widthProperty().addListener((obs, oldVal, newVal) -> redessiner(canvas, scene.getWidth(), scene.getHeight(), plateau));
        scene.heightProperty().addListener((obs, oldVal, newVal) -> redessiner(canvas, scene.getWidth(), scene.getHeight(), plateau));
        redessiner(canvas, scene.getWidth(), scene.getHeight(), plateau);

        scene.setOnKeyPressed(ev -> {
            boolean changement = false;
            KeyCode code = ev.getCode();

            if (ev.isControlDown() && code == KeyCode.Z) {
                changement = plateau.annulerDernierMouvement();
            } else {
                switch (code) {
                    case UP:
                    case Z:
                    case W:
                        changement = plateau.deplacer(Direction.HAUT);
                        break;
                    case DOWN:
                    case S:
                        changement = plateau.deplacer(Direction.BAS);
                        break;
                    case LEFT:
                    case Q:
                    case A:
                        changement = plateau.deplacer(Direction.GAUCHE);
                        break;
                    case RIGHT:
                    case D:
                        changement = plateau.deplacer(Direction.DROITE);
                        break;
                    default:
                        break;
                }
            }

            if (changement) {
                redessiner(canvas, scene.getWidth(), scene.getHeight(), plateau);
            }
        });

        canvas.setOnMouseClicked(ev -> stage.setScene(scenePrecedente));
        canvas.setFocusTraversable(true);
        canvas.requestFocus();
        return scene;
    }

    private static void redessiner(Canvas canvas, double largeur, double hauteur, Plateau plateau) {
        canvas.setWidth(Math.max(largeur, 1));
        canvas.setHeight(Math.max(hauteur, 1));
        dessinerPlateau(canvas.getGraphicsContext2D(), plateau);
    }

    private static void dessinerPlateau(GraphicsContext gc, Plateau plateau) {
        double largeur = gc.getCanvas().getWidth();
        double hauteur = gc.getCanvas().getHeight();

        int nombreLignes = plateau.getGrille().size();
        int nombreColonnes = plateau.getGrille().isEmpty() ? 0 : plateau.getGrille().get(0).size();

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
        String message = plateau.estGagne()
            ? "Niveau termine ! Cliquez pour revenir"
            : "Fleches/ZQSD pour bouger, Ctrl+Z pour annuler - cliquez pour revenir";
        gc.fillText(message, MARGE, ESPACE_TEXTE - 12);

        for (int i = 0; i < nombreLignes; i++) {
            for (int j = 0; j < nombreColonnes; j++) {
                double x = origineX + j * tailleCase;
                double y = origineY + i * tailleCase;
                Case element = plateau.getGrille().get(i).get(j);

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
