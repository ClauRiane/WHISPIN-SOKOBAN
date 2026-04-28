import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Stack;

/**
 * Implémentation de l'algorithme de recherche de chemin A* (A-star)
 * pour le déplacement automatique du personnage sur la grille Sokoban.
 *
 * <h2>Principe général</h2>
 * <p>A* est un algorithme de recherche de plus court chemin sur un graphe.
 * Il combine :</p>
 * <ul>
 *   <li>Le coût réel {@code g} : nombre de pas effectués depuis la source.</li>
 *   <li>Une heuristique {@code h} : estimation optimiste du coût restant
 *       jusqu'à la destination (distance de Manhattan).</li>
 *   <li>Un coût total {@code f = g + h} qui guide l'exploration — on explore
 *       toujours la case dont {@code f} est le plus faible.</li>
 * </ul>
 *
 * <h2>Convention de coordonnées</h2>
 * <p>La grille est représentée par un tableau {@code int[rows][cols]} où :</p>
 * <ul>
 *   <li>{@code rows} = nombre de lignes (axe Y, vertical)</li>
 *   <li>{@code cols} = nombre de colonnes (axe X, horizontal)</li>
 *   <li>{@code grid[row][col] == 1} → case traversable (libre)</li>
 *   <li>{@code grid[row][col] == 0} → case bloquée (mur ou boîte)</li>
 * </ul>
 * <p>Les positions sont des {@link Pair}{@code (row, col)}, pas {@code (x, y)}.</p>
 *
 * <h2>Classes de support</h2>
 * <ul>
 *   <li>{@link Cell} — métadonnées A* d'une case ({@code f, g, h, parent}).</li>
 *   <li>{@link Details} — entrée de la liste ouverte ({@code f, i, j}).</li>
 *   <li>{@link Pair} — coordonnées {@code (row, col)} d'une case.</li>
 * </ul>
 *
 * <h2>Intégration dans le jeu</h2>
 * <p>Appelé par {@code ControleurPartie.gererClic()} lors d'un clic souris.
 * Le chemin retourné est converti en une file de {@link Direction} exécutée
 * progressivement par la boucle d'animation (120 ms entre chaque pas).</p>
 */
public class AAsterix {

    // ── Constructeur ──────────────────────────────────────────────────────────

    /**
     * Construit une instance de l'algorithme A*.
     *
     * <p>Cette classe est sans état : tous les paramètres nécessaires
     * ({@code grid}, {@code src}, {@code dest}) sont passés directement
     * à {@link #aStarSearch}. Une même instance peut donc être réutilisée
     * pour plusieurs recherches successives.</p>
     */
    public AAsterix() {
        // Aucun état à initialiser
    }

    // ── Constantes de déplacement ─────────────────────────────────────────────

    /**
     * Variations de ligne pour les 4 directions cardinales :
     * haut (-1), bas (+1), gauche (0), droite (0).
     * Indexé de 0 à 3 en parallèle avec {@link #dCol}.
     */
    private static final int[] dRow = {-1, 1, 0, 0};

    /**
     * Variations de colonne pour les 4 directions cardinales :
     * haut (0), bas (0), gauche (-1), droite (+1).
     * Indexé de 0 à 3 en parallèle avec {@link #dRow}.
     */
    private static final int[] dCol = { 0, 0,-1, 1};

    // ── Méthodes utilitaires ──────────────────────────────────────────────────

    /**
     * Vérifie qu'une position est bien à l'intérieur des limites de la grille.
     *
     * <p>Une position est valide si ses coordonnées {@code (row, col)} sont
     * toutes deux dans l'intervalle {@code [0, rows[} × {@code [0, cols[}.
     * Cette vérification est effectuée avant tout accès au tableau pour
     * éviter un {@code ArrayIndexOutOfBoundsException}.</p>
     *
     * @param grid  grille de jeu (utilisée uniquement pour sa dimension ici)
     * @param rows  nombre de lignes de la grille
     * @param cols  nombre de colonnes de la grille
     * @param point position à vérifier sous forme {@code Pair(row, col)}
     * @return {@code true} si la position est dans les limites,
     *         {@code false} sinon ou si la grille est vide
     */
    public boolean isValid(int[][] grid, int rows, int cols, Pair point) {
        return rows > 0 && cols > 0
            && point.getFirst()  >= 0 && point.getFirst()  < rows
            && point.getSecond() >= 0 && point.getSecond() < cols;
    }

