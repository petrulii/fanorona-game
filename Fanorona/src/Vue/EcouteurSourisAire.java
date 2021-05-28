package Vue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import Controleur.ControleurMediateur;

public class EcouteurSourisAire extends MouseAdapter {
	ControleurMediateur control;

	public EcouteurSourisAire(ControleurMediateur c) {
		control = c;
	}

	@Override
	public void mousePressed(MouseEvent event) {
		control.instructionSouris("Jouer", event.getX(), event.getY());
	}
}