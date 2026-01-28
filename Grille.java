package sukuban;

public class Grille {
	private int dim;	// Dimension de la Grille
	private int nbcl;	// Nombre de couleurs
//	private int taillePixel;	// Taille de la Grille à l'écran
//	private int casAff;		// Dit si une case est limité à un pix
//								// 0 s'il y a trop de cases à afficher
//								// 1 sinon
//	private int tailleCase;		// Taille d'une case à l'écran
//	private int modulo;		// Saut d'affichage en cas de nombre de cases important
	
	public Grille(int dim, int nbcl) {
		this.dim = dim;
		this.nbcl = nbcl;
	}
}
