/**
 * Représente les métadonnées A* associées à une case de la grille.
 *
 * <p>Pendant l'exécution de l'algorithme A*, chaque case explorée se voit
 * attribuer une instance de {@code Cell} stockée dans le tableau
 * {@code cellDetails[row][col]} de {@link AAsterix}. Cette instance
 * contient :</p>
 * <ul>
 *   <li>Les trois coûts du chemin ({@code f}, {@code g}, {@code h}).</li>
 *   <li>La case parente ({@link #parent}), qui permet de remonter le
 *       chemin optimal depuis la destination jusqu'à la source une fois
 *       l'arrivée atteinte.</li>
 * </ul>
 *
 * <h2>Principe des coûts A*</h2>
 * <pre>
 *   f = g + h
 *
 *   g : coût réel accumulé depuis la source jusqu'à cette case.
 *       Dans notre grille, chaque pas vaut 1, donc g est simplement
 *       le nombre de cases parcourues depuis le départ.
 *
 *   h : heuristique — estimation optimiste du coût restant pour
 *       atteindre la destination. On utilise la distance de Manhattan
 *       |x1 - x2| + |y1 - y2|, qui est admissible car elle ne surestime
 *       jamais le coût réel sur une grille à 4 directions.
 *
 *   f : coût total estimé du chemin passant par cette case.
 *       L'algorithme explore toujours la case dont f est le plus petit
 *       (via la {@code PriorityQueue} de {@link AAsterix}).
 * </pre>
 */
public class Cell {

    // ── Champs ────────────────────────────────────────────────────────────────

    /**
     * Coordonnées de la case parente dans la grille, sous forme de
     * {@code Pair(row, col)}.
     *
     * <p>La case parente est celle depuis laquelle on est arrivé sur cette
     * case avec le meilleur coût connu. Une fois la destination atteinte,
     * on remonte la chaîne des parents de {@code dest} jusqu'à {@code src}
     * pour reconstruire le chemin optimal.</p>
     *
     * <p>Convention spéciale : si la case est la source, {@code parent}
     * pointe sur elle-même — c'est le marqueur d'arrêt utilisé dans
     * {@link AAsterix#tracePath} pour savoir qu'on a remonté jusqu'au bout.</p>
     *
     * <p>Initialisé à {@code Pair(-1, -1)} par le constructeur par défaut
     * pour indiquer qu'aucun parent n'a encore été assigné.</p>
     */
    public Pair parent;

    /**
     * Coût total estimé du chemin passant par cette case : {@code f = g + h}.
     *
     * <p>Initialisé à {@code -1} pour signifier "non encore évalué",
     * ce qui permet à {@link AAsterix} de détecter si la cellule a déjà
     * reçu un coût ou non ({@code f < 0} ⟹ jamais mis à jour).</p>
     */
    public double f;

    /**
     * Coût réel du chemin depuis la source jusqu'à cette case.
     *
     * <p>Chaque déplacement d'une case coûte {@code 1.0}. Ainsi,
     * {@code g} correspond exactement au nombre de pas effectués
     * depuis le départ pour atteindre cette case par le meilleur
     * chemin connu.</p>
     *
     * <p>Initialisé à {@code -1} (non évalué).</p>
     */
    public double g;

    /**
     * Valeur heuristique : estimation du coût restant jusqu'à la destination.
     *
     * <p>Calculée via la distance de Manhattan :
     * {@code h = |row_dest - row| + |col_dest - col|}.
     * Cette heuristique est dite "admissible" car elle ne surestime jamais
     * le coût réel, garantissant que A* trouve toujours le chemin optimal.</p>
     *
     * <p>Initialisée à {@code -1} (non évaluée).</p>
     */
    public double h;

    // ── Constructeurs ─────────────────────────────────────────────────────────

    /**
     * Construit une cellule non initialisée.
     *
     * <p>Tous les coûts sont mis à {@code -1} (sentinelle "non évalué")
     * et le parent est positionné à {@code (-1, -1)} (sentinelle "sans parent").
     * C'est l'état par défaut d'une case non encore explorée par A*.</p>
     */
    public Cell() {
        this.parent = new Pair(-1, -1);
        this.f = -1;
        this.g = -1;
        this.h = -1;
    }

    /**
     * Construit une cellule entièrement initialisée.
     *
     * <p>Utilisé lorsqu'on veut créer directement une cellule avec un parent
     * et des coûts connus, par exemple pour la case source.</p>
     *
     * @param parent coordonnées de la case parente (ou de la case elle-même
     *               pour la source)
     * @param f      coût total estimé ({@code f = g + h})
     * @param g      coût réel depuis la source
     * @param h      valeur heuristique vers la destination
     */
    public Cell(Pair parent, double f, double g, double h) {
        this.parent = parent;
        this.f = f;
        this.g = g;
        this.h = h;
    }
}