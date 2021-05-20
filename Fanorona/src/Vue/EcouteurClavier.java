package Vue;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import Controleur.ControleurMediateur;

public class EcouteurClavier extends KeyAdapter {
	ControleurMediateur control;

	public EcouteurClavier(ControleurMediateur c) {
		control = c;
	}

	@Override
	public void keyPressed(KeyEvent event) {
		switch (event.getKeyCode()) {
			case KeyEvent.VK_A:
				control.instruction("Annuler");
				break;
			case KeyEvent.VK_R:
				control.instruction("Refaire");
				break;
			case KeyEvent.VK_E:
				control.instruction("Exporter");
				break;
			case KeyEvent.VK_I:
				control.instruction("Activer IA");
				break;
			case KeyEvent.VK_DOWN:
				control.instruction("Percusion");
				break;
			case KeyEvent.VK_UP:
				control.instruction("Aspiration");
				break;
		}
	}
}