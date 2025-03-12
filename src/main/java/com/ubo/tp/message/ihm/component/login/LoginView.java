package main.java.com.ubo.tp.message.ihm.component.login;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;

import main.java.com.ubo.tp.message.ihm.component.AbstractComponent;
import main.java.com.ubo.tp.message.ihm.component.IComponent;
import main.java.com.ubo.tp.message.ihm.utils.AvatarUtils;

/**
 * Implémentation de la vue de login/inscription avec JavaFX contenu dans Swing
 */
public class LoginView extends AbstractComponent implements ILoginView, IComponent {

    /**
     * Référence au contrôleur
     */
    private ILoginViewActionListener mActionListener;

    /**
     * Panel principal de la vue (Swing)
     */
    protected JPanel mMainPanel;

    /**
     * Panel JavaFX embarqué dans Swing
     */
    protected JFXPanel jfxPanel;

    // Composants JavaFX du panel de connexion
    protected TextField mLoginUserTagField;
    protected PasswordField mLoginPasswordField;
    protected Button mLoginButton;

    // Composants JavaFX du panel d'inscription
    protected TextField mSignupNameField;
    protected TextField mSignupTagField;
    protected PasswordField mSignupPasswordField;
    protected Button mAvatarButton;
    protected Label mAvatarLabel;
    protected ImageView mAvatarImageView;
    protected Button mSignupButton;

    // Pour stocker le chemin de l'avatar
    private String mAvatarPath;

    /**
     * Constructeur
     */
    public LoginView() {
        // Initialisation des composants graphiques
        this.initComponents();
    }

    /**
     * Initialise les composants graphiques
     */
    protected void initComponents() {
        // Création du panel principal Swing
        mMainPanel = new JPanel(new BorderLayout());

        // Création du panel JavaFX
        jfxPanel = new JFXPanel();
        mMainPanel.add(jfxPanel, BorderLayout.CENTER);

        // La création des composants JavaFX doit se faire sur le thread JavaFX
        Platform.runLater(() -> createJavaFXContent());
    }

    /**
     * Création du contenu JavaFX
     */
    private void createJavaFXContent() {
        // Création du panel principal
        BorderPane mainPane = new BorderPane();
        mainPane.setPadding(new Insets(10));

        // Création du titre
        Label titleLabel = new Label("Bienvenue sur MessageApp");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setPadding(new Insets(20, 0, 20, 0));
        BorderPane.setAlignment(titleLabel, Pos.CENTER);

        // Ajout du titre au panel
        mainPane.setTop(titleLabel);

        // Création du panel central avec onglets
        TabPane tabbedPane = new TabPane();
        tabbedPane.setPadding(new Insets(10, 30, 10, 30));

        // Création des panels de connexion et d'inscription
        GridPane loginPanel = this.createLoginPanel();
        GridPane signupPanel = this.createSignupPanel();

        // Ajout des panels aux onglets
        Tab loginTab = new Tab("Connexion", loginPanel);
        Tab signupTab = new Tab("Inscription", signupPanel);

        // Désactiver la fermeture des onglets
        loginTab.setClosable(false);
        signupTab.setClosable(false);

        tabbedPane.getTabs().addAll(loginTab, signupTab);

        // Ajout des onglets au panel principal
        mainPane.setCenter(tabbedPane);

        // Création de la scène JavaFX
        Scene scene = new Scene(mainPane);
        jfxPanel.setScene(scene);
    }

    /**
     * Création du panel de connexion
     */
    protected GridPane createLoginPanel() {
        GridPane loginPanel = new GridPane();
        loginPanel.setPadding(new Insets(20));
        loginPanel.setHgap(10);
        loginPanel.setVgap(10);
        loginPanel.setAlignment(Pos.CENTER);

        // Création des composants
        Label userTagLabel = new Label("Tag utilisateur :");
        userTagLabel.setFont(Font.font("Arial", 14));

        mLoginUserTagField = new TextField();
        mLoginUserTagField.setPrefWidth(200);
        mLoginUserTagField.setPrefHeight(30);

        Label passwordLabel = new Label("Mot de passe :");
        passwordLabel.setFont(Font.font("Arial", 14));

        mLoginPasswordField = new PasswordField();
        mLoginPasswordField.setPrefWidth(200);
        mLoginPasswordField.setPrefHeight(30);

        mLoginButton = new Button("Se connecter");
        mLoginButton.setPrefSize(150, 40);
        mLoginButton.setOnAction(e -> login());

        // Ajout des composants au panel
        loginPanel.add(userTagLabel, 0, 0);
        loginPanel.add(mLoginUserTagField, 0, 1);
        loginPanel.add(passwordLabel, 0, 2);
        loginPanel.add(mLoginPasswordField, 0, 3);

        // Conteneur pour centrer le bouton
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().add(mLoginButton);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        loginPanel.add(buttonBox, 0, 4);

        return loginPanel;
    }

