package Vue;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import com.formdev.flatlaf.FlatLightLaf;

/**
 * Classe créant la fenêtre de jeu
 * @author Titouan et Marin
 */
public class InterfaceGraphique implements Runnable {

	private Font chargerFont(String nom) {
		Font font = null;
		InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream("Fonts/" + nom + ".ttf");

		try {
			assert in != null;
			font = Font.createFont(Font.TRUETYPE_FONT, in);
		} catch (Exception e) {
			System.out.println("Erreur au chargement de la font  : " + "Fonts/" + nom + ".ttf");
			System.exit(1);
		}

		return font;
	}


        /**
         * Ne pas appeler cette fonction. Appeler plutôt <code>demarrer</code> qui est thread-safe
         */
	@Override
	public void run() {

		try { UIManager.setLookAndFeel(new FlatLightLaf()); } catch (UnsupportedLookAndFeelException e) { e.printStackTrace(); }

		MainGUI fenetre = new MainGUI(chargerFont("unicode.arialr").deriveFont(18f));
		fenetre.setVisible(true);
		fenetre.setLocationRelativeTo(null);

	}

        /**
         * Appeler cette fonction pour lancer l'interface graphique. Ne pas appeler le constructeur ni <code>run()</code>, car ils ne sont pas thread-safe.
         */
	public static void demarrer() {
		SwingUtilities.invokeLater(new InterfaceGraphique());
	}
}