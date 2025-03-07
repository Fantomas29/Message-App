package main.java.com.ubo.tp.message.ihm.component.login;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.component.AbstractComponent;
import main.java.com.ubo.tp.message.ihm.component.IComponent;

/**
 * Implémentation de la vue de login/inscription
 */
public class LoginView extends AbstractComponent implements ILoginView, IComponent {

    /**
     * Référence au contrôleur
     */
    public ILoginController mController;

    /**
     * Panel principal de la vue
     */
    protected JPanel mMainPanel;

    // Composants du panel de connexion
    protected JTextField mLoginUserTagField;
    protected JPasswordField mLoginPasswordField;
    protected JButton mLoginButton;

    // Composants du panel d'inscription
    protected JTextField mSignupNameField;
    protected JTextField mSignupTagField;
    protected JPasswordField mSignupPasswordField;
    protected JButton mAvatarButton;
    protected JLabel mAvatarLabel;
    protected JButton mSignupButton;

    /**
     * Constructeur
     *
     * @param controller Contrôleur associé à la vue
     */
    public LoginView(ILoginController controller) {
        this.mController = controller;

        // Initialisation des composants graphiques
        this.initComponents();
    }

    /**
     * Initialise les composants graphiques
     */
    protected void initComponents() {
        // Création du panel principal
        mMainPanel = new JPanel(new BorderLayout(0, 0));

        // Création du titre
        JLabel titleLabel = new JLabel("Bienvenue sur MessageApp");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(new EmptyBorder(20, 0, 20, 0));

        // Ajout du titre au panel
        mMainPanel.add(titleLabel, BorderLayout.NORTH);

        // Création du panel central avec onglets
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(new EmptyBorder(10, 30, 10, 30));

        // Création des panels de connexion et d'inscription
        JPanel loginPanel = this.createLoginPanel();
        JPanel signupPanel = this.createSignupPanel();

        // Ajout des panels aux onglets
        tabbedPane.addTab("Connexion", loginPanel);
        tabbedPane.addTab("Inscription", signupPanel);

        // Ajout des onglets au panel principal
        mMainPanel.add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Création du panel de connexion
     */
    protected JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridBagLayout());
        loginPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Création des composants
        JLabel userTagLabel = new JLabel("Tag utilisateur :");
        userTagLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        mLoginUserTagField = new JTextField();
        mLoginUserTagField.setPreferredSize(new Dimension(200, 30));

        JLabel passwordLabel = new JLabel("Mot de passe :");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        mLoginPasswordField = new JPasswordField();
        mLoginPasswordField.setPreferredSize(new Dimension(200, 30));

        mLoginButton = new JButton("Se connecter");
        mLoginButton.setPreferredSize(new Dimension(150, 40));
        mLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        // Ajout des composants au panel avec GridBagConstraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Ajout du label userTag
        gbc.gridx = 0;
        gbc.gridy = 0;
        loginPanel.add(userTagLabel, gbc);

        // Ajout du champ userTag
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginPanel.add(mLoginUserTagField, gbc);

        // Ajout du label password
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        loginPanel.add(passwordLabel, gbc);

        // Ajout du champ password
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginPanel.add(mLoginPasswordField, gbc);

