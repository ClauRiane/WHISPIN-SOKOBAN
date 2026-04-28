import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Stack;

/**
 * Algorithme A* pour trouver le chemin le plus court sur la grille Sokoban.
 *
 * Convention de la grille int[][] :
 *   grid[row][col]  où  row = axe Y (vertical), col = axe X (horizontal)
 *   1 = case traversable, 0 = mur ou boîte (bloqué)
 */
public class AAsterix {

    // Directions : haut, bas, gauche, droite (en delta row, delta col)
    private static final int[] dRow = {-1, 1, 0, 0};
    private static final int[] dCol = { 0, 0,-1, 1};

    /** Vérifie qu'une position est dans les limites de la grille. */
    public boolean isValid(int[][] grid, int rows, int cols, Pair point) {
        return rows > 0 && cols > 0
            && point.getFirst()  >= 0 && point.getFirst()  < rows
            && point.getSecond() >= 0 && point.getSecond() < cols;
    }

    /** Vérifie que la case n'est pas bloquée (0=mur/boîte, 1=libre). */
    public boolean isUnBlocked(int[][] grid, int rows, int cols, Pair point) {
        return isValid(grid, rows, cols, point)
            && grid[point.getFirst()][point.getSecond()] == 1;
    }

    /** Vérifie si la position courante est la destination. */
    public boolean isDestination(Pair position, Pair dest) {
        return position.equals(dest);
    }

    /** Distance de Manhattan comme heuristique. */
    public double calculateHValue(Pair src, Pair dest) {
        return Math.abs(src.getFirst()  - dest.getFirst())
             + Math.abs(src.getSecond() - dest.getSecond());
    }

    /**
     * Reconstruit le chemin de src à dest à partir des cellules parentes.
     * Retourne une liste ordonnée Pair(row,col), src exclu, dest inclus.
     */
    private List<Pair> tracePath(Cell[][] cellDetails, Pair src, Pair dest) {
        Stack<Pair> stack = new Stack<>();
        int row = dest.getFirst();
        int col = dest.getSecond();

        while (!(row == src.getFirst() && col == src.getSecond())) {
            stack.push(new Pair(row, col));
            Pair parent = cellDetails[row][col].parent;
            row = parent.getFirst();
            col = parent.getSecond();
        }

        List<Pair> path = new ArrayList<>();
        while (!stack.isEmpty()) {
            path.add(stack.pop());
        }
        return path;
    }

    /**
     * Lance la recherche A* et retourne le chemin ordonné.
     *
     * @param grid  grille [rows][cols] : 1 = traversable, 0 = bloqué
     * @param rows  nombre de lignes
     * @param cols  nombre de colonnes
     * @param src   position de départ  Pair(row, col)
     * @param dest  position d'arrivée  Pair(row, col)
     * @return liste de Pair(row,col) représentant les cases à parcourir
     *         (src exclu, dest inclus), liste vide si déjà sur place,
     *         ou null si aucun chemin trouvé.
     */
    public List<Pair> aStarSearch(int[][] grid, int rows, int cols, Pair src, Pair dest) {
        if (!isValid(grid, rows, cols, src)) {
            System.out.println("[A*] Source invalide.");
            return null;
        }
        if (!isValid(grid, rows, cols, dest)) {
            System.out.println("[A*] Destination invalide.");
            return null;
        }
        if (isDestination(src, dest)) {
            return new ArrayList<>();
        }

        boolean[][] closedList  = new boolean[rows][cols];
        Cell[][]    cellDetails = new Cell[rows][cols];

        int si = src.getFirst();
        int sj = src.getSecond();
        cellDetails[si][sj] = new Cell();
        cellDetails[si][sj].f = 0.0;
        cellDetails[si][sj].g = 0.0;
        cellDetails[si][sj].h = 0.0;
        cellDetails[si][sj].parent = new Pair(si, sj); // pointe sur lui-même = marqueur de source

        PriorityQueue<Details> openList = new PriorityQueue<>(
            (o1, o2) -> Double.compare(o1.getValue(), o2.getValue())
        );
        openList.add(new Details(0.0, si, sj));

        while (!openList.isEmpty()) {
            Details p = openList.poll();
            int i = p.getI();
            int j = p.getJ();
            closedList[i][j] = true;

            for (int k = 0; k < 4; k++) {
                int ni = i + dRow[k];
                int nj = j + dCol[k];
                Pair neighbour = new Pair(ni, nj);

                if (!isValid(grid, rows, cols, neighbour)) continue;

                if (cellDetails[ni][nj] == null) {
                    cellDetails[ni][nj] = new Cell();
                }

                if (isDestination(neighbour, dest)) {
                    cellDetails[ni][nj].parent = new Pair(i, j);
                    return tracePath(cellDetails, src, dest);
                }

                if (!closedList[ni][nj] && isUnBlocked(grid, rows, cols, neighbour)) {
                    double gNew = cellDetails[i][j].g + 1.0;
                    double hNew = calculateHValue(neighbour, dest);
                    double fNew = gNew + hNew;

                    if (cellDetails[ni][nj].f < 0 || cellDetails[ni][nj].f > fNew) {
                        openList.add(new Details(fNew, ni, nj));
                        cellDetails[ni][nj].g = gNew;
                        cellDetails[ni][nj].f = fNew;
                        cellDetails[ni][nj].parent = new Pair(i, j);
                    }
                }
            }
        }

        System.out.println("[A*] Aucun chemin trouvé.");
        return null;
    }
}