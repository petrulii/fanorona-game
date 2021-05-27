package Vue;

import javax.swing.*;
import Modele.AireJeu;
import com.formdev.flatlaf.FlatDarkLaf;

public class InterfaceGraphique implements Runnable {
	private final AireJeu aire_jeu;
	
	public InterfaceGraphique(AireJeu a) {
		aire_jeu = a;
	}

	/*private Font chargerFont(String nom) {
		Font font = null;
		InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream("Fonts" + File.separator + nom + ".ttf");

		try {
			font = Font.createFont(Font.TRUETYPE_FONT, in);
		} catch (Exception e) {
			System.out.println("Erreur au chargement de la font  : " + "Fonts" + File.separator + nom + ".ttf");
			System.exit(1);
		}
		//fenetre_jeu.ajouterFont(chargerFont("nokiafc22").deriveFont(20f));

		return font;
	}*/


	@Override
	public void run() {

		FlatDarkLaf.install();

		new MainGUI(aire_jeu);

	}

	public static void demarrer(AireJeu a) {
		SwingUtilities.invokeLater(new InterfaceGraphique(a));
	}
}