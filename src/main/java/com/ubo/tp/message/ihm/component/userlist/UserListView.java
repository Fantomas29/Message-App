package main.java.com.ubo.tp.message.ihm.component.userlist;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import main.java.com.ubo.tp.message.core.event.EventManager;
import main.java.com.ubo.tp.message.core.event.NavigationEvents;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.component.AbstractComponent;
import main.java.com.ubo.tp.message.ihm.utils.AvatarUtils;

/**
 * Vue de la liste des utilisateurs enregistrés
 */
public class UserListView extends AbstractComponent implements IUserListView {

    /**
     * Hauteur d'une vignette utilisateur
     */
    protected static final int USER_PANEL_HEIGHT = 80;
    /**
     * Hauteur de l'avatar
     */
    protected static final int AVATAR_SIZE = 60;
    /**
     * Panel principal
     */
    protected JPanel mMainPanel;
    /**
     * Panel contenant la liste des utilisateurs
     */
    protected JPanel mUsersPanel;
    /**
     * Champ de recherche
     */
    protected JTextField mSearchField;
    /**
     * Liste des utilisateurs affichés
     */
    protected List<User> mDisplayedUsers;
    /**
     * Map pour stocker les boutons de suivi par utilisateur
     */
    protected Map<UUID, UserFollowButtons> mUserButtons;
    /**
     * Contrôleur de la vue
     */
    private IUserListViewActionListener mActionListener;

    /**
     * Constructeur.
     */
    public UserListView() {
        this.mDisplayedUsers = new ArrayList<>();
        this.mUserButtons = new HashMap<>();

        // Initialisation des composants graphiques
        this.initComponents();
    }

