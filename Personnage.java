import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Représente le personnage (joueur) dans le jeu Sokoban
 * Il n'y a qu'un seul personnage par niveau
 * Le personnage peut se déplacer et pousser des boîtes
 * Il peut être sur une cible ou non
 * 
 */
public class Personnage extends Case {
    
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
        super(0, 0); // les coordonnées du personnage sont gérées par la map
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
    public boolean estPersonnageCible() {
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

    @Override
    public Color getCouleurSol() {
        return surCible ? Color.web("#e8ddb7") : Color.web("#e9d8a6");
    }

    public void dessiner(GraphicsContext gc, double x, double y, double taille, ControleurAnimation controleurAnimation, long maintenantNs) {
        double temps = maintenantNs / 1_000_000_000.0;
        double oscillation = Math.sin(temps * 5.5) * taille * 0.01;

        if (controleurAnimation.getEtat() == ControleurAnimation.Etat.MARCHE) {
            double phase = Math.min(controleurAnimation.dureeEcouleeEnSecondes(maintenantNs) / ControleurAnimation.DUREE_MARCHE_SECONDES, 1.0);
            oscillation += Math.sin(Math.PI * phase) * taille * 0.05;
        } else if (controleurAnimation.getEtat() == ControleurAnimation.Etat.POUSSEE) {
            double phase = Math.min(controleurAnimation.dureeEcouleeEnSecondes(maintenantNs) / ControleurAnimation.DUREE_POUSSEE_SECONDES, 1.0);
            oscillation += Math.sin(Math.PI * phase) * taille * 0.03;
        } else if (controleurAnimation.getEtat() == ControleurAnimation.Etat.BLOQUE) {
            double phase = Math.min(controleurAnimation.dureeEcouleeEnSecondes(maintenantNs) / ControleurAnimation.DUREE_BLOCAGE_SECONDES, 1.0);
            oscillation += Math.sin(phase * 22.0) * taille * 0.02;
        } else if (controleurAnimation.getEtat() == ControleurAnimation.Etat.VICTOIRE) {
            oscillation += Math.sin(temps * 11.0) * taille * 0.03;
        }

        String[] sprite = new String[] {
            "..................",
            "........KK........",
            ".......KBBK.......",
            "......KBBBBK......",
            ".....KBBBBBBK.....",
            "....KBBBBBBBBK....",
            "...KBBBBBBBBBBK...",
            "..KBBBBBBBBBBBBK..",
            ".KBBKWWWKKWWWKBBK.",
            "..KBBBBBBBBBBBBK..",
            ".KBBBBBBBBBBBBBBK.",
            ".KBBBBBBBBBBBBBBK.",
            ".KBBBBBBBBBBBBBBK.",
            ".KBBBBBBWKWBBBBBK.",
            ".KBBBBBBWkWBBBBBK.",
            ".KBBBBBBBBBBBBBBK.",
            ".KBBBBBBBBBBBBBBK.",
            "..KKKKKKKKKKKKKK..",
            ".................."
        };

        Color contour = Color.web("#0f0f0f");
        Color brun = surCible ? Color.web("#c7864a") : Color.web("#b26f38");
        Color ombre = surCible ? Color.web("#a86934") : Color.web("#93582b");
        Color blanc = Color.web("#f5f5f5");
        Color reflet = Color.web("#e8b68f");

        int colonnes = sprite[0].length();
        int lignes = sprite.length;
        double pixel = Math.min(taille / colonnes, taille / lignes);
        double largeurSprite = colonnes * pixel;
        double hauteurSprite = lignes * pixel;
        double baseX = x + (taille - largeurSprite) * 0.5;
        double baseY = y + (taille - hauteurSprite) * 0.5 - oscillation;

        for (int row = 0; row < lignes; row++) {
            String ligne = sprite[row];
            for (int col = 0; col < colonnes; col++) {
                char c = ligne.charAt(col);
                if (c == '.') {
                    continue;
                }

                Color couleur;
                switch (c) {
                    case 'K':
                        couleur = contour;
                        break;
                    case 'B':
                        couleur = brun;
                        break;
                    case 'W':
                        couleur = blanc;
                        break;
                    default:
                        couleur = brun;
                        break;
                }

                gc.setFill(couleur);
                gc.fillRect(baseX + col * pixel, baseY + row * pixel, Math.ceil(pixel), Math.ceil(pixel));
            }
        }

        // Reflets et ombres pour se rapprocher de l'image de reference.
        gc.setFill(reflet);
        gc.fillRect(baseX + pixel * 10, baseY + pixel * 3, pixel * 2, pixel * 1);
        gc.fillRect(baseX + pixel * 14, baseY + pixel * 11, pixel * 2, pixel * 1);

        gc.setFill(ombre);
        gc.fillRect(baseX + pixel * 3, baseY + pixel * 12, pixel * 3, pixel * 2);
        gc.fillRect(baseX + pixel * 11, baseY + pixel * 14, pixel * 4, pixel * 1.5);

        if (controleurAnimation.getEtat() == ControleurAnimation.Etat.VICTOIRE) {
            gc.setStroke(Color.web("#f7c548"));
            gc.setLineWidth(Math.max(1.5, taille * 0.03));
            double halo = taille * (0.47 + 0.05 * Math.sin(temps * 8.0));
            double cx = x + taille * 0.5;
            double cy = y + taille * 0.5 - oscillation;
            gc.strokeOval(cx - halo, cy - halo, halo * 2, halo * 2);
        }
    }
}