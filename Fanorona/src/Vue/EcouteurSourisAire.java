package Vue;

import java.awt.event.MouseEvent;
import Controleur.ControleurMediateur;
import javax.swing.event.MouseInputAdapter;

/**
 * Ecouteur de clics et de mouvement souris
 * @author Titouan
 */
public class EcouteurSourisAire extends MouseInputAdapter {

    private final ControleurMediateur control;

    /**
     * Constructeur
     * @param c le contrôleur à qui envoyer les events
     */
    public EcouteurSourisAire(ControleurMediateur c) {
        control = c;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        control.instructionSouris("Survoler", e.getX(), e.getY());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        control.instructionSouris("Presser", e.getX(), e.getY());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        control.instructionSouris("Glisser", e.getX(), e.getY());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        control.instructionSouris("Relacher", e.getX(), e.getY());
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        control.instructionSouris("Cliquer", e.getX(), e.getY());
    }

}