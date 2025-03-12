package main.java.com.ubo.tp.message.ihm.component.home;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.MessageApp;
import main.java.com.ubo.tp.message.ihm.NotificationManager;
import main.java.com.ubo.tp.message.ihm.component.IView;

/**
 * Classe de la vue d'accueil de l'application en hybride Swing/JavaFX.
 */
public class HomeView extends JPanel implements IView {

    private static final long serialVersionUID = 1L;

    /**
     * Panel JavaFX embarqué dans Swing
     */
    protected JFXPanel jfxPanel;

    /**
     * Écouteur d'actions
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

    // Composants JavaFX
    protected VBox mUserInfoPanel;
    protected Label mUserNameLabel;
    protected Label mUserTagLabel;
    protected ImageView mAvatarImageView;
    protected Circle mAvatarCircle;
    protected Label mAvatarFallbackLabel;
    protected StackPane mAvatarContainer;

    // Badge de messages non lus
    protected Label mUnreadBadgeLabel;
    protected StackPane messagesCardWithBadge;

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
        // Configuration du layout Swing
        this.setLayout(new BorderLayout());

        // Création du panel JavaFX
        jfxPanel = new JFXPanel();
        this.add(jfxPanel, BorderLayout.CENTER);

        // Initialiser le contenu JavaFX sur le thread JavaFX
        Platform.runLater(() -> createJavaFXContent());

        // Enregistrer l'écouteur pour les messages non lus
        NotificationManager.getInstance().addListener(this::updateUnreadBadge);
    }

    /**
     * Création du contenu JavaFX
     */
    private void createJavaFXContent() {
        // Création du panel principal
        BorderPane mainPane = new BorderPane();
        mainPane.setPadding(new Insets(30));
        mainPane.setStyle("-fx-background-color: #F5F5FA;"); // Fond légèrement gris bleuté

        // En-tête avec le logo et le titre
        HBox headerBox = new HBox(20);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        // Logo de l'application
        ImageView logoImageView = createImageView("/images/logo_message.png", 64, 64);
        if (logoImageView == null) {
            Label logoLabel = new Label("*logo*");
            logoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
            headerBox.getChildren().add(logoLabel);
        } else {
            headerBox.getChildren().add(logoImageView);
        }

        // Titre de l'application
        Label titleLabel = new Label("Bienvenue sur MessageApp");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        headerBox.getChildren().add(titleLabel);

        // Espace flexible pour pousser le panel utilisateur à droite
        HBox.setHgrow(createSpacer(), Priority.ALWAYS);
        headerBox.getChildren().add(createSpacer());

        // Panel d'informations utilisateur (visible uniquement lorsqu'un utilisateur est connecté)
        mUserInfoPanel = new VBox(10);
        mUserInfoPanel.setAlignment(Pos.CENTER);
        mUserInfoPanel.setPadding(new Insets(10));
        mUserInfoPanel.setStyle("-fx-background-color: #F0F0FF; -fx-border-color: #C8C8FF; -fx-border-width: 1; -fx-border-radius: 5;");

        // Avatar de l'utilisateur
        mAvatarContainer = new StackPane();
        mAvatarContainer.setMinSize(80, 80);
        mAvatarContainer.setMaxSize(80, 80);

        mAvatarCircle = new Circle(40);
        mAvatarCircle.setFill(Color.LIGHTGRAY);

        mAvatarImageView = new ImageView();
        mAvatarImageView.setFitWidth(80);
        mAvatarImageView.setFitHeight(80);
        mAvatarImageView.setPreserveRatio(true);

        mAvatarFallbackLabel = new Label("?");
        mAvatarFallbackLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        mAvatarFallbackLabel.setTextFill(Color.DARKGRAY);

        mAvatarContainer.getChildren().addAll(mAvatarCircle, mAvatarImageView, mAvatarFallbackLabel);

        // Nom de l'utilisateur
        mUserNameLabel = new Label("Nom d'utilisateur");
        mUserNameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        // Tag de l'utilisateur
        mUserTagLabel = new Label("@tag");
        mUserTagLabel.setFont(Font.font("Arial", FontPosture.ITALIC, 14));
        mUserTagLabel.setTextFill(Color.DARKGRAY);

        // Assemblage du panel utilisateur
        mUserInfoPanel.getChildren().addAll(mAvatarContainer, mUserNameLabel, mUserTagLabel);

        // Masquer initialement le panel utilisateur
        mUserInfoPanel.setVisible(false);

        // Ajout du panel utilisateur à l'en-tête
        headerBox.getChildren().add(mUserInfoPanel);

        // Ajout de l'en-tête au panel principal
        mainPane.setTop(headerBox);

        // Panel central avec les cartes de navigation
        GridPane navigationGrid = new GridPane();
        navigationGrid.setHgap(20);
        navigationGrid.setVgap(20);
        navigationGrid.setAlignment(Pos.CENTER);

        // Carte pour accéder au profil
        VBox profileCard = createNavigationCard(
                "Mon Profil",
                "Consultez et modifiez votre profil utilisateur",
                "/images/user.png",
                () -> {
                    if (mActionListener != null) {
                        mActionListener.onShowProfileRequested();
                    }
                }
        );

        // Carte pour accéder aux messages avec badge
        VBox messagesCard = createNavigationCard(
                "Messages",
                "Consultez et envoyez des messages",
                "/images/message.png",
                () -> {
                    if (mActionListener != null) {
                        mActionListener.onShowMessagesRequested();
                    }
                }
        );

        // Créer le badge pour les messages non lus
        mUnreadBadgeLabel = new Label("0");
        mUnreadBadgeLabel.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-padding: 2 6; -fx-background-radius: 10;");
        mUnreadBadgeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        mUnreadBadgeLabel.setVisible(false); // Masqué par défaut

        // Créer un container pour positionner le badge en haut à droite
        messagesCardWithBadge = new StackPane();
        messagesCardWithBadge.getChildren().add(messagesCard);
        StackPane.setAlignment(mUnreadBadgeLabel, Pos.TOP_RIGHT);
        messagesCardWithBadge.getChildren().add(mUnreadBadgeLabel);

        // Carte pour accéder à la liste des utilisateurs
        VBox usersCard = createNavigationCard(
                "Utilisateurs",
                "Consultez la liste des utilisateurs et gérez vos abonnements",
                "/images/logo_20.png",
                () -> {
                    if (mActionListener != null) {
                        mActionListener.onShowUserListRequested();
                    }
                }
        );

        // Carte pour se déconnecter
        VBox logoutCard = createNavigationCard(
                "Déconnexion",
                "Déconnectez-vous de votre compte",
                "/images/logout.png",
                () -> {
                    if (mActionListener != null) {
                        mActionListener.onLogoutRequested();
                    }
                }
        );

        // Ajout des cartes au panel de navigation
        navigationGrid.add(profileCard, 0, 0);
        navigationGrid.add(messagesCardWithBadge, 1, 0);
        navigationGrid.add(usersCard, 0, 1);
        navigationGrid.add(logoutCard, 1, 1);

        // Ajout du panel de navigation au panel principal
        mainPane.setCenter(navigationGrid);

        // Pied de page
        Label footerLabel = new Label("© 2025 MessageApp - M2-TIIL UBO");
        footerLabel.setFont(Font.font("Arial", FontPosture.ITALIC, 12));
        footerLabel.setTextFill(Color.GRAY);
        footerLabel.setPadding(new Insets(20, 0, 0, 0));
        footerLabel.setAlignment(Pos.CENTER);
        footerLabel.setMaxWidth(Double.MAX_VALUE);

        // Ajout du pied de page au panel principal
        mainPane.setBottom(footerLabel);

        // Création de la scène JavaFX
        Scene scene = new Scene(mainPane);
        jfxPanel.setScene(scene);
    }

