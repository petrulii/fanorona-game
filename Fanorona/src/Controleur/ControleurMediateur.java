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
	/**
	* Joueur actuel, 1 - joueur blanc, 2 - joueur noir.
	*/
	int joueur;
	/**
	* Activation IA, 0 - desactiver, 1 - IA joue premier joueur, 2 - IA joue deuxieme joueur.
	*/
	int active_IA;
	MinMaxIA ia;//AleatoireIA ia;

	public ControleurMediateur(AireJeu aire_jeu, AireGraphique aire_graphique) {
		this.aire_jeu = aire_jeu;
		this.aire_graphique = aire_graphique;
		// Les blancs commence le jeu.
		this.joueur = aire_jeu.BLANC;
		// Si l'attribut debut est null alors on est au debut de creation d'un coup.
		this.debut = null;
		this.active_IA = 0;
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
				creerCoupHumain(ligne, colonne);
				break;
			default:
				System.out.println("Le controleur ne connait pas cette instruction souris.");
		}
	}

	/**
     * Effectue une instruction donne apres un click d'un touche clavier.
	 * @param l'instruction a effectuer
     */
    public void instruction(String instruction) {
		switch (instruction) {
			// Choisit percusion si le joueur a le choix.
			case "Percusion":
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
				}
				break;
			// Refait le dernier coup annule.
			case "Refaire":
				if (aire_jeu.refaireCoupPossible()) {
					aire_jeu.refaireCoup();
					if (aire_jeu.gameOver()) {
						System.out.println("Game Over!");
						System.exit(0);
					}
					// Changement de joueur.
					changeJoueur();
					aire_graphique.repaint();
				}
				break;
			// Sauvegarde l'historique actuel de jeu.
			case "Exporter":
				aire_jeu.sauvegarderHistoriqueCoups();
				System.out.println("Demande export d'hisorique.");
				break;
			// Active le joueur IA, le joueur actuel va etre remplace par un IA.
			case "Activer IA":
				ia = new MinMaxIA(aire_jeu, joueur);//new AleatoireIA(aire_jeu, joueur);
				active_IA = joueur;
				Coup coup_ia = ia.donneCoup(null);
				joueCoup(coup_ia);
				break;
			default:
				System.out.println("Le controleur ne connait pas cette instruction souris.");
		}
	}

    /**
     * Joue un coup valide sur la grille de jeu.
	 * @param le coup valide a jouer
     */
    public void joueCoup(Coup coup) {
    	// Ici le choix est deja fait donc on dit au model qu'il n'attends plus le choix de joueur.
		aire_jeu.setChoixAspirationPercusion(null);
		boolean effectue_capture = aire_jeu.coupFaitCapture(coup);
    	// On joue le coup.
		aire_jeu.joueCoup(coup);
		System.out.println("Joueur "+joueur+" viens de jouer.");
		System.out.println("Son coup : "+coup);
    	// On redessine l'aire graphique apres avoir modifie la grille avec le coup joue.
		aire_graphique.repaint();
    	// Si le joue est termine.
		if (aire_jeu.gameOver()) {
			System.out.println("Game Over!");
	    	// On quitte le program.
			System.exit(0);
		}
		// Changement de joueur.
		if (effectue_capture && aire_jeu.joueurPeutContinuerTour(coup.getFin())) {
			System.out.println("Joueur peut continuer.");
		} else {
			changeJoueur();
		}
		// Si le joueur actuel est un IA on commence le coup d'IA.
		if (ia != null && active_IA == joueur) {
			Coup coup_ia;
			if (coup != null && effectue_capture && aire_jeu.joueurPeutContinuerTour(coup.getFin())) {
				// On recupere le coup d'IA valide qui commence a la fin de dernier coup.
				coup_ia = ia.donneCoup(coup.getFin());
			} else {
				// On recupere le coup d'IA valide.
				coup_ia = ia.donneCoup(null);
			}
			// Si le joueur IA a le choix d'aspiration ou de percusion.
			if (aire_jeu.joueurDoitChoisir(coup_ia)) {
				// On choisit aleatoirement.
				coup.setAspiration(ia.faitChoixAspiration());
			}
			// On joue le coup d'IA valide avec un relentissement.
			CoupLentIA l = new CoupLentIA(this, coup_ia);
		}
    }

    /**
     * Prend les coorodnees ou joueur a clice et essaie de creer un coup.
	 * @param les coorodnees ou joueur a clice sur la grille de jeu
     */
    private void creerCoupHumain(int ligne, int colonne) {
		// Si le joueur n'a pas choisi son pion.
		if (debut == null) {
			// On initialise son pion choisi.
			debut = new Position(ligne, colonne);
			System.out.println("Le pion choisi est sur la case "+debut+".");
		// Si le joueur a deja choisi son pion est maintenant choisit ou mettre son pion.
		} else {
			// On initialise la destination de pion choisi.
			Position fin = new Position(ligne, colonne);
			System.out.println("La destination choisi est sur la case "+fin+".");
			// On cree un coup.
			Coup coup = new Coup(debut, fin, joueur);
			// Aucun pion n'est plus choisi apres la creation d'un coup.
			debut = null;
			// Si le coup cree n'est pas valide.
			if (!aire_jeu.coupValide(coup)) {
				System.out.println("Le coup n'est pas valide, rejoue!");
			// Si le coup est valide, on commence a l'executer.
			} else {
				// Si le joueur a le choix d'aspiration ou de percusion.
				if (aire_jeu.joueurDoitChoisir(coup) && (aire_jeu.getChoixAspirationPercusion() == null)) {
					System.out.println("Joueur "+joueur+" doit choisir entre aspiration et percusion. Touche UP pour aspiration, touche DOWN pour percution.");
					// On dit au model qu'un choix d'utilisateur est attendu.
					aire_jeu.setChoixAspirationPercusion(coup);
				} else {
					// On joue le coup.
					joueCoup(coup);
				}
			}
		}
	}
    
    /**
     * Change de joueur.
     */
    public void changeJoueur() {
		if (joueur == aire_jeu.BLANC) { joueur = aire_jeu.NOIR; } else { joueur = aire_jeu.BLANC; }
    }
    
}