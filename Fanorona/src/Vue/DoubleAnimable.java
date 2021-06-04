package Vue;

import javax.swing.*;

/**
 * Classe utilitaire permettant de calculer la valeur de transparence que doit avoir un élément selon des paramètres donnés.
 * @author Titouan
 */
public class DoubleAnimable {

	private double valeur, compteur;
	private final Timer timer_up, timer_down;

        /**
         * Constructeur de DoublAnimable
         * @param jpanel l'élément parent de l'élément à qui appliquer l'animation (pour l'appel à <code>repaint()</code>)
         * @param valeur_initiale Entre 0.0 et 1.0, représente le niveau de transparence initial de l'élément (0 étant la transparence totale, 1 l'opacité totale)
         * @param images_par_seconde nombre d'ips désiré pour l'animation
         * @param temps_transition durée désirée de l'animation
         */
	public DoubleAnimable(JPanel jpanel, double valeur_initiale, int images_par_seconde, int temps_transition) {
		compteur = valeur_initiale;

		int delai = (int)(1000./(double)images_par_seconde);

		double compteur_inc = (double)delai/(double)temps_transition;

		timer_up = new Timer(delai, null);
		timer_up.addActionListener( ev -> {
			if(compteur <= 1.) {
				valeur = bezier(compteur);
				compteur += compteur_inc;
				jpanel.repaint();
			} else {
				timer_up.stop();
			}
		});

		timer_down = new Timer(delai, null);
		timer_down.addActionListener( ev -> {
			if(compteur >= 0.) {
				valeur = bezier(compteur);
				compteur -= compteur_inc;
				jpanel.repaint();
			} else {
				timer_down.stop();
			}
		});
	}

	private double lerp(double a, double b, double x) {
  		return a + x*(b - a);
	}

	private double bezier(double t){
    	return lerp(lerp(0.0,t,t),lerp(t, 1.0,t),t);
	}

        /**
         * Active le timer de l'animation dans la direction d'opacité
         */
	public void transitionVersUn() {
		timer_down.stop();
		compteur = 0.;
		timer_up.start();
	}

        /**
         * Active le timer de l'animation dans la direction de transparence
         */
	public void transitionVersZero() {
		timer_up.stop();
		compteur = 1.;
		timer_down.start();
	}

        /**
         * @return la valeur actuelle de transparence de l'élément
         */
	public double getValeur() {
		return valeur;
	}
}
