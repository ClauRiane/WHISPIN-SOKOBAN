import java.util.ArrayList;

/**
 * Représente le plateau de jeu Sokoban.
 * Gère la grille, la position du personnage et la logique de déplacement.
 */
public class Plateau {
    
    /** grille du plateau (liste de lignes) */
    private ArrayList<ArrayList<Case>> grille;
    
    /** position actuelle du personnage */
    private Position positionPersonnage;
    
    /** nombre de lignes du plateau */
    private int hauteur;
    
    /** nombre de colonnes du plateau */
    private int largeur;
    
    /** historique des mouvements pour le Ctrl+Z */
    private ArrayList<Mouvement> historique;
    
    /**
     * Construit un plateau vide.
     */
    public Plateau() {
        this.grille = new ArrayList<>();
        this.historique = new ArrayList<>();
        this.positionPersonnage = null;
        this.hauteur = 0;
        this.largeur = 0;
    }
    
    /**
     * Construit un plateau à partir d'une grille existante.
     *
     * @param grille grille de cases
     * @throws IllegalArgumentException si la grille est invalide
     */
    public Plateau(ArrayList<ArrayList<Case>> grille) {
        if (grille == null || grille.isEmpty()) {
            throw new IllegalArgumentException("La grille ne peut pas être vide");
        }
        
        this.grille = grille;
        this.hauteur = grille.size();
        this.largeur = grille.get(0).size();
        this.historique = new ArrayList<>();
        this.positionPersonnage = trouverPositionPersonnage();
        
        if (this.positionPersonnage == null) {
            throw new IllegalArgumentException("Le plateau doit contenir un personnage");
        }
    }
    
