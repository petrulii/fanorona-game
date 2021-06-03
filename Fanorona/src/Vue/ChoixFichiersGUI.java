package Vue;

import java.io.File;
import javax.swing.DefaultListModel;

/**
 * Classe de test : affiche la liste de fichiers dans le dossier de parties sauvegardées.
 * Le dossier de sauvegarde est <code>System.getProperty("user.home") + File.separator + ChoixFichiersGUI.DOSSIER_SAUVEGARDE</code>.
 * @author szczepma
 */
public class ChoixFichiersGUI extends javax.swing.JFrame {
    
    /**
     * représente le chemin du dossier par défaut de l'utilisateur
     */
    private final String chemin_home;
    /**
     * Nom du dossier où sauvegarder les parties
     */
    private static final String DOSSIER_SAUVEGARDE = ".fanorona";
    /**
     * String spéciale utilisée pour représenter l'absence de partie
     */
    private final String AUCUNE_PARTIE = "Aucune partie sauvegardée";
    /**
     * Fenêtre principale à qui on doit donner le nom de fichier à charger
     */
    private final MainGUI fenetre_principale;

    /**
     * Creates new form AfficheFichiers
     * @param fenetre la MainGUI à relier
     */
    public ChoixFichiersGUI(MainGUI fenetre) {
        initComponents();
        chemin_home = System.getProperty("user.home");
        initDossierSauvegarde();
        fenetre_principale = fenetre; 
    }
    
    /**
     * Regarde si le dossier de sauvegardes existe, si non, le crée.
     */
    private void initDossierSauvegarde() {
        File dossier = new File(chemin_home + File.separator + DOSSIER_SAUVEGARDE);
        
        if (!dossier.exists() || !dossier.isDirectory()) {
            System.out.println("Pas de dossier de sauvegardes trouvé, création du dossier " + dossier.getAbsolutePath());
            dossier.mkdir();
        }
    }
    
    /**
     * Met à jour les éléments de la liste selon ce qui se trouve dans le dossier de sauvegardes.
     */
    public void initListe() {
        File dossier = new File(chemin_home + File.separator + DOSSIER_SAUVEGARDE);
        String[] fichiers = dossier.list();
        DefaultListModel<String> items = new DefaultListModel<>();
        
        if (fichiers.length == 0) {
            items.setSize(1);
            items.add(0, AUCUNE_PARTIE);
        } else {
            items.setSize(fichiers.length);
        
            for (String fichier : fichiers) {
                items.add(0,fichier);
            }
        }
        
        bouton_valider.setEnabled(false);
        liste_items.setModel(items);
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bouton_valider = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        liste_items = new javax.swing.JList<>();

        setTitle("Test lister fichiers d'un dossier");
        setAlwaysOnTop(true);
        setMinimumSize(new java.awt.Dimension(350, 250));
        setResizable(false);

        bouton_valider.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        bouton_valider.setText("Valider");
        bouton_valider.setEnabled(false);
        bouton_valider.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bouton_validerActionPerformed(evt);
            }
        });

        liste_items.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        liste_items.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        liste_items.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                liste_itemsValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(liste_items);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(133, Short.MAX_VALUE)
                .addComponent(bouton_valider)
                .addGap(0, 133, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(bouton_valider)
                .addGap(20, 20, 20))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Affiche le fichier choisi, puis quitte
     * @param evt ignored
     */
    private void bouton_validerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bouton_validerActionPerformed
        String choix = liste_items.getSelectedValue();
        
        if (liste_items.getModel().getElementAt(0).equals(AUCUNE_PARTIE)) {
            System.out.println("Il n'y avait aucune partie sauvegardée.");
        } else {
            if (choix == null) {
                System.out.print("Aucun fichier n'a été choisi.");
            } else {
                System.out.print("Fichier choisi : ");
                System.out.println(chemin_home + File.separator + DOSSIER_SAUVEGARDE + File.separator + choix);
                fenetre_principale.chargeFichier(choix);
            }
        }
        
        this.setVisible(false);
    }//GEN-LAST:event_bouton_validerActionPerformed

    private void liste_itemsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_liste_itemsValueChanged
        bouton_valider.setEnabled(true);
    }//GEN-LAST:event_liste_itemsValueChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bouton_valider;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList<String> liste_items;
    // End of variables declaration//GEN-END:variables
}