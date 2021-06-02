package Controleur;
import Modele.*;

/**
 * Joueur IA a la base de l'exploration de l'arbre min/max.
 * @author Petrulionyte Ieva
 * @version 1.0
 */
public class DynamiqueIA extends AlphaBetaIA {

    public DynamiqueIA(AireJeu a, int joueur, int niveau) {
        super(a, joueur, niveau);
        System.out.println("Couleur pions A d'IA min/max dynamique : " + couleur_A);
    }
    
    @Override
    /**
     * Evalue une configuration et l'assigne une valeur qui correspond a nombre de pions de certain couleur sur le plateau de jeu.
     * @param configuration : un grille qui represente un configuration d'un plateau de jeu
     * @param couleur : un couleur d'un des joueurs (noir ou blanc)
     * @return nombre de pions de certain couleur sur le plateau de jeu
     */
    protected int evaluation(AireJeu configuration) {
    	int pions_A = comptePions(configuration, couleur_A);
    	// Strategie debut de partie, quand nombre de pions de joueur IA > 10.
		if (pions_A > 7) {
			//System.out.println("Difference pions A - pions B.");
			return pions_A - comptePions(configuration, couleur_B);
	    // Strategie fin de partie, quand il n'y a plus beacoup (<= changement_evaluation) de pions sur le plateau de jeu.
		} else {
			//System.out.println("Pions diagonals, pions A : " + pions_A+", couleur A : " + couleur_A);
			return pions_A + comptePionsDiagonal(configuration, couleur_A);
		}
	}

}