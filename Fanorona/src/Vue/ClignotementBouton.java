package Vue;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.Timer;

/**
 * Permet de faire clignoter un bouton
 * @author Marin
 */
public class ClignotementBouton {
    /**
     * Le timer pour changer la couleur du bouton
     */
    private final Timer timer_clignotement;
    /**
     * Méthode qui est appelée par le timer
     */
    private final ActionListener taskPerformerTimer;
    /**
     * Le bouton à faire clignoter
     */
    private final JButton bouton;
    /**
     * Compteur pour faire cycler les couleurs
     */
    private double compteur;
    private final Color couleur_depart = Color.WHITE;
    private final Color couleur_arrivee = new Color(0x21c71c);
    
    /**
     * Constructeur
     * @param b le bouton à faire clignoter
     */
    ClignotementBouton(JButton b) {
        taskPerformerTimer = (ActionEvent evt) -> faireClignoterBouton();
        timer_clignotement = new Timer(100, taskPerformerTimer);
        bouton = b;
    }
    
    /**
     * Interpolation linéraire
     * @param a a
     * @param b b
     * @param t c
     * @return l'interpolation linéaire entre a, b et c
     */
    private int lerp(int a, int b, double t) {
        return (int) (a + t * (b - a));
    }
    
    /**
     * Réalise une interpolation entre deux couleurs
     * @param depart couleur de départ
     * @param arrivee couleur d'arrivée
     * @param facteur facteur d'interpolation, entre 0.0 et 1.0
     * @return la couleur interpolée
     */
    private Color interpolCouleur(Color depart, Color arrivee, double facteur) {
        return new Color(
            lerp(depart.getRed(), arrivee.getRed(), facteur),
            lerp(depart.getGreen(), arrivee.getGreen(), facteur),
            lerp(depart.getBlue(), arrivee.getBlue(), facteur)
        );
    }
    
    /**
     * Appelée par le timer_clignotement pour mettre en évidence le bouton terminer
     */
    private void faireClignoterBouton() {
        // si le bouton est désactivé, lui laisser le style désactivé
        if (!bouton.isEnabled()) {
            bouton.setForeground(new Color(51, 51, 51));
            bouton.setBackground(new Color(238, 238, 238));
        } else {
            compteur += 0.4;
            Color nouvelle_couleur = interpolCouleur(
                couleur_depart,
                couleur_arrivee,
                Math.cos(compteur)*.5 + .5
            );
            
            bouton.setBackground(nouvelle_couleur);
        }
    }
    
    /**
     * @param b active ou désactive le clignotement du bouton
     */
    public void setClignotementBouton(boolean b) {
        if (b) {
            compteur = 0.0;
            timer_clignotement.start();
        } else {
            timer_clignotement.stop();
            bouton.setBackground(Color.WHITE);
        }
    }
}
