package Vue;

import Modele.AireJeu;
import Modele.Position;

import java.io.File;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;

import static java.awt.BasicStroke.*;
import java.io.IOException;

/**
 * Cette classe affiche la zone de jeu. Elle est intégrée à MainGUI
 * @author Ieva et Titouan
 */
public class AireGraphique extends JPanel {
    private final AireJeu aire;

    /**
     * Textures utilisées pour l'affichage
     */
    private final Image plateau; //, pion_blanc, pion_noir, pion_blanc_ombre, pion_noir_ombre;

    /**
     * Tailles des éléments affichés et décalages de placement (marges)
     */
    private final Point taille_principale, taille_dessin, taille_plateau;
    private final Point decalage_plateau, decalage_grille;
    private final int taille_cellule, taille_pion;

    /**
     * Informations sur des interactions directes (survol de pions ou glisser-déposer d'un pion)
     */
    private Position position_curseur, position_curseur_temporaire;
    private final Point coordonnees_curseur, coordonnees_curseur_temporaire;
    private boolean aides_sont_affichees, pion_est_maintenu, pion_est_maintenu_temporairement, curseur_survole_pion, magnetisme_est_active;
    private final DoubleAnimable facteur_decalage;

    private ArrayList<Position> chemin_cases, pions_deplacables, cases_accessibles;
    private ArrayList<ArrayList<Position>> pions_supprimables, pions_supprimables_choix_type_coup;

    /**
     * Constructeur
     * @param a une AireJeu (modèle) sur lequel construire une aire graphique
     */
    public AireGraphique(AireJeu a) {
        aire = a;

        // textures utiles
        plateau = chargerTexture("plateau");
        /*pion_blanc = chargerTexture("pion_blanc");
        pion_noir = chargerTexture("pion_noir");
        pion_blanc_ombre = chargerTexture("pion_blanc_ombre");
        pion_noir_ombre = chargerTexture("pion_noir_ombre");*/

		// dimensions relatives entre les éléments
        taille_cellule = 32;
        taille_pion = 16;
        taille_plateau = new Point(288, 160);
        decalage_grille = new Point(taille_pion, taille_pion);

        // dimensions mises à jour en fonction de l'interface
        taille_principale = new Point(1, 1);
        taille_dessin = new Point(0, 0);
        decalage_plateau = new Point(0, 0);

        // informations de positionnement du curseur à l'écran
    	position_curseur = new Position(0,0);
    	coordonnees_curseur = new Point(0,0);
    	coordonnees_curseur_temporaire = new Point(0,0);

    	// booléens pour l'affichage
		aides_sont_affichees = false;
    	pion_est_maintenu = false;
    	pion_est_maintenu_temporairement = false;
    	curseur_survole_pion = false;
    	magnetisme_est_active = false;
    	facteur_decalage = new DoubleAnimable(this, 0., 60, 50);

		// listes de positions pour l'affichage
    	chemin_cases = null;
    	pions_deplacables = null;
    	cases_accessibles = null;
    	pions_supprimables = null;
    	pions_supprimables_choix_type_coup = null;
    }

    /**
     * @param b Change la valeur du booléen indiquant à l'aire d'afficher les coups possibles
     */
    public void afficherAides(boolean b) {
    	aides_sont_affichees = b;
    }

    /**
     * Gère la position courante si le drag and drop est actif.
     * @param p position de départ du curseur
     * @param x coordonnées absolue du curseur
     * @param y coordonnées absolue du curseur
     */
    public void setCurseur(Position p, int x, int y) {
    	position_curseur = p;
    	if(magnetisme_est_active) {
            coordonnees_curseur.setLocation(
                getColonne(x)*taille_cellule + taille_cellule/2,
                getLigne(y)*taille_cellule + taille_cellule/2
            );
		} else {
				coordonnees_curseur.setLocation(ecranVersPlateauX(x), ecranVersPlateauY(y));
		}
			if(!pion_est_maintenu_temporairement)
				coordonnees_curseur_temporaire.setLocation(x, y);
    }

    /**
     * @param b setter pour curseur_survole_pion
     */
    public void setCurseurSurvolePion(boolean b) {
        curseur_survole_pion = b;
    }
    
