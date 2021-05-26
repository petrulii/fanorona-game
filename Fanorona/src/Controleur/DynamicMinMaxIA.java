package Controleur;
import java.util.ArrayList;
import java.util.Random;

import Modele.*;

/**
 * Joueur IA a la base de l'exploration de l'arbre min/max.
 * @author Petrulionyte Ieva
 * @version 1.0
 */
public class DynamicMinMaxIA extends IA {
    AireJeu aire_jeu;
    private int profondeur_max;
    private int changement_evaluation;
    private int couleur_A;
    private int couleur_B;

    public DynamicMinMaxIA(AireJeu a, int joueur, int niveau) {
        this.aire_jeu = a;
        this.couleur_A = joueur;
        this.profondeur_max = niveau;
        this.changement_evaluation = 2;
		System.out.println("L'IA joue: "+joueur);
        if (couleur_A == AireJeu.BLANC) { couleur_B = AireJeu.NOIR; } else { couleur_B = AireJeu.BLANC; }
    }
    
    /**
     * Joue le coup de joueur A en essayant de maximiser le nombre de pions de joueur A dans la configuration.
     * @param configuration : un grille qui represente un configuration d'un plateau de jeu
     * @param profondeur : le profondeur que l'IA explore dans l'arbre des configurations de jeu possibles
     * @return la valeur de la branche dans l'arbre qui amene au configuration qui maximise le nombre de pions de joueur A
     */
    public int donneCoupRecA(AireJeu configuration, int profondeur) {
		//System.out.println("Dans A: "+profondeur);
		int valeur = Integer.MIN_VALUE;
   		if (profondeur >= profondeur_max) {
   			return evaluation(configuration, couleur_A);
   		} else {
   			// peut etre des tabs pre-definis pour chaque niveau de l'arbre ?
   			ArrayList<Coup> coups_jouables = configuration.coupsPossibles(couleur_A);
			profondeur++;
			// intervalle (-inf , +inf)
   			for (Coup coup_ia : coups_jouables) {
   				if (configuration.joueurDoitChoisir(coup_ia)) {
   					coup_ia.setAspiration(true);
   	   				configuration.joueCoup(coup_ia);
   	   				// passer intervalle en param pour donneCoupRecB, dans donneCoupRecB je coupe si configuration evalue pas dans l'intervalle
   	   				valeur = Math.max(valeur, donneCoupRecB(configuration, profondeur));
   	   				configuration.annulerCoup();
   					coup_ia.setAspiration(false);
   				}
   				configuration.joueCoup(coup_ia);
   				valeur = Math.max(valeur, donneCoupRecB(configuration, profondeur));
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
   		if (profondeur >= profondeur_max) {
   			return evaluation(configuration, couleur_A);
   		} else {
   			ArrayList<Coup> coups_jouables = configuration.coupsPossibles(couleur_B);
				profondeur++;
   			for (Coup coup_ia : coups_jouables) {
   				if (configuration.joueurDoitChoisir(coup_ia)) {
   					coup_ia.setAspiration(true);
   	   				configuration.joueCoup(coup_ia);
   	   				valeur = Math.min(valeur, donneCoupRecA(configuration, profondeur));
   	   				configuration.annulerCoup();
   					coup_ia.setAspiration(false);
   				}
   				configuration.joueCoup(coup_ia);
   				valeur = Math.min(valeur, donneCoupRecA(configuration, profondeur));
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
    private int evaluation(AireJeu configuration, int couleur) {
		int pions_couleur = comptePions(configuration, couleur);
		int pions_diagonal = comptePionsDiagonal(configuration, couleur);
    	// Strategie debut de partie, quand nombre de pions > changement_evaluation.
		if (pions_couleur > ((5*9-1)/2)/changement_evaluation) {
			return pions_couleur;
	    // Strategie fin de partie, quand il n'y a plus beacoup (<= changement_evaluation) de pions sur le plateau de jeu.
		} else {
			return pions_diagonal;
		}
	}
    
    /**
     * Compte le nombre de pions diagonals d'un joueur donne sur le plateau de jeu.
     * @param configuration : un grille qui represente un configuration d'un plateau de jeu
     * @param couleur : un couleur d'un des joueurs (noir ou blanc)
     * @return nombre de pions diagonals de certain couleur sur le plateau de jeu
     */
    private int comptePionsDiagonal(AireJeu configuration, int couleur) {
    	int[][] grille = configuration.getGrille();
		int pions_diagonal = 0;
		for (int i = 0; i < AireJeu.NB_LIGNES; i++) {
			for (int j = 0; j < AireJeu.NB_COLONNES; j++) {
				if ((i%2==1 && j%2==1 || i%2==0 && j%2==0) && (grille[i][j] == couleur)) {
					pions_diagonal++;
				}
			}
		}
		return pions_diagonal;
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
    	Coup coup = null;
    	Coup meilleur_coup = null;
        AireJeu aire = aire_jeu.copy();
        // Calcul de liste des coups jouables.
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
		// Pour chaque coup joauable on explore toute branche d'un arbre.
		for (Coup c : coups_jouables) {
			// On jeu un des coups jouables.
   			if (aire.joueurDoitChoisir(c)) {
   				c.setAspiration(true);
   				aire.joueCoup(c);
   	   			valeur = donneCoupRecB(aire, 0);
   				// On choisit le coup qui amene a une branche qui a une feuille avec la configuration desavantageuse pour l'adversaire.
   		    	if (valeur < valeur_min) {
   		    		valeur_min = valeur;
   		    		meilleur_coup = c;
   		    	}
   	   			aire.annulerCoup();
   				c.setAspiration(false);
   			}
			aire.joueCoup(c);
			// On explore une branche d'un arbre Min/Max qui commence par ce coup.
			valeur = Math.max(valeur, donneCoupRecB(aire, 0));
			// On choisit le coup qui amene a une branche qui a une feuille avec la configuration desavantageuse pour l'adversaire.
	    	if (valeur < valeur_min) {
	    		valeur_min = valeur;
	    		meilleur_coup = c;
	    	}
	    	aire.annulerCoup();
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
