package Vue;

import Controleur.ControleurMediateur;
import Modele.AireJeu;

import javax.swing.*;
import java.awt.*;
import java.util.Enumeration;
import java.util.Objects;

/**
 * La classe de la fenêtre principale du jeu.
 * @author Marin et Titouan
 */
public class MainGUI extends javax.swing.JFrame {

    /**
     * L'aire de jeu à qui demander des informations
     */
    private AireJeu aire_jeu;
    /**
     * Le contrôleur à qui envoyer des instructions
     */
    private ControleurMediateur controleur_mediateur;
    /**
     * L'aire graphique à afficher dans la zone de jeu
     */
    private AireGraphique aire_graphique;
    /**
     * Icône représentant le joueur blanc
     */
    private final ImageIcon icone_blanc;
    /**
     * Icône représentant le joueur noir
     */
    private final ImageIcon icone_noir;
    /**
     * Classe servant à activer/désactiver le clignotement du bouton Terminer
     */
    private final ClignotementBouton clignotement_terminer;
    /**
     * Fenêtre permettant de choisir une partie sauvegardée à charger
     */
    private final ChoixFichiersGUI fenetre_chargement;

    /**
     * Creates new form MainGUI
     */
    public MainGUI(Font f) {
        initComponents();

        icone_blanc = new javax.swing.ImageIcon(Objects.requireNonNull(getClass().getResource("/Images/icone_blanc.png")));
        icone_noir = new javax.swing.ImageIcon(Objects.requireNonNull(getClass().getResource("/Images/icone_noir.png")));

        clignotement_terminer = new ClignotementBouton(bouton_terminer);
        fenetre_chargement = new ChoixFichiersGUI(this);
        fenetre_chargement.initListe();

        changerPanneau("panneau_menu");

        appliquerFont(f);
    }

    /**
     * Applique la font donnée en paramètre à tous les éléments textuel de la fenêtres
     * @param f la font à appliquer
     */
    public void appliquerFont(Font f) {
        this.setFont(f);
        fenetre_chargement.setFont(f);
        bouton_annuler.setFont(f);
        bouton_commencer.setFont(f);
        bouton_retablir.setFont(f);
        bouton_terminer.setFont(f);
        checkbox_debutant.setFont(f);
        jLabel1.setFont(f);
        label_debut.setFont(f);
        label_joueur1.setFont(f);
        label_joueur2.setFont(f);
        label_joueur_actif.setFont(f);
        menu_afficher_les_aides.setFont(f);
        menu_bouton.setFont(f);
        menu_charger.setFont(f);
        menu_quitter.setFont(f);
        menu_sauvegarder.setFont(f);
        menu_terminer.setFont(f);
        radio_debut_blanc.setFont(f);
        radio_debut_noir.setFont(f);
        radio_niveau_ia_difficile_j1.setFont(f);
        radio_niveau_ia_difficile_j2.setFont(f);
        radio_niveau_ia_facile_j1.setFont(f);
        radio_niveau_ia_facile_j2.setFont(f);
        radio_niveau_ia_moyen_j1.setFont(f);
        radio_niveau_ia_moyen_j2.setFont(f);
        radio_type_humain_j1.setFont(f);
        radio_type_humain_j2.setFont(f);
        radio_type_ia_j1.setFont(f);
        radio_type_ia_j2.setFont(f);
    }


    /**
     * Bascule entre le panneau de menu et de jeu
     * @param nom_panneau panneau pour lequel on souhaite changer dans la fenêtre
     */
    private void changerPanneau(String nom_panneau) {
        ((CardLayout)conteneur_principal.getLayout()).show(conteneur_principal, nom_panneau);

        if(nom_panneau.equals("panneau_menu")) {
            menu_sauvegarder.setVisible(false);
            menu_charger.setVisible(false);
            menu_separator_2.setVisible(false);
            menu_afficher_les_aides.setVisible(false);
            menu_separator_3.setVisible(false);
            menu_terminer.setVisible(false);
            menu_separator_1.setVisible(false);
        } else if(nom_panneau.equals("panneau_jeu")) {
            menu_sauvegarder.setVisible(true);
            menu_charger.setVisible(true);
            menu_separator_2.setVisible(true);
            menu_afficher_les_aides.setVisible(true);
            menu_separator_3.setVisible(true);
            menu_terminer.setVisible(true);
            menu_separator_1.setVisible(true);
        }
    }

    /**
     * @param b le nouvel état d'activation du bouton terminer
     */
    public void majBoutonTerminer(boolean b) {
        bouton_terminer.setEnabled(b);
    }

    /**
     * @param b le nouvel état d'accent sur le bouton terminer
     */
    public void accentSurBoutonTerminer(boolean b) {
        clignotement_terminer.setClignotementBouton(b);
    }

