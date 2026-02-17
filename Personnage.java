package MODELE;
/**
 * Représente le personnage (joueur) dans le jeu Sokoban
 * Il n'y a qu'un seul personnage par niveau
 * Le personnage peut se déplacer et pousser des boîtes
 * Il peut être sur une cible ou non
 * 
 */
public class Personnage extends Element {
    
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
    public boolean estPersonnage() {
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
}