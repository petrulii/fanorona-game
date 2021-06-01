package Vue;

import Modele.AireJeu;
import Modele.Position;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;

import static java.awt.BasicStroke.*;

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
    private final int taille_cellule;
	private final int taille_pion;

	/**
	 * Informations sur des interactions directes (survol de pions ou glisser-déposer d'un pion)
	 */
    private Position position_curseur, position_curseur_temporaire;
    private final Point coordonnees_curseur, coordonnees_curseur_temporaire;
    private boolean pion_est_maintenu, pion_est_maintenu_temporairement, curseur_survole_pion, magnetisme_est_active;

    private ArrayList<Position> chemin_joueur, pions_deplacables, cases_accessibles;

	/**
	 * Image en mémoire tampon dans laquelle on dessine le jeu
	 */
	private BufferedImage dessin_jeu;

	private boolean aides_sont_affichees;

    public AireGraphique(AireJeu a) {
        aire = a;

        plateau = chargerTexture("plateau");
        /*pion_blanc = chargerTexture("pion_blanc");
        pion_noir = chargerTexture("pion_noir");
        pion_blanc_ombre = chargerTexture("pion_blanc_ombre");
        pion_noir_ombre = chargerTexture("pion_noir_ombre");*/

        taille_cellule = 32;
        taille_pion = 16;

        taille_principale = new Point(1, 1);
        taille_dessin = new Point(0, 0);
        taille_plateau = new Point(288, 160);
        decalage_plateau = new Point(0, 0);
        decalage_grille = new Point(taille_pion, taille_pion);

		position_curseur = new Position(0,0);
		coordonnees_curseur = new Point(0,0);
		coordonnees_curseur_temporaire = new Point(0,0);
		pion_est_maintenu = false;
		curseur_survole_pion = false;
		magnetisme_est_active = false;

		chemin_joueur = null;
		pions_deplacables = null;
		cases_accessibles = null;
    }

    public void afficherAides(boolean b) {
    	aides_sont_affichees = b;
	}

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
	public void setCurseurSurvolePion(boolean b) {
    	curseur_survole_pion = b;
	}
	public void setCurseurMaintienPion(boolean b) {
    	pion_est_maintenu = b;
	}
	public void setCurseurMaintienPionTemporaire(boolean b) {
    	pion_est_maintenu_temporairement = b;
    	position_curseur_temporaire = coordonneesVersPosition(coordonnees_curseur_temporaire.x, coordonnees_curseur_temporaire.y);
	}
	public void setCurseurMagnetisme(boolean b) {
    	magnetisme_est_active = b;
	}

	public void setCheminJoueur(ArrayList<Position> p) {
    	chemin_joueur = p;
	}
	public void afficherPionsDeplacables(ArrayList<Position> p) {
    	pions_deplacables = p;
	}
	public void afficherCasesAccessibles(ArrayList<Position> p) {
    	cases_accessibles = p;
	}

    private Image chargerTexture(String nom) {
		Image img = null;
		InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream("Images" + File.separator + nom + ".png");

		try {
			assert in != null;
			img = ImageIO.read(in);
		} catch (Exception e) {
			System.out.println("Erreur au chargement de l'image : " + "Images" + File.separator + nom + ".png");
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

			dessin_jeu = new BufferedImage(taille_dessin.x, taille_dessin.y, BufferedImage.TYPE_INT_RGB);
        }
    }

	@Override
	public void paintComponent(Graphics g) {
		miseAjourDimensions();

    	Graphics2D ctx = (Graphics2D)g;
    	//Graphics2D ctx = dessin_jeu.createGraphics();
    	ctx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		ctx.clearRect(0, 0, getWidth(), getHeight());

		double scale = (double)getWidth()/taille_dessin.x;
		ctx.scale(scale, scale);

    	ctx.translate(decalage_plateau.x, decalage_plateau.y);

    	dessinerPlateau(ctx);

    	AffineTransform transformation_copy =  ctx.getTransform();
    	ctx.translate(decalage_grille.x, decalage_grille.y);
		dessinerIndicationCasesAccessibles(ctx, cases_accessibles);
        dessinerChemin(ctx, chemin_joueur);
    	dessinerPions(ctx);
    	dessinerPionTemporaire(ctx);
        //indiquerPionsSupprimables(ctx, positions);
		dessinerIndicationPionsJouables(ctx, pions_deplacables);

		ctx.setTransform(transformation_copy);
    	dessinerPionDeplacable(ctx);

    	//Graphics2D main_ctx = (Graphics2D)g;
		//ctx.clearRect(0, 0, getWidth(), getHeight());
    	//ctx.drawImage(dessin_jeu, 0, 0, getWidth(), getHeight(), null);
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
				x, y
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
		int taille_pion_moitie = taille_pion/2;
		/*ctx.drawImage(
				image,
				x - taille_pion_moitie,
				y - taille_pion_moitie,
				taille_pion, taille_pion, null
		);*/

		ctx.setColor(new Color(50, 50, 50, 150));
		ctx.fill(new Ellipse2D.Double(x - taille_pion_moitie, y - taille_pion_moitie, taille_pion, taille_pion));
	}

	private void dessinerPions(Graphics2D ctx) {
		for (int colonne = 0; colonne < AireJeu.NB_COLONNES; colonne++) {
		for (int ligne = 0; ligne < AireJeu.NB_LIGNES; ligne++) {
			Position position = new Position(ligne, colonne);

			int joueur = aire.getCaseGrille(position);

			if(
					joueur == 0
					|| ((pion_est_maintenu || pion_est_maintenu_temporairement) && position_curseur.equals(position))
			) continue;

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
		dessinerUnElementPion(ctx, null, coordonnees_curseur.x + taille_pion/4, coordonnees_curseur.y + taille_pion/4);

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
    	positions.forEach((Position p) -> {
    		ctx.draw(new Ellipse2D.Float(
				p.getColonne()*taille_cellule - taille_moitie,
				p.getLigne()*taille_cellule - taille_moitie,
				taille_pion, taille_pion
			));
		});
	}

	private void dessinerIndicationCasesAccessibles(Graphics2D ctx, ArrayList<Position> positions) {
		if(positions == null)
			return;

    	int taille_moitie = taille_pion/2;

		ctx.setStroke(new BasicStroke(2));
		ctx.setColor(new Color(64, 221, 64));
    	positions.forEach((Position p) -> {
    		ctx.draw(new Ellipse2D.Float(
				p.getColonne()*taille_cellule - taille_moitie,
				p.getLigne()*taille_cellule - taille_moitie,
				taille_pion, taille_pion
			));
		});
	}

	private void dessinerIndicationPionsSupprimables(Graphics2D ctx, ArrayList<Position> positions) {
		int taille_moitie = taille_pion/2;

		ctx.setStroke(new BasicStroke(3));
		ctx.setColor(new Color(255,0,0));
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
    			path.lineTo(positions.get(0).getColonne()*taille_cellule, positions.get(0).getLigne()*taille_cellule);
			}
		} else {
    		path.moveTo(positions.get(0).getColonne()*taille_cellule, positions.get(0).getLigne()*taille_cellule);
    		positions.forEach((Position p) -> path.lineTo(p.getColonne()*taille_cellule, p.getLigne()*taille_cellule));
		}


    	if(pion_est_maintenu_temporairement) {
		}

    	ctx.setStroke(new BasicStroke(3, CAP_ROUND, JOIN_ROUND));
    	ctx.setColor(new Color(60,150,220));
    	ctx.draw(path);
	}

	/**
	 * @return la largeur de l'aire graphique
	 */
	public int getWidth() {
		return getSize().width;
	}

	/**
	 * @return la hauteur de l'aire graphique
	 */
	public int getHeight() {
		return getSize().height;
	}

	/**
	 * @param x abscisse de la coordonnée testée
	 * @param y ordonnée de la coordonnée testée
	 * @return Renvoie vrai si la coordonnée est sur une zone de pion
	 */
	public boolean collisionZonePion(int x, int y) {
    	return collisionZonePion(x, y, 1);
	}

	/**
	 * @param x abscisse de la coordonnée testée
	 * @param y ordonnée de la coordonnée testée
	 * @param taille_relative_pion facteur de la taille de la zone de pion
	 *          (1 = taille de boite de collision inchangée)
	 * @return Renvoie vrai si la coordonnée est sur une zone de pion
	 */
	public boolean collisionZonePion(int x, int y, float taille_relative_pion) {
    	if(!collisionPlateau(x, y))
    		return false;

    	x = ecranVersPlateauX(x);
    	y = ecranVersPlateauY(y);

		int taille_pion_moitie = taille_pion/2;
    	return Math.abs(x%taille_cellule - taille_cellule/2) < taille_pion_moitie*taille_relative_pion
				&& Math.abs(y%taille_cellule - taille_cellule/2) < taille_pion_moitie*taille_relative_pion;
	}

	public boolean collisionPlateau(int x, int y) {

    	x = ecranVersPlateauX(x);
    	y = ecranVersPlateauY(y);

    	return x >= 0 && x < taille_plateau.x && y >= 0 && y < taille_plateau.y;
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

	public Point positionVersCoordonnees(Position p) {
		return new Point((int)positionVersX(p.getColonne()), (int)positionVersY(p.getLigne()));
	}

}