    /**
     * Vérifie qu'une case est traversable (ni mur ni boîte).
     *
     * <p>Retourne {@code true} uniquement si la position est d'abord valide
     * (dans les limites) ET si la valeur dans la grille vaut {@code 1}.
     * Une valeur {@code 0} indique un obstacle infranchissable (mur ou boîte).</p>
     *
     * @param grid  grille encodée : {@code 1} = libre, {@code 0} = bloqué
     * @param rows  nombre de lignes
     * @param cols  nombre de colonnes
     * @param point position à tester sous forme {@code Pair(row, col)}
     * @return {@code true} si la case existe et est traversable
     */
    public boolean isUnBlocked(int[][] grid, int rows, int cols, Pair point) {
        return isValid(grid, rows, cols, point)
            && grid[point.getFirst()][point.getSecond()] == 1;
    }

    /**
     * Vérifie si la position courante est la destination recherchée.
     *
     * <p>Délègue à {@link Pair#equals} pour comparer les coordonnées
     * {@code (row, col)} plutôt que les références objet.</p>
     *
     * @param position position courante de l'explorateur
     * @param dest     destination cible
     * @return {@code true} si les deux positions ont les mêmes coordonnées
     */
    public boolean isDestination(Pair position, Pair dest) {
        return position.equals(dest);
    }

    /**
     * Calcule la distance de Manhattan entre deux cases.
     *
     * <p>La distance de Manhattan est la somme des distances absolues sur
     * chaque axe : {@code |row1 - row2| + |col1 - col2|}. Elle représente
     * le nombre minimal de pas nécessaires pour aller d'une case à l'autre
     * en se déplaçant uniquement horizontalement ou verticalement, sans
     * obstacle. C'est une heuristique <em>admissible</em> pour A* sur ce
     * type de grille : elle ne surestime jamais le coût réel, ce qui
     * garantit que A* trouvera toujours le chemin optimal.</p>
     *
     * @param src  case de départ {@code Pair(row, col)}
     * @param dest case d'arrivée {@code Pair(row, col)}
     * @return distance de Manhattan (nombre entier exprimé en {@code double})
     */
    public double calculateHValue(Pair src, Pair dest) {
        return Math.abs(src.getFirst()  - dest.getFirst())
             + Math.abs(src.getSecond() - dest.getSecond());
    }

    // ── Reconstruction du chemin ──────────────────────────────────────────────

