package main.java.com.ubo.tp.message.core.event;

/**
 * Événements liés à la navigation dans l'application
 */
public class NavigationEvents {

    /**
     * Événement pour demander l'affichage de la vue de login
     */
    public static class ShowLoginViewEvent implements IEvent {
        // Pas de données supplémentaires nécessaires
    }

    /**
     * Événement pour demander l'affichage de la vue principale
     */
    public static class ShowMainViewEvent implements IEvent {
        // Pas de données supplémentaires nécessaires
    }

    /**
     * Événement pour demander l'affichage de la vue de profil
     */
    public static class ShowProfileViewEvent implements IEvent {
        // Pas de données supplémentaires nécessaires
    }

    /**
     * Événement pour demander l'affichage de la vue de liste des utilisateurs
     */
    public static class ShowUserListViewEvent implements IEvent {
        // Pas de données supplémentaires nécessaires
    }
}