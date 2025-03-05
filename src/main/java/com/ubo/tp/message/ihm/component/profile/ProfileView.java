package main.java.com.ubo.tp.message.ihm.component.profile;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.component.AbstractComponent;

/**
 * Panel de consultation et de modification du profil utilisateur
 */
public class ProfileView extends AbstractComponent implements IProfileView {

    private static final long serialVersionUID = 1L;

    /**
     * Référence au controller de profil
     */
    public IProfileController mController;

    /**
     * Utilisateur actuellement connecté
     */
    protected User mConnectedUser;

    /**
     * Panel principal
     */
    protected JPanel mMainPanel;

    // Composants du panel de profil
    protected JLabel mUserTagLabel;
    protected JTextField mNameField;
    protected JPasswordField mCurrentPasswordField;
    protected JPasswordField mNewPasswordField;
    protected JLabel mAvatarLabel;
    protected JButton mAvatarButton;
    protected JButton mSaveButton;
    protected JButton mCancelButton;

    // Panel pour l'avatar
    protected JPanel mAvatarPanel;

    // Chemin vers le nouvel avatar sélectionné
    protected String mNewAvatarPath;

    /**
     * Constructeur.
     *
     * @param controller Référence vers le controller
     */
    public ProfileView(IProfileController controller) {
        this.mController = controller;
        this.mNewAvatarPath = "";

        // Initialisation des composants graphiques
        this.initComponents();
    }

    /**
     * Initialisation des composants graphiques.
     */
    protected void initComponents() {
        // Configuration du layout
        mMainPanel = new JPanel(new BorderLayout(10, 10));
        mMainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Titre du panneau
        JLabel titleLabel = new JLabel("Votre Profil");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Ajout du titre
        mMainPanel.add(titleLabel, BorderLayout.NORTH);

        // Panel principal
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout(20, 0));

