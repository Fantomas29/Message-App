package main.java.com.ubo.tp.message.ihm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import main.java.com.ubo.tp.message.core.database.IDatabaseObserver;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.component.IView;

/**
 * Classe de la vue principale de l'application.
 */
public class MessageAppMainView extends JPanel implements IDatabaseObserver, IView {

    private static final long serialVersionUID = 1L;

    /**
     * Zone de texte pour afficher les événements de la base de données
     */
    protected JTextArea mEventsTextArea;

    /**
     * Format de date pour les logs
     */
    protected SimpleDateFormat mDateFormat;

    /**
     * Constructeur.
     */
    public MessageAppMainView() {
        // Initialisation du format de date
        this.mDateFormat = new SimpleDateFormat("HH:mm:ss");

        // Initialisation des composants graphiques
        this.initComponents();
    }

    /**
     * Initialisation des composants graphiques de la vue
     */
    protected void initComponents() {
        // Configuration du layout
        this.setLayout(new BorderLayout());
        this.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Création de la zone de texte pour les événements
        this.mEventsTextArea = new JTextArea();
        this.mEventsTextArea.setEditable(false);
        this.mEventsTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        this.mEventsTextArea.setBackground(Color.BLACK);
        this.mEventsTextArea.setForeground(Color.GREEN);

        // Ajout d'un message de bienvenue
        this.appendToEventLog("Application démarrée");
        this.appendToEventLog("En attente d'événements de la base de données...");

        // Création du scroll pane pour la zone de texte
        JScrollPane scrollPane = new JScrollPane(this.mEventsTextArea);

        // Ajout du scroll pane au panel principal
        this.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Ajoute un message formaté au log d'événements
     * @param message
     */
    public void appendToEventLog(String message) {
        String timestamp = this.mDateFormat.format(new Date());
        this.mEventsTextArea.append("[" + timestamp + "] " + message + "\n");

        // Défilement automatique vers le bas
        this.mEventsTextArea.setCaretPosition(this.mEventsTextArea.getDocument().getLength());
    }

    @Override
    public void notifyMessageAdded(Message addedMessage) {
        this.appendToEventLog("MESSAGE AJOUTÉ : @" + addedMessage.getSender().getUserTag()
                + " - " + addedMessage.getText());
    }

    @Override
    public void notifyMessageDeleted(Message deletedMessage) {
        this.appendToEventLog("MESSAGE SUPPRIMÉ : @" + deletedMessage.getSender().getUserTag()
                + " - " + deletedMessage.getText());
    }

    @Override
    public void notifyMessageModified(Message modifiedMessage) {
        this.appendToEventLog("MESSAGE MODIFIÉ : @" + modifiedMessage.getSender().getUserTag()
                + " - " + modifiedMessage.getText());
    }

    @Override
    public void notifyUserAdded(User addedUser) {
        this.appendToEventLog("UTILISATEUR AJOUTÉ : @" + addedUser.getUserTag()
                + " - " + addedUser.getName());
    }

    @Override
    public void notifyUserDeleted(User deletedUser) {
        this.appendToEventLog("UTILISATEUR SUPPRIMÉ : @" + deletedUser.getUserTag()
                + " - " + deletedUser.getName());
    }

    @Override
    public void notifyUserModified(User modifiedUser) {
        this.appendToEventLog("UTILISATEUR MODIFIÉ : @" + modifiedUser.getUserTag()
                + " - " + modifiedUser.getName());
    }

    @Override
    public JPanel getComponent() {
        return this;
    }
}