package Vue;

import java.awt.event.MouseEvent;
import Controleur.ControleurMediateur;
import javax.swing.event.MouseInputAdapter;

public class EcouteurSourisAire extends MouseInputAdapter {

	private final ControleurMediateur control;

    public EcouteurSourisAire(ControleurMediateur c) {
        control = c;
    }

	public void mouseMoved(MouseEvent e) {
		control.instructionSouris("Survoler", e.getX(), e.getY());
	}

	public void mousePressed(MouseEvent e) {
    	control.instructionSouris("Presser", e.getX(), e.getY());
	}

	public void mouseDragged(MouseEvent e) {
		control.instructionSouris("Glisser", e.getX(), e.getY());
	}

	public void mouseReleased(MouseEvent e) {
		control.instructionSouris("Relacher", e.getX(), e.getY());
	}

	public void mouseClicked(MouseEvent e) {
    	control.instructionSouris("Cliquer", e.getX(), e.getY());
	}

}