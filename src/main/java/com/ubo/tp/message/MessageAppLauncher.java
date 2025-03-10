package main.java.com.ubo.tp.message;

import main.java.com.ubo.tp.message.common.Constants;
import main.java.com.ubo.tp.message.common.PropertiesManager;
import main.java.com.ubo.tp.message.core.EntityManager;
import main.java.com.ubo.tp.message.core.database.Database;
import main.java.com.ubo.tp.message.core.database.IDatabase;
import main.java.com.ubo.tp.message.ihm.MessageApp;
import main.mock.MessageAppMock;

import javax.swing.*;
import java.awt.*;
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

		// Intercepter les exceptions non gérées spécifiques aux polices TrueType
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				// Si c'est l'exception spécifique de TrueTypeFont, l'ignorer silencieusement
				if (e instanceof NullPointerException &&
						e.getStackTrace().length > 0 &&
						e.getStackTrace()[0].toString().contains("TrueTypeFont.open")) {
					// Ne rien faire - ignorer cette exception spécifique
					return;
				}

				// Pour toutes les autres exceptions, comportement normal d'affichage
				System.err.println("Exception non gérée dans le thread " + t.getName() + ": " + e.getMessage());
				e.printStackTrace();
			}
		});
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