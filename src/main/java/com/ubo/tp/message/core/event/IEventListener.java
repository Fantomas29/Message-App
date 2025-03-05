package main.java.com.ubo.tp.message.core.event;

/**
 * Interface pour les listeners d'événements
 */
public interface IEventListener<T extends IEvent> {
    /**
     * Méthode appelée lorsqu'un événement est émis
     *
     * @param event Événement reçu
     */
    void onEvent(T event);
}
