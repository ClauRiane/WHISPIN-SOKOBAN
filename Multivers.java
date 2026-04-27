import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
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
     * Permet de retrouver le monde parent et la position où réapparaître lors de la sortie.
     */
    public static final class ContexteNavigation {
        /** Identifiant du monde parent. */
        public final char mondePrecedent;
        /** Position de la boîte-monde dans le monde parent (pour mise en surbrillance). */
        public final Position positionBoite;
        /** Position dans le monde parent où le joueur réapparaître en sortant (case après la boîte). */
        public final Position positionSortie;

        public ContexteNavigation(char mondePrecedent, Position positionBoite, Position positionSortie) {
            this.mondePrecedent = mondePrecedent;
            this.positionBoite = positionBoite;
            this.positionSortie = positionSortie;
        }
    }

    /**
     * Résultat d'une tentative de déplacement dans le multivers.
     */
    public enum ResultatDeplacement {
        /** Le personnage s'est déplacé normalement (ou a poussé une boîte). */
        DEPLACE,
        /** Le personnage est entré dans une boîte-monde. */
        ENTRE,
        /** Le personnage est sorti d'un monde enfant. */
        SORTI,
        /** Le déplacement a été bloqué. */
        BLOQUE
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
     * @param positionSortie   position dans le monde parent où réapparaître en sortant
     * @throws IllegalArgumentException si le monde cible n'existe pas
     */
    public void entrerDans(char identifiantCible, Position positionBoite, Position positionSortie) {
        char cible = Character.toUpperCase(identifiantCible);
        if (!existeMonde(cible)) {
            throw new IllegalArgumentException("Le monde '" + cible + "' n'existe pas.");
        }
        pileContextes.push(new ContexteNavigation(mondeCourant, positionBoite, positionSortie));
        mondeCourant = cible;
    }

    /**
     * Sort du monde courant : dépile le contexte et revient dans le monde parent.
     *
     * @return le contexte restauré (monde parent + positionSortie)
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

    // ──────────────────────────────────────────────
    // Déplacement multi-monde
    // ──────────────────────────────────────────────

    /**
     * Tente un déplacement dans la direction donnée, en gérant l'entrée et la sortie des mondes.
     *
     * <ul>
     *   <li>Si la position cible est hors-limites → tente une sortie.</li>
     *   <li>Si la case cible est une {@link CaseBoiteMonde} non poußable → tente une entrée.</li>
     *   <li>Sinon → déplacement classique sur le plateau courant.</li>
     * </ul>
     *
     * @param direction direction du déplacement
     * @return résultat du déplacement
     */
    public ResultatDeplacement deplacer(Direction direction) {
        Plateau plateau = getPlateauCourant();
        Position posActuelle = plateau.getPositionPersonnage();
        Position prochainePos = posActuelle.deplacer(direction);

        // Hors-limites → sortir
        if (!plateau.estDansLimites(prochainePos)) {
            return tenterSortie();
        }

        Case prochaineCase = plateau.getCase(prochainePos);

        // Mur de bordure → sortir (les mondes-boîtes ont des murs sur tout le pourtour)
        if (prochaineCase instanceof CaseMur && plateau.estSurBordure(prochainePos)) {
            return tenterSortie();
        }

        // Boîte-monde non poussable → entrer
        if (prochaineCase instanceof CaseBoiteMonde bm && !plateau.peutSeDeplacer(direction)) {
            return tenterEntree(bm, prochainePos, direction);
        }

        // Boîte simple poussée vers l'extérieur d'un monde enfant → sortir avec la boîte
        if (prochaineCase instanceof CaseBoite
            && !(prochaineCase instanceof CaseBoiteMonde)
            && !plateau.peutSeDeplacer(direction)
            && pousseVersExterieur(plateau, prochainePos, direction)) {
            return tenterSortieAvecBoite(prochainePos, direction);
        }

        // Déplacement classique
        boolean deplace = plateau.deplacer(direction);
        return deplace ? ResultatDeplacement.DEPLACE : ResultatDeplacement.BLOQUE;
    }

    /**
     * Indique si la boîte poussée est bien orientée vers l'extérieur du monde courant.
     */
    private boolean pousseVersExterieur(Plateau plateau, Position posBoite, Direction direction) {
        return switch (direction) {
            case GAUCHE -> posBoite.getx() == 1;
            case DROITE -> posBoite.getx() == plateau.getLargeur() - 2;
            case HAUT -> posBoite.gety() == 1;
            case BAS -> posBoite.gety() == plateau.getHauteur() - 2;
        };
    }

    /**
     * Tente d'entrer dans une boîte-monde.
     * Calcule la position de sortie (case après la boîte dans le monde parent),
     * bascule dans le monde enfant et téléporte le joueur à la première case libre.
     */
    private ResultatDeplacement tenterEntree(CaseBoiteMonde boite, Position posBoite, Direction direction) {
        char identifiant = boite.getIdentifiantMonde();
        if (!existeMonde(identifiant)) {
            return ResultatDeplacement.BLOQUE;
        }
        // Où réapparaître dans le monde parent quand on en sort
        Position positionSortie = posBoite.deplacer(direction);
        entrerDans(identifiant, posBoite, positionSortie);

        // Téléporter le joueur dans le nouveau monde
        Plateau nouveauPlateau = getPlateauCourant();
        Position entree = nouveauPlateau.positionEntreeDepuis(direction);
        nouveauPlateau.teleporterPersonnage(entree);
        return ResultatDeplacement.ENTRE;
    }

    /**
     * Tente de sortir du monde courant.
     * Si la pile est vide (monde racine), le joueur est bloqué.
     * Sinon, revient dans le monde parent et téléporte le joueur à {@code positionSortie}.
     */
    private ResultatDeplacement tenterSortie() {
        if (!peutSortir()) {
            return ResultatDeplacement.BLOQUE;
        }
        ContexteNavigation contexte = sortir();
        Plateau plateauParent = getPlateauCourant();
        // Vérifier que la position de sortie est traversable, sinon chercher une case libre proche
        Position sortie = contexte.positionSortie;
        if (!plateauParent.estDansLimites(sortie) || plateauParent.getCase(sortie) instanceof CaseMur) {
            sortie = plateauParent.positionEntreeDepuis(Direction.DROITE); // fallback : 1ère case libre
        }
        plateauParent.teleporterPersonnage(sortie);
        return ResultatDeplacement.SORTI;
    }

    /**
     * Sort du monde courant en exportant une boîte simple
     * vers l'extérieur de la boîte-monde dans le parent.
     */
    private ResultatDeplacement tenterSortieAvecBoite(Position posBoite, Direction direction) {
        if (!peutSortir()) {
            return ResultatDeplacement.BLOQUE;
        }

        Plateau plateauEnfant = getPlateauCourant();
        Case caseBoite = plateauEnfant.getCase(posBoite);
        if (!(caseBoite instanceof CaseBoite boite) || caseBoite instanceof CaseBoiteMonde) {
            return ResultatDeplacement.BLOQUE;
        }

        ContexteNavigation contexte = pileContextes.peek();
        if (contexte == null) {
            return ResultatDeplacement.BLOQUE;
        }
        Plateau plateauParentAvantSortie = getPlateau(contexte.mondePrecedent);
        Position exportSouhaite = calculerPositionExportSouhaitee(contexte, plateauEnfant, posBoite, direction);
        Position positionExport = trouverPositionExportDisponible(
            plateauParentAvantSortie,
            contexte.positionBoite,
            exportSouhaite,
            direction
        );
        if (positionExport == null) {
            return ResultatDeplacement.BLOQUE;
        }

        // Retirer la boîte du monde enfant
        plateauEnfant.setCase(posBoite, boite.estSurCible() ? CaseCible.getInstance() : CaseVide.getInstance());

        // Revenir dans le parent et y ajouter la boîte exportée (sans supprimer la boîte-monde)
        ContexteNavigation contexteSortie = sortir();
        Plateau plateauParent = getPlateauCourant();
        Case caseExport = plateauParent.getCase(positionExport);
        plateauParent.setCase(positionExport, new CaseBoite(caseExport.estCible()));

        // Téléporter le joueur comme pour une sortie standard
        Position sortie = trouverSortieJoueurValide(plateauParent, contexteSortie.positionSortie, positionExport);
        if (sortie == null) {
            return ResultatDeplacement.BLOQUE;
        }
        plateauParent.teleporterPersonnage(sortie);
        return ResultatDeplacement.SORTI;
    }

    /**
     * Calcule la position d'export désirée autour de la boîte-monde parent,
     * en tenant compte du côté poussé et du décalage dans le monde enfant.
     */
    private Position calculerPositionExportSouhaitee(
        ContexteNavigation contexte,
        Plateau plateauEnfant,
        Position posBoiteEnfant,
        Direction direction
    ) {
        int cx = plateauEnfant.getLargeur() / 2;
        int cy = plateauEnfant.getHauteur() / 2;
        int dx = posBoiteEnfant.getx() - cx;
        int dy = posBoiteEnfant.gety() - cy;
        int xPortail = contexte.positionBoite.getx();
        int yPortail = contexte.positionBoite.gety();

        return switch (direction) {
            case GAUCHE -> new Position(xPortail - 1, yPortail + dy);
            case DROITE -> new Position(xPortail + 1, yPortail + dy);
            case HAUT -> new Position(xPortail + dx, yPortail - 1);
            case BAS -> new Position(xPortail + dx, yPortail + 1);
        };
    }

    /**
     * Cherche une case disponible pour la boîte exportée sur le côté correspondant du portail.
     */
    private Position trouverPositionExportDisponible(
        Plateau plateauParent,
        Position posPortail,
        Position souhaitee,
        Direction direction
    ) {
        int limite = Math.max(plateauParent.getLargeur(), plateauParent.getHauteur());
        for (int d = 0; d < limite; d++) {
            int[] essais = (d == 0) ? new int[]{0} : new int[]{d, -d};
            for (int decalage : essais) {
                Position candidate;
                switch (direction) {
                    case GAUCHE:
                    case DROITE:
                        candidate = new Position(souhaitee.getx(), posPortail.gety() + decalage);
                        break;
                    case HAUT:
                    case BAS:
                        candidate = new Position(posPortail.getx() + decalage, souhaitee.gety());
                        break;
                    default:
                        candidate = souhaitee;
                        break;
                }
                if (!plateauParent.estDansLimites(candidate)) {
                    continue;
                }
                if (candidate.equals(posPortail)) {
                    continue;
                }
                if (plateauParent.getCase(candidate).estTraversable()) {
                    return candidate;
                }
            }
        }
        return null;
    }

    /**
     * Trouve une position de téléportation valide pour le joueur dans le parent.
     */
    private Position trouverSortieJoueurValide(Plateau plateauParent, Position sortiePreferree, Position caseOccupee) {
        if (plateauParent.estDansLimites(sortiePreferree)
            && !sortiePreferree.equals(caseOccupee)
            && plateauParent.getCase(sortiePreferree).estTraversable()) {
            return sortiePreferree;
        }

        for (int y = 0; y < plateauParent.getHauteur(); y++) {
            for (int x = 0; x < plateauParent.getLargeur(); x++) {
                Position p = new Position(x, y);
                if (p.equals(caseOccupee)) {
                    continue;
                }
                if (plateauParent.getCase(p).estTraversable()) {
                    return p;
                }
            }
        }
        return null;
    }

    /**
     * Retourne la pile de navigation dans l'ordre racine → parent immédiat.
     * Chaque élément indique le monde parent et la boîte dans laquelle on est entré.
     */
    public List<ContexteNavigation> getContextesOrdonnes() {
        List<ContexteNavigation> liste = new ArrayList<>(pileContextes);
        java.util.Collections.reverse(liste);
        return liste;
    }

    /**
     * Indique si tous les mondes sont gagnés (toutes les boîtes de chaque plateau sur une cible).
     * C'est la condition de victoire globale du Sokoban récursif.
     */
    public boolean estGagne() {
        for (Plateau p : mondes.values()) {
            if (!p.estGagne()) return false;
        }
        return true;
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
