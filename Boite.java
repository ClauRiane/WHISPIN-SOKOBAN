package MODELE;
/**
 * Représente une boîte dans le jeu Sokoban
 * Une boîte peut être poussée par le personnage si la case suivante le permet
 * Une boîte peut être sur une cible ou non
 * 
 */

public class Boite extends Element {
    
    /** indique si la boîte est actuellement sur une cible */
    private boolean surCible;
    
    /**
     * constructeur par défaut
     * crée une boîte qui n'est pas sur une cible
     */
    public Boite() {
        this(false);
    }
    
    /**
     * Constructeur avec état initial.
     * 
     * @param surCible true si la boîte est initialement sur une cible false sinon
     */
    public Boite(boolean surCible) {
        this.surCible = surCible;
    }
    
    /**
     * une boîte n'est pas traversable (elle bloque le passage lool)
     * 
     * @return false
     */
    @Override
    public boolean estTraversable() {
        return false;
    }
    
    /**
     * une boîte est poussable (si la case suivante le permet) par ex si la case suivante est un mur X
     * 
     * @return true
     */
    @Override
    public boolean estPoussable() {
        return true;
    }
    
    /**
     * indique si cette boîte est une boîte
     * 
     * @return true
     */
    @Override
    public boolean estBoite() {
        return true;
    }
    
    /**
     * vérifie si la boîte est actuellement sur une cible
     * 
     * @return true si la boîte est sur une cible, false sinon
     */
    public boolean estSurCible() {
        return surCible;
    }
    
    /**
     * modifie l'état de la boîte (sur cible ou non)
     * 
     * @param surCible true si la boîte doit être sur une cible false sinon
     */
    public void setSurCible(boolean surCible) {
        this.surCible = surCible;
    }
    
    /**
     * retourne le symbole asci de la boîte
     * '$' si la boîte n'est pas sur une cible
     * '*' si la boîte est sur une cible
     * 
     * @return '$' ou '*'
     */
    @Override
    public char getSymbole() {
        return surCible ? '*' : '$';
    }
    
    /**
     * retourne une représentation textuelle de la boîte
     * 
     * @return "Boite[$]" ou "Boite[*]" selon l'état
     */
    @Override
    public String toString() {
        return surCible ? "Boite[*]" : "Boite[$]";
    }
    
    /**
     * compare cette boîte avec un autre objet
     * deux boîtes sont égales si elles ont le même état (surCible)
     * 
     * @param obj l'objet à comparer
     * @return true si obj est une Boite avec le même état false sinon
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Boite)) return false;
        Boite autre = (Boite) obj;
        return this.surCible == autre.surCible;
    }
    
    /**
     * retourne le hashCode de la boîte
     * 
     * @return le hashcode de l'état surCible
     */
    @Override
    public int hashCode() {
        return Boolean.hashCode(surCible);
    }
}
