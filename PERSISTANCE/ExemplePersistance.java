package PERSISTANCE;

import MODELE.Element;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Tantation d'utilisation du code persistance.
 */
public class ExemplePersistance {
    /**
     * 1) crée un plateau,
     * 2) sauvegarde plateau/partie/chemin,
     * 3) recharge les données depuis les fichiers.
     *
     * @param args non utilisé
     * @throws Exception en cas d'erreur de lecture/écriture
     */
    public static void main(String[] args) throws Exception {
        Path cheminPlateau = Path.of("PERSISTANCE", "solution", "niveau1.txt");
        Path cheminPartie = Path.of("PERSISTANCE", "solution", "sauvegarde_partie.json");
        Path cheminSolution = Path.of("PERSISTANCE", "solution", "chemin_solution.json");

        List<String> lignes = List.of(
            "#####",
            "#.@ #",
            "# $ #",
            "# . #",
            "#####"
        );

        List<List<Element>> grille = PlateauTexteFichier.convertirLignesVersGrille(lignes);
        ServicePersistance.sauvegarderPlateauDansFichierTexte(cheminPlateau, grille);

        EtatPartie etat = new EtatPartie(
            "niveau1",
            12,
            System.currentTimeMillis(),
            PlateauTexteFichier.convertirGrilleVersLignes(grille),
            Arrays.asList(Mouvement.DROITE, Mouvement.BAS, Mouvement.GAUCHE)
        );
        ServicePersistance.sauvegarderPartieDansFichierJson(cheminPartie, etat);
        ServicePersistance.sauvegarderCheminDansFichierJson(cheminSolution, etat.getChemin());

        EtatPartie partieChargee = ServicePersistance.chargerPartieDepuisFichierJson(cheminPartie);
        List<Mouvement> cheminCharge = ServicePersistance.chargerCheminDepuisFichierJson(cheminSolution);

        System.out.println("Partie chargée: " + partieChargee.getNiveau() + ", coups=" + partieChargee.getCoups());
        System.out.println("Chemin chargé: " + cheminCharge);
    }
}
