import java.util.List;

public class Carte<T> {

    private List<List<T>> carte;

    public Carte(List<List<T>> carte){
        this.carte = carte;
    }

    public List<List<T>> getCarte() {
        return carte;
    }

    public void setCarte(List<List<T>> carte) {
        this.carte = carte;
    }

    public void afficherCarte(){
        for (List<T> ligne : carte) {
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
