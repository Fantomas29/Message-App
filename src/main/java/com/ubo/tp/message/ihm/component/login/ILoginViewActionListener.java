package main.java.com.ubo.tp.message.ihm.component.login;

/**
 * Interface de callback pour les actions de la vue de login
 */
public interface ILoginViewActionListener {
    /**
     * Action de connexion d'un utilisateur
     * @param userTag Tag de l'utilisateur
     * @param password Mot de passe
     */
    void onLoginRequested(String userTag, String password);

    /**
     * Action d'inscription d'un utilisateur
     * @param name Nom de l'utilisateur
     * @param tag Tag de l'utilisateur
     * @param password Mot de passe
     * @param avatarPath Chemin vers l'avatar
     */
    void onSignupRequested(String name, String tag, String password, String avatarPath);

    /**
     * Action de sélection d'un avatar
     */
    String onAvatarSelectionRequested();
}