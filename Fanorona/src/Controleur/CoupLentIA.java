package Controleur;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import Modele.Coup;

public class CoupLentIA implements ActionListener {
		ControleurMediateur control;
		Coup coup;
		Timer t;
		
		public CoupLentIA(ControleurMediateur control, Coup coup) {
			this.control = control;
			this.coup = coup;
			// On donne a Timer l'objet meme comme l'objet d'ecoute.
			t = new Timer(0, this);
			t.start();
		}
	
		@Override
		public void actionPerformed(ActionEvent e) {
			control.joueCoup(coup);//control.joueIA(coup);
			t.stop();
		}
	}