    /**
     * Active ou désactive les boutons Annuler et Rétablir selon l'historique actif
     * @param veto si false, force la désactivation des boutons. Si true, leur activation dépend de l'historique
     */
    public void majBoutonHistorique(boolean veto) {
        bouton_annuler.setEnabled(veto && aire_jeu.annulationCoupPossible());
        bouton_retablir.setEnabled(veto && aire_jeu.refaireCoupPossible());
    }

    /**
     * Indique à qui c'est le tour
     * @param j le numéro du joueur
     * @param b true si le joueur actif est une IA, false sinon
     */
    public void majAffichageJoueurActif(int j, boolean b) {
        
        label_joueur_actif.setText(b ? "L'IA réfléchit..." : "C'est au tour de");
        
        label_joueur_actif.setIcon(
                j == AireJeu.BLANC ?
                        icone_blanc
                        : icone_noir
        );
    }

    /**
     * Affiche la pop-up qui indique que la partie est terminée.
     * @param j le numéro du joueur
     */
    public void afficherGameOver(int j) {
        label_joueur_actif.setText("Partie terminée.");

        // affichage d’une boite de dialogue de confirmation
        Object[] options = {"Relancer la partie", "Retourner au menu principal"};

        int n = JOptionPane.showOptionDialog(this,
            "a gagné la partie !",
            "Fin de la partie",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            j == AireJeu.BLANC ?
                icone_blanc
                : icone_noir,
            options,
            options[0]
        );

        switch (n) {
            case JOptionPane.YES_OPTION:
                rapatrierFormulaire();
                break;
            case JOptionPane.NO_OPTION:
            case JOptionPane.CLOSED_OPTION:
                changerPanneau("panneau_menu");
                break;
        }
    }
    
