package Modele;

import java.util.ArrayList;

/**
 * Classe principale de modele de jeu qui characterise l'aire de jeu. Elle a un grille de jeu 5 sur 9.
 * @author Petrulionyte Ieva, Yu Ran
 * @version 1.0
 */
public class AireJeu {
	final int NB_LIGNES = 5;
	final int NB_COLONNES = 9;
	final int BLANC = 1;
	final int NOIR = 2;
	// 0 - case vide, 1 - case blanc, 2 - case noir.
	public int[][] grille;
	/**
	* Liste pour enregistrer les coups.
	*/
	ArrayList<Coup> coups;
	/**
	* Liste pour se souvenir des coups annules en cours d'annulation.
	*/
	ArrayList<Coup> coups_annules;

	public AireJeu() {
		// Initialisation d'un liste pour l'enregistrement des coups.
		coups = new ArrayList<Coup>();
		// Initialisation d'un liste pour l'annulation des coups.
		coups_annules = new ArrayList<Coup>();
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
	}

	/**
	 * Creation et execution d'un coup a utiliser pour le controleur, on suppose que le coup est valide.
	 * @param coup : le coup a jouer
	 */
	public void joueCoup(Coup coup) {
		// Si le joueur etait en train d'annuler les coups et recommence a jouer.
		if (!coups_annules.isEmpty()) { coups_annules = new ArrayList<Coup>(); }
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
		coups.add(coup);
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
			System.out.println("La couleur que le joueur veux joueur ne correspond pas au couleur de pion.");
			return false;
		}
		// Si la position debut ou fin n'est pas sur la grille.
		if ( !positionEstSurGrille(debut) || !positionEstSurGrille(fin) ) {
			System.out.println("La position debut ou fin n'est pas sur la grille.");
			return false;
		}
		// Si la position debut et fin ne sont pas distincts.
		if ( debut.equals(fin) ) {
			System.out.println("La position debut et fin ne sont pas distincts.");
			return false;
		}
		// Si la position fin n'est pas adjacente vide a la position debut.
		if ( !sontPositionsAdjacents(debut, fin) ) {
			System.out.println("La position fin n'est pas adjacente vide a la position debut.");
			return false;
		}
		
		// Si le joueur jeue la meme direction comme son coup precedent.
		if ( coups.size()>1 && memeDirectionSuiteCoups(coup) ) {
			System.out.println("La direction de coup n'est pas compatible avec le coup precedent.");
			return false;
		}
		
		// Si le jouer essaie de revenir vers la meme position dans la suite de coups
		if ( coups.size()>0 && memePositionDansSuiteCoups(coup) ) {
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
		return true;
	}

	/**
	 * Verifie si le joueur n'essaie pas de revenir vers la meme position dans sa suite des coups.
	 * @param le coup a jouer
	 * @return vrai si le joueur essaie de revenir vers la meme position, faux sinon
	 */
	private boolean memePositionDansSuiteCoups(Coup coup) {
		int i = coups.size()-1;
		while (i >= 0 && (coups.get(i)).getJoueur() == coup.joueur) {
			System.out.println("coup i : "+coups.get(i)+", i : "+1+", coup : "+coup);
			if ((coups.get(i)).getFin() != coup.getFin() && (coups.get(i)).getDebut() != coup.getFin()) {
				return true;
			}
			i--;
		}
		return false;
	}

	/**
	 * Verifie si le coup n'est pas dans de meme joueur et dans le meme direction comme le coup precedent.
	 * @param coup : le coup a jouer
	 * @return vrai si le coup est de meme joueur et dans le meme direction, faux sinon
	 */
	private boolean memeDirectionSuiteCoups(Coup coup) {
		Coup dernier_coup = coups.get(coups.size()-1);
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
		return !(ligne < 0 || ligne > NB_LIGNES || colonne < 0 || colonne > NB_COLONNES);
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
	private ArrayList<Position> positionsAdjacents(Position p) {		// Factoriser ca (boucle).
		ArrayList<Position> positions = new ArrayList<Position>();
		// Case en bas.
		if ( ((p.getLigne()+1 >= 0) && (p.getLigne()+1 < NB_LIGNES))) {
			if (grille[p.getLigne()+1][p.getColonne()] == 0)
				positions.add(new Position(p.getLigne()+1, p.getColonne()));
			// Si on verifie une case avec 8 voisins, autrement dit les deux ligne et colonne impairs ou pairs.
			if (p.getColonne()%2==1 && p.getLigne()%2==1 || p.getColonne()%2==0 && p.getLigne()%2==0) {
				// Case en bas a droite.
				if ((p.getColonne()+1 >= 0) && (p.getColonne()+1 < NB_COLONNES)) {
					if (grille[p.getLigne()+1][p.getColonne()+1] == 0)
						positions.add(new Position(p.getLigne()+1, p.getColonne()+1));
				}
				// Case en bas a gauche.
				if ((p.getColonne()-1 >= 0) && (p.getColonne()-1 < NB_COLONNES)) {
					if (grille[p.getLigne()+1][p.getColonne()-1] == 0)
						positions.add(new Position(p.getLigne()+1, p.getColonne()-1));
				}
			}
		}
		// Case en haut.
		if ( ((p.getLigne()-1 >= 0) && (p.getLigne()-1 < NB_LIGNES))) {
			if (grille[p.getLigne()-1][p.getColonne()] == 0)
				positions.add(new Position(p.getLigne()-1, p.getColonne()));
			// Si on verifie une case avec 8 voisins, autrement dit les deux ligne et colonne impairs ou pairs.
			if (p.getColonne()%2==1 && p.getLigne()%2==1 || p.getColonne()%2==0 && p.getLigne()%2==0) {
				// Case en haut a droite.
				if ((p.getColonne()+1 >= 0) && (p.getColonne()+1 < NB_COLONNES)) {
					if (grille[p.getLigne()-1][p.getColonne()+1] == 0)
						positions.add(new Position(p.getLigne()-1, p.getColonne()+1));
				}
				// Case en haut a gauche.
				if ((p.getColonne()-1 >= 0) && (p.getColonne()-1 < NB_COLONNES)) {
					if (grille[p.getLigne()-1][p.getColonne()-1] == 0)
						positions.add(new Position(p.getLigne()-1, p.getColonne()-1));
				}
			}
		}
		// Case a droite.
		if ((p.getColonne()+1 >= 0) && (p.getColonne()+1 < NB_COLONNES)) {
			if (grille[p.getLigne()][p.getColonne()+1] == 0)
				positions.add(new Position(p.getLigne(), p.getColonne()+1));
		}
		// Case a gauche.
		if ((p.getColonne()-1 >= 0) && (p.getColonne()-1 < NB_COLONNES)) {
			if (grille[p.getLigne()][p.getColonne()-1] == 0)
				positions.add(new Position(p.getLigne(), p.getColonne()-1));
		}
		return positions;
	}
	
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
		// L'inverse de fait qu'il soit les deux couleurs sur la grille de jeu.
		return !(noir && blanc);
	}
	
