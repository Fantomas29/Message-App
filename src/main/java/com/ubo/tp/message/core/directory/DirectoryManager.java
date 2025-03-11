package main.java.com.ubo.tp.message.core.directory;

import main.java.com.ubo.tp.message.common.Constants;
import main.java.com.ubo.tp.message.common.PropertiesManager;
import main.java.com.ubo.tp.message.core.EntityManager;

import java.io.File;
import java.util.Properties;

/**
 * Classe dédiée à la gestion du répertoire d'échange
 */
public class DirectoryManager {

    /**
     * Référence au gestionnaire d'entités
     */
    private EntityManager mEntityManager;

    /**
     * Répertoire d'échange de l'application
     */
    private String mExchangeDirectoryPath;

    /**
     * Classe de surveillance de répertoire
     */
    private IWatchableDirectory mWatchableDirectory;

    /**
     * Constructeur
     * @param entityManager Gestionnaire d'entités
     */
    public DirectoryManager(EntityManager entityManager) {
        this.mEntityManager = entityManager;
    }

    /**
     * Charge la configuration depuis le fichier
     * @return Chemin du répertoire configuré, ou null si non défini
     */
    public String loadConfiguredDirectory() {
        String configuredDirectory = getConfiguredDirectoryPath();
        if (configuredDirectory != null && !configuredDirectory.isEmpty()) {
            File directory = new File(configuredDirectory);
            if (directory.exists() && directory.isDirectory()) {
                if (initDirectory(configuredDirectory)) {
                    return configuredDirectory;
                }
            }
        }
        return null;
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
     * Arrête la surveillance du répertoire
     */
    public void stopWatching() {
        if (mWatchableDirectory != null) {
            mWatchableDirectory.stopWatching();
        }
    }

    /**
     * Obtient le chemin du répertoire d'échange actuel
     * @return Le chemin du répertoire d'échange
     */
    public String getExchangeDirectoryPath() {
        return mExchangeDirectoryPath;
    }
}