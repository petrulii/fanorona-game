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
	private final Image plateau, pion_a, pion_b, pion_a_ombre, pion_b_ombre;

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
    private final PionDeplacable pion_deplace;
    private Position position_survolee;

	/**
	 * Image en mémoire tampon dans laquelle on dessine le jeu
	 */
	private BufferedImage dessin_jeu;

    public AireGraphique(AireJeu a) {
    	setFocusable(true);
    	requestFocus();

        aire = a;

        plateau = chargerTexture("plateau");
        pion_a = chargerTexture("pion_a");
        pion_b = chargerTexture("pion_b");
        pion_a_ombre = chargerTexture("pion_a_ombre");
        pion_b_ombre = chargerTexture("pion_b_ombre");

        taille_cellule = 32;
        taille_pion = 16;

        taille_principale = new Point(getSize().width, getSize().height);
        taille_dessin = new Point(0, 0);
        taille_plateau = new Point(288, 160);
        decalage_plateau = new Point(0, 0);
        decalage_grille = new Point(taille_pion, taille_pion);

        pion_deplace = new PionDeplacable();
        position_survolee = new Position(-1, -1);
    }

    private Image chargerTexture(String nom) {
		Image img = null;
		InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream("Images" + File.separator + nom + ".png");

		try {
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
            double taille_dessin_ratio = (double)aire.getNbColonnes()/(double)aire.getNbLignes();

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

	/**
	 * Indique à l'aire graphique sa coordonnée qui est survolée
	 * @param x l'abscisse de la coordonnée survolée
	 * @param y l'ordonnée de la coordonnée survolée
	 */
	public void survolerPosition(int x, int y) {
    	if(collisionZonePion(x, y)) {
    		position_survolee = new Position(getLigne(y), getColonne(x));
		} else {
    		position_survolee = new Position(-1, -1);
		}
	}

	/**
	 * Indique à l'aire graphique que le pion est maintenu à la coordonnée donnée en entrée
	 * @param x l'abscisse de la coordonnée
	 * @param y l'ordonnée de la coordonnée
	 */
    public void maintenirPion(int x, int y) {
    	if(!pion_deplace.estRelache()) {
    		if(collisionZonePion(x, y, .75f)) {
    			pion_deplace.setPositionAbsolue(
    					(getColonne(x)*taille_cellule) + decalage_grille.x,
						(getLigne(y)*taille_cellule) + decalage_grille.y
				);
			} else {
    			pion_deplace.setPositionAbsolue(ecranVersPlateauX(x), ecranVersPlateauY(y));
			}
		} else if(collisionZonePion(x, y)) {
    		int ligne = getLigne(y), colonne = getColonne(x);
			int joueur = aire.getGrille()[ligne][colonne];

			if(joueur == 0)
				return;

			pion_deplace.maintenir(new Position(ligne, colonne), ecranVersPlateauX(x), ecranVersPlateauY(y), joueur);
		}
	}

	/**
	 * Indique à l'aire graphique que le pion est relaché à la coordonnée donnée en entrée
	 * @param x l'abscisse de la coordonnée où le relâchement est fait
	 * @param y l'ordonnée de la coordonnée où le relâchement est fait
	 * @return renvoie Vrai si le pion est relaché sur une case de pion
	 */
	public Boolean relacherPion(int x, int y) {
    	if(!pion_deplace.estRelache()) {
    		pion_deplace.relacher();
    		return collisionZonePion(x, y, .75f);
		}
    	return false;
	}

	/**
	 * @return la position de départ du pion relaché
	 */
	public Position getPositionDepartPion() {
    	return pion_deplace.getPositionDepart();
	}

	@Override
	public void paintComponent(Graphics g) {
		miseAjourDimensions();

    	Graphics2D ctx = dessin_jeu.createGraphics();
    	//ctx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		ctx.clearRect(0, 0, getWidth(), getHeight());
    	ctx.translate(decalage_plateau.x, decalage_plateau.y);

    	dessinerPlateau(ctx);

    	/*ArrayList<Position> positions = new ArrayList<>();
        positions.add(new Position(1,2));
        positions.add(new Position(1,3));
        positions.add(new Position(2,4));
        positions.add(new Position(3,4));*/

        //dessinerChemin(ctx, positions);
    	dessinerPions(ctx);
        //indiquerPionsSupprimables(ctx, positions);
		//dessinerIndicationPionsJouables(ctx, positions);
    	dessinerPionDeplacable(ctx);

    	Graphics2D main_ctx = (Graphics2D)g;
		main_ctx.clearRect(0, 0, getWidth(), getHeight());
    	main_ctx.drawImage(dessin_jeu, 0, 0, getWidth(), getHeight(), null);
	}

	private void dessinerPlateau(Graphics2D ctx) {
    	ctx.drawImage(plateau, 0, 0, taille_plateau.x, taille_plateau.y, null);
	}

	private void dessinerPions(Graphics2D ctx) {
    	ctx.translate(decalage_grille.x, decalage_grille.y);

		int[][] grille = aire.getGrille();

		int taille_pion_moitie = taille_pion/2;

		for (int x = 0; x < aire.getNbColonnes(); x++) {
		for (int y = 0; y < aire.getNbLignes(); y++) {

			if(grille[y][x] == 0)
				continue;
			if(!pion_deplace.estRelache() && pion_deplace.getPositionDepart().equals(new Position(y, x)))
				continue;

			double facteur_taille = (position_survolee.getColonne() == x && position_survolee.getLigne() == y) ? 1.25 : 1;

			ctx.drawImage(
					grille[y][x] == 1 ? pion_a : pion_b,
					taille_cellule*x - (int)(taille_pion_moitie*facteur_taille),
					taille_cellule*y - (int)(taille_pion_moitie*facteur_taille),
					(int)(taille_pion*facteur_taille),
					(int)(taille_pion*facteur_taille),
					null
			);
		}
		}

    	ctx.translate(-decalage_grille.x, -decalage_grille.y);
	}

	private void dessinerPionDeplacable(Graphics2D ctx) {
    	if(pion_deplace.estRelache())
    		return;

		int taille_pion_moitie = taille_pion/2;
		ctx.drawImage(
    			pion_deplace.getJoueur() == 1 ? pion_a_ombre : pion_b_ombre,
				pion_deplace.getPositionXabsolue() - taille_pion_moitie + taille_pion/4,
				pion_deplace.getPositionYabsolue() - taille_pion_moitie + taille_pion/4,
				taille_pion,
				taille_pion,
				null
		);

    	float[] dash_array = {10f, 10f};
    	ctx.setColor(new Color(255,255,255));
    	ctx.setStroke(new BasicStroke(2, CAP_SQUARE, JOIN_MITER, 1,  dash_array, 0));
    	ctx.draw(new Line2D.Float(
				pion_deplace.getPositionXabsolue(),
				pion_deplace.getPositionYabsolue(),
    			taille_cellule*pion_deplace.getPositionDepart().getColonne() + decalage_grille.x,
    			taille_cellule*pion_deplace.getPositionDepart().getLigne() + decalage_grille.y
		));

    	ctx.drawImage(
    			pion_deplace.getJoueur() == 1 ? pion_a : pion_b,
				pion_deplace.getPositionXabsolue() - taille_pion_moitie,
				pion_deplace.getPositionYabsolue() - taille_pion_moitie,
				taille_pion,
				taille_pion,
				null
		);
	}

	private void dessinerIndicationPionsJouables(Graphics2D ctx, ArrayList<Position> positions) {
    	ctx.translate(decalage_grille.x, decalage_grille.y);

    	int taille_moitie = taille_pion/2;

    	positions.forEach((Position p) -> {
    		ctx.setStroke(new BasicStroke(1));
    		ctx.setColor(new Color(255,0,0));
    		ctx.draw(new Ellipse2D.Float(
				p.getColonne()*taille_cellule - taille_moitie,
				p.getLigne()*taille_cellule - taille_moitie,
				taille_pion,
				taille_pion
			));
		});

    	ctx.translate(-decalage_grille.x, -decalage_grille.y);
	}

	private void indiquerPionsSupprimables(Graphics2D ctx, ArrayList<Position> positions) {
    	ctx.translate(decalage_grille.x, decalage_grille.y);

    	int taille_moitie = taille_pion/2;

    	for(Position position : positions) {
    		ctx.setStroke(new BasicStroke(3));
    		ctx.setColor(new Color(255,0,0));
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

    	ctx.translate(-decalage_grille.x, -decalage_grille.y);
	}

	private void dessinerChemin(Graphics2D ctx, ArrayList<Position> positions) {
    	ctx.translate(decalage_grille.x, decalage_grille.y);
    	Path2D path = new Path2D.Float();

    	path.moveTo(positions.get(0).getColonne()*taille_cellule, positions.get(0).getLigne()*taille_cellule);
    	positions.forEach((Position p) -> path.lineTo(p.getColonne()*taille_cellule, p.getLigne()*taille_cellule));

    	ctx.setStroke(new BasicStroke(4));
    	ctx.setColor(new Color(0,0,0, 150));
    	ctx.draw(path);

    	ctx.translate(-decalage_grille.x, -decalage_grille.y);
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
	private Boolean collisionZonePion(int x, int y) {
    	return collisionZonePion(x, y, 1);
	}

	/**
	 * @param x abscisse de la coordonnée testée
	 * @param y ordonnée de la coordonnée testée
	 * @param taille_relative_pion facteur de la taille de la zone de pion
	 *          (1 = taille de boite de collision inchangée)
	 * @return Renvoie vrai si la coordonnée est sur une zone de pion
	 */
	private Boolean collisionZonePion(int x, int y, float taille_relative_pion) {
    	if(!collisionPlateau(x, y))
    		return false;

    	x = ecranVersPlateauX(x);
    	y = ecranVersPlateauY(y);

		int taille_pion_moitie = taille_pion/2;
    	return Math.abs(x%taille_cellule - taille_cellule/2) < taille_pion_moitie*taille_relative_pion
				&& Math.abs(y%taille_cellule - taille_cellule/2) < taille_pion_moitie*taille_relative_pion;
	}

	private Boolean collisionPlateau(int x, int y) {
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
	public Position getPosition(int x, int y) {
    	return new Position(getLigne(y), getColonne(x));
	}

}
