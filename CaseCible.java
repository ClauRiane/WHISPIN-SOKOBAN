/**
 * Représente une cible dans le jeu Sokoban
 * Une cible est traversable et indique où les boîtes doivent être placées
 * Le jeu est gagné quand toutes les cibles ont une boîte dessus
 * 
 */
public final class CaseCible extends Case {
    
    // instance unique partagée 
    private static final CaseCible INSTANCE = new CaseCible();
    
    /**
     * constructeur privé pour empêcher l'instanciation directe
     * utiliser getInstance() à la place
     */
    private CaseCible() {
        super(0, 0);
        // les cibles sont inchangeables
    }
    
    /**
     * retourne l'instance unique de Cible 
     * 
     * @return l'instance unique de Cible
     */
    public static CaseCible getInstance() {
        return INSTANCE;
    }
    
    /**
     * une cible est toujours traversable (le personnage peut marcher dessus)
     * 
     * @return true
     */
    @Override
    public boolean estTraversable() {
        return true;
    }
    
    /**
     * une cible n'est pas poussable
     * 
     * @return false
     */
    @Override
    public boolean estPoussable() {
        return false;
    }
    
    /**
     * une cible est bien une cible mdr
     * 
     * @return true
     */
    @Override
    public boolean estCible() {
        return true;
    }
    
    /**
     * retourne le symbole asci de la cible
     * 
     * @return '.'
     */
    @Override
    public char getSymbole() {
        return '.';
    }
    
    /**
     * retourne une version textuelle de la cible
     * 
     * @return "Cible[.]"
     */
    @Override
    public String toString() {
        return "Cible[.]";
    }
    
    /**
     * deux cibles sont toujours égales (même instance)
     * 
     * @param obj l'objet à comparer
     * @return true si obj est une Cible, false sinon
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof CaseCible;
    }
    
    /**
     * le hashCode est constant pour toutes les cibles
     * 
     * @return un hashcode constant
     */
    @Override
    public int hashCode() {
        return CaseCible.class.hashCode();
    }
}