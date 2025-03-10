package main.java.com.ubo.tp.message.core.event;

import main.java.com.ubo.tp.message.datamodel.Message;

/**
 * Événements liés aux notifications de messages
 */
public class MessageEvents {

    /**
     * Événement émis lorsqu'un message est créé par un utilisateur suivi
     */
    public static class FollowedUserMessageEvent implements IEvent {
        private final Message message;

        public FollowedUserMessageEvent(Message message) {
            this.message = message;
        }

        public Message getMessage() {
            return message;
        }
    }
}