    /**
     * Indique à la vue qu'il faut charger un fichier de nom donné
     * @param nom nom du fichier à charger
     */
    public void chargeFichier(String nom) {
        controleur_mediateur.instructionImporter(nom);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        group_debut = new javax.swing.ButtonGroup();
        group_type_j1 = new javax.swing.ButtonGroup();
        group_niveau_j1 = new javax.swing.ButtonGroup();
        group_type_j2 = new javax.swing.ButtonGroup();
        group_niveau_j2 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        conteneur_principal = new javax.swing.JPanel();
        panneau_menu = new javax.swing.JPanel();
        panel_flottant = new javax.swing.JPanel();
        panel_joueurs = new javax.swing.JPanel();
        panel_joueur1 = new javax.swing.JPanel();
        label_joueur1 = new javax.swing.JLabel();
        radio_type_humain_j1 = new javax.swing.JRadioButton();
        radio_type_ia_j1 = new javax.swing.JRadioButton();
        panel_options_ia_j1 = new javax.swing.JPanel();
        radio_niveau_ia_facile_j1 = new javax.swing.JRadioButton();
        radio_niveau_ia_moyen_j1 = new javax.swing.JRadioButton();
        radio_niveau_ia_difficile_j1 = new javax.swing.JRadioButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        panel_joueur2 = new javax.swing.JPanel();
        label_joueur2 = new javax.swing.JLabel();
        radio_type_humain_j2 = new javax.swing.JRadioButton();
        radio_type_ia_j2 = new javax.swing.JRadioButton();
        panel_options_ia_j2 = new javax.swing.JPanel();
        radio_niveau_ia_facile_j2 = new javax.swing.JRadioButton();
        radio_niveau_ia_moyen_j2 = new javax.swing.JRadioButton();
        radio_niveau_ia_difficile_j2 = new javax.swing.JRadioButton();
        panel_debut = new javax.swing.JPanel();
        radio_debut_noir = new javax.swing.JRadioButton();
        radio_debut_blanc = new javax.swing.JRadioButton();
        label_debut = new javax.swing.JLabel();
        bouton_commencer = new javax.swing.JButton();
        panel_debutant = new javax.swing.JPanel();
        checkbox_debutant = new javax.swing.JCheckBox();
        paneau_jeu = new javax.swing.JPanel();
        section_boutons_haut = new javax.swing.JPanel();
        section_boutons_haut_panel2 = new javax.swing.JPanel();
        label_joueur_actif = new javax.swing.JLabel();
        zone_de_jeu = new javax.swing.JPanel();
        section_boutons_bas = new javax.swing.JPanel();
        bouton_terminer = new javax.swing.JButton();
        bouton_annuler = new javax.swing.JButton();
        bouton_retablir = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        barre_menu = new javax.swing.JMenuBar();
        menu_bouton = new javax.swing.JMenu();
        menu_charger = new javax.swing.JMenuItem();
        menu_sauvegarder = new javax.swing.JMenuItem();
        menu_separator_1 = new javax.swing.JPopupMenu.Separator();
        menu_afficher_les_aides = new javax.swing.JMenuItem();
        menu_separator_2 = new javax.swing.JPopupMenu.Separator();
        menu_terminer = new javax.swing.JMenuItem();
        menu_separator_3 = new javax.swing.JPopupMenu.Separator();
        menu_quitter = new javax.swing.JMenuItem();

        jLabel1.setText("jLabel1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(150, 63, 5));
        setMinimumSize(new java.awt.Dimension(800, 600));

        conteneur_principal.setBackground(new java.awt.Color(0, 255, 65));
        conteneur_principal.setFocusable(false);
        conteneur_principal.setOpaque(false);
        conteneur_principal.setPreferredSize(new java.awt.Dimension(400, 400));
        conteneur_principal.setLayout(new java.awt.CardLayout());

        panneau_menu.setFocusable(false);
        panneau_menu.setMinimumSize(new java.awt.Dimension(0, 0));
        panneau_menu.setLayout(new java.awt.GridBagLayout());

        panel_flottant.setPreferredSize(new java.awt.Dimension(400, 260));
        panel_flottant.setRequestFocusEnabled(false);

        panel_joueur1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        label_joueur1.setText("Joueur Noir");
        label_joueur1.setFocusable(false);

        group_type_j1.add(radio_type_humain_j1);
        radio_type_humain_j1.setSelected(true);
        radio_type_humain_j1.setText("Humain");
        radio_type_humain_j1.setFocusable(false);
        radio_type_humain_j1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radio_type_humain_j1ActionPerformed(evt);
            }
        });

        group_type_j1.add(radio_type_ia_j1);
        radio_type_ia_j1.setText("IA");
        radio_type_ia_j1.setFocusable(false);
        radio_type_ia_j1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radio_type_ia_j1ActionPerformed(evt);
            }
        });

        panel_options_ia_j1.setBorder(javax.swing.BorderFactory.createTitledBorder("Niveau de l’IA"));

        group_niveau_j1.add(radio_niveau_ia_facile_j1);
        radio_niveau_ia_facile_j1.setSelected(true);
        radio_niveau_ia_facile_j1.setText("Facile");
        radio_niveau_ia_facile_j1.setEnabled(false);
        radio_niveau_ia_facile_j1.setFocusable(false);

        group_niveau_j1.add(radio_niveau_ia_moyen_j1);
        radio_niveau_ia_moyen_j1.setText("Moyen");
        radio_niveau_ia_moyen_j1.setEnabled(false);
        radio_niveau_ia_moyen_j1.setFocusable(false);

        group_niveau_j1.add(radio_niveau_ia_difficile_j1);
        radio_niveau_ia_difficile_j1.setText("Difficile");
        radio_niveau_ia_difficile_j1.setEnabled(false);
        radio_niveau_ia_difficile_j1.setFocusable(false);

        javax.swing.GroupLayout panel_options_ia_j1Layout = new javax.swing.GroupLayout(panel_options_ia_j1);
        panel_options_ia_j1.setLayout(panel_options_ia_j1Layout);
        panel_options_ia_j1Layout.setHorizontalGroup(
            panel_options_ia_j1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_options_ia_j1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(panel_options_ia_j1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(radio_niveau_ia_difficile_j1)
                    .addComponent(radio_niveau_ia_moyen_j1)
                    .addComponent(radio_niveau_ia_facile_j1)
                    .addComponent(filler3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel_options_ia_j1Layout.setVerticalGroup(
            panel_options_ia_j1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_options_ia_j1Layout.createSequentialGroup()
                .addComponent(radio_niveau_ia_facile_j1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radio_niveau_ia_moyen_j1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radio_niveau_ia_difficile_j1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(filler3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout panel_joueur1Layout = new javax.swing.GroupLayout(panel_joueur1);
        panel_joueur1.setLayout(panel_joueur1Layout);
        panel_joueur1Layout.setHorizontalGroup(
            panel_joueur1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_joueur1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(label_joueur1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(panel_joueur1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_joueur1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel_options_ia_j1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panel_joueur1Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(panel_joueur1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(radio_type_humain_j1)
                            .addComponent(radio_type_ia_j1))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panel_joueur1Layout.setVerticalGroup(
            panel_joueur1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_joueur1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_joueur1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radio_type_humain_j1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radio_type_ia_j1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel_options_ia_j1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel_joueur2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        label_joueur2.setText("Joueur Blanc");
        label_joueur2.setFocusable(false);

        group_type_j2.add(radio_type_humain_j2);
        radio_type_humain_j2.setText("Humain");
        radio_type_humain_j2.setFocusable(false);
        radio_type_humain_j2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radio_type_humain_j2ActionPerformed(evt);
            }
        });

        group_type_j2.add(radio_type_ia_j2);
        radio_type_ia_j2.setSelected(true);
        radio_type_ia_j2.setText("IA");
        radio_type_ia_j2.setFocusable(false);
        radio_type_ia_j2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radio_type_ia_j2ActionPerformed(evt);
            }
        });

        panel_options_ia_j2.setBorder(javax.swing.BorderFactory.createTitledBorder("Niveau de l’IA"));
        panel_options_ia_j2.setFocusable(false);

        group_niveau_j2.add(radio_niveau_ia_facile_j2);
        radio_niveau_ia_facile_j2.setSelected(true);
        radio_niveau_ia_facile_j2.setText("Facile");
        radio_niveau_ia_facile_j2.setFocusable(false);

        group_niveau_j2.add(radio_niveau_ia_moyen_j2);
        radio_niveau_ia_moyen_j2.setText("Moyen");
        radio_niveau_ia_moyen_j2.setFocusable(false);

        group_niveau_j2.add(radio_niveau_ia_difficile_j2);
        radio_niveau_ia_difficile_j2.setText("Difficile");
        radio_niveau_ia_difficile_j2.setFocusable(false);

        javax.swing.GroupLayout panel_options_ia_j2Layout = new javax.swing.GroupLayout(panel_options_ia_j2);
        panel_options_ia_j2.setLayout(panel_options_ia_j2Layout);
        panel_options_ia_j2Layout.setHorizontalGroup(
            panel_options_ia_j2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_options_ia_j2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(panel_options_ia_j2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(radio_niveau_ia_facile_j2)
                    .addComponent(radio_niveau_ia_moyen_j2)
                    .addComponent(radio_niveau_ia_difficile_j2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel_options_ia_j2Layout.setVerticalGroup(
            panel_options_ia_j2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_options_ia_j2Layout.createSequentialGroup()
                .addComponent(radio_niveau_ia_facile_j2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radio_niveau_ia_moyen_j2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radio_niveau_ia_difficile_j2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panel_joueur2Layout = new javax.swing.GroupLayout(panel_joueur2);
        panel_joueur2.setLayout(panel_joueur2Layout);
        panel_joueur2Layout.setHorizontalGroup(
            panel_joueur2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_joueur2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_joueur2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel_options_ia_j2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panel_joueur2Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addGroup(panel_joueur2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(radio_type_humain_j2)
                            .addComponent(radio_type_ia_j2))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panel_joueur2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(label_joueur2)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panel_joueur2Layout.setVerticalGroup(
            panel_joueur2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_joueur2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_joueur2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radio_type_humain_j2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radio_type_ia_j2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel_options_ia_j2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panel_joueursLayout = new javax.swing.GroupLayout(panel_joueurs);
        panel_joueurs.setLayout(panel_joueursLayout);
        panel_joueursLayout.setHorizontalGroup(
            panel_joueursLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_joueursLayout.createSequentialGroup()
                .addComponent(panel_joueur1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel_joueur2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panel_joueursLayout.setVerticalGroup(
            panel_joueursLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel_joueur2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panel_joueur1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        panel_debut.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        panel_debut.setFocusable(false);
        panel_debut.setPreferredSize(new java.awt.Dimension(300, 66));

        group_debut.add(radio_debut_noir);
        radio_debut_noir.setSelected(true);
        radio_debut_noir.setText("Noir");
        radio_debut_noir.setFocusable(false);

        group_debut.add(radio_debut_blanc);
        radio_debut_blanc.setText("Blanc");
        radio_debut_blanc.setFocusable(false);

        label_debut.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        label_debut.setText("Qui commence ?");

        javax.swing.GroupLayout panel_debutLayout = new javax.swing.GroupLayout(panel_debut);
        panel_debut.setLayout(panel_debutLayout);
        panel_debutLayout.setHorizontalGroup(
            panel_debutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_debutLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(label_debut)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panel_debutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(radio_debut_noir)
                    .addComponent(radio_debut_blanc))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel_debutLayout.setVerticalGroup(
            panel_debutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_debutLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(radio_debut_noir)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radio_debut_blanc)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(panel_debutLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(label_debut)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        bouton_commencer.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        bouton_commencer.setText("Commencer la partie");
        bouton_commencer.setFocusable(false);
        bouton_commencer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bouton_commencerActionPerformed(evt);
            }
        });

        panel_debutant.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        checkbox_debutant.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        checkbox_debutant.setSelected(true);
        checkbox_debutant.setText("Afficher les aides");
        checkbox_debutant.setFocusable(false);
        checkbox_debutant.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkbox_debutantActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel_debutantLayout = new javax.swing.GroupLayout(panel_debutant);
        panel_debutant.setLayout(panel_debutantLayout);
        panel_debutantLayout.setHorizontalGroup(
            panel_debutantLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_debutantLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(checkbox_debutant)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel_debutantLayout.setVerticalGroup(
            panel_debutantLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_debutantLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(checkbox_debutant)
                .addContainerGap())
        );

        javax.swing.GroupLayout panel_flottantLayout = new javax.swing.GroupLayout(panel_flottant);
        panel_flottant.setLayout(panel_flottantLayout);
        panel_flottantLayout.setHorizontalGroup(
            panel_flottantLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_flottantLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_flottantLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel_joueurs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_flottantLayout.createSequentialGroup()
                        .addGroup(panel_flottantLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(panel_debutant, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panel_debut, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE))
                        .addContainerGap())))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_flottantLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(bouton_commencer)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel_flottantLayout.setVerticalGroup(
            panel_flottantLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_flottantLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel_joueurs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel_debut, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel_debutant, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bouton_commencer)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.ipady = 150;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        panneau_menu.add(panel_flottant, gridBagConstraints);

        conteneur_principal.add(panneau_menu, "panneau_menu");

        paneau_jeu.setToolTipText("");

        section_boutons_haut.setOpaque(false);
        section_boutons_haut.setLayout(new javax.swing.OverlayLayout(section_boutons_haut));

        section_boutons_haut_panel2.setOpaque(false);
        section_boutons_haut_panel2.setLayout(new java.awt.GridBagLayout());

        label_joueur_actif.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        label_joueur_actif.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/icone_blanc.png"))); // NOI18N
        label_joueur_actif.setText("Joueur courant");
        label_joueur_actif.setToolTipText("");
        label_joueur_actif.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        section_boutons_haut_panel2.add(label_joueur_actif, new java.awt.GridBagConstraints());

        section_boutons_haut.add(section_boutons_haut_panel2);

        zone_de_jeu.setFocusable(false);
        zone_de_jeu.setOpaque(false);
        zone_de_jeu.setLayout(new java.awt.BorderLayout());

        section_boutons_bas.setOpaque(false);

        bouton_terminer.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        bouton_terminer.setText("Terminer le tour");
        bouton_terminer.setToolTipText("Termine le tour et passe au joueur suivant.");
        bouton_terminer.setFocusable(false);
        bouton_terminer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bouton_terminerActionPerformed(evt);
            }
        });

        bouton_annuler.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        bouton_annuler.setText("Annuler");
        bouton_annuler.setToolTipText("Annule le dernier coup.");
        bouton_annuler.setEnabled(false);
        bouton_annuler.setFocusable(false);
        bouton_annuler.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bouton_annulerActionPerformed(evt);
            }
        });

        bouton_retablir.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        bouton_retablir.setText("Rétablir");
        bouton_retablir.setToolTipText("Rétablit le dernier coup annulé.");
        bouton_retablir.setEnabled(false);
        bouton_retablir.setFocusable(false);
        bouton_retablir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bouton_retablirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout section_boutons_basLayout = new javax.swing.GroupLayout(section_boutons_bas);
        section_boutons_bas.setLayout(section_boutons_basLayout);
        section_boutons_basLayout.setHorizontalGroup(
            section_boutons_basLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, section_boutons_basLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(bouton_annuler)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bouton_retablir)
                .addGap(18, 18, 18)
                .addComponent(filler2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bouton_terminer)
                .addContainerGap())
        );
        section_boutons_basLayout.setVerticalGroup(
            section_boutons_basLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(section_boutons_basLayout.createSequentialGroup()
                .addGroup(section_boutons_basLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(section_boutons_basLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(bouton_terminer)
                        .addComponent(bouton_annuler)
                        .addComponent(bouton_retablir))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, section_boutons_basLayout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(section_boutons_basLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, section_boutons_basLayout.createSequentialGroup()
                                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(8, 8, 8))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, section_boutons_basLayout.createSequentialGroup()
                                .addComponent(filler2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout paneau_jeuLayout = new javax.swing.GroupLayout(paneau_jeu);
        paneau_jeu.setLayout(paneau_jeuLayout);
        paneau_jeuLayout.setHorizontalGroup(
            paneau_jeuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(section_boutons_haut, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(zone_de_jeu, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(section_boutons_bas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        paneau_jeuLayout.setVerticalGroup(
            paneau_jeuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneau_jeuLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(section_boutons_haut, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(zone_de_jeu, javax.swing.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(section_boutons_bas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        conteneur_principal.add(paneau_jeu, "panneau_jeu");

        barre_menu.setFocusable(false);

        menu_bouton.setText("Menu");
        menu_bouton.setFocusable(false);

        menu_charger.setText("Charger une partie");
        menu_charger.setToolTipText("Charge une partie et son historique de coups.");
        menu_charger.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_chargerActionPerformed(evt);
            }
        });
        menu_bouton.add(menu_charger);

        menu_sauvegarder.setText("Sauvegarder la partie");
        menu_sauvegarder.setToolTipText("Enregistre la partie dans son état courant et son historique de coups dans un fichier.");
        menu_sauvegarder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_sauvegarderActionPerformed(evt);
            }
        });
        menu_bouton.add(menu_sauvegarder);
        menu_bouton.add(menu_separator_1);

        menu_afficher_les_aides.setText("Cacher les aides");
        menu_afficher_les_aides.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_afficher_les_aidesActionPerformed(evt);
            }
        });
        menu_bouton.add(menu_afficher_les_aides);
        menu_bouton.add(menu_separator_2);

        menu_terminer.setText("Arrêter la partie");
        menu_terminer.setToolTipText("Termine cette partie et revient au menu.");
        menu_terminer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_terminerActionPerformed(evt);
            }
        });
        menu_bouton.add(menu_terminer);
        menu_bouton.add(menu_separator_3);

        menu_quitter.setText("Quitter");
        menu_quitter.setToolTipText("Quitte le logiciel.");
        menu_quitter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_quitterActionPerformed(evt);
            }
        });
        menu_bouton.add(menu_quitter);

        barre_menu.add(menu_bouton);

        setJMenuBar(barre_menu);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(conteneur_principal, javax.swing.GroupLayout.DEFAULT_SIZE, 924, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(conteneur_principal, javax.swing.GroupLayout.DEFAULT_SIZE, 587, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Cette méthode lance une nouvelle partie en récupérant les infos du formulaire de menu
     */
    private void rapatrierFormulaire() {
        // le joueur qui commence
        int joueur_qui_commence = radio_debut_noir.isSelected() ? AireJeu.NOIR : AireJeu.BLANC;

        // la difficulté des IA, vaut 0 si le type du joueur est HUMAIN
        int niveau_type_j1 = radio_type_humain_j1.isSelected() ? ControleurMediateur.HUMAIN
                        : radio_niveau_ia_facile_j1.isSelected() ? ControleurMediateur.FACILE
                        : radio_niveau_ia_moyen_j1.isSelected() ? ControleurMediateur.MOYEN
                        : ControleurMediateur.DIFFICILE;

        int niveau_type_j2 = radio_type_humain_j2.isSelected() ? ControleurMediateur.HUMAIN
                        : radio_niveau_ia_facile_j2.isSelected() ? ControleurMediateur.FACILE
                        : radio_niveau_ia_moyen_j2.isSelected() ? ControleurMediateur.MOYEN
                        : ControleurMediateur.DIFFICILE;

        boolean mode_debutant = checkbox_debutant.isSelected();

        // créé une aire de jeu
        aire_jeu = new AireJeu();

        // créé l'aire graphique associée et l'ajoute à la fenêtre
        if(aire_graphique != null) zone_de_jeu.remove(aire_graphique);
        aire_graphique = new AireGraphique(aire_jeu);
        zone_de_jeu.add(aire_graphique, java.awt.BorderLayout.CENTER);
        zone_de_jeu.revalidate();
        zone_de_jeu.repaint();

        // ajoute le controleur mediateur + les écouteurs d'évènements
        controleur_mediateur = new ControleurMediateur(aire_jeu, aire_graphique, this, niveau_type_j1, niveau_type_j2, joueur_qui_commence);
        EcouteurSourisAire ecouteur_souris_aire = new EcouteurSourisAire(controleur_mediateur);
        aire_graphique.addMouseListener(ecouteur_souris_aire);
        aire_graphique.addMouseMotionListener(ecouteur_souris_aire);
        aire_graphique.afficherAides(mode_debutant);
    }

    /**
     * Méthode appelée lorsque l'on clique sur le bouton Terminer
     * @param evt ignored
     */
    private void bouton_terminerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bouton_terminerActionPerformed
        controleur_mediateur.instruction("Finir tour");
    }//GEN-LAST:event_bouton_terminerActionPerformed

    /**
     * Méthode appelée lorsque l'on clique sur le bouton Annuler
     * @param evt ignored
     */
    private void bouton_annulerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bouton_annulerActionPerformed
        controleur_mediateur.instruction("Annuler");
    }//GEN-LAST:event_bouton_annulerActionPerformed

    /**
     * Méthode appelée lorsque l'on clique sur le bouton Rétablir
     * @param evt ignored
     */
    private void bouton_retablirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bouton_retablirActionPerformed
        controleur_mediateur.instruction("Refaire");
    }//GEN-LAST:event_bouton_retablirActionPerformed

    /**
     * Méthode appelée lorsque l'on clique sur le bouton pour commencer la partie
     * @param evt ignored
     */
    private void bouton_commencerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bouton_commencerActionPerformed

        rapatrierFormulaire();

        // affiche l'aire de jeu (ce qui cache le menu)
        changerPanneau("panneau_jeu");
    }//GEN-LAST:event_bouton_commencerActionPerformed

    /**
     * Méthode qui active ou désactive les boutons radio de difficulté IA lorsque l'on change le type du joueur 1
     * @param evt ignored
     */
    private void radio_type_humain_j1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radio_type_humain_j1ActionPerformed

        Enumeration<AbstractButton> boutons = group_niveau_j1.getElements();
        boolean activer_boutons = radio_type_ia_j1.isSelected();

        while(boutons.hasMoreElements()) {
            boutons.nextElement().setEnabled(activer_boutons);
        }
    }//GEN-LAST:event_radio_type_humain_j1ActionPerformed

    /**
     * Méthode qui active ou désactive les boutons radio de difficulté IA lorsque l'on change le type du joueur 1
     * @param evt ignored
     */
    private void radio_type_ia_j1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radio_type_ia_j1ActionPerformed
        radio_type_humain_j1ActionPerformed(evt);
    }//GEN-LAST:event_radio_type_ia_j1ActionPerformed

    /**
     * Méthode qui active ou désactive les boutons radio de difficulté IA lorsque l'on change le type du joueur 2
     * @param evt ignored
     */
    private void radio_type_humain_j2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radio_type_humain_j2ActionPerformed

        Enumeration<AbstractButton> boutons = group_niveau_j2.getElements();
        boolean activer_boutons = radio_type_ia_j2.isSelected();

        while(boutons.hasMoreElements()) {
            boutons.nextElement().setEnabled(activer_boutons);
        }
    }//GEN-LAST:event_radio_type_humain_j2ActionPerformed

    /**
     * Méthode qui active ou désactive les boutons radio de difficulté IA lorsque l'on change le type du joueur 2
     * @param evt ignored
     */
    private void radio_type_ia_j2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radio_type_ia_j2ActionPerformed
        radio_type_humain_j2ActionPerformed(evt);
    }//GEN-LAST:event_radio_type_ia_j2ActionPerformed

    /**
     * Méthode appelée lorsque l'on clique sur le bouton Charger une partie dans le menu
     * @param evt ignored
     */
    private void menu_chargerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu_chargerActionPerformed
        fenetre_chargement.initListe();
        fenetre_chargement.setVisible(true);
    }//GEN-LAST:event_menu_chargerActionPerformed

    /**
     * Méthode appelée lorsque l'on clique sur le bouton Sauvegarder la partie dans le menu
     * @param evt ignored
     */
    private void menu_sauvegarderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu_sauvegarderActionPerformed
        controleur_mediateur.instruction("Exporter");
    }//GEN-LAST:event_menu_sauvegarderActionPerformed

    /**
     * Affiche une fenêtre de dialogue pour indiquer que le fichier a été supprimé
     * @param nom_fichier le nom du fichier qui a été exporté
     */
    public void afficherInformationNomFichierExport(String nom_fichier) {
        Object[] options = {"OK"};
        JOptionPane.showOptionDialog(this,
            "<html>La partie a été sauvegardé sous le nom <em>" + nom_fichier + "</em>.</html>",
            "Partie sauvegardée",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null,
            options,
            options[0]
        );
    }

    /**
     * Méthode appelée lorsque l'on clique sur le bouton Arrêter la partie dans le menu
     * @param evt ignored
     */
    private void menu_terminerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu_terminerActionPerformed

        // affichage d’une boite de dialogue de confirmation
        Object[] options = {"Terminer et retourner au menu", "Revenir au jeu"};

        // affiche une boite de dialogue et récupère le choix du joueur
        int n = JOptionPane.showOptionDialog(this,
            "Êtes-vous certain de vouloir terminer la partie ? Tous les changements seront perdus.",
            "Terminer la partie",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE,
            null,
            options,
            options[0]
        );

        // agit en fonction du choix de l'utilisateur : ne fait une action que s'il clique sur Oui
        switch (n) {
            case JOptionPane.YES_OPTION:
                controleur_mediateur.instruction("Finir partie");
                changerPanneau("panneau_menu");
            case JOptionPane.NO_OPTION:
            default:
                break;
        }
    }//GEN-LAST:event_menu_terminerActionPerformed

    /**
     * Méthode appelée lorsque l'on clique sur le bouton Quitter dans le menu
     * @param evt ignored
     */
    private void menu_quitterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu_quitterActionPerformed

        // affichage d’une boite de dialogue de confirmation
        Object[] options = {"Oui", "Non"};
        int n = JOptionPane.showOptionDialog(this,
            "Êtes-vous sûr de vouloir quitter ? La partie ne sera pas sauvegardée.",
            "Quitter ?",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE,
            null,
            options,
            options[0]
        );

        // si l'utilisateur a cliqué sur Oui, quitter le programme
        switch (n) {
            case JOptionPane.YES_OPTION:
                System.exit(0);
            case JOptionPane.NO_OPTION:
            default:
                break;
        }
    }//GEN-LAST:event_menu_quitterActionPerformed

    /**
     * Méthode appelée lorsque l'on clique sur le bouton d'affichage des aides
     * @param evt ignored
     */
    private void menu_afficher_les_aidesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu_afficher_les_aidesActionPerformed

        final String etat_inactif = "Afficher les aides";
        final String etat_actif = "Cacher les aides";

        if(menu_afficher_les_aides.getText().equals(etat_actif)) {
            menu_afficher_les_aides.setText(etat_inactif);
            checkbox_debutant.setSelected(false);
            if (aire_graphique != null)
                aire_graphique.afficherAides(false);
        } else {
            menu_afficher_les_aides.setText(etat_actif);
            checkbox_debutant.setSelected(true);
            if (aire_graphique != null)
                aire_graphique.afficherAides(true);
        }
        
        if (aire_graphique != null)
            aire_graphique.repaint();
    }//GEN-LAST:event_menu_afficher_les_aidesActionPerformed

    /**
     * Met à jour le menu pour rester cohérent avec l'état actuel des aides
     * @param evt ignored
     */
    private void checkbox_debutantActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkbox_debutantActionPerformed
        menu_afficher_les_aidesActionPerformed(evt);
    }//GEN-LAST:event_checkbox_debutantActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuBar barre_menu;
    private javax.swing.JButton bouton_annuler;
    private javax.swing.JButton bouton_commencer;
    private javax.swing.JButton bouton_retablir;
    private javax.swing.JButton bouton_terminer;
    private javax.swing.JCheckBox checkbox_debutant;
    private javax.swing.JPanel conteneur_principal;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.ButtonGroup group_debut;
    private javax.swing.ButtonGroup group_niveau_j1;
    private javax.swing.ButtonGroup group_niveau_j2;
    private javax.swing.ButtonGroup group_type_j1;
    private javax.swing.ButtonGroup group_type_j2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel label_debut;
    private javax.swing.JLabel label_joueur1;
    private javax.swing.JLabel label_joueur2;
    private javax.swing.JLabel label_joueur_actif;
    private javax.swing.JMenuItem menu_afficher_les_aides;
    private javax.swing.JMenu menu_bouton;
    private javax.swing.JMenuItem menu_charger;
    private javax.swing.JMenuItem menu_quitter;
    private javax.swing.JMenuItem menu_sauvegarder;
    private javax.swing.JPopupMenu.Separator menu_separator_1;
    private javax.swing.JPopupMenu.Separator menu_separator_2;
    private javax.swing.JPopupMenu.Separator menu_separator_3;
    private javax.swing.JMenuItem menu_terminer;
    private javax.swing.JPanel paneau_jeu;
    private javax.swing.JPanel panel_debut;
    private javax.swing.JPanel panel_debutant;
    private javax.swing.JPanel panel_flottant;
    private javax.swing.JPanel panel_joueur1;
    private javax.swing.JPanel panel_joueur2;
    private javax.swing.JPanel panel_joueurs;
    private javax.swing.JPanel panel_options_ia_j1;
    private javax.swing.JPanel panel_options_ia_j2;
    private javax.swing.JPanel panneau_menu;
    private javax.swing.JRadioButton radio_debut_blanc;
    private javax.swing.JRadioButton radio_debut_noir;
    private javax.swing.JRadioButton radio_niveau_ia_difficile_j1;
    private javax.swing.JRadioButton radio_niveau_ia_difficile_j2;
    private javax.swing.JRadioButton radio_niveau_ia_facile_j1;
    private javax.swing.JRadioButton radio_niveau_ia_facile_j2;
    private javax.swing.JRadioButton radio_niveau_ia_moyen_j1;
    private javax.swing.JRadioButton radio_niveau_ia_moyen_j2;
    private javax.swing.JRadioButton radio_type_humain_j1;
    private javax.swing.JRadioButton radio_type_humain_j2;
    private javax.swing.JRadioButton radio_type_ia_j1;
    private javax.swing.JRadioButton radio_type_ia_j2;
    private javax.swing.JPanel section_boutons_bas;
    private javax.swing.JPanel section_boutons_haut;
    private javax.swing.JPanel section_boutons_haut_panel2;
    private javax.swing.JPanel zone_de_jeu;
    // End of variables declaration//GEN-END:variables
}
