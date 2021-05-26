package Controleur;
import java.util.ArrayList;
import java.util.Random;

import Modele.*;

/**
 * Joueur IA a la base de l'exploration de l'arbre min/max.
 * @author Petrulionyte Ieva
 * @version 1.0
 */
public class StaticMinMaxIA extends IA {
    AireJeu aire_jeu;
    private int profondeur_max;
    private int couleur_A;
    private int couleur_B;
    private int meilleur_valeur;
    private Coup meilleur_coup;

    public StaticMinMaxIA(AireJeu a, int joueur, int niveau) {
        this.aire_jeu = a;
        this.couleur_A = joueur;
        this.profondeur_max = niveau;
		System.out.println("L'IA joue: "+joueur);
        if (couleur_A == AireJeu.BLANC) { couleur_B = AireJeu.NOIR; } else { couleur_B = AireJeu.BLANC; }
    }
    
    /**
     * Joue le coup de joueur A en essayant de maximiser le nombre de pions de joueur A dans la configuration.
     * @param configuration : un grille qui represente un configuration d'un plateau de jeu
     * @param profondeur : le profondeur que l'IA explore dans l'arbre des configurations de jeu possibles
     * @return la valeur de la branche dans l'arbre qui amene au configuration qui maximise le nombre de pions de joueur A
     */
    public int donneCoupRecA(AireJeu configuration, int profondeur, ArrayList<Coup> coups_initials) {
		//System.out.println("Dans A: "+profondeur);
		int valeur = Integer.MIN_VALUE;
		int valeur_neoud;
   		if (profondeur > profondeur_max) {
   			return evaluation(configuration);
   		} else {
   			// peut etre des tabs pre-definis pour chaque niveau de l'arbre ?
   			ArrayList<Coup> coups_jouables;
   			if (coups_initials != null) {
   				coups_jouables = coups_initials;
   			} else {
   				coups_jouables = configuration.coupsPossibles(couleur_A);
   			}
			profondeur++;
			// intervalle (-inf , +inf)
   			for (Coup coup_ia : coups_jouables) {
   				if (configuration.joueurDoitChoisir(coup_ia)) {
   					coup_ia.setAspiration(true);
   	   				configuration.joueCoup(coup_ia);
   	   				// passer intervalle en param pour donneCoupRecB, dans donneCoupRecB je coupe si configuration evalue pas dans l'intervalle
   	   				valeur_neoud = donneCoupRecB(configuration, profondeur);
   	   				// Si on est dans niveau 0.
   	   				if ((coups_initials != null) && valeur_neoud < meilleur_valeur) {
   	   					meilleur_valeur = valeur_neoud;
	   		    		meilleur_coup = coup_ia;
	   		    	}
   	   				valeur = Math.max(valeur, valeur_neoud);
   	   				configuration.annulerCoup();
   					coup_ia.setAspiration(false);
   				}
   				configuration.joueCoup(coup_ia);
   				valeur_neoud = donneCoupRecB(configuration, profondeur);
   				// Si on est dans niveau 0.
   	   			if ((coups_initials != null) && valeur_neoud < meilleur_valeur) {
   	   				meilleur_valeur = valeur_neoud;
	   		    	meilleur_coup = coup_ia;
	   		    }
   				valeur = Math.max(valeur, valeur_neoud);
   				configuration.annulerCoup();
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
		int valeur = Integer.MAX_VALUE;
   		if (profondeur > profondeur_max) {
   			return evaluation(configuration);
   		} else {
   			ArrayList<Coup> coups_jouables = configuration.coupsPossibles(couleur_B);
				profondeur++;
   			for (Coup coup_ia : coups_jouables) {
   				if (configuration.joueurDoitChoisir(coup_ia)) {
   					coup_ia.setAspiration(true);
   	   				configuration.joueCoup(coup_ia);
   	   				valeur = Math.min(valeur, donneCoupRecA(configuration, profondeur, null));
   	   				configuration.annulerCoup();
   					coup_ia.setAspiration(false);
   				}
   				configuration.joueCoup(coup_ia);
   				valeur = Math.min(valeur, donneCoupRecA(configuration, profondeur, null));
   				configuration.annulerCoup();
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
    private int evaluation(AireJeu configuration) {
		return comptePions(configuration, couleur_A);
	}

	/**
     * Compte le nombre de pions d'un joueur donne sur le plateau de jeu.
     * @param configuration : un grille qui represente un configuration d'un plateau de jeu
     * @param couleur : un couleur d'un des joueurs (noir ou blanc)
     * @return nombre de pions de certain couleur sur le plateau de jeu
     */
    private int comptePions(AireJeu configuration, int couleur) {
    	int[][] grille = configuration.getGrille();
		int pions_couleur = 0;
		for (int i = 0; i < AireJeu.NB_LIGNES; i++) {
			for (int j = 0; j < AireJeu.NB_COLONNES; j++) {
				if (grille[i][j] == couleur) {
					pions_couleur++;
				}
			}
		}
		return pions_couleur;
	}

	/**
     * Genere un coup d'IA valide en explorant un arbre min/max.
     * @param debut : un position debut d'un coup d'IA
     * @return un Coup valide
     */
    public Coup donneCoup(Position debut) {
    	meilleur_valeur = Integer.MAX_VALUE;
    	meilleur_coup = null;
        AireJeu aire = aire_jeu.copy();
        // Calcul des coups possibles.
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
        // Calcul de meilleur coup.
    	donneCoupRecA(aire, 0, coups_jouables);
		System.out.println("Coup IA MinMax: "+meilleur_coup);
		return meilleur_coup;
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
