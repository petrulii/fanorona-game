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
		set(l, c);
	}

	public Position(Position p) {
		set(p.getLigne(), p.getColonne());
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
         * @return 
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

	public Position getSigne(Position position) {
		Position position_dir = soustraire(position);
		return new Position(
				Integer.signum(position_dir.getLigne()),
				Integer.signum(position_dir.getColonne())
		);
	}

	/**
	 * @param positions une ArrayList de positions
	 * @return la position la plus en haut ou la plus à gauche
	 */
	static public Position getPositionMin(ArrayList<Position> positions) {
		Position position_min = new Position(positions.get(0));
		for (Position position : positions)
			if(position_min.getLigne() > position.getLigne() || position_min.getColonne() > position.getColonne())
				position_min.set(position);
		return position_min;
	}

	/**
	 * @param positions une ArrayList de positions
	 * @return la position la plus en bas ou la plus à droite
	 */
	static public Position getPositionMax(ArrayList<Position> positions) {
		Position position_max = new Position(positions.get(0));
		for (Position position : positions)
			if(position_max.getLigne() < position.getLigne() || position_max.getColonne() < position.getColonne())
				position_max.set(position);
		return position_max;
	}

	/**
	 *
	 * @param positions une ArrayList de positions alignées
	 * @return les deux positions les plus éloignées
	 */
	static public ArrayList<Position> getExtremites(ArrayList<Position> positions) {
		ArrayList<Position> extremites = new ArrayList<>(2);
		Position p_min = new Position(positions.get(0));
		Position p_max = new Position(positions.get(0));
		for (Position position : positions) {
			if(p_min.getColonne() > position.getColonne())
				p_min.set(position);
			if(p_max.getColonne() < position.getColonne())
				p_max.set(position);
		}
		extremites.add(p_min);
		extremites.add(p_max);
		return extremites;
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
        @Override
	public String toString() { return "( "+ligne+", "+colonne+" )"; }

	/**
	 *
	 * @param p la position vers laquelle on veut set la position courante
	 */
	public void set(Position p) {
		set(p.getLigne(), p.getColonne());
	}

	/**
	 *
	 * @param l la nouvelle ligne de la position courante
	 * @param c la nouvelle colonne de la position courante
	 */
	public void set(int l, int c) {
		ligne = l;
		colonne = c;
	}

}