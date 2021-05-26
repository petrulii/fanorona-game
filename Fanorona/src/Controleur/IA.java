package Controleur;
import Modele.Coup;
import Modele.Position;

/**
 * Interface commun pour tout joueur aleatoire.
 * @author Petrulionyte Ieva
 * @version 1.0
 */
public abstract class IA {
	/**
     * Genere un coup en utilisant la strategie d'IA
     * @return un Coup valide
     */
	public Coup donneCoup() { return null; }
	public Coup donneCoup(Position debut) { return null; }
	/**
     * Genere un choix d'aspiration en utilisant la strategie d'IA
     * @return un choix d'aspiration
     */
    public boolean faitChoixAspiration() { return false; }
}