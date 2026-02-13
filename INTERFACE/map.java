import java.util.ArrayList;

public class map<E>{
    private ArrayList<ArrayList<E>> carte;

    public map(ArrayList<ArrayList<E>> carte){ //constructeur de la classe map
        this.carte = carte;
    }

    public ArrayList<ArrayList<E>> getCarte() { //récupérer la carte
        return carte;
    }

    public void setCarte(ArrayList<ArrayList<E>> carte) { //modifier la carte
        this.carte = carte;
    }

    public void afficherCarte(){ //afficher la carte
        for (ArrayList<E> ligne : carte) {
            for (E element : ligne) {
                System.out.print(element + " ");
            }
            System.out.println();
        }
    }
}
