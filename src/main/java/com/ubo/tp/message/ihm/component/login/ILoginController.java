package main.java.com.ubo.tp.message.ihm.component.login;

import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.component.IController;

/**
 * Interface du contrôleur pour la gestion de l'authentification des utilisateurs.
 */
public interface ILoginController extends IController {

    /**
     * Connecte un utilisateur à partir de son tag et mot de passe.
     *
     * @param userTag Le tag de l'utilisateur
     * @param password Le mot de passe de l'utilisateur
     */
    void loginUser(String userTag, String password);

    /**
     * Inscrit un nouvel utilisateur dans le système.
     *
     * @param user L'utilisateur à inscrire
     */
    void signupUser(User user);

    /**
     * Gère la sélection d'un avatar pour l'inscription
     *
     * @return Le chemin vers l'avatar sélectionné, ou null si annulé
     */
    String selectAvatar();
}