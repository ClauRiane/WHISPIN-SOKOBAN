public class Main{
    public static void main(String[] args) {
        // 0 : The cell is blocked
        // 1 : The cell is not blocked

        int[][] grid = {
            {1, 1, 0, 0, 1, 0, 0, 0},
            {1, 0, 0, 1, 1, 0, 1, 0},
            {1, 1, 0, 1, 0, 0, 1, 0},
            {1, 1, 0, 1, 1, 1, 1, 1},
            {1, 1, 0, 0, 0, 1, 1, 1},
            {0, 1, 1, 1, 0, 1, 1, 0},
            {1, 1, 0, 1, 1, 1, 1, 0},
            {0, 1, 0, 1, 1, 1, 1, 1}
        };

        // Start is the left-most upper-most corner
        Pair src = new Pair(0, 0);

        // Destination is the right-most bottom-most corner
        Pair dest = new Pair(6, 6);

        AAsterix app = new AAsterix();
        app.aStarSearch(grid, grid.length, grid[0].length, src, dest);
    }
}