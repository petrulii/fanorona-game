package Controleur;
import Modele.AireJeu;
import Modele.Coup;
import Modele.Position;
import Vue.AireGraphique;

/**
 * La classe principale de controleur qui recoit les notifications de la vue et decide qoui faire.
 * @author Petrulionyte Ieva
 * @version 1.0
 */
public class ControleurMediateur {
	AireJeu aire_jeu;
	AireGraphique aire_graphique;
	Position debut;

	public ControleurMediateur(AireJeu a, AireGraphique a_graphique) {
		aire_jeu = a;
		aire_graphique = a_graphique;
		// Si debut est null alors on est au debut de creation d'un coup.
		debut = null;
	}

    /**
     * Effectue une instruction donne apres un click souris.
	 * @param l'instruction a effectuer
	 * @param l'entier coordone x de fenetre graphique
	 * @param l'entier coordone y de fenetre graphique
     */
    public void instructionSouris(String instruction, int x, int y) {
		switch (instruction) {
			case "Jouer":
				int ligne = y/aire_graphique.getCaseHeight();
				int colonne = x/aire_graphique.getCaseWidth();
				if (debut == null) {
					debut = new Position(ligne, colonne);
					System.out.println("Le pion choisi est sur la case "+debut+".");
				} else {
					Position fin = new Position(ligne, colonne);
					System.out.println("La destination choisi est sur la case "+fin+".");
					Coup coup = new Coup(debut, fin);
					debut = null;
					if (!aire_jeu.coupValide(coup)) {
						System.out.println("Le coup n'est pas valide, rejoue!");
					} else {
						aire_jeu.joueCoup(coup);
						System.out.println("Joueur viens de jouer.");
						aire_graphique.repaint();
						if (aire_jeu.gameOver()) {
							System.out.println("Game Over!");
							System.exit(0);
						}
					}
				}
				break;
			default:
				System.out.println("Le controleur ne connait pas cette instruction souris.");
		}
	}

    /**
     * Effectue une instruction donne apres un click d'un touche clavier.
	 * @param l'instruction a effectuer
     */
    public void instructionClavier(String instruction) {
	}
    
}