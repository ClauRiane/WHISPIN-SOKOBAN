import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Rendu visuel de la boîte (fleur).
 * Gère aussi l'aspiration dans la ruche puis l'effet de miel.
 */
public final class RenduBoite {
    private static final double DUREE_ASPIRATION_SECONDES = 0.60;

    private RenduBoite() {
    }

    public static void dessiner(
        GraphicsContext gc,
        double x,
        double y,
        double taille,
        boolean surCible,
        long surCibleDepuisNs,
        ControleurAnimation controleurAnimation,
        long maintenantNs
    ) {
        // Petit rebond pendant la poussée pour renforcer la sensation de mouvement.
        double rebond = 0.0;
        if (controleurAnimation.getEtat() == ControleurAnimation.Etat.POUSSEE) {
            double phase = Math.min(controleurAnimation.dureeEcouleeEnSecondes(maintenantNs) / ControleurAnimation.DUREE_POUSSEE_SECONDES, 1.0);
            rebond = Math.sin(Math.PI * phase) * taille * 0.06;
        }

        double cx = x + taille * 0.5;
        double cy = y + taille * 0.5 - rebond;

        if (surCible) {
            double ecoulee = (maintenantNs - surCibleDepuisNs) / 1_000_000_000.0;
            if (ecoulee < DUREE_ASPIRATION_SECONDES) {
                dessinerAspiration(gc, cx, cy, taille, ecoulee / DUREE_ASPIRATION_SECONDES);
            } else {
                dessinerMielDegoulinant(gc, cx, cy, taille, maintenantNs);
            }
            return;
        }

        dessinerFleur(gc, cx, cy, taille);
    }

    private static void dessinerFleur(GraphicsContext gc, double cx, double cy, double taille) {
        Color petaleCouleur = Color.web("#ff90c8");
        Color petaleContour = Color.web("#d4006f");
        Color coeurCouleur = Color.web("#f5c800");
        Color coeurContour = Color.web("#8b5e00");

        int nbPetales = 6;
        double petaleRayon = taille * 0.22;
        double petaleEpais = taille * 0.13;
        double dist = taille * 0.22;

        gc.setFill(petaleCouleur);
        gc.setStroke(petaleContour);
        gc.setLineWidth(Math.max(1.0, taille * 0.025));

        for (int i = 0; i < nbPetales; i++) {
            double angle = Math.PI * 2.0 * i / nbPetales;
            double pcx = cx + Math.cos(angle) * dist;
            double pcy = cy + Math.sin(angle) * dist;
            gc.save();
            gc.translate(pcx, pcy);
            gc.rotate(Math.toDegrees(angle));
            gc.fillOval(-petaleRayon, -petaleEpais, petaleRayon * 2, petaleEpais * 2);
            gc.strokeOval(-petaleRayon, -petaleEpais, petaleRayon * 2, petaleEpais * 2);
            gc.restore();
        }

        double coeurR = taille * 0.18;
        gc.setFill(coeurCouleur);
        gc.fillOval(cx - coeurR, cy - coeurR, coeurR * 2, coeurR * 2);
        gc.setStroke(coeurContour);
        gc.setLineWidth(Math.max(1.5, taille * 0.035));
        gc.strokeOval(cx - coeurR, cy - coeurR, coeurR * 2, coeurR * 2);

        gc.setFill(coeurContour);
        double dotR = taille * 0.03;
        gc.fillOval(cx - dotR, cy - dotR, dotR * 2, dotR * 2);
        for (int i = 0; i < 5; i++) {
            double a = Math.PI * 2.0 * i / 5;
            double ddx = cx + Math.cos(a) * coeurR * 0.55;
            double ddy = cy + Math.sin(a) * coeurR * 0.55;
            gc.fillOval(ddx - dotR, ddy - dotR, dotR * 2, dotR * 2);
        }
    }