    /**
     * Initialise les composants graphiques
     */
    protected void initComponents() {
        // Panel principal
        mMainPanel = new JPanel(new BorderLayout(0, 0));
        mMainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Titre
        JLabel titleLabel = new JLabel("Liste des utilisateurs");
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

        // Panel pour le titre et le bouton
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(homeButton, BorderLayout.EAST);

        // Panel pour la liste des utilisateurs
        mUsersPanel = new JPanel();
        mUsersPanel.setLayout(new BoxLayout(mUsersPanel, BoxLayout.Y_AXIS));

        // Scroll pane VERTICAL uniquement pour la liste des utilisateurs
        JScrollPane scrollPane = new JScrollPane(mUsersPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Panel de recherche
        JPanel searchPanel = createSearchPanel();

        // Ajout des composants au panel principal
        mMainPanel.add(topPanel, BorderLayout.NORTH);
        mMainPanel.add(scrollPane, BorderLayout.CENTER);
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
                    mActionListener.onSearchUsersRequested(mSearchField.getText().trim());
                }
            }
        });

        JButton searchButton = new JButton("Rechercher");
        searchButton.addActionListener(e -> {
            if (mActionListener != null) {
                mActionListener.onSearchUsersRequested(mSearchField.getText().trim());
            }
        });

        JButton resetButton = new JButton("Réinitialiser");
        resetButton.addActionListener(e -> {
            mSearchField.setText("");
            if (mActionListener != null) {
                mActionListener.onRefreshUserListRequested();
            }
        });

        searchPanel.add(searchLabel);
        searchPanel.add(mSearchField);
        searchPanel.add(searchButton);
        searchPanel.add(resetButton);

        return searchPanel;
    }

    /**
     * Crée un panel pour un utilisateur avec ses statistiques précalculées
     */
    protected JPanel createUserPanel(User user, int followersCount, int followingCount) {
        // Utiliser GridBagLayout pour un contrôle précis des composants
        JPanel userPanel = new JPanel(new GridBagLayout());
        userPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                new EmptyBorder(5, 5, 5, 5)));
        userPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, USER_PANEL_HEIGHT + 20));

        // Contraintes pour le layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.gridheight = 1;
        gbc.weighty = 1.0;

        // Avatar
        JLabel avatarLabel = new JLabel();
        avatarLabel.setPreferredSize(new Dimension(AVATAR_SIZE, AVATAR_SIZE));
        avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Chargement de l'avatar si disponible
        AvatarUtils.displayAvatar(avatarLabel, user.getAvatarPath(), AVATAR_SIZE, "?");

        // Ajout de l'avatar
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.weightx = 0.0;
        userPanel.add(avatarLabel, gbc);

        // Informations de l'utilisateur
        JLabel nameLabel = new JLabel(user.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel tagLabel = new JLabel("@" + user.getUserTag());
        tagLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        tagLabel.setForeground(Color.GRAY);

        // Informations sur les followers et abonnements
        JLabel statsLabel = new JLabel("<html><font color='#666666'>" +
                followersCount + " abonnés · " +
                followingCount + " abonnements</font></html>");
        statsLabel.setFont(new Font("Arial", Font.PLAIN, 11));

        // Ajout du nom
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        userPanel.add(nameLabel, gbc);

        // Ajout du tag
        gbc.gridx = 1;
        gbc.gridy = 1;
        userPanel.add(tagLabel, gbc);

        // Ajout des statistiques
        gbc.gridx = 1;
        gbc.gridy = 2;
        userPanel.add(statsLabel, gbc);

        // Création des boutons pour suivre/ne plus suivre
        final JButton followButton = new JButton("Suivre");
        followButton.setPreferredSize(new Dimension(120, 30));
        followButton.setFont(new Font("Arial", Font.BOLD, 13));
        followButton.setBackground(new Color(255, 255, 255));
        followButton.setForeground(new Color(30, 144, 255));
        followButton.setOpaque(true);
        followButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(30, 144, 255), 2),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        final JButton unfollowButton = new JButton("Ne plus suivre");
        unfollowButton.setPreferredSize(new Dimension(130, 30));
        unfollowButton.setFont(new Font("Arial", Font.BOLD, 13));
        unfollowButton.setBackground(new Color(255, 255, 255));
        unfollowButton.setForeground(new Color(220, 20, 60));
        unfollowButton.setOpaque(true);
        unfollowButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 20, 60), 2),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        // Ajout des écouteurs d'événements
        followButton.addActionListener(e -> {
            if (mActionListener != null) {
                mActionListener.onFollowUserRequested(user);
            }
        });

        unfollowButton.addActionListener(e -> {
            if (mActionListener != null) {
                mActionListener.onUnfollowUserRequested(user);
            }
        });

        // Par défaut, cacher les boutons (ils seront configurés dans updateFollowStatus)
        followButton.setVisible(false);
        unfollowButton.setVisible(false);

        // Ajout du bouton Suivre
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        userPanel.add(followButton, gbc);

        // Ajout du bouton Ne plus suivre
        gbc.gridx = 3;
        userPanel.add(unfollowButton, gbc);

        // Stocker les boutons pour cet utilisateur
        mUserButtons.put(user.getUuid(), new UserFollowButtons(followButton, unfollowButton));

        return userPanel;
    }

    @Override
    public void updateUserList(Set<User> users, Map<UUID, Integer> followersCountMap) {
        // Vider la liste actuelle
        mUsersPanel.removeAll();
        mDisplayedUsers.clear();
        mUserButtons.clear();

        // Utiliser un Map pour éliminer les doublons par UUID
        Map<UUID, User> uniqueUsers = new HashMap<>();

        // Ajouter les nouveaux utilisateurs (en éliminant les doublons)
        for (User user : users) {
            // Ne pas afficher l'utilisateur "inconnu"
            if (user.getUuid().equals(main.java.com.ubo.tp.message.common.Constants.UNKNONWN_USER_UUID)) {
                continue;
            }

            // S'assurer que l'utilisateur n'est pas déjà ajouté (par UUID)
            uniqueUsers.put(user.getUuid(), user);
        }

        // Ajouter les utilisateurs uniques à l'affichage
        for (User user : uniqueUsers.values()) {
            // Récupérer le nombre de followers depuis la map
            int followersCount = followersCountMap.getOrDefault(user.getUuid(), 0);
            int followingCount = user.getFollows().size();

            mDisplayedUsers.add(user);
            JPanel userPanel = createUserPanel(user, followersCount, followingCount);
            mUsersPanel.add(userPanel);
        }

        // Si aucun utilisateur
        if (mDisplayedUsers.isEmpty()) {
            JLabel emptyLabel = new JLabel("Aucun utilisateur trouvé", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            mUsersPanel.add(emptyLabel);
        }

        // Ajouter un panel vide à la fin pour éviter que le dernier utilisateur ne soit étiré
        mUsersPanel.add(Box.createVerticalGlue());

        // Forcer la mise à jour de l'affichage
        mUsersPanel.revalidate();
        mUsersPanel.repaint();

        // Force à redessiner tout après un petit délai
        SwingUtilities.invokeLater(() -> {
            mMainPanel.revalidate();
            mMainPanel.repaint();
        });
    }

    @Override
    public void updateFollowStatus(User connectedUser) {
        if (connectedUser == null) {
            System.out.println("Impossible de mettre à jour le statut d'abonnement : utilisateur connecté null");
            return;
        }

        // Pour chaque utilisateur affiché
        for (User user : mDisplayedUsers) {
            UserFollowButtons actionButtons = mUserButtons.get(user.getUuid());

            if (actionButtons != null) {
                // Vérifier si l'utilisateur connecté suit l'utilisateur courant
                boolean isFollowing = connectedUser.isFollowing(user);
                boolean isCurrentUser = user.getUuid().equals(connectedUser.getUuid());

                actionButtons.updateVisibility(isFollowing, isCurrentUser);
            } else {
                System.out.println("Pas de boutons trouvés pour l'utilisateur @" + user.getUserTag());
            }
        }

        // Forcer la mise à jour de l'affichage
        SwingUtilities.invokeLater(() -> {
            mUsersPanel.revalidate();
            mUsersPanel.repaint();
        });
    }

    @Override
    public JComponent getComponent() {
        return mMainPanel;
    }

    public void setActionListener(IUserListViewActionListener listener) {
        this.mActionListener = listener;
    }

    /**
     * Classe interne pour gérer les boutons de suivi/non-suivi d'un utilisateur
     */
    private static class UserFollowButtons {
        private final JButton followButton;
        private final JButton unfollowButton;

        public UserFollowButtons(JButton followButton, JButton unfollowButton) {
            this.followButton = followButton;
            this.unfollowButton = unfollowButton;
        }

        /**
         * Mise à jour de la visibilité des boutons
         *
         * @param isFollowing   Indique si l'utilisateur est suivi
         * @param isCurrentUser Indique si c'est l'utilisateur courant
         */
        public void updateVisibility(boolean isFollowing, boolean isCurrentUser) {
            if (isCurrentUser) {
                // Masquer les boutons pour l'utilisateur courant
                followButton.setVisible(false);
                unfollowButton.setVisible(false);
            } else {
                // Configurer la visibilité en fonction du statut de suivi
                followButton.setVisible(!isFollowing);
                unfollowButton.setVisible(isFollowing);
            }
        }
    }
}