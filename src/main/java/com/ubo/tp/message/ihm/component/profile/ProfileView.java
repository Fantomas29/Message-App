package main.java.com.ubo.tp.message.ihm.component.profile;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.component.AbstractComponent;

/**
 * Panel de consultation et de modification du profil utilisateur
 * Version hybride Swing/JavaFX
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
     * Panel principal Swing
     */
    protected JPanel mMainPanel;

    /**
     * Panel JavaFX
     */
    protected JFXPanel jfxPanel;

    // Composants JavaFX
    protected Label mUserTagLabel;
    protected TextField mNameField;
    protected PasswordField mCurrentPasswordField;
    protected PasswordField mNewPasswordField;
    protected ImageView mAvatarImageView;
    protected Label mAvatarFallbackLabel;
    protected Button mAvatarButton;
    protected Button mSaveButton;
    protected Button mQuitButton;
    protected Circle mAvatarCircle;

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
        // Configuration du layout Swing
        mMainPanel = new JPanel(new BorderLayout());

        // Création du panel JavaFX
        jfxPanel = new JFXPanel();
        mMainPanel.add(jfxPanel, BorderLayout.CENTER);

        // Initialiser le contenu JavaFX sur le thread JavaFX
        Platform.runLater(() -> createJavaFXContent());
    }

    /**
     * Création du contenu JavaFX
     */
    private void createJavaFXContent() {
        // Panel principal avec un style moderne
        BorderPane mainPane = new BorderPane();
        mainPane.setPadding(new Insets(20));
        mainPane.setStyle("-fx-background-color: #f8f9fa;");

        // Titre du panneau
        Label titleLabel = new Label("Votre Profil");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setPadding(new Insets(0, 0, 20, 0));
        titleLabel.setStyle("-fx-text-fill: #2c3e50;");
        BorderPane.setAlignment(titleLabel, Pos.CENTER);

        // Ajout du titre
        mainPane.setTop(titleLabel);

        // Panel de contenu central
        HBox contentBox = new HBox(20);
        contentBox.setAlignment(Pos.CENTER);

        // Création du panel Avatar (à gauche)
        VBox avatarBox = createAvatarPanel();

        // Création du panel Informations (à droite)
        VBox infoBox = createInfoPanel();

        // Assemblage du contenu central
        contentBox.getChildren().addAll(avatarBox, infoBox);
        mainPane.setCenter(contentBox);

        // Panel de boutons (en bas)
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        // Bouton Enregistrer
        mSaveButton = new Button("Enregistrer");
        mSaveButton.setStyle(
                "-fx-background-color: #4e73df; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 8px 20px; " +
                        "-fx-background-radius: 5px;"
        );
        mSaveButton.setOnAction(e -> saveProfile());

        // Bouton Page d'accueil
        mQuitButton = new Button("Page d'accueil");
        mQuitButton.setStyle(
                "-fx-background-color: #e6e6fa; " +
                        "-fx-text-fill: #323264; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 8px 20px; " +
                        "-fx-background-radius: 5px;"
        );
        mQuitButton.setOnAction(e -> {
            if (mActionListener != null) {
                mActionListener.onReturnToMainViewRequested();
            }
        });

        // Ajout des boutons au panel
        buttonBox.getChildren().addAll(mSaveButton, mQuitButton);
        mainPane.setBottom(buttonBox);

        // Création de la scène JavaFX
        Scene scene = new Scene(mainPane);
        jfxPanel.setScene(scene);
    }

    /**
     * Création du panel Avatar
     */
    private VBox createAvatarPanel() {
        // Container principal
        VBox avatarBox = new VBox(15);
        avatarBox.setAlignment(Pos.CENTER);
        avatarBox.setPadding(new Insets(20));
        avatarBox.setMinWidth(220);
        avatarBox.setMaxWidth(220);
        avatarBox.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 10px; " +
                        "-fx-border-radius: 10px; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-width: 1px;"
        );

        // Titre
        Label avatarTitle = new Label("Avatar");
        avatarTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        avatarTitle.setStyle("-fx-text-fill: #2c3e50;");

        // Container pour l'avatar avec effet de cercle
        javafx.scene.layout.StackPane avatarContainer = new javafx.scene.layout.StackPane();
        avatarContainer.setMinSize(150, 150);
        avatarContainer.setMaxSize(150, 150);

        // Cercle pour la découpe de l'avatar
        mAvatarCircle = new Circle(75);
        mAvatarCircle.setFill(Color.LIGHTGRAY);

        // ImageView pour l'avatar
        mAvatarImageView = new ImageView();
        mAvatarImageView.setFitWidth(150);
        mAvatarImageView.setFitHeight(150);
        mAvatarImageView.setPreserveRatio(true);

        // Effet d'ombre pour l'avatar
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(10.0);
        dropShadow.setOffsetX(0);
        dropShadow.setOffsetY(0);
        dropShadow.setColor(Color.gray(0.5, 0.5));
        mAvatarCircle.setEffect(dropShadow);

        // Label de repli
        mAvatarFallbackLabel = new Label("?");
        mAvatarFallbackLabel.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        mAvatarFallbackLabel.setStyle("-fx-text-fill: #6c757d;");

        // Clip pour l'ImageView (forme circulaire)
        mAvatarImageView.setClip(new Circle(75, 75, 75));

        // Assembler le container d'avatar
        avatarContainer.getChildren().addAll(mAvatarCircle, mAvatarImageView, mAvatarFallbackLabel);

        // Bouton pour changer l'avatar
        mAvatarButton = new Button("Changer l'avatar");
        mAvatarButton.setStyle(
                "-fx-background-color: #f1f1f1; " +
                        "-fx-text-fill: #495057; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 8px 15px; " +
                        "-fx-background-radius: 5px;"
        );
        mAvatarButton.setMaxWidth(Double.MAX_VALUE);
        mAvatarButton.setOnAction(e -> {
            if (mActionListener != null) {
                String avatarPath = mActionListener.onAvatarSelectionRequested();
                if (avatarPath != null) {
                    mNewAvatarPath = avatarPath;
                    updateAvatarDisplay(mNewAvatarPath);
                }
            }
        });

        // Assemblage final
        avatarBox.getChildren().addAll(avatarTitle, avatarContainer, mAvatarButton);

        return avatarBox;
    }

    /**
     * Création du panel Informations
     */
    private VBox createInfoPanel() {
        // Container principal
        VBox infoBox = new VBox(10);
        infoBox.setPadding(new Insets(20));
        infoBox.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 10px; " +
                        "-fx-border-radius: 10px; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-width: 1px;"
        );

        // Titre
        Label infoTitle = new Label("Informations");
        infoTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        infoTitle.setStyle("-fx-text-fill: #2c3e50;");

        // Grille pour les champs
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(15);
        infoGrid.setVgap(15);
        infoGrid.setPadding(new Insets(15, 0, 0, 0));

        // Label pour le tag utilisateur
        Label tagLabel = new Label("Tag utilisateur:");
        tagLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        tagLabel.setStyle("-fx-text-fill: #495057;");

        // Champ pour le tag utilisateur (non modifiable)
        mUserTagLabel = new Label();
        mUserTagLabel.setFont(Font.font("Arial", 14));
        mUserTagLabel.setStyle("-fx-text-fill: #6c757d;");

        // Label pour le nom
        Label nameLabel = new Label("Nom:");
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        nameLabel.setStyle("-fx-text-fill: #495057;");

        // Champ pour le nom
        mNameField = new TextField();
        mNameField.setStyle("-fx-font-size: 14px; -fx-padding: 8px;");

        // Label pour le mot de passe actuel
        Label currentPasswordLabel = new Label("Mot de passe actuel:");
        currentPasswordLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        currentPasswordLabel.setStyle("-fx-text-fill: #495057;");

        // Champ pour le mot de passe actuel
        mCurrentPasswordField = new PasswordField();
        mCurrentPasswordField.setStyle("-fx-font-size: 14px; -fx-padding: 8px;");

        // Label pour le nouveau mot de passe
        Label newPasswordLabel = new Label("Nouveau mot de passe (optionnel):");
        newPasswordLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        newPasswordLabel.setStyle("-fx-text-fill: #495057;");

        // Champ pour le nouveau mot de passe
        mNewPasswordField = new PasswordField();
        mNewPasswordField.setStyle("-fx-font-size: 14px; -fx-padding: 8px;");

        // Ajout des composants à la grille
        infoGrid.add(tagLabel, 0, 0);
        infoGrid.add(mUserTagLabel, 1, 0);

        infoGrid.add(nameLabel, 0, 1);
        infoGrid.add(mNameField, 1, 1);

        infoGrid.add(currentPasswordLabel, 0, 2);
        infoGrid.add(mCurrentPasswordField, 1, 2);

        infoGrid.add(newPasswordLabel, 0, 3);
        infoGrid.add(mNewPasswordField, 1, 3);

        // Assemblage final
        infoBox.getChildren().addAll(infoTitle, infoGrid);

        return infoBox;
    }

    /**
     * Met à jour l'affichage de l'avatar
     */
    private void updateAvatarDisplay(String avatarPath) {
        if (avatarPath != null && !avatarPath.isEmpty()) {
            try {
                File avatarFile = new File(avatarPath);
                if (avatarFile.exists()) {
                    Image avatarImage = new Image(avatarFile.toURI().toString());
                    mAvatarImageView.setImage(avatarImage);
                    mAvatarFallbackLabel.setVisible(false);
                } else {
                    mAvatarImageView.setImage(null);
                    mAvatarFallbackLabel.setVisible(true);
                }
            } catch (Exception e) {
                mAvatarImageView.setImage(null);
                mAvatarFallbackLabel.setVisible(true);
                System.err.println("Erreur lors du chargement de l'avatar: " + e.getMessage());
            }
        } else {
            mAvatarImageView.setImage(null);
            mAvatarFallbackLabel.setVisible(true);
        }
    }

    /**
     * Enregistre les modifications du profil
     */
    protected void saveProfile() {
        // Récupération des valeurs
        String name = mNameField.getText().trim();
        String currentPassword = mCurrentPasswordField.getText();
        String newPassword = mNewPasswordField.getText();

        // Validation de base
        if (name.isEmpty()) {
            showError("Erreur", "Le nom ne peut pas être vide.");
            return;
        }

        if (currentPassword.isEmpty()) {
            showError("Erreur", "Veuillez saisir votre mot de passe actuel pour confirmer les modifications.");
            return;
        }

        if (mActionListener != null) {
            // Demande au contrôleur de mettre à jour le profil
            boolean success = mActionListener.onUpdateProfileRequested(name, currentPassword, newPassword, mNewAvatarPath);

            if (success) {
                // Réinitialisation des champs
                resetFields();

                // Affichage d'un message de succès
                Platform.runLater(() -> {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Succès");
                    alert.setHeaderText(null);
                    alert.setContentText("Votre profil a été mis à jour avec succès.");
                    alert.showAndWait();
                });
            }
        }
    }

    /**
     * Affiche une boîte de dialogue d'erreur
     */
    public void showError(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    @Override
    public void updateUserInfo(User connectedUser) {
        if (connectedUser != null) {
            this.mConnectedUser = connectedUser;
            this.mNewAvatarPath = connectedUser.getAvatarPath();

            Platform.runLater(() -> {
                // Affichage du tag utilisateur (non modifiable)
                mUserTagLabel.setText("@" + connectedUser.getUserTag());

                // Affichage du nom
                mNameField.setText(connectedUser.getName());

                // Vider les champs de mot de passe
                mCurrentPasswordField.setText("");
                mNewPasswordField.setText("");

                // Affichage de l'avatar
                updateAvatarDisplay(connectedUser.getAvatarPath());
            });
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