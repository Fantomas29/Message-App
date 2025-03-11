package main.java.com.ubo.tp.message.ihm.component.message;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import main.java.com.ubo.tp.message.core.event.EventManager;
import main.java.com.ubo.tp.message.core.event.NavigationEvents;
import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.NotificationManager;
import main.java.com.ubo.tp.message.ihm.component.AbstractComponent;
import main.java.com.ubo.tp.message.ihm.utils.AvatarUtils;


/**
 * Vue pour la gestion des messages
 */
public class MessageView extends AbstractComponent implements IMessageView {

    /**
     * Hauteur d'une vignette de message
     */
    protected static final int MESSAGE_PANEL_HEIGHT = 120;

    /**
     * Hauteur de l'avatar
     */
    protected static final int AVATAR_SIZE = 50;

    /**
     * Référence au contrôleur
     */
    private IMessageViewActionListener mActionListener;

    /**
     * Session de l'application
     */
    protected ISession mSession;


    /**
     * Panel principal
     */
    protected JPanel mMainPanel;

    /**
     * Panel contenant la liste des messages
     */
    protected JPanel mMessagesPanel;

    /**
     * Champ de recherche
     */
    protected JTextField mSearchField;

    /**
     * Champ de saisie de message
     */
    protected JTextArea mMessageField;

    /**
     * Label affichant le nombre de caractères restants
     */
    protected JLabel mCharCountLabel;

    /**
     * Bouton d'envoi de message
     */
    protected JButton mSendButton;

    /**
     * Format de date pour l'affichage des messages
     */
    protected SimpleDateFormat mDateFormat;

    /**
     * Liste des messages affichés
     */
    protected List<Message> mDisplayedMessages;

    /**
     * Constructeur
     */
    public MessageView() {
        this.mDisplayedMessages = new ArrayList<>();
        this.mDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        // Initialisation des composants graphiques
        this.initComponents();
    }

    /**
     * Définit la session de l'application
     *
     * @param session Session de l'application
     */
    public void setSession(ISession session) {
        this.mSession = session;
    }

