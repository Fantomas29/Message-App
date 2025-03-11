package main.java.com.ubo.tp.message.ihm.component;

/**
 * Interface pour les vues qui peuvent afficher des messages
 */
public interface IViewWithMessages extends IView {
    /**
     * Affiche un message d'erreur
     * @param title Titre du message
     * @param message Contenu du message
     */
    void showError(String title, String message);

    /**
     * Affiche un message d'information
     * @param title Titre du message
     * @param message Contenu du message
     */
    void showInfo(String title, String message);
}