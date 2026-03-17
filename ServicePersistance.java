import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ServicePersistance {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String CLE_NIVEAU = "niveau";
    private static final String CLE_COUPS = "coups";
    private static final String CLE_HORODATAGE = "horodatage";
    private static final String CLE_PLATEAU = "plateau";
    private static final String CLE_CHEMIN = "chemin";
    private static final String CLE_MOUVEMENTS = "mouvements";

    private ServicePersistance() {
    }

    /**
     * Sauvegarde une grille Sokoban dans un fichier texte.
     *
     * @param chemin chemin du fichier cible
     * @param grille grille à sauvegarder
     * @throws IOException en cas d'erreur d'écriture
     */
    public static void sauvegarderPlateauDansFichierTexte(Path cheminFichier, List<List<Case>> grillePlateau) throws IOException {
        PlateauTexteFichier.sauvegarderDansFichierTexte(cheminFichier, grillePlateau);
    }

    /**
     * Charge une grille Sokoban depuis un fichier texte.
     *
     * @param chemin chemin du fichier source
     * @return la grille chargée
     * @throws IOException en cas d'erreur de lecture
     */
    public static List<List<Case>> chargerPlateauDepuisFichierTexte(Path cheminFichier) throws IOException {
        return PlateauTexteFichier.chargerDepuisFichierTexte(cheminFichier);
    }

    /**
     * Sauvegarde l'état complet d'une partie dans un fichier JSON.
     *
     * @param chemin chemin du fichier cible
     * @param etat état de partie à sauvegarder
     * @throws IOException en cas d'erreur d'écriture
     */
    public static void sauvegarderPartieDansFichierJson(Path cheminFichier, EtatPartie etatPartie) throws IOException {
        Map<String, Object> donneesJson = new LinkedHashMap<>();
        donneesJson.put(CLE_NIVEAU, etatPartie.getNiveau());
        donneesJson.put(CLE_COUPS, etatPartie.getCoups());
        donneesJson.put(CLE_HORODATAGE, etatPartie.getHorodatage());
        donneesJson.put(CLE_PLATEAU, new ArrayList<>(etatPartie.getPlateau()));

        donneesJson.put(CLE_CHEMIN, convertirMouvementsEnCodes(etatPartie.getChemin()));

        ecrireDansFichierJson(cheminFichier, donneesJson);
    }

    /**
     * Charge l'état complet d'une partie depuis un fichier JSON.
     *
     * @param chemin chemin du fichier source
     * @return état de partie reconstruit
     * @throws IOException en cas d'erreur de lecture
     */
    public static EtatPartie chargerPartieDepuisFichierJson(Path cheminFichier) throws IOException {
        Map<String, Object> donneesJson = MAPPER.readValue(
            Files.readString(cheminFichier),
            new TypeReference<Map<String, Object>>() {
            }
        );

        String niveau = lireChampTexte(donneesJson.get(CLE_NIVEAU), CLE_NIVEAU);
        int coups = lireChampEntier(donneesJson.get(CLE_COUPS), CLE_COUPS);
        long horodatage = lireChampLong(donneesJson.get(CLE_HORODATAGE), CLE_HORODATAGE);

        List<String> lignesPlateau = lireChampListeTexte(donneesJson.get(CLE_PLATEAU), CLE_PLATEAU);
        List<String> codesMouvements = lireChampListeTexte(donneesJson.get(CLE_CHEMIN), CLE_CHEMIN);
        List<Mouvement> mouvementsChemin = new ArrayList<>();
        for (String codeMouvement : codesMouvements) {
            if (codeMouvement.length() != 1) {
                throw new IllegalArgumentException("Code mouvement invalide: " + codeMouvement);
            }
            mouvementsChemin.add(Mouvement.depuisCode(codeMouvement.charAt(0)));
        }

        return new EtatPartie(niveau, coups, horodatage, lignesPlateau, mouvementsChemin);
    }

    /**
     * Sauvegarde une liste de mouvements dans un fichier JSON.
     *
     * @param chemin chemin du fichier cible
     * @param mouvements mouvements à sauvegarder
     * @throws IOException en cas d'erreur d'écriture
     */
    public static void sauvegarderCheminDansFichierJson(Path cheminFichier, List<Mouvement> listeMouvements) throws IOException {
        Map<String, Object> donneesJson = new LinkedHashMap<>();

        donneesJson.put(CLE_MOUVEMENTS, convertirMouvementsEnCodes(listeMouvements));
        ecrireDansFichierJson(cheminFichier, donneesJson);
    }

    /**
     * Charge une liste de mouvements depuis un fichier JSON.
     *
     * @param chemin chemin du fichier source
     * @return liste des mouvements chargés
     * @throws IOException en cas d'erreur de lecture
     */
    public static List<Mouvement> chargerCheminDepuisFichierJson(Path cheminFichier) throws IOException {
        Map<String, Object> donneesJson = MAPPER.readValue(
            Files.readString(cheminFichier),
            new TypeReference<Map<String, Object>>() {
            }
        );
        List<String> codesMouvements = lireChampListeTexte(donneesJson.get(CLE_MOUVEMENTS), CLE_MOUVEMENTS);

        List<Mouvement> mouvementsCharges = new ArrayList<>();
        for (String codeMouvement : codesMouvements) {
            if (codeMouvement.length() != 1) {
                throw new IllegalArgumentException("Code mouvement invalide: " + codeMouvement);
            }
            mouvementsCharges.add(Mouvement.depuisCode(codeMouvement.charAt(0)));
        }
        return mouvementsCharges;
    }

    private static void ecrireDansFichierJson(Path cheminFichier, Map<String, Object> donneesJson) throws IOException {
        if (cheminFichier.getParent() != null) {
            Files.createDirectories(cheminFichier.getParent());
        }
        MAPPER.writerWithDefaultPrettyPrinter().writeValue(cheminFichier.toFile(), donneesJson);
    }

    private static String lireChampTexte(Object valeurChamp, String nomChamp) {
        if (valeurChamp instanceof String texte) {
            return texte;
        }
        throw new IllegalArgumentException("Champ JSON invalide (String attendu): " + nomChamp);
    }

    private static int lireChampEntier(Object valeurChamp, String nomChamp) {
        if (valeurChamp instanceof Number nombre) {
            return nombre.intValue();
        }
        throw new IllegalArgumentException("Champ JSON invalide (int attendu): " + nomChamp);
    }

    private static long lireChampLong(Object valeurChamp, String nomChamp) {
        if (valeurChamp instanceof Number nombre) {
            return nombre.longValue();
        }
        throw new IllegalArgumentException("Champ JSON invalide (long attendu): " + nomChamp);
    }

    private static List<String> lireChampListeTexte(Object valeurChamp, String nomChamp) {
        if (!(valeurChamp instanceof List<?> listeValeurs)) {
            throw new IllegalArgumentException("Champ JSON invalide (liste attendue): " + nomChamp);
        }

        List<String> resultat = new ArrayList<>();
        for (Object valeurElement : listeValeurs) {
            if (!(valeurElement instanceof String texte)) {
                throw new IllegalArgumentException("Champ JSON invalide (liste de String attendue): " + nomChamp);
            }
            resultat.add(texte);
        }
        return resultat;
    }

    private static List<String> convertirMouvementsEnCodes(List<Mouvement> mouvements) {
        List<String> codesMouvements = new ArrayList<>();
        for (Mouvement mouvement : mouvements) {
            codesMouvements.add(String.valueOf(mouvement.obtenirCode()));
        }
        return codesMouvements;
    }
}
