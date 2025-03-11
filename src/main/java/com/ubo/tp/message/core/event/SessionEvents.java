package main.java.com.ubo.tp.message.core.event;

import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Événements liés à la session utilisateur
 */
public class SessionEvents {

    /**
     * Événement émis lors de la connexion d'un utilisateur
     */
    public static class UserLoggedInEvent implements IEvent {
        private final User user;

        public UserLoggedInEvent(User user) {
            this.user = user;
        }

        public User getUser() {
            return user;
        }
    }

    /**
     * Événement émis lors de la déconnexion d'un utilisateur
     */
    public static class UserLoggedOutEvent implements IEvent {
        // Pas de données supplémentaires nécessaires
    }

    /**
     * Événement émis lorsqu'un profil utilisateur est mis à jour
     */
    public static class UserProfileUpdatedEvent implements IEvent {
        private final User user;

        public UserProfileUpdatedEvent(User user) {
            this.user = user;
        }

        public User getUser() {
            return user;
        }
    }
}