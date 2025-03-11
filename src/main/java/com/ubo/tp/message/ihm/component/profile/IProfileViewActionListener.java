package main.java.com.ubo.tp.message.ihm.component.profile;

/**
 * Interface de callback pour les actions de la vue de profil
 */
public interface IProfileViewActionListener {
    /**
     * Action de mise à jour du profil utilisateur
     *
     * @param updatedName     Nom mis à jour
     * @param currentPassword Mot de passe actuel
     * @param newPassword     Nouveau mot de passe
     * @param avatarPath      Chemin vers l'avatar
     * @return
     */
    boolean onUpdateProfileRequested(String updatedName, String currentPassword, String newPassword, String avatarPath);

    /**
     * Action de sélection d'un avatar
     * @return Le chemin vers l'avatar sélectionné, ou null si annulé
     */
    String onAvatarSelectionRequested();

    /**
     * Action de retour à la vue principale
     */
    void onReturnToMainViewRequested();
}