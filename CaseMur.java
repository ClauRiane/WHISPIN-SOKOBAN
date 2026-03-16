/**
 * Représente un mur dans le jeu Sokoban.
 * Un mur est un élément statique et immutable qui bloque tous les déplacements.
 * Il ne peut être ni traversé ni poussé.
 * 
 */
public final class CaseMur extends Case {
    
    // comme tous les murs sont identiques on peut utiliser une seule instance
    private static final CaseMur INSTANCE = new CaseMur();
    
    /**
     * constructeur privé pour empêcher une instance directe
     * utiliser getInstance() à la place
     */
    private CaseMur() {
        super(0, 0);
        // Les murs sont inchamgeables donc on met rien ici
    }
    
    /**
     * Retourne l'instance unique de Mur 
     * 
     * @return l'instance unique de Mur
     */
    public static CaseMur getInstance() {
        return INSTANCE;
    }
    
    /**
     * un mur n'est jamais traversable
     * 
     * @return false
     */
    @Override
    public boolean estTraversable() {
        return false;
    }
    
    /**
     * un mur n'est jamais poussable
     * 
     * @return false
     */
    @Override
    public boolean estPoussable() {
        return false;
    }
    
    /**
     * retourne le symbole asci du mur
     * 
     * @return '#'
     */
    @Override
    public char getSymbole() {
        return '#';
    }
    
    /**
     * retourne une représentation textuelle du mur
     * 
     * @return "Mur[#]"
     */
    @Override
    public String toString() {
        return "Mur[#]";
    }
    
    /**
     * deux murs sont toujours égaux (même instance)
     * 
     * @param obj l'objet à comparer
     * @return true si obj est un Mur false sinon
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof CaseMur;
    }
    
    /**
     * le hashCode est constant pour tous les murs
     * 
     * @return un hashcode 
     */
    @Override
    public int hashCode() {
        return CaseMur.class.hashCode();
    }
}