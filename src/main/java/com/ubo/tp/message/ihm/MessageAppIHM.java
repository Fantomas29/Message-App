package main.java.com.ubo.tp.message.ihm;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import main.java.com.ubo.tp.message.core.event.EventManager;
import main.java.com.ubo.tp.message.core.event.IEventListener;
import main.java.com.ubo.tp.message.core.event.NavigationEvents;
import main.java.com.ubo.tp.message.core.event.SessionEvents;
import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Classe principale de l'interface utilisateur de l'application.
 */
public class MessageAppIHM {

    /**
     * Référence vers la classe technique principale.
     */
    protected MessageApp mMessageApp;

    /**
     * Vue principale de l'application.
     */
    protected MessageAppMainView mMainView;

    /**
     * Fenêtre principale de l'application.
     */
    protected JFrame mFrame;

    /**
     * Panel principal avec CardLayout pour les différentes vues
     */
    protected JPanel mMainPanel;

    /**
     * CardLayout pour la gestion des vues
     */
    protected CardLayout mCardLayout;

    /**
     * Constantes pour les noms des cartes
     */
    protected static final String LOGIN_VIEW = "LOGIN_VIEW";
    protected static final String MAIN_VIEW = "MAIN_VIEW";
    protected static final String PROFILE_VIEW = "PROFILE_VIEW";
    protected static final String USER_LIST_VIEW = "USER_LIST_VIEW";

    /**
     * Constructeur.
     *
     * @param messageApp Référence vers la classe technique principale
     */
    public MessageAppIHM(MessageApp messageApp) {
        this.mMessageApp = messageApp;

        // Enregistrement aux événements de session
        this.registerSessionEvents();
    }

    /**
     * Enregistre les écouteurs d'événements de session
     */
    private void registerSessionEvents() {
        EventManager eventManager = EventManager.getInstance();

        // Événement de connexion
        eventManager.addListener(SessionEvents.UserLoggedInEvent.class, new IEventListener<SessionEvents.UserLoggedInEvent>() {
            @Override
            public void onEvent(SessionEvents.UserLoggedInEvent event) {
                onUserLogin(event.getUser());
            }
        });

        // Événement de déconnexion
        eventManager.addListener(SessionEvents.UserLoggedOutEvent.class, new IEventListener<SessionEvents.UserLoggedOutEvent>() {
            @Override
            public void onEvent(SessionEvents.UserLoggedOutEvent event) {
                onUserLogout();
            }
        });
    }

    /**
     * Enregistre les écouteurs d'événements de navigation
     */
    private void registerNavigationEvents() {
        EventManager eventManager = EventManager.getInstance();

        // Écouter l'événement de navigation pour la liste des utilisateurs
        eventManager.addListener(NavigationEvents.ShowUserListViewEvent.class, new IEventListener<NavigationEvents.ShowUserListViewEvent>() {
            @Override
            public void onEvent(NavigationEvents.ShowUserListViewEvent event) {
                showUserListView(mMessageApp.getUserListView().getComponent());
            }
        });

        // Écouter l'événement de navigation pour le profil
        eventManager.addListener(NavigationEvents.ShowProfileViewEvent.class, new IEventListener<NavigationEvents.ShowProfileViewEvent>() {
            @Override
            public void onEvent(NavigationEvents.ShowProfileViewEvent event) {
                showProfileView(mMessageApp.getProfileView().getComponent());
            }
        });

        // Écouter l'événement pour l'affichage de la vue principale
        eventManager.addListener(NavigationEvents.ShowMainViewEvent.class, new IEventListener<NavigationEvents.ShowMainViewEvent>() {
            @Override
            public void onEvent(NavigationEvents.ShowMainViewEvent event) {
                showMainView();
            }
        });

        // Écouter l'événement pour l'affichage de la vue de login
        eventManager.addListener(NavigationEvents.ShowLoginViewEvent.class, new IEventListener<NavigationEvents.ShowLoginViewEvent>() {
            @Override
            public void onEvent(NavigationEvents.ShowLoginViewEvent event) {
                showLoginView();
            }
        });
    }

    /**
     * Initialisation de l'interface utilisateur.
     */
    public void init() {
        // Initialisation du look and feel
        this.initLookAndFeel();

        // Création et configuration de la fenêtre principale
        this.createMainFrame();

        // Initialisation des vues
        this.initViews();

        // Initialisation de la barre de menu
        this.initMenuBar();

        // Enregistrement des écouteurs d'événements de navigation
        this.registerNavigationEvents();

        // Demande de sélection du répertoire d'échange
        this.askForExchangeDirectory();
    }

