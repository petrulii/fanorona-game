package Controleur;
import java.util.ArrayList;

import Modele.*;

/**
 * Joueur IA a la base de l'exploration de l'arbre min/max.
 * @author Petrulionyte Ieva
 * @version 1.0
 */
public class MinMaxIA extends IA {
    AireJeu aire_jeu;
    protected int profondeur_max;
    protected int couleur_A;
    protected int couleur_B;
    protected int meilleur_valeur;
    protected Coup meilleur_coup;

    public MinMaxIA(AireJeu a, int joueur, int niveau) {
        this.aire_jeu = a;
        this.couleur_A = joueur;
        this.profondeur_max = niveau;
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
		int valeur = Integer.MIN_VALUE + 1;
		int valeur_neoud;
   		if (profondeur >= profondeur_max && coups_initials != null) {
   			//System.out.println("Feuille joueur A: " + evaluation(configuration));
   			return evaluation(configuration);
   		} else {
   			// peut etre des tabs pre-definis pour chaque niveau de l'arbre ?
   			ArrayList<Coup> coups_jouables;
   			if (coups_initials != null) {
   				coups_jouables = coups_initials;
   			} else {
   				coups_jouables = configuration.coupsPossibles(couleur_A);
   			}
   			ArrayList<ArrayList<Coup>> tours_jouables = toursPossibles(configuration, coups_jouables, couleur_A);
			profondeur++;
			// intervalle (-inf , +inf)
	   		//System.out.println("Taille coups jouables joueur A: " + coups_jouables.size());
			for (ArrayList<Coup> tour_ia : tours_jouables) {
				jouerTour(configuration, tour_ia);
				valeur_neoud = donneCoupRecB(configuration, profondeur);
	   			// Si on est dans niveau 0.
	   	   		if ((coups_initials != null) && valeur_neoud > meilleur_valeur) {
	   	   			meilleur_valeur = valeur_neoud;
		   		   	meilleur_coup = tour_ia.get(0);
		   		}
	   			valeur = Math.max(valeur, valeur_neoud);
				annulerTour(configuration, tour_ia);
			}
   		}
		return valeur;
    }

    /**
     * Joue un tour (une sequence de coups).
     * @param configuration : un grille qui represente un configuration d'un plateau de jeu
     * @param tour_ia : liste de coups a jouer
     */
    private void jouerTour(AireJeu configuration, ArrayList<Coup> tour_ia) {
		for (Coup coup_ia : tour_ia) {
			configuration.joueCoup(coup_ia);
		}
	}
  
    /**
     * Annule un tour (une sequence de coups).
     * @param configuration : un grille qui represente un configuration d'un plateau de jeu
     * @param tour_ia : liste de coups a annuler
     */
    private void annulerTour(AireJeu configuration, ArrayList<Coup> tour_ia) {
		for (Coup coup_ia : tour_ia) {
			configuration.annulerCoup();
		}
	}

	/**
     * Joue le coup de joueur B en essayant de minimiser le nombre de pions de joueur A dans la configuration.
     * @param configuration : un grille qui represente un configuration d'un plateau de jeu
     * @param profondeur : le profondeur que l'IA explore dans l'arbre des configurations de jeu possibles
     * @return la valeur de la branche dans l'arbre qui amene au configuration qui minimise le nombre de pions de joueur A
     */
    public int donneCoupRecB(AireJeu configuration, int profondeur) {
		//System.out.println("Dans B: "+profondeur);
		int valeur = Integer.MAX_VALUE - 1;
   		if (profondeur >= profondeur_max) {
   			//System.out.println("Feuille joueur B: " + evaluation(configuration));
   			return evaluation(configuration);
   		} else {
   			ArrayList<Coup> coups_jouables = configuration.coupsPossibles(couleur_B);
   			ArrayList<ArrayList<Coup>> tours_jouables = toursPossibles(configuration, coups_jouables, couleur_B);
			profondeur++;
	   		//System.out.println("Taille coups jouables joueur B: " + coups_jouables.size()+", prof : "+profondeur);
			for (ArrayList<Coup> tour_ia : tours_jouables) {
				jouerTour(configuration, tour_ia);
				valeur = Math.min(valeur, donneCoupRecA(configuration, profondeur, null));
   				annulerTour(configuration, tour_ia);
			}
   		}
		return valeur;
    }

    /**
     * Evalue une configuration et l'assigne une valeur qui correspond a nombre de pions de certain couleur sur le plateau de jeu.
     * @param configuration : un grille qui represente un configuration d'un plateau de jeu
     * @return nombre de pions de certain couleur sur le plateau de jeu
     */
    protected int evaluation(AireJeu configuration) {
		System.out.println("Nombre pions A.");
		return comptePions(configuration, couleur_A);
	}

