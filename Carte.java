import java.util.ArrayList;

public class Carte<T> {

    private ArrayList<ArrayList<T>> carte;

    public Carte(ArrayList<ArrayList<T>> carte){
        this.carte = carte;
    }

    public ArrayList<ArrayList<T>> getCarte() {
        return carte;
    }

    public void setCarte(ArrayList<ArrayList<T>> carte) {
        this.carte = carte;
    }

    public void afficherCarte(){
        for (ArrayList<T> ligne : carte) {
            for (T element : ligne) {
                if (element instanceof CaseMur) {
                    System.out.print("# ");
                } else if (element instanceof CaseVide) {
                    System.out.print("  ");
                } else {
                    System.out.print("? ");
                }
            }
            System.out.println();
        }
    }
}