	/**
	 * Renvoie vrai si la case a verifier est l'adversaire de couleur donne, faux sinon.
	 * @param couleur de joueur
	 * @param case a verifier
	 * @return vrai si la case l'adversaire, faux sinon
	 */
	public boolean estAdversaire(int couleur_joueur, Position case_a_verifier) {
		if (case_a_verifier.getLigne() < 0 || case_a_verifier.getLigne() >= NB_LIGNES) {
			return false;
		}
		if (case_a_verifier.getColonne() < 0 || case_a_verifier.getColonne() >= NB_COLONNES) {
			return false;
		}
		if (grille[case_a_verifier.getLigne()][case_a_verifier.getColonne()] != couleur_joueur &&
			grille[case_a_verifier.getLigne()][case_a_verifier.getColonne()] != 0) {
			return true;
		} else {
			return false;
		}
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
			//System.out.println("direction c : "+direction_c+", direction l : "+direction_l+", l : "+l+", c : "+c);
			position = new Position(l, c);
			if (estAdversaire(couleur, position)) {
				pions.add(position);
				//System.out.println("Ajout dans pions : "+position);
				grille[l][c] = 0;
			} else {
				break;
			}
			c = c + direction_c * choix;
			l = l + direction_l * choix;
		}
		return pions;
	}

	/**
	 * Renvoie la grille de jeu.
	 * @return grille de jeu
	 */
	public int[][] getGrille() { return grille; }
	
	/**
	 * Renvoie le nombre de lignes dans la grille de jeu.
	 * @return le nombre de lignes
	 */
	public int getNbLignes() { return NB_LIGNES; }
	/**
	 * Renvoie le nombre de colonnes dans la grille de jeu.
	 * @return le nombre de colonnes
	 */
	public int getNbColonnes() { return NB_COLONNES; }
	
	/**
	 * Dit si l'annulation d'un coup est possible coups annules
	 * @return vrai s'il y a des coups a annuler, faux sinon
	 */
	public boolean annulationCoupPossible() {
		return (!coups.isEmpty());
	}
	
	/**
	 * Dit si c'est possible de refaire un coup
	 * @return vrai s'il y a des coups annules, faux sinon
	 */
	public boolean refaireCoupPossible() {
		return (!coups_annules.isEmpty());
	}

	/**
	 * Annulation d'un coup
	 */
	public void annulerCoup() {
		if (annulationCoupPossible()) {
			// On recupere le dernier coup dans la liste des coups de jeu.
			Coup coup = coups.remove((coups.size() - 1));
			System.out.println("Coup a annuler: "+coup);
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
			coups_annules.add(coup);
			afficheCoups();
			afficheCoupsAnnules();
		}
	}
	
	/**
	 * Execution d'un coup qui etait le dernier a etre annule
	 */
	public void refaireCoup() {
		if (refaireCoupPossible()) {
			Coup coup = coups_annules.remove((coups_annules.size() - 1));
			System.out.println("Coup refait: "+coup);
			executeCoup(coup);
			afficheCoups();
			afficheCoupsAnnules();
		}
	}
	
	/**
	 * Lit un liste des coups dans un fichier
	 * @param nom_fichier : fichier qui contien un liste des coups
	 */
/*	public ArrayList<Coup> chargeHistoriqueCoups(String nom_fichier) {
		return HistoriqueCoups.importer(nom_fichier);
	}*/
	
	/**
	 * Ecrit la liste des coups dans un fichier
	 */
	public void sauvegarderHistoriqueCoups() {
		HistoriqueCoups.exporter(coups);
	}
	
	/**
	 * Affiche les coups
	 */
	public void afficheCoups() {
		System.out.print("Coups de jeu : ( ");
		for(Coup c : coups) {
			System.out.print(c.toString() + " ");
		}
		System.out.println(")");
	}
	
	/**
	 * Affiche les coups annules
	 */
	public void afficheCoupsAnnules() {
		System.out.print("Coups annules : ( ");
		for(Coup c : coups_annules) {
			System.out.print(c.toString() + " ");
		}
		System.out.println(")");
	}

}