import java.nio.file.Path;

public class Jeu {
    private static final Path CHEMIN_CARTE_PAR_DEFAUT = Path.of("carte_niveau_simple.txt");

    private final Carte<Case> carte;

    public Jeu() {
        this.carte = chargerCarteDeTest();
    }

    private static Carte<Case> chargerCarteDeTest() {
        try {
            return new Carte<>(PlateauTexteFichier.chargerDepuisFichierTexte(CHEMIN_CARTE_PAR_DEFAUT));
        } catch (Exception e) {
            throw new IllegalStateException("Erreur : impossible de charger la carte !", e);
        }
    }

    public Carte<Case> getCarte() {
        return carte;
    }

    public void lancer() {
        System.out.println("Bienvenue dans le jeu de Sokoban !");
        carte.afficherCarte();
    }
}