    /**
     * Crée un espace flexible
     */
    private javafx.scene.Node createSpacer() {
        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    /**
     * Crée une carte de navigation
     *
     * @param title Titre de la carte
     * @param description Description de la carte
     * @param iconPath Chemin vers l'icône
     * @param action Action à exécuter au clic
     * @return Une carte de navigation
     */
    protected VBox createNavigationCard(String title, String description, String iconPath, Runnable action) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-border-color: #C8C8FF; -fx-border-width: 1; -fx-border-radius: 5;");
        card.setMinWidth(250);
        card.setMinHeight(180);

        // En-tête avec icône et titre
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        // Icône de la carte
        ImageView iconView = createImageView(iconPath, 48, 48);
        if (iconView == null) {
            Label iconPlaceholder = new Label("");
            iconPlaceholder.setMinWidth(48);
            header.getChildren().add(iconPlaceholder);
        } else {
            header.getChildren().add(iconView);
        }

        // Titre de la carte
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        header.getChildren().add(titleLabel);

        // Description de la carte
        Label descLabel = new Label(description);
        descLabel.setFont(Font.font("Arial", 14));
        descLabel.setWrapText(true);

        // Bouton pour activer la carte
        Button actionButton = new Button("Accéder");
        actionButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        actionButton.setStyle("-fx-background-color: #E6E6FA; -fx-text-fill: #323264;");
        actionButton.setOnAction(e -> action.run());
        actionButton.setMaxWidth(Double.MAX_VALUE);

        // Espace flexible pour pousser le bouton vers le bas
        VBox.setVgrow(createVerticalSpacer(), Priority.ALWAYS);

        // Assemblage de la carte
        card.getChildren().addAll(header, descLabel, createVerticalSpacer(), actionButton);

        return card;
    }

