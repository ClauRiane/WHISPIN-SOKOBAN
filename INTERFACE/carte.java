package INTERFACE;
import MODELE.CaseVide;
import MODELE.Mur;
import java.util.ArrayList;

public class carte<Element> {
    private ArrayList<ArrayList<Element>> carte;

    public carte(ArrayList<ArrayList<Element>> carte){ //constructeur de la classe map
        this.carte = carte;
    }

    public ArrayList<ArrayList<Element>> getCarte() { //récupérer la carte
        return carte;
    }

    public void setCarte(ArrayList<ArrayList<Element>> carte) { //modifier la carte
        this.carte = carte;
    }

    public void afficherCarte(){ //afficher la carte
        System.out.println("Taille de la carte : " + carte.size());
        System.out.println("Affichage de la carte :");
        for (ArrayList<Element> ligne : carte) {
            for (Element element : ligne) {
                //System.out.print(element + " ");
                if (element instanceof Mur) {
                    System.out.print("# ");
                } else if (element instanceof CaseVide) {
                    System.out.print("  ");
                } else {
                    System.out.print("? "); // pour les éléments inconnus
                }
            }
            System.out.println();
        }
        System.out.println("fin");
    }
}