    /**
     * Reconstruit le chemin optimal depuis {@code src} jusqu'à {@code dest}
     * en remontant la chaîne de parents stockée dans {@code cellDetails}.
     *
     * <p><strong>Fonctionnement :</strong> à chaque case, {@code cellDetails[row][col].parent}
     * pointe vers la case depuis laquelle on est arrivé ici avec le meilleur
     * coût. On remonte cette chaîne en empilant chaque case dans un
     * {@code Stack}, puis on retourne la pile dans l'ordre source → dest.</p>
     *
     * <p><strong>Condition d'arrêt :</strong> la source est identifiable parce
     * que son parent pointe sur elle-même (convention fixée dans
     * {@link #aStarSearch}).</p>
     *
     * <p>La source elle-même n'est <em>pas</em> incluse dans le résultat,
     * car le personnage s'y trouve déjà.</p>
     *
     * @param cellDetails tableau des métadonnées A* de chaque case
     * @param src         position de départ {@code Pair(row, col)}
     * @param dest        position d'arrivée {@code Pair(row, col)}
     * @return liste ordonnée des cases à traverser, de la première
     *         case après {@code src} jusqu'à {@code dest} inclus
     */
    private List<Pair> tracePath(Cell[][] cellDetails, Pair src, Pair dest) {
        Stack<Pair> stack = new Stack<>();
        int row = dest.getFirst();
        int col = dest.getSecond();

        // Remontée de dest vers src en suivant les parents
        // Arrêt dès qu'on atteint src (son parent pointe sur lui-même)
        while (!(row == src.getFirst() && col == src.getSecond())) {
            stack.push(new Pair(row, col));           // on empile la case courante
            Pair parent = cellDetails[row][col].parent;
            row = parent.getFirst();                  // on recule vers le parent
            col = parent.getSecond();
        }
        // src n'est pas empilé : le personnage y est déjà

        // On dépile pour obtenir l'ordre src → dest
        List<Pair> path = new ArrayList<>();
        while (!stack.isEmpty()) {
            path.add(stack.pop());
        }
        return path;
    }

    // ── Algorithme principal ──────────────────────────────────────────────────

