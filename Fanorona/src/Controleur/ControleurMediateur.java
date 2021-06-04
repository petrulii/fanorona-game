package Controleur;
import Modele.AireJeu;
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


		System.out.println(niveau_IA2);
		// si aucun des joueurs n'est une IA
		if(niveau_IA1 == HUMAIN && niveau_IA2 == HUMAIN)
			this.automate_joueur = new AutomateControleur(aire_jeu, aire_graphique, fenetre, joueur_commence);
		else {
			this.automate_joueur = new AutomateControleurIA(aire_jeu, aire_graphique, fenetre, joueur_commence,
					creerIA(niveau_IA1, AireJeu.NOIR),
					creerIA(niveau_IA2, AireJeu.BLANC)
			);
		}
	}

	/**
	 * Creer un jouer IA de certain niveau donne.
	 * @param niveau : niveau d'IA demande
	 * @param couleur : le couleur d'IA
	 * @return : IA cree
	 */
    public IA creerIA(int niveau, int couleur) {
    	// Initialisation de joueur IA.
		switch (niveau) {
			case FACILE:
				return new AleatoireIA(aire_jeu, couleur);
			case MOYEN:
				return new AlphaBetaIA(aire_jeu, couleur, 2);
			case DIFFICILE:
				return new AlphaBetaIA(aire_jeu, couleur, 6);
			default:
				return null;
		}
	}

    /**
     * Effectue une instruction apr��s un clic (ou un drag-and-drop) de la souris sur la grille
	 * @param instruction : l'instruction a effectuer
	 * @param x : l'abscisse de la coordonn��e de la souris
	 * @param y : l'ordonnee de la coordonn��e de la souris
     */
    public void instructionSouris(String instruction, int x, int y) {

		switch (instruction) {
			case "Survoler":
				automate_joueur.actionJoueur(AutomateControleur.T.SURVOL, x, y);
				break;
			case "Presser":
				automate_joueur.actionJoueur(AutomateControleur.T.PRESSION, x, y);
				break;
			case "Glisser":
				automate_joueur.actionJoueur(AutomateControleur.T.DRAG, x, y);
				break;
			case "Relacher":
				automate_joueur.actionJoueur(AutomateControleur.T.RELACHEMENT, x, y);
				break;
			case "Cliquer":
				automate_joueur.actionJoueur(AutomateControleur.T.CLIC, x, y);
				break;
			default:
				System.out.println("Le controleur ne connait pas cette instruction souris.");
				break;
		}
	}

	/**
     * Effectue une instruction donne apres un click d'un touche clavier.
	 * @param instruction : l'instruction a effectuer
     */
    public void instruction(String instruction) {
		// Charger un historique de jeu.
		switch (instruction) {
			case "Finir tour":
				automate_joueur.actionJoueur(AutomateControleur.T.TERMINER_TOUR);
				break;
			case "Finir partie":
				automate_joueur.terminerPartieDeForce();
				break;
			case "Annuler":
				automate_joueur.annulerCoup();
				break;
			case "Refaire":
				automate_joueur.refaireCoup();
				break;
			case "Exporter":
				String nom_fichier_exporte = aire_jeu.sauvegarderHistoriqueCoups();
				fenetre.afficherInformationNomFichierExport(nom_fichier_exporte);
				break;
			default:
				System.out.println("Le controleur ne connait pas cette instruction.");
				break;
		}
	}
    
    /**
     * Demande au contrôleur de charger une partie. Est séparé des autres instructions car demande un paramètre en plus.
     * @param nom le nom du fichier de sauvegarde à charger.
     */
    public void instructionImporter(String nom) {
        aire_jeu.chargeHistoriqueCoups(nom);
        automate_joueur.majApresChangementHistorique(aire_jeu.getDernierCoup());
    }
    
}