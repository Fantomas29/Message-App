package main.java.com.ubo.tp.message.ihm.component.userlist;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.component.IViewWithMessages;

/**
 * Interface de la vue de liste des utilisateurs
 */
public interface IUserListView extends IViewWithMessages {
    /**
     * Met à jour la liste des utilisateurs affichés avec leurs statistiques
     *
     * @param users Ensemble des utilisateurs à afficher
     * @param followersCountMap Carte des nombres d'abonnés pour chaque utilisateur
     */
    void updateUserList(Set<User> users, Map<UUID, Integer> followersCountMap);

    /**
     * Met à jour le statut d'abonnement pour les utilisateurs
     *
     * @param connectedUser Utilisateur connecté
     */
    void updateFollowStatus(User connectedUser);
}