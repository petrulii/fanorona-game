package Controleur;
import Modele.*;

/**
 * Joueur IA a la base de l'exploration de l'arbre min/max.
 * @author Petrulionyte Ieva
 * @version 1.0
 */
public class StaticMinMaxIA extends MinMaxIA {
    AireJeu aire_jeu;
    private int couleur_A;
    private int couleur_B;

    public StaticMinMaxIA(AireJeu a, int joueur, int niveau) {
        super(a, joueur, niveau);
    }
    
    @Override
    /**
     * Evalue une configuration et l'assigne une valeur qui correspond a nombre de pions de certain couleur sur le plateau de jeu.
     * @param configuration : un grille qui represente un configuration d'un plateau de jeu
     * @param couleur : un couleur d'un des joueurs (noir ou blanc)
     * @return nombre de pions de certain couleur sur le plateau de jeu
     */
    protected int evaluation(AireJeu configuration) {
		return comptePions(configuration, couleur_A) - comptePions(configuration, couleur_B);
	}

}
