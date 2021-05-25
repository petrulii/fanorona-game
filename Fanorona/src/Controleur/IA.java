package Controleur;
import Modele.Coup;
import Modele.Position;

/**
 * Interface commun pour tout joueur aleatoire.
 * @author Petrulionyte Ieva
 * @version 1.0
 */
public interface IA {
	/**
     * Genere un coup en utilisant la strategie d'IA
     * @return un Coup valide
     */
	public Coup donneCoup(Position debut);
    public boolean faitChoixAspiration();
}