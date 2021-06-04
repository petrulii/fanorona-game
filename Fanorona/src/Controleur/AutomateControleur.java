package Controleur;

import Modele.AireJeu;
import Modele.Position;
import Modele.Coup;

import Vue.MainGUI;
import Vue.AireGraphique;

import java.util.ArrayList;
import java.util.Collections;

public class AutomateControleur {

    protected final AireJeu aire_jeu;
    protected final AireGraphique aire_graphique;
    protected final MainGUI fenetre;

    // état courant de l'automate
    protected int etat_courant = -1;

    // vaut vrai si le joueur a fait au moins un coup lors de son tour
    protected boolean premier_coup_est_effectue = false;
    // vaut vrai si le premier coup a engendré une capture
    protected boolean premier_coup_a_effectue_capture = false;
    // position cliquée en premier dans la grille (si état valide)
    protected Position position_debut;
    // dernier coup effectué, valeur valide seulement si : premier_coup_est_effectue == true
    protected Coup coup_valide_possible, dernier_coup_effectue;
    // listes des positions cliquables dans le cas où un choix de type de coup est attendu
	protected ArrayList<ArrayList<Position>> listes_positions_choix_type_coup;
	protected int choix_type_coup_survole;

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
			// mais que son curseur n'est pas positionné sur un élément cliquable
            ATTENTE_CHOIX_TYPE_COUP = 4,
			// état lorsque l'automate attend du joueur qu'il clique sur l'élément cliquable pour choisir le type de coup
			ATTENTE_CHOIX_TYPE_COUP_POSSIBLE = 5,
			// état lorsque le joueur souhaite terminer son tour (état final)
            FIN_TOUR = 6,
			// état final bloquant pour forcer la fin d'une partie
			FIN_PARTIE = 7;
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
			CLIC = 5;
	}

	public AutomateControleur(AireJeu aj, AireGraphique ag, MainGUI f, int joueur_commence) {
		aire_jeu = aj;
		aire_graphique = ag;
		fenetre = f;

		changerJoueur(joueur_commence);
		initialiserTour();
	}

	/**
	 * Permet d'assurer la fin de la partie courante (action irréversible pour l'instance !)
	 */
	public void terminerPartieDeForce() {
		changerEtat(E.FIN_PARTIE);
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
		fenetre.majAffichageJoueurActif(false);
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
		fenetre.majBoutonTerminer(
				premier_coup_est_effectue
				&& etat_courant != E.ATTENTE_CHOIX_TYPE_COUP
		);
		fenetre.majBoutonHistorique(etat_courant != E.ATTENTE_CHOIX_TYPE_COUP);
	}


	protected void changerEtat(int etat_suivant) {
		changerEtat(etat_suivant, false);
	}

	/**
	 * Permet de changer de changer d'état dans la machine et d'appliquer toutes les
	 * modifications nécessaires à la vue suite à ce changement.Attention : ne devrait pas être appelé autre part que dans la méthode "action()"
	 * @param etat_suivant un état suivant valide de la structure AutomateControleur.E
         * @param forcer_changement si le changement est forcé et indépendant de l’état de la machine
	 */
	protected void changerEtat(int etat_suivant, boolean forcer_changement) {
		boolean etat_a_change = etat_suivant != etat_courant;

		etat_courant = etat_suivant;

		if(etat_a_change || forcer_changement) {

			switch(etat_suivant) {

				case E.INIT: {
					//System.out.println("INIT");
					premier_coup_est_effectue = false;
					premier_coup_a_effectue_capture = false;
					coup_valide_possible = null;
					dernier_coup_effectue = null;
					position_debut = null;
					changerEtat(E.ATTENTE_ACTION);
					break;
				}

				case E.ATTENTE_ACTION: {
					//System.out.println("ATTENTE_ACTION");
					fenetre.accentSurBoutonTerminer(false);
					if(premier_coup_est_effectue) {
						if(
								premier_coup_a_effectue_capture
								&& aire_jeu.joueurPeutContinuerTour(dernier_coup_effectue.getFin())
						) {
							aire_graphique.setPionsDeplacables(new ArrayList<>(Collections.singletonList(dernier_coup_effectue.getFin())));
						} else {
							fenetre.accentSurBoutonTerminer(true);
						}
					} else {
						aire_graphique.setPionsDeplacables(aire_jeu.positionsDebutCoupsPossibles());
					}
					aire_graphique.setPionsSupprimables(null);
					aire_graphique.setCasesAccessibles(null);
					aire_graphique.setCurseurMaintienPion(false);
					aire_graphique.setCurseurMaintienPionTemporaire(false);
					aire_graphique.setChoixTypeCoups(null);
					aire_graphique.setCheminJoueur(aire_jeu.listeHistoriquePosTourCourant(getJoueurActif()));
					break;
				}

				case E.RELACHEMENT_VALIDE_IMPOSSIBLE: {
					//System.out.println("RELACHEMENT_VALIDE_IMPOSSIBLE");
					aire_graphique.setPionsDeplacables(null);
					aire_graphique.setPionsSupprimables(null);
					aire_graphique.setCurseurMaintienPion(true);
					aire_graphique.setCasesAccessibles(aire_jeu.positionsFinsCoupsPossibles(getJoueurActif(), position_debut));
					aire_graphique.setCurseurMagnetisme(false);
					break;
				}

				case E.RELACHEMENT_VALIDE_POSSIBLE: {
					//System.out.println("RELACHEMENT_VALIDE_POSSIBLE");
					aire_graphique.setPionsSupprimables(aire_jeu.listeCapturesCoup(coup_valide_possible));
					aire_graphique.setCurseurMagnetisme(true);
					break;
				}

				case E.ATTENTE_CHOIX_TYPE_COUP: {
					//System.out.println("ATTENTE_CHOIX_TYPE_COUP");
					aire_graphique.setCurseurMaintienPion(false);
					aire_graphique.setCurseurMaintienPionTemporaire(true);
					aire_graphique.setPionsSupprimables(null);
					aire_graphique.setCasesAccessibles(null);
					aire_graphique.setChoixTypeCoups(listes_positions_choix_type_coup);
					break;
				}

				case E.ATTENTE_CHOIX_TYPE_COUP_POSSIBLE : {
					//System.out.println("ATTENTE_CHOIX_TYPE_COUP_POSSIBLE");
					ArrayList<ArrayList<Position>> listes_pions_supprimables_temp = new ArrayList<>(1);
					listes_pions_supprimables_temp.add(listes_positions_choix_type_coup.get(choix_type_coup_survole));
					aire_graphique.setPionsSupprimables(listes_pions_supprimables_temp);
					break;
				}

				case E.FIN_TOUR: {
					//System.out.println("FIN_TOUR");
					fenetre.accentSurBoutonTerminer(false);
					passerTourSuivant();
					break;
				}

			}

			etatCourantAchange();
		}

	}

	public void annulerCoup() {
		majApresChangementHistorique(aire_jeu.annulerCoup());
		fenetre.majAffichageJoueurActif(false);
	}

	public void refaireCoup() {
		majApresChangementHistorique(aire_jeu.refaireCoup());
		fenetre.majAffichageJoueurActif(false);
	}

	public void majApresChangementHistorique(Coup coup_restaure) {
		if(coup_restaure != null) {
			int nb_coups_effectues = aire_jeu.listeHistoriquePosTourCourant(getJoueurActif()).size();
			premier_coup_est_effectue = nb_coups_effectues >= 1;
			premier_coup_a_effectue_capture =
						premier_coup_est_effectue
						&& (nb_coups_effectues > 1 || coup_restaure.getPions().size() > 0);
			dernier_coup_effectue = aire_jeu.getDernierCoup();

			changerEtat(E.ATTENTE_ACTION, true);
			aire_graphique.repaint();
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

			case E.ATTENTE_ACTION: {
                if (
                		transition == T.PRESSION
                		&& aire_graphique.collisionZonePion(x, y)
						&& aire_jeu.getCaseGrille(position_curseur) == getJoueurActif()
						&& (
								aire_jeu.joueurPeutContinuerTour(position_curseur)
								|| (!aire_jeu.joueurPeutFaireCoup() && aire_jeu.joueurPeutContinuerTourSansCapture(position_curseur))
						)
						&& (!premier_coup_est_effectue || position_curseur.equals(dernier_coup_effectue.getFin()))
						&& (!premier_coup_est_effectue || premier_coup_a_effectue_capture)
				) {
                	position_debut = position_curseur;
                	changerEtat(E.RELACHEMENT_VALIDE_IMPOSSIBLE);
                } else if (transition == T.TERMINER_TOUR && premier_coup_est_effectue) {
                	changerEtat(E.FIN_TOUR);
				}
				break;
            }

			case E.RELACHEMENT_VALIDE_IMPOSSIBLE: {
				if (transition == T.RELACHEMENT) {
					changerEtat(E.ATTENTE_ACTION);
				} else if (transition == T.DRAG) {
					Coup coup_temp = new Coup(position_debut, position_curseur, getJoueurActif());
					coup_valide_possible = coup_temp;
					if (
							aire_graphique.collisionZonePion(x, y)
							&& aire_jeu.coupValide(coup_temp)
					) {
						changerEtat(E.RELACHEMENT_VALIDE_POSSIBLE);
					}
				}
				break;
			}

			case E.RELACHEMENT_VALIDE_POSSIBLE: {
				Coup coup_temp = new Coup(position_debut, position_curseur, getJoueurActif());

				boolean coup_est_valide = aire_jeu.coupValide(coup_temp);

				if(transition == T.DRAG) {
					if(!coup_est_valide || !aire_graphique.collisionZonePion(x, y))
						changerEtat(E.RELACHEMENT_VALIDE_IMPOSSIBLE);
				} else if(transition == T.RELACHEMENT) {
					if(coup_est_valide) {
						if(aire_jeu.joueurDoitChoisir(coup_temp)) {
							aire_jeu.setChoixAspirationPercusion(coup_temp);
							listes_positions_choix_type_coup = aire_jeu.listeCapturesCoup(coup_temp);
							changerEtat(E.ATTENTE_CHOIX_TYPE_COUP);
						} else {
							jouerCoup(coup_temp);
						}
					}
				}
				break;
			}

			case E.ATTENTE_CHOIX_TYPE_COUP: {
					if (
							position_curseur.estDansListePositions(listes_positions_choix_type_coup.get(0))
					) {
						choix_type_coup_survole = 0;
						changerEtat(E.ATTENTE_CHOIX_TYPE_COUP_POSSIBLE);
					} else if(position_curseur.estDansListePositions(listes_positions_choix_type_coup.get(1))) {
						choix_type_coup_survole = 1;
						changerEtat(E.ATTENTE_CHOIX_TYPE_COUP_POSSIBLE);
					}
				break;
			}

			case E.ATTENTE_CHOIX_TYPE_COUP_POSSIBLE : {
				if (transition == T.SURVOL) {
					if (
							!position_curseur.estDansListePositions(listes_positions_choix_type_coup.get(0))
							&& !position_curseur.estDansListePositions(listes_positions_choix_type_coup.get(1))
					) {
						changerEtat(E.ATTENTE_CHOIX_TYPE_COUP);
					}
				} else if (transition == T.CLIC) {
					Coup coup_temp = aire_jeu.getChoixAspirationPercusion();

					if(position_curseur.estDansListePositions(listes_positions_choix_type_coup.get(0)))
						coup_temp.setAspiration(false);
					else if(position_curseur.estDansListePositions(listes_positions_choix_type_coup.get(1)))
						coup_temp.setAspiration(true);
					else
						System.out.println("Ne devrait pas arriver : choix type coup invalide.");

					if(aire_jeu.coupValide(coup_temp)) {
						aire_jeu.setChoixAspirationPercusion(null);
						jouerCoup(coup_temp);
					}
				}
				break;
			}

		}

		aire_graphique.setCurseur(position_debut, x, y);
		aire_graphique.repaint();
	}

	private void jouerCoup(Coup coup_temp) {
		aire_jeu.joueCoup(coup_temp);
		dernier_coup_effectue = coup_temp;

		if(!premier_coup_est_effectue) {
			premier_coup_est_effectue = true;
			premier_coup_a_effectue_capture = coup_temp.copyPionsCaptures().size() > 0;
		}
		aire_graphique.setPionsSupprimes(dernier_coup_effectue.getPions(), getJoueurActif() == AireJeu.BLANC ? AireJeu.NOIR : AireJeu.BLANC);

		changerEtat(E.ATTENTE_ACTION);
	}

}
