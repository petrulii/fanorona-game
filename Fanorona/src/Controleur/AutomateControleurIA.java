package Controleur;

import Modele.AireJeu;
import Modele.Coup;
import Modele.Position;
import Vue.AireGraphique;
import Vue.MainGUI;

import javax.swing.*;
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
    public void passerTourSuivant() {
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
		fenetre.majBoutonTerminer(premier_coup_est_effectue && ia_courante == null);
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

	@Override
	public void actionPerformed(ActionEvent e) {

		switch (etat_courant) {
			case E.ATTENTE_ACTION -> {
				if(!premier_coup_est_effectue) {
					compteur_ia = 0;
					coup_ia = ia_courante.donneCoup(null);

					int x_debut = aire_graphique.positionVersX(coup_ia.getDebut());
					int y_debut = aire_graphique.positionVersY(coup_ia.getDebut());

					actionIA(T.PRESSION, x_debut, y_debut);
					iaEtapeSuivante();
				} else {
					if(aire_jeu.joueurPeutContinuerTour(coup_ia.getFin())) {
						compteur_ia = 0;
						coup_ia = ia_courante.donneCoup(coup_ia.getFin());

						int x_debut = aire_graphique.positionVersX(coup_ia.getDebut());
						int y_debut = aire_graphique.positionVersY(coup_ia.getDebut());

						actionIA(T.PRESSION, x_debut, y_debut);
						iaEtapeSuivante();
					} else {
						actionIA(T.TERMINER_TOUR);
					}
				}
			}

			case E.RELACHEMENT_VALIDE_IMPOSSIBLE -> {
				int x_debut = aire_graphique.positionVersX(coup_ia.getDebut());
				int y_debut = aire_graphique.positionVersY(coup_ia.getDebut());
				int x_fin = aire_graphique.positionVersX(coup_ia.getFin());
				int y_fin = aire_graphique.positionVersY(coup_ia.getFin());

				int x = (int)bezier(x_debut, x_fin, compteur_ia);
				int y = (int)bezier(y_debut, y_fin, compteur_ia);

				if(compteur_ia < 1) {
					compteur_ia += .125;
					aire_graphique.setCurseur(position_debut, x, y);
					aire_graphique.repaint();
					iaEtapeSuivanteCourte();
				} else {
					actionIA(T.DRAG, x, y);
					iaEtapeSuivante();
				}
			}

			case E.RELACHEMENT_VALIDE_POSSIBLE -> {
				int x_fin = aire_graphique.positionVersX(coup_ia.getFin());
				int y_fin = aire_graphique.positionVersY(coup_ia.getFin());

				actionIA(T.RELACHEMENT, x_fin, y_fin);

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
