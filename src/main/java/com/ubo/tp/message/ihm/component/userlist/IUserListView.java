package main.java.com.ubo.tp.message.ihm.component.userlist;

import java.util.Set;

import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.component.IView;

/**
 * Interface de la vue de liste des utilisateurs
 */
public interface IUserListView extends IView {

    /**
     * Met à jour la liste des utilisateurs affichés
     *
     * @param users Ensemble des utilisateurs à afficher
     */
    void updateUserList(Set<User> users);

    /**
     * Affiche un message d'erreur
     *
     * @param title Titre du message
     * @param message Contenu du message
     */
    void showError(String title, String message);

    /**
     * Affiche un message d'information
     *
     * @param title Titre du message
     * @param message Contenu du message
     */
    void showInfo(String title, String message);

    /**
     * Met à jour le statut d'abonnement pour les utilisateurs
     *
     * @param connectedUser Utilisateur connecté
     */
    void updateFollowStatus(User connectedUser);
}