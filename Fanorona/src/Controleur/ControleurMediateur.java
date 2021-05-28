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
	/**
	* Joueur actuel, 1 - joueur blanc, 2 - joueur noir.
	*/
	int joueur;
	/**
	* Premier joueur IA qui joue toujours pour les blancs, null si desactive.
	*/
	IA IA1;
	/**
	* Deuxieme joueur IA qui joue toujours pour les noirs, null si desactive.
	*/
	IA IA2;
	public static final int HUMAIN = 0;
	public static final int FACILE = 1;
	public static final int MOYEN = 2;
	public static final int DIFFICILE = 3;

	public ControleurMediateur(AireJeu aire_jeu, AireGraphique aire_graphique, MainGUI fenetre, int niveau_IA1, int niveau_IA2, int joueur_commence) {
		this.aire_jeu = aire_jeu;
		this.aire_graphique = aire_graphique;
		this.fenetre = fenetre;
		// Le joueur qui commence le jeu, blanc ou noir.
		this.joueur = joueur_commence;
		aire_jeu.setJoueur(joueur);
		// Si IA 1 joue, on l'initialise.
		if (niveau_IA1 != 0) {
			// Activation d'IA 1.
			IA1 = creerIA(niveau_IA1, AireJeu.BLANC);
		}
		// Si IA 2 joue, on l'initialise.
		if (niveau_IA2 != 0) {
			// Activation d'IA 2.
			IA2 = creerIA(niveau_IA2, AireJeu.NOIR);
		}
		if (joueur == AireJeu.BLANC && IA1 != null) {
			Coup coup_IA1 = IA1.donneCoup(null);
			joueCoup(coup_IA1);
		} else if (joueur == AireJeu.NOIR && IA2 != null) {
			Coup coup_IA2 = IA2.donneCoup(null);
			joueCoup(coup_IA2);
		}

		fenetre.mettreAjour();
	}
	
    /**
     * Creer un jouer IA de certain niveau donne.
	 * @param niveau : niveau d'IA demande
	 * @return joueur IA
     */
    public IA creerIA(int niveau, int couleur) {
		IA ia = switch (niveau) {
			case FACILE -> new AleatoireIA(aire_jeu, couleur);
			case MOYEN -> new StaticMinMaxIA(aire_jeu, couleur, 5);
			case DIFFICILE -> new DynamicMinMaxIA(aire_jeu, couleur, 5);
			default -> null;
			// Initialisation de joueur IA.
		};
		return ia;
	}

	/**
	 * Effectue une instruction après un clic simple de la souris sur la grille
	 * @param instruction l'instruction à effectuer
	 * @param position la position de la souris sur une case de la grille
	 */
	public void instructionSouris(String instruction, Position position) {
    	instructionSouris(instruction, position, null);
	}

    /**
     * Effectue une instruction après un clic (ou un drag-and-drop) de la souris sur la grille
	 * @param instruction l'instruction a effectuer
	 * @param position_depart la position de départ de la souris
	 * @param position_arrivee la position d'arrivée de la souris (valable si drag-and-drop)
     */
    public void instructionSouris(String instruction, Position position_depart, Position position_arrivee) {
		switch (instruction) {
			case "Jouer":
				creerCoupHumain(position_depart, position_arrivee);
				break;
			default:
				System.out.println("Le controleur ne connait pas cette instruction souris.");
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
				System.out.println("Joueur veut finir son tour.");
				changeJoueur();
				if (joueur == AireJeu.BLANC && IA1 != null) {
					Coup coup_IA1 = IA1.donneCoup(null);
					joueCoup(coup_IA1);
				} else if (joueur == AireJeu.NOIR && IA2 != null) {
					Coup coup_IA2 = IA2.donneCoup(null);
					joueCoup(coup_IA2);
				}
				fenetre.mettreAjour();
				break;
			// Choisit percussion si le joueur a le choix.
			case "Percussion":
				if (aire_jeu.getChoixAspirationPercusion() != null) {
					Coup coup = aire_jeu.getChoixAspirationPercusion();
					aire_jeu.setChoixAspirationPercusion(null);
					coup.setAspiration(false);
					joueCoup(coup);
				}
				break;
			// Choisit l'aspiration si le joueur a le choix.
			case "Aspiration":
				if (aire_jeu.getChoixAspirationPercusion() != null) {
					Coup coup = aire_jeu.getChoixAspirationPercusion();
					aire_jeu.setChoixAspirationPercusion(null);
					coup.setAspiration(true);
					joueCoup(coup);
				}
				break;
			// Annule le dernier coup joue.
			case "Annuler":
				if (aire_jeu.annulationCoupPossible()) {
					aire_jeu.annulerCoup();
					// Changement de joueur.
					changeJoueur();
					aire_graphique.repaint();
					fenetre.mettreAjour();
				}
				break;
			// Refait le dernier coup annule.
			case "Refaire":
				if (aire_jeu.refaireCoupPossible()) {
					aire_jeu.refaireCoup();
					// Changement de joueur.
					changeJoueur();
					aire_graphique.repaint();
					fenetre.mettreAjour();
				}
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
				fenetre.mettreAjour();
				System.out.println("Demande import d'hisorique.");
				break;
			default:
				System.out.println("Le controleur ne connait pas cette instruction souris.");
		}
	}

    /**
     * Joue un coup valide sur la grille de jeu.
	 * @param coup le coup valide a jouer
     */
    public void joueCoup(Coup coup) {
    	// Ici le choix est deja fait donc on dit au model qu'il n'attends plus le choix de joueur.
		aire_jeu.setChoixAspirationPercusion(null);
		boolean effectue_capture = aire_jeu.coupFaitCapture(coup);
    	// On joue le coup.
		aire_jeu.joueCoup(coup);
		System.out.println("Joueur "+joueur+" viens de jouer.");
		System.out.println("Son coup : "+coup);
		// Continuation de tour.
		if (effectue_capture && aire_jeu.joueurPeutContinuerTour(coup.getFin())) {
			System.out.println("Joueur peut continuer.");
		// Changement de joueur.
		} else {
			changeJoueur();
		}
		// Si le joueur actuel est un IA on commence le coup d'IA.
		if (joueur == AireJeu.BLANC && IA1 != null || joueur == AireJeu.NOIR && IA2 != null) {
			Coup coup_ia = null;
			// Si l'IA continue son tour.
			if (effectue_capture && aire_jeu.joueurPeutContinuerTour(coup.getFin())) {
				// On recupere le coup d'IA valide qui commence a la fin de dernier coup.
				if (joueur == AireJeu.BLANC) {
					coup_ia = IA1.donneCoup(coup.getFin());
				} else if (joueur == AireJeu.NOIR) {
					coup_ia = IA2.donneCoup(coup.getFin());
				}
			// Si l'IA commence son tour.
			} else {
				// On recupere le coup d'IA valide.
				if (joueur == AireJeu.BLANC) {
					coup_ia = IA1.donneCoup(null);
				} else if (joueur == AireJeu.NOIR) {
					coup_ia = IA2.donneCoup(null);
				}
			}
			// Si le joueur IA a le choix d'aspiration ou de percusion.
			if (aire_jeu.joueurDoitChoisir(coup_ia)) {
				// On recupere le choix d'aspiration d'IA.
				if (joueur == AireJeu.BLANC) {
					coup_ia.setAspiration(IA1.faitChoixAspiration());
				} else if (joueur == AireJeu.NOIR) {
					coup_ia.setAspiration(IA2.faitChoixAspiration());
				}
			}
			// On joue le coup d'IA valide avec un relentissement.
			CoupLentIA l = new CoupLentIA(this, coup_ia);
		}
    	// On redessine l'aire graphique apres avoir modifie la grille avec le coup joue.
		aire_graphique.repaint();
		fenetre.mettreAjour();
    }

    private void creerCoupHumain(Position debut, Position fin) {
    	System.out.println("Le pion choisi est sur la case "+debut+".");
    	System.out.println("La destination choisie est sur la case "+fin+".");

		// On cree un coup.
		Coup coup = new Coup(debut, fin, joueur);

		if (!aire_jeu.coupValide(coup)) {
			System.out.println("Le coup n'est pas valide, rejoue!");
		// Si le coup est valide, on commence a l'executer.
		} else {
			// Si le joueur a le choix d'aspiration ou de percusion.
			if (aire_jeu.joueurDoitChoisir(coup) && (aire_jeu.getChoixAspirationPercusion() == null)) {
				System.out.println("Joueur "+joueur+" doit choisir entre une aspiration et une percussion.");
				// On dit au model qu'un choix d'utilisateur est attendu.
				aire_jeu.setChoixAspirationPercusion(coup);
			} else {
				// On joue le coup.
				joueCoup(coup);
			}
		}
	}
    
    /**
     * Change de joueur.
     */
    public void changeJoueur() {
    	joueur = joueur == AireJeu.BLANC ? AireJeu.NOIR : AireJeu.BLANC;
		aire_jeu.setJoueur(joueur);
    }
    
}