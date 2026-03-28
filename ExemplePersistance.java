import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Tentative d'utilisation du code persistance.
 */
public final class ExemplePersistance {
    private static final Path CHEMIN_PLATEAU = Path.of("PERSISTANCE", "solution", "niveau1.txt");
    private static final Path CHEMIN_PARTIE = Path.of("PERSISTANCE", "solution", "sauvegarde_partie.json");
    private static final Path CHEMIN_SOLUTION = Path.of("PERSISTANCE", "solution", "chemin_solution.json");

    private ExemplePersistance() {
    }

    /**
     * 1) crée un plateau,
     * 2) sauvegarde plateau/partie/chemin,
     * 3) recharge les données depuis les fichiers.
     *
     * @param args non utilisé
     * @throws Exception en cas d'erreur de lecture/écriture
     */
    public static void main(String[] args) throws Exception {
        List<String> lignesPlateau = List.of(
            "#####",
            "#.@ #",
            "# $ #",
            "# . #",
            "#####"
        );

        List<List<Case>> grille = PlateauTexteFichier.convertirLignesVersGrille(lignesPlateau);
        ServicePersistance.sauvegarderPlateauDansFichierTexte(CHEMIN_PLATEAU, grille);

        EtatPartie etat = new EtatPartie(
            "niveau1",
            12,
            System.currentTimeMillis(),
            PlateauTexteFichier.convertirGrilleVersLignes(grille),
            Arrays.asList(
                new Mouvement(Direction.DROITE, false),
                new Mouvement(Direction.BAS, false),
                new Mouvement(Direction.GAUCHE, false)
            )
        );
        ServicePersistance.sauvegarderPartieDansFichierJson(CHEMIN_PARTIE, etat);
        ServicePersistance.sauvegarderCheminDansFichierJson(CHEMIN_SOLUTION, etat.getChemin());

        EtatPartie partieChargee = ServicePersistance.chargerPartieDepuisFichierJson(CHEMIN_PARTIE);
        List<Mouvement> cheminCharge = ServicePersistance.chargerCheminDepuisFichierJson(CHEMIN_SOLUTION);

        System.out.println("Partie chargée: " + partieChargee.getNiveau() + ", coups=" + partieChargee.getCoups());
        System.out.println("Chemin chargé: " + cheminCharge);
    }
}
