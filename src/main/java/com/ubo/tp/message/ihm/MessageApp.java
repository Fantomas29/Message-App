package main.java.com.ubo.tp.message.ihm;

import java.io.File;

import main.java.com.ubo.tp.message.core.EntityManager;
import main.java.com.ubo.tp.message.core.database.IDatabase;
import main.java.com.ubo.tp.message.core.directory.IWatchableDirectory;
import main.java.com.ubo.tp.message.core.event.EventManager;
import main.java.com.ubo.tp.message.core.event.NavigationEvents;
import main.java.com.ubo.tp.message.core.event.SessionEvents;
import main.java.com.ubo.tp.message.core.navigation.NavigationService;
import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.component.home.HomeController;
import main.java.com.ubo.tp.message.ihm.component.login.*;
import main.java.com.ubo.tp.message.ihm.component.message.*;
import main.java.com.ubo.tp.message.ihm.component.profile.*;
import main.java.com.ubo.tp.message.ihm.component.userlist.*;
import main.java.com.ubo.tp.message.core.database.MessageNotificationObserver;
import main.java.com.ubo.tp.message.core.directory.DirectoryManager;


/**
 * Classe principale technique de l'application.
 * Coordonne les différents composants et gère le cycle de vie de l'application.
 */
public class MessageApp {
	/**
	 * Base de données.
	 */
	protected IDatabase mDatabase;

	/**
	 * Gestionnaire du répertoire d'échange
	 */
	protected DirectoryManager mDirectoryManager;

	/**
	 *
	 */
	protected HomeController mHomeController;

	/**
	 * Service de navigation
	 */
	protected NavigationService mNavigationService;

	/**
	 * Gestionnaire des entités contenu de la base de données.
	 */
	protected EntityManager mEntityManager;

	/**
	 * Session de l'application
	 */
	protected ISession mSession;

	/**
	 * Vue de login
	 */
	protected ILoginView mLoginView;

	/**
	 * Contrôleur de login
	 */
	protected ILoginController mLoginController;

	/**
	 * Vue de profil
	 */
	protected IProfileView mProfileView;

	/**
	 * Contrôleur de profil
	 */
	protected IProfileController mProfileController;

	/**
	 * Vue de la liste des utilisateurs
	 */
	protected IUserListView mUserListView;

	/**
	 * Contrôleur de la liste des utilisateurs
	 */
	protected IUserListController mUserListController;

	/**
	 * Vue des messages
	 */
	protected IMessageView mMessageView;

	/**
	 * Contrôleur des messages
	 */
	protected IMessageController mMessageController;

	/**
	 * Classe de surveillance de répertoire
	 */
	protected IWatchableDirectory mWatchableDirectory;

	/**
	 * Répertoire d'échange de l'application.
	 */
	protected String mExchangeDirectoryPath;

	/**
	 * Interface utilisateur de l'application.
	 */
	protected MessageAppIHM mIHM;

	/**
	 * Constructeur.
	 *
	 * @param database      Base de données de l'application
	 * @param entityManager Gestionnaire des entités
	 */
	public MessageApp(IDatabase database, EntityManager entityManager) {
		this.mDatabase = database;
		this.mEntityManager = entityManager;
		this.mDirectoryManager = new DirectoryManager(entityManager);

		// Création de la session
		this.mSession = new Session();

		// Création de l'IHM
		this.mIHM = new MessageAppIHM(this);

		// Initialisation des composants
		this.initComponents();

		// Enregistrement des écouteurs d'événements
		this.registerEventListeners();
	}

	/**
	 * Initialise les composants de l'application
	 */
	protected void initComponents() {
		// Initialisation du composant de login
		this.mLoginView = new LoginView();
		this.mLoginController = new LoginController(mDatabase, mEntityManager, mSession, mLoginView);
		((LoginView) mLoginView).setActionListener((ILoginViewActionListener) mLoginController);

		// Initialisation du composant de profil
		this.mProfileView = new ProfileView();
		this.mProfileController = new ProfileController(mDatabase, mEntityManager, mSession, mProfileView);
		((ProfileView) mProfileView).setActionListener((IProfileViewActionListener) mProfileController);

		// Initialisation du composant de liste des utilisateurs
		this.mUserListView = new UserListView();
		this.mUserListController = new UserListController(mDatabase, mEntityManager, mSession, mUserListView);
		((UserListView) mUserListView).setActionListener((IUserListViewActionListener) mUserListController);

		// Initialisation du composant de messages
		// on passe la session pour avoir accès à l'utilisateur connecté et avoir ses messages d'un couleur différente
		this.mMessageView = new MessageView();
		((MessageView) mMessageView).setSession(mSession);
		this.mMessageController = new MessageController(mDatabase, mEntityManager, mSession, mMessageView);
		((MessageView) mMessageView).setActionListener((IMessageViewActionListener) mMessageController);

		// Initialisation du service de navigation
		this.mNavigationService = new NavigationService(
				this.mIHM,
				this.mMessageController,
				this.mUserListController
		);
	}

