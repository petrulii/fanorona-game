package Modele;

import java.util.ArrayList;

/**
 * Classe principale de modele de jeu qui characterise l'aire de jeu.
 * @author Petrulionyte Ieva, Yu Ran
 * @version 1.0
 */
public class AireJeu {
	final int NB_LIGNES = 5;
	final int NB_COLONNES = 9;
	// 0 - case vide, 1 - case blanc, 2 - case noir.
	public int[][] grille;
	/**
	* liste pour enregistrer les coups
	*/
	ArrayList<Coup> coups;
	/**
	* liste pour se souvenir des coups annules en cours d'annulation
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
			if (j%2 == 1) {
				grille[NB_LIGNES/2][j] = 1;
			} else {
				grille[NB_LIGNES/2][j] = 2;
			}
		}
		// La position au centre est vide.
		grille[NB_LIGNES/2][NB_COLONNES/2] = 0;
	}

	/**
	 * Execution d'un coup
	 * @param coup : le coup a jouer
	 */
	public void joueCoup(Coup coup) {
		// Si le joueur etait en train d'annuler les coups et recommence a jouer.
		if (!coups_annules.isEmpty()) { coups_annules = new ArrayList<Coup>(); }
		// On verifie si le coup est valide.
		if (coupValide(coup)) {
			coups.add(coup);
			joueCoup(coup);
		}
	}
	
	/**
	 * Verifier si le coups peut etre jouer
	 * @param l : le coup a jouer
	 * @return vrai si le coup est valide, faux sinon
	 */
	private boolean coupValide(Coup coup) {
		// LES CHOSES A VERIFIER POUR QUE LE COUP SOIT VALIDE :
		// Le debut et la fin ne depasse pas la grille.
		// Le debut et la fin sont distincts.
		// Le debut et adjacent a la fin.
		// Si aspiration est vrai, alors il y a la possibilite de capturer celui devant.
		// Si aspiration est faux, alors il y a la possibilite de capturer celui derriere.
		return false;
	}
	
	private boolean sontCoupsAdjacents(Coup fin, Coup debut) {
		ArrayList<Coup> coups_adjacents = coupsAdjacents(debut);
		return coups_adjacents.contains(fin);
	}
	
	private ArrayList<Coup> coupsAdjacents(Coup coup) {
		return null;
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
	 * Renvoie la grille de jeu
	 * @return grille de jeu
	 */
	public int[][] getGrille() { return grille; }
	
	/**
	 * Renvoie le nombre de lignes dans la grille de jeu
	 * @return le nombre de lignes
	 */
	public int getNbLignes() { return NB_LIGNES; }
	/**
	 * Renvoie le nombre de colonnes dans la grille de jeu
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
	public ArrayList<Coup> chargeHistoriqueCoups(String nom_fichier) {
		return HistoriqueCoups.importer(nom_fichier);
	}
	
	/**
	 * Ecrit la liste des coups dans un fichier
	 */
	public void sauvegarderHistoriqueCoups() {
		HistoriqueCoups.exporter(coups);
	}

}