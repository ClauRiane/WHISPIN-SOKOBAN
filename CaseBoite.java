import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Représente une boîte dans le jeu Sokoban
 * Une boîte peut être poussée par le personnage si la case suivante le permet
 * Une boîte peut être sur une cible ou non
 * 
 */
public class CaseBoite extends Case {

    private static final double DUREE_ASPIRATION_SECONDES = 0.60;

    /** indique si la boîte est actuellement sur une cible */
    private boolean surCible;

    /** horodatage (nanoTime) du moment où la boîte est arrivée sur une cible */
    private long surCibleDepuisNs;
    
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
        this.surCibleDepuisNs = surCible ? System.nanoTime() : 0L;
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

        double cx = x + taille * 0.5;
        double cy = y + taille * 0.5 - rebond;

        if (surCible) {
            double ecoulee = (maintenantNs - surCibleDepuisNs) / 1_000_000_000.0;
            if (ecoulee < DUREE_ASPIRATION_SECONDES) {
                dessinerAspiration(gc, cx, cy, taille, ecoulee / DUREE_ASPIRATION_SECONDES);
            } else {
                dessinerMielDegoulinant(gc, cx, cy, taille, maintenantNs);
            }
            return;
        }

        // --- Fleur normale ---
        dessinerFleur(gc, cx, cy, taille);
    }

    /** Dessine la fleur centrée sur (cx, cy). */
    private void dessinerFleur(GraphicsContext gc, double cx, double cy, double taille) {
        Color petaleCouleur = Color.web("#ff90c8");
        Color petaleContour = Color.web("#d4006f");
        Color coeurCouleur  = Color.web("#f5c800");
        Color coeurContour  = Color.web("#8b5e00");

        int nbPetales = 6;
        double petaleRayon = taille * 0.22;
        double petaleEpais = taille * 0.13;
        double dist        = taille * 0.22;

        gc.setFill(petaleCouleur);
        gc.setStroke(petaleContour);
        gc.setLineWidth(Math.max(1.0, taille * 0.025));

        for (int i = 0; i < nbPetales; i++) {
            double angle = Math.PI * 2.0 * i / nbPetales;
            double pcx = cx + Math.cos(angle) * dist;
            double pcy = cy + Math.sin(angle) * dist;
            gc.save();
            gc.translate(pcx, pcy);
            gc.rotate(Math.toDegrees(angle));
            gc.fillOval(-petaleRayon, -petaleEpais, petaleRayon * 2, petaleEpais * 2);
            gc.strokeOval(-petaleRayon, -petaleEpais, petaleRayon * 2, petaleEpais * 2);
            gc.restore();
        }

        double coeurR = taille * 0.18;
        gc.setFill(coeurCouleur);
        gc.fillOval(cx - coeurR, cy - coeurR, coeurR * 2, coeurR * 2);
        gc.setStroke(coeurContour);
        gc.setLineWidth(Math.max(1.5, taille * 0.035));
        gc.strokeOval(cx - coeurR, cy - coeurR, coeurR * 2, coeurR * 2);

        gc.setFill(coeurContour);
        double dotR = taille * 0.03;
        gc.fillOval(cx - dotR, cy - dotR, dotR * 2, dotR * 2);
        for (int i = 0; i < 5; i++) {
            double a = Math.PI * 2.0 * i / 5;
            double ddx = cx + Math.cos(a) * coeurR * 0.55;
            double ddy = cy + Math.sin(a) * coeurR * 0.55;
            gc.fillOval(ddx - dotR, ddy - dotR, dotR * 2, dotR * 2);
        }
    }

    /**
     * Animation d'aspiration : la fleur tourne sur elle-même en rétrécissant
     * vers l'entrée de la ruche, puis disparaît.
     * phase : 0.0 (début) → 1.0 (absorbée)
     */
    private void dessinerAspiration(GraphicsContext gc, double cx, double cy, double taille, double phase) {
        // ease-in cubique : lent au début puis accélère
        double eased = phase * phase * phase;
        double scale = Math.max(0.0, 1.0 - eased);

        // L'entrée de la ruche est approximativement en cy + taille*0.18
        double entreeY = cy + taille * 0.18;
        double currentY = cy + (entreeY - cy) * eased;
        double rotation = eased * 540.0; // 1.5 tours

        // Filets d'air aspirés convergeant vers l'entrée
        if (phase > 0.05) {
            double alpha = (1.0 - eased) * 0.7;
            gc.setStroke(Color.web("#c07000", alpha));
            gc.setLineWidth(Math.max(1.0, taille * 0.02));
            int nbFilets = 6;
            for (int i = 0; i < nbFilets; i++) {
                double angle = Math.PI * 2.0 * i / nbFilets + eased * Math.PI * 3.0;
                double r = taille * 0.38 * (1.0 - eased * 0.85);
                double sx = cx + Math.cos(angle) * r;
                double sy = currentY + Math.sin(angle) * r * 0.4;
                double ex = cx + Math.cos(angle + 0.9) * r * 0.25;
                double ey = currentY + Math.sin(angle + 0.9) * r * 0.12;
                gc.strokeLine(sx, sy, ex, ey);
            }
        }

        // La fleur elle-même : pivote et rétrécit vers l'entrée
        if (scale > 0.01) {
            gc.save();
            gc.translate(cx, currentY);
            gc.rotate(rotation);
            gc.scale(scale, scale);
            dessinerFleur(gc, 0.0, 0.0, taille);
            gc.restore();
        }
    }

    private void dessinerMielDegoulinant(GraphicsContext gc, double cx, double cy, double taille, long maintenantNs) {
        double temps = maintenantNs / 1_000_000_000.0;

        // Coordonnées du bas du dôme de la ruche (repris de CaseCible)
        double domeBasY = cy - taille * 0.26 + taille * 0.60;  // dy + dh
        double domeW    = taille * 0.66;

        // Filet de miel qui descend le long du dôme
        Color mielClair  = Color.web("#ffd700");
        Color mielAmbre  = Color.web("#e08000");
        Color mielFonce  = Color.web("#c05800");

        // Définition des coulures : (position relative X, phase d'animation, longueur max)
        double[][] coulures = {
            { -0.22, 0.0,  0.55 },
            { -0.06, 3.5,  0.40 },
            {  0.10, 7.0,  0.62 },
            {  0.24, 2.0,  0.35 },
            { -0.14, 5.2,  0.48 },
        };

        for (double[] c : coulures) {
            double offsetX  = c[0] * taille;
            double phase    = c[1];
            double longueurMax = c[2] * taille;

            // Chaque coulure « tombe » puis revient, en boucle décalée
            // vitesse réduite : 0.20 au lieu de 0.60, période 5.5s au lieu de 2.2s
            double cycle = (temps * 0.20 + phase) % 5.5;
            double longueur;
            if (cycle < 3.5) {
                longueur = longueurMax * (cycle / 3.5);
            } else {
                // La goutte se détache et repart de zéro
                longueur = longueurMax * Math.max(0, 1.0 - (cycle - 3.5) / 1.5);
            }

            double bx = cx + offsetX;
            double by = domeBasY;
            double largeurTrait = Math.max(2.0, taille * 0.055);

            // Trait épais ambré (le filet)
            gc.setStroke(mielAmbre);
            gc.setLineWidth(largeurTrait);
            gc.strokeLine(bx, by, bx, by + longueur * 0.80);

            // Reflet clair au centre du filet
            gc.setStroke(mielClair);
            gc.setLineWidth(Math.max(1.0, largeurTrait * 0.35));
            gc.strokeLine(bx, by, bx, by + longueur * 0.70);

            // Goutte en bas du filet
            if (longueur > taille * 0.06) {
                double gr = Math.min(largeurTrait * 1.1, taille * 0.07);
                double gx = bx;
                double gy = by + longueur;
                // Corps ovale de la goutte
                gc.setFill(mielAmbre);
                gc.fillOval(gx - gr, gy - gr * 0.6, gr * 2, gr * 2.2);
                gc.setFill(mielClair);
                gc.fillOval(gx - gr * 0.45, gy - gr * 0.45, gr * 0.6, gr * 0.6);
            }
        }

        // Flaques de miel accumulées sous la ruche
        gc.setFill(Color.web("#e08000", 0.55));
        double flaqueY = domeBasY + taille * 0.22;
        gc.fillOval(cx - taille * 0.28, flaqueY, taille * 0.56, taille * 0.10);
        gc.setFill(Color.web("#ffd700", 0.45));
        gc.fillOval(cx - taille * 0.20, flaqueY + taille * 0.01, taille * 0.40, taille * 0.06);
    }
}