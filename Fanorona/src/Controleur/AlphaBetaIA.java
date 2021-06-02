package Controleur;
import java.util.ArrayList;
import java.util.Random;

import Modele.*;

/**
 * Joueur IA a la base de l'exploration de l'arbre min/max et optimisation alpha/beta.
 * @author Petrulionyte Ieva
 * @version 1.0
 */
public class AlphaBetaIA extends IA {
    AireJeu aire_jeu;
    protected int profondeur_max;
    protected int couleur_A;
    protected int couleur_B;
    protected int meilleur_valeur;
    protected Coup meilleur_coup;
    protected ArrayList<Coup> meilleurs_coups;

    public AlphaBetaIA(AireJeu a, int joueur, int niveau) {
        this.aire_jeu = a;
        this.couleur_A = joueur;
        this.profondeur_max = niveau;
        this.meilleurs_coups = new ArrayList<Coup>();
        if (couleur_A == AireJeu.BLANC) { couleur_B = AireJeu.NOIR; } else { couleur_B = AireJeu.BLANC; }
    }
    
    /**
     * Joue le coup de joueur A en essayant de maximiser le nombre de pions de joueur A dans la configuration.
     * @param configuration : un grille qui represente un configuration d'un plateau de jeu
     * @param profondeur : le profondeur que l'IA explore dans l'arbre des configurations de jeu possibles
     * @param coups_initials : la liste des premiers coups jouables a partir des positions actuels de joueur
     * @param alpha
     * @param beta
     * @param maximiser : vaut vrai si le joueur essaie de maximiser la victoire d'IA, faux s'il essaie de la minimiser
     * @return la valeur de la branche dans l'arbre qui amene au configuration qui maximise le nombre de pions de joueur A
     */
    public int donneCoupRec(AireJeu configuration, int profondeur, ArrayList<Coup> coups_initials, int alpha, int beta, boolean maximiser) {
		//System.out.println("Alpha: "+alpha+", beta: "+beta+", prof: "+profondeur);
    	if (profondeur < 0 || configuration.gameOver()) {
   			return evaluation(configuration);
   		}
		profondeur = profondeur - 1;
		// Si on maximise.
   		if (maximiser) {
   			int valeur = Integer.MIN_VALUE + 1;
   			int valeur_neoud;
   			// Calcul tours jouables.
   			ArrayList<Coup> coups_jouables;
   			// Cas de premier coup dans les tours possibles.
   			if (coups_initials != null) {
   				coups_jouables = coups_initials;
   			} else {
   				coups_jouables = configuration.coupsPossibles(couleur_A);
   			}
   			ArrayList<ArrayList<Coup>> tours_jouables = toursPossibles(configuration, coups_jouables, couleur_A);
   			// Fin calcul tours jouables.
			for (ArrayList<Coup> tour_ia : tours_jouables) {
				// On affiche le tour initial.
	   	   		if ((coups_initials != null)) {
	   	   			System.out.println("###### tour A prof 0 ######");
		   	   		afficheTour(tour_ia);
		   		}
	   	   		//System.out.println("###### A ######");
				// Debut calcul recursif.
				jouerTour(configuration, tour_ia);
				valeur_neoud = donneCoupRec(configuration, profondeur, null, alpha, beta, false);
		   		// On trouve un des meilleurs coups.
				// Si profondeur est 0, factoriser avec joueur B (qui minimize).
				// Pas d'attribut meilleure_valeur, au place comparer avec valeur.
	   	   		/*if ((coups_initials != null) && valeur_neoud == meilleur_valeur) {
	   	   			meilleurs_coups.add(tour_ia.get(0));
		   		}*/
	   	   		// On trouve le meilleur coup.
	   	   		if ((coups_initials != null) && valeur_neoud > meilleur_valeur) {
		   	   		afficheGrille(configuration.getGrille());
		   	   		System.out.println("valeur_neoud: "+valeur_neoud);
	   	   			meilleur_valeur = valeur_neoud;
	   	   			meilleurs_coups.clear();
	   	   			meilleurs_coups.add(tour_ia.get(0));
		   	   		System.out.println("je clear er j'ajoute coup: "+tour_ia.get(0));
		   		   	//meilleur_coup = tour_ia.get(0);
		   		}
				// Fin calcul recursif.
	   			valeur = Math.max(valeur, valeur_neoud);
	   			alpha = Math.max(alpha, valeur);
				annulerTour(configuration, tour_ia);
				if (alpha >= beta) {
					break;
				}
			}
			return valeur;
		// Si on minimise.
   		} else {
   			int valeur = Integer.MAX_VALUE - 1;
   	   		ArrayList<Coup> coups_jouables = configuration.coupsPossibles(couleur_B);
   	   		ArrayList<ArrayList<Coup>> tours_jouables = toursPossibles(configuration, coups_jouables, couleur_B);
	   		for (ArrayList<Coup> tour_ia : tours_jouables) {
	   	   		if ((profondeur == profondeur_max-2)) {
	   	   			System.out.println("###### tour B prof 1 ######");
		   	   		afficheTour(tour_ia);
		   		}
				// On affiche le tour initial.
	   	   		//System.out.println("###### B ######");
	   	   		//afficheTour(tour_ia);
	   			// Debut calcul recursif.
				jouerTour(configuration, tour_ia);
				valeur = Math.min(valeur, donneCoupRec(configuration, profondeur, null, alpha, beta, true));
				// Fin calcul recursif.
				beta = Math.min(beta, valeur);
				annulerTour(configuration, tour_ia);
				if (beta <= alpha) {
					break;
				}
			}
	   		return valeur;
   		}
    }

