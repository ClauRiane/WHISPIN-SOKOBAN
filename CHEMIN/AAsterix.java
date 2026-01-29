public class AAsterix{
    // Methos to check if our cell (row, col) is valid
    public boolean isValid(int[][] grid, int rows, int cols, Pair point){
        if(rows > 0 && cols > 0){
            return (point.getFirst() >= 0) && (point.getFirst() < rows) && (point.getSecond() >= 0) && (point.getSecond() < cols);
        }

        return false;
    }

    // Is the cell blocked ?
    public boolean isUnBlocked(int[][] grid, int rows, int cols, Pair point){
        return isValid(grid, rows, cols, point) && grid[point.getFirst()][point.getSecond()] == 1;
    }

    // Method to check if destination cell has been already reached
    public boolean isDestination(Pair position, Pair dest){
        return position == dest || position.equals(dest);
    }

    // Method to calculate heuristic function
    public double calculateHValue(Pair src, Pair dest){
        return Math.sqrt(Math.pow((src.getFirst() - dest.getFirst()), 2.0) + Math.pow((src.getSecond() - dest.getSecond()), 2.0));
    }

    // Method for tracking the path from source to destination
    public void tracePath(Cell[][] cellDetails, int cols, int rows, Pair dest){

    }
}