package main.java.com.ubo.tp.message.ihm;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import main.java.com.ubo.tp.message.core.event.EventManager;
import main.java.com.ubo.tp.message.core.event.IEventListener;
import main.java.com.ubo.tp.message.core.event.MessageEvents;
import main.java.com.ubo.tp.message.datamodel.Message;

/**
 * Gestionnaire des notifications et messages non lus
 */
public class MessageNotificationManager {
    /**
     * Ensemble des IDs de messages non lus
     */
    private Set<UUID> unreadMessages;

    /**
     * Instance singleton
     */
    private static MessageNotificationManager instance;

    /**
     * Écouteurs pour les changements de statut des messages non lus
     */
    private Set<IUnreadMessagesListener> listeners;

    /**
     * Interface pour les écouteurs de changements de statut des messages non lus
     */
    public interface IUnreadMessagesListener {
        void onUnreadMessagesChanged(int count);
    }

    /**
     * Constructeur privé (pattern Singleton)
     */
    private MessageNotificationManager() {
        this.unreadMessages = new HashSet<>();
        this.listeners = new HashSet<>();

        // Enregistrer l'écouteur pour les nouveaux messages d'utilisateurs suivis
        EventManager.getInstance().addListener(
                MessageEvents.FollowedUserMessageEvent.class,
                new IEventListener<MessageEvents.FollowedUserMessageEvent>() {
                    @Override
                    public void onEvent(MessageEvents.FollowedUserMessageEvent event) {
                        addUnreadMessage(event.getMessage());
                    }
                }
        );
    }

    /**
     * Obtenir l'instance unique du gestionnaire
     * @return l'instance du gestionnaire
     */
    public static synchronized MessageNotificationManager getInstance() {
        if (instance == null) {
            instance = new MessageNotificationManager();
        }
        return instance;
    }

    /**
     * Ajouter un message non lu
     * @param message le message marqué comme non lu
     */
    public void addUnreadMessage(Message message) {
        unreadMessages.add(message.getUuid());
        notifyListeners();
    }

    /**
     * Marquer tous les messages comme lus
     */
    public void markAllAsRead() {
        unreadMessages.clear();
        notifyListeners();
    }

    /**
     * Obtenir le nombre de messages non lus
     * @return le nombre de messages non lus
     */
    public int getUnreadCount() {
        return unreadMessages.size();
    }

    /**
     * Ajouter un écouteur pour les changements de statut des messages non lus
     * @param listener l'écouteur à ajouter
     */
    public void addListener(IUnreadMessagesListener listener) {
        listeners.add(listener);
        // Notifier immédiatement du statut actuel
        listener.onUnreadMessagesChanged(getUnreadCount());
    }

    /**
     * Supprimer un écouteur
     * @param listener l'écouteur à supprimer
     */
    public void removeListener(IUnreadMessagesListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notifier tous les écouteurs d'un changement de statut
     */
    private void notifyListeners() {
        int count = getUnreadCount();
        for (IUnreadMessagesListener listener : listeners) {
            listener.onUnreadMessagesChanged(count);
        }
    }
}