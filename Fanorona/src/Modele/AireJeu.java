package Modele;

import java.util.ArrayList;

/**
 * Classe principale de modele de jeu qui characterise l'aire de jeu. Elle a un grille de jeu 5 sur 9.
 * @author Petrulionyte Ieva, Yu Ran
 * @version 1.0
 */
public class AireJeu {
	public static final int BLANC = 1;
	public static final int NOIR = 2;
	public static final int NB_LIGNES = 5;
	public static final int NB_COLONNES = 9;
	// 0 - case vide, 1 - case blanc, 2 - case noir.
	private int[][] grille;
	private HistoriqueCoups historique;
	// 0 - case vide, 1 - case blanc, 2 - case noir.
	int joueur; // FAIRE CA + getter, ou coup actuel?
	/**
	* Ce attribut n'est pas null si l'utilisateur doit choisir entre l'aspiration et percusion.
	*/
	Coup choix_aspiration_percusion;


	/*******************/
	/** CONSTRUCTEURS **/
	/*******************/

	/**
	 * Constructeur d'aire de jeu avec la grille initial de fanorona.
	 */
	public AireJeu() {
		// Initialisation d'historique pour l'enregistrement de jeu.
		historique = new HistoriqueCoups();
		// Initialisation d'une grille de jeu.
		this.grille = new int[NB_LIGNES][NB_COLONNES];
		// Remplissage initial d'une grille de jeu.
		for (int i = 0; i < NB_LIGNES; i++) {
			for (int j = 0; j < NB_COLONNES; j++) {
				if (i < NB_LIGNES/2) {
					grille[i][j] = 2;
				} else {
					grille[i][j] = 1;
				}
			}
		}
		for (int j = 0; j < NB_COLONNES; j++) {
			if (j%2 == 1 && j < NB_COLONNES/2) {
				grille[NB_LIGNES/2][j] = 1;
			} else {
				grille[NB_LIGNES/2][j] = 2;
			}
		}
		grille[NB_LIGNES/2][6] = 1;
		grille[NB_LIGNES/2][8] = 1;
		// La position au centre est vide.
		grille[NB_LIGNES/2][NB_COLONNES/2] = 0;
		choix_aspiration_percusion = null;
	}

	/**
	 * Constructeur utilise pour faire un deep copie d'aire de jeu.
	 * @param copie_grille : une copie de grille de jeu
	 * @param historique_copie : une copie d'historique de jeu
	 * @param choix : le coup choix si l'utilisateur doit choisir entre l'aspiration et percusion
	 */
	public AireJeu(int[][] copie_grille, HistoriqueCoups historique_copie, Coup choix) {
		this.grille = copie_grille;
		this.historique = historique_copie;
		this.choix_aspiration_percusion = choix;
	}


	/******************************/
	/** VERIFICATION COUP VALIDE **/
	/******************************/

