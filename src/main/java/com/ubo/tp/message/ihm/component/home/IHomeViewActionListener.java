package main.java.com.ubo.tp.message.ihm.component.home;

/**
 * Interface de callback pour les actions de la vue d'accueil
 */
public interface IHomeViewActionListener {
    /**
     * Action de demande d'affichage du profil
     */
    void onShowProfileRequested();

    /**
     * Action de demande d'affichage des messages
     */
    void onShowMessagesRequested();

    /**
     * Action de demande d'affichage de la liste des utilisateurs
     */
    void onShowUserListRequested();

    /**
     * Action de demande de déconnexion
     */
    void onLogoutRequested();
}