	/**
     * Joue un tour (une sequence de coups).
     * @param configuration : un grille qui represente un configuration d'un plateau de jeu
     * @param tour : une sequence des coups a jouer
     */
    private void jouerTour(AireJeu configuration, ArrayList<Coup> tour_ia) {
		for (Coup coup_ia : tour_ia) {
			configuration.joueCoup(coup_ia);
		}
	}

    /**
     * Annule un tour (une sequence de coups).
     * @param configuration : un grille qui represente un configuration d'un plateau de jeu
     * @param tour : une sequence des coups a annuler
     */
    private void annulerTour(AireJeu configuration, ArrayList<Coup> tour_ia) {
		for (Coup coup_ia : tour_ia) {
			configuration.annulerCoup();
		}
	}

    /**
     * Evalue une configuration et l'assigne une valeur qui correspond a nombre de pions de certain couleur sur le plateau de jeu.
     * @param configuration : un grille qui represente un configuration d'un plateau de jeu
     * @param couleur : un couleur d'un des joueurs (noir ou blanc)
     * @return nombre de pions de certain couleur sur le plateau de jeu
     */
    protected int evaluation(AireJeu configuration) {
    	int pions_A = comptePions(configuration, couleur_A);
    	int pions_B = comptePions(configuration, couleur_B);
    	// Strategie debut de partie, quand nombre de pions de joueur IA > 10.
		if (pions_A > 7) {
			//System.out.println("Difference pions A - pions B.");
			//profondeur_max = 2;
			return pions_A - pions_B;
	    // Strategie fin de partie, quand il n'y a plus beacoup (<= changement_evaluation) de pions sur le plateau de jeu.
		} else {
			//System.out.println("Pions diagonals, pions A : " + pions_A+", couleur A : " + couleur_A);
			return pions_A - pions_B;
		}
    }


