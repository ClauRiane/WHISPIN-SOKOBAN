import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

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

    @Override
    public Color getCouleurSol() {
        return Color.web("#e8ddb7");
    }

    public void dessiner(GraphicsContext gc, double x, double y, double taille, long maintenantNs, boolean gagne) {
        double frequencePulsation = gagne ? 8.0 : 4.5;
        double temps = maintenantNs / 1_000_000_000.0;
        double pulsation = 0.12 * (Math.sin(temps * frequencePulsation) + 1.0);
        double rayonExterieur = taille * (0.42 + pulsation * 0.06);
        double cx = x + taille / 2.0;
        double cy = y + taille / 2.0;

        gc.setFill(Color.web("#fff1c2"));
        gc.fillOval(cx - rayonExterieur, cy - rayonExterieur, rayonExterieur * 2, rayonExterieur * 2);
        gc.setStroke(Color.web("#b08b2d"));
        gc.setLineWidth(Math.max(1.5, taille * 0.04));
        gc.strokeOval(cx - rayonExterieur, cy - rayonExterieur, rayonExterieur * 2, rayonExterieur * 2);

        double rayonInterieur = rayonExterieur * 0.55;
        gc.setStroke(Color.web("#7f6a28"));
        gc.strokeOval(cx - rayonInterieur, cy - rayonInterieur, rayonInterieur * 2, rayonInterieur * 2);
    }
}