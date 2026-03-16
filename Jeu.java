import java.nio.file.Path;


public class Jeu {

    private Carte<Case> carte;

    public Jeu() {
        carte = creerCarteDeTest();
    }

    public Carte<Case> creerCarteDeTest() {
        /*ArrayList<ArrayList<Case>> grille = new ArrayList<>();

        ArrayList<ArrayList<Case>> carte = new ArrayList<>();

        // Ligne 1: #####
        ArrayList<Case> ligne1 = new ArrayList<>();
        ligne1.add(CaseMur.getInstance());
        ligne1.add(CaseMur.getInstance());
        ligne1.add(CaseMur.getInstance());
        ligne1.add(CaseMur.getInstance());
        ligne1.add(CaseMur.getInstance());
        // Ligne 2: #   #
        ArrayList<Case> ligne2 = new ArrayList<>();
        ligne2.add(CaseMur.getInstance());
        ligne2.add(CaseVide.getInstance());   // à créer
        ligne2.add(CaseVide.getInstance());
        ligne2.add(CaseVide.getInstance());
        ligne2.add(CaseMur.getInstance());
        // Ligne 3: #####
        ArrayList<Case> ligne3 = new ArrayList<>();
        ligne3.add(CaseMur.getInstance());
        ligne3.add(CaseMur.getInstance());
        ligne3.add(CaseMur.getInstance());
        ligne3.add(CaseMur.getInstance());
        ligne3.add(CaseMur.getInstance());

        carte.add(ligne1);
        carte.add(ligne2);
        carte.add(ligne3);

        // construire la carte ici (comme plus haut)
        return new carte<>(carte);*/
        try {
        return new Carte<>(PlateauTexteFichier.chargerDepuisFichierTexte(Path.of("carte_niveau_simple.txt")));
    } catch (Exception e) {
        System.out.println("Erreur : impossible de charger la carte !");
        e.printStackTrace();
        System.exit(1);
        return null; // ce code ne sera jamais atteint, mais est nécessaire pour que le compilateur soit satisfait
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