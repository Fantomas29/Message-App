package main.java.com.ubo.tp.message.ihm.component.message;

import java.util.Set;

import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.ihm.component.IViewWithMessages;

/**
 * Interface de la vue de gestion des messages
 */
public interface IMessageView extends IViewWithMessages {
    /**
     * Met à jour la liste des messages affichés
     *
     * @param messages Liste des messages à afficher
     */
    void updateMessageList(Set<Message> messages);

    /**
     * Réinitialise le champ de saisie de message
     */
    void resetMessageField();
}