	private void afficheGrille(int[][] grille) {
		System.out.println("Grille:");
		for (int i = 0; i < AireJeu.NB_LIGNES; i++) {
			for (int j = 0; j < AireJeu.NB_COLONNES; j++) {
				System.out.print(grille[i][j]+" ");
			}
			System.out.println();
		}
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
     * Calcule la liste des tours jouables par un joueur a partir d'un liste des coups jouables.
     * @param configuration : la grille qui represente le plateau de jeu
     * @param coups_jouables : une sequence des coups jouables par un joueur
     * @param joueur : le joueur a qui est le tour
     * @return la liste des tours jouables
     */
    protected ArrayList<ArrayList<Coup>> toursPossibles(AireJeu configuration, ArrayList<Coup> coups_jouables, int joueur) {
    	ArrayList<ArrayList<Coup>> tours_jouables = new ArrayList<ArrayList<Coup>>();
    	// Ajout des coups avec choix.
    	Coup c_copie;
		for (Coup c : coups_jouables) {
			c_copie = c.copy();
			// Ajout d'un coup avec choix d'aspiration different.
			if (configuration.joueurDoitChoisir(c)) {
				if (c_copie.getAspiration()) {
					c_copie.setAspiration(false);
				} else {
					c_copie.setAspiration(true);
				}
				ArrayList<Coup> t1 = new ArrayList<Coup>();
				t1.add(c_copie);
				tours_jouables.add(t1);
			}
			// Ajout de tour (sequence de coups) avec un seul coup sans continuation.
			ArrayList<Coup> t0 = new ArrayList<Coup>();
			t0.add(c);
			tours_jouables.add(t0);
		}
		ArrayList<ArrayList<Coup>> tours_jouables_final = new ArrayList<ArrayList<Coup>>();
		for (ArrayList<Coup> tour : tours_jouables) {
			// completer tour avec les coups percusion/aspiration
			tours_jouables_final.add(tour);
			// Sauf si apres le coup initial le joueur peut continuer.
			tours_jouables_final.addAll(toursPossiblesRec(tour, configuration, joueur));
		}
		return tours_jouables_final;
	}

    /**
     * Calcule la liste des tours possibles recursivement.
     * @param tour : une sequence des coups possible
     * @param configuration : la grille qui represente le plateau de jeu
     * @param joueur : le joueur a qui est le tour
     * @return la liste des tours possibles
     */
    protected ArrayList<ArrayList<Coup>> toursPossiblesRec(ArrayList<Coup> tour, AireJeu configuration, int joueur) {
    	// Ajout des tours possibles dans la liste de tours jouables.
    	ArrayList<ArrayList<Coup>> tours_jouables = new ArrayList<ArrayList<Coup>>();
    	Coup c;
    	Coup c_possible;
		c = tour.get(tour.size()-1);
		configuration.joueCoup(c);
		//System.out.println("Joueur: "+joueur+", peut continuer: "+configuration.joueurPeutContinuerTour(c.getFin()));
		if (configuration.joueurPeutContinuerTour(c.getFin())) {
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
		} else {
			configuration.annulerCoup();
		}
		return tours_jouables;
	}
    
    /**
     * Copie une liste des coups.
     * @param coups : la liste des coups a copier
     * @return la copie de la liste des coups
     */
    public ArrayList<Coup> copyCoups(ArrayList<Coup> coups) {
    	ArrayList<Coup> coups_copie = new ArrayList<Coup>();
		for (Coup c : coups) {
			coups_copie.add(c);
		}
		return coups_copie;
    }

	/**
     * Genere un coup d'IA valide en utilisant la strategie alpha/beta.
     * @param debut : un position debut d'un coup d'IA
     * @return un Coup valide
     */
    public Coup donneCoup(Position debut) {
    	meilleur_valeur = Integer.MIN_VALUE;
    	meilleur_coup = null;
  		meilleurs_coups.clear();
        AireJeu aire = aire_jeu.copy();
        // Calcul des coups possibles.
    	ArrayList<Coup> coups_jouables_initials = aire.coupsPossibles(couleur_A);
    	ArrayList<Coup> coups_jouables = new ArrayList<Coup>();
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
        // Calcul des meilleurs coups.
    	donneCoupRec(aire, profondeur_max, coups_jouables, Integer.MIN_VALUE + 1, Integer.MAX_VALUE - 1, true);
        // Choisir un coup aleatoirement dans les meilleurs coups.
    	Random r = new Random(1);
		int index = (int)(r.nextFloat() * meilleurs_coups.size());
		System.out.println("MC: "+stringCoups(meilleurs_coups));
		return meilleurs_coups.get(index);
    }

	/**
	 * Affiche une sequence de coups.
	 * @param tour : sequence de coups
	 * @return : une chaine de caractere avec les coups
	 */
	public void afficheTour(ArrayList<Coup> tour) {
		System.out.println("Tour :");
		System.out.println(stringCoups(tour));
		System.out.println();
	}

	/**
	 * Transforme une liste de coups en chaine de charactere.
	 * @param liste : liste de coups
	 * @return : une chaine de caractere avec les coups
	 */
	public String stringCoups(ArrayList<Coup> liste) {
		String s = "";
		for(Coup c : liste) {
    		s += c.toStringEspace();
		}
		return s;
	}

}