    /**
     * Crée un espace vertical flexible
     */
    private javafx.scene.Node createVerticalSpacer() {
        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    /**
     * Crée un ImageView à partir d'un chemin de ressource
     */
    private ImageView createImageView(String resourcePath, int width, int height) {
        try {
            String correctedPath = resourcePath;
            if (!correctedPath.startsWith("/")) {
                correctedPath = "/" + correctedPath;
            }

            java.net.URL resourceURL = getClass().getResource(correctedPath);

            if (resourceURL != null) {
                javafx.scene.image.Image image = new javafx.scene.image.Image(resourceURL.toExternalForm(), width, height, true, true);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(width);
                imageView.setFitHeight(height);
                imageView.setPreserveRatio(true);
                return imageView;
            } else {
                System.err.println("Resource not found: " + resourcePath);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error loading icon: " + e.getMessage());
            return null;
        }
    }

    /**
     * Met à jour les informations utilisateur
     *
     * @param user Utilisateur connecté
     */
    public void updateUserInfo(User user) {
        Platform.runLater(() -> {
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
                            javafx.scene.image.Image avatar = new javafx.scene.image.Image(avatarFile.toURI().toString(),
                                    80, 80, true, true);
                            mAvatarImageView.setImage(avatar);
                            mAvatarFallbackLabel.setVisible(false);
                        } catch (Exception e) {
                            mAvatarImageView.setImage(null);
                            mAvatarFallbackLabel.setVisible(true);
                        }
                    } else {
                        mAvatarImageView.setImage(null);
                        mAvatarFallbackLabel.setVisible(true);
                    }
                } else {
                    mAvatarImageView.setImage(null);
                    mAvatarFallbackLabel.setVisible(true);
                }

                // Afficher le panel utilisateur
                mUserInfoPanel.setVisible(true);
            } else {
                // Masquer le panel utilisateur si aucun utilisateur n'est connecté
                mUserInfoPanel.setVisible(false);
            }
        });
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
        Platform.runLater(() -> {
            if (count > 0) {
                mUnreadBadgeLabel.setText(String.valueOf(count));
                mUnreadBadgeLabel.setVisible(true);
            } else {
                mUnreadBadgeLabel.setVisible(false);
            }
        });
    }
}