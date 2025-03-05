package main.java.com.ubo.tp.message.ihm.component.profile;

import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.component.IView;

/**
 * Interface de la vue de profil utilisateur
 */
public interface IProfileView extends IView {

    /**
     * Met à jour les informations de l'utilisateur affiché
     *
     * @param connectedUser Utilisateur connecté
     */
    void updateUserInfo(User connectedUser);

    /**
     * Réinitialise les champs avec les valeurs actuelles
     */
    void resetFields();

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