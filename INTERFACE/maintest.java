import java.util.ArrayList;

public class maintest {
    public static void main(String[] args) {
        ArrayList<ArrayList<String>> carte = new ArrayList<>();

        ArrayList<String> ligne1 = new ArrayList<>();
        ligne1.add("A");
        ligne1.add("B");
        ligne1.add("C");

        ArrayList<String> ligne2 = new ArrayList<>();
        ligne2.add("D");
        ligne2.add("E");
        ligne2.add("F");

        carte.add(ligne1);
        carte.add(ligne2);

        map<String> maCarte = new map<>(carte);
        maCarte.afficherCarte();
    }
}
