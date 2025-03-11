package main.java.com.ubo.tp.message.core.navigation;

import main.java.com.ubo.tp.message.core.event.EventManager;
import main.java.com.ubo.tp.message.core.event.IEventListener;
import main.java.com.ubo.tp.message.core.event.NavigationEvents;
import main.java.com.ubo.tp.message.ihm.MessageAppIHM;
import main.java.com.ubo.tp.message.ihm.component.message.IMessageController;
import main.java.com.ubo.tp.message.ihm.component.userlist.IUserListController;
import main.java.com.ubo.tp.message.ihm.NotificationManager;

/**
 * Service responsable de la navigation entre les différentes vues de l'application
 */
public class NavigationService {

    /**
     * Interface utilisateur de l'application
     */
    private MessageAppIHM mIHM;

    /**
     * Contrôleur des messages
     */
    private IMessageController mMessageController;

    /**
     * Contrôleur de la liste des utilisateurs
     */
    private IUserListController mUserListController;

    /**
     * Constructeur
     *
     * @param ihm Interface utilisateur de l'application
     * @param messageController Contrôleur des messages
     * @param userListController Contrôleur de la liste des utilisateurs
     */
    public NavigationService(MessageAppIHM ihm,
                             IMessageController messageController,
                             IUserListController userListController) {
        this.mIHM = ihm;
        this.mMessageController = messageController;
        this.mUserListController = userListController;

        // Enregistrement des écouteurs d'événements
        this.registerEventListeners();
    }

    /**
     * Enregistre les écouteurs d'événements pour la navigation
     */
    private void registerEventListeners() {
        EventManager eventManager = EventManager.getInstance();

        // Navigation vers la vue principale
        eventManager.addListener(NavigationEvents.ShowMainViewEvent.class,
                new IEventListener<NavigationEvents.ShowMainViewEvent>() {
                    @Override
                    public void onEvent(NavigationEvents.ShowMainViewEvent event) {
                        showMainView();
                    }
                });

        // Navigation vers la vue de login
        eventManager.addListener(NavigationEvents.ShowLoginViewEvent.class,
                new IEventListener<NavigationEvents.ShowLoginViewEvent>() {
                    @Override
                    public void onEvent(NavigationEvents.ShowLoginViewEvent event) {
                        showLoginView();
                    }
                });

        // Navigation vers la vue de profil
        eventManager.addListener(NavigationEvents.ShowProfileViewEvent.class,
                new IEventListener<NavigationEvents.ShowProfileViewEvent>() {
                    @Override
                    public void onEvent(NavigationEvents.ShowProfileViewEvent event) {
                        showProfileView();
                    }
                });

        // Navigation vers la vue des messages
        eventManager.addListener(NavigationEvents.ShowMessageViewEvent.class,
                new IEventListener<NavigationEvents.ShowMessageViewEvent>() {
                    @Override
                    public void onEvent(NavigationEvents.ShowMessageViewEvent event) {
                        showMessageView();
                    }
                });

        // Navigation vers la vue de la liste des utilisateurs
        eventManager.addListener(NavigationEvents.ShowUserListViewEvent.class,
                new IEventListener<NavigationEvents.ShowUserListViewEvent>() {
                    @Override
                    public void onEvent(NavigationEvents.ShowUserListViewEvent event) {
                        showUserListView();
                    }
                });
    }

    /**
     * Affiche la vue principale
     */
    private void showMainView() {
        mIHM.showMainView();
    }

    /**
     * Affiche la vue de login
     */
    private void showLoginView() {
        mIHM.showLoginView();
    }

    /**
     * Affiche la vue de profil
     */
    private void showProfileView() {
        mIHM.showProfileView();
    }

    /**
     * Affiche la vue des messages
     */
    private void showMessageView() {
        // Marquer les messages comme lus avant d'afficher la vue
        NotificationManager.getInstance().markAllAsRead();

        // Rafraîchir les messages
        mMessageController.refreshMessages();

        // Afficher la vue
        mIHM.showMessageView();
    }

    /**
     * Affiche la vue de la liste des utilisateurs
     */
    private void showUserListView() {
        // Rafraîchir la liste des utilisateurs
        mUserListController.refreshUserList();

        // Afficher la vue
        mIHM.showUserListView();
    }
}