import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Rendu visuel de la cible sous forme de ruche.
 */
public final class RenduCible {
    private RenduCible() {
    }

    public static void dessiner(GraphicsContext gc, double x, double y, double taille, long maintenantNs, boolean gagne) {
        double frequencePulsation = gagne ? 8.0 : 3.0;
        double temps = maintenantNs / 1_000_000_000.0;
        double pulsation = 0.015 * Math.sin(temps * frequencePulsation);
        double cx = x + taille * 0.5;
        double cy = y + taille * 0.5;

        gc.setFill(Color.web("#7a5228", 0.55));
        gc.fillOval(cx - taille * 0.34, cy + taille * 0.24, taille * 0.68, taille * 0.10);

        double dw = taille * (0.66 + pulsation);
        double dh = taille * (0.60 + pulsation);
        double dx = cx - dw / 2.0;
        double dy = cy - taille * 0.26;

        gc.setFill(Color.web("#c46e1a"));
        gc.fillOval(dx, dy, dw, dh);

        gc.setFill(Color.web("#e89240"));
        gc.fillOval(dx + dw * 0.08, dy + dh * 0.05, dw * 0.84, dh * 0.70);

        gc.setStroke(Color.web("#7a3e10"));
        gc.setLineWidth(Math.max(1.5, taille * 0.04));
        gc.strokeOval(dx, dy, dw, dh);

        gc.setStroke(Color.web("#7a3e10", 0.65));
        gc.setLineWidth(Math.max(1.0, taille * 0.025));
        double[] frac = { 0.18, 0.36, 0.54, 0.70 };
        for (double fy : frac) {
            double ly = dy + dh * fy;
            double halfDy = ly - (dy + dh / 2.0);
            double chord = (dw / 2.0) * Math.sqrt(Math.max(0, 1.0 - (halfDy / (dh / 2.0)) * (halfDy / (dh / 2.0))));
            gc.strokeLine(cx - chord * 0.82, ly, cx + chord * 0.82, ly);
        }

        gc.setFill(Color.web("#3d1f0a"));
        gc.fillOval(cx - taille * 0.10, cy + taille * 0.18, taille * 0.20, taille * 0.11);

        gc.setFill(Color.web("#f5c060", 0.45));
        gc.fillOval(dx + dw * 0.22, dy + dh * 0.08, dw * 0.28, dh * 0.20);
    }
}