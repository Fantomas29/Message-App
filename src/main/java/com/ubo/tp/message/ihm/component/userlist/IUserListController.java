package main.java.com.ubo.tp.message.ihm.component.userlist;

import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.component.IController;

/**
 * Interface du contrôleur pour la gestion de la liste des utilisateurs
 */
public interface IUserListController extends IController {

    /**
     * Initialise la liste des utilisateurs
     */
    void initUserList();

    /**
     * Rafraîchit la liste des utilisateurs
     */
    void refreshUserList();

    /**
     * Gère l'abonnement à un utilisateur
     *
     * @param userToFollow Utilisateur auquel s'abonner
     */
    void followUser(User userToFollow);

    /**
     * Gère le désabonnement d'un utilisateur
     *
     * @param userToUnfollow Utilisateur duquel se désabonner
     */
    void unfollowUser(User userToUnfollow);

    /**
     * Effectue une recherche d'utilisateurs
     *
     * @param searchText Texte de recherche
     */
    void searchUsers(String searchText);
}