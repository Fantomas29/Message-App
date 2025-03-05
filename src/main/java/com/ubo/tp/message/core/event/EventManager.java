package main.java.com.ubo.tp.message.core.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestionnaire d'événements centralisé permettant la communication entre composants.
 */
public class EventManager {

    // Instance unique (Singleton)
    private static EventManager instance;

    // Map associant chaque type d'événement à ses listeners
    private Map<Class<? extends IEvent>, List<IEventListener>> listeners;

    /**
     * Constructeur privé pour le Singleton
     */
    private EventManager() {
        listeners = new HashMap<>();
    }

    /**
     * Récupérer l'instance unique du gestionnaire d'événements
     */
    public static synchronized EventManager getInstance() {
        if (instance == null) {
            instance = new EventManager();
        }
        return instance;
    }

    /**
     * Enregistrer un listener pour un type d'événement spécifique
     *
     * @param eventClass Classe de l'événement
     * @param listener Listener à enregistrer
     */
    public <T extends IEvent> void addListener(Class<T> eventClass, IEventListener<T> listener) {
        List<IEventListener> eventListeners = listeners.computeIfAbsent(eventClass, k -> new ArrayList<>());
        eventListeners.add(listener);
    }

    /**
     * Supprimer un listener pour un type d'événement spécifique
     *
     * @param eventClass Classe de l'événement
     * @param listener Listener à supprimer
     */
    public <T extends IEvent> void removeListener(Class<T> eventClass, IEventListener<T> listener) {
        List<IEventListener> eventListeners = listeners.get(eventClass);
        if (eventListeners != null) {
            eventListeners.remove(listener);
        }
    }

    /**
     * Émettre un événement à tous les listeners concernés
     *
     * @param event Événement à émettre
     */
    public <T extends IEvent> void fireEvent(T event) {
        List<IEventListener> eventListeners = listeners.get(event.getClass());
        if (eventListeners != null) {
            for (IEventListener listener : eventListeners) {
                listener.onEvent(event);
            }
        }
    }
}

