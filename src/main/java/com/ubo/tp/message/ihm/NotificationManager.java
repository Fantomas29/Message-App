package main.java.com.ubo.tp.message.ihm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import main.java.com.ubo.tp.message.core.event.EventManager;
import main.java.com.ubo.tp.message.core.event.MessageEvents;
import main.java.com.ubo.tp.message.datamodel.Message;

/**
 * Gestionnaire unifié des notifications et des messages non lus.
 * Cette classe combine les fonctionnalités de l'ancien MessageNotificationManager
 * et NotificationManager en une seule classe.
 */
public class NotificationManager {
    // Singleton instance
    private static NotificationManager instance;

    // Fenêtre principale de l'application
    private JFrame mainFrame;

    // Ensemble des IDs de messages non lus
    private Set<UUID> unreadMessages;

    // Écouteurs pour les changements de statut des messages non lus
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
    private NotificationManager() {
        this.unreadMessages = new HashSet<>();
        this.listeners = new HashSet<>();
    }

    /**
     * Initialise la fenêtre principale pour l'affichage des notifications
     * @param mainFrame la fenêtre principale de l'application
     */
    public void setMainFrame(JFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    /**
     * Obtenir l'instance unique du gestionnaire
     * @return l'instance du gestionnaire
     */
    public static synchronized NotificationManager getInstance() {
        if (instance == null) {
            instance = new NotificationManager();
        }
        return instance;
    }

    /**
     * Initialise les écouteurs d'événements pour les notifications
     */
    public void init() {
        // Enregistrement de l'écouteur d'événements de notification
        EventManager.getInstance().addListener(
                MessageEvents.FollowedUserMessageEvent.class,
                event -> handleNewMessage(event.getMessage())
        );
    }

    /**
     * Gère un nouveau message d'un utilisateur suivi
     * @param message le message reçu
     */
    private void handleNewMessage(Message message) {
        // Ajouter aux messages non lus
        addUnreadMessage(message);

        // Afficher une notification visuelle
        displayNotification(message);
    }

    /**
     * Ajoute un message non lu
     * @param message le message marqué comme non lu
     */
    public void addUnreadMessage(Message message) {
        unreadMessages.add(message.getUuid());
        notifyListeners();
    }

    /**
     * Marque tous les messages comme lus
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

    /**
     * Affiche une notification pour un message
     * @param message Message pour lequel afficher une notification
     */
    protected void displayNotification(final Message message) {
        // Vérifier que la fenêtre principale est configurée
        if (mainFrame == null) {
            return;
        }

        // Exécution dans le thread d'interface utilisateur
        SwingUtilities.invokeLater(() -> {
            // Création d'une boîte de dialogue non modale
            final JDialog notificationDialog = new JDialog(mainFrame, "Nouveau message", false);

            // Création du panel de contenu
            JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
            contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
            contentPanel.setBackground(new Color(240, 248, 255)); // Bleu très clair

            // Titre de la notification
            JLabel titleLabel = new JLabel("@" + message.getSender().getUserTag() + " a publié un message");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 14));

            // Contenu du message
            JLabel messageLabel = new JLabel("<html><body width='250px'>" + message.getText() + "</body></html>");
            messageLabel.setFont(new Font("Arial", Font.PLAIN, 12));

            // Ajout des composants au panel
            contentPanel.add(titleLabel, BorderLayout.NORTH);
            contentPanel.add(messageLabel, BorderLayout.CENTER);

            // Configuration de la boîte de dialogue
            notificationDialog.setContentPane(contentPanel);
            notificationDialog.setPreferredSize(new Dimension(300, 150));
            notificationDialog.pack();

            // Positionnement en bas à droite de l'écran
            int x = mainFrame.getX() + mainFrame.getWidth() - notificationDialog.getWidth() - 20;
            int y = mainFrame.getY() + mainFrame.getHeight() - notificationDialog.getHeight() - 20;
            notificationDialog.setLocation(x, y);

            // Affichage de la notification
            notificationDialog.setVisible(true);

            // Timer pour fermer automatiquement la notification après 2 secondes
            Timer timer = new Timer(1500, e -> notificationDialog.dispose());
            timer.setRepeats(false);
            timer.start();
        });
    }
}