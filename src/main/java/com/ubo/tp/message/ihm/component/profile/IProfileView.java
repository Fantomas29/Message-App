package main.java.com.ubo.tp.message.ihm.component.profile;

import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.component.IViewWithMessages;

/**
 * Interface de la vue de profil utilisateur
 */
public interface IProfileView extends IViewWithMessages {
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
}