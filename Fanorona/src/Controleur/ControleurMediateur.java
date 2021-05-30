package Controleur;
import Modele.AireJeu;
import Modele.Coup;
import Modele.Position;
import Vue.AireGraphique;
import Vue.MainGUI;

/**
 * La classe principale de controleur qui recoit les notifications de la vue et decide qoui faire.
 * @author Petrulionyte Ieva, Yu Ran
 * @version 1.0
 */
public class ControleurMediateur {

	AireJeu aire_jeu;
	AireGraphique aire_graphique;
	MainGUI fenetre;

	AutomateControleur automate_joueur;

	public static final int HUMAIN = 0;
	public static final int FACILE = 1;
	public static final int MOYEN = 2;
	public static final int DIFFICILE = 3;

	public ControleurMediateur(AireJeu aire_jeu, AireGraphique aire_graphique, MainGUI fenetre, int niveau_IA1, int niveau_IA2, int joueur_commence) {
		this.aire_jeu = aire_jeu;
		this.aire_graphique = aire_graphique;
		this.fenetre = fenetre;

		// si aucun des joueurs n'est une IA
		if(niveau_IA1 == HUMAIN && niveau_IA2 == HUMAIN) {

			this.automate_joueur = new AutomateControleur(
					aire_jeu,
					aire_graphique,
					fenetre,
					joueur_commence
			);

		} else {

			this.automate_joueur = new AutomateControleurIA(
					aire_jeu,
					aire_graphique,
					fenetre,
					joueur_commence,
					creerIA(niveau_IA1, AireJeu.NOIR),
					creerIA(niveau_IA2, AireJeu.BLANC)
			);

		}


	}
	
    /**
     * Creer un jouer IA de certain niveau donne.
	 * @param niveau : niveau d'IA demande
	 * @return joueur IA
     */
    public IA creerIA(int niveau, int couleur) {
    	// Initialisation de joueur IA.
		return switch (niveau) {
			case FACILE -> new AleatoireIA(aire_jeu, couleur);
			case MOYEN -> new StaticMinMaxIA(aire_jeu, couleur, 5);
			case DIFFICILE -> new DynamicMinMaxIA(aire_jeu, couleur, 5);
			default -> null;
		};
	}

    /**
     * Effectue une instruction après un clic (ou un drag-and-drop) de la souris sur la grille
	 * @param instruction l'instruction a effectuer
	 * @param x l'abscisse de la coordonnée de la souris
	 * @param y l'ordonnee de la coordonnée de la souris
     */
    public void instructionSouris(String instruction, int x, int y) {

		switch (instruction) {
			case "Survoler" -> automate_joueur.actionJoueur(AutomateControleur.T.SURVOL, x, y);
			case "Presser" -> automate_joueur.actionJoueur(AutomateControleur.T.PRESSION, x, y);
			case "Glisser" -> automate_joueur.actionJoueur(AutomateControleur.T.DRAG, x, y);
			case "Relacher" -> automate_joueur.actionJoueur(AutomateControleur.T.RELACHEMENT, x, y);
			case "Cliquer" -> automate_joueur.actionJoueur(AutomateControleur.T.CLIC, x, y);
			default -> System.out.println("Le controleur ne connait pas cette instruction souris.");
		}

	}

	/**
     * Effectue une instruction donne apres un click d'un touche clavier.
	 * @param instruction l'instruction a effectuer
     */
    public void instruction(String instruction) {
		switch (instruction) {
			// Le joueur choisit de finir son tour.
			case "Finir tour":
				automate_joueur.actionJoueur(AutomateControleur.T.TERMINER_TOUR);
				break;
			// Annule le dernier coup joue.
			case "Annuler":
				/*if (aire_jeu.annulationCoupPossible()) {
					aire_jeu.annulerCoup();
					automate_joueur.passerTourPrecedent();
				}*/
				break;
			// Refait le dernier coup annule.
			case "Refaire":
				/*if (aire_jeu.refaireCoupPossible()) {
					aire_jeu.refaireCoup();
					automate_joueur.passerTourSuivant();
				}*/
				break;
			// Sauvegarde l'historique actuel de jeu.
			case "Exporter":
				aire_jeu.sauvegarderHistoriqueCoups();
				System.out.println("Demande export d'hisorique.");
				break;
			// Charger un historique de jeu.
			case "Importer":
				aire_jeu.chargeHistoriqueCoups("historique-05_25_2021-15_09_32.txt");
				aire_graphique.repaint();
				//fenetre.mettreAjour();
				System.out.println("Demande import d'historique.");
				break;
			default:
				System.out.println("Le controleur ne connait pas cette instruction.");
		}
	}
    
}