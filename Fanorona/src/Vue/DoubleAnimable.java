package Vue;

import javax.swing.*;

public class DoubleAnimable {

	private double valeur, compteur;
	private final Timer timer_up, timer_down;

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

	public void transitionVersUn() {
		timer_down.stop();
		compteur = 0.;
		timer_up.start();
	}

	public void transitionVersZero() {
		timer_up.stop();
		compteur = 1.;
		timer_down.start();
	}

	public double getValeur() {
		return valeur;
	}
}
