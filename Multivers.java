import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Contient l'ensemble des mondes du Sokoban récursif et l'état de navigation du joueur.
 *
 * Le joueur se trouve toujours dans un monde courant à une position donnée.
 * Quand il entre dans une boîte-monde, le contexte courant est empilé et on
 * bascule dans le monde référencé. Quand il sort, on dépile.
 *
 * Usage typique :
 * <pre>
 *   Map&lt;Character, ArrayList&lt;ArrayList&lt;Case&gt;&gt;&gt; grilles =
 *       PlateauTexteFichier.chargerTousLesMondes(chemin);
 *   Multivers mv = Multivers.depuisGrilles(grilles);
 * </pre>
 */
public class Multivers {

    /**
     * Représente un contexte de navigation sauvegardé lors de l'entrée dans une boîte-monde.
     * Permet de retrouver le monde parent et la position d'où l'on est entré.
     */
    public static final class ContexteNavigation {
        /** Identifiant du monde parent. */
        public final char mondePrecedent;
        /** Position du joueur dans le monde parent avant l'entrée. */
        public final Position positionPrecedente;

        public ContexteNavigation(char mondePrecedent, Position positionPrecedente) {
            this.mondePrecedent = mondePrecedent;
            this.positionPrecedente = positionPrecedente;
        }
    }

    /** Tous les mondes chargés, indexés par leur lettre. */
    private final Map<Character, Plateau> mondes;

    /** Lettre du monde dans lequel le joueur se trouve actuellement. */
    private char mondeCourant;

    /** Pile de contextes pour pouvoir revenir au monde parent (entrée dans une boîte). */
    private final Deque<ContexteNavigation> pileContextes;

    /**
     * Construit un Multivers à partir d'une map lettre→plateau déjà instanciés.
     * Le premier monde de la map est considéré comme le monde de départ.
     *
     * @param mondes map des plateaux, dans l'ordre d'insertion
     * @throws IllegalArgumentException si la map est vide
     */
    public Multivers(Map<Character, Plateau> mondes) {
        if (mondes == null || mondes.isEmpty()) {
            throw new IllegalArgumentException("Un Multivers doit contenir au moins un monde.");
        }
        this.mondes = new LinkedHashMap<>(mondes);
        this.mondeCourant = mondes.keySet().iterator().next();
        this.pileContextes = new ArrayDeque<>();
    }

    /**
     * Construit un Multivers directement à partir des grilles brutes.
     * Chaque grille est convertie en {@link Plateau}.
     * Le premier monde trouvé est celui de départ.
     *
     * @param grilles map lettre→grille de cases
     * @return nouveau Multivers initialisé
     */
    public static Multivers depuisGrilles(Map<Character, ArrayList<ArrayList<Case>>> grilles) {
        Map<Character, Plateau> plateaux = new LinkedHashMap<>();
        for (Map.Entry<Character, ArrayList<ArrayList<Case>>> entree : grilles.entrySet()) {
            plateaux.put(entree.getKey(), new Plateau(entree.getValue()));
        }
        return new Multivers(plateaux);
    }

    // ──────────────────────────────────────────────
    // Accesseurs sur le monde courant
    // ──────────────────────────────────────────────

    /**
     * Retourne l'identifiant du monde courant.
     *
     * @return lettre du monde courant
     */
    public char getMondeCourant() {
        return mondeCourant;
    }

    /**
     * Retourne le plateau du monde courant.
     *
     * @return plateau actif
     */
    public Plateau getPlateauCourant() {
        return mondes.get(mondeCourant);
    }

    /**
     * Retourne un plateau par son identifiant.
     *
     * @param lettre identifiant du monde
     * @return plateau correspondant, ou null si inexistant
     */
    public Plateau getPlateau(char lettre) {
        return mondes.get(Character.toUpperCase(lettre));
    }

    /**
     * Vérifie si un monde existe dans le Multivers.
     *
     * @param lettre identifiant du monde
     * @return true si le monde existe
     */
    public boolean existeMonde(char lettre) {
        return mondes.containsKey(Character.toUpperCase(lettre));
    }

    /**
     * Retourne tous les mondes.
     *
     * @return map non modifiable lettre→plateau
     */
    public Map<Character, Plateau> getTousLesMondes() {
        return java.util.Collections.unmodifiableMap(mondes);
    }

    // ──────────────────────────────────────────────
    // Navigation entre mondes
    // ──────────────────────────────────────────────

    /**
     * Entre dans une boîte-monde : empile le contexte courant et bascule vers le monde cible.
     *
     * @param identifiantCible lettre du monde dans lequel entrer
     * @param positionDansMondeCourant position du joueur avant l'entrée
     * @throws IllegalArgumentException si le monde cible n'existe pas
     */
    public void entrerDans(char identifiantCible, Position positionDansMondeCourant) {
        char cible = Character.toUpperCase(identifiantCible);
        if (!existeMonde(cible)) {
            throw new IllegalArgumentException("Le monde '" + cible + "' n'existe pas.");
        }
        pileContextes.push(new ContexteNavigation(mondeCourant, positionDansMondeCourant));
        mondeCourant = cible;
    }

    /**
     * Sort du monde courant : dépile le contexte et revient dans le monde parent.
     *
     * @return le contexte restauré (monde parent + position d'entrée)
     * @throws IllegalStateException si on est déjà dans le monde racine
     */
    public ContexteNavigation sortir() {
        if (pileContextes.isEmpty()) {
            throw new IllegalStateException("Impossible de sortir : déjà dans le monde racine.");
        }
        ContexteNavigation contexte = pileContextes.pop();
        mondeCourant = contexte.mondePrecedent;
        return contexte;
    }

    /**
     * Indique si le joueur peut sortir (n'est pas dans le monde racine).
     *
     * @return true si une sortie est possible
     */
    public boolean peutSortir() {
        return !pileContextes.isEmpty();
    }

    /**
     * Profondeur actuelle de navigation (0 = monde racine).
     *
     * @return profondeur
     */
    public int getProfondeur() {
        return pileContextes.size();
    }

    /**
     * Réinitialise la navigation au monde racine (vide la pile de contextes).
     * Utile pour le Ctrl+Z complet ou le retour au menu.
     */
    public void reinitialiserNavigation() {
        if (!pileContextes.isEmpty()) {
            ContexteNavigation racine = null;
            while (!pileContextes.isEmpty()) {
                racine = pileContextes.pop();
            }
            mondeCourant = racine.mondePrecedent;
        }
    }

    @Override
    public String toString() {
        return "Multivers{mondeCourant='" + mondeCourant
            + "', profondeur=" + getProfondeur()
            + ", mondes=" + mondes.keySet() + "}";
    }
}
