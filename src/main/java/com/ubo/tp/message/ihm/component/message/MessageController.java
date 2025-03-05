package main.java.com.ubo.tp.message.ihm.component.message;

import java.util.HashSet;
import java.util.Set;

import main.java.com.ubo.tp.message.core.EntityManager;
import main.java.com.ubo.tp.message.core.database.IDatabase;
import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Contrôleur pour la gestion des messages
 */
public class MessageController implements IMessageController {

    /**
     * Référence vers la base de données
     */
    protected IDatabase mDatabase;

    /**
     * Référence vers le gestionnaire d'entités
     */
    protected EntityManager mEntityManager;

    /**
     * Référence vers la session
     */
    protected ISession mSession;

    /**
     * Vue des messages
     */
    protected IMessageView mView;

    /**
     * Constructeur.
     *
     * @param database Base de données
     * @param entityManager Gestionnaire d'entités
     * @param session Session
     * @param view Vue des messages
     */
    public MessageController(IDatabase database, EntityManager entityManager, ISession session, IMessageView view) {
        this.mDatabase = database;
        this.mEntityManager = entityManager;
        this.mSession = session;
        this.mView = view;
    }

    @Override
    public boolean sendMessage(String messageText) {
        // Vérification qu'un utilisateur est connecté
        User connectedUser = mSession.getConnectedUser();
        if (connectedUser == null) {
            mView.showError("Erreur", "Vous devez être connecté pour envoyer un message");
            return false;
        }

        // Vérification que le message n'est pas vide
        if (messageText == null || messageText.trim().isEmpty()) {
            mView.showError("Erreur", "Le message ne peut pas être vide");
            return false;
        }

        // Vérification de la longueur du message (SRS-MAP-MSG-002)
        if (messageText.length() > 200) {
            mView.showError("Erreur", "Le message ne doit pas dépasser 200 caractères");
            return false;
        }

        // Création du message
        Message newMessage = new Message(connectedUser, messageText);

        // Ajout du message à la base de données
        mDatabase.addMessage(newMessage);

        // Création du fichier de message
        mEntityManager.writeMessageFile(newMessage);

        // Notification à la vue
        mView.showInfo("Succès", "Message envoyé avec succès");
        mView.resetMessageField();

        // Mise à jour de la liste des messages
        refreshMessages();

        return true;
    }

    @Override
    public void refreshMessages() {
        // Récupération de tous les messages
        Set<Message> allMessages = mDatabase.getMessages();

        // Mise à jour de la vue
        mView.updateMessageList(allMessages);
    }

    @Override
    public void searchMessages(String searchText) {
        // Si la recherche est vide, afficher tous les messages
        if (searchText == null || searchText.trim().isEmpty()) {
            refreshMessages();
            return;
        }

        Set<Message> filteredMessages = new HashSet<>();
        Set<Message> allMessages = mDatabase.getMessages();

        // Si aucun symbole spécial n'est présent, rechercher selon les deux critères (union)
        if (!searchText.contains("@") && !searchText.contains("#")) {
            // Recherche par texte général - union des critères
            String searchLower = searchText.toLowerCase();

            // Parcourir tous les messages pour rechercher manuellement
            for (Message message : allMessages) {
                // Vérifier si le texte du message contient la recherche
                String messageText = message.getText().toLowerCase();
                if (messageText.contains(searchLower)) {
                    filteredMessages.add(message);
                }

                // Vérifier également dans les tags et les userTags
                for (String tag : message.getTags()) {
                    if (tag.toLowerCase().contains(searchLower)) {
                        filteredMessages.add(message);
                        break;
                    }
                }

                for (String userTag : message.getUserTags()) {
                    if (userTag.toLowerCase().contains(searchLower)) {
                        filteredMessages.add(message);
                        break;
                    }
                }

                // Vérifier aussi le nom de l'utilisateur émetteur
                if (message.getSender().getName().toLowerCase().contains(searchLower) ||
                        message.getSender().getUserTag().toLowerCase().contains(searchLower)) {
                    filteredMessages.add(message);
                }
            }
        } else {
            // Recherche spécifique selon le symbole
            if (searchText.contains("@")) {
                // Recherche par utilisateur
                String userTag = searchText.substring(searchText.indexOf("@") + 1).trim();
                // Si l'utilisateur a ajouté un espace après le @, on ne prend que le premier mot
                if (userTag.contains(" ")) {
                    userTag = userTag.split("\\s+")[0];
                }

                // Récupérer les messages qui citent cet utilisateur
                Set<Message> taggedMessages = mDatabase.getMessagesWithUserTag(userTag);
                filteredMessages.addAll(taggedMessages);

                // Également récupérer les messages émis par cet utilisateur (s'il existe)
                for (User user : mDatabase.getUsers()) {
                    if (user.getUserTag().equalsIgnoreCase(userTag)) {
                        filteredMessages.addAll(mDatabase.getUserMessages(user));
                        break;
                    }
                }
            }

            if (searchText.contains("#")) {
                // Recherche par tag
                String tag = searchText.substring(searchText.indexOf("#") + 1).trim();
                // Si l'utilisateur a ajouté un espace après le #, on ne prend que le premier mot
                if (tag.contains(" ")) {
                    tag = tag.split("\\s+")[0];
                }

                // Ajouter les messages avec ce tag
                filteredMessages.addAll(mDatabase.getMessagesWithTag(tag));
            }
        }

        // Mise à jour de la vue
        mView.updateMessageList(filteredMessages);
    }
}