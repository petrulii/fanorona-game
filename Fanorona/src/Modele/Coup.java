package Modele;

import java.util.ArrayList;

/**
 * Un coup est une position de debut et une position fin d'un pion, aussi le numero de joueur et son choix entre aspiration et percussion.
 * @author Petrulionyte Ieva, Yu Ran
 * @version 1.0
 */
public class Coup {
	Position debut;
	Position fin;
	int joueur;
	/**
	 * Aspiration vaut vrai seulement quand l'utilisateur a le choix et il choisit l'aspiration.
	 */
	ArrayList<Position> pions_captures;
	/**
	 * Aspiration vaut vrai seulement quand l'utilisateur a le choix et il choisit l'aspiration.
	 */
	boolean aspiration = false;

	public Coup(Position debut, Position fin, int joueur) {
		this.debut = debut;
		this.fin = fin;
		this.joueur = joueur;
		this.pions_captures = null;
	}

	public boolean equals(Coup coup) {
		// L'adresse dans memoire pointe vers le meme objet.
		if (this == coup) {
			return true;
		// Les attributs des coups sont egals.
		} else if (debut.equals(coup.getDebut()) && fin.equals(coup.getFin()) && joueur==coup.getJoueur()) {
			return true;
		}
		return false;
	}

	/**
	 * Set l'attribut pions captures, qui designe tout pion capture pendant ce coup.
	 * @param les pions captures
	 */
	public void setPionsCaptures(ArrayList<Position> p) { this.pions_captures = p; }

	/**
	 * Set l'attribut aspiration, si vrai, on capture le pion devant, derriere sinon.
	 * @param la valeur d'aspiration a set
	 */
	public void setAspiration(boolean aspiration) { this.aspiration = aspiration; }
	
	/**
	 * Renvoie la postion debut de coup.
	 * @return la postion debut
	 */
	public Position getDebut() { return debut; }
	
	/**
	 * Renvoie la postion fin de coup.
	 * @return la postion fin
	 */
	public Position getFin() { return fin; }

	/**
	 * Renvoie l'entier joueur de coup.
	 * @return l'entier joueur
	 */
	public int getJoueur() { return joueur; }

	/**
	 * Renvoie les pions captures pendant ce coup.
	 * @return les pions captures
	 */
	public ArrayList<Position> getPions() { return pions_captures; }
	
	/**
	 * Renvoie le boolean aspiration de coup.
	 * @return le boolean aspiration
	 */
	public boolean getAspiration() { return aspiration; }
	
	/**
	 * Affichage d'un coup
	 * @return une chaine de caracteres decrivant le coup.
	 */
	public String toString() {
		String s = new String();
		s = "Debut: "+debut+", fin: "+fin+" , aspiration: "+aspiration+" , joueur: "+joueur+", pions captures: [ ";
		for (Position p : pions_captures) {
			s = s+p.toString()+" ";
		}
		s = s+"].";
		return s;
	}
	
}
