package Controleur;

import Modele.AireJeu;
import Modele.Coup;
import Modele.Position;
import Vue.AireGraphique;
import Vue.MainGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AutomateControleurIA extends AutomateControleur implements ActionListener {

    private final IA ia_noir, ia_blanc;
	private IA ia_courante;
    private Coup coup_ia;

    private final Timer timer_ia, timer_ia_court;
    private final int timing_ia = 1000;
    private double compteur_ia;

    private Point coordonnees_souris;

    public AutomateControleurIA(AireJeu aj, AireGraphique ag, MainGUI f, int joueur_commence, IA ia_n, IA ia_b) {
        super(aj, ag, f, joueur_commence);

		ia_noir = ia_n;
		ia_blanc = ia_b;

		timer_ia = new Timer(timing_ia, this);
		timer_ia.setRepeats(false);

		timer_ia_court = new Timer(16, this);

		changerJoueur(joueur_commence);

		faireJouerIa();
    }

    private boolean iaCourantePeutJouer() {
    	return ia_courante != null;
	}

    @Override
    protected void passerTourSuivant() {
		if(!aire_jeu.gameOver()) {
			changerJoueur();
			initialiserTour();

			faireJouerIa();
		} else {
			fenetre.afficherGameOver(joueur_courant);
		}
	}

	@Override
	protected void changerJoueur(int j) {
		joueur_courant = j;
		miseAjourJoueurIA();
		fenetre.majAffichageJoueurActif(joueur_courant, ia_courante != null);
	}

	protected void miseAjourJoueurIA() {
		ia_courante = joueur_courant == AireJeu.BLANC ? ia_blanc : ia_noir;
	}

	@Override
	protected void etatCourantAchange() {
		fenetre.majBoutonTerminer(
				!iaCourantePeutJouer()
				&& premier_coup_est_effectue
				&& etat_courant != E.ATTENTE_CHOIX_TYPE_COUP
				&& etat_courant != E.ATTENTE_CHOIX_TYPE_COUP_POSSIBLE
		);
		fenetre.majBoutonHistorique(
				etat_courant != E.ATTENTE_CHOIX_TYPE_COUP && etat_courant != E.ATTENTE_CHOIX_TYPE_COUP_POSSIBLE
				&& !iaCourantePeutJouer()
		);
	}

	@Override
	public void annulerCoup() {
    	Coup coup_restaure = null;
    	while(aire_jeu.annulationCoupPossible()) {
			coup_restaure = aire_jeu.annulerCoup();
			joueur_courant = coup_restaure.getJoueur();
			miseAjourJoueurIA();
			if(!iaCourantePeutJouer())
				break;
		}
    	majApresChangementHistorique(coup_restaure);
		fenetre.majAffichageJoueurActif(joueur_courant,ia_courante != null);
		faireJouerIa();
	}

	@Override
	public void refaireCoup() {
    	Coup coup_restaure = null;
    	while(aire_jeu.refaireCoupPossible()) {
			coup_restaure = aire_jeu.refaireCoup();
			joueur_courant = coup_restaure.getJoueur();
			miseAjourJoueurIA();
			if(!iaCourantePeutJouer())
				break;
		}
		fenetre.majAffichageJoueurActif(joueur_courant, ia_courante != null);
    	majApresChangementHistorique(coup_restaure);
		faireJouerIa();
	}

	@Override
	public void actionJoueur(int transition, int x, int y) {
		if(!iaCourantePeutJouer())
			action(transition, x, y);
	}

	protected void actionIA(int transition) {
		actionIA(transition, 0, 0);
	}

	protected void actionIA(int transition, int x, int y) {
		action(transition, x, y);
	}

	private void lancerAnimationIA() {
		timer_ia_court.start();
		compteur_ia = 0;
	}

	private void stopperAnimationIA() {
		timer_ia_court.stop();
	}

	private void faireJouerIa() {
		if(iaCourantePeutJouer())
    		timer_ia.start();
	}

	private double lerp(double a, double b, double x) {
  		return a + x*(b - a);
	}

	private double bezier(double p1, double p2, double t){
		double a = lerp(p1, p2,t);
    	return lerp(lerp(p1,a,t),lerp(a,p2,t),t);
	}

	private Point bezier(Point p1, Point p2, double t) {
    	return new Point((int)bezier(p1.x, p2.x, t), (int)bezier(p1.y, p2.y, t));
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		switch (etat_courant) {
			case E.ATTENTE_ACTION: {
				if(
						!premier_coup_est_effectue
						|| (premier_coup_a_effectue_capture && aire_jeu.joueurPeutContinuerTour(coup_ia.getFin()))
				) {
					aire_jeu.setChoixAspirationPercusion(null);
					coup_ia = ia_courante.donneCoup(premier_coup_est_effectue ? coup_ia.getFin() : null);

					Point coordonnees_debut = aire_graphique.positionVersCoordonnees(coup_ia.getDebut());

					actionIA(T.PRESSION, coordonnees_debut.x, coordonnees_debut.y);
					lancerAnimationIA();
				} else {
					actionIA(T.TERMINER_TOUR);
				}
			}
			break;

			case E.RELACHEMENT_VALIDE_IMPOSSIBLE: {
				Point coordonnees_debut = aire_graphique.positionVersCoordonnees(coup_ia.getDebut());
				Point coordonnees_fin = aire_graphique.positionVersCoordonnees(coup_ia.getFin());

				if(compteur_ia < 1) {
					Point coordonnees_interpolees = bezier(coordonnees_debut, coordonnees_fin, compteur_ia);

					aire_graphique.setCurseur(position_debut, coordonnees_interpolees.x, coordonnees_interpolees.y);
					aire_graphique.repaint();
					compteur_ia += .125;
				} else {
					actionIA(T.DRAG, coordonnees_fin.x, coordonnees_fin.y);
					stopperAnimationIA();
					faireJouerIa();
				}
			}
			break;

			case E.RELACHEMENT_VALIDE_POSSIBLE: {
				Point coordonnees_fin = aire_graphique.positionVersCoordonnees(coup_ia.getFin());

				actionIA(T.RELACHEMENT, coordonnees_fin.x, coordonnees_fin.y);
				faireJouerIa();
			}
			break;

			case E.ATTENTE_CHOIX_TYPE_COUP: {
				coordonnees_souris = aire_graphique.positionVersCoordonnees(
						listes_positions_choix_type_coup.get(coup_ia.getAspiration() ? 1 : 0).get(0)
				);
				actionIA(T.SURVOL, coordonnees_souris.x, coordonnees_souris.y);
				faireJouerIa();
			}
			break;

			case E.ATTENTE_CHOIX_TYPE_COUP_POSSIBLE: {
				actionIA(T.CLIC, coordonnees_souris.x, coordonnees_souris.y);

				faireJouerIa();
			}
			break;

		}

	}

	@Override
	protected void actionApresChoixTypeCoup(Position position_curseur) {
    	if(ia_courante != null) {
			aire_jeu.setChoixAspirationPercusion(null);
			jouerCoup(coup_ia);
		} else {
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
	}

}
