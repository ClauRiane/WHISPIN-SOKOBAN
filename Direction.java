/**
 * Enum représentant les quatre directions de déplacement dans le jeu 
 * Chaque direction contient son delta en x et y pour faciliter les calculs de position
 * 
 */
public enum Direction {
    
    /** vers le haut (y diminue) */
    HAUT(0, -1, "Haut", "↑"),
    
    /** vers le bas (y augmente) */
    BAS(0, 1, "Bas", "↓"),
    
    /** vers la gauche (x diminue) */
    GAUCHE(-1, 0, "Gauche", "←"),
    
    /** vers la droite (x augmente) */
    DROITE(1, 0, "Droite", "→");
    
    /** Déplacement en x */
    private final int X;
    
    /** Déplacement en y */
    private final int Y;
    
    /** Nom de la direction */
    private final String nom;
    
    /** Symbole de la direction */
    private final String symbole;
    
    /**
     * constructeur de Direction
     * 
     * @param X le déplacement en x
     * @param Y le déplacement en y
     * @param nom le nom de la direction
     * @param symbole le symbole de la direction
     */
    Direction(int X, int Y, String nom, String symbole) {
        this.X = X;
        this.Y = Y;
        this.nom = nom;
        this.symbole = symbole;
    }
    
    /**
     * retourne le déplacement en x pour cette direction
     * 
     * @return  x (-1, 0 ou 1)
     */
    public int getgX() {
        return X;
    }
    
    /**
     * retourne le déplacement en y pour cette direction
     * 
     * @return  y (-1, 0 ou 1)
     */
    public int getgY() {
        return Y;
    }
    
    /**
     * retourne le nom de la direction
     * 
     * @return le nom ("Haut", "Bas", "Gauche", "Droite")
     */
    public String getNom() {
        return nom;
    }
    
    /**
     * retourne le symbole de la direction
     * 
     * @return le symbole (↑, ↓, ←, →)
     */
    public String getSymbole() {
        return symbole;
    }
    
    /**
     * retourne la direction opposée
     * 
     * @return la direction opposée (HAUT↔BAS, GAUCHE↔DROITE)
     */
    public Direction getOpposee() {
        switch (this) {
            case HAUT: return BAS;
            case BAS: return HAUT;
            case GAUCHE: return DROITE;
            case DROITE: return GAUCHE;
            default: throw new IllegalStateException("Direction inconnue");
        }
    }
    
    /**
     * vérifie si cette direction est horizontale
     * 
     * @return true si la direction est GAUCHE ou DROITE, false sinon
     */
    public boolean estHorizontale() {
        return this == GAUCHE || this == DROITE;
    }
    
    /**
     * vérifie si cette direction est verticale
     * 
     * @return true si la direction est HAUT ou BAS, false sinon
     */
    public boolean estVerticale() {
        return this == HAUT || this == BAS;
    }
    
    /**
     * convertit une lettre en direction (pour la persistance/solutions)
     * Format standard Sokoban : u=up, d=down, l=left, r=right
     * 
     * @param lettre la lettre représentant la direction ('u', 'd', 'l', 'r')
     * @return la Direction correspondante
     * @throws IllegalArgumentException si la lettre n'est pas valide
     */
    public static Direction fromLettre(char lettre) {
        switch (Character.toLowerCase(lettre)) {
            case 'u': return HAUT;
            case 'd': return BAS;
            case 'l': return GAUCHE;
            case 'r': return DROITE;
            default: throw new IllegalArgumentException("Lettre de direction invalide : " + lettre);
        }
    }
    
    /**
     * convertit cette direction en lettre (pour la persistance/solutions)
     * format standard Sokoban : u=up, d=down, l=left, r=right
     * 
     * @return la lettre représentant la direction
     */
    public char toLettre() {
        switch (this) {
            case HAUT: return 'u';
            case BAS: return 'd';
            case GAUCHE: return 'l';
            case DROITE: return 'r';
            default: throw new IllegalStateException("Direction inconnue");
        }
    }
    
    /**
     * retourne une représentation textuelle de la direction
     * 
     * @return le symbole et le nom de la direction
     */
    @Override
    public String toString() {
        return symbole + " " + nom;
    }
}