package Modele;

import java.io.*;
import java.sql.Date;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

/**
 * Classe pour ecrire et lire dans un fichier une liste de coups.
 * @author Yu Ran
 * @version 1.0
 */
public class HistoriqueCoups {
	/**
	* Liste pour enregistrer les coups.
	*/
	ArrayList<Coup> coups;
	/**
	* Liste pour se souvenir des coups annules en cours d'annulation.
	*/
	ArrayList<Coup> coups_annules;
    
	public HistoriqueCoups() {
		// Initialisation d'un liste pour l'enregistrement des coups.
		coups = new ArrayList<Coup>();
		// Initialisation d'un liste pour l'annulation des coups.
		coups_annules = new ArrayList<Coup>();
	}

	public void ajouterCoup(Coup coup) {
		coups.add(coup);
	}

	public void ajouterCoupAnnule(Coup coup) {
		coups_annules.add(coup);
	}

	public Coup enleveCoup() {
		return coups.remove(coups.size() - 1);
	}

	public Coup enleveCoupAnnule() {
		return coups_annules.remove(coups_annules.size() - 1);
	}

	public Coup getDernierCoup() {
		return coups.get(coups.size()-1);
	}

	public Coup getDernierCoupAnnule() {
		return coups_annules.get(coups_annules.size()-1);
	}
	
	/**
	 * Dit si l'annulation d'un coup est possible coups annules
	 * @return vrai s'il y a des coups a annuler, faux sinon
	 */
	public boolean annulationCoupPossible() {
		return (!coups.isEmpty());
	}
	
	/**
	 * Dit si c'est possible de refaire un coup
	 * @return vrai s'il y a des coups annules, faux sinon
	 */
	public boolean refaireCoupPossible() {
		return (!coups_annules.isEmpty());
	}
	
    /**
     * Prend une liste des coups et l'ecrit dans un fichier
     * @param la liste des coups
     * @throws IOException
     */
    public void exporter() {
    	// Debut: ( 3, 2 ), fin: ( 2, 2 ) , aspiration: false , joueur: 1, pions captures: [ ( 1, 2 ) ( 0, 2 ) ].
    	try {
    		String pattern = "-MM_dd_yyyy-HH_mm_ss";
    		DateFormat df = new SimpleDateFormat(pattern);
    		java.util.Date today = Calendar.getInstance().getTime();
    		String todayAsString = df.format(today);
    		String nom_fichier = "res"+File.separator+"Historiques"+File.separator+"historique"+todayAsString+".txt";
    		File file = new File(nom_fichier);
            BufferedWriter f = new BufferedWriter(new FileWriter(file));
            if (coups.isEmpty()) {
            	System.out.println("Liste des coups est vide");
            } else {
            	f.write(stringCoups(coups)+"\n");
            	f.write(stringCoups(coups_annules)+"\n");
            }
            f.close();
			System.out.println("L'hisorique a ete exporte.");
        } catch (IOException e) {
        	System.out.println("Impossible de creer le fichier d'historique d'un jeu.");
        }
    }
    
    /**
     * Prend un nom de fichier et lit un liste de coups dans ce fichier
     * @param nom_fichier : nom de fichier
     * @return la liste lu dans le fichier
     * @throws IOException 
     * @throws NumberFormatException 
     */
    public void importer(String nom_fichier) {
    	// Debut: ( 3, 2 ), fin: ( 2, 2 ) , aspiration: false , joueur: 1, pions captures: [ ( 1, 2 ) ( 0, 2 ) ].
    	coups = new ArrayList<Coup>();
    	try {
			BufferedReader bf; 
			bf = new BufferedReader(new FileReader("res"+File.separator+"Historiques"+File.separator+nom_fichier));
			String text_coup;
			// On parcours le fichier ligne par ligne pour lire les coups de jeu.
			while ((text_coup = bf.readLine()) != null){
				String[] ligne_coup = text_coup.split(" ");
				// Une ligne qui contient un coup aura imperativement au moins 5 entiers.
				if (ligne_coup.length < 5) {
					break;
				}
				Coup c = construireCoup(ligne_coup);
				coups.add(c);
			}
			// On parcours le fichier ligne par ligne pour lire les coups annules de jeu.
			while ((text_coup = bf.readLine()) != null){
				String[] ligne_coup = text_coup.split(" ");
				// Une ligne qui contient un coup aura imperativement au moins 5 entiers.
				if (ligne_coup.length < 5) {
					break;
				}
				Coup c = construireCoup(ligne_coup);
				coups_annules.add(c);
			}
			bf.close();
		} catch (FileNotFoundException e) { // Les erreurs
			System.out.println("Ficher non existe : " + nom_fichier);
			System.exit(1);
		} catch (NumberFormatException e) {
			System.out.println("Erreur transformer le String en int");
			System.exit(1);
		} catch (IOException e) {
			System.out.println("Erreur lire ou fermer le fichier : " + nom_fichier);
			System.exit(1);
		}
    }
	
