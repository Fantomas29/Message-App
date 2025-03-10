package main.java.com.ubo.tp.message.ihm.component.home;

import main.java.com.ubo.tp.message.ihm.component.IController;

public interface IHomeController extends IController {
    /**
     * Affiche la vue de profil
     */
    void showProfile();

    /**
     * Affiche la vue des messages
     */
    void showMessages();

    /**
     * Affiche la vue de la liste des utilisateurs
     */
    void showUserList();

    /**
     * Gère la déconnexion de l'utilisateur
     */
    void logout();

    /**
     * Met à jour le badge de messages non lus
     * @param count nombre de messages non lus
     */
    void updateUnreadBadge(int count);
}