package Vue;
import Modele.AireJeu;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.InputStream;

public class AireGraphique extends JComponent {
	AireJeu aire;
	Image blanc4, noir4, blanc8, noir8, vide4, vide8;

	private Image chargeImage(String nom) {
		Image img = null;
		InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream("Images" + File.separator + nom + ".png");
		try {
			// Chargement d'une image utilisable dans Swing
			img = ImageIO.read(in);
		} catch (Exception e) {
			System.out.println("J'arrive pas a construire les images." + "Images" + File.separator + nom + ".png");
			System.exit(1);
		}
		return img;
	}

	public AireGraphique(AireJeu aire) {
		this.aire = aire;
		blanc4 = chargeImage("Blanc4");
		blanc8 = chargeImage("Blanc8");
		noir4 = chargeImage("Noir4");
		noir8 = chargeImage("Noir8");
		vide4 = chargeImage("Vide4");
		vide8 = chargeImage("Vide8");
	}

	@Override
	public void paintComponent(Graphics g) {
		// Graphics 2D est le vrai type de l'objet pass�� en param��tre
		// Le cast permet d'avoir acces a un peu plus de primitives de dessin
		Graphics2D drawable = (Graphics2D) g;

		// On reccupere quelques infos provenant de la partie JComponent
		int width_case = getCaseWidth();
		int height_case = getCaseHeight();

		// On efface tout
		drawable.clearRect(0, 0, getWidth(), getHeight());
		
		int[][] grille = aire.getGrille();
		for (int j = 0; j < AireJeu.NB_COLONNES; j++) {
			for (int i = 0; i < AireJeu.NB_LIGNES; i++) {
				switch(grille[i][j]) {
				    case 1:
						// Si c'est une case avec 8 voisins, autrement dit les deux ligne et colonne impairs ou pairs.
						if (i%2==1 && j%2==1 || i%2==0 && j%2==0) {
							drawable.drawImage(blanc8, j*width_case, i*height_case, width_case, height_case, null);							
						} else {
							drawable.drawImage(blanc4, j*width_case, i*height_case, width_case, height_case, null);
						}
						break;
					case 2:
						// Si c'est une case avec 8 voisins, autrement dit les deux ligne et colonne impairs ou pairs.
						if (i%2==1 && j%2==1 || i%2==0 && j%2==0) {
							drawable.drawImage(noir8, j*width_case, i*height_case, width_case, height_case, null);							
						} else {
							drawable.drawImage(noir4, j*width_case, i*height_case, width_case, height_case, null);
						}
						break;
					case 0:
						// Si c'est une case avec 8 voisins, autrement dit les deux ligne et colonne impairs ou pairs.
						if (i%2==1 && j%2==1 || i%2==0 && j%2==0) {
							drawable.drawImage(vide8, j*width_case, i*height_case, width_case, height_case, null);							
						} else {
							drawable.drawImage(vide4, j*width_case, i*height_case, width_case, height_case, null);
						}
						break;
				}
			}
		}
	}
	
	public int getWidth() {
		return getSize().width;
	}
	
	public int getHeight() {
		return getSize().height;
	}

	public int getCaseWidth() {
		return getWidth()/AireJeu.NB_COLONNES;
	}

	public int getCaseHeight() {
		return getHeight()/AireJeu.NB_LIGNES;
	}

}