package Controleur;
import java.util.ArrayList;
import java.util.Random;

import Modele.*;

/**
 * Joueur IA a la base de l'exploration de l'arbre min/max.
 * @author Petrulionyte Ieva
 * @version 1.0
 */
public class MinMaxIA implements IA {
    AireJeu aire_jeu;
    int PROFONDEUR_MAX = 5;
    int couleur_A;
    int couleur_B;

    public MinMaxIA(AireJeu a, int joueur) {
        aire_jeu = a;
        couleur_A = joueur;
		System.out.println("L'IA joue: "+joueur);
        if (couleur_A == 1) { couleur_B = 2; } else { couleur_B = 1; }
    }
    
    /**
     * Joue le coup de joueur A en essayant de maximiser le nombre de pions de joueur A dans la configuration.
     * @param configuration : un grille qui represente un configuration d'un plateau de jeu
     * @param profondeur : le profondeur que l'IA explore dans l'arbre des configurations de jeu possibles
     * @return la valeur de la branche dans l'arbre qui amene au configuration qui maximise le nombre de pions de joueur A
     */
    public int donneCoupRecA(AireJeu configuration, int profondeur) {
		//System.out.println("Dans A: "+profondeur);
		int valeur = evaluation(configuration, couleur_A);
   		if (profondeur >= PROFONDEUR_MAX) {
   			return valeur;
   		} else {
   			ArrayList<Coup> coups_jouables = configuration.coupsPossibles(couleur_A);
			profondeur++;
   			for (Coup c : coups_jouables) {
   				AireJeu configuration_coup = configuration.copy();
   				configuration_coup.joueCoup(c);
   				valeur = Math.max(valeur, donneCoupRecB(configuration_coup, profondeur));
   			}
   		}
		return valeur;
    }

    /**
     * Joue le coup de joueur B en essayant de minimiser le nombre de pions de joueur A dans la configuration.
     * @param configuration : un grille qui represente un configuration d'un plateau de jeu
     * @param profondeur : le profondeur que l'IA explore dans l'arbre des configurations de jeu possibles
     * @return la valeur de la branche dans l'arbre qui amene au configuration qui minimise le nombre de pions de joueur A
     */
    public int donneCoupRecB(AireJeu configuration, int profondeur) {
		//System.out.println("Dans B: "+profondeur);
		int valeur = evaluation(configuration, couleur_A);
   		if (profondeur >= PROFONDEUR_MAX) {
   			return valeur;
   		} else {
   			ArrayList<Coup> coups_jouables = configuration.coupsPossibles(couleur_B);
				profondeur++;
   			for (Coup c : coups_jouables) {
   				AireJeu configuration_coup = configuration.copy();
   				configuration_coup.joueCoup(c);
   				valeur = Math.min(valeur, donneCoupRecA(configuration_coup, profondeur));
   			}
   		}
		return valeur;
    }
    
    /**
     * Evalue une configuration et l'assigne une valeur qui correspond a nombre de pions de certain couleur sur le plateau de jeu.
     * @param configuration : un grille qui represente un configuration d'un plateau de jeu
     * @param couleur : un couleur d'un des joueurs (noir ou blanc)
     * @return nombre de pions de certain couleur sur le plateau de jeu
     */
    private int evaluation(AireJeu configuration, int couleur) {
    	int[][] grille = configuration.getGrille();
    	int pions = 0;
		for (int i = 0; i < AireJeu.NB_LIGNES; i++) {
			for (int j = 0; j < AireJeu.NB_COLONNES; j++) {
				if (grille[i][j] == couleur) {
					pions++;
				}
			}
		}
		return pions;
	}

	/**
     * Genere un coup d'IA valide en explorant un arbre min/max.
     * @param debut : un position debut d'un coup d'IA
     * @return un Coup valide
     */
    public Coup donneCoup(Position debut) {
    	Coup coup = null;
    	Coup meilleur_coup = null;
        AireJeu aire = aire_jeu.copy();
    	ArrayList<Coup> coups_jouables_initials = aire.coupsPossibles(couleur_A);
    	ArrayList<Coup> coups_jouables = new ArrayList<Coup>();
        if (debut != null) {
			for (Coup cp : coups_jouables_initials) {
				if (cp.getDebut().equals(debut)) {
					coups_jouables.add(cp);
				}
			}
		} else {
			coups_jouables = coups_jouables_initials;
		}
		int valeur = 0;
		int valeur_min = 100;
		// pour chaque coup joauable on explore toute branche d'un arbre et/ou
		for (Coup c : coups_jouables) {
			// il faut faire les copies de la grille afin de ne pas modifier le plateau de jeu principal
			AireJeu configuration_coup = aire.copy();
			// on jeu un des coups jouable
			configuration_coup.joueCoup(c);
			// on explore une branche d'un arbre et/ou qui commence par ce coup
			valeur = donneCoupRecB(configuration_coup, 0);
	    	if (valeur<valeur_min) {
	    		valeur_min = valeur;
	    		meilleur_coup = c;
	    	}
		}
		coup = meilleur_coup;
		System.out.println("Coup IA MinMax: "+coup);
		return coup;
    }

	/**
     * Fait un choix aleatoire entre l'aspiration et percusion.
     * @return vrai si choix d'aspiration, faux sinon
     */
    @Override
	public boolean faitChoixAspiration() {
		Random r = new Random();
		return r.nextBoolean();
	}

}
