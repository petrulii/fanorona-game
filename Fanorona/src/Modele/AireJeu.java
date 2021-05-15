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
	 * Execution d'un coup.
	 * @param coup : le coup a jouer
	 */
	public void joueCoup(Coup coup) {
		// Si le joueur etait en train d'annuler les coups et recommence a jouer.
		if (!coups_annules.isEmpty()) { coups_annules = new ArrayList<Coup>(); }
		// On verifie si le coup est valide.
		if (coupValide(coup)) {
			coups.add(coup);
			Position debut = coup.getDebut();
			Position fin = coup.getFin();
			int direction_l = fin.getLigne()-debut.getLigne();
			int direction_c = fin.getColonne()-debut.getColonne();
			int couleur = grille[debut.getLigne()][debut.getColonne()];
			grille[debut.getLigne()][debut.getColonne()] = 0;
			grille[fin.getLigne()][fin.getColonne()] = couleur;
			// Regler la capture des pions.
			Position capture_devant = new Position((fin.getLigne()+direction_l), (fin.getColonne()+direction_c));
			Position capture_derriere = new Position((debut.getLigne()-direction_l), (debut.getColonne()-direction_c));
			// Si aspiration est vrai.
			if ( coup.getAspiration() ) {
				grille[capture_devant.getLigne()][capture_devant.getColonne()] = 0;
			} else {
				if 		(grille[capture_devant.getLigne()][capture_devant.getColonne()] != couleur &&
						grille[capture_devant.getLigne()][capture_devant.getColonne()] != 0) {
							grille[capture_devant.getLigne()][capture_devant.getColonne()] = 0;
				} else {
					if (grille[capture_derriere.getLigne()][capture_derriere.getColonne()] != couleur &&
						grille[capture_devant.getLigne()][capture_devant.getColonne()] != 0) {
							grille[capture_derriere.getLigne()][capture_derriere.getColonne()] = 0;
					}
				}
			}
		}
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
		// Si la position debut ou fin n'est pas sur la grille.
		if ( !positionEstSurGrille(debut) || !positionEstSurGrille(fin) ) {
			return false;
		}
		// Si la position debut et fin ne sont pas distincts.
		if ( debut.equals(fin) ) {
			return false;
		}
		// Si la position fin n'est pas adjacente vide a la position debut.
		if ( !sontPositionsAdjacents(debut, fin) ) {
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
	 * Renvoie vrai si la position est sur la grille, faux sinon.
	 * @param position : la position a verifier
	 * @return vrai si la position est sur la grille de jeu, faux sinon
	 */
	public boolean positionEstSurGrille(Position position) {
		int ligne = position.getLigne();
		int colonne = position.getColonne();
		if (ligne < 0 || ligne > NB_LIGNES || colonne < 0 || colonne > NB_COLONNES) {
			return false;
		} else {
			return true;
		}
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
	private ArrayList<Position> positionsAdjacents(Position p) {
		ArrayList<Position> positions = new ArrayList<Position>();
		// Case en bas.
		if ( ((p.getLigne()+1 >= 0) && (p.getLigne()+1 <= NB_LIGNES))) {
			if (grille[p.getLigne()+1][p.getColonne()] == 0)
				positions.add(new Position(p.getLigne()+1, p.getColonne()));
			// Si on verifie une case avec 8 voisins, autrement dit les deux ligne et colonne impairs ou pairs.
			if (p.getColonne()%2==1 && p.getLigne()%2==1 || p.getColonne()%2==0 && p.getLigne()%2==0) {
				// Case en bas a droite.
				if ((p.getColonne()+1 >= 0) && (p.getColonne()+1 <= NB_COLONNES)) {
					if (grille[p.getLigne()+1][p.getColonne()+1] == 0)
						positions.add(new Position(p.getLigne()+1, p.getColonne()+1));
				}
				// Case en bas a gauche.
				if ((p.getColonne()-1 >= 0) && (p.getColonne()-1 <= NB_COLONNES)) {
					if (grille[p.getLigne()+1][p.getColonne()-1] == 0)
						positions.add(new Position(p.getLigne()+1, p.getColonne()-1));
				}
			}
		}
		// Case en haut.
		if ( ((p.getLigne()-1 >= 0) && (p.getLigne()-1 <= NB_LIGNES))) {
			if (grille[p.getLigne()-1][p.getColonne()] == 0)
				positions.add(new Position(p.getLigne()-1, p.getColonne()));
			// Si on verifie une case avec 8 voisins, autrement dit les deux ligne et colonne impairs ou pairs.
			if (p.getColonne()%2==1 && p.getLigne()%2==1 || p.getColonne()%2==0 && p.getLigne()%2==0) {
				// Case en haut a droite.
				if ((p.getColonne()+1 >= 0) && (p.getColonne()+1 <= NB_COLONNES)) {
					if (grille[p.getLigne()-1][p.getColonne()+1] == 0)
						positions.add(new Position(p.getLigne()-1, p.getColonne()+1));
				}
				// Case en haut a gauche.
				if ((p.getColonne()-1 >= 0) && (p.getColonne()-1 <= NB_COLONNES)) {
					if (grille[p.getLigne()-1][p.getColonne()-1] == 0)
						positions.add(new Position(p.getLigne()-1, p.getColonne()-1));
				}
			}
		}
		// Case a droite.
		if ((p.getColonne()+1 >= 0) && (p.getColonne()+1 <= NB_COLONNES)) {
			if (grille[p.getLigne()][p.getColonne()+1] == 0)
				positions.add(new Position(p.getLigne(), p.getColonne()+1));
		}
		// Case a gauche.
		if ((p.getColonne()-1 >= 0) && (p.getColonne()-1 <= NB_COLONNES)) {
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
	
	
	/* ********************************************* */
	/* ANNULER ET REFAIRE UN COUP, A FAIRE PLUS TARD */
	/* ********************************************* */
	
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
			Coup coup = coups.remove((coups.size() - 1));
			coups_annules.add(coup);
			//System.out.println("Coup annule: "+coup);
			// Ici il faut changer la grille pour que ca correspond a l'annulation d'un coup.
		}
	}
	
	/**
	 * Execution d'un coup qui etait le dernier a etre annule
	 */
	public void refaireCoup() {
		if (refaireCoupPossible()) {
			Coup coup = coups_annules.remove((coups_annules.size() - 1));
			coups.add(coup);
			//System.out.println("Coup refait: "+coup);
			joueCoup(coup);
		}
	}
	
	/**
	 * Affiche les coups
	 */
	public void afficheCoups() {
		System.out.print("( ");
		for(Coup c : coups) {
			System.out.print(c.toString() + " ");
		}
		System.out.println(")");
	}
	
	/**
	 * Affiche les coups annules
	 */
	public void afficheCoupsAnnules() {
		System.out.print("( ");
		for(Coup c : coups_annules) {
			System.out.print(c.toString() + " ");
		}
		System.out.println(")");
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
/*	public void sauvegarderHistoriqueCoups() {
		HistoriqueCoups.exporter(coups);
	}*/

}