package Modele;

/**
 * Un coup est une position de debut et une position fin d'un pion, aussi le choix entre aspiration et percussion.
 * @author Petrulionyte Ieva, Yu Ran
 * @version 1.0
 */
public class Coup {
	Position debut;
	Position fin;
	boolean aspiration = false;

	public Coup(Position debut, Position fin) {
		this.debut = debut;
		this.fin = fin;
	}

	public boolean equals(Coup coup) {
		// L'adresse dans memoire pointe vers le meme objet.
		if (this == coup) {
			return true;
		// Les attributs des coups sont egals.
		} else if (debut.equals(coup.getDebut()) && fin.equals(coup.getFin())) {
			return true;
		}
		return false;
	}

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
	 * Affichage d'un coup
	 * @return une chaine de caracteres decrivant le coup.
	 */
	public String toString() { return "Debut: "+debut+", fin: "+fin+" , aspiration: "+aspiration+"."; }
	
}
