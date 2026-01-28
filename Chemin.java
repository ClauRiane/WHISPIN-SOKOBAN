package sukuban;

import java.util.LinkedList;

public class Chemin {
	public boolean testListeVide(LinkedList<Position> maListe) {
		// La liste existe ?
		if(maListe == null) {
			return true;
		}
		
		// La liste est vide ?
		return maListe.isEmpty();
	}
}
