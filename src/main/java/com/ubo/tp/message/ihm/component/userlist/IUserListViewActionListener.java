package main.java.com.ubo.tp.message.ihm.component.userlist;

import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Interface de callback pour les actions de la vue de liste d'utilisateurs
 */
public interface IUserListViewActionListener {
    /**
     * Action d'initialisation de la liste des utilisateurs
     */
    void onInitUserListRequested();

    /**
     * Action de rafraîchissement de la liste des utilisateurs
     */
    void onRefreshUserListRequested();

    /**
     * Action de suivi d'un utilisateur
     * @param userToFollow Utilisateur à suivre
     */
    void onFollowUserRequested(User userToFollow);

    /**
     * Action de désabonnement d'un utilisateur
     * @param userToUnfollow Utilisateur à ne plus suivre
     */
    void onUnfollowUserRequested(User userToUnfollow);

    /**
     * Action de recherche d'utilisateurs
     * @param searchText Texte de recherche
     */
    void onSearchUsersRequested(String searchText);
}