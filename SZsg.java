package sukuban;

import java.util.LinkedList;

public class SZsg extends Chemin {
	private int dim;
	private int nbcl;
	private ListeCase lzsg;
	private ListeCase b[];
	private int[][] app;
	private LinkedList<Position> maListe;
	
	public SZsg(int dim, int nbcl, ListeCase lzsg, ListeCase b, int[][] app, LinkedList<Position> maListe) {
		this.dim = dim;
		this.nbcl = nbcl;
		this.lzsg = lzsg;
		this.b = b;
		this.app = app;
		this.maListe = new LinkedList<>(maListe);
	}
	
	public int agranditSzsg(int[][] M, SZsg z, int cl, int k, int l) {
		int i, j;
		int	nb = 0;
		int[] x1 = {0, 0, 1, -1}; // new int[]{0, 0, 1, -1};
		int[] y1 = {1,-1, 0, 0};
		int x2, y2;
		int nvCouleur;
		LinkedList<Position> maPile = new LinkedList<>();
		Position tmp;
		
		maPile.addFirst(new Position(k, l));
		
		while(!testListeVide(maPile)) {
			tmp = maPile.removeFirst();
			
			if(z.getApp()[tmp.getX()][tmp.getY()] == -1) {
				continue;
			}
			
			z.getLzsg().addHeadListeCase(i, j);
			z.getApp()[i][j] = -1;
			
			nb++;
			
			for(int a = 0; a < 4; a++) {
				x2 = i + y1[a];
				y2 = j + y1[a];
				
				if(x2 >= 0 && x2 < z.getDim() && y2 >= 0 && y2 < z.getDim()) {
					if(z.getApp()[x2][y2] == -1) {
						continue;
					}
					
					if(M[x2][y2] == cl) {
						if(z.getApp()[x2][y2] != -1) {
							maPile.addFirst(new Position(x2, y2));
						}
					} else {
						nvCouleur = M[x2][y2];
						
						if(z.getApp()[x2][y2] != nvCouleur) {
							z.getB().addHeadListeCase(x2, y2);
							z.getApp()[x2][y2] = nvCouleur;
						}
					}
				}
			}
		}
		
		return nb;
	}
	
	public int strategieMaxBordureZone(int[][] M, Grille G, int dim, int nbcl, int aff) {
		SZsg z;
		
		int taille = 0;
		int chgmtCouleur = 0;
		int ancienneCouleur = 0;
		int maxCouleur;
		int maxGain;
		int simuleGain;
		
		ListeCase l;
		ListeCase bordure;
		ListeCase tmp;
		
		taille += agranditSzsg(M, z, ancienneCouleur, 0, 0);
		while(taille < dim * dim) {
			chgmtCouleur++;
			maxCouleur = -1;
			maxGain = -1;
			
			for(int c = 0; c < nbcl; c++) {
				if(c == ancienneCouleur) {
					continue;
				}
				
				if(testListeVide(z.getB()[c])) {
					continue;
				}
			}
		}
		
		return chgmtCouleur;
	}
	
	public int[][] getApp() {
		return this.app;
	}
	
	public ListeCase getLzsg() {
		return this.lzsg;
	}
	
	public int getDim() {
		return this.dim;
	}
	
	public ListeCase getB() {
		return this.b[];
	}
}