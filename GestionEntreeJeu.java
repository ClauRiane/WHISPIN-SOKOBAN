import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public final class GestionEntreeJeu {
    private GestionEntreeJeu() {
    }

    public static void gererTouche(KeyEvent evenementTouche, Plateau plateau, ControleurAnimation controleurAnimation, long maintenantNs) {
        boolean actionEffectuee = false;
        KeyCode touche = evenementTouche.getCode();
        Direction directionTentative = null;
        boolean vaPousserUneBoite = false;

        if (evenementTouche.isControlDown() && touche == KeyCode.Z) {
            actionEffectuee = plateau.annulerDernierMouvement();
            if (actionEffectuee) {
                controleurAnimation.notifierAnnulation(plateau.estGagne(), maintenantNs);
            }
        } else {
            switch (touche) {
                case UP:
                case Z:
                case W:
                    directionTentative = Direction.HAUT;
                    vaPousserUneBoite = plateau.vaPousserBoite(directionTentative);
                    actionEffectuee = plateau.deplacer(Direction.HAUT);
                    break;
                case DOWN:
                case S:
                    directionTentative = Direction.BAS;
                    vaPousserUneBoite = plateau.vaPousserBoite(directionTentative);
                    actionEffectuee = plateau.deplacer(Direction.BAS);
                    break;
                case LEFT:
                case Q:
                case A:
                    directionTentative = Direction.GAUCHE;
                    vaPousserUneBoite = plateau.vaPousserBoite(directionTentative);
                    actionEffectuee = plateau.deplacer(Direction.GAUCHE);
                    break;
                case RIGHT:
                case D:
                    directionTentative = Direction.DROITE;
                    vaPousserUneBoite = plateau.vaPousserBoite(directionTentative);
                    actionEffectuee = plateau.deplacer(Direction.DROITE);
                    break;
                default:
                    break;
            }
        }

        if (directionTentative != null) {
            if (actionEffectuee) {
                controleurAnimation.notifierDeplacementReussi(directionTentative, vaPousserUneBoite, plateau.estGagne(), maintenantNs);
            } else {
                controleurAnimation.notifierDeplacementBloque(directionTentative, maintenantNs);
            }
        }
    }

    /**
     * Variante multi-monde : utilise {@link Multivers#deplacer(Direction)} qui gère
     * l'entrée et la sortie des boîtes-mondes.
     * Le Ctrl+Z annule sur le plateau courant (monde actif).
     */
    public static void gererTouche(KeyEvent evenementTouche, Multivers multivers, ControleurAnimation controleurAnimation, long maintenantNs) {
        KeyCode touche = evenementTouche.getCode();
        Direction directionTentative = null;

        if (evenementTouche.isControlDown() && touche == KeyCode.Z) {
            Plateau courant = multivers.getPlateauCourant();
            boolean annule = courant.annulerDernierMouvement();
            if (annule) {
                controleurAnimation.notifierAnnulation(multivers.estGagne(), maintenantNs);
            }
            return;
        }

        switch (touche) {
            case UP: case Z: case W: directionTentative = Direction.HAUT;   break;
            case DOWN: case S:       directionTentative = Direction.BAS;    break;
            case LEFT: case Q: case A: directionTentative = Direction.GAUCHE; break;
            case RIGHT: case D:      directionTentative = Direction.DROITE; break;
            default: break;
        }

        if (directionTentative == null) return;

        Plateau courant = multivers.getPlateauCourant();
        boolean vaPousser = courant.vaPousserBoite(directionTentative);
        Multivers.ResultatDeplacement resultat = multivers.deplacer(directionTentative);
        Plateau apres = multivers.getPlateauCourant();

        if (resultat != Multivers.ResultatDeplacement.BLOQUE) {
            controleurAnimation.notifierDeplacementReussi(directionTentative, vaPousser, multivers.estGagne(), maintenantNs);
        } else {
            controleurAnimation.notifierDeplacementBloque(directionTentative, maintenantNs);
        }
    }
}
