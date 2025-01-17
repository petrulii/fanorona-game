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
		this.pions_captures = new ArrayList<>();
	}

	public Coup(Position debut, Position fin, boolean aspiration, int joueur, ArrayList<Position> pions) {
		this.debut = debut;
		this.fin = fin;
		this.aspiration = aspiration;
		this.joueur = joueur;
		this.pions_captures = pions;
	}

	/**
	 * Copier un coup
	 * @return un coup
	 */
	public Coup copy() {
		Position d = (this.debut).copy();
		Position f = (this.fin).copy();
		boolean a = this.aspiration;
		int j = this.joueur;
		ArrayList<Position> p_c = copyPionsCaptures();
		return new Coup(d, f, a, j, p_c);
	}

	/**
	 * Copie la liste des pions captures.
	 * @return une liste des pions
	 */
    public ArrayList<Position> copyPionsCaptures() {
		return new ArrayList<>(this.pions_captures);
    }

    /**
     * Verifier si deux coups sont egals
     * @param coup : le coup a comparer
     * @return vrai si les deux coup sont pareil, false sinon
     */
	public boolean equals(Coup coup) {
		// L'adresse dans memoire pointe vers le meme objet.
		if (this == coup) {
			return true;
		// Les attributs des coups sont egals.
		} else return debut.equals(coup.getDebut()) && fin.equals(coup.getFin()) && joueur == coup.getJoueur();
	}

	/**
	 * Set l'attribut pions captures, qui designe tout pion capture pendant ce coup.
	 * @param p les pions captures
	 */
	public void setPionsCaptures(ArrayList<Position> p) { this.pions_captures = p; }

	/**
	 * Set l'attribut aspiration, si vrai, on capture le pion devant, derriere sinon.
	 * @param aspiration la valeur d'aspiration a set
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
        @Override
	public String toString() {
		String s = "Debut: " + debut + ", fin: " + fin + " , aspiration: " + aspiration + " , joueur: " + joueur + ", pions captures: [ ";
		for (Position p : pions_captures)
			s += p + " ";
		s += "].\n";
		return s;
	}

	/**
	 * Affichage d'un coup sans explications et que avec des espaces.
	 * @return une chaine de caracteres decrivant le coup.
	 */
	public String toStringEspace() {
		String s = debut.getLigne() + " " + debut.getColonne() + " " + fin.getLigne() + " " + fin.getColonne() + " " + aspiration + " " + joueur + " ";
		for (Position p : pions_captures)
			s += p.getLigne() + " " + p.getColonne() + " ";
		s += "\n";
		return s;
	}
	
}
