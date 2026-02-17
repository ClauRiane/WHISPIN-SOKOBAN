/*classe qui va nous permettre de définir les cases d'une map
chaque type de case est ensuite défini dans des sous classes
chaque case a des coordonnées*/

public class Case{
    private int x;
    private int y;
    
    /**
     * @requires x>=0
     * @requires y>=0
     * 
     */
    public Case(int x, int y){
        if(x<0 || y<0){
            throw new IllegalArgumentException("les coordonnées doivent être positive");
        }
        this.x = x;
        this.y = y;
    }

    /**
     * nous dit si la case est un mur ou non
     */
    public boolean estMur(){}

    /**
     * nous dit si la case est une boite ou non
     */
    public boolean estBoite(){}

    /**
     * nous dit si la case est vide ou non
     */
    public boolean estVide(){}

    /**
     * nous dit si la case est une cible ou non
     */
    public boolean estCible(){}

     /**
     * nous dit si le personnage est sur une case vide ou non
     */
    public boolean estPersonnageCible(){}

     /**
     * nous dit si la boite est sur une cible ou non
     */
    public boolean estBoiteCible(){}

    /**
     * nous dit si le personnage ou une boite peut se deplacer sur la case
     */
    public boolean estTraversable(){}
}