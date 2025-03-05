package main.java.com.ubo.tp.message.ihm.component.message;

import java.util.Set;

import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.ihm.component.IView;

/**
 * Interface de la vue de gestion des messages
 */
public interface IMessageView extends IView {

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

    /**
     * Affiche un message d'erreur
     *
     * @param title Titre du message
     * @param message Contenu du message
     */
    void showError(String title, String message);

    /**
     * Affiche un message d'information
     *
     * @param title Titre du message
     * @param message Contenu du message
     */
    void showInfo(String title, String message);
}