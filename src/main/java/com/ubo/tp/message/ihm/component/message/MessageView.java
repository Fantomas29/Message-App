package main.java.com.ubo.tp.message.ihm.component.message;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.component.AbstractComponent;

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
    public IMessageController mController;

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
     *
     * @param controller Contrôleur associé à la vue
     */
    public MessageView(IMessageController controller) {
        this.mController = controller;
        this.mDisplayedMessages = new ArrayList<>();
        this.mDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        // Initialisation des composants graphiques
        this.initComponents();
    }

    /**
     * Constructeur sans contrôleur (utilisé pour l'initialisation MVC)
     */
    public MessageView() {
        this(null);
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
        mMainPanel.add(titleLabel, BorderLayout.NORTH);
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
                if (mController != null) {
                    mController.searchMessages(mSearchField.getText().trim());
                }
            }
        });

        JButton searchButton = new JButton("Rechercher");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mController != null) {
                    mController.searchMessages(mSearchField.getText().trim());
                }
            }
        });

        JButton resetButton = new JButton("Réinitialiser");
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mSearchField.setText("");
                if (mController != null) {
                    mController.refreshMessages();
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
                if (mController != null) {
                    mController.sendMessage(mMessageField.getText());
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
        messagePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                new EmptyBorder(10, 10, 10, 10)));
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
        if (sender.getAvatarPath() != null && !sender.getAvatarPath().isEmpty()) {
            File avatarFile = new File(sender.getAvatarPath());
            if (avatarFile.exists()) {
                try {
                    ImageIcon originalIcon = new ImageIcon(sender.getAvatarPath());
                    Image img = originalIcon.getImage().getScaledInstance(AVATAR_SIZE, AVATAR_SIZE, Image.SCALE_SMOOTH);
                    avatarLabel.setIcon(new ImageIcon(img));
                } catch (Exception e) {
                    avatarLabel.setText("?");
                    avatarLabel.setFont(new Font("Arial", Font.BOLD, 24));
                }
            } else {
                avatarLabel.setText("?");
                avatarLabel.setFont(new Font("Arial", Font.BOLD, 24));
            }
        } else {
            avatarLabel.setText("?");
            avatarLabel.setFont(new Font("Arial", Font.BOLD, 24));
        }

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

        // Trier les messages par date (du plus récent au plus ancien)
        sortedMessages.sort((m1, m2) -> Long.compare(m2.getEmissionDate(), m1.getEmissionDate()));

        // Ajouter les nouveaux messages
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
    }

    @Override
    public void resetMessageField() {
        mMessageField.setText("");
        updateCharacterCount();
    }

    @Override
    public void showError(String title, String message) {
        JOptionPane.showMessageDialog(mMainPanel, message, title, JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void showInfo(String title, String message) {
        JOptionPane.showMessageDialog(mMainPanel, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public JComponent getComponent() {
        return mMainPanel;
    }

    @Override
    public void init() {
        // Initialisation de la liste des messages
        if (mController != null) {
            mController.refreshMessages();
        }
    }

    @Override
    public JComponent getView() {
        return getComponent();
    }
}