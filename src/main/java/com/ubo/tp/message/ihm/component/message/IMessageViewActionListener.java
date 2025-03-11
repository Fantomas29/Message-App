package main.java.com.ubo.tp.message.ihm.component.message;

/**
 * Interface de callback pour les actions de la vue des messages
 */
public interface IMessageViewActionListener {
    /**
     * Action d'envoi d'un message
     * @param messageText Le texte du message à envoyer
     */
    void onSendMessageRequested(String messageText);

    /**
     * Action de recherche de messages
     * @param searchText Le texte de recherche
     */
    void onSearchMessagesRequested(String searchText);

    /**
     * Action de rafraîchissement de la liste des messages
     */
    void onRefreshMessagesRequested();
}