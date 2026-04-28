/**
 * Représente une paire de coordonnées entières {@code (first, second)}.
 *
 * <p>Dans le contexte de l'algorithme A*, {@code first} désigne la ligne
 * (axe Y, vertical) et {@code second} désigne la colonne (axe X, horizontal),
 * soit la convention {@code Pair(row, col)}.</p>
 *
 * <p>Cette classe est utilisée pour :</p>
 * <ul>
 *   <li>Stocker la position d'une case dans la grille de recherche.</li>
 *   <li>Stocker le parent d'une cellule dans {@link Cell}, permettant
 *       de remonter le chemin trouvé par A*.</li>
 *   <li>Représenter la source et la destination passées à
 *       {@link AAsterix#aStarSearch}.</li>
 * </ul>
 */
public class Pair {

    // ── Champs ────────────────────────────────────────────────────────────────

    /**
     * Première coordonnée : ligne (row) dans la grille,
     * c'est-à-dire la position sur l'axe Y (vertical).
     * Doit être ≥ 0.
     */
    int first;

    /**
     * Deuxième coordonnée : colonne (col) dans la grille,
     * c'est-à-dire la position sur l'axe X (horizontal).
     * Doit être ≥ 0.
     */
    int second;

    // ── Constructeur ──────────────────────────────────────────────────────────

    /**
     * Construit une paire de coordonnées.
     *
     * @param first  ligne (row), axe Y — doit être ≥ 0 dans le contexte du jeu
     * @param second colonne (col), axe X — doit être ≥ 0 dans le contexte du jeu
     */
    public Pair(int first, int second) {
        this.first  = first;
        this.second = second;
    }

    // ── Accesseurs ────────────────────────────────────────────────────────────

    /**
     * Retourne la première coordonnée (ligne / axe Y).
     *
     * @return valeur de {@code first}
     */
    public int getFirst() {
        return this.first;
    }

    /**
     * Retourne la deuxième coordonnée (colonne / axe X).
     *
     * @return valeur de {@code second}
     */
    public int getSecond() {
        return this.second;
    }

    // ── Égalité & hachage ─────────────────────────────────────────────────────

    /**
     * Vérifie si deux paires représentent la même position.
     *
     * <p>Deux {@code Pair} sont égales si et seulement si leurs champs
     * {@code first} et {@code second} sont identiques.
     * Cette méthode est essentielle pour l'algorithme A* : elle permet de
     * tester {@code isDestination(position, dest)} en comparant les
     * coordonnées plutôt que les références objet.</p>
     *
     * @param obj l'objet à comparer
     * @return {@code true} si {@code obj} est un {@code Pair} avec les
     *         mêmes coordonnées, {@code false} sinon
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Pair
            && this.first  == ((Pair) obj).first
            && this.second == ((Pair) obj).second;
    }

    /**
     * Retourne un code de hachage cohérent avec {@link #equals}.
     *
     * <p>Utilise la formule classique {@code 31 * first + second} pour
     * distribuer uniformément les paires dans les structures de hachage
     * (ex. {@code HashMap}, {@code HashSet}).</p>
     *
     * @return code de hachage de cette paire
     */
    @Override
    public int hashCode() {
        return 31 * first + second;
    }

    /**
     * Retourne une représentation textuelle de la paire.
     *
     * @return chaîne au format {@code "(first, second)"}
     */
    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}