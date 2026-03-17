import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class PlateauTexteFichier {
    private PlateauTexteFichier() {
    }

    /**
     * Charge un plateau Sokoban depuis un fichier texte.
     *
     * @param chemin chemin du fichier source
     * @return la grille chargée
     * @throws IOException en cas d'erreur de lecture
     */
    public static List<List<Case>> chargerDepuisFichierTexte(Path cheminFichier) throws IOException {
        List<String> lignesPlateau = Files.readAllLines(cheminFichier, StandardCharsets.UTF_8);
        return convertirLignesVersGrille(lignesPlateau);
    }

    /**
     * Sauvegarde un plateau Sokoban dans un fichier texte.
     *
     * @param chemin chemin du fichier cible
     * @param grille grille à sauvegarder
     * @throws IOException en cas d'erreur d'écriture
     */
    public static void sauvegarderDansFichierTexte(Path cheminFichier, List<List<Case>> grillePlateau) throws IOException {
        List<String> lignesPlateau = convertirGrilleVersLignes(grillePlateau);
        if (cheminFichier.getParent() != null) {
            Files.createDirectories(cheminFichier.getParent());
        }
        Files.write(cheminFichier, lignesPlateau, StandardCharsets.UTF_8);
    }

    /**
     * Convertit des lignes ASCII Sokoban en grille d'éléments.
     *
     * @param lignes lignes du plateau
     * @return grille correspondante
     */
    public static List<List<Case>> convertirLignesVersGrille(List<String> lignesPlateau) {
        if (lignesPlateau == null || lignesPlateau.isEmpty()) {
            throw new IllegalArgumentException("Le fichier de plateau est vide");
        }

        int largeurMax = 0;
        for (String ligne : lignesPlateau) {
            if (ligne.length() > largeurMax) {
                largeurMax = ligne.length();
            }
        }

        List<List<Case>> grillePlateau = new ArrayList<>();
        int nombrePersonnages = 0;

        for (String ligneTexte : lignesPlateau) {
            List<Case> elementsDeLigne = new ArrayList<>();
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
    public static List<String> convertirGrilleVersLignes(List<List<Case>> grillePlateau) {
        if (grillePlateau == null || grillePlateau.isEmpty()) {
            throw new IllegalArgumentException("La grille est vide");
        }

        List<String> lignesPlateau = new ArrayList<>();
        for (List<Case> elementsDeLigne : grillePlateau) {
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
}
