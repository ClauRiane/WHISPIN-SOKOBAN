import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Représente une case vide dans le jeu Sokoban
 * Une case vide est traversable par le personnage et les boîtes
 * 
 */
public final class CaseVide extends Case {
    
    // instance unique 
    private static final CaseVide INSTANCE = new CaseVide();
    
    /**
     * constructeur privé pour empêcher une instance directe
     * utiliser getInstance() à la place
     */
    private CaseVide() {
        super(0, 0);
        // les cases vides sont inchangeables
        
    }
    
    /**
     * retourne l'instance unique de CaseVide
     * 
     */
    public static CaseVide getInstance() {
        return INSTANCE;
    }
    
    /**
     * Une case vide est toujours traversable.
     * 
     * @return true
     */
    @Override
    public boolean estTraversable() {
        return true;
    }
    
    /**
     * une case vide n'est pas poussable
     * 
     * @return false
     */
    @Override
    public boolean estPoussable() {
        return false;
    }
    
    /**
     * retourne le symbole asci de la case vide
     * 
     * @return ' ' (espace)
     */
    @Override
    public char getSymbole() {
        return ' ';
    }
    
    /**
     * retourne une version textuelle de la case vide
     * 
     * @return "CaseVide[ ]"
     */
    @Override
    public String toString() {
        return "CaseVide[ ]";
    }
    
    /**
     * deux cases vides sont toujours égales (même instance)
     * 
     * @param obj l'objet à comparer
     * @return true si obj est une CaseVide false sinon
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof CaseVide;
    }
    
    /**
     * le hashCode est constant pour toutes les cases vides.
     * 
     * @return un hashcode constant
     */
    @Override
    public int hashCode() {
        return CaseVide.class.hashCode();
    }

    @Override
    public Color getCouleurSol() {
        return Color.web("#e9d8a6");
    }

    public void dessiner(GraphicsContext gc, double x, double y, double taille) {
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