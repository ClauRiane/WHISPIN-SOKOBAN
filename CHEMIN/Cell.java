public class Cell {
    public Pair parent;
    // f = g + h, where h is heuristic (check to the definition if you don't know what is it)
    public double f, g, h;

    public Cell(){
        parent = new Pair(-1, -1);
        f = -1;
        g = -1;
        h = -1;
    }

    public Cell(Pair parent, double f, double g, double h){
        this.parent = parent;
        this.f = f;
        this.g = g;
        this.h = h;
    }
}