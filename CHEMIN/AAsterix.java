
import java.util.PriorityQueue;
import java.util.Stack;

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
        System.err.println("The Path : ");
        Stack<Pair> path = new Stack<>();

        int row = dest.getFirst();
        int col = dest.getSecond();

        Pair nextNode = cellDetails[row][col].parent;
        do { 
            path.push(new Pair(row, col));
            nextNode = cellDetails[row][col].parent;
            row = nextNode.getFirst();
            col = nextNode.getSecond();
        } while (cellDetails[row][col].parent != nextNode);

        while(!path.empty()){
            Pair p = path.peek();
            path.pop();
            System.out.println("-> (" + p.getFirst() + "," + p.getSecond() + ") ");
        }
    }

    public void aStarSearch(int[][] grid, int rows, int cols, Pair src, Pair dest){
        if(!isValid(grid, rows, cols, src)){
            System.out.println("Source is invalid...");
            return;
        }

        if(!(isValid(grid, rows, cols, dest))){
            System.out.println("Destination is invalid...");
            return;
        }

        if(isDestination(src, dest)){
            System.out.println("We're already (t)here...");
            return;
        }

        // Our closed list
        boolean[][] closedList = new boolean[rows][cols];
        Cell[][] cellDetails = new Cell[rows][cols];

        int i, j;

        // Initialising of the starting cell
        i = src.getFirst();
        j = src.getSecond();
        cellDetails[i][j] = new Cell();
        cellDetails[i][j].f = 0.0;
        cellDetails[i][j].g = 0.0;
        cellDetails[i][j].h = 0.0;
        cellDetails[i][j].parent = new Pair(i, j);

        // Creating an open list
        PriorityQueue<Details> openList = new PriorityQueue<>((o1, o2) -> (int) Math.round(o1.getValue() - o2.getValue()));

        // Put the starting cell on the open list, set f.startCell = 0
        openList.add(new Details(0.0, i, j));

        while(!openList.isEmpty()){
            Details p = openList.peek();
            // Add to the closed list
            i = p.getI(); // Second element of tuple
            j = p.getJ(); // Third element of tuple

            // Remove from the open list
            openList.poll();
            closedList[i][j] = true;

            // Generating all the 4 neighbors of the cell
            for(int addX = -1; addX <= 1; addX++){
                for(int addY = -1; addY <= 1; addY++){
                    Pair neighbour = new Pair(i + addX, j + addY);
                    if(isValid(grid, rows, cols, neighbour)){
                        if(cellDetails[neighbour.getFirst()] == null){
                            cellDetails[neighbour.getFirst()] = new Cell[cols];
                        }

                        if(cellDetails[neighbour.getFirst()][neighbour.getSecond()] == null){
                            cellDetails[neighbour.getFirst()][neighbour.getSecond()] = new Cell();
                        }

                        if(isDestination(neighbour, dest)){
                            cellDetails[neighbour.getFirst()][neighbour.getSecond()].parent = new Pair(i, j);
                            System.out.println("The destination cell is found");
                            tracePath(cellDetails, rows, cols, dest);
                            return;
                        }

                        else if (!closedList[neighbour.getFirst()][neighbour.getSecond()] && isUnBlocked(grid, rows, cols, neighbour)){
                            double gNew, hNew, fNew;
                            gNew = cellDetails[i][j].g + 1.0;
                            hNew = calculateHValue(neighbour, dest);
                            fNew = gNew + hNew;

                            if(cellDetails[neighbour.getFirst()][neighbour.getSecond()].f == -1 || cellDetails[neighbour.getFirst()][neighbour.getSecond()].f > fNew){
                                openList.add(new Details(fNew, neighbour.getFirst(), neighbour.getSecond()));

                                // Update the details of this
                                // cell
                                cellDetails[neighbour.getFirst()][neighbour.getSecond()].g = gNew;
                                cellDetails[neighbour.getFirst()][neighbour.getSecond()].f = fNew;
                                cellDetails[neighbour.getFirst()][neighbour.getSecond()].parent = new Pair(i, j);
                            }
                        }
                    }
                }
            }
        }

        System.out.println("Failed to find the Destination Cell");
    }
}