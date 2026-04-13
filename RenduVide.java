import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Rendu visuel du sol (case vide).
 */
public final class RenduVide {
    private RenduVide() {
    }

    public static void dessiner(GraphicsContext gc, double x, double y, double taille) {
        gc.setFill(Color.web("#f2dfb4"));
        gc.fillRect(x, y, taille, taille);
        gc.setFill(Color.web("#d6be8b"));
        double pas = Math.max(6, taille * 0.22);
        for (double offsetY = pas * 0.5; offsetY < taille; offsetY += pas) {
            for (double offsetX = pas * 0.5; offsetX < taille; offsetX += pas) {
                gc.fillOval(x + offsetX - 1.5, y + offsetY - 1.5, 3, 3);
            }
        }
    }
}