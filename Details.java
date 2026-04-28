/**
 * Représente une entrée dans la liste ouverte (open list) de l'algorithme A*.
 *
 * <p>L'algorithme A* maintient une file de priorité ({@code PriorityQueue})
 * contenant les cases candidates à explorer. Chaque entrée associe :</p>
 * <ul>
 *   <li>le coût total estimé {@code f} de la case — utilisé comme clé de
 *       tri pour que la file retourne toujours la case la plus prometteuse
 *       en premier ;</li>
 *   <li>les coordonnées {@code (i, j)} de la case dans la grille, où
 *       {@code i} est la ligne (axe Y) et {@code j} la colonne (axe X).</li>
 * </ul>
 *
 * <p>Cette classe joue le rôle d'un tuple immuable {@code (f, i, j)}.
 * Elle est comparable via un lambda passé à la {@code PriorityQueue} de
 * {@link AAsterix} : {@code (o1, o2) -> Double.compare(o1.getValue(), o2.getValue())}.</p>
 *
 * <h2>Pourquoi une classe séparée et pas directement {@link Pair} ?</h2>
 * <p>{@code Pair} ne stocke que deux entiers {@code (row, col)}.
 * {@code Details} ajoute le coût {@code f} (un {@code double}) nécessaire
 * pour ordonner la file de priorité — on ne peut pas trier des cases par
 * coordonnées, il faut trier par coût estimé.</p>
 */
public class Details {

    // ── Champs ────────────────────────────────────────────────────────────────

    /**
     * Coût total estimé de la case : {@code f = g + h}.
     *
     * <p>C'est la valeur de tri de la {@code PriorityQueue} : la case avec
     * le {@code f} le plus petit est dépilée en premier, ce qui garantit
     * que A* explore toujours le chemin le plus prometteur.</p>
     */
    private final double value;

    /**
     * Ligne de la case dans la grille (axe Y, vertical).
     * Correspond à {@code Pair.first} dans le reste du code.
     */
    private final int i;

    /**
     * Colonne de la case dans la grille (axe X, horizontal).
     * Correspond à {@code Pair.second} dans le reste du code.
     */
    private final int j;

    // ── Constructeur ──────────────────────────────────────────────────────────

    /**
     * Construit une entrée de la liste ouverte.
     *
     * @param value coût total estimé {@code f} de la case
     * @param i     ligne de la case (axe Y, ≥ 0)
     * @param j     colonne de la case (axe X, ≥ 0)
     */
    public Details(double value, int i, int j) {
        this.value = value;
        this.i     = i;
        this.j     = j;
    }

    // ── Accesseurs ────────────────────────────────────────────────────────────

    /**
     * Retourne le coût total estimé {@code f} de la case.
     *
     * <p>Utilisé par le comparateur de la {@code PriorityQueue} dans
     * {@link AAsterix#aStarSearch} pour trier les candidats : la case avec
     * le {@code f} le plus petit est traitée en priorité.</p>
     *
     * @return valeur de {@code f}
     */
    public double getValue() {
        return this.value;
    }

    /**
     * Retourne la ligne de la case (axe Y).
     *
     * @return indice de ligne {@code i}, ≥ 0
     */
    public int getI() {
        return this.i;
    }

    /**
     * Retourne la colonne de la case (axe X).
     *
     * @return indice de colonne {@code j}, ≥ 0
     */
    public int getJ() {
        return this.j;
    }
}