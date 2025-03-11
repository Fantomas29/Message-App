package main.java.com.ubo.tp.message.ihm.component.login;

import main.java.com.ubo.tp.message.ihm.component.IViewWithMessages;

/**
 * Interface de la vue de login/inscription
 */
public interface ILoginView extends IViewWithMessages {
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
     * Notifie la vue d'une erreur lors de la déconnexion
     */
    void notifyLogoutError();
}