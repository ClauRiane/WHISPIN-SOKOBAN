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
        double frequencePulsation = gagne ? 8.0 : 3.0;
        double temps = maintenantNs / 1_000_000_000.0;
        double pulsation = 0.015 * Math.sin(temps * frequencePulsation);
        double cx = x + taille * 0.5;
        double cy = y + taille * 0.5;

        // Base de la ruche (ombre)
        gc.setFill(Color.web("#7a5228", 0.55));
        gc.fillOval(cx - taille * 0.34, cy + taille * 0.24, taille * 0.68, taille * 0.10);

        // Corps de la ruche
        double dw = taille * (0.66 + pulsation);
        double dh = taille * (0.60 + pulsation);
        double dx = cx - dw / 2.0;
        double dy = cy - taille * 0.26;

        gc.setFill(Color.web("#c46e1a"));
        gc.fillOval(dx, dy, dw, dh);

        // Couche dorée par-dessus
        gc.setFill(Color.web("#e89240"));
        gc.fillOval(dx + dw * 0.08, dy + dh * 0.05, dw * 0.84, dh * 0.70);

        // Contour
        gc.setStroke(Color.web("#7a3e10"));
        gc.setLineWidth(Math.max(1.5, taille * 0.04));
        gc.strokeOval(dx, dy, dw, dh);

        // Lignes horizontales (anneaux alvéoles)
        gc.setStroke(Color.web("#7a3e10", 0.65));
        gc.setLineWidth(Math.max(1.0, taille * 0.025));
        double[] frac = { 0.18, 0.36, 0.54, 0.70 };
        for (double fy : frac) {
            double ly = dy + dh * fy;
            double halfDy = ly - (dy + dh / 2.0);
            double chord = (dw / 2.0) * Math.sqrt(Math.max(0, 1.0 - (halfDy / (dh / 2.0)) * (halfDy / (dh / 2.0))));
            gc.strokeLine(cx - chord * 0.82, ly, cx + chord * 0.82, ly);
        }

        // Entrée de la ruche
        gc.setFill(Color.web("#3d1f0a"));
        gc.fillOval(cx - taille * 0.10, cy + taille * 0.18, taille * 0.20, taille * 0.11);

        // Reflet sur le dôme
        gc.setFill(Color.web("#f5c060", 0.45));
        gc.fillOval(dx + dw * 0.22, dy + dh * 0.08, dw * 0.28, dh * 0.20);
    }
}