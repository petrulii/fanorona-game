package Vue;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import Controleur.ControleurMediateur;
import Modele.AireJeu;

import java.awt.*;
import java.io.File;
import java.io.InputStream;

public class InterfaceGraphique implements Runnable {
	private final AireJeu aire_jeu;
	
	public InterfaceGraphique(AireJeu a) {
		aire_jeu = a;
	}

	private Font chargerFont(String nom) {
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
	}


	@Override
	public void run() {
		FlatDarkLaf.install();
		// creation fenetre de jeu
		/*JeuGUI fenetre_jeu = new JeuGUI();

		AireGraphique aire_graphique = new AireGraphique(aire_jeu);
		fenetre_jeu.ajouterAireGraphique(aire_graphique);

		ControleurMediateur control = new ControleurMediateur(aire_jeu, aire_graphique);
		aire_graphique.addMouseListener(new EcouteurSourisAire(control, aire_graphique));
		aire_graphique.addKeyListener(new EcouteurClavier(control));

		fenetre_jeu.setSize(largeur, hauteur);
		fenetre_jeu.pack();

		// création fenetre de menu
		MenuGUI fenetre_menu = new MenuGUI();
		fenetre_menu.setVisible(true);

		fenetre_menu.ajouterFenetreJeu(fenetre_jeu);
		fenetre_jeu.ajouterFenetreMenu(fenetre_menu);*/

		MainGUI fenetre = new MainGUI();
		AireGraphique aire_graphique = new AireGraphique(aire_jeu);
		ControleurMediateur control = new ControleurMediateur(aire_jeu, aire_graphique);
		aire_graphique.addMouseListener(new EcouteurSourisAire(control, aire_graphique));
		aire_graphique.addKeyListener(new EcouteurClavier(control));
		fenetre.ajouterAireGraphique(aire_graphique);
		fenetre.pack();
		fenetre.setVisible(true);
	}

	public static void demarrer(AireJeu a) {
		SwingUtilities.invokeLater(new InterfaceGraphique(a));
	}
}