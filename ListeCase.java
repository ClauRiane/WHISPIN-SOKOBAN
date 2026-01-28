package sukuban;

import java.util.LinkedList;
import java.util.Iterator;

public class ListeCase {
	private int i, j;
	private LinkedList<Position> maListe;
	
	public ListeCase(int i, int j) {
		this.i = i;
		this.j = j;
		
		this.maListe = new LinkedList<>();
	}
	
	public LinkedList<Position> getListe() {
		return this.maListe;
	}
	
	public void addHeadListeCase(int i, int j) {
		this.maListe.addFirst(new Position(i, j));
	}
}
