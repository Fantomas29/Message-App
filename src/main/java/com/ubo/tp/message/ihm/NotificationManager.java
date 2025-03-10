package main.java.com.ubo.tp.message.ihm;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import main.java.com.ubo.tp.message.core.event.EventManager;
import main.java.com.ubo.tp.message.core.event.IEventListener;
import main.java.com.ubo.tp.message.core.event.MessageEvents;
import main.java.com.ubo.tp.message.datamodel.Message;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Gestionnaire de notifications pour les messages
 */
public class NotificationManager {

    /**
     * Fenêtre principale de l'application
     */
    protected JFrame mMainFrame;

    /**
     * Constructeur
     * @param mainFrame Fenêtre principale de l'application
     */
    public NotificationManager(JFrame mainFrame) {
        this.mMainFrame = mainFrame;

        // Enregistrement de l'écouteur d'événements de notification
        EventManager.getInstance().addListener(
                MessageEvents.FollowedUserMessageEvent.class,
                new IEventListener<MessageEvents.FollowedUserMessageEvent>() {
                    @Override
                    public void onEvent(MessageEvents.FollowedUserMessageEvent event) {
                        displayNotification(event.getMessage());
                    }
                }
        );
    }

    /**
     * Affiche une notification pour un message
     * @param message Message pour lequel afficher une notification
     */
    protected void displayNotification(final Message message) {
        // Exécution dans le thread d'interface utilisateur
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Création d'une boîte de dialogue non modale
                final JDialog notificationDialog = new JDialog(mMainFrame, "Nouveau message", false);

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
                int x = mMainFrame.getX() + mMainFrame.getWidth() - notificationDialog.getWidth() - 20;
                int y = mMainFrame.getY() + mMainFrame.getHeight() - notificationDialog.getHeight() - 20;
                notificationDialog.setLocation(x, y);

                // Affichage de la notification
                notificationDialog.setVisible(true);

                // Timer pour fermer automatiquement la notification après 5 secondes
                Timer timer = new Timer(5000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        notificationDialog.dispose();
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        });
    }
}