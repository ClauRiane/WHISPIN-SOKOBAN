/**
 * représente un mouvement effectué dans le jeu Sokoban
 * un mouvement peut être un simple déplacement du personnage ou un déplacement
 * qui pousse une boite. Cette classe permet de stocker l'information nécessaire
 * pour annuler le mouvement (Ctrl+Z)
 */
public class Mouvement {

    /** la direction du mouvement */
    private final Direction direction;

    /** la position de départ du personnage (nullable pour les mouvements chargés depuis persistance) */
    private final Position positionDepart;

    /** la position d'arrivée du personnage (nullable pour les mouvements chargés depuis persistance) */
    private final Position positionArrivee;

    /** indique si ce mouvement a poussé une boite */
    private final boolean aPousseeBoite;

    /** position de la boite avant le mouvement (null si pas de poussée ou persistance) */
    private final Position positionBoiteAvant;

    /** position de la boite après le mouvement (null si pas de poussée ou persistance) */
    private final Position positionBoiteApres;

    /** indique si le personnage était sur une cible avant le mouvement */
    private final boolean personnageSurCibleAvant;

    /** indique si la boite était sur une cible avant le mouvement */
    private final boolean boiteSurCibleAvant;

    public Mouvement(Direction direction, Position positionDepart,
                     Position positionArrivee, boolean personnageSurCibleAvant) {
        this(direction, positionDepart, positionArrivee, personnageSurCibleAvant,
             false, null, null, false);
    }

    public Mouvement(Direction direction, Position positionDepart,
                     Position positionArrivee, boolean personnageSurCibleAvant,
                     boolean aPousseeBoite, Position positionBoiteAvant,
                     Position positionBoiteApres, boolean boiteSurCibleAvant) {
        if (direction == null) {
            throw new IllegalArgumentException("La direction ne peut pas etre nulle");
        }
        this.direction = direction;
        this.positionDepart = positionDepart;
        this.positionArrivee = positionArrivee;
        this.personnageSurCibleAvant = personnageSurCibleAvant;
        this.aPousseeBoite = aPousseeBoite;
        this.positionBoiteAvant = positionBoiteAvant;
        this.positionBoiteApres = positionBoiteApres;
        this.boiteSurCibleAvant = boiteSurCibleAvant;
    }

    /**
     * constructeur utilitaire pour les mouvements venant d'un fichier de persistance.
     */
    public Mouvement(Direction direction, boolean aPousseeBoite) {
        this(direction, null, null, false, aPousseeBoite, null, null, false);
    }

    public Direction getDirection() {
        return direction;
    }

    public Position getPositionDepart() {
        return positionDepart;
    }

    public Position getPositionArrivee() {
        return positionArrivee;
    }

    public boolean aPousseeBoite() {
        return aPousseeBoite;
    }

    public Position getPositionBoiteAvant() {
        return positionBoiteAvant;
    }

    public Position getPositionBoiteApres() {
        return positionBoiteApres;
    }

    public boolean personnageEtaitSurCible() {
        return personnageSurCibleAvant;
    }

    public boolean boiteEtaitSurCible() {
        return boiteSurCibleAvant;
    }

    /**
     * format persistance moderne: u/d/l/r, majuscule si poussée de boite.
     */
    public char toLettre() {
        char lettre = direction.toLettre();
        return aPousseeBoite ? Character.toUpperCase(lettre) : lettre;
    }

    /**
     * compatibilité legacy: H/B/G/D.
     */
    public char obtenirCode() {
        switch (direction) {
            case HAUT:
                return 'H';
            case BAS:
                return 'B';
            case GAUCHE:
                return 'G';
            case DROITE:
                return 'D';
            default:
                throw new IllegalStateException("Direction inconnue");
        }
    }

    /**
     * Accepte les formats legacy H/B/G/D et moderne u/d/l/r (+ majuscules pour poussée).
     */
    public static Mouvement depuisCode(char code) {
        boolean poussee = Character.isUpperCase(code) && "UDLR".indexOf(code) >= 0;
        char normalise = Character.toLowerCase(code);
        Direction direction;

        switch (normalise) {
            case 'h':
            case 'u':
                direction = Direction.HAUT;
                break;
            case 'b':
            case 'd':
                direction = Direction.BAS;
                break;
            case 'g':
            case 'l':
                direction = Direction.GAUCHE;
                break;
            case 'r':
                direction = Direction.DROITE;
                break;
            default:
                throw new IllegalArgumentException("Code de mouvement invalide: " + code);
        }

        return new Mouvement(direction, poussee);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Mouvement[").append(direction.getSymbole());
        if (positionDepart != null && positionArrivee != null) {
            sb.append(" ").append(positionDepart).append(" -> ").append(positionArrivee);
        }
        if (aPousseeBoite) {
            sb.append(", pousse boite");
            if (positionBoiteAvant != null && positionBoiteApres != null) {
                sb.append(" ").append(positionBoiteAvant).append(" -> ").append(positionBoiteApres);
            }
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Mouvement)) {
            return false;
        }
        Mouvement autre = (Mouvement) obj;
        return this.direction == autre.direction
            && this.aPousseeBoite == autre.aPousseeBoite
            && (this.positionDepart == null ? autre.positionDepart == null : this.positionDepart.equals(autre.positionDepart))
            && (this.positionArrivee == null ? autre.positionArrivee == null : this.positionArrivee.equals(autre.positionArrivee))
            && (this.positionBoiteAvant == null ? autre.positionBoiteAvant == null : this.positionBoiteAvant.equals(autre.positionBoiteAvant))
            && (this.positionBoiteApres == null ? autre.positionBoiteApres == null : this.positionBoiteApres.equals(autre.positionBoiteApres));
    }

    @Override
    public int hashCode() {
        int result = direction.hashCode();
        result = 31 * result + Boolean.hashCode(aPousseeBoite);
        result = 31 * result + (positionDepart == null ? 0 : positionDepart.hashCode());
        result = 31 * result + (positionArrivee == null ? 0 : positionArrivee.hashCode());
        result = 31 * result + (positionBoiteAvant == null ? 0 : positionBoiteAvant.hashCode());
        result = 31 * result + (positionBoiteApres == null ? 0 : positionBoiteApres.hashCode());
        return result;
    }
}