	private Coup construireCoup(String[] ligne_coup) {		// A CORRIGER
		int l_debut = (int) Integer.parseInt(ligne_coup[0]);
		System.out.println(ligne_coup[0]);
		int c_debut = (int) Integer.parseInt(ligne_coup[1]);
		System.out.println(ligne_coup[1]);
		Position p_debut = new Position(l_debut, c_debut);
		int l_fin = (int) Integer.parseInt(ligne_coup[2]);
		System.out.println(ligne_coup[2]);
		int c_fin = (int) Integer.parseInt(ligne_coup[3]);
		System.out.println(ligne_coup[3]);
		Position p_fin = new Position(l_fin, c_fin);
		boolean aspiration = (boolean) Boolean.parseBoolean(ligne_coup[4]);
		System.out.println(ligne_coup[4]);
		int joueur = (int) Integer.parseInt(ligne_coup[5]);
		System.out.println(ligne_coup[5]);
		ArrayList<Position> pions_caputures = new ArrayList<Position>();
		Position p;
		try {
			for (int i = 6; i < ligne_coup.length; i = i + 2) {
				p = new Position((int) Integer.parseInt(ligne_coup[i]), (int) Integer.parseInt(ligne_coup[i+1]));
				// Ajouter la position dans la liste
				pions_caputures.add(p);
				System.out.println(ligne_coup[i]);
				System.out.println(ligne_coup[i+1]);
			}
		} catch(ArrayIndexOutOfBoundsException e) {
			System.out.println(e);
		}
		return new Coup(p_debut, p_fin, aspiration, joueur, pions_caputures);
	}

	/**
	 * Transforme une liste de coups en chaine de charactere.
	 */
	public String stringCoups(ArrayList<Coup> liste) {
		String s = new String();
		for(Coup c : liste) {
    		s = s + c.toStringEspace();
		}
		return s;
	}
	
	/**
	 * Affiche les coups de jeu.
	 */
	public void afficheCoups() {
		System.out.println(stringCoups(coups));
	}
	
	/**
	 * Affiche les coups annules.
	 */
	public void afficheCoupsAnnules() {
		System.out.println(stringCoups(coups_annules));
	}
    
    /**
     * Copie la liste des coups joues pendant le jeu
     */
    public ArrayList<Coup> copyCoups() {
    	ArrayList<Coup> coups_copie = new ArrayList<Coup>();
		for (Coup c : coups) {
			coups_copie.add(c);
		}
		return coups_copie;
    }
    
    /**
     * Copie la liste des coups annules pendant le jeu en cours d'annulation
     */
    public ArrayList<Coup> copyCoupsAnnules() {
    	ArrayList<Coup> coups_annules_copie = new ArrayList<Coup>();
		for (Coup c : coups_annules) {
			coups_annules_copie.add(c);
		}
		return coups_annules_copie;
    }
    
    /**
     * Assigne la liste des coups joues pendant le jeu
     */
    public void setCoups(ArrayList<Coup> coups_copie) {
		this.coups = coups_copie;
	}
    
    /**
     * Assigne la liste des coups annules pendant le jeu en cours d'annulation
     */
    public void setCoupsAnnules(ArrayList<Coup> coups_annules_copie) {
		this.coups_annules = coups_annules_copie;
	}
    
    /**
     * Assigne la liste des coups annules pendant le jeu en cours d'annulation
     */
    public void resetCoupsAnnules() {
		this.coups_annules = new ArrayList<Coup>();
	}
	
    /**
     * Renvoie la liste des coups joues pendant le jeu
     * @return la liste des coups joues pendant le jeu
     */
    public ArrayList<Coup> getCoups() {
    	return coups;
    }
	
    /**
     * Renvoie la liste des coups annules pendant le jeu en cours d'annulation
     * @return la liste des coups annules pendant le jeu
     */
    public ArrayList<Coup> getCoupsAnnules() {
    	return coups_annules;
    }

	public HistoriqueCoups copy() {
		HistoriqueCoups h = new HistoriqueCoups();
		h.setCoups(copyCoups());
		h.setCoupsAnnules(copyCoupsAnnules());
		return h;
	}
    
}