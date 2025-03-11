package main.java.com.ubo.tp.message.ihm.component.profile;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import main.java.com.ubo.tp.message.core.EntityManager;
import main.java.com.ubo.tp.message.core.database.IDatabase;
import main.java.com.ubo.tp.message.core.event.EventManager;
import main.java.com.ubo.tp.message.core.event.NavigationEvents;
import main.java.com.ubo.tp.message.core.event.SessionEvents;
import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Contrôleur pour la gestion du profil utilisateur.
 */
public class ProfileController implements IProfileController, IProfileViewActionListener {

    /**
     * Référence vers la base de données
     */
    protected IDatabase mDatabase;

    /**
     * Référence vers le gestionnaire d'entités
     */
    protected EntityManager mEntityManager;

    /**
     * Référence vers la session
     */
    protected ISession mSession;

    /**
     * Vue de profil
     */
    protected IProfileView mView;

    /**
     * Chemin de l'avatar sélectionné
     */
    protected String mSelectedAvatarPath;

    /**
     * Constructeur.
     *
     * @param database Référence vers la base de données
     * @param entityManager Référence vers le gestionnaire d'entités
     * @param session Référence vers la session
     * @param view Vue de profil
     */
    public ProfileController(IDatabase database, EntityManager entityManager, ISession session, IProfileView view) {
        this.mDatabase = database;
        this.mEntityManager = entityManager;
        this.mSession = session;
        this.mView = view;
    }

    @Override
    public void showProfileView() {
        // Mise à jour des informations de l'utilisateur dans la vue
        User connectedUser = mSession.getConnectedUser();
        if (connectedUser != null) {
            mView.updateUserInfo(connectedUser);

            // Émission d'un événement pour demander l'affichage de la vue de profil
            EventManager.getInstance().fireEvent(new NavigationEvents.ShowProfileViewEvent());
        }
    }

    @Override
    public boolean updateProfile(String updatedName, String currentPassword, String newPassword, String avatarPath) {
        // Récupération de l'utilisateur connecté
        User connectedUser = mSession.getConnectedUser();

        if (connectedUser == null) {
            return false;
        }

        // Vérification du mot de passe actuel
        if (!connectedUser.getUserPassword().equals(currentPassword)) {
            mView.showError("Erreur", "Mot de passe incorrect");
            return false;
        }

        // Vérification que le nom n'est pas vide
        if (updatedName == null || updatedName.trim().isEmpty()) {
            mView.showError("Erreur", "Le nom ne peut pas être vide");
            return false;
        }

        // Mise à jour du nom
        connectedUser.setName(updatedName);

        // Mise à jour du mot de passe si fourni
        if (newPassword != null && !newPassword.isEmpty()) {
            connectedUser.setUserPassword(newPassword);
        }

        // Mise à jour de l'avatar si fourni
        if (avatarPath != null) {
            connectedUser.setAvatarPath(avatarPath);
        }

        // Mise à jour de l'utilisateur dans la base de données
        mDatabase.modifiyUser(connectedUser);

        // Écriture du fichier utilisateur
        mEntityManager.writeUserFile(connectedUser);

        // Émission d'un événement pour indiquer la mise à jour du profil
        EventManager.getInstance().fireEvent(new SessionEvents.UserProfileUpdatedEvent(connectedUser));

        // Affichage d'un message de confirmation
        mView.showInfo("Succès", "Profil mis à jour avec succès");

        return true;
    }

    @Override
    public String selectAvatar() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Sélectionner un avatar");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);

        // Filtre pour les images
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Images", "jpg", "jpeg", "png", "gif");
        fileChooser.addChoosableFileFilter(filter);

        // Si un avatar est déjà défini, positionner le sélecteur dans le même répertoire
        User connectedUser = mSession.getConnectedUser();
        if (connectedUser != null && connectedUser.getAvatarPath() != null && !connectedUser.getAvatarPath().isEmpty()) {
            File currentAvatar = new File(connectedUser.getAvatarPath());
            if (currentAvatar.exists()) {
                fileChooser.setCurrentDirectory(currentAvatar.getParentFile());
            }
        }

        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            mSelectedAvatarPath = selectedFile.getAbsolutePath();
            return mSelectedAvatarPath;
        }

        return null;
    }

    @Override
    public void returnToMainView() {
        // Émission d'un événement pour demander l'affichage de la vue principale
        EventManager.getInstance().fireEvent(new NavigationEvents.ShowMainViewEvent());
    }

    // Implémentation des méthodes de callback
    @Override
    public boolean onUpdateProfileRequested(String updatedName, String currentPassword, String newPassword, String avatarPath) {
        return this.updateProfile(updatedName, currentPassword, newPassword, avatarPath);
    }

    @Override
    public String onAvatarSelectionRequested() {
        return this.selectAvatar();
    }

    @Override
    public void onReturnToMainViewRequested() {
        this.returnToMainView();
    }
}