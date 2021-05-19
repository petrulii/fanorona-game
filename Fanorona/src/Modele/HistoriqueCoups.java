package Modele;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Classe pour ecrire et lire dans un fichier une liste de coups.
 * @author Yu Ran
 * @version 1.0
 */
public class HistoriqueCoups {
    
    /**
     * Prend une liste des coups et l'ecrit dans un fichier
     * @param la liste des coups
     * @throws IOException
     */
    public static void exporter(ArrayList<Coup> coups) {
    	try {
    		Random r = new Random();
    		String nom_fichier = "res"+File.separator+"Historiques"+File.separator+"historique"+r.nextInt()+".txt";
    		File file = new File(nom_fichier);
            BufferedWriter f = new BufferedWriter(new FileWriter(file));
            if(coups.isEmpty()) {
            	System.out.println("Liste des coups est vide");
            }else {
            	//Parcourir la liste
            	for(Coup c : coups) {
            		String s;
            		s = "Debut: "+c.getDebut()+", fin: "+c.getFin()+" , aspiration: "+c.getAspiration()+" , joueur: "+c.getJoueur();
            		s = s + ", pions captures: [ ";
            		for (Position p : c.getPions()) {
            			s = s+p+" ";
            		}
            		s = s+"].";
                	f.write(s+"\n");
                }
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
    /*public static ArrayList<Coup> importer(String nom_fichier) {
    	ArrayList<Coup> les_coups = new ArrayList<Coup>();
    	
		try {
			BufferedReader bf;
			bf = new BufferedReader(new FileReader(nom_fichier));
			String un_coup;
			int x=0;
			int y = 0;
			while((un_coup=bf.readLine())!=null){ // Parcourir le fichier ligne par ligne
				String[] ss = un_coup.split(" ");
				x = Integer.parseInt(ss[0]);
				y = Integer.parseInt(ss[1]);
				Coup c = new Coup(x,y);
				les_coups.add(c);
			}
			bf.close();
		} catch (FileNotFoundException e) { // Les erreurs
			System.out.println("Ficher non existe : " + nom_fichier);
			System.exit(1);
		} catch (NumberFormatException e) {
			System.out.println("Erreur transform le String en int");
			System.exit(1);
		} catch (IOException e) {
			System.out.println("Erreur lire ou fermer le fichier : "+ nom_fichier);
			System.exit(1);
		}
		
        return null;
    }*/
    
}