import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Jeu {
    private static final Path CHEMIN_CARTE_PAR_DEFAUT = Path.of("carte_niveau_simple.txt");

    private final Plateau plateau;

    public Jeu() {
        this.plateau = chargerPlateauDeTest();
    }

    private static Plateau chargerPlateauDeTest() {
        try {
            List<List<Case>> grille = PlateauTexteFichier.chargerDepuisFichierTexte(CHEMIN_CARTE_PAR_DEFAUT);
            return new Plateau(convertirEnArrayList(grille));
        } catch (Exception e) {
            throw new IllegalStateException("Erreur : impossible de charger la carte !", e);
        }
    }

    private static ArrayList<ArrayList<Case>> convertirEnArrayList(List<List<Case>> grille) {
        ArrayList<ArrayList<Case>> copie = new ArrayList<>();
        for (List<Case> ligne : grille) {
            copie.add(new ArrayList<>(ligne));
        }
        return copie;
    }

    public Plateau getPlateau() {
        return plateau;
    }

    public void lancer() {
        System.out.println("Bienvenue dans le jeu de Sokoban !");
        System.out.println(plateau);
    }
}