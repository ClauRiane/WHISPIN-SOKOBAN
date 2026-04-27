import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PlateauTexteFichier {
    private static final Pattern PATTERN_ENTETE = Pattern.compile("^([A-Za-z])\\s+(\\d+)$");

    private PlateauTexteFichier() {
    }

    /**
     * Charge un plateau Sokoban depuis un fichier texte.
     *
     * @param chemin chemin du fichier source
     * @return la grille chargée
     * @throws IOException en cas d'erreur de lecture
     */
    public static ArrayList<ArrayList<Case>> chargerDepuisFichierTexte(Path cheminFichier) throws IOException {
        ArrayList<String> toutesLignes = new ArrayList<>(Files.readAllLines(cheminFichier, StandardCharsets.UTF_8));
        if (toutesLignes.isEmpty()) {
            throw new IllegalArgumentException("Le fichier de plateau est vide");
        }

        // En-tete attendu: "lettre taille" (ex: A 6)
        String premiereLigne = toutesLignes.get(0).trim();
        ArrayList<String> lignesPlateau;
        Matcher matcherEntete = PATTERN_ENTETE.matcher(premiereLigne);
        if (!matcherEntete.matches()) {
            throw new IllegalArgumentException("Entete invalide. Format attendu: 'A 6'");
        }

        char lettreNiveau = matcherEntete.group(1).charAt(0);
        int tailleAnnoncee = Integer.parseInt(matcherEntete.group(2));
        lignesPlateau = new ArrayList<>(toutesLignes.subList(1, toutesLignes.size()));
        validerDimensionsAnnoncees(lignesPlateau, tailleAnnoncee, lettreNiveau);
        return convertirLignesVersGrille(lignesPlateau);
    }

    /**
     * Sauvegarde un plateau Sokoban dans un fichier texte.
     *
     * @param chemin chemin du fichier cible
     * @param grille grille à sauvegarder
     * @throws IOException en cas d'erreur d'écriture
     */
    public static void sauvegarderDansFichierTexte(Path cheminFichier, ArrayList<ArrayList<Case>> grillePlateau) throws IOException {
        sauvegarderDansFichierTexte(cheminFichier, grillePlateau, 'A');
    }

    /**
     * Sauvegarde un plateau Sokoban dans un fichier texte avec en-tete "lettre taille".
     *
     * @param cheminFichier chemin du fichier cible
     * @param grillePlateau grille a sauvegarder
     * @param lettreNiveau identifiant de niveau (ex: A, B, C)
     * @throws IOException en cas d'erreur d'ecriture
     */
    public static void sauvegarderDansFichierTexte(
        Path cheminFichier,
        ArrayList<ArrayList<Case>> grillePlateau,
        char lettreNiveau
    ) throws IOException {
        if (!Character.isLetter(lettreNiveau)) {
            throw new IllegalArgumentException("La lettre de niveau doit etre alphabetique");
        }
        validerGrilleCarree(grillePlateau);

        ArrayList<String> lignesGrille = convertirGrilleVersLignes(grillePlateau);
        ArrayList<String> toutesLignes = new ArrayList<>();
        toutesLignes.add(Character.toUpperCase(lettreNiveau) + " " + grillePlateau.size());
        toutesLignes.addAll(lignesGrille);
        if (cheminFichier.getParent() != null) {
            Files.createDirectories(cheminFichier.getParent());
        }
        Files.write(cheminFichier, toutesLignes, StandardCharsets.UTF_8);
    }

    /**
     * Convertit des lignes ASCII Sokoban en grille d'éléments.
     *
     * @param lignes lignes du plateau
     * @return grille correspondante
     */
    public static ArrayList<ArrayList<Case>> convertirLignesVersGrille(ArrayList<String> lignesPlateau) {
        if (lignesPlateau == null || lignesPlateau.isEmpty()) {
            throw new IllegalArgumentException("Le fichier de plateau est vide");
        }

        int largeurMax = 0;
        for (String ligne : lignesPlateau) {
            if (ligne.length() > largeurMax) {
                largeurMax = ligne.length();
            }
        }

        ArrayList<ArrayList<Case>> grillePlateau = new ArrayList<>();
        int nombrePersonnages = 0;

        for (String ligneTexte : lignesPlateau) {
            ArrayList<Case> elementsDeLigne = new ArrayList<>();
            for (int i = 0; i < largeurMax; i++) {
                // Complète les lignes trop courtes pour garantir une grille rectangulaire.
                char symbole = i < ligneTexte.length() ? ligneTexte.charAt(i) : ' ';
                Case elementCourant = convertirSymboleVersElement(symbole);
                if (elementCourant.estPersonnageCible()) {
                    nombrePersonnages++;
                }
                elementsDeLigne.add(elementCourant);
            }
            grillePlateau.add(elementsDeLigne);
        }

        if (nombrePersonnages != 1) {
            throw new IllegalArgumentException("Le plateau doit contenir exactement un personnage");
        }

        return grillePlateau;
    }

    /**
     * Convertit une grille d'éléments en lignes ASCII Sokoban.
     *
     * @param grille grille à convertir
     * @return lignes texte du plateau
     */
    public static ArrayList<String> convertirGrilleVersLignes(ArrayList<ArrayList<Case>> grillePlateau) {
        if (grillePlateau == null || grillePlateau.isEmpty()) {
            throw new IllegalArgumentException("La grille est vide");
        }

        ArrayList<String> lignesPlateau = new ArrayList<>();
        for (ArrayList<Case> elementsDeLigne : grillePlateau) {
            StringBuilder ligneConstruite = new StringBuilder();
            for (Case elementCourant : elementsDeLigne) {
                ligneConstruite.append(elementCourant.getSymbole());
            }
            lignesPlateau.add(ligneConstruite.toString());
        }
        return lignesPlateau;
    }

    /**
     * Convertit un symbole ASCII en élément du modèle.
     *
     * @param symbole caractère Sokoban
     * @return élément correspondant
     */
    public static Case convertirSymboleVersElement(char symbole) {
        return switch (symbole) {
            case '#' -> CaseMur.getInstance();
            case ' ' -> CaseVide.getInstance();
            case '.' -> CaseCible.getInstance();
            case '$' -> new CaseBoite(false);
            case '*' -> new CaseBoite(true);
            case '@' -> new Personnage(false);
            case '+' -> new Personnage(true);
            default -> throw new IllegalArgumentException("Symbole inconnu dans le plateau: '" + symbole + "'");
        };
    }

    private static void validerDimensionsAnnoncees(ArrayList<String> lignesPlateau, int tailleAnnoncee, char lettreNiveau) {
        if (tailleAnnoncee <= 0) {
            throw new IllegalArgumentException("Taille invalide dans l'entete du niveau " + lettreNiveau + ": " + tailleAnnoncee);
        }
        if (lignesPlateau.size() != tailleAnnoncee) {
            throw new IllegalArgumentException(
                "Le niveau " + lettreNiveau + " annonce " + tailleAnnoncee
                    + " lignes mais le fichier en contient " + lignesPlateau.size()
            );
        }
        for (int i = 0; i < lignesPlateau.size(); i++) {
            int longueurLigne = lignesPlateau.get(i).length();
            if (longueurLigne != tailleAnnoncee) {
                throw new IllegalArgumentException(
                    "Le niveau " + lettreNiveau + " annonce " + tailleAnnoncee
                        + " colonnes, mais la ligne " + (i + 1) + " en contient " + longueurLigne
                );
            }
        }
    }

    private static void validerGrilleCarree(ArrayList<ArrayList<Case>> grillePlateau) {
        if (grillePlateau == null || grillePlateau.isEmpty()) {
            throw new IllegalArgumentException("La grille est vide");
        }
        int taille = grillePlateau.size();
        for (int i = 0; i < grillePlateau.size(); i++) {
            ArrayList<Case> ligne = grillePlateau.get(i);
            if (ligne == null || ligne.size() != taille) {
                throw new IllegalArgumentException(
                    "La grille doit etre carree (" + taille + "x" + taille + "), ligne "
                        + (i + 1) + " invalide"
                );
            }
        }
    }
}
