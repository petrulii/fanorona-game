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
    int NB_LIGNES;
    int NB_COLONNES;
    int PROFONDEUR_MAX = 6;
    int couleur_A;
    int couleur_B;

    public MinMaxIA(AireJeu a, int joueur) {
        aire_jeu = a;
        couleur_A = joueur;
		System.out.println("L'IA joue: "+joueur);
        if (couleur_A == 1) { couleur_B = 2; } else { couleur_B = 1; }
        NB_LIGNES = aire_jeu.getNbLignes();
        NB_COLONNES = aire_jeu.getNbColonnes();
    }
    
    /**
     * Correspond a <Calcul_Joueur_A(configuration)> dans les slides ici : https://prog6.gricad-pages.univ-grenoble-alpes.fr/prog6_projet_2020-2021/Amphis-IA.pdf
     * @param un grille qui represente un configuration d'un plateau de jeu
     */
    public int donneCoupRecA(AireJeu configuration, int profondeur) {
		//System.out.println("Dans A: "+profondeur);
		int valeur = evaluation(configuration, couleur_A);
   		if (profondeur <= 0) {
   			return valeur;
   		} else {
   			ArrayList<Coup> coups_jouables = configuration.coupsPossibles(couleur_A);
			profondeur--;
   			for (Coup c : coups_jouables) {
   				AireJeu configuration_coup = configuration.copy();
   				configuration_coup.joueCoup(c);
   				valeur = Math.max(valeur, donneCoupRecB(configuration_coup, profondeur));
   			}
   		}
		return valeur;
    }

	/**
     * Correspond a <Calcul_Joueur_B(configuration)> dans les slides ici : https://prog6.gricad-pages.univ-grenoble-alpes.fr/prog6_projet_2020-2021/Amphis-IA.pdf
     * @param un grille qui represente un configuration d'un plateau de jeu
     */
    public int donneCoupRecB(AireJeu configuration, int profondeur) {
		//System.out.println("Dans B: "+profondeur);
		int valeur = evaluation(configuration, couleur_A);
   		if (profondeur <= 0) {
   			return valeur;
   		} else {
   			ArrayList<Coup> coups_jouables = configuration.coupsPossibles(couleur_B);
				profondeur--;
   			for (Coup c : coups_jouables) {
   				AireJeu configuration_coup = configuration.copy();
   				configuration_coup.joueCoup(c);
   				valeur = Math.min(valeur, donneCoupRecA(configuration_coup, profondeur));
   			}
   		}
		return valeur;
    }
    
    private int evaluation(AireJeu configuration, int couleur) {
    	int[][] grille = configuration.getGrille();
    	int pions = 0;
		for (int i = 0; i < NB_LIGNES; i++) {
			for (int j = 0; j < NB_COLONNES; j++) {
				if (grille[i][j] == couleur) {
					pions++;
				}
			}
		}
		return pions;
	}

	/**
     * Genere un coup
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
				//System.out.println("J'ajoute peut etre coup jouable : "+cp.getDebut()+cp.getFin()+", debut : "+debut);
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
			//System.out.println("J'explore coup jouable "+c);
			// il faut faire les copies de la grille afin de ne pas modifier le plateau de jeu principal
			AireJeu configuration_coup = aire.copy();
			// on jeu un des coups jouable
			configuration_coup.joueCoup(c);
			// on explore une branche d'un arbre et/ou qui commence par ce coup
			valeur = donneCoupRecB(configuration_coup, PROFONDEUR_MAX);
	    	if (valeur<valeur_min) {
	    		valeur_min = valeur;
	    		meilleur_coup = c;
	    	}
		}
		coup = meilleur_coup;
		System.out.println("Coup IA MinMax: "+coup);
		return coup;
    }

	@Override
	public boolean faitChoixAspiration() {
		Random r = new Random();
		return r.nextBoolean();
	}

	@Override
	public Coup donneCoup() {
		return null;
	}

}
