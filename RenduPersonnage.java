import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Rendu visuel du personnage (abeille).
 * Cette classe contient uniquement de la logique d'affichage.
 */
public final class RenduPersonnage {
    private RenduPersonnage() {
    }

    public static void dessiner(
        GraphicsContext gc,
        double x,
        double y,
        double taille,
        boolean surCible,
        ControleurAnimation controleurAnimation,
        long maintenantNs
    ) {
        // Animation verticale légère pour donner une impression de flottement.
        double temps = maintenantNs / 1_000_000_000.0;
        double oscillation = Math.sin(temps * 5.5) * taille * 0.01;

        if (controleurAnimation.getEtat() == ControleurAnimation.Etat.MARCHE) {
            double phase = Math.min(controleurAnimation.dureeEcouleeEnSecondes(maintenantNs) / ControleurAnimation.DUREE_MARCHE_SECONDES, 1.0);
            oscillation += Math.sin(Math.PI * phase) * taille * 0.05;
        } else if (controleurAnimation.getEtat() == ControleurAnimation.Etat.POUSSEE) {
            double phase = Math.min(controleurAnimation.dureeEcouleeEnSecondes(maintenantNs) / ControleurAnimation.DUREE_POUSSEE_SECONDES, 1.0);
            oscillation += Math.sin(Math.PI * phase) * taille * 0.03;
        } else if (controleurAnimation.getEtat() == ControleurAnimation.Etat.BLOQUE) {
            double phase = Math.min(controleurAnimation.dureeEcouleeEnSecondes(maintenantNs) / ControleurAnimation.DUREE_BLOCAGE_SECONDES, 1.0);
            oscillation += Math.sin(phase * 22.0) * taille * 0.02;
        } else if (controleurAnimation.getEtat() == ControleurAnimation.Etat.VICTOIRE) {
            oscillation += Math.sin(temps * 11.0) * taille * 0.03;
        }

        double cx = x + taille * 0.5;
        double cy = y + taille * 0.5 - oscillation;

        boolean ailesOuvertes = Math.sin(temps * 16.0) >= 0.0;
        Direction direction = controleurAnimation.getDirectionRegard();
        String[] sprite = spritePourDirection(direction, ailesOuvertes);
        dessinerSprite(gc, sprite, cx, cy, taille, surCible);
    }

    private static void dessinerSprite(GraphicsContext gc, String[] sprite, double cx, double cy, double taille, boolean surCible) {
        int colonnes = sprite[0].length();
        int lignes = sprite.length;
        double pixel = Math.max(1.0, Math.min(taille / colonnes, taille / lignes));
        double largeurSprite = colonnes * pixel;
        double hauteurSprite = lignes * pixel;

        double baseX = cx - largeurSprite / 2.0;
        double baseY = cy - hauteurSprite / 2.0;

        for (int row = 0; row < lignes; row++) {
            String ligne = sprite[row];
            for (int col = 0; col < colonnes; col++) {
                char c = ligne.charAt(col);
                Color couleur = couleurPourPixel(c, surCible);
                if (couleur == null) {
                    continue;
                }
                gc.setFill(couleur);
                gc.fillRect(baseX + col * pixel, baseY + row * pixel, Math.ceil(pixel), Math.ceil(pixel));
            }
        }
    }

    private static String[] spritePourDirection(Direction direction, boolean ailesOuvertes) {
        if (direction == null) {
            direction = Direction.BAS;
        }
        String[] base = ailesOuvertes ? spriteHautOuvert() : spriteHautFerme();
        return switch (direction) {
            case HAUT -> base;
            case BAS -> miroirVertical(base);
            case DROITE -> rotationHoraire(base);
            case GAUCHE -> rotationAntiHoraire(base);
        };
    }

    private static Color couleurPourPixel(char c, boolean surCible) {
        return switch (c) {
            case 'K' -> Color.web("#1b2026");
            case 'W' -> Color.web("#c9dfdd");
            case 'Y' -> surCible ? Color.web("#ffd765") : Color.web("#f4c244");
            case 'O' -> Color.web("#ec9e42");
            case 'B' -> Color.web("#2a1a00");
            case 'H' -> Color.web("#4a2f14");
            default -> null;
        };
    }

    private static String[] spriteHautOuvert() {
        return new String[] {
            ".......KK.......",
            "......KHHK......",
            ".....KHHHHK.....",
            "..KKKKBBBBKKKK..",
            ".KWWWWYYYYWWWWK.",
            "KWWWWYYYYYYWWWWK",
            "KWWWYYBBBBYYWWWK",
            ".KWWYYYYYYYYWWK.",
            "..KWWYYOOYYWWK..",
            "...KYYYYYYYYK...",
            "...KYYBBBBYYK...",
            "...KYYYYYYYYK...",
            "....KYYYYYYK....",
            ".....KYYYYK.....",
            "......KYYK......",
            ".......KK......."
        };
    }

    private static String[] spriteHautFerme() {
        return new String[] {
            ".......KK.......",
            "......KHHK......",
            ".....KHHHHK.....",
            "...KKBBBBBBKK...",
            "..KWWWYYYYWWWK..",
            ".KWWWYYYYYYWWWK.",
            ".KWWYYBBBBYYWWK.",
            "..KWYYYYYYYYWK..",
            "...KWYYOOYYWK...",
            "...KYYYYYYYYK...",
            "...KYYBBBBYYK...",
            "...KYYYYYYYYK...",
            "....KYYYYYYK....",
            ".....KYYYYK.....",
            "......KYYK......",
            ".......KK......."
        };
    }

    private static String[] miroirVertical(String[] sprite) {
        String[] miroir = new String[sprite.length];
        for (int i = 0; i < sprite.length; i++) {
            miroir[i] = sprite[sprite.length - 1 - i];
        }
        return miroir;
    }

    private static String[] rotationHoraire(String[] sprite) {
        int lignes = sprite.length;
        int colonnes = sprite[0].length();
        String[] tourne = new String[colonnes];
        for (int col = 0; col < colonnes; col++) {
            char[] ligne = new char[lignes];
            for (int row = 0; row < lignes; row++) {
                ligne[row] = sprite[lignes - 1 - row].charAt(col);
            }
            tourne[col] = new String(ligne);
        }
        return tourne;
    }

    private static String[] rotationAntiHoraire(String[] sprite) {
        int lignes = sprite.length;
        int colonnes = sprite[0].length();
        String[] tourne = new String[colonnes];
        for (int col = 0; col < colonnes; col++) {
            char[] ligne = new char[lignes];
            for (int row = 0; row < lignes; row++) {
                ligne[row] = sprite[row].charAt(colonnes - 1 - col);
            }
            tourne[col] = new String(ligne);
        }
        return tourne;
    }
}