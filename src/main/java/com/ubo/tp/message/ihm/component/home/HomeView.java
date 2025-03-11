package main.java.com.ubo.tp.message.ihm.component.home;

import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.Container;


import javax.swing.*;
import javax.swing.border.EmptyBorder;

import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.MessageApp;
import main.java.com.ubo.tp.message.ihm.NotificationManager;
import main.java.com.ubo.tp.message.ihm.component.IView;

/**
 * Classe de la vue d'accueil de l'application.
 */
public class HomeView extends JPanel implements IView {

    private static final long serialVersionUID = 1L;


    /**
     *
     */
    private IHomeViewActionListener mActionListener;

    /**
     * Session de l'application
     */
    protected ISession mSession;

    /**
     * Référence à l'application principale
     */
    protected MessageApp mMessageApp;

    /**
     * Panneau d'informations utilisateur
     */
    protected JPanel mUserInfoPanel;

    /**
     * Étiquette du nom d'utilisateur
     */
    protected JLabel mUserNameLabel;

    /**
     * Carte pour accéder aux messages
     */
    protected JPanel mMessagesCard;

    /**
     * Carte pour accéder aux utilisateurs
     */
    protected JLabel mUnreadBadgeLabel;

    /**
     * Étiquette du tag utilisateur
     */
    protected JLabel mUserTagLabel;

    /**
     * Label pour l'avatar
     */
    protected JLabel mAvatarLabel;

    /**
     * Constructeur.
     *
     * @param session Session de l'application
     */
    public HomeView(ISession session) {
        this.mSession = session;

        // Initialisation des composants graphiques
        this.initComponents();
    }


    public void setActionListener(IHomeViewActionListener listener) {
        this.mActionListener = listener;
    }

    /**
     * Initialisation des composants graphiques de la vue
     */
    protected void initComponents() {
        // Configuration du layout
        this.setLayout(new BorderLayout(20, 20));
        this.setBorder(new EmptyBorder(30, 30, 30, 30));
        this.setBackground(new Color(245, 245, 250)); // Fond légèrement gris bleuté

        // En-tête avec le logo et le titre
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
        headerPanel.setOpaque(false);

        // Logo de l'application
        JLabel logoLabel = new JLabel();
        try {
            java.net.URL resourceURL = getClass().getResource("/images/logo_message.png");
            if (resourceURL != null) {
                ImageIcon originalIcon = new ImageIcon(resourceURL);
                Image img = originalIcon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                logoLabel.setIcon(new ImageIcon(img));
            } else {
                logoLabel.setText("*logo*");
                logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
                System.err.println("Resource not found: /images/logo_message.png");
            }
        } catch (Exception e) {
            logoLabel.setText("*logo*");
            logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
            System.err.println("Error loading icon: " + e.getMessage());
        }

        // Titre de l'application
        JLabel titleLabel = new JLabel("Bienvenue sur MessageApp");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Assemblage de l'en-tête
        headerPanel.add(logoLabel);
        headerPanel.add(Box.createHorizontalStrut(20));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createHorizontalGlue());

