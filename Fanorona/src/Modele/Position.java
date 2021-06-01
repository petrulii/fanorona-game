package Modele;

/**
 * Une position est la coordonne ligne est la coordonne colonne dans la grille de jeu.
 * @author Petrulionyte Ieva, Yu Ran
 * @version 1.0
 */
public class Position {
	int ligne;
	int colonne;
	
	public Position(int l, int c) {
		ligne = l;
		colonne = c;
	}
	
	/**
	 * Copier la position courant
	 * @return une Position
	 */
	public Position copy() {
		return new Position(ligne, colonne);
	}

	/**
	 * Verifier si deux positions sont pareil
	 * @param p : position a comparer
	 * @return true si les deux positions sont egale,
	 * 		   false sinon 
	 */
	public boolean equals(Position p) {
		// L'adresse dans memoire pointe vers le meme objet.
		if (this == p) {
			return true;
		// Les attributs des positions sont egals.
		} else return this.ligne == p.getLigne() && this.colonne == p.getColonne();
	}

	/**
	 * --?
	 * @param p la position que l'on soustrait
	 */
	public Position soustraire(Position p) {
		return new Position(ligne-p.getLigne(), colonne-p.getColonne());
	}

	/**
	 * Renvoie la coordonne ligne de la position
	 * @return la coordonne ligne
	 */
	public int getLigne() {	return ligne; }

	/**
	 * Renvoie la coordonne colonne de la position
	 * @return la coordonne colonne
	 */
	public int getColonne() { return colonne; }

	/**
	 * Affichage d'unu position
	 * @return une chaine de caracteres decrivant la position
	 */
	public String toString() { return "( "+ligne+", "+colonne+" )"; }

}