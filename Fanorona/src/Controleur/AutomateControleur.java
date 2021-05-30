package Controleur;

import Modele.AireJeu;
import Modele.Position;
import Modele.Coup;

import Vue.MainGUI;
import Vue.AireGraphique;

import java.util.ArrayList;

public class AutomateControleur {

    protected final AireJeu aire_jeu;
    protected final AireGraphique aire_graphique;
    protected final MainGUI fenetre;

    // état courant de l'automate
    protected int etat_courant = -1;

    // vaut vrai si le joueur a fait au moins un coup lors de son tour
    protected Boolean premier_coup_est_effectue = false;
    // position cliquée en premier dans la grille (si état valide)
    protected Position position_debut;
    // dernier coup effectué, valeur valide seulement si : premier_coup_est_effectue == true
    protected Coup dernier_coup_effectue;

    /*
    	États de l'automate
     */
	protected static class E {
		public final static int
			// état initial pour lancer la machine
			INIT = 0,
			// état attente d'une action du joueur
			ATTENTE_ACTION = 1,
			// état lorsqu'un pion est maintenu mais qu'il ne peut pas être relaché sur la cellule courante (coup invalide sinon)
            RELACHEMENT_VALIDE_IMPOSSIBLE = 2,
			// état lorsqu'un pion est maintenu et qu'il peut être relaché sur la cellule courante (peut jouer un coup)
            RELACHEMENT_VALIDE_POSSIBLE = 3,
			// état lorsque l'automate attend du joueur qu'il choissise un type de coup (percussion ou aspiration)
            ATTENTE_CHOIX_TYPE_COUP = 4,
			// état lorsque le joueur souhaite terminer son tour (état final)
            FIN_TOUR = 5;
	}

	/*
		Instructions de transitions possibles de l'automate
	 */
	public static class T {
		public final static int
            PRESSION = 0,
            RELACHEMENT = 1,
            DRAG = 2,
            SURVOL = 3,
			TERMINER_TOUR = 4,
			CLIC = 5,
			CHOIX_ASPIRATION = 6,
			CHOIX_PERCUSSION = 7;
	}

	public AutomateControleur(AireJeu aj, AireGraphique ag, MainGUI f, int joueur_commence) {
		aire_jeu = aj;
		aire_graphique = ag;
		fenetre = f;

		changerJoueur(joueur_commence);
		initialiserTour();
	}

	/**
	 * Prépare l'automate pour le prochain tour
	 */
	protected void initialiserTour() {
		changerEtat(E.INIT);
	}

	/**
	 *
	 * @return le joueur actif de l'aire_jeu
	 */
	protected int getJoueurActif() {
		return aire_jeu.getJoueur();
	}

	/**
	 * Change de joueur en fonction du joueur actif
	 */
	protected void changerJoueur() {
		changerJoueur(getJoueurActif() == AireJeu.BLANC ? AireJeu.NOIR : AireJeu.BLANC);
	}

	/**
	 * Change de joueur en fonction d'un joueur désigné en paramètre
	 * et actualise la vue en conséquence
	 * @param j le numéro du joueur
	 */
	protected void changerJoueur(int j) {
		aire_jeu.setJoueur(j);
		fenetre.majAffichageJoueurActif();
	}

	/**
	 * Permet de passer d'un tour à un autre automatiquement
	 */
	protected void passerTourSuivant() {
		if(!aire_jeu.gameOver()) {
			changerJoueur();
			initialiserTour();
		} else {
			fenetre.afficherGameOver();
		}
	}

	/**
	 * Est appelée à chaque chancement d'état de l'automate
	 */
	protected void etatCourantAchange() {
		fenetre.majBoutonTerminer(premier_coup_est_effectue);
	}

	/**
	 * Permet de changer de changer d'état dans la machine et d'appliquer toutes les
	 * modifications nécessaires à la vue suite à ce changement.
	 * Attention : ne devrait pas être appelé autre part que dans la méthode "action()"
	 * @param etat_suivant un état suivant valide de la structure AutomateControleur.E
	 */
	protected void changerEtat(int etat_suivant) {
		boolean etat_a_change = etat_suivant != etat_courant;

		etat_courant = etat_suivant;

		if(etat_a_change) {

			switch(etat_suivant) {

				case E.INIT -> {
					System.out.println("INIT");
					premier_coup_est_effectue = false;
					changerEtat(E.ATTENTE_ACTION);
				}

				case E.ATTENTE_ACTION -> {
					System.out.println("ATTENTE_ACTION");
					if(premier_coup_est_effectue) {
						if(aire_jeu.joueurPeutContinuerTour(dernier_coup_effectue.getFin())) {
							ArrayList<Position> p = new ArrayList<>();
							p.add(dernier_coup_effectue.getFin());
							aire_graphique.afficherPionsDeplacables(p);
						}
					} else {
						aire_graphique.afficherPionsDeplacables(aire_jeu.positionsDebutCoupsPossibles(getJoueurActif()));
					}
					aire_graphique.setCurseurMaintienPion(false);
					aire_graphique.afficherCasesAccessibles(null);
					aire_graphique.setCurseurMaintienPionTemporaire(false);
				}

				case E.RELACHEMENT_VALIDE_IMPOSSIBLE -> {
					System.out.println("RELACHEMENT_VALIDE_IMPOSSIBLE");
					aire_graphique.afficherPionsDeplacables(null);
					//aire_graphique.afficherPionsSupprimables(null);
					aire_graphique.setCurseurMaintienPion(true);
					aire_graphique.afficherCasesAccessibles(aire_jeu.positionsFinsCoupsPossibles(getJoueurActif(), position_debut));
					aire_graphique.setCurseurMagnetisme(false);
				}

				case E.RELACHEMENT_VALIDE_POSSIBLE -> {
					System.out.println("RELACHEMENT_VALIDE_POSSIBLE");
					//aire_graphique.afficherPionsSupprimables(aire_jeu.get);
					aire_graphique.setCurseurMagnetisme(true);
				}

				case E.ATTENTE_CHOIX_TYPE_COUP -> {
					System.out.println("ATTENTE_CHOIX_TYPE_COUP");
					aire_graphique.setCurseurMaintienPion(false);
					aire_graphique.setCurseurMaintienPionTemporaire(true);
					// aire_graphique.afficherCasesAccessibles(false, null);
					// aire_graphique.afficherPionsSupprimables(false, liste);
				}

				case E.FIN_TOUR -> {
					System.out.println("FIN_TOUR");
					aire_graphique.emptyCheminJoueur();
					passerTourSuivant();
				}

			}

			etatCourantAchange();
		}

	}

