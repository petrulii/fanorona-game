package Controleur;

import java.util.ArrayList;
import java.util.Random;
import Modele.*;

/**
 * Joueur IA aleatoire.
 * @author Petrulionyte Ieva
 * @version 1.0
 */
public class AleatoireIA extends IA {
    AireJeu aire_jeu;
    int joueur;

    public AleatoireIA(AireJeu a, int j) {
        aire_jeu = a;
        joueur = j;
    }

    /**
     * Genere un entier aleatoire entre les entiers min et max
	 * @param l'entier min
	 * @param l'entier max
     * @return un entier aleatoire entre min et max
     */
    public int randomInRange(int min, int max) {
    	Random r = new Random();
        return r.nextInt((max-min)+1)+min; 
    }
    
    /**
     * Genere un coup aleatoire.
     * @return un Coup valide
     */
    public Coup donneCoup(Position debut) {
    	boolean debut_null = false;
    	if (debut == null) {
        	debut_null = true;
        	debut = new Position(0,0);
    	}
    	Position fin = new Position(0,0);
    	Coup coup = new Coup(debut, fin, joueur);
    	while (!aire_jeu.coupValide(coup)) {
        	if (debut_null) {
        		debut =  new Position(randomInRange(0, AireJeu.NB_LIGNES-1), randomInRange(0, AireJeu.NB_COLONNES-1));
        	}
    		fin =  new Position(randomInRange(0, AireJeu.NB_LIGNES-1), randomInRange(0, AireJeu.NB_COLONNES-1));
    		coup = new Coup(debut, fin, joueur);
    	}
		return coup;
    }

    /**
     * Choisit un coup aleatoirement dans la liste des coups possibles.
     * @param la liste des coups possibles
     * @return un Coup valide
     */
    public Coup donneCoup(ArrayList<Coup> coupsPossibles) {
		int index = randomInRange(0, coupsPossibles.size()-1);
		return coupsPossibles.get(index);
	}

	/**
     * Fait un choix aleatoire entre l'aspiration et percusion.
     * @return vrai si choix d'aspiration, faux sinon
     */
    @Override
	public boolean faitChoixAspiration() {
		Random r = new Random();
		return r.nextBoolean();
	}

}