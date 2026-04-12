import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public final class RenduPlateau {
    private static final Color FOND = Color.web("#ece8dc");
    private static final int MARGE = 20;
    private static final int ESPACE_TEXTE = 40;

    private RenduPlateau() {
    }

    public static void redessiner(
        Canvas canvas,
        double largeur,
        double hauteur,
        Plateau plateau,
        ControleurAnimation controleurAnimation,
        FeuArtifice feuArtifice,
        long maintenantNs
    ) {
        canvas.setWidth(Math.max(largeur, 1));
        canvas.setHeight(Math.max(hauteur, 1));
        dessinerPlateau(canvas.getGraphicsContext2D(), plateau, controleurAnimation, feuArtifice, maintenantNs);
    }

    private static void dessinerPlateau(
        GraphicsContext gc,
        Plateau plateau,
        ControleurAnimation controleurAnimation,
        FeuArtifice feuArtifice,
        long maintenantNs
    ) {
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
            ? "Niveau termine ! Feu d'artifice en cours..."
            : "ZQSD pour bouger, Ctrl+Z pour annuler";
        gc.fillText(message, MARGE, ESPACE_TEXTE - 12);

        for (int i = 0; i < nombreLignes; i++) {
            for (int j = 0; j < nombreColonnes; j++) {
                double x = origineX + j * tailleCase;
                double y = origineY + i * tailleCase;
                Case element = plateau.getGrille().get(i).get(j);

                gc.setFill(element.getCouleurSol());
                gc.fillRect(x, y, tailleCase - 1, tailleCase - 1);
                gc.setStroke(Color.web("#2d241c"));
                gc.strokeRect(x, y, tailleCase - 1, tailleCase - 1);

                if (element instanceof CaseMur mur) {
                    mur.dessiner(gc, x + 1, y + 1, tailleCase - 2);
                    continue;
                }

                CaseVide.getInstance().dessiner(gc, x + 1, y + 1, tailleCase - 2);

                boolean cibleVisible = element instanceof CaseCible
                    || (element instanceof CaseBoite && ((CaseBoite) element).estSurCible())
                    || (element instanceof Personnage && ((Personnage) element).estSurCible());
                if (cibleVisible) {
                    CaseCible.getInstance().dessiner(gc, x + 1, y + 1, tailleCase - 2, maintenantNs, plateau.estGagne());
                }

                if (element instanceof CaseBoite boite) {
                    boite.dessiner(gc, x + 1, y + 1, tailleCase - 2, controleurAnimation, maintenantNs);
                    continue;
                }

                if (element instanceof CaseCible || element instanceof CaseVide) {
                    continue;
                }

                if (element instanceof Personnage personnage) {
                    personnage.dessiner(gc, x + 1, y + 1, tailleCase - 2, controleurAnimation, maintenantNs);
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

        if (plateau.estGagne()) {
            feuArtifice.dessiner(gc, maintenantNs);
        }
    }
}
