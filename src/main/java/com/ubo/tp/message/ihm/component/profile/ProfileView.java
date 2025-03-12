package main.java.com.ubo.tp.message.ihm.component.profile;

import java.awt.*;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.component.AbstractComponent;
import main.java.com.ubo.tp.message.ihm.utils.AvatarUtils;

/**
 * Panel de consultation et de modification du profil utilisateur
 */
public class ProfileView extends AbstractComponent implements IProfileView {

    private static final long serialVersionUID = 1L;

    /**
     * Référence au controller de profil
     */
    private IProfileViewActionListener mActionListener;

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
     */
    public ProfileView() {
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
        mAvatarButton.addActionListener(e -> {
            if (mActionListener != null) {
                String avatarPath = mActionListener.onAvatarSelectionRequested();
                if (avatarPath != null) {
                    mNewAvatarPath = avatarPath;
                    AvatarUtils.displayAvatar(mAvatarLabel, mNewAvatarPath, 150, "?");
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
        mSaveButton.addActionListener(e -> saveProfile());

        // Bouton Quitter
        JButton mQuitButton = new JButton("Page d'accueil");
        mQuitButton.setPreferredSize(new Dimension(120, 30));
        mQuitButton.setBackground(new Color(230, 230, 250));
        mQuitButton.setForeground(new Color(50, 50, 100));
        mQuitButton.setFont(new Font("Arial", Font.BOLD, 12));
        mQuitButton.addActionListener(e -> {
            if (mActionListener != null) {
                mActionListener.onReturnToMainViewRequested();
            }
        });

        // Ajout des boutons au panel
        buttonPanel.add(mSaveButton);
        buttonPanel.add(mQuitButton);

        // Ajout des panels au layout principal
        contentPanel.add(mAvatarPanel, BorderLayout.WEST);
        contentPanel.add(infoPanel, BorderLayout.CENTER);

        // Ajout des panels au panel principal
        mMainPanel.add(contentPanel, BorderLayout.CENTER);
        mMainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Enregistre les modifications du profil
     */
    protected void saveProfile() {
        // Récupération des valeurs
        String name = mNameField.getText().trim();
        String currentPassword = new String(mCurrentPasswordField.getPassword());
        String newPassword = new String(mNewPasswordField.getPassword());

        if (mActionListener != null) {
            // Demande au contrôleur de mettre à jour le profil
            boolean success = mActionListener.onUpdateProfileRequested(name, currentPassword, newPassword, mNewAvatarPath);

            if (success) {
                // Réinitialisation des champs
                resetFields();
            }
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
            AvatarUtils.displayAvatar(mAvatarLabel, mConnectedUser.getAvatarPath(), 150, "?");
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
    public JComponent getComponent() {
        return mMainPanel;
    }

    public void setActionListener(IProfileViewActionListener listener) {
        this.mActionListener = listener;
    }
}