	/**
     * Compte le nombre de pions d'un joueur donne sur le plateau de jeu.
     * @param configuration : un grille qui represente un configuration d'un plateau de jeu
     * @param couleur : un couleur d'un des joueurs (noir ou blanc)
     * @return nombre de pions de certain couleur sur le plateau de jeu
     */
    protected int comptePions(AireJeu configuration, int couleur) {
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
     * Compte le nombre de pions diagonals d'un joueur donne sur le plateau de jeu.
     * @param configuration : un grille qui represente un configuration d'un plateau de jeu
     * @param couleur : un couleur d'un des joueurs (noir ou blanc)
     * @return nombre de pions diagonals de certain couleur sur le plateau de jeu
     */
    protected int comptePionsDiagonal(AireJeu configuration, int couleur) {
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
     * Calcul  recursivement.
     * @param configuration : un grille qui represente un configuration d'un plateau de jeu
     * @param coups_jouables : tous les listes des coups qui peuvent etre jouer
     * @param joueur : Le joueur de ces tours
     * @return les tours possibles
     */
    protected ArrayList<ArrayList<Coup>> toursPossibles(AireJeu configuration, ArrayList<Coup> coups_jouables, int joueur) {
    	ArrayList<ArrayList<Coup>> tours_jouables = new ArrayList<>();
    	// Ajout des coups avec choix.
    	Coup c_copie;
		for (Coup c : coups_jouables) {
			c_copie = c.copy();
			// Ajout d'un coup avec choix d'aspiration different.
			if (configuration.joueurDoitChoisir(c)) {
				c_copie.setAspiration(!c_copie.getAspiration());
				ArrayList<Coup> t1 = new ArrayList<>();
				t1.add(c_copie);
				tours_jouables.add(t1);
			}
			// Ajout de tour (sequence de coups) avec un seul coup sans continuation.
			ArrayList<Coup> t0 = new ArrayList<>();
			t0.add(c);
			tours_jouables.add(t0);
		}
		ArrayList<ArrayList<Coup>> tours_jouables_final = new ArrayList<>();
		for (ArrayList<Coup> tour : tours_jouables) {
			// completer tour avec les coups percusion/aspiration
			tours_jouables_final.add(tour);
			tours_jouables_final.addAll(toursPossiblesRec(tour, configuration, joueur));
		}
		return tours_jouables;
	}

    /**
     * Fonction recurssive qui calcule les tours possibles recursivement.
     * @param tour : les coups a jouer
     * @param configuration : un grille qui represente un configuration d'un plateau de jeu
     * @param joueur : Le joueur de ces tours
     * @return les tours possibles
     */
    protected ArrayList<ArrayList<Coup>> toursPossiblesRec(ArrayList<Coup> tour, AireJeu configuration, int joueur) {
    	// Ajout des tours possibles dans la liste de tours jouables.
    	ArrayList<ArrayList<Coup>> tours_jouables = new ArrayList<>();
    	Coup c;
    	Coup c_possible;
		//System.out.println(stringCoups(tour));
		c = tour.get(tour.size()-1);
		if (configuration.joueurPeutContinuerTour(c.getFin())) {
			configuration.joueCoup(c);
			ArrayList<Position> positions_possibles = configuration.positionsAdjacents(c.getFin());
			for (Position fin : positions_possibles) {
				c_possible = new Coup(c.getFin(), fin, joueur);
				if (configuration.coupValide(c_possible)) {
					ArrayList<Coup> tour_possible = copyCoups(tour);
					tour_possible.add(c_possible);
					tours_jouables.add(tour_possible);
					tours_jouables.addAll(toursPossiblesRec(tour_possible, configuration, joueur));
				}
			}
			configuration.annulerCoup();
		}
		return tours_jouables;
	}

    /**
     * Copie la liste des coups.
     * @param coups : Les coups a copier
     * @return liste des coups
     */
    public ArrayList<Coup> copyCoups(ArrayList<Coup> coups) {
		return new ArrayList<>(coups);
    }

	/**
     * Genere un coup d'IA valide en explorant un arbre min/max.
     * @param debut : un position debut d'un coup d'IA
     * @return un Coup valide
     */
    public Coup donneCoup(Position debut) {
    	meilleur_valeur = Integer.MIN_VALUE;
    	meilleur_coup = null;
        AireJeu aire = aire_jeu.copy();
        // Calcul des coups possibles.
    	ArrayList<Coup> coups_jouables_initials = aire.coupsPossibles(couleur_A);
    	ArrayList<Coup> coups_jouables = new ArrayList<>();
        if (debut != null) {
    		ArrayList<Position> voisins = aire.positionsAdjacents(debut);
    		Coup c;
    		for (Position fin : voisins) {
    			c = new Coup(debut, fin, couleur_A);
    			if (aire.coupFaitCapture(c) && aire.coupValide(c)) {
    				coups_jouables.add(c);
    			}
    		}
		} else {
			coups_jouables = coups_jouables_initials;
		}
        // Calcul de meilleur coup.
    	donneCoupRecA(aire, 0, coups_jouables);
		//System.out.println("Coup IA MinMax: " + meilleur_coup);
		return meilleur_coup;
    }

    /**
     * Transforme une liste de coups en chaine de charactere.
     * @param liste : liste des coups a transforme
     * @return une chaine de caracteres avec les coups 
     */
	public String stringCoups(ArrayList<Coup> liste) {
		String s = "";
		for(Coup c : liste)
    		s += c.toStringEspace();
		return s;
	}

}
