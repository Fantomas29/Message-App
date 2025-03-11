package main.java.com.ubo.tp.message.ihm.component.home;

import main.java.com.ubo.tp.message.core.event.EventManager;
import main.java.com.ubo.tp.message.core.event.NavigationEvents;
import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.NotificationManager;
import main.java.com.ubo.tp.message.ihm.component.IController;

/**
 * Contrôleur pour la vue d'accueil
 */
public class HomeController implements IController, IHomeViewActionListener {

    /**
     * Session de l'application
     */
    protected ISession mSession;

    /**
     * Constructeur
     *
     * @param session Session de l'application
     */
    public HomeController(ISession session) {
        this.mSession = session;
    }

    @Override
    public void onShowProfileRequested() {
        EventManager.getInstance().fireEvent(new NavigationEvents.ShowProfileViewEvent());
    }

    @Override
    public void onShowMessagesRequested() {
        // Marquer les messages comme lus
        NotificationManager.getInstance().markAllAsRead();

        // Émettre un événement de navigation
        EventManager.getInstance().fireEvent(new NavigationEvents.ShowMessageViewEvent());
    }

    @Override
    public void onShowUserListRequested() {
        EventManager.getInstance().fireEvent(new NavigationEvents.ShowUserListViewEvent());
    }

    @Override
    public void onLogoutRequested() {
        System.out.println("Demande de déconnexion reçue");

        // Vérifier que l'utilisateur est bien connecté avant de déconnecter
        User connectedUser = mSession.getConnectedUser();
        if (connectedUser != null) {
            System.out.println("Déconnexion de l'utilisateur: @" + connectedUser.getUserTag());

            // Déconnecter l'utilisateur
            mSession.disconnect();

            // Vérifier que la déconnexion a fonctionné
            if (mSession.getConnectedUser() == null) {
                System.out.println("Déconnexion réussie!");
                // Utiliser un événement au lieu d'un appel direct
                EventManager.getInstance().fireEvent(new NavigationEvents.ShowLoginViewEvent());
            } else {
                System.err.println("ERREUR: La déconnexion a échoué!");
            }
        } else {
            System.out.println("Aucun utilisateur connecté à déconnecter");
        }
    }
}