        // Ajout du bouton de connexion
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 5, 5, 5);
        loginPanel.add(mLoginButton, gbc);

        return loginPanel;
    }

    /**
     * Création du panel d'inscription
     */
    protected JPanel createSignupPanel() {
        JPanel signupPanel = new JPanel();
        signupPanel.setLayout(new GridBagLayout());
        signupPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Création des composants
        JLabel nameLabel = new JLabel("Nom :");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        mSignupNameField = new JTextField();
        mSignupNameField.setPreferredSize(new Dimension(200, 30));

        JLabel tagLabel = new JLabel("Tag utilisateur :");
        tagLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        mSignupTagField = new JTextField();
        mSignupTagField.setPreferredSize(new Dimension(200, 30));

        JLabel passwordLabel = new JLabel("Mot de passe :");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        mSignupPasswordField = new JPasswordField();
        mSignupPasswordField.setPreferredSize(new Dimension(200, 30));

        JLabel avatarLabel = new JLabel("Avatar :");
        avatarLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        mAvatarButton = new JButton("Parcourir...");
        mAvatarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mController.selectAvatar();
            }
        });

        mAvatarLabel = new JLabel("Aucun avatar sélectionné");
        mAvatarLabel.setFont(new Font("Arial", Font.ITALIC, 12));

        mSignupButton = new JButton("S'inscrire");
        mSignupButton.setPreferredSize(new Dimension(150, 40));
        mSignupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                signup();
            }
        });

        // Ajout des composants au panel avec GridBagConstraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Ajout du label nom
        gbc.gridx = 0;
        gbc.gridy = 0;
        signupPanel.add(nameLabel, gbc);

        // Ajout du champ nom
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        signupPanel.add(mSignupNameField, gbc);

        // Ajout du label tag
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        signupPanel.add(tagLabel, gbc);

        // Ajout du champ tag
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        signupPanel.add(mSignupTagField, gbc);

        // Ajout du label password
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        signupPanel.add(passwordLabel, gbc);

        // Ajout du champ password
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        signupPanel.add(mSignupPasswordField, gbc);

        // Ajout du label avatar
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.NONE;
        signupPanel.add(avatarLabel, gbc);

        // Panel pour avatar avec bouton et label
        JPanel avatarPanel = new JPanel(new BorderLayout(5, 0));
        avatarPanel.add(mAvatarButton, BorderLayout.WEST);
        avatarPanel.add(mAvatarLabel, BorderLayout.CENTER);

        // Ajout du panel avatar
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        signupPanel.add(avatarPanel, gbc);

        // Ajout du bouton d'inscription
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 5, 5, 5);
        signupPanel.add(mSignupButton, gbc);

        return signupPanel;
    }

    /**
     * Action de connexion
     */
    protected void login() {
        String userTag = mLoginUserTagField.getText().trim();
        String password = new String(mLoginPasswordField.getPassword());

        mController.loginUser(userTag, password);
    }

    /**
     * Action d'inscription
     */
    protected void signup() {
        String name = mSignupNameField.getText().trim();
        String tag = mSignupTagField.getText().trim();
        String password = new String(mSignupPasswordField.getPassword());

        // Vérification des champs obligatoires
        if (name.isEmpty() || tag.isEmpty() || password.isEmpty()) {
            showError("Erreur d'inscription", "Tous les champs sont obligatoires");
            return;
        }

        // Création de l'utilisateur
        Set<String> follows = new HashSet<>();
        User newUser = new User(UUID.randomUUID(), tag, password, name, follows,
                mAvatarLabel.getText().equals("Aucun avatar sélectionné") ? "" : mAvatarLabel.getText());

        // Demande au controller d'enregistrer l'utilisateur
        mController.signupUser(newUser);
    }

    @Override
    public JComponent getComponent() {
        return mMainPanel;
    }

    @Override
    public void resetLoginFields() {
        mLoginUserTagField.setText("");
        mLoginPasswordField.setText("");
    }

    @Override
    public void resetSignupFields() {
        mSignupNameField.setText("");
        mSignupTagField.setText("");
        mSignupPasswordField.setText("");
        mAvatarLabel.setText("Aucun avatar sélectionné");
    }

    @Override
    public void updateAvatarPath(String avatarPath) {
        if (avatarPath != null && !avatarPath.isEmpty()) {
            // Affichage du nom de fichier
            String fileName = avatarPath.substring(avatarPath.lastIndexOf(System.getProperty("file.separator")) + 1);
            mAvatarLabel.setText(fileName);
        } else {
            mAvatarLabel.setText("Aucun avatar sélectionné");
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
    public void init() {
        // Rien à faire pour l'initialisation
    }

    @Override
    public JComponent getView() {
        return getComponent();
    }
}