	/**
	 * Verifier si le coups peut etre jouer.
	 * @param coup : le coup a jouer
	 * @return vrai si le coup est valide, faux sinon
	 */
	public boolean coupValide(Coup coup) {
		Position debut = coup.getDebut();
		Position fin = coup.getFin();
		int direction_l = fin.getLigne()-debut.getLigne();
		int direction_c = fin.getColonne()-debut.getColonne();
		// Si la couleur que le joueur veux joueur ne correspond pas au couleur de pion.
		if (grille[debut.getLigne()][debut.getColonne()] != coup.getJoueur()) {
			//System.out.println("La couleur que le joueur veux joueur ne correspond pas au couleur de pion.");
			return false;
		}
		// Si la position debut ou fin n'est pas sur la grille.
		if ( !positionEstSurGrille(debut) || !positionEstSurGrille(fin) ) {
			//System.out.println("La position debut ou fin n'est pas sur la grille.");
			return false;
		}
		// Si la position debut et fin ne sont pas distincts.
		if ( debut.equals(fin) ) {
			//System.out.println("La position debut et fin ne sont pas distincts.");
			return false;
		}
		// Si la position fin n'est pas adjacente vide a la position debut.
		if ( !sontPositionsAdjacents(debut, fin) ) {
			//System.out.println("La position fin n'est pas adjacente vide a la position debut.");
			return false;
		}
		
		// Si le joueur jeue la meme direction comme son coup precedent.
		if ( (historique.getCoups()).size()>0 && memeDirectionSuiteCoups(coup) ) {
			//System.out.println("La direction de coup n'est pas compatible avec le coup precedent.");
			return false;
		}
		
		// Si le jouer essaie de revenir vers la meme position dans la suite de coups
		if ( (historique.getCoups()).size()>0 && memePositionDansSuiteCoups(coup) ) {
			//System.out.println("Le jouer essaie de revenir vers la meme position dans la suite de coups.");
			return false;
		}

		// S'il existe des coups avec des captures mais ce coup n'effectue pas de captures.
		if ( !coupFaitCapture(coup) && joueurPeutCapturer(coup.getJoueur())) {
			//System.out.println("Il y a des captures possible. Mais le coup ne fait pas de capture.");
			return false;
		}
		
		Position capture_devant = new Position((fin.getLigne()+direction_l), (fin.getColonne()+direction_c));
		// Si aspiration est vrai.
		if ( coup.getAspiration() ) {
			// S'il n'est pas possible de capturer celui devant.
			if (grille[capture_devant.getLigne()][capture_devant.getColonne()] == grille[debut.getLigne()][debut.getColonne()] ||
				grille[capture_devant.getLigne()][capture_devant.getColonne()] == 0) {
				return false;
			}
		}
		return (coup != null);
	}


	/****************/
	/** JOUER COUP **/
	/****************/

	/**
	 * Creation et execution d'un coup a utiliser pour le controleur, on suppose que le coup est valide.
	 * @param coup : le coup a jouer
	 */
	public void joueCoup(Coup coup) {
		// Si le joueur etait en train d'annuler les coups et recommence a jouer.
		if (historique.refaireCoupPossible()) { historique.resetCoupsAnnules(); }
		executeCoup(coup);
	}

	/**
	 * Execution d'un coup, on suppose qu'il est valide.
	 * @param coup : le coup a jouer
	 */
	public void executeCoup(Coup coup) {
		// Gere la capture des pions d'adversaire.
		coup.setPionsCaptures(effectueCapture(coup));
		// La case de debut devient vide.
		Position debut = coup.getDebut();
		grille[debut.getLigne()][debut.getColonne()] = 0;
		Position fin = coup.getFin();
		// Deplace le pion dans la case de fin.
		grille[fin.getLigne()][fin.getColonne()] = coup.getJoueur();
		historique.ajouterCoup(coup);
	}
	
	/**
	 * Execution d'un capture associe au coup.
	 * @param le coup a jouer
	 * @return les pions captures pendant ce coup
	 */
	public ArrayList<Position> effectueCapture(Coup coup) {
		ArrayList<Position> pions = new ArrayList<Position>();
		Position debut = coup.getDebut();
		Position fin = coup.getFin();
		// Direction dans la ligne.
		int direction_l = fin.getLigne()-debut.getLigne();
		// Direction dans la colonne.
		int direction_c = fin.getColonne()-debut.getColonne();
		// Couleur de pion dans la case de debut de coup.
		int couleur = grille[debut.getLigne()][debut.getColonne()];
		Position capture_devant = new Position((fin.getLigne()+direction_l), (fin.getColonne()+direction_c));
		Position capture_derriere = new Position((debut.getLigne()-direction_l), (debut.getColonne()-direction_c));
		// Si le joueur a le choix d'aspiration ou de percusion.
		if (joueurDoitChoisir(coup)) {
			// Si le joueur a choisi de faire l'aspiration.
			if ( coup.getAspiration() ) {
				pions.addAll(captureLigneAdversaire(capture_devant, couleur, direction_l, direction_c, 1));
			// Sinon, le joueur joue la percusion.
			} else {
				pions.addAll(captureLigneAdversaire(capture_derriere, couleur, direction_l, direction_c, -1));
			}
		// Si le joueur n'a pas de choix, alors on verifie les deux possibilites et effectue la seule possible.
		} else {
			// Si capture devant est possible.
			if (estAdversaire(couleur, capture_devant)) {
				pions.addAll(captureLigneAdversaire(capture_devant, couleur, direction_l, direction_c, 1));
			}
			// Si capture derriere est possible.
			if (estAdversaire(couleur, capture_derriere)) {
				pions.addAll(captureLigneAdversaire(capture_derriere, couleur, direction_l, direction_c, -1));
			}
		}
		return pions;
	}

