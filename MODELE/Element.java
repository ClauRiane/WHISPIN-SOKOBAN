/**
 * Classe abstraite représentant un élément du jeu Sokoban.
 * Tous les éléments du plateau (Mur, Boite, Personnage, Cible, CaseVide) 
 * héritent de cette classe.
 */
 
public abstract class Element {
    
    /**
     * Indique si cet élément peut être traversé par le personnage
     * 
     * @return true si le personnage peut se déplacer sur cet élément, false sinon
     */
    public abstract boolean estTraversable();
    
    /**
     * indique si cet élément peut être poussé par le personnage
     * 
     * @return true si l'élément peut être poussé, false sinon
     */
    public abstract boolean estPoussable();
    
    /**
     * retourne le symbole asci représentant l'élément
     * 
     * ' '  case vide
     * '#'  mur
     * '$'  boite
     * '@'  personnage
     * '.'  cible
     * '*'  boite sur cible
     * '+'  personnage sur cible
     * 
     * @return le caractère représentant cet élément
     */
    public abstract char getSymbole();
    
    /**
     * indique l'élément est une cible 
     * 
     * @return true si c'est une cible false sinon
     */
    public boolean estCible() {
        return false;
    }
    
    /**
     * indique si cet élément est une boite
     * 
     * @return true si c'est une boite false sinon
     */
    public boolean estBoite() {
        return false;
    }
    
    /**
     * indique si l'élément est le personnage
     * 
     * @return true si c'est le personnage false sinon
     */
    public boolean estPersonnage() {
        return false;
    }
    
    /**
     * retourne l'element en versiontextuelle 
     * 
     * @return le symbole de l'élément sous forme de String
     */
    @Override
    public String toString() {
        return String.valueOf(getSymbole());
    }
}