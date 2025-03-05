package main.java.com.ubo.tp.message.ihm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import main.java.com.ubo.tp.message.core.event.EventManager;
import main.java.com.ubo.tp.message.core.event.IEventListener;
import main.java.com.ubo.tp.message.core.event.NavigationEvents;
import main.java.com.ubo.tp.message.core.event.SessionEvents;
import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Classe de la barre de menu de l'application.
 */
public class MessageAppMenuBar extends JMenuBar {

    private static final long serialVersionUID = 1L;

    /**
     * Fenêtre principale de l'application
     */
    protected JFrame mMainFrame;

    /**
     * Référence vers la classe technique principale
     */
    protected MessageApp mMessageApp;

    /**
     * Menu "Utilisateur" qui apparaît seulement quand connecté
     */
    protected JMenu mUserMenu;

    /**
     * MenuItem pour se déconnecter
     */
    protected JMenuItem mLogoutMenuItem;

    /**
     * Constructeur.
     *
     * @param mainFrame Fenêtre principale de l'application
     * @param messageApp Référence vers la classe technique principale
     */
    public MessageAppMenuBar(JFrame mainFrame, MessageApp messageApp) {
        this.mMainFrame = mainFrame;
        this.mMessageApp = messageApp;

        // Initialisation des composants graphiques du menu
        this.initMenuComponents();

        // Ajout comme observateur de la session pour gérer l'affichage du menu utilisateur
        this.registerSessionEvents();
    }

    /**
     * Enregistre les écouteurs d'événements de session
     */
    private void registerSessionEvents() {
        EventManager eventManager = EventManager.getInstance();

        // Écouteur pour l'événement de connexion
        eventManager.addListener(SessionEvents.UserLoggedInEvent.class, new IEventListener<SessionEvents.UserLoggedInEvent>() {
            @Override
            public void onEvent(SessionEvents.UserLoggedInEvent event) {
                // Ajout du menu utilisateur à la connexion
                MessageAppMenuBar.this.add(mUserMenu, 1); // Insérer entre "Fichier" et "?"
                MessageAppMenuBar.this.revalidate();
            }
        });

        // Écouteur pour l'événement de déconnexion
        eventManager.addListener(SessionEvents.UserLoggedOutEvent.class, new IEventListener<SessionEvents.UserLoggedOutEvent>() {
            @Override
            public void onEvent(SessionEvents.UserLoggedOutEvent event) {
                // Retrait du menu utilisateur à la déconnexion
                MessageAppMenuBar.this.remove(mUserMenu);
                MessageAppMenuBar.this.revalidate();
            }
        });
    }