	/**
	 * Renvoie vrai si le coup fait une capture.
	 * @param le coup a jouer
	 * @return vrai si le coup fait une capture, faux sinon
	 */
	public boolean coupFaitCapture(Coup coup) {
		Position debut = coup.getDebut();
		Position fin = coup.getFin();
		// Direction dans la ligne.
		int direction_l = fin.getLigne()-debut.getLigne();
		// Direction dans la colonne.
		int direction_c = fin.getColonne()-debut.getColonne();
		// Couleur de pion dans la case de debut de coup.
		int couleur = coup.getJoueur();
		int couleur_adversaire;
		if (couleur == BLANC) { couleur_adversaire = NOIR; } else { couleur_adversaire = BLANC; }
		Position capture_devant = new Position((fin.getLigne()+direction_l), (fin.getColonne()+direction_c));
		Position capture_derriere = new Position((debut.getLigne()-direction_l), (debut.getColonne()-direction_c));
		boolean resultat = false;
		if(positionEstSurGrille(capture_devant) && grille[capture_devant.getLigne()][capture_devant.getColonne()]==couleur_adversaire) {
			resultat = true;
		}
		if(positionEstSurGrille(capture_derriere) && grille[capture_derriere.getLigne()][capture_derriere.getColonne()]==couleur_adversaire) {
			resultat = true;
		}
		return resultat;
	}

	/**
	 * Renvoie vrai si dans la grille il y a des coups de ce joueur avec des captures possibles.
	 * @param le numero de joueur
	 * @return vrai si dans la grille il y a des coups de ce joueur avec des captures possibles, faux sinon
	 */
	public ArrayList<Coup> coupsPossibles(int joueur) {
		ArrayList<Coup> coups_possibles = new ArrayList<Coup>();
		Position debut;
		Coup coup;
		// Verifie si dans la grille il y a des coups de ce joueur avec des captures possibles.
		for (int l = 0; l < NB_LIGNES; l++) {
			for (int c = 0; c < NB_COLONNES; c++) {
				if (grille[l][c] == joueur) {
					debut = new Position(l, c);
					ArrayList<Position> voisins = positionsAdjacents(debut);
					for (Position fin : voisins) {
						coup = new Coup(debut, fin, joueur);
						if (coupValide(coup)) {
							coups_possibles.add(coup);
						}
					}
				}
			}
		}
		return coups_possibles;
	}

