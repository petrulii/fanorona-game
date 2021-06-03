package Modele;

import java.util.ArrayList;

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
	 * @param positions une ArrayList de positions
	 * @return vrai si l'instance actuelle est égale à l'une des positions de la liste donnée
	 */
	public boolean estDansListePositions(ArrayList<Position> positions) {
		for (Position position : positions)
			if(position.equals(this))
				return true;
		return false;
	}

	/**
	 * @param positions une ArrayList de positions
	 * @return une position avec la ligne et la colonne les plus petites de celles des positions données en entrée
	 */
	static public Position getPositionMin(ArrayList<Position> positions) {
		int ligne_min = positions.get(0).getLigne();
		int colonne_min = positions.get(0).getColonne();
		for (Position position : positions) {
			ligne_min = Math.min(ligne_min, position.getLigne());
			colonne_min = Math.min(colonne_min, position.getColonne());
		}
		return new Position(ligne_min, colonne_min);
	}

	/**
	 * @param positions une ArrayList de positions
	 * @return une position avec la ligne et la colonne les plus grandes de celles des positions données en entrée
	 */
	static public Position getPositionMax(ArrayList<Position> positions) {
		int ligne_max = positions.get(0).getLigne();
		int colonne_max = positions.get(0).getColonne();
		for (Position position : positions) {
			ligne_max = Math.max(ligne_max, position.getLigne());
			colonne_max = Math.max(colonne_max, position.getColonne());
		}
		return new Position(ligne_max, colonne_max);
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