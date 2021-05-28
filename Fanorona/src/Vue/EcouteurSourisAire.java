package Vue;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import Controleur.ControleurMediateur;

public class EcouteurSourisAire implements MouseListener, MouseMotionListener {

    private final AireGraphique aire_graphique;
	private final ControleurMediateur control;

    public EcouteurSourisAire(ControleurMediateur c, AireGraphique a) {
        aire_graphique = a;
        control = c;
        a.addMouseMotionListener(this);
        a.addMouseListener(this);
    }

    @Override
	public void mouseDragged(MouseEvent e) {
		aire_graphique.maintenirPion(e.getX(), e.getY());
		aire_graphique.repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mouseDragged(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(aire_graphique.relacherPion(e.getX(), e.getY())) {
			//System.out.println("Pion relaché sur case : " + colonne + " " + ligne);
			control.instructionSouris(
					"Jouer",
					aire_graphique.getPositionDepartPion(),
					aire_graphique.coordonneesVersPosition(e.getX(), e.getY())
			);
		}
		aire_graphique.repaint();
	}

    @Override
	public void mouseClicked(MouseEvent e) {
		//System.out.println("clicked");
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		aire_graphique.survolerPosition(e.getX(), e.getY());
		aire_graphique.repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		//System.out.println("entered");
	}

	@Override
	public void mouseExited(MouseEvent e) {
		//System.out.println("exited");
	}
}