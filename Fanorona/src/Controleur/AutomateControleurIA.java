package Controleur;

import Modele.AireJeu;
import Modele.Coup;
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
    private final int timing_ia = 500;
    private double compteur_ia;

    public AutomateControleurIA(AireJeu aj, AireGraphique ag, MainGUI f, int joueur_commence, IA ia_n, IA ia_b) {
        super(aj, ag, f, joueur_commence);

		ia_noir = ia_n;
		ia_blanc = ia_b;

		timer_ia = new Timer(timing_ia, this);
		timer_ia.setRepeats(false);

		timer_ia_court = new Timer(16, this);

		changerJoueur(joueur_commence);

		if(ia_courante != null)
			faireJouerIa();
    }

    @Override
    protected void passerTourSuivant() {
		if(!aire_jeu.gameOver()) {
			changerJoueur();
			initialiserTour();

			if (ia_courante != null)
				faireJouerIa();
		} else {
			fenetre.afficherGameOver();
		}
	}

	@Override
	protected void changerJoueur(int j) {
		aire_jeu.setJoueur(j);
		miseAjourJoueurIA();
		fenetre.majAffichageJoueurActif();
	}

	protected void miseAjourJoueurIA() {
		ia_courante = getJoueurActif() == AireJeu.BLANC ? ia_blanc : ia_noir;
	}

	@Override
	protected void etatCourantAchange() {
		fenetre.majBoutonTerminer(
				ia_courante == null
				&& premier_coup_est_effectue
				&& etat_courant != E.ATTENTE_CHOIX_TYPE_COUP
		);
		fenetre.majBoutonHistorique(
				etat_courant != E.ATTENTE_CHOIX_TYPE_COUP
				&& ia_courante == null
		);
	}

	@Override
	public void annulerCoup() {
    	while(aire_jeu.annulationCoupPossible()) {
			majApresChangementHistorique(aire_jeu.annulerCoup());
			if(ia_courante == null)
				break;
		}
    	repriseApresChangementHistorique();
	}

	@Override
	public void refaireCoup() {
    	while(aire_jeu.refaireCoupPossible()) {
			majApresChangementHistorique(aire_jeu.refaireCoup());
			if(ia_courante == null)
				break;
		}
    	repriseApresChangementHistorique();
	}

	protected void repriseApresChangementHistorique() {
		fenetre.majAffichageJoueurActif();
		changerEtat(E.ATTENTE_ACTION, true);
		aire_graphique.repaint();
		if(ia_courante != null)
			faireJouerIa();
	}

	@Override
	protected void majApresChangementHistorique(Coup coup_restaure) {
		if(coup_restaure != null) {
			miseAjourJoueurIA();

			int nb_coups_effectues = aire_jeu.listeHistoriquePosTourCourant(getJoueurActif()).size();
			premier_coup_est_effectue = nb_coups_effectues >= 1;
			premier_coup_a_effectue_capture =
						premier_coup_est_effectue
						&& (nb_coups_effectues > 1 || coup_restaure.getPions().size() > 0);
			dernier_coup_effectue = aire_jeu.getDernierCoup();
		}
	}

	@Override
	public void actionJoueur(int transition, int x, int y) {
		if(ia_courante == null)
			action(transition, x, y);
	}

	protected void actionIA(int transition) {
		actionIA(transition, 0, 0);
	}

	protected void actionIA(int transition, int x, int y) {
		action(transition, x, y);
	}

	private void etapeSuivanteIA() {
    	faireJouerIa();
	}

	private void lancerAnimationIA() {
		timer_ia_court.start();
		compteur_ia = 0;
	}

	private void stopperAnimationIA() {
		timer_ia_court.stop();
	}

	private void faireJouerIa() {
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
				coup_ia.setAspiration(ia_courante.faitChoixAspiration());

				actionIA(T.CHOIX_ASPIRATION);
				faireJouerIa();
			}
			break;
		}

	}

}
