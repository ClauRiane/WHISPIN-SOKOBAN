import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public final class RenduPlateau {

    private static final Color FOND_GENERAL  = Color.web("#d0d0d0");
    private static final Color CADRE_BLANC   = Color.web("#ffffff");
    private static final Color ANNEAU_SOMBRE = Color.web("#444444");
    private static final Color CHAMP_JEU     = Color.web("#585858");

    private static final double BORD_CADRE       = 4.0;
    private static final double EPAISSEUR_ANNEAU = 10.0;

    private RenduPlateau() {}

    public static void redessiner(
        Canvas canvas,
        double largeur,
        double hauteur,
        Plateau plateau,
        ControleurAnimation controleurAnimation,
        FeuArtifice feuArtifice,
        Image imageFond,
        long maintenantNs
    ) {
        canvas.setWidth(Math.max(largeur, 1));
        canvas.setHeight(Math.max(hauteur, 1));
        dessiner(canvas.getGraphicsContext2D(), plateau, controleurAnimation, feuArtifice, maintenantNs);
    }

    private static void dessiner(
        GraphicsContext gc,
        Plateau plateau,
        ControleurAnimation controleurAnimation,
        FeuArtifice feuArtifice,
        long maintenantNs
    ) {
        double W = gc.getCanvas().getWidth();
        double H = gc.getCanvas().getHeight();

        gc.setFill(FOND_GENERAL);
        gc.fillRect(0, 0, W, H);

        double marge     = Math.min(W, H) * 0.06;
        double cadreX    = marge;
        double cadreY    = marge;
        double cadreW    = W - marge * 2;
        double cadreH    = H - marge * 2;
        double coinCadre = Math.max(16, Math.min(cadreW, cadreH) * 0.06);

        gc.setFill(Color.web("#000000", 0.18));
        gc.fillRoundRect(cadreX + 4, cadreY + 6, cadreW, cadreH, coinCadre, coinCadre);

        gc.setFill(CADRE_BLANC);
        gc.fillRoundRect(cadreX, cadreY, cadreW, cadreH, coinCadre, coinCadre);

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(BORD_CADRE);
        gc.strokeRoundRect(cadreX + BORD_CADRE / 2, cadreY + BORD_CADRE / 2,
                           cadreW - BORD_CADRE, cadreH - BORD_CADRE, coinCadre, coinCadre);

        double pad        = BORD_CADRE + 8;
        double anneauX    = cadreX + pad;
        double anneauY    = cadreY + pad;
        double anneauW    = cadreW - pad * 2;
        double anneauH    = cadreH - pad * 2;
        double coinAnneau = Math.max(10, coinCadre * 0.7);

        gc.setFill(ANNEAU_SOMBRE);
        gc.fillRoundRect(anneauX, anneauY, anneauW, anneauH, coinAnneau, coinAnneau);

        double champX   = anneauX + EPAISSEUR_ANNEAU;
        double champY   = anneauY + EPAISSEUR_ANNEAU;
        double champW   = anneauW - EPAISSEUR_ANNEAU * 2;
        double champH   = anneauH - EPAISSEUR_ANNEAU * 2;
        double coinChamp = Math.max(6, coinAnneau * 0.5);

        gc.setFill(CHAMP_JEU);
        gc.fillRoundRect(champX, champY, champW, champH, coinChamp, coinChamp);

        int nLignes   = plateau.getGrille().size();
        int nColonnes = plateau.getGrille().isEmpty() ? 1 : plateau.getGrille().get(0).size();
        double tailleCase = Math.min(champW / nColonnes, champH / nLignes);
        double origineX   = champX + (champW - nColonnes * tailleCase) / 2.0;
        double origineY   = champY + (champH - nLignes * tailleCase) / 2.0;

        dessinerGrille(gc, plateau, controleurAnimation, maintenantNs,
                       nLignes, nColonnes, tailleCase, origineX, origineY);

        int surCibles   = plateau.compterBoitesSurCibles();
        int totalCibles = plateau.compterCibles();
        double tailleFont = Math.max(14, Math.min(cadreW, cadreH) * 0.042);
        gc.setFill(Color.web("#333333"));
        gc.setFont(Font.font("SansSerif", FontWeight.BOLD, tailleFont));
        gc.setTextAlign(TextAlignment.CENTER);
        // Positionner le score dans la bande blanche en bas (sous l'anneau)
        double scoreY = cadreY + cadreH - (cadreH - anneauH) / 2.0 + tailleFont * 0.35;
        gc.fillText(surCibles + " / " + totalCibles, cadreX + cadreW / 2.0, scoreY);
        gc.setTextAlign(TextAlignment.LEFT);

        if (plateau.estGagne()) {
            gc.setFill(Color.web("#000000", 0.38));
            gc.fillRoundRect(cadreX, cadreY, cadreW, cadreH * 0.14, coinCadre, coinCadre);
            gc.setFill(Color.web("#f2d974"));
            gc.setFont(Font.font("SansSerif", FontWeight.BOLD, Math.max(14, cadreW * 0.038)));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText("Victoire !", cadreX + cadreW / 2.0, cadreY + cadreH * 0.10);
            gc.setTextAlign(TextAlignment.LEFT);
            feuArtifice.dessiner(gc, maintenantNs);
        }
    }

    private static void dessinerGrille(
        GraphicsContext gc,
        Plateau plateau,
        ControleurAnimation controleurAnimation,
        long maintenantNs,
        int nLignes, int nColonnes,
        double tailleCase,
        double origineX, double origineY
    ) {
        boolean gagne = plateau.estGagne();

        for (int i = 0; i < nLignes; i++) {
            for (int j = 0; j < nColonnes; j++) {
                double x = origineX + j * tailleCase;
                double y = origineY + i * tailleCase;
                Case element = plateau.getGrille().get(i).get(j);

                if (element instanceof CaseVide) {
                    continue;
                }
                if (element instanceof CaseMur) {
                    RenduMur.dessiner(gc, x, y, tailleCase);
                    continue;
                }

                boolean surCible = (element instanceof CaseCible)
                    || (element instanceof CaseBoite && ((CaseBoite) element).estSurCible())
                    || (element instanceof Personnage && ((Personnage) element).estSurCible());

                if (element instanceof CaseCible || surCible) {
                    RenduCible.dessiner(gc, x, y, tailleCase, maintenantNs, gagne);
                }

                if (element instanceof CaseBoite boite) {
                    RenduBoite.dessiner(gc, x, y, tailleCase,
                        boite.estSurCible(), boite.getSurCibleDepuisNs(), controleurAnimation, maintenantNs);
                    continue;
                }

                if (element instanceof Personnage personnage) {
                    RenduPersonnage.dessiner(gc, x, y, tailleCase,
                        personnage.estSurCible(), controleurAnimation, maintenantNs);
                }
            }
        }
    }
}
