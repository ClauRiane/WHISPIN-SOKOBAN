package MODELE;

public abstract class Element {
    public abstract boolean estTraversable();
    public abstract boolean estPoussable();
    public abstract char getSymbole();

    public boolean estCible() {
        return false;
    }

    public boolean estBoite() {
        return false;
    }

    public boolean estPersonnage() {
        return false;
    }

    @Override
    public String toString() {
        return String.valueOf(getSymbole());
    }
}
