package main.java.com.ubo.tp.message.ihm.component.login;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import main.java.com.ubo.tp.message.core.EntityManager;
import main.java.com.ubo.tp.message.core.database.IDatabase;
import main.java.com.ubo.tp.message.core.event.EventManager;
import main.java.com.ubo.tp.message.core.event.SessionEvents;
import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Implémentation du contrôleur pour la gestion de l'authentification des utilisateurs
 */
public class LoginController implements ILoginController, ILoginViewActionListener {

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
     * Vue du composant
     */
    protected ILoginView mView;

    /**
     * Chemin vers l'avatar sélectionné
     */
    protected String mAvatarPath = "";

    /**
     * Constructeur
     *
     * @param database Base de données
     * @param entityManager Gestionnaire d'entités
     * @param session Session
     * @param view Vue
     */
    public LoginController(IDatabase database, EntityManager entityManager, ISession session, ILoginView view) {
        this.mDatabase = database;
        this.mEntityManager = entityManager;
        this.mSession = session;
        this.mView = view;
    }

    @Override
    public void loginUser(String userTag, String password) {
        if (userTag == null || userTag.isEmpty()) {
            mView.showError("Erreur de connexion", "Veuillez entrer votre tag utilisateur");
            return;
        }

        if (password == null || password.isEmpty()) {
            mView.showError("Erreur de connexion", "Veuillez entrer votre mot de passe");
            return;
        }

        // Récupération de tous les utilisateurs
        Set<User> users = mDatabase.getUsers();

        // Recherche de l'utilisateur avec le tag correspondant
        User foundUser = users.stream()
                .filter(user -> user.getUserTag().equals(userTag))
                .findFirst()
                .orElse(null);

        // Vérification de l'utilisateur et du mot de passe
        if (foundUser == null) {
            mView.showError("Erreur de connexion", "Utilisateur introuvable");
            return;
        }

        if (!foundUser.getUserPassword().equals(password)) {
            mView.showError("Erreur de connexion", "Mot de passe incorrect");
            return;
        }

        // Connexion de l'utilisateur à la session
        mSession.connect(foundUser);

        // Réinitialisation des champs
        mView.resetLoginFields();

        // Émission d'un événement de connexion
        EventManager.getInstance().fireEvent(new SessionEvents.UserLoggedInEvent(foundUser));

        // Affichage d'un message de bienvenue
        mView.showInfo("Connexion réussie", "Bienvenue " + foundUser.getName() + " !");
    }

    @Override
    public void signupUser(User newUser) {
        // Si l'utilisateur n'est pas fourni, nous le créons à partir des données de la vue
        if (newUser == null) {
            // Cette partie est normalement gérée par la vue LoginView
            return;
        }

        // Vérification des champs obligatoires selon SRS-MAP-USR-002
        if (newUser.getName() == null || newUser.getName().isEmpty()) {
            mView.showError("Erreur d'inscription", "Le nom est obligatoire");
            return;
        }

        if (newUser.getUserTag() == null || newUser.getUserTag().isEmpty()) {
            mView.showError("Erreur d'inscription", "Le tag utilisateur est obligatoire");
            return;
        }

        if (newUser.getUserPassword() == null || newUser.getUserPassword().isEmpty()) {
            mView.showError("Erreur d'inscription", "Le mot de passe est obligatoire");
            return;
        }

        // Vérification que le tag n'existe pas déjà (SRS-MAP-USR-003)
        Set<User> users = mDatabase.getUsers();
        for (User user : users) {
            if (user.getUserTag().equals(newUser.getUserTag())) {
                mView.showError("Erreur d'inscription", "Ce tag utilisateur existe déjà");
                return;
            }
        }

        // Ajout de l'utilisateur à la base de données
        mDatabase.addUser(newUser);

        // Création du fichier de l'utilisateur
        mEntityManager.writeUserFile(newUser);

        // Réinitialisation des champs
        mView.resetSignupFields();
        mAvatarPath = "";

        // Affichage d'un message de confirmation
        mView.showInfo("Inscription réussie", "Inscription réussie ! Vous pouvez maintenant vous connecter.");
    }

    @Override
    public void logoutUser() {
        // Déconnexion de l'utilisateur
        mSession.disconnect();

        // Émission d'un événement de déconnexion
        EventManager.getInstance().fireEvent(new SessionEvents.UserLoggedOutEvent());
    }

    @Override
    public String selectAvatar() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Sélectionner un avatar");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);

        // Filtre pour les images
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Images", "jpg", "jpeg", "png", "gif");
        fileChooser.addChoosableFileFilter(filter);

        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            mAvatarPath = selectedFile.getAbsolutePath();
            mView.updateAvatarPath(mAvatarPath);
            return mAvatarPath;
        }

        return null;
    }

    // Implémentation des méthodes de callback
    @Override
    public void onLoginRequested(String userTag, String password) {
        this.loginUser(userTag, password);
    }

    @Override
    public void onSignupRequested(String name, String tag, String password, String avatarPath) {
        // Création de l'utilisateur
        Set<String> follows = new HashSet<>();
        User newUser = new User(UUID.randomUUID(), tag, password, name, follows, avatarPath);

        // Enregistrement de l'utilisateur
        this.signupUser(newUser);
    }

    @Override
    public String onAvatarSelectionRequested() {
        return this.selectAvatar();
    }

    /**
     * Vérifie l'état de déconnexion et notifie la vue si nécessaire
     */
    public void validateLogoutState() {
        // Vérification que l'utilisateur est bien déconnecté (logique du contrôleur)
        boolean isLogoutValid = (mSession == null || mSession.getConnectedUser() == null);

        if (!isLogoutValid) {
            // Obtention des informations d'erreur
            String errorUserTag = mSession.getConnectedUser().getUserTag();
            System.err.println("ERREUR: Un utilisateur est toujours connecté alors que la vue de login est affichée!");
            System.err.println("Utilisateur connecté: @" + errorUserTag);

            // Délégation à la vue pour l'affichage du message
            mView.notifyLogoutError();
        } else {
            System.out.println("Validation de la déconnexion: OK - Aucun utilisateur connecté");
        }
    }
}