	/**
	 * Applique une transition d'un joueur humain à l'automate
	 * @param transition une transition valide de la structure AutomateControleur.T
	 */
	public void actionJoueur(int transition) {
		actionJoueur(transition, 0, 0);
	}

	/**
	 * Applique une transition d'un joueur humain à l'automate
	 * @param transition une transition valide de la structure AutomateControleur.T
	 * @param x l'abscisse des coordonnées du pointeur
	 * @param y l'ordonnée des coordonnées du pointeur
	 */
	public void actionJoueur(int transition, int x, int y) {
		action(transition, x, y);
	}

	/**
	 * Applique une transition à l'automate
	 * @param transition une transition valide de la structure AutomateControleur.T
	 * @param x l'abscisse des coordonnées du pointeur
	 * @param y l'ordonnée des coordonnées du pointeur
	 */
	protected void action(int transition, int x, int y) {
		Position position_curseur = aire_graphique.coordonneesVersPosition(x, y);

		switch(etat_courant) {

			case E.ATTENTE_ACTION -> {
                if (
                		transition == T.PRESSION
                		&& aire_graphique.collisionZonePion(x, y)
						&& aire_jeu.getCaseGrille(position_curseur) == getJoueurActif()
						&& (!premier_coup_est_effectue || position_curseur.equals(dernier_coup_effectue.getFin()))
						//&& (!premier_coup_est_effectue || aire_jeu.joueurPeutContinuerTour(dernier_coup_effectue.getFin()))
						//|| (premier_coup_est_effectue && position_curseur.equals(dernier_coup_effectue.getFin()))
				) {
                	position_debut = position_curseur;
                	changerEtat(E.RELACHEMENT_VALIDE_IMPOSSIBLE);
                } else if (transition == T.TERMINER_TOUR && premier_coup_est_effectue) {
                	changerEtat(E.FIN_TOUR);
				}
            }

			case E.RELACHEMENT_VALIDE_IMPOSSIBLE -> {
				if (transition == T.RELACHEMENT) {
					changerEtat(E.ATTENTE_ACTION);
				} else if (transition == T.DRAG) {
					if (
							aire_graphique.collisionZonePion(x, y)
							&& aire_jeu.coupValide(new Coup(position_debut, position_curseur, getJoueurActif()))
					) {
						changerEtat(E.RELACHEMENT_VALIDE_POSSIBLE);
					}
				}
			}

			case E.RELACHEMENT_VALIDE_POSSIBLE -> {
				Coup coup_temp = new Coup(position_debut, position_curseur, getJoueurActif());
				boolean coup_est_valide = aire_jeu.coupValide(coup_temp);
				if(transition == T.DRAG) {
					if(!coup_est_valide)
						changerEtat(E.RELACHEMENT_VALIDE_IMPOSSIBLE);
				} else if(transition == T.RELACHEMENT) {
					if(coup_est_valide) {
						if(aire_jeu.joueurDoitChoisir(coup_temp)) {
							aire_jeu.setChoixAspirationPercusion(coup_temp);
							changerEtat(E.ATTENTE_CHOIX_TYPE_COUP);
						} else {
							aire_jeu.joueCoup(coup_temp);
							dernier_coup_effectue = coup_temp;

							if(!premier_coup_est_effectue) {
								premier_coup_est_effectue = true;
								aire_graphique.ajouterACheminJoueur(position_debut);
							}
							aire_graphique.ajouterACheminJoueur(dernier_coup_effectue.getFin());

							changerEtat(E.ATTENTE_ACTION);
						}
					}
				}
			}

			case E.ATTENTE_CHOIX_TYPE_COUP -> {
				Coup coup_temp = aire_jeu.getChoixAspirationPercusion();
				boolean choix_est_effectue = true;

				if(transition == T.CHOIX_ASPIRATION) {
					coup_temp.setAspiration(true);
				} else if(transition == T.CHOIX_PERCUSSION) {
					coup_temp.setAspiration(false);
				} else if(transition == T.CLIC) {
					// TODO : détection type coup
					coup_temp.setAspiration(true);
				} else {
					choix_est_effectue = false;
				}

				if(choix_est_effectue && aire_jeu.coupValide(coup_temp)) {
					aire_jeu.setChoixAspirationPercusion(null);

					aire_jeu.joueCoup(coup_temp);
					dernier_coup_effectue = coup_temp;

					if(!premier_coup_est_effectue) {
						premier_coup_est_effectue = true;
						aire_graphique.ajouterACheminJoueur(position_debut);
					}
					aire_graphique.ajouterACheminJoueur(dernier_coup_effectue.getFin());

					changerEtat(E.ATTENTE_ACTION);

				}
			}

		}

		aire_graphique.setCurseur(position_debut, x, y);
		aire_graphique.repaint();
	}

}