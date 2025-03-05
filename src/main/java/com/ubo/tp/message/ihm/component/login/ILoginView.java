package main.java.com.ubo.tp.message.ihm.component.login;

import main.java.com.ubo.tp.message.ihm.component.IView;

/**
 * Interface de la vue de login/inscription
 */
public interface ILoginView extends IView {

    /**
     * Réinitialise les champs du formulaire de connexion
     */
    void resetLoginFields();

    /**
     * Réinitialise les champs du formulaire d'inscription
     */
    void resetSignupFields();

    /**
     * Met à jour le chemin d'avatar affiché
     *
     * @param avatarPath Chemin vers l'avatar
     */
    void updateAvatarPath(String avatarPath);

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
}