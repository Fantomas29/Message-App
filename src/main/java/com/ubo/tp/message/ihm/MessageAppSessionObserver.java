package main.java.com.ubo.tp.message.ihm;

import main.java.com.ubo.tp.message.core.session.ISessionObserver;
import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Classe d'observation de la session pour l'application principale.
 */
public class MessageAppSessionObserver implements ISessionObserver {

    /**
     * Référence vers l'application principale
     */
    protected MessageAppIHM mMainView;

    /**
     * Constructeur.
     *
     * @param mainView La vue principale de l'application
     */
    public MessageAppSessionObserver(MessageAppIHM mainView) {
        this.mMainView = mainView;
    }

    @Override
    public void notifyLogin(User connectedUser) {
        // Notification à la vue principale qu'un utilisateur s'est connecté
        mMainView.onUserLogin(connectedUser);
    }

    @Override
    public void notifyLogout() {
        // Notification à la vue principale qu'un utilisateur s'est déconnecté
        mMainView.onUserLogout();
    }
}