    /**
     * Initialisation du look and feel du système.
     */
    protected void initLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                 | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    /**
     * Création et configuration de la fenêtre principale.
     */
    protected void createMainFrame() {
        mFrame = new JFrame("MessageApp");
        mFrame.setSize(800, 600);
        mFrame.setLocationRelativeTo(null);
        mFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // Gestion de la fermeture de la fenêtre
        mFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Demander à la classe technique de quitter proprement
                mMessageApp.exitApplication();
            }
        });

        // Ajout d'une icône à l'application
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/main/resources/images/logo_message.png"));
            java.awt.Image img = originalIcon.getImage().getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH);
            mFrame.setIconImage(img);
        } catch (Exception e) {
            System.err.println("Impossible de charger l'icône de l'application");
        }
    }

    /**
     * Initialisation des vues de l'application.
     */
    protected void initViews() {
        // Initialisation du panel principal avec CardLayout
        mCardLayout = new CardLayout();
        mMainPanel = new JPanel(mCardLayout);
        mFrame.getContentPane().add(mMainPanel, BorderLayout.CENTER);

        // Initialisation de la vue principale
        mMainView = new MessageAppMainView();

        // Ajout de la vue comme observateur de la base de données
        mMessageApp.getDatabase().addObserver(mMainView);

        // Ajout des vues au CardLayout
        mMainPanel.add(mMessageApp.getLoginView().getComponent(), LOGIN_VIEW);
        mMainPanel.add(mMainView, MAIN_VIEW);

        // Affichage de la vue de login par défaut
        mCardLayout.show(mMainPanel, LOGIN_VIEW);
    }

    /**
     * Initialisation de la barre de menu.
     */
    protected void initMenuBar() {
        MessageAppMenuBar menuBar = new MessageAppMenuBar(mFrame, mMessageApp);
        mFrame.setJMenuBar(menuBar);
    }

    /**
     * Demande à l'utilisateur de sélectionner le répertoire d'échange.
     */
    protected void askForExchangeDirectory() {
        // Récupération du répertoire configuré
        String currentPath = mMessageApp.getConfiguredDirectoryPath();
        boolean needNewDirectory = true;

        if (currentPath != null && mMessageApp.isValidExchangeDirectory(new File(currentPath))) {
            // Un répertoire valide existe, demander à l'utilisateur s'il veut le changer
            int choice = JOptionPane.showConfirmDialog(mFrame,
                    "Répertoire d'échange actuel : " + currentPath + "\n\nSouhaitez-vous utiliser un autre répertoire d'échange ?",
                    "Configuration du répertoire d'échange",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (choice == JOptionPane.NO_OPTION) {
                // L'utilisateur veut garder le répertoire existant
                needNewDirectory = false;
                // Informer la classe technique d'utiliser ce répertoire
                if (!mMessageApp.initDirectory(currentPath)) {
                    // Si l'initialisation échoue, on aura besoin d'un nouveau répertoire
                    needNewDirectory = true;
                    JOptionPane.showMessageDialog(mFrame,
                            "Impossible d'utiliser le répertoire configuré.",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        // Si un nouveau répertoire est nécessaire
        while (needNewDirectory) {
            // Afficher message d'information
            JOptionPane.showMessageDialog(mFrame,
                    "Veuillez sélectionner le répertoire d'échange pour l'application.",
                    "Configuration nécessaire",
                    JOptionPane.INFORMATION_MESSAGE);

            // Afficher le sélecteur de fichiers
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Sélectionner le répertoire d'échange");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setAcceptAllFileFilterUsed(false);

            // Positionner le sélecteur dans le répertoire actuel si existant
            if (currentPath != null) {
                File directory = new File(currentPath);
                if (directory.exists()) {
                    fileChooser.setCurrentDirectory(directory);
                }
            }

            // Afficher le sélecteur et récupérer la réponse
            int result = fileChooser.showOpenDialog(mFrame);

            if (result == JFileChooser.APPROVE_OPTION) {
                // L'utilisateur a sélectionné un répertoire
                File selectedDirectory = fileChooser.getSelectedFile();
                String directoryPath = selectedDirectory.getAbsolutePath();

                // Demander à la classe technique de vérifier et initialiser ce répertoire
                if (mMessageApp.initDirectory(directoryPath)) {
                    // Si l'initialisation réussit, sauvegarder la configuration
                    mMessageApp.saveDirectoryInConfiguration(directoryPath);
                    needNewDirectory = false;
                } else {
                    // Si l'initialisation échoue, afficher une erreur
                    JOptionPane.showMessageDialog(mFrame,
                            "Le répertoire sélectionné n'est pas valide.",
                            "Erreur de sélection",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // L'utilisateur a annulé la sélection
                int choice = JOptionPane.showConfirmDialog(mFrame,
                        "Un répertoire d'échange est nécessaire pour le fonctionnement de l'application.\nVoulez-vous réessayer?",
                        "Configuration requise",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (choice == JOptionPane.NO_OPTION) {
                    // L'utilisateur ne veut pas configurer, demander à la classe technique de quitter
                    mMessageApp.exitApplication();
                }
                // Sinon, la boucle continue et demande à nouveau un répertoire
            }
        }
    }

    /**
     * Affiche l'interface utilisateur.
     */
    public void show() {
        mFrame.setVisible(true);
    }

    /**
     * Affiche la vue de login
     */
    public void showLoginView() {
        mCardLayout.show(mMainPanel, LOGIN_VIEW);
    }

    /**
     * Affiche la vue principale
     */
    public void showMainView() {
        mCardLayout.show(mMainPanel, MAIN_VIEW);
    }

    /**
     * Méthode appelée pour afficher la vue de profil
     *
     * @param profileView La vue de profil à afficher
     */
    public void showProfileView(JComponent profileView) {
        // Vérifiez d'abord si la vue de profil est déjà dans le CardLayout
        boolean profileViewExists = false;
        for (Component comp : mMainPanel.getComponents()) {
            if (comp.equals(profileView)) {
                profileViewExists = true;
                break;
            }
        }

        // Si la vue n'existe pas encore, l'ajouter
        if (!profileViewExists) {
            mMainPanel.add(profileView, PROFILE_VIEW);
        } else {
            // Sinon, la remplacer
            mMainPanel.remove(profileView);
            mMainPanel.add(profileView, PROFILE_VIEW);
            mMainPanel.revalidate();
        }

        // Afficher la vue de profil
        mCardLayout.show(mMainPanel, PROFILE_VIEW);
    }

    /**
     * Méthode appelée pour afficher la vue de liste des utilisateurs
     *
     * @param userListView La vue de liste des utilisateurs à afficher
     */
    public void showUserListView(JComponent userListView) {
        // Vérifiez d'abord si la vue est déjà dans le CardLayout
        boolean userListViewExists = false;
        for (Component comp : mMainPanel.getComponents()) {
            if (comp.equals(userListView)) {
                userListViewExists = true;
                break;
            }
        }

        // Si la vue n'existe pas encore, l'ajouter
        if (!userListViewExists) {
            mMainPanel.add(userListView, USER_LIST_VIEW);
        } else {
            // Sinon, la remplacer
            mMainPanel.remove(userListView);
            mMainPanel.add(userListView, USER_LIST_VIEW);
            mMainPanel.revalidate();
        }

        // Afficher la vue de liste des utilisateurs
        mCardLayout.show(mMainPanel, USER_LIST_VIEW);
    }

    /**
     * Méthode appelée lorsqu'un utilisateur se connecte
     *
     * @param connectedUser L'utilisateur connecté
     */
    public void onUserLogin(User connectedUser) {
        // Passage à la vue principale
        mCardLayout.show(mMainPanel, MAIN_VIEW);

        // Mettre à jour le titre avec le nom de l'utilisateur
        mFrame.setTitle("MessageApp - " + connectedUser.getName() + " (@" + connectedUser.getUserTag() + ")");

        // Message de bienvenue dans le log
        mMainView.appendToEventLog("Utilisateur connecté : @" + connectedUser.getUserTag());
    }

    /**
     * Méthode appelée lorsqu'un utilisateur se déconnecte
     */
    public void onUserLogout() {
        // Retour à la vue de login
        mCardLayout.show(mMainPanel, LOGIN_VIEW);

        // Réinitialisation du titre
        mFrame.setTitle("MessageApp");

        // Message dans le log
        mMainView.appendToEventLog("Déconnexion de l'utilisateur");
    }
}