    /**
     * @param b setter pour pion_est_maintenu
     */
    public void setCurseurMaintienPion(boolean b) {
        pion_est_maintenu = b;
        if(b)
        	facteur_decalage.transitionVersUn();
        else
        	facteur_decalage.transitionVersZero();
    }

    /**
     * @param b setter pour pion_est_maintenu_temporairement
     */
    public void setCurseurMaintienPionTemporaire(boolean b) {
        pion_est_maintenu_temporairement = b;
        position_curseur_temporaire = coordonneesVersPosition(coordonnees_curseur_temporaire.x, coordonnees_curseur_temporaire.y);
    }
    
    /**
     * @param b setter pour magnetisme_est_active
     */
    public void setCurseurMagnetisme(boolean b) {
        magnetisme_est_active = b;
    }

    /**
     * Met à jour le chemin parcouru par le joueur pour affichage
     * @param p setter pour chemin_cases
     */
    public void setCheminJoueur(ArrayList<Position> p) {
    	chemin_cases = p;
    }

    /**
     * Met à jour la liste des pions déplaçables pour affichage
     * @param p setter pour pion_deplacables
     */
    public void setPionsDeplacables(ArrayList<Position> p) {
    	pions_deplacables = p;
    }

    /**
     * Met à jour les cases accessibles pour affichage
     * @param p setter pour cases_accessibles
     */
    public void setCasesAccessibles(ArrayList<Position> p) {
    	cases_accessibles = p;
    }

    public void setPionsSupprimables(ArrayList<ArrayList<Position>> p) {
    	pions_supprimables = p;
	}

	public void setChoixTypeCoups(ArrayList<ArrayList<Position>> p) {
    	pions_supprimables_choix_type_coup = p;
	}

    /**
     * @param nom nom de l'image à charger, sans chemin ni extension
     * @return une Image chargée à partir d'un fichier PNG du dossier Images
     */
    private Image chargerTexture(String nom) {
		Image img = null;
		InputStream in = getClass().getClassLoader().getResourceAsStream("Images/" + nom + ".png");

		try {
			assert in != null;
			img = ImageIO.read(in);
		} catch (IOException e) {
            System.err.println("Erreur au chargement de l'image : " + "Images" + File.separator + nom + ".png");
            System.err.println(e);
            System.exit(1);
        }

        return img;
    }