    /**
     * Initialise les composants graphiques
     */
    protected void initComponents() {
        // Panel principal
        mMainPanel = new JPanel(new BorderLayout(0, 0));
        mMainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Titre
        JLabel titleLabel = new JLabel("Messages");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));

        // Bouton Page d'accueil
        JButton homeButton = new JButton("Page d'accueil");
        homeButton.setPreferredSize(new Dimension(150, 30));
        homeButton.setBackground(new Color(230, 230, 250));
        homeButton.setForeground(new Color(50, 50, 100));
        homeButton.setFont(new Font("Arial", Font.BOLD, 12));
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Émission d'un événement pour retourner à la page d'accueil
                EventManager.getInstance().fireEvent(new NavigationEvents.ShowMainViewEvent());
            }
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(homeButton, BorderLayout.EAST);

        // Panel pour la liste des messages
        mMessagesPanel = new JPanel();
        mMessagesPanel.setLayout(new BoxLayout(mMessagesPanel, BoxLayout.Y_AXIS));

        // Scroll pane pour la liste des messages
        JScrollPane scrollPane = new JScrollPane(mMessagesPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Panel de recherche
        JPanel searchPanel = createSearchPanel();

        // Panel de saisie de message
        JPanel messageInputPanel = createMessageInputPanel();

        // Layout principal
        JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(messageInputPanel, BorderLayout.SOUTH);

        // Ajout des composants au panel principal
        mMainPanel.add(topPanel, BorderLayout.NORTH);
        mMainPanel.add(contentPanel, BorderLayout.CENTER);
        mMainPanel.add(searchPanel, BorderLayout.SOUTH);
    }

    /**
     * Crée le panel de recherche
     */
    protected JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JLabel searchLabel = new JLabel("Rechercher : ");
        searchLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        mSearchField = new JTextField();
        mSearchField.setPreferredSize(new Dimension(200, 30));
        mSearchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (mActionListener != null) {
                    mActionListener.onSearchMessagesRequested(mSearchField.getText().trim());
                }
            }
        });

        JButton searchButton = new JButton("Rechercher");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mActionListener != null) {
                    mActionListener.onSearchMessagesRequested(mSearchField.getText().trim());
                }
            }
        });

        JButton resetButton = new JButton("Réinitialiser");
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mSearchField.setText("");
                if (mActionListener != null) {
                    mActionListener.onRefreshMessagesRequested();
                }
            }
        });

        searchPanel.add(searchLabel);
        searchPanel.add(mSearchField);
        searchPanel.add(searchButton);
        searchPanel.add(resetButton);

        return searchPanel;
    }

    /**
     * Crée le panel de saisie de message
     */
    protected JPanel createMessageInputPanel() {
        JPanel messageInputPanel = new JPanel(new BorderLayout(10, 0));
        messageInputPanel.setBorder(BorderFactory.createTitledBorder("Nouveau message"));

        // Champ de saisie de message
        mMessageField = new JTextArea();
        mMessageField.setLineWrap(true);
        mMessageField.setWrapStyleWord(true);
        mMessageField.setRows(3);
        mMessageField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        mMessageField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                updateCharacterCount();
            }
        });

        // Compteur de caractères
        mCharCountLabel = new JLabel("0/200");
        mCharCountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        mCharCountLabel.setFont(new Font("Arial", Font.ITALIC, 12));

        // Bouton d'envoi
        mSendButton = new JButton("Envoyer");
        mSendButton.setPreferredSize(new Dimension(100, 30));
        mSendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mActionListener != null) {
                    mActionListener.onSendMessageRequested(mMessageField.getText());
                }
            }
        });

        // Panel pour le bouton d'envoi et le compteur
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(mCharCountLabel, BorderLayout.WEST);
        buttonPanel.add(mSendButton, BorderLayout.EAST);
        buttonPanel.setBorder(new EmptyBorder(5, 0, 0, 0));

        // Scroll pane pour le champ de message
        JScrollPane scrollPane = new JScrollPane(mMessageField);

        // Assemblage du panel
        messageInputPanel.add(scrollPane, BorderLayout.CENTER);
        messageInputPanel.add(buttonPanel, BorderLayout.SOUTH);

        return messageInputPanel;
    }

    /**
     * Met à jour le compteur de caractères
     */
    protected void updateCharacterCount() {
        int count = mMessageField.getText().length();
        mCharCountLabel.setText(count + "/200");

        // Changer la couleur selon le nombre de caractères
        if (count > 200) {
            mCharCountLabel.setForeground(Color.RED);
            mSendButton.setEnabled(false);
        } else {
            mCharCountLabel.setForeground(Color.BLACK);
            mSendButton.setEnabled(true);
        }
    }

    /**
     * Crée un panel pour un message
     */
    protected JPanel createMessagePanel(Message message) {
        // Panel principal du message
        JPanel messagePanel = new JPanel(new GridBagLayout());

        // Récupération de l'utilisateur connecté
        User connectedUser = null;
        if (mSession != null) {
            connectedUser = mSession.getConnectedUser();
        }

        // Vérification si le message est de l'utilisateur connecté
        boolean isMyMessage = false;
        if (connectedUser != null && message.getSender() != null) {
            isMyMessage = connectedUser.getUuid().equals(message.getSender().getUuid());
        }

        // Couleur de fond différente pour les messages de l'utilisateur connecté
        if (isMyMessage) {
            // Couleur verte distincte pour vos propres messages
            messagePanel.setBackground(new Color(230, 255, 230)); // Vert plus visible
            messagePanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 3, 1, 0, new Color(50, 180, 50)),
                    new EmptyBorder(10, 10, 10, 10)));
        } else {
            // Fond neutre pour les autres messages
            messagePanel.setBackground(new Color(250, 250, 255)); // Très léger bleu
            messagePanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                    new EmptyBorder(10, 10, 10, 10)));
        }

        messagePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, MESSAGE_PANEL_HEIGHT));

        // Contraintes pour le layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.gridheight = 1;
        gbc.weighty = 1.0;

        // Récupération de l'utilisateur émetteur
        User sender = message.getSender();

        // Avatar
        JLabel avatarLabel = new JLabel();
        avatarLabel.setPreferredSize(new Dimension(AVATAR_SIZE, AVATAR_SIZE));
        avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Chargement de l'avatar si disponible
        AvatarUtils.displayAvatar(avatarLabel, sender.getAvatarPath(), AVATAR_SIZE, "?");

        // Ajout de l'avatar
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        gbc.weightx = 0.0;
        messagePanel.add(avatarLabel, gbc);

        // Nom de l'utilisateur
        JLabel nameLabel = new JLabel(sender.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));

        // Tag de l'utilisateur
        JLabel tagLabel = new JLabel("@" + sender.getUserTag());
        tagLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        tagLabel.setForeground(Color.GRAY);

        // Date du message
        JLabel dateLabel = new JLabel(mDateFormat.format(new Date(message.getEmissionDate())));
        dateLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        dateLabel.setForeground(Color.GRAY);
        dateLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        // Texte du message
        JTextArea textArea = new JTextArea(message.getText());
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setFont(new Font("Arial", Font.PLAIN, 14));
        textArea.setBackground(messagePanel.getBackground());
        textArea.setBorder(null);

        // Ajout du nom
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        messagePanel.add(nameLabel, gbc);

        // Ajout du tag
        gbc.gridx = 1;
        gbc.gridy = 1;
        messagePanel.add(tagLabel, gbc);

        // Ajout de la date
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        messagePanel.add(dateLabel, gbc);

        // Ajout du texte
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        messagePanel.add(textArea, gbc);

        return messagePanel;
    }

    @Override
    public void updateMessageList(Set<Message> messages) {
        // Vider la liste actuelle
        mMessagesPanel.removeAll();
        mDisplayedMessages.clear();

        // Convertir le Set en List pour pouvoir trier
        List<Message> sortedMessages = new ArrayList<>(messages);

        // Trier les messages par date (du plus ancien au plus récent pour un affichage style chat)
        sortedMessages.sort((m1, m2) -> Long.compare(m1.getEmissionDate(), m2.getEmissionDate()));

        // Ajouter les messages
        for (Message message : sortedMessages) {
            mDisplayedMessages.add(message);
            JPanel messagePanel = createMessagePanel(message);
            mMessagesPanel.add(messagePanel);
        }

        // Si aucun message
        if (mDisplayedMessages.isEmpty()) {
            JLabel emptyLabel = new JLabel("Aucun message à afficher", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            mMessagesPanel.add(emptyLabel);
        }

        // Ajouter un panel vide à la fin pour éviter que le dernier message ne soit étiré
        mMessagesPanel.add(Box.createVerticalGlue());

        // Forcer la mise à jour de l'affichage
        mMessagesPanel.revalidate();
        mMessagesPanel.repaint();

        // Faire défiler automatiquement vers le bas pour voir les messages les plus récents
        SwingUtilities.invokeLater(() -> {
            JScrollPane scrollPane = (JScrollPane) mMessagesPanel.getParent().getParent();
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    @Override
    public void resetMessageField() {
        mMessageField.setText("");
        updateCharacterCount();
    }

    @Override
    public JComponent getComponent() {
        return mMainPanel;
    }

    public void setActionListener(IMessageViewActionListener listener) {
        this.mActionListener = listener;
    }
}