    private static void dessinerAspiration(GraphicsContext gc, double cx, double cy, double taille, double phase) {
        // Courbe d'accélération simple: lent au début, plus rapide à la fin.
        double eased = phase * phase * phase;
        double scale = Math.max(0.0, 1.0 - eased);

        double entreeY = cy + taille * 0.18;
        double currentY = cy + (entreeY - cy) * eased;
        double rotation = eased * 540.0;

        if (phase > 0.05) {
            double alpha = (1.0 - eased) * 0.7;
            gc.setStroke(Color.web("#c07000", alpha));
            gc.setLineWidth(Math.max(1.0, taille * 0.02));
            int nbFilets = 6;
            for (int i = 0; i < nbFilets; i++) {
                double angle = Math.PI * 2.0 * i / nbFilets + eased * Math.PI * 3.0;
                double r = taille * 0.38 * (1.0 - eased * 0.85);
                double sx = cx + Math.cos(angle) * r;
                double sy = currentY + Math.sin(angle) * r * 0.4;
                double ex = cx + Math.cos(angle + 0.9) * r * 0.25;
                double ey = currentY + Math.sin(angle + 0.9) * r * 0.12;
                gc.strokeLine(sx, sy, ex, ey);
            }
        }

        if (scale > 0.01) {
            gc.save();
            gc.translate(cx, currentY);
            gc.rotate(rotation);
            gc.scale(scale, scale);
            dessinerFleur(gc, 0.0, 0.0, taille);
            gc.restore();
        }
    }

    private static void dessinerMielDegoulinant(GraphicsContext gc, double cx, double cy, double taille, long maintenantNs) {
        double temps = maintenantNs / 1_000_000_000.0;
        double domeBasY = cy - taille * 0.26 + taille * 0.60;

        Color mielClair = Color.web("#ffd700");
        Color mielAmbre = Color.web("#e08000");

        double[][] coulures = {
            { -0.22, 0.0, 0.55 },
            { -0.06, 3.5, 0.40 },
            { 0.10, 7.0, 0.62 },
            { 0.24, 2.0, 0.35 },
            { -0.14, 5.2, 0.48 },
        };

        for (double[] c : coulures) {
            double offsetX = c[0] * taille;
            double phase = c[1];
            double longueurMax = c[2] * taille;

            double cycle = (temps * 0.20 + phase) % 5.5;
            double longueur;
            if (cycle < 3.5) {
                longueur = longueurMax * (cycle / 3.5);
            } else {
                longueur = longueurMax * Math.max(0, 1.0 - (cycle - 3.5) / 1.5);
            }

            double bx = cx + offsetX;
            double by = domeBasY;
            double largeurTrait = Math.max(2.0, taille * 0.055);

            gc.setStroke(mielAmbre);
            gc.setLineWidth(largeurTrait);
            gc.strokeLine(bx, by, bx, by + longueur * 0.80);

            gc.setStroke(mielClair);
            gc.setLineWidth(Math.max(1.0, largeurTrait * 0.35));
            gc.strokeLine(bx, by, bx, by + longueur * 0.70);

            if (longueur > taille * 0.06) {
                double gr = Math.min(largeurTrait * 1.1, taille * 0.07);
                double gx = bx;
                double gy = by + longueur;
                gc.setFill(mielAmbre);
                gc.fillOval(gx - gr, gy - gr * 0.6, gr * 2, gr * 2.2);
                gc.setFill(mielClair);
                gc.fillOval(gx - gr * 0.45, gy - gr * 0.45, gr * 0.6, gr * 0.6);
            }
        }

        gc.setFill(Color.web("#e08000", 0.55));
        double flaqueY = domeBasY + taille * 0.22;
        gc.fillOval(cx - taille * 0.28, flaqueY, taille * 0.56, taille * 0.10);
        gc.setFill(Color.web("#ffd700", 0.45));
        gc.fillOval(cx - taille * 0.20, flaqueY + taille * 0.01, taille * 0.40, taille * 0.06);
    }
}