    /**
     * Recherche le plus court chemin entre {@code src} et {@code dest}
     * sur la grille fournie, en évitant les cases bloquées (valeur {@code 0}).
     *
     * <h4>Étapes de l'algorithme</h4>
     * <ol>
     *   <li><strong>Validation</strong> — vérifie que source et destination
     *       sont dans les limites et que la source n'est pas déjà la
     *       destination.</li>
     *   <li><strong>Initialisation</strong> — crée la {@code closedList}
     *       (cases déjà traitées) et le tableau {@code cellDetails}
     *       (métadonnées de chaque case). Initialise la source avec
     *       {@code f=g=h=0} et {@code parent = src} (marqueur de source).</li>
     *   <li><strong>Boucle principale</strong> — tant que la liste ouverte
     *       n'est pas vide :
     *       <ol type="a">
     *         <li>Dépile la case avec le {@code f} le plus petit.</li>
     *         <li>La marque comme traitée dans {@code closedList}.</li>
     *         <li>Pour chacun de ses 4 voisins :
     *           <ul>
     *             <li>Si c'est la destination → reconstructe et retourne le
     *                 chemin via {@link #tracePath}.</li>
     *             <li>Si la case est libre et non encore traitée → calcule
     *                 {@code gNew}, {@code hNew}, {@code fNew}. Si le nouveau
     *                 {@code f} est meilleur que l'ancien, met à jour les
     *                 métadonnées et ajoute la case à la liste ouverte.</li>
     *           </ul>
     *         </li>
     *       </ol>
     *   </li>
     *   <li><strong>Échec</strong> — si la liste ouverte se vide sans avoir
     *       atteint la destination, retourne {@code null}.</li>
     * </ol>
     *
     * @param grid  grille de jeu {@code [rows][cols]} :
     *              {@code 1} = case traversable, {@code 0} = case bloquée
     * @param rows  nombre de lignes de la grille (axe Y)
     * @param cols  nombre de colonnes de la grille (axe X)
     * @param src   position de départ sous forme {@code Pair(row, col)}
     * @param dest  position d'arrivée sous forme {@code Pair(row, col)}
     * @return liste ordonnée de {@code Pair(row, col)} représentant les cases
     *         à parcourir ({@code src} exclu, {@code dest} inclus) ;
     *         liste vide si {@code src == dest} ;
     *         {@code null} si aucun chemin n'existe ou si les positions
     *         sont invalides
     */
    public List<Pair> aStarSearch(int[][] grid, int rows, int cols, Pair src, Pair dest) {

        // ── 1. Validation des entrées ─────────────────────────────────────────
        if (!isValid(grid, rows, cols, src)) {
            System.out.println("[A*] Source invalide.");
            return null;
        }
        if (!isValid(grid, rows, cols, dest)) {
            System.out.println("[A*] Destination invalide.");
            return null;
        }
        // Cas trivial : déjà sur la destination
        if (isDestination(src, dest)) {
            return new ArrayList<>();
        }

        // ── 2. Initialisation des structures de données ───────────────────────

        // closedList[row][col] = true si la case a déjà été traitée
        // (on ne la traitera plus même si on la rencontre à nouveau)
        boolean[][] closedList = new boolean[rows][cols];

        // cellDetails[row][col] stocke les coûts f/g/h et le parent
        // Alloué paresseusement : null tant que la case n'a pas été rencontrée
        Cell[][] cellDetails = new Cell[rows][cols];

        // Initialisation de la case source
        int si = src.getFirst();
        int sj = src.getSecond();
        cellDetails[si][sj] = new Cell();
        cellDetails[si][sj].f = 0.0;
        cellDetails[si][sj].g = 0.0;
        cellDetails[si][sj].h = 0.0;
        // La source pointe sur elle-même : marqueur d'arrêt pour tracePath()
        cellDetails[si][sj].parent = new Pair(si, sj);

        // Liste ouverte : file de priorité triée par f croissant
        // → toujours la case la plus prometteuse en tête
        PriorityQueue<Details> openList = new PriorityQueue<>(
            (o1, o2) -> Double.compare(o1.getValue(), o2.getValue())
        );
        openList.add(new Details(0.0, si, sj)); // on part de la source

        // ── 3. Boucle principale A* ───────────────────────────────────────────
        while (!openList.isEmpty()) {

            // Dépile la case candidate avec le plus petit f
            Details p = openList.poll();
            int i = p.getI();
            int j = p.getJ();

            // Marquer comme traitée : on a trouvé le chemin optimal vers (i,j)
            closedList[i][j] = true;

            // Exploration des 4 voisins (haut, bas, gauche, droite)
            for (int k = 0; k < 4; k++) {
                int ni = i + dRow[k]; // ligne du voisin
                int nj = j + dCol[k]; // colonne du voisin
                Pair neighbour = new Pair(ni, nj);

                // Ignorer les voisins hors limites
                if (!isValid(grid, rows, cols, neighbour)) continue;

                // Initialisation paresseuse de la cellule voisine
                if (cellDetails[ni][nj] == null) {
                    cellDetails[ni][nj] = new Cell();
                }

                // ── Cas 1 : le voisin est la destination ──────────────────────
                if (isDestination(neighbour, dest)) {
                    // On enregistre son parent avant de reconstruire le chemin
                    cellDetails[ni][nj].parent = new Pair(i, j);
                    System.out.println("[A*] Destination atteinte.");
                    return tracePath(cellDetails, src, dest);
                }

                // ── Cas 2 : voisin libre et pas encore traité ─────────────────
                if (!closedList[ni][nj] && isUnBlocked(grid, rows, cols, neighbour)) {

                    // Calcul des nouveaux coûts pour ce voisin
                    double gNew = cellDetails[i][j].g + 1.0; // +1 car chaque pas coûte 1
                    double hNew = calculateHValue(neighbour, dest);
                    double fNew = gNew + hNew;

                    // On met à jour uniquement si ce chemin est meilleur
                    // (f < 0 signifie "jamais évalué" → toujours mettre à jour)
                    if (cellDetails[ni][nj].f < 0 || cellDetails[ni][nj].f > fNew) {
                        openList.add(new Details(fNew, ni, nj));
                        cellDetails[ni][nj].g      = gNew;
                        cellDetails[ni][nj].f      = fNew;
                        cellDetails[ni][nj].parent = new Pair(i, j);
                    }
                }
                // ── Cas 3 : voisin déjà dans closedList ou bloqué → ignoré ───
            }
        }

        // ── 4. Aucun chemin trouvé ────────────────────────────────────────────
        System.out.println("[A*] Aucun chemin trouvé.");
        return null;
    }
}