    /**
     * Trouve la position du personnage dans la grille.
     *
     * @return position du personnage, ou null si non trouvée
     */
    private Position trouverPositionPersonnage() {
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                Case caseActuelle = grille.get(y).get(x);
                if (caseActuelle.estPersonnageCible()) {
                    return new Position(x, y);
                }
            }
        }
        return null;
    }
    
    /**
     * Retourne la case à la position donnée.
     *
     * @param pos position
     * @return case à cette position
     * @throws IndexOutOfBoundsException si la position est hors limites
     */
    public Case getCase(Position pos) {
        return getCase(pos.getx(), pos.gety());
    }
    
    /**
     * Retourne la case aux coordonnées données.
     *
     * @param x coordonnée x
     * @param y coordonnée y
     * @return case à cette position
     * @throws IndexOutOfBoundsException si les coordonnées sont hors limites
     */
    public Case getCase(int x, int y) {
        if (!estDansLimites(x, y)) {
            throw new IndexOutOfBoundsException("Position hors limites: (" + x + ", " + y + ")");
        }
        return grille.get(y).get(x);
    }
    
    /**
     * Modifie la case à la position donnée.
     *
     * @param pos position
     * @param nouvelleCase nouvelle case
     */
    public void setCase(Position pos, Case nouvelleCase) {
        setCase(pos.getx(), pos.gety(), nouvelleCase);
    }
    
    /**
     * Modifie la case aux coordonnées données.
     *
     * @param x coordonnée x
     * @param y coordonnée y
     * @param nouvelleCase nouvelle case
     */
    public void setCase(int x, int y, Case nouvelleCase) {
        if (!estDansLimites(x, y)) {
            throw new IndexOutOfBoundsException("Position hors limites: (" + x + ", " + y + ")");
        }
        grille.get(y).set(x, nouvelleCase);
    }
    
    /**
     * Vérifie si des coordonnées sont dans les limites du plateau.
     *
     * @param x coordonnée x
     * @param y coordonnée y
     * @return true si les coordonnées sont valides, false sinon
     */
    public boolean estDansLimites(int x, int y) {
        return x >= 0 && x < largeur && y >= 0 && y < hauteur;
    }
    
    /**
     * Vérifie si une position est dans les limites du plateau.
     *
     * @param pos position à vérifier
     * @return true si la position est valide, false sinon
     */
    public boolean estDansLimites(Position pos) {
        return estDansLimites(pos.getx(), pos.gety());
    }
    
    /**
     * Retourne la position actuelle du personnage.
     *
     * @return position du personnage
     */
    public Position getPositionPersonnage() {
        return positionPersonnage;
    }
    
    /**
     * Retourne la grille complète.
     *
     * @return grille
     */
    public ArrayList<ArrayList<Case>> getGrille() {
        return grille;
    }
    
    /**
     * Retourne la hauteur du plateau.
     *
     * @return nombre de lignes
     */
    public int getHauteur() {
        return hauteur;
    }
    
    /**
     * Retourne la largeur du plateau.
     *
     * @return nombre de colonnes
     */
    public int getLargeur() {
        return largeur;
    }
    
    /**
     * Vérifie si un déplacement dans une direction est possible.
     *
     * @param direction direction du déplacement
     * @return true si le déplacement est possible, false sinon
     */
    public boolean peutSeDeplacer(Direction direction) {
        Position prochainePos = positionPersonnage.deplacer(direction);
        
        // vérifie si la prochaine position est dans les limites
        if (!estDansLimites(prochainePos)) {
            return false;
        }
        
        Case prochaineCase = getCase(prochainePos);
        
        // Si la case est traversable (vide ou cible), on peut y aller
        if (prochaineCase.estTraversable()) {
            return true;
        }
        
        // si c'est une boite, vérifier si elle peut être poussée
        if (prochaineCase.estBoite()) {
            return peutPousserBoite(prochainePos, direction);
        }
        
        // Sinon (mur) peut pas
        return false;
    }

    /**
     * Indique si le prochain déplacement poussera une boîte.
     * Cette vérification relève des règles du plateau.
     *
     * @param direction direction de déplacement envisagée
     * @return true si le personnage poussera une boîte, false sinon
     */
    public boolean vaPousserBoite(Direction direction) {
        if (!peutSeDeplacer(direction)) {
            return false;
        }

        Position prochainePos = positionPersonnage.deplacer(direction);
        if (!estDansLimites(prochainePos)) {
            return false;
        }

        return getCase(prochainePos).estBoite();
    }
    
    /**
     * Vérifie si une boîte peut être poussée dans une direction.
     *
     * @param posBoite position de la boîte
     * @param direction direction de la poussée
     * @return true si la boîte peut être poussée, false sinon
     */
    private boolean peutPousserBoite(Position posBoite, Direction direction) {
        Position posApresBoite = posBoite.deplacer(direction);
        
        // vérifie si la position après la boite est dans les limites
        if (!estDansLimites(posApresBoite)) {
            return false;
        }
        
        Case caseApresBoite = getCase(posApresBoite);
        
        // la boite peut être poussée si la case suivante est traversable
        return caseApresBoite.estTraversable();
    }
    
    /**
     * Effectue un déplacement dans une direction.
     * Gère le déplacement du personnage et la poussée de boîtes si nécessaire.
     *
     * @param direction direction du déplacement
     * @return true si le déplacement a été effectué, false sinon
     */
    public boolean deplacer(Direction direction) {
        if (!peutSeDeplacer(direction)) {
            return false;
        }
        
        Position prochainePos = positionPersonnage.deplacer(direction);
        Case prochaineCase = getCase(prochainePos);
        Case caseActuelle = getCase(positionPersonnage);
        
        boolean personnageEtaitSurCible = caseActuelle instanceof Personnage && 
                                          ((Personnage) caseActuelle).estSurCible();
        
        // créer le mouvement pour l'historique
        Mouvement mouvement;
        
        // cas 1 : Pousser une boite
        if (prochaineCase.estBoite()) {
            Position posApresBoite = prochainePos.deplacer(direction);
            Case caseApresBoite = getCase(posApresBoite);
            
            CaseBoite boite = (CaseBoite) prochaineCase;
            boolean boiteEtaitSurCible = boite.estSurCible();
            
            // Déplacer la boite
            boolean boiteSurNouvelleCible = caseApresBoite.estCible();
            CaseBoite nouvelleBoite = new CaseBoite(boiteSurNouvelleCible);
            setCase(posApresBoite, nouvelleBoite);
            
            // Le personnage prend la place de la boite
            boolean personnageSurCible = boiteEtaitSurCible;
            Personnage nouveauPersonnage = new Personnage(personnageSurCible);
            setCase(prochainePos, nouveauPersonnage);
            
            // Restaurer la case d'origine (vide ou cible)
            if (personnageEtaitSurCible) {
                setCase(positionPersonnage, CaseCible.getInstance());
            } else {
                setCase(positionPersonnage, CaseVide.getInstance());
            }
            
            // Créer le mouvement avec poussée de boite
            mouvement = new Mouvement(
                direction,
                positionPersonnage,
                prochainePos,
                personnageEtaitSurCible,
                true,
                prochainePos,
                posApresBoite,
                boiteEtaitSurCible
            );
        }
        // Cas 2 : Déplacement simple (case vide ou cible)
        else {
            boolean personnageSurCible = prochaineCase.estCible();
            Personnage nouveauPersonnage = new Personnage(personnageSurCible);
            setCase(prochainePos, nouveauPersonnage);
            
            // restaurer la case d'origine
            if (personnageEtaitSurCible) {
                setCase(positionPersonnage, CaseCible.getInstance());
            } else {
                setCase(positionPersonnage, CaseVide.getInstance());
            }
            
            // Créer le mouvement simple
            mouvement = new Mouvement(
                direction,
                positionPersonnage,
                prochainePos,
                personnageEtaitSurCible
            );
        }
        
        // Mettre à jour la position du personnage
        positionPersonnage = prochainePos;
        
        // Ajouter le mouvement à l'historique
        historique.add(mouvement);
        
        return true;
    }
    
    /**
     * Annule le dernier mouvement effectué (Ctrl+Z).
     *
     * @return true si l'annulation a réussi, false sinon
     */
    public boolean annulerDernierMouvement() {
        if (historique.isEmpty()) {
            return false;
        }
        
        // récupérer et retirer le dernier mouvement
        Mouvement dernierMouvement = historique.remove(historique.size() - 1);
        
        Position posDepart = dernierMouvement.getPositionDepart();
        Position posArrivee = dernierMouvement.getPositionArrivee();
        
        // Cas 1 : Mouvement avec poussée de boite
        if (dernierMouvement.aPousseeBoite()) {
            Position posBoiteAvant = dernierMouvement.getPositionBoiteAvant();
            Position posBoiteApres = dernierMouvement.getPositionBoiteApres();
            
            // Remettre la boite à sa position d'origine
            CaseBoite boite = new CaseBoite(dernierMouvement.boiteEtaitSurCible());
            setCase(posBoiteAvant, boite);
            
            // Restaurer la case où était la boite après (vide ou cible)
            Case caseApresBoite = getCase(posBoiteApres);
            if (caseApresBoite instanceof CaseBoite) {
                CaseBoite boiteApres = (CaseBoite) caseApresBoite;
                if (boiteApres.estSurCible()) {
                    setCase(posBoiteApres, CaseCible.getInstance());
                } else {
                    setCase(posBoiteApres, CaseVide.getInstance());
                }
            }
        } else {
            // Cas 2 : Mouvement simple - restaurer la case d'arrivée
            Case caseArrivee = getCase(posArrivee);
            if (caseArrivee instanceof Personnage && ((Personnage) caseArrivee).estSurCible()) {
                setCase(posArrivee, CaseCible.getInstance());
            } else {
                setCase(posArrivee, CaseVide.getInstance());
            }
        }
        
        // Remettre le personnage à sa position de départ
        Personnage personnage = new Personnage(dernierMouvement.personnageEtaitSurCible());
        setCase(posDepart, personnage);
        
        // Mettre à jour la position actuelle
        positionPersonnage = posDepart;
        
        return true;
    }
    
    /**
     * Vérifie si le niveau est terminé (toutes les boîtes sur des cibles).
     *
     * @return true si le niveau est gagné, false sinon
     */
    public boolean estGagne() {
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                Case caseActuelle = grille.get(y).get(x);
                
                // Si on trouve une boite qui n'est pas sur une cible, le niveau n'est pas gagné
                if (caseActuelle instanceof CaseBoite) {
                    CaseBoite boite = (CaseBoite) caseActuelle;
                    if (!boite.estSurCible()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    /**
     * Compte le nombre de boîtes sur des cibles.
     *
     * @return nombre de boîtes correctement placées
     */
    public int compterBoitesSurCibles() {
        int compte = 0;
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                Case caseActuelle = grille.get(y).get(x);
                if (caseActuelle instanceof CaseBoite) {
                    CaseBoite boite = (CaseBoite) caseActuelle;
                    if (boite.estSurCible()) {
                        compte++;
                    }
                }
            }
        }
        return compte;
    }
    
    /**
     * Compte le nombre total de cibles dans le niveau.
     *
     * @return nombre de cibles
     */
    public int compterCibles() {
        int compte = 0;
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                Case caseActuelle = grille.get(y).get(x);
                if (caseActuelle.estCible() || 
                    (caseActuelle instanceof CaseBoite && ((CaseBoite) caseActuelle).estSurCible()) ||
                    (caseActuelle instanceof Personnage && ((Personnage) caseActuelle).estSurCible())) {
                    compte++;
                }
            }
        }
        return compte;
    }
    
    /**
     * Réinitialise l'historique des mouvements.
     */
    public void reinitialiserHistorique() {
        historique.clear();
    }

    /**
     * Remplace l'etat actuel du plateau par une nouvelle grille chargee.
     * L'historique est reinitialise.
     *
     * @param nouvelleGrille nouvelle grille a appliquer
     */
    public void chargerDepuisGrille(ArrayList<ArrayList<Case>> nouvelleGrille) {
        if (nouvelleGrille == null || nouvelleGrille.isEmpty()) {
            throw new IllegalArgumentException("La grille chargee ne peut pas etre vide");
        }

        int largeurReference = nouvelleGrille.get(0).size();
        if (largeurReference == 0) {
            throw new IllegalArgumentException("La grille chargee est invalide (ligne vide)");
        }

        ArrayList<ArrayList<Case>> copie = new ArrayList<>();
        int nombrePersonnages = 0;

        for (ArrayList<Case> ligne : nouvelleGrille) {
            if (ligne == null || ligne.size() != largeurReference) {
                throw new IllegalArgumentException("La grille chargee doit etre rectangulaire");
            }
            ArrayList<Case> copieLigne = new ArrayList<>(ligne);
            for (Case element : copieLigne) {
                if (element != null && element.estPersonnageCible()) {
                    nombrePersonnages++;
                }
            }
            copie.add(copieLigne);
        }

        if (nombrePersonnages != 1) {
            throw new IllegalArgumentException("La grille chargee doit contenir exactement un personnage");
        }

        this.grille = copie;
        this.hauteur = copie.size();
        this.largeur = largeurReference;
        this.positionPersonnage = trouverPositionPersonnage();
        this.historique.clear();
    }
    
    /**
     * Retourne l'historique des mouvements.
     *
     * @return liste des mouvements effectués
     */
    public ArrayList<Mouvement> getHistorique() {
        return historique;
    }
    
    /**
     * Retourne une représentation textuelle du plateau (debug).
     *
     * @return représentation textuelle du plateau
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                sb.append(grille.get(y).get(x).getSymbole());
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}