package main.java.com.ubo.tp.message.core.database;

import main.java.com.ubo.tp.message.core.event.EventManager;
import main.java.com.ubo.tp.message.core.event.MessageEvents;
import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Classe d'observation de la base de données pour les notifications de messages
 */
public class MessageNotificationObserver implements IDatabaseObserver {

    /**
     * Session de l'application
     */
    protected ISession mSession;

    /**
     * Constructeur
     * @param session Session de l'application
     */
    public MessageNotificationObserver(ISession session) {
        this.mSession = session;
    }

    @Override
    public void notifyMessageAdded(Message addedMessage) {
        // Récupération de l'utilisateur connecté
        User connectedUser = mSession.getConnectedUser();

        // Si aucun utilisateur n'est connecté, ne rien faire
        if (connectedUser == null) {
            return;
        }

        // Récupération de l'émetteur du message
        User sender = addedMessage.getSender();

        // Vérification si l'utilisateur connecté suit l'émetteur du message
        if (connectedUser.isFollowing(sender)) {
            // Émission d'un événement de notification
            EventManager.getInstance().fireEvent(
                    new MessageEvents.FollowedUserMessageEvent(addedMessage)
            );
        }
    }

    @Override
    public void notifyMessageDeleted(Message deletedMessage) {
        // Rien à faire pour les suppressions de messages
    }

    @Override
    public void notifyMessageModified(Message modifiedMessage) {
        // Rien à faire pour les modifications de messages
    }

    @Override
    public void notifyUserAdded(User addedUser) {
        // Rien à faire pour les ajouts d'utilisateurs
    }

    @Override
    public void notifyUserDeleted(User deletedUser) {
        // Rien à faire pour les suppressions d'utilisateurs
    }

    @Override
    public void notifyUserModified(User modifiedUser) {
        // Rien à faire pour les modifications d'utilisateurs
    }
}