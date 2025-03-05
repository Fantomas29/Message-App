package main.java.com.ubo.tp.message.ihm.component.message;

import main.java.com.ubo.tp.message.ihm.component.IController;

/**
 * Interface du contrôleur pour la gestion des messages
 */
public interface IMessageController extends IController {

    /**
     * Envoie un message
     *
     * @param messageText Le texte du message à envoyer
     * @return true si l'envoi a réussi, false sinon
     */
    boolean sendMessage(String messageText);

    /**
     * Rafraîchit la liste des messages
     */
    void refreshMessages();

    /**
     * Recherche des messages selon un critère
     *
     * @param searchText Le texte de recherche
     */
    void searchMessages(String searchText);
}