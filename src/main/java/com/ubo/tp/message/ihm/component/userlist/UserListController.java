package main.java.com.ubo.tp.message.ihm.component.userlist;

import java.util.*;

import main.java.com.ubo.tp.message.core.EntityManager;
import main.java.com.ubo.tp.message.core.database.IDatabase;
import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Contrôleur pour la gestion de la liste des utilisateurs
 */
public class UserListController implements IUserListController, IUserListViewActionListener {

    /**
     * Référence vers la base de données
     */
    protected IDatabase mDatabase;

    /**
     * Référence vers le gestionnaire d'entités
     */
    protected EntityManager mEntityManager;

    /**
     * Référence vers la session
     */
    protected ISession mSession;

    /**
     * Vue de la liste des utilisateurs
     */
    protected IUserListView mView;

    /**
     * Constructeur.
     *
     * @param database      Référence vers la base de données
     * @param entityManager Référence vers le gestionnaire d'entités
     * @param session       Référence vers la session
     * @param view          Vue de la liste des utilisateurs
     */
    public UserListController(IDatabase database, EntityManager entityManager, ISession session, IUserListView view) {
        this.mDatabase = database;
        this.mEntityManager = entityManager;
        this.mSession = session;
        this.mView = view;
    }

    /**
     * Calcule le nombre réel d'utilisateurs suivis par un utilisateur
     * @param user Utilisateur dont on veut connaître le nombre d'abonnements
     * @param allUsers Ensemble de tous les utilisateurs
     * @return Nombre d'abonnements réels
     */
    private int calculateFollowingCount(User user, Set<User> allUsers) {
        if (user == null) {
            return 0;
        }

        int count = 0;
        Set<String> followedTags = user.getFollows();

        // Pour chaque tag suivi
        for (String tag : followedTags) {
            // Vérifier s'il correspond à un utilisateur existant
            for (User potentialUser : allUsers) {
                if (potentialUser.getUserTag().equals(tag)) {
                    count++;
                    break;
                }
            }
        }

        return count;
    }

    @Override
    public void initUserList() {
        // Récupération de tous les utilisateurs
        Set<User> allUsers = mDatabase.getUsers();

        // Précalculer les statistiques
        Map<UUID, Integer> followersCountMap = new HashMap<>();
        Map<UUID, Integer> followingCountMap = new HashMap<>();

        for (User user : allUsers) {
            // Nombre d'abonnés (personnes qui suivent cet utilisateur)
            followersCountMap.put(user.getUuid(), mDatabase.getFollowers(user).size());

            // Nombre d'abonnements (personnes que suit cet utilisateur)
            followingCountMap.put(user.getUuid(), calculateFollowingCount(user, allUsers));
        }

        // Mise à jour de la vue
        mView.updateUserList(allUsers, followersCountMap, followingCountMap);

        // Mise à jour du statut d'abonnement
        User connectedUser = mSession.getConnectedUser();
        if (connectedUser != null) {
            mView.updateFollowStatus(connectedUser);
        }
    }

    @Override
    public void refreshUserList() {
        initUserList();
    }

    @Override
    public void followUser(User userToFollow) {
        // Récupération de l'utilisateur connecté
        User connectedUser = mSession.getConnectedUser();

        if (connectedUser == null) {
            mView.showError("Erreur", "Aucun utilisateur connecté");
            return;
        }

        // Vérification que l'utilisateur ne s'abonne pas à lui-même
        if (connectedUser.equals(userToFollow)) {
            mView.showError("Erreur", "Vous ne pouvez pas vous abonner à vous-même");
            return;
        }

        // Vérification que l'utilisateur n'est pas déjà abonné
        if (connectedUser.isFollowing(userToFollow)) {
            mView.showInfo("Information", "Vous êtes déjà abonné à @" + userToFollow.getUserTag());
            return;
        }

        // Abonnement à l'utilisateur
        connectedUser.addFollowing(userToFollow.getUserTag());

        // Mise à jour dans la base de données
        mDatabase.modifiyUser(connectedUser);

        // Écriture du fichier utilisateur
        mEntityManager.writeUserFile(connectedUser);

        // Rafraîchir la liste pour mettre à jour les statistiques
        refreshUserList();
    }

    @Override
    public void unfollowUser(User userToUnfollow) {
        // Récupération de l'utilisateur connecté
        User connectedUser = mSession.getConnectedUser();

        if (connectedUser == null) {
            mView.showError("Erreur", "Aucun utilisateur connecté");
            return;
        }

        // Vérification que l'utilisateur est bien abonné
        if (!connectedUser.isFollowing(userToUnfollow)) {
            mView.showInfo("Information", "Vous n'êtes pas abonné à @" + userToUnfollow.getUserTag());
            return;
        }

        // Désabonnement de l'utilisateur
        connectedUser.removeFollowing(userToUnfollow.getUserTag());

        // Mise à jour dans la base de données
        mDatabase.modifiyUser(connectedUser);

        // Écriture du fichier utilisateur
        mEntityManager.writeUserFile(connectedUser);

        // Rafraîchir la liste pour mettre à jour les statistiques
        refreshUserList();
    }

    @Override
    public void searchUsers(String searchText) {
        // Si la recherche est vide, afficher tous les utilisateurs
        if (searchText == null || searchText.trim().isEmpty()) {
            initUserList();
            return;
        }

        // Récupération de tous les utilisateurs
        Set<User> allUsers = mDatabase.getUsers();
        Set<User> filteredUsers = new HashSet<>();

        // Maps pour stocker les statistiques
        Map<UUID, Integer> followersCountMap = new HashMap<>();
        Map<UUID, Integer> followingCountMap = new HashMap<>();

        // Filtre des utilisateurs selon le texte de recherche (tag ou nom)
        String lowerSearchText = searchText.toLowerCase();

        for (User user : allUsers) {
            if (user.getUserTag().toLowerCase().contains(lowerSearchText) ||
                    user.getName().toLowerCase().contains(lowerSearchText)) {
                filteredUsers.add(user);

                // Calculer les statistiques
                followersCountMap.put(user.getUuid(), mDatabase.getFollowers(user).size());
                followingCountMap.put(user.getUuid(), calculateFollowingCount(user, allUsers));
            }
        }

        // Mise à jour de la vue
        mView.updateUserList(filteredUsers, followersCountMap, followingCountMap);

        // Mise à jour du statut d'abonnement
        User connectedUser = mSession.getConnectedUser();
        if (connectedUser != null) {
            mView.updateFollowStatus(connectedUser);
        }
    }

    /**
     * Permet d'accéder à la base de données depuis la vue (pour les statistiques)
     *
     * @return la base de données
     */
    public IDatabase getDatabase() {
        return mDatabase;
    }

    // Implémentation des méthodes de callback
    @Override
    public void onInitUserListRequested() {
        this.initUserList();
    }

    @Override
    public void onRefreshUserListRequested() {
        this.refreshUserList();
    }

    @Override
    public void onFollowUserRequested(User userToFollow) {
        this.followUser(userToFollow);
    }

    @Override
    public void onUnfollowUserRequested(User userToUnfollow) {
        this.unfollowUser(userToUnfollow);
    }

    @Override
    public void onSearchUsersRequested(String searchText) {
        this.searchUsers(searchText);
    }
}