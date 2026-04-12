import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Représente le personnage (joueur) dans le jeu Sokoban
 * Il n'y a qu'un seul personnage par niveau
 * Le personnage peut se déplacer et pousser des boîtes
 * Il peut être sur une cible ou non
 * 
 */
public class Personnage extends Case {
    
    /** indique si le personnage est actuellement sur une cible */
    private boolean surCible;
    
    /**
     * constructeur
     * crée un personnage qui n'est pas sur une cible
     */
    public Personnage() {
        this(false);
    }
    
    /**
     * constructeur avec état 
     * 
     * @param surCible true si le personnage est initialement sur une cible false sinon
     */
    public Personnage(boolean surCible) {
        super(0, 0); // les coordonnées du personnage sont gérées par la map
        this.surCible = surCible;
    }
    
    /**
     * le personnage n'est pas traversable (on ne peut pas marcher dessus sauf si il est tres tres petit, nn jrigole)
     * 
     * @return false
     */
    @Override
    public boolean estTraversable() {
        return false;
    }
    
    /**
     * le personnage n'est pas poussable (on peut pas le pousser sauf si il est tres tres leger, nn cbn j'arrete)
     * 
     * @return false
     */
    @Override
    public boolean estPoussable() {
        return false;
    }
    
    /**
     * indique si cet élément est le personnage
     * 
     * @return true
     */
    @Override
    public boolean estPersonnageCible() {
        return true;
    }
    
    /**
     * vérifie si le personnage est actuellement sur une cible
     * 
     * @return true si le personnage est sur une cible false sinon
     */
    public boolean estSurCible() {
        return surCible;
    }
    
    /**
     * modifie l'état du personnage (sur cible ou non)
     * 
     * @param surCible true si le personnage doit être sur une cible false sinon
     */
    public void setSurCible(boolean surCible) {
        this.surCible = surCible;
    }
    
    /**
     * Retourne le symbole asci du personnage
     * '@' si le personnage n'est pas sur une cible
     * '+' si le personnage est sur une cible
     * 
     * @return '@' ou '+'
     */
    @Override
    public char getSymbole() {
        return surCible ? '+' : '@';
    }
    
    /**
     * retourne une représentation textuelle du personnage
     * 
     * @return "Personnage[@]" ou "Personnage[+]" selon l'état
     */
    @Override
    public String toString() {
        return surCible ? "Personnage[+]" : "Personnage[@]";
    }
    
    /**
     * compare ce personnage avec un autre objet
     * deux personnages sont égaux si ils ont le même état (surCible)
     * 
     * @param obj l'objet à comparer
     * @return true si obj est un Personnage avec le même état, false sinon
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Personnage)) return false;
        Personnage autre = (Personnage) obj;
        return this.surCible == autre.surCible;
    }
    
    /**
     * calcule le hashCode du personnage
     * 
     * @return le hashcode basé sur l'état surCible
     */
    @Override
    public int hashCode() {
        return Boolean.hashCode(surCible) * 31 + Personnage.class.hashCode();
    }

    @Override
    public Color getCouleurSol() {
        return surCible ? Color.web("#e8ddb7") : Color.web("#e9d8a6");
    }

    public void dessiner(GraphicsContext gc, double x, double y, double taille, ControleurAnimation controleurAnimation, long maintenantNs) {
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
        dessinerSprite(gc, sprite, cx, cy, taille);
    }

    private void dessinerSprite(GraphicsContext gc, String[] sprite, double cx, double cy, double taille) {
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
                Color couleur = couleurPourPixel(c);
                if (couleur == null) {
                    continue;
                }
                gc.setFill(couleur);
                gc.fillRect(baseX + col * pixel, baseY + row * pixel, Math.ceil(pixel), Math.ceil(pixel));
            }
        }
    }

    private String[] spritePourDirection(Direction direction, boolean ailesOuvertes) {
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

    private Color couleurPourPixel(char c) {
        return switch (c) {
            case 'K' -> Color.web("#1b2026"); // contour
            case 'W' -> Color.web("#c9dfdd"); // aile claire
            case 'Y' -> surCible ? Color.web("#ffd765") : Color.web("#f4c244"); // jaune principal
            case 'O' -> Color.web("#ec9e42"); // ombre chaude
            case 'B' -> Color.web("#2a1a00"); // bande noire
            case 'H' -> Color.web("#4a2f14"); // tete/torse brun
            default -> null;
        };
    }

    private String[] spriteHautOuvert() {
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

    private String[] spriteHautFerme() {
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

    private String[] miroirHorizontal(String[] sprite) {
        String[] miroir = new String[sprite.length];
        for (int i = 0; i < sprite.length; i++) {
            String ligne = sprite[i];
            miroir[i] = new StringBuilder(ligne).reverse().toString();
        }
        return miroir;
    }

    private String[] miroirVertical(String[] sprite) {
        String[] miroir = new String[sprite.length];
        for (int i = 0; i < sprite.length; i++) {
            miroir[i] = sprite[sprite.length - 1 - i];
        }
        return miroir;
    }

    private String[] rotationHoraire(String[] sprite) {
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

    private String[] rotationAntiHoraire(String[] sprite) {
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