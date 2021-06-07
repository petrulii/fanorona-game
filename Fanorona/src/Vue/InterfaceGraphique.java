package Vue;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * Classe créant la fenêtre de jeu
 * @author Titouan et Marin
 */
public class InterfaceGraphique implements Runnable {

	/**
     * @param nom nom de l'image à charger, sans chemin ni extension
     * @return une Image chargée à partir d'un fichier PNG du dossier Images
     */
    private Image chargerImage(String nom) {
		Image img = null;
		InputStream in = getClass().getClassLoader().getResourceAsStream("Images/" + nom + ".png");

		try {
			assert in != null;
			img = ImageIO.read(in);
		} catch (IOException e) {
            System.err.println("Erreur au chargement de l'image : " + "Images" + File.separator + nom + ".png");
            System.err.println(e);
            System.exit(1);
        }

        return img;
    }

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

		try { UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf()); } catch (UnsupportedLookAndFeelException e) { e.printStackTrace(); }

		System.setProperty("awt.useSystemAAFontSettings", "on");
		System.setProperty("swing.aatext", "true");

		MainGUI fenetre = new MainGUI(chargerFont("unicode.arialr").deriveFont(18f));
		fenetre.setIconImage(chargerImage("icone_jeu"));
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