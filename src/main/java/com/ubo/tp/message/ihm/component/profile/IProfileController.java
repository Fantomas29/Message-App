package main.java.com.ubo.tp.message.ihm.component.profile;

import main.java.com.ubo.tp.message.ihm.component.IController;

/**
 * Interface du contrôleur pour la gestion du profil utilisateur.
 */
public interface IProfileController extends IController {

    /**
     * Affiche la vue de profil de l'utilisateur.
     */
    void showProfileView();

    /**
     * Met à jour le profil de l'utilisateur.
     *
     * @param updatedName     Le nouveau nom de l'utilisateur
     * @param currentPassword Le mot de passe actuel pour vérification
     * @param newPassword     Le nouveau mot de passe (peut être vide pour ne pas changer)
     * @param avatarPath      Le chemin vers l'avatar (peut être vide pour ne pas changer)
     * @return true si la mise à jour a réussi, false sinon
     */
    boolean updateProfile(String updatedName, String currentPassword, String newPassword, String avatarPath);

    /**
     * Sélectionne un nouvel avatar pour l'utilisateur
     *
     * @return Le chemin vers le fichier sélectionné, ou null si annulé
     */
    String selectAvatar();

    /**
     * Retourne à la vue principale de l'application.
     */
    void returnToMainView();
}