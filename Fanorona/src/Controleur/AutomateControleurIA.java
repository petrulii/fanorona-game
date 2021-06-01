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

    private final Timer timer_ia;
    private final int timing_ia = 500;
    private double compteur_ia;

    public AutomateControleurIA(AireJeu aj, AireGraphique ag, MainGUI f, int joueur_commence, IA ia_n, IA ia_b) {
        super(aj, ag, f, joueur_commence);

		ia_noir = ia_n;
		ia_blanc = ia_b;

		timer_ia = new Timer(timing_ia, this);
		timer_ia.setRepeats(false);

		changerJoueur(joueur_commence);

		if(ia_courante != null)
			faireJouerIa(null);
    }

    @Override
    protected void passerTourSuivant() {
		if(!aire_jeu.gameOver()) {
			changerJoueur();
			initialiserTour();

			if (ia_courante != null)
				faireJouerIa(null);
		} else {
			fenetre.afficherGameOver();
		}
	}

	@Override
	protected void changerJoueur(int j) {
		aire_jeu.setJoueur(j);
		ia_courante = j == AireJeu.BLANC ? ia_blanc : ia_noir;
		fenetre.majAffichageJoueurActif();
	}

	@Override
	public void actionJoueur(int transition, int x, int y) {
		if(ia_courante == null)
			action(transition, x, y);
	}

	@Override
	protected void etatCourantAchange() {
		fenetre.majBoutonTerminer(
				ia_courante == null
				&& premier_coup_est_effectue
				&& etat_courant != E.ATTENTE_CHOIX_TYPE_COUP
		);
		fenetre.majBoutonHistorique(etat_courant != E.ATTENTE_CHOIX_TYPE_COUP);
	}

	protected void actionIA(int transition) {
		actionIA(transition, 0, 0);
	}

	protected void actionIA(int transition, int x, int y) {
		action(transition, x, y);
	}

    private void iaEtapeSuivante() {
		timer_ia.setInitialDelay(timing_ia);
		timer_ia.start();
	}

	private void iaEtapeSuivanteCourte() {
		timer_ia.setInitialDelay(16);
		timer_ia.start();
	}

	private void faireJouerIa(Position p) {
		iaEtapeSuivante();
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
			case E.ATTENTE_ACTION -> {
				if(
						!premier_coup_est_effectue
						|| (premier_coup_a_effectue_capture && aire_jeu.joueurPeutContinuerTour(coup_ia.getFin()))
				) {
					compteur_ia = 0;
					coup_ia = ia_courante.donneCoup(premier_coup_est_effectue ? coup_ia.getFin() : null);

					Point coordonnees_debut = aire_graphique.positionVersCoordonnees(coup_ia.getDebut());

					actionIA(T.PRESSION, coordonnees_debut.x, coordonnees_debut.y);
					iaEtapeSuivante();
				} else {
					actionIA(T.TERMINER_TOUR);
				}
			}

			case E.RELACHEMENT_VALIDE_IMPOSSIBLE -> {
				Point coordonnees_debut = aire_graphique.positionVersCoordonnees(coup_ia.getDebut());
				Point coordonnees_fin = aire_graphique.positionVersCoordonnees(coup_ia.getFin());

				Point coordonnees_interpolees = bezier(coordonnees_debut, coordonnees_fin, compteur_ia);

				if(compteur_ia < 1) {
					compteur_ia += .125;
					aire_graphique.setCurseur(position_debut, coordonnees_interpolees.x, coordonnees_interpolees.y);
					aire_graphique.repaint();
					iaEtapeSuivanteCourte();
				} else {
					actionIA(T.DRAG, coordonnees_interpolees.x, coordonnees_interpolees.y);
					iaEtapeSuivante();
				}
			}

			case E.RELACHEMENT_VALIDE_POSSIBLE -> {
				Point coordonnees_fin = aire_graphique.positionVersCoordonnees(coup_ia.getFin());

				actionIA(T.RELACHEMENT, coordonnees_fin.x, coordonnees_fin.y);

				iaEtapeSuivante();
			}

			case E.ATTENTE_CHOIX_TYPE_COUP -> {
				coup_ia.setAspiration(ia_courante.faitChoixAspiration());

				actionIA(T.CHOIX_ASPIRATION);

				iaEtapeSuivante();
			}
		}

	}

}