    /**
     * Création du panel d'inscription
     */
    protected GridPane createSignupPanel() {
        GridPane signupPanel = new GridPane();
        signupPanel.setPadding(new Insets(20));
        signupPanel.setHgap(10);
        signupPanel.setVgap(10);
        signupPanel.setAlignment(Pos.CENTER);

        // Création des composants
        Label nameLabel = new Label("Nom :");
        nameLabel.setFont(Font.font("Arial", 14));

        mSignupNameField = new TextField();
        mSignupNameField.setPrefWidth(200);
        mSignupNameField.setPrefHeight(30);

        Label tagLabel = new Label("Tag utilisateur :");
        tagLabel.setFont(Font.font("Arial", 14));

        mSignupTagField = new TextField();
        mSignupTagField.setPrefWidth(200);
        mSignupTagField.setPrefHeight(30);

        Label passwordLabel = new Label("Mot de passe :");
        passwordLabel.setFont(Font.font("Arial", 14));

        mSignupPasswordField = new PasswordField();
        mSignupPasswordField.setPrefWidth(200);
        mSignupPasswordField.setPrefHeight(30);

        Label avatarLabel = new Label("Avatar :");
        avatarLabel.setFont(Font.font("Arial", 14));

        mAvatarButton = new Button("Parcourir...");
        mAvatarButton.setOnAction(e -> {
            if (mActionListener != null) {
                String path = mActionListener.onAvatarSelectionRequested();
                if (path != null) {
                    // L'interaction avec l'interface doit se faire sur le thread JavaFX
                    final String finalPath = path;
                    Platform.runLater(() -> updateAvatarPath(finalPath));
                    mAvatarPath = path;
                }
            }
        });

        mAvatarLabel = new Label("Aucun avatar sélectionné");
        mAvatarLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));

        mAvatarImageView = new ImageView();
        mAvatarImageView.setFitHeight(100);
        mAvatarImageView.setFitWidth(100);
        mAvatarImageView.setPreserveRatio(true);

        mSignupButton = new Button("S'inscrire");
        mSignupButton.setPrefSize(150, 40);
        mSignupButton.setOnAction(e -> signup());

        // Ajout des composants au panel
        signupPanel.add(nameLabel, 0, 0);
        signupPanel.add(mSignupNameField, 0, 1);
        signupPanel.add(tagLabel, 0, 2);
        signupPanel.add(mSignupTagField, 0, 3);
        signupPanel.add(passwordLabel, 0, 4);
        signupPanel.add(mSignupPasswordField, 0, 5);
        signupPanel.add(avatarLabel, 0, 6);

        // Panel pour avatar avec bouton et label
        HBox avatarPanel = new HBox(10);
        avatarPanel.getChildren().addAll(mAvatarButton, mAvatarLabel);
        signupPanel.add(avatarPanel, 0, 7);
        signupPanel.add(mAvatarImageView, 0, 8);

        // Conteneur pour centrer le bouton
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().add(mSignupButton);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        signupPanel.add(buttonBox, 0, 9);

        return signupPanel;
    }

    /**
     * Action de connexion
     */
    protected void login() {
        String userTag = mLoginUserTagField.getText().trim();
        String password = mLoginPasswordField.getText();

        if (mActionListener != null) {
            mActionListener.onLoginRequested(userTag, password);
        }
    }

    /**
     * Action d'inscription
     */
    protected void signup() {
        String name = mSignupNameField.getText().trim();
        String tag = mSignupTagField.getText().trim();
        String password = mSignupPasswordField.getText();

        // Vérification des champs obligatoires
        if (name.isEmpty() || tag.isEmpty() || password.isEmpty()) {
            showError("Erreur d'inscription", "Tous les champs sont obligatoires");
            return;
        }

        // Si l'avatar n'est pas sélectionné, envoyer une chaîne vide
        String avatarPath = mAvatarPath;
        if (avatarPath == null) {
            avatarPath = "";
        }

        if (mActionListener != null) {
            mActionListener.onSignupRequested(name, tag, password, avatarPath);
        }
    }

    @Override
    public JComponent getComponent() {
        return mMainPanel;
    }

    @Override
    public void resetLoginFields() {
        // L'accès aux composants JavaFX doit se faire sur le thread JavaFX
        Platform.runLater(() -> {
            mLoginUserTagField.setText("");
            mLoginPasswordField.setText("");
        });
    }

    @Override
    public void resetSignupFields() {
        // L'accès aux composants JavaFX doit se faire sur le thread JavaFX
        Platform.runLater(() -> {
            mSignupNameField.setText("");
            mSignupTagField.setText("");
            mSignupPasswordField.setText("");
            mAvatarLabel.setText("Aucun avatar sélectionné");
            mAvatarImageView.setImage(null);
            mAvatarPath = null;
        });
    }

    @Override
    public void updateAvatarPath(String avatarPath) {
        if (avatarPath != null && !avatarPath.isEmpty()) {
            // Affichage du nom de fichier
            String fileName = avatarPath.substring(avatarPath.lastIndexOf(File.separator) + 1);
            mAvatarLabel.setText(fileName);

            // Affichage de l'image
            try {
                File file = new File(avatarPath);
                Image image = new Image(file.toURI().toString());
                mAvatarImageView.setImage(image);
            } catch (Exception e) {
                mAvatarImageView.setImage(null);
                System.err.println("Erreur lors du chargement de l'image: " + e.getMessage());
            }
        } else {
            mAvatarLabel.setText("Aucun avatar sélectionné");
            mAvatarImageView.setImage(null);
        }
    }

    public void setActionListener(ILoginViewActionListener listener) {
        this.mActionListener = listener;
    }

    @Override
    public void notifyLogoutError() {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                    mMainPanel,
                    "Un problème est survenu lors de la déconnexion. " +
                            "Veuillez redémarrer l'application si vous rencontrez des problèmes.",
                    "Erreur de session",
                    JOptionPane.ERROR_MESSAGE
            );
        });
    }

    /**
     * Affiche une boîte de dialogue d'erreur
     */
    public void showError(String title, String message) {
        // Utiliser SwingUtilities pour afficher la boîte de dialogue Swing
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                    mMainPanel,
                    message,
                    title,
                    JOptionPane.ERROR_MESSAGE
            );
        });
    }
}