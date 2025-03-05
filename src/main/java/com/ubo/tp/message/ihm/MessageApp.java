package main.java.com.ubo.tp.message.ihm;

import java.io.File;
import java.util.Properties;

import main.java.com.ubo.tp.message.common.Constants;
import main.java.com.ubo.tp.message.common.PropertiesManager;
import main.java.com.ubo.tp.message.core.EntityManager;
import main.java.com.ubo.tp.message.core.database.IDatabase;
import main.java.com.ubo.tp.message.core.directory.IWatchableDirectory;
import main.java.com.ubo.tp.message.core.directory.WatchableDirectory;
import main.java.com.ubo.tp.message.core.event.EventManager;
import main.java.com.ubo.tp.message.core.event.IEventListener;
import main.java.com.ubo.tp.message.core.event.NavigationEvents;
import main.java.com.ubo.tp.message.core.event.SessionEvents;
import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.component.login.ILoginController;
import main.java.com.ubo.tp.message.ihm.component.login.ILoginView;
import main.java.com.ubo.tp.message.ihm.component.login.LoginController;
import main.java.com.ubo.tp.message.ihm.component.login.LoginView;
import main.java.com.ubo.tp.message.ihm.component.message.IMessageController;
import main.java.com.ubo.tp.message.ihm.component.message.IMessageView;
import main.java.com.ubo.tp.message.ihm.component.message.MessageController;
import main.java.com.ubo.tp.message.ihm.component.message.MessageView;
import main.java.com.ubo.tp.message.ihm.component.profile.IProfileController;
import main.java.com.ubo.tp.message.ihm.component.profile.IProfileView;
import main.java.com.ubo.tp.message.ihm.component.profile.ProfileController;
import main.java.com.ubo.tp.message.ihm.component.profile.ProfileView;
import main.java.com.ubo.tp.message.ihm.component.userlist.IUserListController;
import main.java.com.ubo.tp.message.ihm.component.userlist.IUserListView;
import main.java.com.ubo.tp.message.ihm.component.userlist.UserListController;
import main.java.com.ubo.tp.message.ihm.component.userlist.UserListView;

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
		this.mLoginView = new LoginView(null);
		this.mLoginController = new LoginController(mDatabase, mEntityManager, mSession, mLoginView);
		((LoginView) mLoginView).mController = mLoginController;

		// Initialisation du composant de profil
		this.mProfileView = new ProfileView(null);
		this.mProfileController = new ProfileController(mDatabase, mEntityManager, mSession, mProfileView);
		((ProfileView) mProfileView).mController = mProfileController;

		// Initialisation du composant de liste des utilisateurs
		this.mUserListView = new UserListView(null);
		this.mUserListController = new UserListController(mDatabase, mEntityManager, mSession, mUserListView);
		((UserListView) mUserListView).mController = mUserListController;

		// Initialisation du composant de messages
		this.mMessageView = new MessageView(null);
		this.mMessageController = new MessageController(mDatabase, mEntityManager, mSession, mMessageView);
		((MessageView) mMessageView).mController = mMessageController;
	}

	/**
	 * Enregistre les écouteurs d'événements de l'application
	 */
	protected void registerEventListeners() {
		EventManager eventManager = EventManager.getInstance();

		// Écoute des événements de session
		eventManager.addListener(SessionEvents.UserLoggedInEvent.class, new IEventListener<SessionEvents.UserLoggedInEvent>() {
			@Override
			public void onEvent(SessionEvents.UserLoggedInEvent event) {
				// Affichage de la vue principale
				mIHM.showMainView();
				// Log de l'événement de connexion
				User connectedUser = event.getUser();
				if (connectedUser != null) {
					System.out.println("Utilisateur connecté : @" + connectedUser.getUserTag());
				}
			}
		});

		eventManager.addListener(SessionEvents.UserLoggedOutEvent.class, new IEventListener<SessionEvents.UserLoggedOutEvent>() {
			@Override
			public void onEvent(SessionEvents.UserLoggedOutEvent event) {
				// Affichage de la vue de login
				mIHM.showLoginView();
				// Log de l'événement de déconnexion
				System.out.println("Utilisateur déconnecté");
			}
		});

		// Écoute des événements de navigation
		eventManager.addListener(NavigationEvents.ShowLoginViewEvent.class, new IEventListener<NavigationEvents.ShowLoginViewEvent>() {
			@Override
			public void onEvent(NavigationEvents.ShowLoginViewEvent event) {
				mIHM.showLoginView();
			}
		});

		eventManager.addListener(NavigationEvents.ShowMainViewEvent.class, new IEventListener<NavigationEvents.ShowMainViewEvent>() {
			@Override
			public void onEvent(NavigationEvents.ShowMainViewEvent event) {
				mIHM.showMainView();
			}
		});

		eventManager.addListener(NavigationEvents.ShowProfileViewEvent.class, new IEventListener<NavigationEvents.ShowProfileViewEvent>() {
			@Override
			public void onEvent(NavigationEvents.ShowProfileViewEvent event) {
				// Mise à jour de la vue de profil avec l'utilisateur connecté
				((ProfileView) mProfileView).updateUserInfo(mSession.getConnectedUser());
				// Affichage de la vue de profil
				mIHM.showProfileView(mProfileView.getComponent());
			}
		});

		eventManager.addListener(NavigationEvents.ShowUserListViewEvent.class, new IEventListener<NavigationEvents.ShowUserListViewEvent>() {
			@Override
			public void onEvent(NavigationEvents.ShowUserListViewEvent event) {
				// Affichage de la vue de liste des utilisateurs
				mIHM.showUserListView(mUserListView.getComponent());
			}
		});

		eventManager.addListener(NavigationEvents.ShowMessageViewEvent.class, new IEventListener<NavigationEvents.ShowMessageViewEvent>() {
			@Override
			public void onEvent(NavigationEvents.ShowMessageViewEvent event) {
				// Affichage de la vue des messages
				mIHM.showMessageView(mMessageView.getComponent());
			}
		});
	}

	/**
	 * Initialisation de l'application.
	 */
	public void init() {
		// Chargement de la configuration si elle existe
		this.loadConfiguration();

		// Initialisation de l'IHM
		this.mIHM.init();
	}

	/**
	 * Charge la configuration de l'application
	 */
	protected void loadConfiguration() {
		// Chargement du répertoire d'échange depuis la configuration si disponible
		String configuredDirectory = this.getConfiguredDirectoryPath();
		if (configuredDirectory != null && !configuredDirectory.isEmpty()) {
			File directory = new File(configuredDirectory);
			if (directory.exists() && directory.isDirectory()) {
				this.initDirectory(configuredDirectory);
			}
		}
	}

	/**
	 * Quitte l'application proprement.
	 */
	public void exitApplication() {
		// Arrêt de la surveillance du répertoire si nécessaire
		if (mWatchableDirectory != null) {
			mWatchableDirectory.stopWatching();
		}

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
		if (directoryPath == null || directoryPath.isEmpty()) {
			return false;
		}

		mExchangeDirectoryPath = directoryPath;

		// Arrêt de la surveillance si elle était déjà active
		if (mWatchableDirectory != null) {
			mWatchableDirectory.stopWatching();
		}

		mWatchableDirectory = new WatchableDirectory(directoryPath);
		mEntityManager.setExchangeDirectory(directoryPath);

		mWatchableDirectory.initWatching();
		mWatchableDirectory.addObserver(mEntityManager);

		System.out.println("Répertoire d'échange initialisé : " + directoryPath);
		return true;
	}

	/**
	 * Sauvegarde le chemin du répertoire d'échange dans la configuration.
	 *
	 * @param directoryPath Chemin du répertoire à sauvegarder
	 */
	public void saveDirectoryInConfiguration(String directoryPath) {
		Properties properties = PropertiesManager.loadProperties(Constants.CONFIGURATION_FILE);
		properties.setProperty(Constants.CONFIGURATION_KEY_EXCHANGE_DIRECTORY, directoryPath);
		PropertiesManager.writeProperties(properties, Constants.CONFIGURATION_FILE);
	}

	/**
	 * Récupère le chemin du répertoire d'échange depuis la configuration.
	 *
	 * @return Le chemin du répertoire d'échange ou null s'il n'est pas configuré
	 */
	public String getConfiguredDirectoryPath() {
		Properties properties = PropertiesManager.loadProperties(Constants.CONFIGURATION_FILE);
		return properties.getProperty(Constants.CONFIGURATION_KEY_EXCHANGE_DIRECTORY);
	}

	/**
	 * Vérifie si le répertoire donné est valide pour servir de répertoire d'échange.
	 *
	 * @param directory Répertoire à vérifier
	 * @return true si le répertoire est valide, false sinon
	 */
	public boolean isValidExchangeDirectory(File directory) {
		return directory != null && directory.exists() && directory.isDirectory()
				&& directory.canRead() && directory.canWrite();
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
	 * Obtient le gestionnaire d'entités de l'application.
	 *
	 * @return Le gestionnaire d'entités
	 */
	public EntityManager getEntityManager() {
		return mEntityManager;
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
	 * Obtient l'interface utilisateur de l'application.
	 *
	 * @return L'interface utilisateur
	 */
	public MessageAppIHM getIHM() {
		return mIHM;
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
	 * Obtient le contrôleur de profil
	 *
	 * @return Le contrôleur de profil
	 */
	public IProfileController getProfileController() {
		return mProfileController;
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
	 * Obtient le contrôleur de liste des utilisateurs
	 *
	 * @return Le contrôleur de liste des utilisateurs
	 */
	public IUserListController getUserListController() {
		return mUserListController;
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
	 * Obtient le contrôleur des messages
	 *
	 * @return Le contrôleur des messages
	 */
	public IMessageController getMessageController() {
		return mMessageController;
	}

	/**
	 * Affiche l'application.
	 */
	public void show() {
		// Affichage de l'IHM
		this.mIHM.show();
	}

	/**
	 * Affiche le profil de l'utilisateur connecté
	 */
	public void showProfile() {
		// Vérifie qu'un utilisateur est connecté
		if (mSession.getConnectedUser() != null) {
			// Émission d'un événement pour demander l'affichage de la vue de profil
			EventManager.getInstance().fireEvent(new NavigationEvents.ShowProfileViewEvent());
		}
	}

	/**
	 * Affiche la liste des utilisateurs enregistrés
	 */
	public void showUserList() {
		// Vérifie qu'un utilisateur est connecté
		if (mSession.getConnectedUser() != null) {
			// Initialisation de la liste des utilisateurs
			mUserListController.refreshUserList();
			// Émission d'un événement pour demander l'affichage de la vue des utilisateurs
			EventManager.getInstance().fireEvent(new NavigationEvents.ShowUserListViewEvent());
		}
	}

	/**
	 * Affiche la vue des messages
	 */
	public void showMessages() {
		// Vérifie qu'un utilisateur est connecté
		if (mSession.getConnectedUser() != null) {
			// Initialisation de la liste des messages
			mMessageController.refreshMessages();
			// Émission d'un événement pour demander l'affichage de la vue des messages
			EventManager.getInstance().fireEvent(new NavigationEvents.ShowMessageViewEvent());
		}
	}
}