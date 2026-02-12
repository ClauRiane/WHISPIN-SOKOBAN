/**
 * Représente une case vide dans le jeu Sokoban
 * Une case vide est traversable par le personnage et les boîtes
 * 
 */
public final class CaseVide extends Element {
    
    // instance unique 
    private static final CaseVide INSTANCE = new CaseVide();
    
    /**
     * constructeur privé pour empêcher une instance directe
     * utiliser getInstance() à la place
     */
    private CaseVide() {
        // Les cases vides sont inchangeanbles donc on met rien ici
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
}