        // Panel d'informations utilisateur (visible uniquement lorsqu'un utilisateur est connecté)
        mUserInfoPanel = new JPanel();
        mUserInfoPanel.setLayout(new BoxLayout(mUserInfoPanel, BoxLayout.Y_AXIS));
        mUserInfoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 255), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        mUserInfoPanel.setBackground(new Color(240, 240, 255));

        // Avatar de l'utilisateur
        mAvatarLabel = new JLabel();
        mAvatarLabel.setAlignmentX(CENTER_ALIGNMENT);
        mAvatarLabel.setPreferredSize(new Dimension(80, 80));
        mAvatarLabel.setMinimumSize(new Dimension(80, 80));
        mAvatarLabel.setMaximumSize(new Dimension(80, 80));

        // Nom de l'utilisateur
        mUserNameLabel = new JLabel("Nom d'utilisateur");
        mUserNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        mUserNameLabel.setAlignmentX(CENTER_ALIGNMENT);

        // Tag de l'utilisateur
        mUserTagLabel = new JLabel("@tag");
        mUserTagLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        mUserTagLabel.setForeground(Color.DARK_GRAY);
        mUserTagLabel.setAlignmentX(CENTER_ALIGNMENT);

        // Assemblage du panel utilisateur
        mUserInfoPanel.add(mAvatarLabel);
        mUserInfoPanel.add(Box.createVerticalStrut(10));
        mUserInfoPanel.add(mUserNameLabel);
        mUserInfoPanel.add(Box.createVerticalStrut(5));
        mUserInfoPanel.add(mUserTagLabel);

        // Masquer initialement le panel utilisateur
        mUserInfoPanel.setVisible(false);

        // Ajout du panel utilisateur à l'en-tête
        headerPanel.add(mUserInfoPanel);

        // Ajout de l'en-tête au panel principal
        this.add(headerPanel, BorderLayout.NORTH);

        // Panel central avec les cartes de navigation
        JPanel navigationPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        navigationPanel.setOpaque(false);

        // Carte pour accéder au profil
        JPanel profileCard = createNavigationCard(
                "Mon Profil",
                "Consultez et modifiez votre profil utilisateur",
                "/main/resources/images/user.png",
                e -> {
                    if (mActionListener != null) {
                        mActionListener.onShowProfileRequested();
                    }
                }
        );

        // Carte pour accéder aux messages
        JPanel messagesCard = createNavigationCard(
                "Messages",
                "Consultez et envoyez des messages",
                "/main/resources/images/message.png",
                e -> {
                    if (mActionListener != null) {
                        mActionListener.onShowMessagesRequested();
                    }
                }
        );

        JPanel badgeContainer = new JPanel(new BorderLayout());
        badgeContainer.setOpaque(false);
        badgeContainer.add(messagesCard, BorderLayout.CENTER);

        // Créer le badge pour les messages non lus
        mUnreadBadgeLabel = new JLabel("0");
        mUnreadBadgeLabel.setFont(new Font("Arial", Font.BOLD, 12));
        mUnreadBadgeLabel.setForeground(Color.WHITE);
        mUnreadBadgeLabel.setBackground(Color.RED);
        mUnreadBadgeLabel.setOpaque(true);
        mUnreadBadgeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mUnreadBadgeLabel.setVerticalAlignment(SwingConstants.CENTER);
        mUnreadBadgeLabel.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        mUnreadBadgeLabel.setVisible(false); // Masqué par défaut

        // Créer un panel pour positionner le badge en haut à droite
        JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        badgePanel.setOpaque(false);
        badgePanel.add(mUnreadBadgeLabel);


        // Ajouter le badge en position NORTH dans le container
        badgeContainer.add(badgePanel, BorderLayout.NORTH);

        // Stocker la référence
        this.mMessagesCard = badgeContainer;

        // Ajouter un écouteur pour mettre à jour le badge
        NotificationManager.getInstance().addListener(this::updateUnreadBadge);


        // Carte pour accéder à la liste des utilisateurs
        JPanel usersCard = createNavigationCard(
                "Utilisateurs",
                "Consultez la liste des utilisateurs et gérez vos abonnements",
                "/main/resources/images/logo_20.png",
                e -> {
                    if (mActionListener != null) {
                        mActionListener.onShowUserListRequested();
                    }
                }
        );

        // Carte pour se déconnecter
        JPanel logoutCard = createNavigationCard(
                "Déconnexion",
                "Déconnectez-vous de votre compte",
                "/main/resources/images/logout.png",
                e -> {
                    if (mActionListener != null) {
                        mActionListener.onLogoutRequested();
                    }
                }
        );

        // Ajout des cartes au panel de navigation
        navigationPanel.add(profileCard);
        navigationPanel.add(badgeContainer);
        navigationPanel.add(usersCard);
        navigationPanel.add(logoutCard);

        // Ajout du panel de navigation au panel principal
        this.add(navigationPanel, BorderLayout.CENTER);

        // Pied de page
        JLabel footerLabel = new JLabel("© 2025 MessageApp - M2-TIIL UBO", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        footerLabel.setForeground(Color.GRAY);
        footerLabel.setBorder(new EmptyBorder(20, 0, 0, 0));

        // Ajout du pied de page au panel principal
        this.add(footerLabel, BorderLayout.SOUTH);
    }

    /**
     * Crée une carte de navigation
     *
     * @param title Titre de la carte
     * @param description Description de la carte
     * @param iconPath Chemin vers l'icône
     * @param actionListener Action à exécuter au clic
     * @return Une carte de navigation
     */
    protected JPanel createNavigationCard(String title, String description, String iconPath, ActionListener actionListener) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 255), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        card.setBackground(Color.WHITE);

        // Titre de la carte
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        // Description de la carte
        JLabel descLabel = new JLabel("<html>" + description + "</html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        // Panel pour le texte
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(10));
        textPanel.add(descLabel);

        // Icône de la carte
        JLabel iconLabel = new JLabel();
        try {
            String correctedPath = iconPath.replace("/main/resources", "");
            java.net.URL resourceURL = getClass().getResource(correctedPath);

            if (resourceURL != null) {
                ImageIcon originalIcon = new ImageIcon(resourceURL);
                Image img = originalIcon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
                iconLabel.setIcon(new ImageIcon(img));
            } else {
                System.err.println("Resource not found: " + iconPath);
                iconLabel.setText("");
            }
        } catch (Exception e) {
            System.err.println("Error loading icon: " + e.getMessage());
            iconLabel.setText("");
        }
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setPreferredSize(new Dimension(60, 60));

        // Bouton pour activer la carte
        JButton actionButton = new JButton("Accéder");
        actionButton.addActionListener(actionListener);
        actionButton.setFont(new Font("Arial", Font.BOLD, 14));
        actionButton.setBackground(new Color(230, 230, 250));
        actionButton.setForeground(new Color(50, 50, 100));

        // Assemblage de la carte
        card.add(iconLabel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        card.add(actionButton, BorderLayout.SOUTH);

        return card;
    }

    /**
     * Met à jour les informations utilisateur
     *
     * @param user Utilisateur connecté
     */
    public void updateUserInfo(User user) {
        if (user != null) {
            // Mise à jour du nom
            mUserNameLabel.setText(user.getName());

            // Mise à jour du tag
            mUserTagLabel.setText("@" + user.getUserTag());

            // Mise à jour de l'avatar
            if (user.getAvatarPath() != null && !user.getAvatarPath().isEmpty()) {
                File avatarFile = new File(user.getAvatarPath());
                if (avatarFile.exists()) {
                    try {
                        ImageIcon originalIcon = new ImageIcon(user.getAvatarPath());
                        Image img = originalIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                        mAvatarLabel.setIcon(new ImageIcon(img));
                        mAvatarLabel.setText("");
                    } catch (Exception e) {
                        mAvatarLabel.setIcon(null);
                        mAvatarLabel.setText("?");
                        mAvatarLabel.setFont(new Font("Arial", Font.BOLD, 36));
                        mAvatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    }
                } else {
                    mAvatarLabel.setIcon(null);
                    mAvatarLabel.setText("?");
                    mAvatarLabel.setFont(new Font("Arial", Font.BOLD, 36));
                    mAvatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
                }
            } else {
                mAvatarLabel.setIcon(null);
                mAvatarLabel.setText("?");
                mAvatarLabel.setFont(new Font("Arial", Font.BOLD, 36));
                mAvatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
            }

            // Afficher le panel utilisateur
            mUserInfoPanel.setVisible(true);
        } else {
            // Masquer le panel utilisateur si aucun utilisateur n'est connecté
            mUserInfoPanel.setVisible(false);
        }

        // Forcer le rafraîchissement
        this.revalidate();
        this.repaint();
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    /**
     * Met à jour le badge de messages non lus
     * @param count nombre de messages non lus
     */
    protected void updateUnreadBadge(final int count) {
        // Exécuter dans le thread EDT pour garantir la sécurité des composants Swing
        SwingUtilities.invokeLater(() -> {
            if (count > 0) {
                mUnreadBadgeLabel.setText(String.valueOf(count));
                mUnreadBadgeLabel.setVisible(true);
            } else {
                mUnreadBadgeLabel.setVisible(false);
            }

            // Forcer le rafraîchissement de l'interface
            if (mMessagesCard != null) {
                mMessagesCard.revalidate();
                mMessagesCard.repaint();
            }
        });
    }

}