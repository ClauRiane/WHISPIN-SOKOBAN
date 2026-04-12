import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public final class GestionEntreeJeu {
    private GestionEntreeJeu() {
    }

    public static void gererTouche(KeyEvent ev, Plateau plateau, ControleurAnimation controleurAnimation, long maintenantNs) {
        boolean changement = false;
        KeyCode touche = ev.getCode();
        Direction directionTentative = null;
        boolean poussaitUneBoite = false;

        if (ev.isControlDown() && touche == KeyCode.Z) {
            changement = plateau.annulerDernierMouvement();
            if (changement) {
                controleurAnimation.notifierAnnulation(plateau.estGagne(), maintenantNs);
            }
        } else {
            switch (touche) {
                case UP:
                case Z:
                case W:
                    directionTentative = Direction.HAUT;
                    poussaitUneBoite = tentativePoussee(plateau, directionTentative);
                    changement = plateau.deplacer(Direction.HAUT);
                    break;
                case DOWN:
                case S:
                    directionTentative = Direction.BAS;
                    poussaitUneBoite = tentativePoussee(plateau, directionTentative);
                    changement = plateau.deplacer(Direction.BAS);
                    break;
                case LEFT:
                case Q:
                case A:
                    directionTentative = Direction.GAUCHE;
                    poussaitUneBoite = tentativePoussee(plateau, directionTentative);
                    changement = plateau.deplacer(Direction.GAUCHE);
                    break;
                case RIGHT:
                case D:
                    directionTentative = Direction.DROITE;
                    poussaitUneBoite = tentativePoussee(plateau, directionTentative);
                    changement = plateau.deplacer(Direction.DROITE);
                    break;
                default:
                    break;
            }
        }

        if (directionTentative != null) {
            if (changement) {
                controleurAnimation.notifierDeplacementReussi(directionTentative, poussaitUneBoite, plateau.estGagne(), maintenantNs);
            } else {
                controleurAnimation.notifierDeplacementBloque(directionTentative, maintenantNs);
            }
        }
    }

    private static boolean tentativePoussee(Plateau plateau, Direction direction) {
        if (!plateau.peutSeDeplacer(direction)) {
            return false;
        }
        Position prochainePos = plateau.getPositionPersonnage().deplacer(direction);
        if (!plateau.estDansLimites(prochainePos)) {
            return false;
        }
        return plateau.getCase(prochainePos).estBoite();
    }
}