	/**
	 * Enregistre les écouteurs d'événements de l'application
	 */
	protected void registerEventListeners() {
		EventManager eventManager = EventManager.getInstance();

		eventManager.addListener(SessionEvents.UserLoggedInEvent.class,
				event -> {
					// Seulement des actions de logging ou de mise à jour d'état
					User connectedUser = event.getUser();
					if (connectedUser != null) {
						System.out.println("Utilisateur connecté : @" + connectedUser.getUserTag());
					}

					// Déclencher un événement de navigation
					EventManager.getInstance().fireEvent(new NavigationEvents.ShowMainViewEvent());
				}
		);

		eventManager.addListener(SessionEvents.UserLoggedOutEvent.class,
				event -> {
					// Seulement des actions de logging ou de mise à jour d'état
					System.out.println("Utilisateur déconnecté");

					// Déclencher un événement de navigation
					EventManager.getInstance().fireEvent(new NavigationEvents.ShowLoginViewEvent());
				}
		);
	}

	/**
	 * Initialisation de l'application.
	 */
	public void init() {
		// Chargement de la configuration du répertoire si elle existe
		mDirectoryManager.loadConfiguredDirectory();

		// Création et ajout de l'observateur de notifications de messages
		MessageNotificationObserver notificationObserver = new MessageNotificationObserver(this.mSession);
		this.mDatabase.addObserver(notificationObserver);

		// Initialisation du contrôleur de la vue d'accueil
		this.mHomeController = new HomeController(this.mSession);

		// Initialisation de l'IHM
		this.mIHM.init();

		// Initialisation du gestionnaire de notifications
		NotificationManager.getInstance().init();
	}

	/**
	 * Quitte l'application proprement.
	 */
	public void exitApplication() {
		// Arrêt de la surveillance du répertoire
		mDirectoryManager.stopWatching();

		// Fermeture de l'application
		System.exit(0);
	}

	/**
	 * Initialise le répertoire d'échange avec le chemin fourni.
	 *
	 * @param directoryPath Chemin du répertoire d'échange
	 * @return true si l'initialisation a réussi, false sinon
	 */
	public boolean initDirectory(String directoryPath) {
		return mDirectoryManager.initDirectory(directoryPath);
	}

	/**
	 * Sauvegarde le chemin du répertoire d'échange dans la configuration.
	 *
	 * @param directoryPath Chemin du répertoire à sauvegarder
	 */
	public void saveDirectoryInConfiguration(String directoryPath) {
		mDirectoryManager.saveDirectoryInConfiguration(directoryPath);
	}

	/**
	 * Récupère le chemin du répertoire d'échange depuis la configuration.
	 *
	 * @return Le chemin du répertoire d'échange ou null s'il n'est pas configuré
	 */
	public String getConfiguredDirectoryPath() {
		return mDirectoryManager.getConfiguredDirectoryPath();
	}

	/**
	 * Vérifie si le répertoire donné est valide pour servir de répertoire d'échange.
	 *
	 * @param directory Répertoire à vérifier
	 * @return true si le répertoire est valide, false sinon
	 */
	public boolean isValidExchangeDirectory(File directory) {
		return mDirectoryManager.isValidExchangeDirectory(directory);
	}

	/**
	 * Obtient la base de données de l'application.
	 *
	 * @return La base de données
	 */
	public IDatabase getDatabase() {
		return mDatabase;
	}

	/**
	 * Obtient la session de l'application.
	 *
	 * @return La session
	 */
	public ISession getSession() {
		return mSession;
	}

	/**
	 * Obtient la vue de login
	 *
	 * @return La vue de login
	 */
	public ILoginView getLoginView() {
		return mLoginView;
	}

	/**
	 * Obtient le contrôleur de login
	 *
	 * @return Le contrôleur de login
	 */
	public ILoginController getLoginController() {
		return mLoginController;
	}

	/**
	 * Obtient la vue de profil
	 *
	 * @return La vue de profil
	 */
	public IProfileView getProfileView() {
		return mProfileView;
	}

	/**
	 * Obtient la vue de liste des utilisateurs
	 *
	 * @return La vue de liste des utilisateurs
	 */
	public IUserListView getUserListView() {
		return mUserListView;
	}

	/**
	 * Obtient la vue des messages
	 *
	 * @return La vue des messages
	 */
	public IMessageView getMessageView() {
		return mMessageView;
	}

	/**
	 * Affiche l'application.
	 */
	public void show() {
		// Affichage de l'IHM
		this.mIHM.show();
	}

	public HomeController getHomeController() {
		return this.mHomeController;
	}
}