    /**
     * Initialisation des composants graphiques du menu
     */
    protected void initMenuComponents() {
        // Création du menu "Fichier"
        JMenu fileMenu = new JMenu("Fichier");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        // Création de l'item "Quitter"
        JMenuItem exitMenuItem = new JMenuItem("Quitter");
        exitMenuItem.setMnemonic(KeyEvent.VK_Q);
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));

        // Ajout d'une icône à l'item "Quitter"
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/main/resources/images/exit.png"));
            // Redimensionner l'icône à une taille raisonnable (16x16 pixels)
            java.awt.Image img = originalIcon.getImage().getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
            ImageIcon exitIcon = new ImageIcon(img);
            exitMenuItem.setIcon(exitIcon);
        } catch (Exception e) {
            System.err.println("Impossible de charger l'icône pour Quitter");
        }

        // Ajout d'un écouteur d'événement pour quitter l'application
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Utilisation de la classe technique pour quitter l'application
                mMessageApp.exitApplication();
            }
        });

        // Ajout de l'item au menu "Fichier"
        fileMenu.add(exitMenuItem);

        // Création du menu "Utilisateur" (initialement invisible)
        mUserMenu = new JMenu("Utilisateur");
        mUserMenu.setMnemonic(KeyEvent.VK_U);

        // Création de l'item "Profil"
        JMenuItem profileMenuItem = new JMenuItem("Mon Profil");
        profileMenuItem.setMnemonic(KeyEvent.VK_P);

        // Ajout d'une icône à l'item "Profil"
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/main/resources/images/user.png"));
            // Redimensionner l'icône à une taille raisonnable (16x16 pixels)
            java.awt.Image img = originalIcon.getImage().getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
            ImageIcon profileIcon = new ImageIcon(img);
            profileMenuItem.setIcon(profileIcon);
        } catch (Exception e) {
            System.err.println("Impossible de charger l'icône pour Profil");
        }

        // Ajout d'un écouteur d'événement pour afficher le profil
        profileMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Émission d'un événement pour demander l'affichage de la vue de profil
                EventManager.getInstance().fireEvent(new NavigationEvents.ShowProfileViewEvent());
            }
        });

        // Création de l'item "Liste des utilisateurs"
        JMenuItem userListMenuItem = new JMenuItem("Liste des utilisateurs");
        userListMenuItem.setMnemonic(KeyEvent.VK_L);

        // Ajout d'une icône à l'item "Liste des utilisateurs"
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/main/resources/images/user_list.png"));
            // Redimensionner l'icône à une taille raisonnable (16x16 pixels)
            java.awt.Image img = originalIcon.getImage().getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
            ImageIcon userListIcon = new ImageIcon(img);
            userListMenuItem.setIcon(userListIcon);
        } catch (Exception e) {
            System.err.println("Impossible de charger l'icône pour Liste des utilisateurs");
        }

        // Ajout d'un écouteur d'événement pour afficher la liste des utilisateurs
        userListMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Appel à la méthode pour afficher la liste des utilisateurs
                mMessageApp.showUserList();
            }
        });

        // Création de l'item "Messages"
        JMenuItem messagesMenuItem = new JMenuItem("Messages");
        messagesMenuItem.setMnemonic(KeyEvent.VK_M);

        // Ajout d'une icône à l'item "Messages"
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/main/resources/images/message.png"));
            // Redimensionner l'icône à une taille raisonnable (16x16 pixels)
            java.awt.Image img = originalIcon.getImage().getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
            ImageIcon messageIcon = new ImageIcon(img);
            messagesMenuItem.setIcon(messageIcon);
        } catch (Exception e) {
            System.err.println("Impossible de charger l'icône pour Messages");
        }

        // Ajout d'un écouteur d'événement pour afficher les messages
        messagesMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Appel à la méthode pour afficher les messages
                mMessageApp.showMessages();
            }
        });

        // Création de l'item "Déconnexion"
        mLogoutMenuItem = new JMenuItem("Déconnexion");
        mLogoutMenuItem.setMnemonic(KeyEvent.VK_D);

        // Ajout d'une icône à l'item "Déconnexion"
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/main/resources/images/logout.png"));
            // Redimensionner l'icône à une taille raisonnable (16x16 pixels)
            java.awt.Image img = originalIcon.getImage().getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
            ImageIcon logoutIcon = new ImageIcon(img);
            mLogoutMenuItem.setIcon(logoutIcon);
        } catch (Exception e) {
            System.err.println("Impossible de charger l'icône pour Déconnexion");
        }

        // Ajout d'un écouteur d'événement pour la déconnexion
        mLogoutMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Déconnexion de l'utilisateur
                mMessageApp.getSession().disconnect();
            }
        });

        // Ajout des items au menu "Utilisateur"
        mUserMenu.add(profileMenuItem);
        mUserMenu.add(userListMenuItem);
        mUserMenu.add(messagesMenuItem);
        mUserMenu.addSeparator(); // Séparateur entre les fonctionnalités et la déconnexion
        mUserMenu.add(mLogoutMenuItem);

        // Création du menu "?"
        JMenu helpMenu = new JMenu("?");
        helpMenu.setMnemonic(KeyEvent.VK_H);

        // Création de l'item "À propos"
        JMenuItem aboutMenuItem = new JMenuItem("À propos");
        aboutMenuItem.setMnemonic(KeyEvent.VK_A);

        // Ajout d'une icône à l'item "À propos"
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/main/resources/images/about.png"));
            // Redimensionner l'icône à une taille raisonnable (16x16 pixels)
            java.awt.Image img = originalIcon.getImage().getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
            ImageIcon aboutIcon = new ImageIcon(img);
            aboutMenuItem.setIcon(aboutIcon);
        } catch (Exception e) {
            System.err.println("Impossible de charger l'icône pour À propos");
        }

        // Ajout d'un écouteur d'événement pour afficher la boîte de dialogue "À propos"
        aboutMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAboutDialog();
            }
        });

        // Ajout de l'item au menu "?"
        helpMenu.add(aboutMenuItem);

        // Ajout des menus à la barre de menu
        this.add(fileMenu);
        this.add(helpMenu);
    }

    /**
     * Affiche la boîte de dialogue "À propos"
     */
    protected void showAboutDialog() {
        // Création d'une boîte de dialogue personnalisée
        JDialog aboutDialog = new JDialog(mMainFrame, "À propos", true);

        // Création du contenu de la boîte de dialogue
        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Ajout du logo UBO
        JLabel logoLabel = new JLabel();
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/main/resources/images/logo_ubo.png"));
            // Pour le logo dans la boîte de dialogue, une taille modérée est appropriée (100x100 pixels max)
            int originalWidth = originalIcon.getIconWidth();
            int originalHeight = originalIcon.getIconHeight();
            int maxSize = 100;

            // Préserver le ratio d'aspect
            int newWidth, newHeight;
            if (originalWidth > originalHeight) {
                newWidth = maxSize;
                newHeight = (originalHeight * maxSize) / originalWidth;
            } else {
                newHeight = maxSize;
                newWidth = (originalWidth * maxSize) / originalHeight;
            }

            java.awt.Image img = originalIcon.getImage().getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH);
            ImageIcon logoIcon = new ImageIcon(img);
            logoLabel.setIcon(logoIcon);
            logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (Exception e) {
            System.err.println("Impossible de charger le logo UBO");
        }

        // Ajout du texte d'information
        JLabel infoLabel = new JLabel("<html><center><b>UBO M2-TIIL</b><br>Département Informatique</center></html>");
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Ajout des composants au panel
        contentPanel.setLayout(new java.awt.BorderLayout());
        contentPanel.add(logoLabel, java.awt.BorderLayout.CENTER);
        contentPanel.add(infoLabel, java.awt.BorderLayout.SOUTH);

        // Configuration de la boîte de dialogue
        aboutDialog.setContentPane(contentPanel);
        aboutDialog.setResizable(false);
        aboutDialog.pack();
        aboutDialog.setLocationRelativeTo(mMainFrame);

        // Affichage de la boîte de dialogue
        aboutDialog.setVisible(true);
    }
}