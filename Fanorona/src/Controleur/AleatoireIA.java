package Controleur;

import java.util.Random;
import Modele.*;

/**
 * Joueur IA aleatoire.
 * @author Petrulionyte Ieva
 * @version 1.0
 */
public class AleatoireIA implements IA {
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
     * Genere un coup aleatoire
     * @return un Coup valide
     */
    public Coup donneCoup() {
    	Position debut = new Position(0,0);
    	Position fin = new Position(0,0);
    	Coup coup = new Coup(debut, fin, joueur);
    	while (!aire_jeu.coupValide(coup)) {
    		debut = new Position(randomInRange(0, aire_jeu.getNbLignes()-1), randomInRange(0, aire_jeu.getNbColonnes()-1));
    		fin =  new Position(randomInRange(0, aire_jeu.getNbLignes()-1), randomInRange(0, aire_jeu.getNbColonnes()-1));
    		coup = new Coup(debut, fin, joueur);
    	}
		return coup;
    }

	public boolean faitChoixAspiration() {
		Random r = new Random();
		return r.nextBoolean();
	}

}