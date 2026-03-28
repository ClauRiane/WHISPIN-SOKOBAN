import java.util.ArrayList;

/**
 * représente le plateau de jeu Sokoban
 * gère la grille, la position du personnage et toute la logique de déplacement
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
     * constructeur vide pour créer un plateau vide
     */
    public Plateau() {
        this.grille = new ArrayList<>();
        this.historique = new ArrayList<>();
        this.positionPersonnage = null;
        this.hauteur = 0;
        this.largeur = 0;
    }
    
    /**
     * constructeur avec une grille existante
     * 
     * @param grille la grille de cases
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
     * trouve la position du personnage dans la grille
     * 
     * @return la position du personnage, ou null si non trouvé
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
     * retourne la case à la position donnée
     * 
     * @param pos la position
     * @return la case à cette position
     * @throws IndexOutOfBoundsException si la position est hors limites
     */
    public Case getCase(Position pos) {
        return getCase(pos.getx(), pos.gety());
    }
    
    /**
     * retourne la case aux coordonnées données
     * 
     * @param x coordonnée x
     * @param y coordonnée y
     * @return la case à cette position
     * @throws IndexOutOfBoundsException si les coordonnées sont hors limites
     */
    public Case getCase(int x, int y) {
        if (!estDansLimites(x, y)) {
            throw new IndexOutOfBoundsException("Position hors limites: (" + x + ", " + y + ")");
        }
        return grille.get(y).get(x);
    }
    
    /**
     * modifie la case à la position donnée
     * 
     * @param pos la position
     * @param nouvelleCase la nouvelle case
     */
    public void setCase(Position pos, Case nouvelleCase) {
        setCase(pos.getx(), pos.gety(), nouvelleCase);
    }
    
    /**
     * modifie la case aux coordonnées données
     * 
     * @param x coordonnée x
     * @param y coordonnée y
     * @param nouvelleCase la nouvelle case
     */
    public void setCase(int x, int y, Case nouvelleCase) {
        if (!estDansLimites(x, y)) {
            throw new IndexOutOfBoundsException("Position hors limites: (" + x + ", " + y + ")");
        }
        grille.get(y).set(x, nouvelleCase);
    }
    
    /**
     * vérifie si une position est dans les limites du plateau
     * 
     * @param x coordonnée x
     * @param y coordonnée y
     * @return true si dans les limites, false sinon
     */
    public boolean estDansLimites(int x, int y) {
        return x >= 0 && x < largeur && y >= 0 && y < hauteur;
    }
    
    /**
     * vérifie si une position est dans les limites du plateau
     * 
     * @param pos la position à vérifier
     * @return true si dans les limites, false sinon
     */
    public boolean estDansLimites(Position pos) {
        return estDansLimites(pos.getx(), pos.gety());
    }
    
    /**
     * retourne la position actuelle du personnage
     * 
     * @return la position du personnage
     */
    public Position getPositionPersonnage() {
        return positionPersonnage;
    }
    
    /**
     * retourne la grille complète
     * 
     * @return la grille
     */
    public ArrayList<ArrayList<Case>> getGrille() {
        return grille;
    }
    
    /**
     * retourne la hauteur du plateau
     * 
     * @return le nombre de lignes
     */
    public int getHauteur() {
        return hauteur;
    }
    
    /**
     * retourne la largeur du plateau
     * 
     * @return le nombre de colonnes
     */
    public int getLargeur() {
        return largeur;
    }
    
    /**
     * vérifie si un déplacement dans une direction est possible
     * 
     * @param direction la direction du déplacement
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
     * vérifie si une boite peut être poussée dans une direction
     * 
     * @param posBoite position de la boite
     * @param direction direction de la poussée
     * @return true si la boite peut être poussée, false sinon
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
     * effectue un déplacement dans une direction
     * gère le déplacement du personnage et la poussée de boites si nécessaire
     * 
     * @param direction la direction du déplacement
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
            boolean personnageSurCible = prochaineCase.estCible();
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
     * annule le dernier mouvement effectué (Ctrl+Z)
     * 
     * @return true si l'annulation a réussi, false s'il n'y a pas de mouvement à annuler
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
     * vérifie si le niveau est terminé (toutes les boites sur des cibles)
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
     * vompte le nombre de boites sur des cibles 
     * 
     * @return le nombre de boites correctement placées
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
     * compte le nombre total de cibles dans le niveau
     * 
     * @return le nombre de cibles
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
     * réinitialise l'historique des mouvements
     */
    public void reinitialiserHistorique() {
        historique.clear();
    }
    
    /**
     * retourne l'historique des mouvements
     * 
     * @return la liste des mouvements effectués
     */
    public ArrayList<Mouvement> getHistorique() {
        return historique;
    }
    
    /**
     * affiche le plateau sous forme textuelle (pour debug)
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