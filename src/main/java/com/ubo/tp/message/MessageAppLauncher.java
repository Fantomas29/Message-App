package main.java.com.ubo.tp.message;

import main.java.com.ubo.tp.message.common.Constants;
import main.java.com.ubo.tp.message.common.PropertiesManager;
import main.java.com.ubo.tp.message.core.EntityManager;
import main.java.com.ubo.tp.message.core.database.Database;
import main.java.com.ubo.tp.message.core.database.IDatabase;
import main.java.com.ubo.tp.message.ihm.MessageApp;
import main.mock.MessageAppMock;

import java.util.Properties;

/**
 * Classe de lancement de l'application.
 *
 * @author S.Lucas
 */
public class MessageAppLauncher {

	/**
	 * Indique si le mode bouchoné est activé.
	 */
	protected static boolean IS_MOCK_ENABLED = false;

	/**
	 * Launcher.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		// Chargement des propriétés
		Properties properties = PropertiesManager.loadProperties(Constants.CONFIGURATION_FILE);

		// Vérification du mode bouchoné
		String mockEnabledValue = properties.getProperty(Constants.CONFIGURATION_KEY_MOCK_ENABLED, "false");
		IS_MOCK_ENABLED = Boolean.parseBoolean(mockEnabledValue);

		// Création de la base de données
		IDatabase database = new Database();

		// Création du gestionnaire d'entités
		EntityManager entityManager = new EntityManager(database);

		// Activation du mode bouchoné si nécessaire
		if (IS_MOCK_ENABLED) {
			MessageAppMock mock = new MessageAppMock(database, entityManager);
			mock.showGUI();
		}

		// Création et initialisation de l'application
		MessageApp messageApp = new MessageApp(database, entityManager);
		messageApp.init();
		messageApp.show();
	}
}