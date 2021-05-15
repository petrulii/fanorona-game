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
     * @param coups : la liste des coups
     * @return le dernier coup  
     */
    /*public static void exporter(ArrayList<Coup> coups) {
    	try {
    		Random r = new Random();
    		File file = new File("res"+File.separator+"Historiques"+File.separator+"historique"+r.nextInt()+".txt");
            BufferedWriter f = new BufferedWriter(new FileWriter(file));
            if(coups.size() == 0) {
            	System.out.println("Liste des coups est vide");
            }else {
            	//Parcourir la liste
            	for(Coup c:coups) {
                	f.write(c.getLigne() + " " + c.getColonne()+ "\r\n");
                }
            }
            
            f.close();
        } catch (IOException e) {
        	System.out.println("Erreur de creer un fichier." + "les_coups.txt");
			
        }
    }*/
    
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