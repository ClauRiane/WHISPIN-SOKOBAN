import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Représente une boîte dans le jeu Sokoban
 * Une boîte peut être poussée par le personnage si la case suivante le permet
 * Une boîte peut être sur une cible ou non
 * 
 */
public class CaseBoite extends Case {
    
    /** indique si la boîte est actuellement sur une cible */
    private boolean surCible;
    
    /**
     * constructeur par défaut
     * crée une boîte qui n'est pas sur une cible
     */
    public CaseBoite() {
        this(false);
    }
    
    /**
     * onstructeur avec état initial.
     * 
     * @param surCible true si la boîte est initialement sur une cible false sinon
     */
    public CaseBoite(boolean surCible) {
        super(0, 0); // les coordonnées de la boîte sont gérées par la map
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
        if (!(obj instanceof CaseBoite)) return false;
        CaseBoite autre = (CaseBoite) obj;
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

    @Override
    public Color getCouleurSol() {
        return surCible ? Color.web("#e8ddb7") : Color.web("#e9d8a6");
    }

    public void dessiner(GraphicsContext gc, double x, double y, double taille, ControleurAnimation controleurAnimation, long maintenantNs) {
        double rebond = 0.0;
        if (controleurAnimation.getEtat() == ControleurAnimation.Etat.POUSSEE) {
            double phase = Math.min(controleurAnimation.dureeEcouleeEnSecondes(maintenantNs) / ControleurAnimation.DUREE_POUSSEE_SECONDES, 1.0);
            rebond = Math.sin(Math.PI * phase) * taille * 0.06;
        }

        double bx = x + taille * 0.12;
        double by = y + taille * 0.12 - rebond;
        double bs = taille * 0.76;

        gc.setFill(surCible ? Color.web("#df9b2d") : Color.web("#a86a2a"));
        gc.fillRoundRect(bx, by, bs, bs, 8, 8);
        gc.setStroke(surCible ? Color.web("#7e5415") : Color.web("#6f4317"));
        gc.setLineWidth(Math.max(1.5, taille * 0.04));
        gc.strokeRoundRect(bx, by, bs, bs, 8, 8);

        gc.strokeLine(bx + bs * 0.5, by + bs * 0.15, bx + bs * 0.5, by + bs * 0.85);
        gc.strokeLine(bx + bs * 0.15, by + bs * 0.5, bx + bs * 0.85, by + bs * 0.5);
    }
}