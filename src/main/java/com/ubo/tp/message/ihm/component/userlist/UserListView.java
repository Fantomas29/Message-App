package main.java.com.ubo.tp.message.ihm.component.userlist;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.component.AbstractComponent;

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
     * Référence au contrôleur
     */
    public IUserListController mController;
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
     * Map pour stocker les boutons par utilisateur
     */
    protected Map<UUID, JButton[]> mUserButtons;

    /**
     * Constructeur.
     *
     * @param controller Contrôleur de la vue
     */
    public UserListView(IUserListController controller) {
        this.mController = controller;
        this.mDisplayedUsers = new ArrayList<>();
        this.mUserButtons = new HashMap<>();

        // Initialisation des composants graphiques
        this.initComponents();
    }

    /**
     * Constructeur sans contrôleur (utilisé pour l'initialisation MVC)
     */
    public UserListView() {
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
        JLabel titleLabel = new JLabel("Liste des utilisateurs");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));

        // Panel pour la liste des utilisateurs
        mUsersPanel = new JPanel();
        mUsersPanel.setLayout(new BoxLayout(mUsersPanel, BoxLayout.Y_AXIS));

        // Scroll pane VERTICAL uniquement pour la liste des utilisateurs
        JScrollPane scrollPane = new JScrollPane(mUsersPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // Désactiver barre horizontale

        // Panel de recherche
        JPanel searchPanel = createSearchPanel();

        // Ajout des composants au panel principal
        mMainPanel.add(titleLabel, BorderLayout.NORTH);
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
                if (mController != null) {
                    mController.searchUsers(mSearchField.getText().trim());
                }
            }
        });

        JButton searchButton = new JButton("Rechercher");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mController != null) {
                    mController.searchUsers(mSearchField.getText().trim());
                }
            }
        });

        JButton resetButton = new JButton("Réinitialiser");
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mSearchField.setText("");
                if (mController != null) {
                    mController.refreshUserList();
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
     * Crée un panel pour un utilisateur
     */
    protected JPanel createUserPanel(User user) {
        // Utiliser GridBagLayout pour un contrôle précis des composants
        JPanel userPanel = new JPanel(new GridBagLayout());
        userPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                new EmptyBorder(5, 5, 5, 5)));
        userPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, USER_PANEL_HEIGHT));

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
        if (user.getAvatarPath() != null && !user.getAvatarPath().isEmpty()) {
            File avatarFile = new File(user.getAvatarPath());
            if (avatarFile.exists()) {
                try {
                    ImageIcon originalIcon = new ImageIcon(user.getAvatarPath());
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
        gbc.gridheight = 2;
        gbc.weightx = 0.0;
        userPanel.add(avatarLabel, gbc);

        // Informations de l'utilisateur
        JLabel nameLabel = new JLabel(user.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel tagLabel = new JLabel("@" + user.getUserTag());
        tagLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        tagLabel.setForeground(Color.GRAY);

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

        // Création des boutons avec une taille fixe et plus visible
        final JButton followButton = new JButton("Suivre");
        followButton.setPreferredSize(new Dimension(120, 30));
        followButton.setFont(new Font("Arial", Font.BOLD, 13));
        followButton.setBackground(new Color(255, 255, 255)); // Fond blanc
        followButton.setForeground(new Color(30, 144, 255));  // Texte bleu
        followButton.setOpaque(true);
        followButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(30, 144, 255), 2), // Bordure bleue
                BorderFactory.createEmptyBorder(4, 8, 4, 8)  // Padding interne
        ));

        final JButton unfollowButton = new JButton("Ne plus suivre");
        unfollowButton.setPreferredSize(new Dimension(130, 30));
        // Augmenter la taille et le gras de la police pour une meilleure lisibilité
        unfollowButton.setFont(new Font("Arial", Font.BOLD, 13));
        unfollowButton.setBackground(new Color(255, 255, 255)); // Fond blanc
        unfollowButton.setForeground(new Color(220, 20, 60));    // Texte rouge
        unfollowButton.setOpaque(true);
        unfollowButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 20, 60), 2), // Bordure rouge
                BorderFactory.createEmptyBorder(4, 8, 4, 8)  // Padding interne
        ));

        // Ajout des écouteurs d'événements
        followButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Clic sur le bouton Suivre pour @" + user.getUserTag());
                if (mController != null) {
                    mController.followUser(user);
                }
            }
        });

        unfollowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Clic sur le bouton Ne plus suivre pour @" + user.getUserTag());
                if (mController != null) {
                    mController.unfollowUser(user);
                }
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
        JButton[] buttons = new JButton[2];
        buttons[0] = followButton;
        buttons[1] = unfollowButton;
        mUserButtons.put(user.getUuid(), buttons);

        return userPanel;
    }

    @Override
    public void updateUserList(Set<User> users) {
        // Vider la liste actuelle
        mUsersPanel.removeAll();
        mDisplayedUsers.clear();
        mUserButtons.clear();

        System.out.println("Mise à jour de la liste des utilisateurs : " + users.size() + " utilisateurs reçus");

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

        System.out.println("Nombre d'utilisateurs uniques à afficher : " + uniqueUsers.size());

        // Ajouter les utilisateurs uniques à l'affichage
        for (User user : uniqueUsers.values()) {
            mDisplayedUsers.add(user);
            JPanel userPanel = createUserPanel(user);
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
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                mMainPanel.revalidate();
                mMainPanel.repaint();
            }
        });
    }

    @Override
    public void updateFollowStatus(User connectedUser) {
        if (connectedUser == null) {
            System.out.println("Impossible de mettre à jour le statut d'abonnement : utilisateur connecté null");
            return;
        }

        System.out.println("Mise à jour du statut d'abonnement pour l'utilisateur @" + connectedUser.getUserTag());
        System.out.println("Nombre d'utilisateurs dans la vue : " + mDisplayedUsers.size());
        System.out.println("Nombre de boutons stockés : " + mUserButtons.size());

        // Pour chaque utilisateur affiché
        for (User user : mDisplayedUsers) {
            // Récupérer les boutons associés à cet utilisateur
            JButton[] buttons = mUserButtons.get(user.getUuid());

            if (buttons != null) {
                JButton followButton = buttons[0];
                JButton unfollowButton = buttons[1];

                // Ne pas afficher les boutons pour l'utilisateur connecté lui-même
                if (user.getUuid().equals(connectedUser.getUuid())) {
                    followButton.setVisible(false);
                    unfollowButton.setVisible(false);
                    System.out.println("Utilisateur @" + user.getUserTag() + " - C'est l'utilisateur connecté, pas de boutons affichés");
                    continue;
                }

                // Vérifier si l'utilisateur connecté suit l'utilisateur courant
                boolean isFollowing = connectedUser.isFollowing(user);

                // Configuration des boutons en fonction du statut d'abonnement
                followButton.setVisible(!isFollowing);
                unfollowButton.setVisible(isFollowing);

                System.out.println("Utilisateur @" + user.getUserTag() + " - Est suivi: " + isFollowing +
                        " - Bouton Suivre visible: " + followButton.isVisible() +
                        " - Bouton Ne plus suivre visible: " + unfollowButton.isVisible());
            } else {
                System.out.println("Pas de boutons trouvés pour l'utilisateur @" + user.getUserTag());
            }
        }

        // Forcer la mise à jour de l'affichage
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                mUsersPanel.revalidate();
                mUsersPanel.repaint();
            }
        });
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
        // Essayer de forcer un look and feel cohérent
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initialisation de la liste des utilisateurs
        if (mController != null) {
            mController.initUserList();
        } else {
            System.err.println("Le contrôleur n'est pas initialisé pour la vue UserListView");
        }
    }

    @Override
    public JComponent getView() {
        return getComponent();
    }
}