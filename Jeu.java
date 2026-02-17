package INTERFACE;
import MODELE.CaseVide;
import MODELE.Element;
import MODELE.Mur;
import java.util.ArrayList;


public class Jeu {

    private carte<Element> carte;

    public Jeu() {
        carte = creerCarteDeTest();
    }

    public carte<Element> creerCarteDeTest() {
        ArrayList<ArrayList<Element>> grille = new ArrayList<>();

        ArrayList<ArrayList<Element>> carte = new ArrayList<>();

        // Ligne 1: #####
        ArrayList<Element> ligne1 = new ArrayList<>();
        ligne1.add(Mur.getInstance());
        ligne1.add(Mur.getInstance());
        ligne1.add(Mur.getInstance());
        ligne1.add(Mur.getInstance());
        ligne1.add(Mur.getInstance());

        // Ligne 2: #   #
        ArrayList<Element> ligne2 = new ArrayList<>();
        ligne2.add(Mur.getInstance());
        ligne2.add(CaseVide.getInstance());   // à créer
        ligne2.add(CaseVide.getInstance());
        ligne2.add(CaseVide.getInstance());
        ligne2.add(Mur.getInstance());

        // Ligne 3: #####
        ArrayList<Element> ligne3 = new ArrayList<>();
        ligne3.add(Mur.getInstance());
        ligne3.add(Mur.getInstance());
        ligne3.add(Mur.getInstance());
        ligne3.add(Mur.getInstance());
        ligne3.add(Mur.getInstance());

        carte.add(ligne1);
        carte.add(ligne2);
        carte.add(ligne3);

        // construire la carte ici (comme plus haut)
        return new carte<>(carte);
    }

    public void lancer() {
        System.out.println("Bienvenue dans le jeu de Sokoban !");
        carte.afficherCarte();
    }
}