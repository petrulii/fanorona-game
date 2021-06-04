package Vue;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.InputStream;
/*import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;*/

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
		//fenetre_jeu.ajouterFont(chargerFont("nokiafc22").deriveFont(20f));

		return font;
	}


        /**
         * Ne pas appeler cette fonction. Appeler plutôt <code>demarrer</code> qui est thread-safe
         */
	@Override
	public void run() {

		/*try {
			UIManager.setLookAndFeel(new FlatLightLaf());
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}*/

		MainGUI fenetre = new MainGUI();

		//fenetre.appliquerFont(chargerFont("FredokaOne-Regular").deriveFont(16f));
		fenetre.setVisible(true);

	}

        /**
         * Appeler cette fonction pour lancer l'interface graphique. Ne pas appeler le constructeur ni <code>run()</code>, car ils ne sont pas thread-safe.
         */
	public static void demarrer() {
		SwingUtilities.invokeLater(new InterfaceGraphique());
	}
}