        // Panel gauche pour l'avatar
        mAvatarPanel = new JPanel();
        mAvatarPanel.setLayout(new BorderLayout(0, 10));
        mAvatarPanel.setPreferredSize(new Dimension(200, 250));
        mAvatarPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Avatar",
                TitledBorder.CENTER,
                TitledBorder.TOP));

        // Label pour l'avatar
        mAvatarLabel = new JLabel();
        mAvatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mAvatarLabel.setVerticalAlignment(SwingConstants.CENTER);
        mAvatarLabel.setPreferredSize(new Dimension(180, 180));

        // Bouton pour changer l'avatar
        mAvatarButton = new JButton("Changer l'avatar");
        mAvatarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String avatarPath = mController.selectAvatar();
                if (avatarPath != null) {
                    mNewAvatarPath = avatarPath;
                    displayAvatar(mNewAvatarPath);
                }
            }
        });

        // Ajout des composants au panel avatar
        mAvatarPanel.add(mAvatarLabel, BorderLayout.CENTER);
        mAvatarPanel.add(mAvatarButton, BorderLayout.SOUTH);

        // Panel droit pour les informations de profil
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Informations",
                TitledBorder.CENTER,
                TitledBorder.TOP));

        // Composants du panel d'informations
        JLabel tagLabel = new JLabel("Tag utilisateur:");
        tagLabel.setFont(new Font("Arial", Font.BOLD, 14));

        mUserTagLabel = new JLabel();
        mUserTagLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel nameLabel = new JLabel("Nom:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));

        mNameField = new JTextField();
        mNameField.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel currentPasswordLabel = new JLabel("Mot de passe actuel:");
        currentPasswordLabel.setFont(new Font("Arial", Font.BOLD, 14));

        mCurrentPasswordField = new JPasswordField();

        JLabel newPasswordLabel = new JLabel("Nouveau mot de passe (optionnel):");
        newPasswordLabel.setFont(new Font("Arial", Font.BOLD, 14));

        mNewPasswordField = new JPasswordField();

        // Ajout des composants au panel d'informations avec GridBagConstraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Tag utilisateur
        gbc.gridx = 0;
        gbc.gridy = 0;
        infoPanel.add(tagLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        infoPanel.add(mUserTagLabel, gbc);

        // Nom
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        infoPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        infoPanel.add(mNameField, gbc);

        // Mot de passe actuel
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        infoPanel.add(currentPasswordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        infoPanel.add(mCurrentPasswordField, gbc);

        // Nouveau mot de passe
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        infoPanel.add(newPasswordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        infoPanel.add(mNewPasswordField, gbc);

        // Panel pour les boutons
        JPanel buttonPanel = new JPanel();

        // Bouton Enregistrer
        mSaveButton = new JButton("Enregistrer");
        mSaveButton.setPreferredSize(new Dimension(120, 30));
        mSaveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveProfile();
            }
        });

        // Bouton Annuler
        mCancelButton = new JButton("Annuler");
        mCancelButton.setPreferredSize(new Dimension(120, 30));
        mCancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetFields();
            }
        });

        // Bouton Quitter
        JButton mQuitButton = new JButton("Quitter");
        mQuitButton.setPreferredSize(new Dimension(120, 30));
        mQuitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mController.returnToMainView();
            }
        });

        // Ajout des boutons au panel
        buttonPanel.add(mSaveButton);
        buttonPanel.add(mCancelButton);
        buttonPanel.add(mQuitButton);

        // Ajout des panels au layout principal
        contentPanel.add(mAvatarPanel, BorderLayout.WEST);
        contentPanel.add(infoPanel, BorderLayout.CENTER);

        // Ajout des panels au panel principal
        mMainPanel.add(contentPanel, BorderLayout.CENTER);
        mMainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Affiche l'avatar de l'utilisateur
     *
     * @param avatarPath Chemin vers l'avatar
     */
    protected void displayAvatar(String avatarPath) {
        if (avatarPath != null && !avatarPath.isEmpty()) {
            try {
                // Chargement de l'image
                ImageIcon originalIcon = new ImageIcon(avatarPath);

                // Redimensionnement de l'image pour qu'elle tienne dans le label
                Image img = originalIcon.getImage();
                Image resizedImg = img.getScaledInstance(150, 150, Image.SCALE_SMOOTH);

                // Création d'une nouvelle icône avec l'image redimensionnée
                ImageIcon resizedIcon = new ImageIcon(resizedImg);

                // Affichage de l'icône dans le label
                mAvatarLabel.setIcon(resizedIcon);
                mAvatarLabel.setText("");
            } catch (Exception e) {
                // En cas d'erreur, afficher un texte
                mAvatarLabel.setIcon(null);
                mAvatarLabel.setText("Aucun avatar");
            }
        } else {
            // Pas d'avatar défini
            mAvatarLabel.setIcon(null);
            mAvatarLabel.setText("Aucun avatar");
        }
    }

    /**
     * Enregistre les modifications du profil
     */
    protected void saveProfile() {
        // Récupération des valeurs
        String name = mNameField.getText().trim();
        String currentPassword = new String(mCurrentPasswordField.getPassword());
        String newPassword = new String(mNewPasswordField.getPassword());

        // Demande au contrôleur de mettre à jour le profil
        boolean success = mController.updateProfile(name, currentPassword, newPassword, mNewAvatarPath);

        if (success) {
            // Réinitialisation des champs
            resetFields();
        }
    }

    @Override
    public void updateUserInfo(User connectedUser) {
        if (connectedUser != null) {
            this.mConnectedUser = connectedUser;
            this.mNewAvatarPath = connectedUser.getAvatarPath();

            // Affichage du tag utilisateur (non modifiable)
            mUserTagLabel.setText("@" + connectedUser.getUserTag());

            // Affichage du nom
            mNameField.setText(connectedUser.getName());

            // Vider les champs de mot de passe
            mCurrentPasswordField.setText("");
            mNewPasswordField.setText("");

            // Affichage de l'avatar
            displayAvatar(mNewAvatarPath);
        }
    }

    @Override
    public void resetFields() {
        if (mConnectedUser != null) {
            // Réinitialisation avec les valeurs actuelles de l'utilisateur
            updateUserInfo(mConnectedUser);
        }
    }

    @Override
    public void showError(String title, String message) {
        JOptionPane.showMessageDialog(
                mMainPanel,
                message,
                title,
                JOptionPane.ERROR_MESSAGE
        );
    }

    @Override
    public void showInfo(String title, String message) {
        JOptionPane.showMessageDialog(
                mMainPanel,
                message,
                title,
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    @Override
    public JComponent getComponent() {
        return mMainPanel;
    }

    @Override
    public void init() {
        // Pas d'initialisation supplémentaire nécessaire
    }

    @Override
    public JComponent getView() {
        return getComponent();
    }
}