	/**
	 * Renvoie vrai si dans la grille il y a des coups de ce joueur avec des captures possibles.
	 * @param le numero de joueur
	 * @return vrai si dans la grille il y a des coups de ce joueur avec des captures possibles, faux sinon
	 */
	private boolean joueurPeutCapturer(int joueur) {
		Position debut;
		Coup coup;
		// Verifie si dans la grille il y a des coups de ce joueur avec des captures possibles.
		for (int l = 0; l < NB_LIGNES; l++) {
			for (int c = 0; c < NB_COLONNES; c++) {
				if (grille[l][c] == joueur) {
					debut = new Position(l, c);
					ArrayList<Position> voisins = positionsAdjacents(debut);
					for (Position fin : voisins) {
						coup = new Coup(debut, fin, joueur);
						if (coupFaitCapture(coup)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Renvoie vrai si le pion a des captures possibles.
	 * @param la position de pion
	 * @return vrai si le pion a des captures possibles, faux sinon
	 */
	public boolean joueurPeutContinuerTour(Position debut) {
		Coup coup;
		int joueur = grille[debut.getLigne()][debut.getColonne()];
		ArrayList<Position> voisins = positionsAdjacents(debut);
		for (Position fin : voisins) {
			coup = new Coup(debut, fin, joueur);
			//System.out.println("Je test : "+coup.getDebut()+coup.getFin()+".");
			if (coupFaitCapture(coup) && coupValide(coup)) {
				//System.out.println("Ca passe : "+coup.getDebut()+coup.getFin()+".");
				return true;
			}
		}
		return false;
	}

	/**
	 * Verifie si le joueur n'essaie pas de revenir vers la meme position dans sa suite des coups.
	 * @param le coup a jouer
	 * @return vrai si le joueur essaie de revenir vers la meme position, faux sinon
	 */
	private boolean memePositionDansSuiteCoups(Coup coup) {
		ArrayList<Coup> coups = historique.getCoups();
		Position destination = coup.getFin();
		//System.out.println("Destination : "+destination+".");
		int i = coups.size()-1;
		while (i >= 0 && (coups.get(i)).getJoueur() == coup.joueur) {
			if ((coups.get(i)).getFin().equals(destination)) {
				return true;
			}
			i--;
		}
		// Aucun coup dans la suite de coups de joueur.
		if (i == coups.size()-1) {
			return false;
		}
		// Un seul coup dans la suite de coups de joueur.
		if (i == coups.size()-2) {
			int j = coups.size()-1;
			//System.out.println("Le seul dans la suite : "+(coups.get(j)).getDebut()+(coups.get(j)).getFin()+".");
			return ((coups.get(j).getDebut()).equals(destination));
		}
		//System.out.println("Le dernier dans la suite : "+(coups.get(i+1)).getDebut()+(coups.get(i+1)).getFin()+".");
		return (((coups.get(i+1)).getDebut()).equals(destination));
	}

	/**
	 * Verifie si le coup n'est pas dans de meme joueur et dans le meme direction comme le coup precedent.
	 * @param coup : le coup a jouer
	 * @return vrai si le coup est de meme joueur et dans le meme direction, faux sinon
	 */
	private boolean memeDirectionSuiteCoups(Coup coup) {
		Coup dernier_coup = historique.getDernierCoup();
		Position debut_der_coup = dernier_coup.getDebut();
		Position fin_der_coup = dernier_coup.getFin();
		int joueur_der_coup = dernier_coup.getJoueur();
		// Direction de coup actuel.
		Position dir = (coup.getFin()).soustraire(coup.getDebut());
		// Direction de dernier coup.
		Position dir_der = fin_der_coup.soustraire(debut_der_coup);
		// Si c'est le meme joueur et le coup a la meme direction comme le coup precedent.
		if ( coup.getJoueur() == joueur_der_coup && (dir.equals(dir_der))) {
			return true;
		}
		return false;
	}

	/**
	 * Renvoie vrai si le joueur a le choix d'aspiration ou de percusion, faux sinon.
	 * @param un coup valide a verifier pour la possibilite de choix entre l'aspiration et la percusion
	 * @return vrai si le joueur a le choix, faux sinon
	 */
	public boolean joueurDoitChoisir(Coup coup) {
		Position debut = coup.getDebut();
		Position fin = coup.getFin();
		// Direction dans la ligne.
		int direction_l = fin.getLigne()-debut.getLigne();
		// Direction dans la colonne.
		int direction_c = fin.getColonne()-debut.getColonne();
		// Couleur de pion dans la case de debut de coup.
		int couleur = grille[debut.getLigne()][debut.getColonne()];
		// Positions possibles de capture des pions.
		Position capture_devant = new Position((fin.getLigne()+direction_l), (fin.getColonne()+direction_c));
		Position capture_derriere = new Position((debut.getLigne()-direction_l), (debut.getColonne()-direction_c));
		return (estAdversaire(couleur, capture_devant) && estAdversaire(couleur, capture_derriere));
	}

	/**
	 * Renvoie vrai si la position est sur la grille, faux sinon.
	 * @param position : la position a verifier
	 * @return vrai si la position est sur la grille de jeu, faux sinon
	 */
	public boolean positionEstSurGrille(Position position) {
		int ligne = position.getLigne();
		int colonne = position.getColonne();
		return (ligne >= 0 && ligne < NB_LIGNES && colonne >= 0 && colonne < NB_COLONNES);
	}

	/**
	 * Renvoie vrai si la ligne est sur la grille, faux sinon.
	 * @param ligne : la ligne a verifier
	 * @return vrai si la ligne est sur la grille de jeu, faux sinon
	 */
	public boolean ligneEstSurGrille(int ligne) {
		return (ligne >= 0 && ligne < NB_LIGNES);
	}

	/**
	 * Renvoie vrai si la colonne est sur la grille, faux sinon.
	 * @param colonne : la colonne a verifier
	 * @return vrai si la colonne est sur la grille de jeu, faux sinon
	 */
	public boolean colonneEstSurGrille(int colonne) {
		return (colonne >= 0 && colonne < NB_COLONNES);
	}
	
	/**
	 * Verifie si le coup est adjacent vide sur la grille de jeu.
	 * @param coup : le coup a jouer
	 * @return vrai si le coup est adjacent vide sur la grille de jeu, faux sinon
	 */
	private boolean sontPositionsAdjacents(Position debut, Position fin) {
		ArrayList<Position> positions_adjacents = positionsAdjacents(debut);
		// Cherche la position fin dans la liste des positions adjacents a la position debut.
		for (int i = 0; i < positions_adjacents.size(); i++) {
			Position pos = positions_adjacents.get(i);
			if (pos.equals(fin)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Renvoie la liste des coups adjacents vides sur la grille de jeu.
	 * @param coup : le coup
	 * @return liste des coups adjacents vides sur la grille de jeu
	 */
	public ArrayList<Position> positionsAdjacents(Position p) {		// Factoriser ca (boucle?).
		ArrayList<Position> positions = new ArrayList<Position>();
		int l = p.getLigne();
		int c = p.getColonne();
		// Case en bas.
		if (ligneEstSurGrille(l+1)) {
			if (grille[l+1][c] == 0)
				positions.add(new Position(l+1, c));
			// Si on a une case avec 8 voisins, autrement dit les deux ligne et colonne impairs ou pairs.
			if (c%2==1 && l%2==1 || c%2==0 && l%2==0) {
				// Case en bas a droite.
				if (colonneEstSurGrille(c+1)) {
					if (grille[l+1][c+1] == 0)
						positions.add(new Position(l+1, c+1));
				}
				// Case en bas a gauche.
				if (colonneEstSurGrille(c-1)) {
					if (grille[l+1][c-1] == 0)
						positions.add(new Position(l+1, c-1));
				}
			}
		}
		// Case en haut.
		if (ligneEstSurGrille(l-1)) {
			if (grille[l-1][c] == 0)
				positions.add(new Position(l-1, c));
			// Si on a une case avec 8 voisins, autrement dit les deux ligne et colonne impairs ou pairs.
			if (c%2==1 && l%2==1 || c%2==0 && l%2==0) {
				// Case en haut a droite.
				if (colonneEstSurGrille(c+1)) {
					if (grille[l-1][c+1] == 0)
						positions.add(new Position(l-1, c+1));
				}
				// Case en haut a gauche.
				if (colonneEstSurGrille(c-1)) {
					if (grille[l-1][c-1] == 0)
						positions.add(new Position(l-1, c-1));
				}
			}
		}
		// Case a droite.
		if (colonneEstSurGrille(c+1)) {
			if (grille[l][c+1] == 0)
				positions.add(new Position(l, c+1));
		}
		// Case a gauche.
		if (colonneEstSurGrille(c-1)) {
			if (grille[l][c-1] == 0)
				positions.add(new Position(l, c-1));
		}
		return positions;
	}
	
	/**
	 * Renvoie vrai si la case a verifier est l'adversaire de couleur donne, faux sinon.
	 * @param couleur de joueur
	 * @param case a verifier
	 * @return vrai si la case l'adversaire, faux sinon
	 */
	public boolean estAdversaire(int couleur_joueur, Position case_a_verifier) {
		return (positionEstSurGrille(case_a_verifier) && grille[case_a_verifier.getLigne()][case_a_verifier.getColonne()] != couleur_joueur &&
			grille[case_a_verifier.getLigne()][case_a_verifier.getColonne()] != 0);
	}
	
	/**
	 * Capture les pions d'adversaire associes a la capture d'un coup.
	 * @param case de premier pion a capturer
	 * @param couleur de joueur
	 * @param direction ligne de capture des pions
	 * @param direction colonne de capture des pions
	 * @param choix entre aspiration (1) et percusion (-1)
	 * @return les pions d'adversaire capturees
	 */
	public ArrayList<Position> captureLigneAdversaire(Position depart, int couleur, int direction_l, int direction_c, int choix) {
		Position position;
		ArrayList<Position> pions = new ArrayList<Position>();
		int l = depart.getLigne();
		int c = depart.getColonne();
		while ((l >= 0 && l < NB_LIGNES) && (c >= 0 && c < NB_COLONNES)) {
			position = new Position(l, c);
			if (estAdversaire(couleur, position)) {
				pions.add(position);
				grille[l][c] = 0;
			} else {
				break;
			}
			c = c + direction_c * choix;
			l = l + direction_l * choix;
		}
		return pions;
	}

	
	/*************/
	/** FIN JEU **/
	/*************/
	
	/**
	 * Renvoie vrai si le jeu est termine, faux sinon.
	 * @return vrai s'il reste que des pions d'un couleur dans la grille, faux sinon
	 */
	public boolean gameOver() {
		boolean noir = false;
		boolean blanc = false;
		// Verifie s'il y a les pions blancs et noirs sur la grille de jeu.
		for (int i = 0; i < NB_LIGNES; i++) {
			for (int j = 0; j < NB_COLONNES; j++) {
				if (grille[i][j] == 2) {
					noir = true;
				} else if (grille[i][j] == 1) {
					blanc = true;
				}
			}
		}
		// L'inverse de fait que les deux couleurs sont sur la grille de jeu.
		return !(noir && blanc);
	}

	
	/*************/
	/** GETTERS **/
	/*************/
	
	/**
	 * Renvoie le joueur actuel (noir ou blanc).
	 * @return le joueur actuel
	 */
	public int getJoueur() { return joueur; }
	
	/**
	 * Renvoie la grille de jeu.
	 * @return grille de jeu
	 */
	public int[][] getGrille() { return grille; }

	/**
	 * Renvoie le coup sur lequel un choix d'aspiration ou percusion est ettendu.
	 * @return le coup sur lequel un choix d'aspiration ou percusion est ettendu
	 */
	public Coup getChoixAspirationPercusion() {
		return choix_aspiration_percusion;
	}

	
	/*************/
	/** SETTERS **/
	/*************/

	/**
	 * Met a jour le joueur actuel.
	 * @param j : le joueur actuel
	 */
	public void setJoueur(int j) {
		this.joueur = j;
	}

	/**
	 * Met a jour le coup sur lequel un choix d'aspiration ou percusion est ettendu.
	 * @param le coup sur lequel un choix d'aspiration ou percusion est ettendu
	 */
	public void setChoixAspirationPercusion(Coup c) {
		choix_aspiration_percusion = c;
	}

	
	/**************************/
	/** ANNULER/REFAIRE COUP **/
	/**************************/
	
	/**
	 * Dit si l'annulation d'un coup est possible coups annules
	 * @return vrai s'il y a des coups a annuler, faux sinon
	 */
	public boolean annulationCoupPossible() {
		return historique.annulationCoupPossible();
	}
	
	/**
	 * Dit si c'est possible de refaire un coup
	 * @return vrai s'il y a des coups annules, faux sinon
	 */
	public boolean refaireCoupPossible() {
		return historique.refaireCoupPossible();
	}

	/**
	 * Annulation d'un coup.
	 */
	public void annulerCoup() {
		if (annulationCoupPossible()) {
			// On recupere le dernier coup dans la liste des coups de jeu.
			Coup coup = historique.enleveCoup();
			//System.out.println("Coup a annuler: "+coup);
			// La case de fin devient vide.
			Position fin = coup.getFin();
			grille[fin.getLigne()][fin.getColonne()] = 0;
			// Deplace le pion dans la case de debut.
			Position debut = coup.getDebut();
			grille[debut.getLigne()][debut.getColonne()] = coup.getJoueur();
			// Annulation des captures.
			int couleur_pion;
			if (coup.getJoueur() == 1) { couleur_pion = 2; } else { couleur_pion = 1; }
			for (Position p : coup.getPions()) {
				grille[p.getLigne()][p.getColonne()] = couleur_pion;
			}
			historique.ajouterCoupAnnule(coup);
		}
	}
	
	/**
	 * Execution d'un coup qui etait le dernier a etre annule.
	 */
	public void refaireCoup() {
		if (refaireCoupPossible()) {
			Coup coup = historique.enleveCoupAnnule();
			System.out.println("Coup refait: "+coup);
			executeCoup(coup);
		}
	}


	/***************/
	/** DEEP COPY **/
	/***************/
	
	/**
	 * Renvoie la copie de la classe AireJeu.
	 */
	public AireJeu copy() {
		int[][] copie_grille = copyGrille();
		HistoriqueCoups historique_copie = historique.copy();
		Coup choix = choix_aspiration_percusion;
		return new AireJeu(copie_grille, historique_copie, choix);
	}

	/**
     * Copy d'un grille qui represente un configuration d'un plateau de jeu
     * @param un grille qui represente un configuration d'un plateau de jeu
     * @return un copie profond d'un grille qui represente un configuration d'un plateau de jeu
     */
    public int[][] copyGrille() {
    	int[][] grille_copie = new int[NB_LIGNES][NB_COLONNES];
		for (int i = 0; i < NB_LIGNES; i++) {
			for (int j = 0; j < NB_COLONNES; j++) {
				grille_copie[i][j] = grille[i][j];
			}
		}
		return grille_copie;
    }


	/****************/
	/** HISTORIQUE **/
	/****************/
	
	/**
	 * Ecrit la liste des coups dans un fichier.
	 */
	public void sauvegarderHistoriqueCoups() {
		historique.exporter();
	}
	
	/**
	 * Lit un liste des coups dans un fichier.
	 * @param nom_fichier : fichier qui contien un liste des coups
	 */
	// sauvegarder aussi les coups annulees pour pouvoir refaire quand on reprend la partie
	public void chargeHistoriqueCoups(String nom_fichier) {
		historique.importer(nom_fichier);
		ArrayList<Coup> coups = historique.getCoups();
		for (Coup coup : coups) {
			// Gere la capture des pions d'adversaire.
			coup.setPionsCaptures(effectueCapture(coup));
			// La case de debut devient vide.
			Position debut = coup.getDebut();
			grille[debut.getLigne()][debut.getColonne()] = 0;
			Position fin = coup.getFin();
			// Deplace le pion dans la case de fin.
			grille[fin.getLigne()][fin.getColonne()] = coup.getJoueur();
		}
	}

}