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

import main.java.com.ubo.tp.message.core.event.EventManager;
import main.java.com.ubo.tp.message.core.event.NavigationEvents;
import main.java.com.ubo.tp.message.core.event.SessionEvents;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.component.home.HomeView;
import main.java.com.ubo.tp.message.ihm.component.login.LoginController;
import main.java.com.ubo.tp.message.ihm.component.profile.IProfileController;

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
    protected HomeView mMainView;

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
    protected static final String MESSAGE_VIEW = "MESSAGE_VIEW";

    /**
     * Constructeur.
     *
     * @param messageApp Référence vers la classe technique principale
     */
    public MessageAppIHM(MessageApp messageApp) {
        this.mMessageApp = messageApp;

        // Enregistrement aux événements de session et de navigation
        this.registerSessionEvents();
    }

    /**
     * Enregistre les écouteurs d'événements de session
     */
    private void registerSessionEvents() {
        EventManager eventManager = EventManager.getInstance();

        // Événement de connexion
        eventManager.addListener(SessionEvents.UserLoggedInEvent.class, event -> onUserLogin(event.getUser()));

        // Événement de déconnexion
        eventManager.addListener(SessionEvents.UserLoggedOutEvent.class, event -> onUserLogout());

        // Événement de mise à jour de profil
        eventManager.addListener(SessionEvents.UserProfileUpdatedEvent.class, event -> {
            // Mettre à jour la vue d'accueil avec les informations utilisateur à jour
            mMainView.updateUserInfo(event.getUser());
        });
    }

    /**
     * Enregistre les écouteurs d'événements de navigation
     */
    private void registerNavigationEvents() {
        EventManager eventManager = EventManager.getInstance();

        // Écouter l'événement de navigation pour la liste des utilisateurs
        eventManager.addListener(NavigationEvents.ShowUserListViewEvent.class, event -> showUserListView());

        // Écouter l'événement de navigation pour le profil
        eventManager.addListener(NavigationEvents.ShowProfileViewEvent.class, event -> showProfileView());

        // Écouter l'événement de navigation pour les messages
        eventManager.addListener(NavigationEvents.ShowMessageViewEvent.class, event -> showMessageView());

        // Écouter l'événement pour l'affichage de la vue principale
        eventManager.addListener(NavigationEvents.ShowMainViewEvent.class, event -> showMainView());

        // Écouter l'événement pour l'affichage de la vue de login
        eventManager.addListener(NavigationEvents.ShowLoginViewEvent.class, event -> showLoginView());
    }

    /**
     * Initialisation de l'interface utilisateur.
     */
    public void init() {
        // Configuration initiale de l'IHM
        initUIComponents();

        // Configuration du gestionnaire de notifications
        NotificationManager.getInstance().setMainFrame(mFrame);

        // Enregistrement des écouteurs d'événements de navigation
        this.registerNavigationEvents();

        // Demande de sélection du répertoire d'échange
        this.askForExchangeDirectory();
    }

    /**
     * Initialise les composants de l'interface utilisateur.
     */
    private void initUIComponents() {
        // Initialisation du look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Création et configuration de la fenêtre principale
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
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/images/logo_message.png"));
            java.awt.Image img = originalIcon.getImage().getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH);
            mFrame.setIconImage(img);
        } catch (Exception e) {
            System.err.println("Impossible de charger l'icône de l'application");
        }

        // Initialisation du panel principal avec CardLayout
        mCardLayout = new CardLayout();
        mMainPanel = new JPanel(mCardLayout);
        mFrame.getContentPane().add(mMainPanel, BorderLayout.CENTER);

        // Initialisation de la vue principale d'accueil
        mMainView = new HomeView(mMessageApp.getSession());
        mMainView.setActionListener(mMessageApp.getHomeController());

        // Ajout des vues au CardLayout
        mMainPanel.add(mMessageApp.getLoginView().getComponent(), LOGIN_VIEW);
        mMainPanel.add(mMainView, MAIN_VIEW);

        // Affichage de la vue de login par défaut
        mCardLayout.show(mMainPanel, LOGIN_VIEW);

        // Initialisation de la barre de menu
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
     * Méthode pour afficher la vue de profil
     */
    public void showProfileView() {
        JComponent profileView = mMessageApp.getProfileView().getComponent();
        showView(profileView, PROFILE_VIEW);
        IProfileController iProfileController = mMessageApp.mProfileController;
        iProfileController.showProfileView();
    }

    /**
     * Méthode pour afficher la vue de liste des utilisateurs
     */
    public void showUserListView() {
        JComponent userListView = mMessageApp.getUserListView().getComponent();
        showView(userListView, USER_LIST_VIEW);
    }

    /**
     * Méthode pour afficher la vue des messages
     */
    public void showMessageView() {
        JComponent messageView = mMessageApp.getMessageView().getComponent();
        NotificationManager.getInstance().markAllAsRead();
        showView(messageView, MESSAGE_VIEW);
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

        // Mettre à jour les informations utilisateur sur la vue d'accueil
        mMainView.updateUserInfo(connectedUser);
    }

    /**
     * Méthode appelée lorsqu'un utilisateur se déconnecte
     */
    public void onUserLogout() {
        System.out.println("Méthode onUserLogout() appelée dans MessageAppIHM");

        // Vérifier que l'utilisateur est réellement déconnecté dans la session
        if (mMessageApp.getSession().getConnectedUser() != null) {
            System.err.println("ALERTE: Session incohérente - l'utilisateur est toujours connecté!");
        } else {
            System.out.println("Session vérifiée: utilisateur correctement déconnecté");
        }

        // Retour à la vue de login
        mCardLayout.show(mMainPanel, LOGIN_VIEW);

        // Réinitialisation du titre
        mFrame.setTitle("MessageApp");

        // Mise à jour de la vue principale (utilisateur déconnecté)
        mMainView.updateUserInfo(null);

        // Validation explicite de l'état de déconnexion dans la vue de login
        if (mMessageApp.getLoginController() instanceof LoginController) {
            ((LoginController) mMessageApp.getLoginController()).validateLogoutState();
        }

        System.out.println("Interface mise à jour pour refléter la déconnexion");
    }

    /**
     * Méthode générique pour afficher une vue avec gestion dynamique du CardLayout
     *
     * @param view La vue à afficher
     * @param viewName Le nom de la vue dans le CardLayout
     */
    private void showView(JComponent view, String viewName) {
        // Vérifier si la vue existe déjà dans le panel
        boolean viewExists = false;
        for (Component comp : mMainPanel.getComponents()) {
            if (comp.equals(view)) {
                viewExists = true;
                break;
            }
        }

        // Si la vue n'existe pas, l'ajouter
        if (!viewExists) {
            mMainPanel.add(view, viewName);
        } else {
            // Si la vue existe, la remplacer
            mMainPanel.remove(view);
            mMainPanel.add(view, viewName);
            mMainPanel.revalidate();
        }

        // Afficher la vue
        mCardLayout.show(mMainPanel, viewName);
    }

}