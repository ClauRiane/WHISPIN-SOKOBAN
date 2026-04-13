import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Rendu visuel des murs en motif briques.
 */
public final class RenduMur {
    private RenduMur() {
    }

    public static void dessiner(GraphicsContext gc, double x, double y, double taille) {
        gc.setFill(Color.web("#5c6770"));
        gc.fillRoundRect(x, y, taille, taille, 8, 8);

        double hauteurBrique = Math.max(4, taille * 0.24);
        gc.setStroke(Color.web("#3d4650"));
        gc.setLineWidth(Math.max(1.0, taille * 0.02));
        for (double offsetY = hauteurBrique; offsetY < taille; offsetY += hauteurBrique) {
            gc.strokeLine(x, y + offsetY, x + taille, y + offsetY);
        }

        double largeurBrique = Math.max(8, taille * 0.35);
        boolean decale = false;
        for (double offsetY = 0; offsetY < taille; offsetY += hauteurBrique) {
            double depart = decale ? largeurBrique * 0.5 : 0;
            for (double offsetX = depart; offsetX < taille; offsetX += largeurBrique) {
                gc.strokeLine(x + offsetX, y + offsetY, x + offsetX, y + Math.min(offsetY + hauteurBrique, taille));
            }
            decale = !decale;
        }
    }
}