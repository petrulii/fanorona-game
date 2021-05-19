package Controleur;
import Modele.AireJeu;
import Modele.Coup;
import Modele.Position;
import Vue.AireGraphique;

/**
 * La classe principale de controleur qui recoit les notifications de la vue et decide qoui faire.
 * @author Petrulionyte Ieva, Yu Ran
 * @version 1.0
 */
public class ControleurMediateur {
	AireJeu aire_jeu;
	AireGraphique aire_graphique;
	Position debut;
	// 1 - joueur blanc, 2 - joueur noir.
	int joueur;
	/**
	* Joueur actuel.
	*/

	public ControleurMediateur(AireJeu a, AireGraphique a_graphique) {
		aire_jeu = a;
		aire_graphique = a_graphique;
		joueur = 1;
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
					Coup coup = new Coup(debut, fin, joueur);
					debut = null;
					if (!aire_jeu.coupValide(coup)) {
						coup.getJoueur();
						System.out.println("Le coup n'est pas valide, rejoue!");
					} else {
						// Si le joueur a le choix d'aspiration ou de percusion.
						if (aire_jeu.joueurDoitChoisir(coup)) {
							System.out.println("Joueur "+joueur+" doit choisir entre aspiration et percusion.");
						}
						aire_jeu.joueCoup(coup);
						System.out.println("Joueur "+joueur+" viens de jouer.");
						System.out.println("Son coup : "+coup);
						aire_graphique.repaint();
						if (aire_jeu.gameOver()) {
							System.out.println("Game Over!");
							System.exit(0);
						}
						// Changement de joueur.
						if (joueur == 1) { joueur = 2; } else { joueur = 1; }
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
		switch (instruction) {
			case "Annuler":
				if (aire_jeu.annulationCoupPossible()) {
					aire_jeu.annulerCoup();
					// Changement de joueur.
					if (joueur == 1) { joueur = 2; } else { joueur = 1; }
					aire_graphique.repaint();
				}
				break;
			case "Refaire":
				if (aire_jeu.refaireCoupPossible()) {
					aire_jeu.refaireCoup();
					if (aire_jeu.gameOver()) {
						System.out.println("Game Over!");
						System.exit(0);
					}
					// Changement de joueur.
					if (joueur == 1) { joueur = 2; } else { joueur = 1; }
					aire_graphique.repaint();
				}
				break;
			case "Exporter":
				aire_jeu.sauvegarderHistoriqueCoups();
				System.out.println("Demande export d'hisorique.");
				break;
			default:
				System.out.println("Le controleur ne connait pas cette instruction souris.");
		}
	}
    
}