    /**
     * Permet d'actualiser les dimensions de l'affichage en cas de redimensionnement de la fenêtre principale
     */
    private void miseAjourDimensions() {
        if(taille_principale.x != getSize().width || taille_principale.y != getSize().height) {
            taille_principale.setLocation(getSize().width, getSize().height);

            double taille_principale_ratio = (double)taille_principale.x/(double)taille_principale.y;
            double taille_dessin_ratio = (double)AireJeu.NB_COLONNES/(double)AireJeu.NB_LIGNES;

            if(taille_principale_ratio >= taille_dessin_ratio) {
            	taille_dessin.setLocation((int)(taille_plateau.y*taille_principale_ratio), taille_plateau.y);
            	decalage_plateau.setLocation((taille_dessin.x - taille_plateau.x)/2, 0);
            } else {
            	taille_dessin.setLocation(taille_plateau.x, (int)(taille_plateau.x/taille_principale_ratio));
            	decalage_plateau.setLocation(0, (taille_dessin.y - taille_plateau.y)/2);
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
    	Graphics2D ctx = (Graphics2D)g;

    	// met à jour les dimensions si besoin
        miseAjourDimensions();

        // efface le précédent dessin du canvas
    	ctx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		ctx.clearRect(0, 0, getWidth(), getHeight());

		// transformations de l'affichage
		final double scale = (double)getWidth()/taille_dessin.x;
		ctx.scale(scale, scale);
    	ctx.translate(decalage_plateau.x, decalage_plateau.y);
    	AffineTransform transformation_copy =  ctx.getTransform();

    	// dessin du fond du plateau de jeu
    	dessinerPlateau(ctx);

    	ctx.translate(decalage_grille.x, decalage_grille.y);

    	// dessin du chemin de coups
        dessinerChemin(ctx, chemin_cases);

        // dessin des pions à l'écran
    	dessinerPions(ctx);
    	dessinerPionTemporaire(ctx);

    	// dessin des indications
		if(aides_sont_affichees) {
			dessinerIndicationCasesAccessibles(ctx, cases_accessibles);
			dessinerIndicationPionsSupprimables(ctx, pions_supprimables);
			dessinerIndicationPionsJouables(ctx, pions_deplacables);
		}

		dessinerIndicationChoixTypeCoup(ctx, pions_supprimables_choix_type_coup);

		// dessin du pion en drag and drop
		ctx.setTransform(transformation_copy);
    	dessinerPionDeplacable(ctx);
    }

    private void dessinerPlateau(Graphics2D ctx) {
    	ctx.drawImage(plateau, 0, 0, taille_plateau.x, taille_plateau.y, null);
    }

    private void dessinerUnPion(Graphics2D ctx, Position position, int joueur) {
        dessinerUnPion(
            ctx,
            position.getColonne()*taille_cellule,
            position.getLigne()*taille_cellule,
            joueur
		);
    }

    private void dessinerUnPion(Graphics2D ctx, int x, int y, int joueur) {
        /*dessinerUnElementPion(
            ctx,
            joueur == AireJeu.BLANC ? pion_blanc : pion_noir,
            x,
            y
	);*/
        int taille_pion_moitie = taille_pion/2;
        Ellipse2D cercle = new Ellipse2D.Double(x - taille_pion_moitie, y - taille_pion_moitie, taille_pion, taille_pion);
        ctx.setColor(joueur == AireJeu.BLANC ? new Color(255, 255, 255) : new Color(50, 50, 50));
        ctx.fill(cercle);
        ctx.setStroke(new BasicStroke(2));
        ctx.setColor(new Color(0));
        ctx.draw(cercle);
    }

    private void dessinerUnElementPion(Graphics2D ctx, Image image, int x, int y) {
    	int taille_pionnn = taille_pion + 1;
        int taille_pion_moitie = taille_pionnn/2;
        /*ctx.drawImage(
            image,
            x - taille_pion_moitie,
            y - taille_pion_moitie,
            taille_pion, taille_pion, null
        );*/

        ctx.setColor(new Color(50, 50, 50, 150));
        ctx.fill(new Ellipse2D.Double(x - taille_pion_moitie, y - taille_pion_moitie, taille_pionnn, taille_pionnn));
    }

    private void dessinerPions(Graphics2D ctx) {
        for (int colonne = 0; colonne < AireJeu.NB_COLONNES; colonne++) {
		for (int ligne = 0; ligne < AireJeu.NB_LIGNES; ligne++) {
			Position position = new Position(ligne, colonne);

			int joueur = aire.getCaseGrille(position);

			if(
				joueur == 0
				|| (
					(pion_est_maintenu || pion_est_maintenu_temporairement)
					&& position_curseur.equals(position)
				)
			)
				continue;

			dessinerUnPion(ctx, new Position(ligne, colonne), joueur);
		}
        }
    }

    private void dessinerPionTemporaire(Graphics2D ctx) {
        if(!pion_est_maintenu_temporairement)
            return;
        dessinerUnPion(ctx, position_curseur_temporaire, aire.getCaseGrille(position_curseur));
    }

    private void dessinerPionDeplacable(Graphics2D ctx) {
        if(!pion_est_maintenu)
            return;

    	//Image pion_ombre = aire.getCaseGrille(position_curseur) == AireJeu.BLANC ? pion_blanc_ombre : pion_noir_ombre;
        int decalage_ombre = (int)(((double)taille_pion/4)*facteur_decalage.getValeur());
		dessinerUnElementPion(
        		ctx,
				null,
				coordonnees_curseur.x + decalage_ombre,
				coordonnees_curseur.y + decalage_ombre
		);

    	float[] dash_array = {10f, 7f};
    	ctx.setColor(new Color(60,150,220));
    	ctx.setStroke(new BasicStroke(3, CAP_ROUND, JOIN_MITER, 1,  dash_array, 0));
    	ctx.draw(new Line2D.Float(
            coordonnees_curseur.x, coordonnees_curseur.y,
            taille_cellule*position_curseur.getColonne() + decalage_grille.x,
            taille_cellule*position_curseur.getLigne() + decalage_grille.y
        ));

        dessinerUnPion(ctx, coordonnees_curseur.x, coordonnees_curseur.y, aire.getCaseGrille(position_curseur));
    }

    private void dessinerIndicationPionsJouables(Graphics2D ctx, ArrayList<Position> positions) {
        if(positions == null)
            return;

        int taille_moitie = taille_pion/2;

        ctx.setStroke(new BasicStroke(2));
        ctx.setColor(new Color(64, 221, 64));
		for (Position p : positions) {
			ctx.draw(new Ellipse2D.Float(
					p.getColonne()*taille_cellule - taille_moitie,
					p.getLigne()*taille_cellule - taille_moitie,
					taille_pion, taille_pion
			));
		}
	}

	private void dessinerIndicationChoixTypeCoup(Graphics2D ctx, ArrayList<ArrayList<Position>> listes_positions) {
    	if(pions_supprimables_choix_type_coup == null)
    		return;

        int taille_double = taille_pion*2;

    	ArrayList<Position> liste_positions_aspiration = listes_positions.get(0);

    	Position position_min = Position.getPositionMin(liste_positions_aspiration);
    	Position position_taille = Position.getPositionMax(liste_positions_aspiration).soustraire(position_min);
        float[] dash_array = {10f, 7f};
    	ctx.setStroke(new BasicStroke(2, CAP_ROUND, JOIN_ROUND, 1, dash_array, 0));
        ctx.setColor(new Color(119, 64, 221));
    	ctx.draw(new Rectangle2D.Float(
			position_min.getColonne()*taille_cellule - taille_pion,
			position_min.getLigne()*taille_cellule - taille_pion,
			position_taille.getColonne()*taille_cellule + taille_double,
			position_taille.getLigne()*taille_cellule + taille_double
		));

    	ArrayList<Position> liste_positions_percussion = listes_positions.get(1);

    	position_min = Position.getPositionMin(liste_positions_percussion);
    	position_taille = Position.getPositionMax(liste_positions_percussion).soustraire(position_min);
    	ctx.draw(new Rectangle2D.Float(
			position_min.getColonne()*taille_cellule - taille_pion,
			position_min.getLigne()*taille_cellule - taille_pion,
			position_taille.getColonne()*taille_cellule + taille_double,
			position_taille.getLigne()*taille_cellule + taille_double
		));

	}

    private void dessinerIndicationCasesAccessibles(Graphics2D ctx, ArrayList<Position> positions) {
        if(positions == null)
            return;

    	int taille_moitie = taille_pion/2;

        ctx.setStroke(new BasicStroke(2));
        ctx.setColor(new Color(64, 221, 64));
    	positions.forEach(p -> ctx.draw(new Ellipse2D.Float(
			p.getColonne()*taille_cellule - taille_moitie,
			p.getLigne()*taille_cellule - taille_moitie,
			taille_pion, taille_pion
		)));
    }

    private void dessinerIndicationPionsSupprimables(Graphics2D ctx, ArrayList<ArrayList<Position>> liste_positions) {
    	if(liste_positions == null)
    		return;

        int taille_moitie = taille_pion/2;

        ctx.setStroke(new BasicStroke(3));
        ctx.setColor(new Color(255,0,0));

        for(ArrayList<Position> positions : liste_positions) {
        	for(Position position : positions) {
				ctx.draw(new Line2D.Float(
					position.getColonne()*taille_cellule - taille_moitie,
					position.getLigne()*taille_cellule - taille_moitie,
					position.getColonne()*taille_cellule + taille_moitie,
					position.getLigne()*taille_cellule + taille_moitie
				));
				ctx.draw(new Line2D.Float(
					position.getColonne()*taille_cellule + taille_moitie,
					position.getLigne()*taille_cellule - taille_moitie,
					position.getColonne()*taille_cellule - taille_moitie,
					position.getLigne()*taille_cellule + taille_moitie
				));
			}
        }

    }

    private void dessinerChemin(Graphics2D ctx, ArrayList<Position> positions) {
        if(positions.size() == 0 && !pion_est_maintenu_temporairement)
            return;

    	Path2D path = new Path2D.Float();

    	if(pion_est_maintenu_temporairement) {
            path.moveTo(
                taille_cellule*position_curseur_temporaire.getColonne(),
                taille_cellule*position_curseur_temporaire.getLigne()
            );
            if(positions.size() == 0) {
                path.lineTo(
                    taille_cellule*position_curseur.getColonne(),
                    taille_cellule*position_curseur.getLigne()
                );
            } else {
            	positions.forEach((Position p) -> path.lineTo(p.getColonne()*taille_cellule, p.getLigne()*taille_cellule));
            }
        } else {
            path.moveTo(positions.get(0).getColonne()*taille_cellule, positions.get(0).getLigne()*taille_cellule);
            positions.forEach((Position p) -> path.lineTo(p.getColonne()*taille_cellule, p.getLigne()*taille_cellule));
        }

    	ctx.setStroke(new BasicStroke(3, CAP_ROUND, JOIN_ROUND));
    	ctx.setColor(new Color(60,150,220));
    	ctx.draw(path);
    }

    /**
     * @param x l'abscisse de la coordonnée à convertir
     * @return convertit l'abscisse absolue en abscisse locale dans le plateau
     */
    private int ecranVersPlateauX(int x) {
        return (x*taille_dessin.x)/taille_principale.x - decalage_plateau.x;
    }

    /**
     * @param y l'ordonnée de la coordonnée à convertir
     * @return convertit l'ordonnée absolue en ordonnée locale dans le plateau
     */
    private int ecranVersPlateauY(int y) {
        return (y*taille_dessin.y)/taille_principale.y - decalage_plateau.y;
    }

    /**
     * @param x valeur x de la coordonnée
     * @param y valeur y de la coordonnée
     * @return true si (x, y) se trouve dans la zone du plateau de jeu
     */
    public boolean collisionPlateau(int x, int y) {
        x = ecranVersPlateauX(x);
        y = ecranVersPlateauY(y);

        return x >= 0 && x < taille_plateau.x && y >= 0 && y < taille_plateau.y;
    }

    /**
     * @param x abscisse de la coordonnée testée
     * @param y ordonnée de la coordonnée testée
     * @return Renvoie vrai si la coordonnée est sur une zone de pion
     */
    public boolean collisionZonePion(int x, int y) {
    	if(!collisionPlateau(x, y))
    		return false;

        int taille_pion_moitie = taille_pion/2;
        int taille_cellule_moitie = taille_cellule/2;
    	return Math.abs(ecranVersPlateauX(x)%taille_cellule - taille_cellule_moitie) < taille_pion_moitie
            && Math.abs(ecranVersPlateauY(y)%taille_cellule - taille_cellule_moitie) < taille_pion_moitie;
    }

    /**
     * @param x l'abscisse de la coordonnée
     * @return renvoie la colonne associée à l'abscisse absolue
     */
    private int getColonne(int x) {
        return ecranVersPlateauX(x)/taille_cellule;
    }

    /**
     * @param y l'ordonnée de la coordonnée
     * @return renvoie la ligne associée à l'ordonnée absolue
     */
    private int getLigne(int y) {
        return ecranVersPlateauY(y)/taille_cellule;
    }

    /**
     * @param x l'abscisse de la coordonnée
     * @param y l'ordonnée de la coordonnée
     * @return renvoie une position en fonction de coordonnées en absolu
     */
    public Position coordonneesVersPosition(int x, int y) {
        return new Position(getLigne(y), getColonne(x));
    }


    private double positionVersX(int colonne) {
        return (((double)colonne*(double)taille_cellule + decalage_plateau.x + decalage_grille.x)*taille_principale.x)/taille_dessin.x;
    }

    private double positionVersY(int ligne) {
        return (((double)ligne*(double)taille_cellule + decalage_plateau.y + decalage_grille.y)*taille_principale.y)/taille_dessin.y;
    }

    /**
     * @param p une position sur le plateau
     * @return les coordonnées de cette position sur le plateau de jeu
     */
    public Point positionVersCoordonnees(Position p) {
        return new Point((int)positionVersX(p.getColonne()), (int)positionVersY(p.getLigne()));
    }

}
