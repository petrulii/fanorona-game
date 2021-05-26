package Vue;
import javax.swing.*;
import Controleur.ControleurMediateur;
import Modele.AireJeu;

public class InterfaceGraphique implements Runnable {
	int X;
	int Y;
	AireJeu aire_jeu;
	JFrame frame;
	
	public InterfaceGraphique(AireJeu a, int X, int Y) {
		aire_jeu = a;
		this.X = X;
		this.Y = Y;
	}

	public void run() {
		frame = new JFrame("Jeu");
		AireGraphique aire_graphique = new AireGraphique(aire_jeu);
		frame.add(aire_graphique);
		// 2 - niveau d'IA 1, 3 - niveau d'IA 2, BLANC - joueur qui commence
		ControleurMediateur control = new ControleurMediateur(aire_jeu, aire_graphique, 0, 3, AireJeu.BLANC);
		aire_graphique.setFocusable(true);
		aire_graphique.addMouseListener(new EcouteurSourisAire(control));
		aire_graphique.addKeyListener(new EcouteurClavier(control));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(X, Y);
		frame.setResizable(true);
		frame.setVisible(true);
	}

	public static void demarrer(AireJeu a, int X, int Y) {
		SwingUtilities.invokeLater(new InterfaceGraphique(a, X, Y));
	}
}