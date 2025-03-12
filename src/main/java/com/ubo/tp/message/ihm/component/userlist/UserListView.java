package main.java.com.ubo.tp.message.ihm.component.userlist;

import java.awt.BorderLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.swing.JComponent;
import javax.swing.JPanel;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import main.java.com.ubo.tp.message.core.event.EventManager;
import main.java.com.ubo.tp.message.core.event.NavigationEvents;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.component.AbstractComponent;

/**
 * Vue de la liste des utilisateurs enregistrés en JavaFX
 */
public class UserListView extends AbstractComponent implements IUserListView {

    /**
     * Taille de l'avatar
     */
    protected static final int AVATAR_SIZE = 60;

    /**
     * Panel principal Swing
     */
    protected JPanel mMainPanel;

    /**
     * Panel JavaFX
     */
    protected JFXPanel jfxPanel;

    /**
     * Champ de recherche
     */
    protected TextField mSearchField;

    /**
     * Container JavaFX pour la liste des utilisateurs
     */
    protected VBox mUsersContainer;

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
     * Constructeur
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
        // Panel principal
        BorderPane mainPane = new BorderPane();
        mainPane.setPadding(new Insets(10));
        mainPane.setStyle("-fx-background-color: #f8f9fa;");

        // Barre supérieure avec titre et bouton accueil
        HBox topBar = createTopBar();
        mainPane.setTop(topBar);

        // Zone de la liste des utilisateurs
        createUsersListArea(mainPane);

        // Zone de recherche
        HBox searchBar = createSearchBar();
        mainPane.setBottom(searchBar);

        // Création de la scène JavaFX
        Scene scene = new Scene(mainPane);
        jfxPanel.setScene(scene);
    }

    /**
     * Création de la barre supérieure
     */
    private HBox createTopBar() {
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(0, 0, 10, 0));
        topBar.setAlignment(Pos.CENTER);

        // Titre
        Label titleLabel = new Label("Liste des utilisateurs");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: #2c3e50;");
        HBox.setHgrow(titleLabel, Priority.ALWAYS);
        titleLabel.setAlignment(Pos.CENTER);

        // Bouton Page d'accueil
        Button homeButton = new Button("Page d'accueil");
        homeButton.setStyle(
                "-fx-background-color: #e6e6fa; " +
                        "-fx-text-fill: #323264; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-size: 12px; " +
                        "-fx-padding: 8px 15px; " +
                        "-fx-background-radius: 5px;"
        );
        homeButton.setOnAction(e -> {
            // Émission d'un événement pour retourner à la page d'accueil
            EventManager.getInstance().fireEvent(new NavigationEvents.ShowMainViewEvent());
        });

        topBar.getChildren().addAll(titleLabel, homeButton);

        return topBar;
    }

    /**
     * Création de la zone de liste des utilisateurs
     */
    private void createUsersListArea(BorderPane parent) {
        // Container pour les utilisateurs
        mUsersContainer = new VBox(10);
        mUsersContainer.setPadding(new Insets(10));

        // ScrollPane pour faire défiler les utilisateurs
        ScrollPane scrollPane = new ScrollPane(mUsersContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle(
                "-fx-background: white; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-background-color: white; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-background-radius: 5px;"
        );

        parent.setCenter(scrollPane);
    }

    /**
     * Création de la barre de recherche
     */
    private HBox createSearchBar() {
        HBox searchBar = new HBox(10);
        searchBar.setPadding(new Insets(10, 0, 0, 0));
        searchBar.setAlignment(Pos.CENTER);

        // Label
        Label searchLabel = new Label("Rechercher : ");
        searchLabel.setFont(Font.font("Arial", 14));

        // Champ de recherche
        mSearchField = new TextField();
        mSearchField.setPrefWidth(200);
        mSearchField.setStyle("-fx-padding: 5px; -fx-font-size: 14px;");

        // Écouteur pour la recherche en temps réel
        mSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (mActionListener != null) {
                mActionListener.onSearchUsersRequested(newValue.trim());
            }
        });

        // Bouton de recherche
        Button searchButton = new Button("Rechercher");
        searchButton.setStyle(
                "-fx-background-color: #f8f9fa; " +
                        "-fx-text-fill: #495057; " +
                        "-fx-border-color: #ced4da; " +
                        "-fx-border-radius: 3px; " +
                        "-fx-padding: 5px 10px;"
        );
        searchButton.setOnAction(e -> {
            if (mActionListener != null) {
                mActionListener.onSearchUsersRequested(mSearchField.getText().trim());
            }
        });

        // Bouton de réinitialisation
        Button resetButton = new Button("Réinitialiser");
        resetButton.setStyle(
                "-fx-background-color: #f8f9fa; " +
                        "-fx-text-fill: #495057; " +
                        "-fx-border-color: #ced4da; " +
                        "-fx-border-radius: 3px; " +
                        "-fx-padding: 5px 10px;"
        );
        resetButton.setOnAction(e -> {
            mSearchField.setText("");
            if (mActionListener != null) {
                mActionListener.onRefreshUserListRequested();
            }
        });

        searchBar.getChildren().addAll(searchLabel, mSearchField, searchButton, resetButton);

        return searchBar;
    }

    /**
     * Crée un panel pour un utilisateur avec ses statistiques
     */
    protected HBox createUserPanel(User user, int followersCount, int followingCount) {
        // Panel principal pour l'utilisateur
        HBox userBox = new HBox(15);
        userBox.setPadding(new Insets(10));
        userBox.setStyle(
                "-fx-background-color: white; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-width: 0 0 1 0; " +
                        "-fx-background-radius: 5px;"
        );
        userBox.setMinHeight(AVATAR_SIZE + 20);

        // Avatar avec effet circulaire
        StackPane avatarPane = new StackPane();
        avatarPane.setMinSize(AVATAR_SIZE, AVATAR_SIZE);
        avatarPane.setMaxSize(AVATAR_SIZE, AVATAR_SIZE);

        Circle avatarCircle = new Circle(AVATAR_SIZE / 2);
        avatarCircle.setFill(Color.LIGHTGRAY);

        // Effet d'ombre pour l'avatar
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(0);
        dropShadow.setOffsetY(0);
        dropShadow.setColor(Color.gray(0.5, 0.3));
        avatarCircle.setEffect(dropShadow);

        ImageView avatarImageView = new ImageView();
        avatarImageView.setFitWidth(AVATAR_SIZE);
        avatarImageView.setFitHeight(AVATAR_SIZE);
        avatarImageView.setPreserveRatio(true);

        // Label de repli pour l'avatar
        Label avatarFallbackLabel = new Label("?");
        avatarFallbackLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        avatarFallbackLabel.setStyle("-fx-text-fill: #6c757d;");

        // Chargement de l'avatar si disponible
        if (user.getAvatarPath() != null && !user.getAvatarPath().isEmpty()) {
            File avatarFile = new File(user.getAvatarPath());
            if (avatarFile.exists()) {
                try {
                    Image avatarImage = new Image(avatarFile.toURI().toString(),
                            AVATAR_SIZE, AVATAR_SIZE, true, true);
                    avatarImageView.setImage(avatarImage);
                    avatarImageView.setClip(new Circle(AVATAR_SIZE/2, AVATAR_SIZE/2, AVATAR_SIZE/2));
                    avatarFallbackLabel.setVisible(false);
                } catch (Exception e) {
                    avatarImageView.setImage(null);
                    avatarFallbackLabel.setVisible(true);
                }
            } else {
                avatarImageView.setImage(null);
                avatarFallbackLabel.setVisible(true);
            }
        } else {
            avatarImageView.setImage(null);
            avatarFallbackLabel.setVisible(true);
        }

        avatarPane.getChildren().addAll(avatarCircle, avatarImageView, avatarFallbackLabel);

        // Informations sur l'utilisateur
        VBox userInfo = new VBox(5);
        userInfo.setAlignment(Pos.CENTER_LEFT);

        // Nom de l'utilisateur
        Label nameLabel = new Label(user.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        nameLabel.setStyle("-fx-text-fill: #2c3e50;");

        // Tag de l'utilisateur
        Label tagLabel = new Label("@" + user.getUserTag());
        tagLabel.setFont(Font.font("Arial", FontPosture.ITALIC, 12));
        tagLabel.setStyle("-fx-text-fill: #6c757d;");

        // Statistiques (followers et following)
        HBox statsBox = new HBox(10);

        Label followersLabel = new Label(followersCount + " abonnés");
        followersLabel.setFont(Font.font("Arial", 11));
        followersLabel.setStyle("-fx-text-fill: #6c757d;");

        Label followingLabel = new Label(followingCount + " abonnements");
        followingLabel.setFont(Font.font("Arial", 11));
        followingLabel.setStyle("-fx-text-fill: #6c757d;");

        statsBox.getChildren().addAll(followersLabel, new Label("·"), followingLabel);

        // Assemblage des infos utilisateur
        userInfo.getChildren().addAll(nameLabel, tagLabel, statsBox);
        HBox.setHgrow(userInfo, Priority.ALWAYS);

        // Boutons de suivi/non-suivi
        VBox buttonsBox = new VBox(5);
        buttonsBox.setAlignment(Pos.CENTER);

        // Bouton Suivre
        Button followButton = new Button("Suivre");
        followButton.setStyle(
                "-fx-background-color: white; " +
                        "-fx-text-fill: #1e90ff; " +
                        "-fx-border-color: #1e90ff; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 5; " +
                        "-fx-padding: 5 15; " +
                        "-fx-font-weight: bold;"
        );
        followButton.setOnAction(e -> {
            if (mActionListener != null) {
                mActionListener.onFollowUserRequested(user);
            }
        });

        // Bouton Ne plus suivre
        Button unfollowButton = new Button("Ne plus suivre");
        unfollowButton.setStyle(
                "-fx-background-color: white; " +
                        "-fx-text-fill: #dc143c; " +
                        "-fx-border-color: #dc143c; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 5; " +
                        "-fx-padding: 5 15; " +
                        "-fx-font-weight: bold;"
        );
        unfollowButton.setOnAction(e -> {
            if (mActionListener != null) {
                mActionListener.onUnfollowUserRequested(user);
            }
        });

        // Par défaut, cacher les boutons (ils seront configurés dans updateFollowStatus)
        followButton.setVisible(false);
        unfollowButton.setVisible(false);

        buttonsBox.getChildren().addAll(followButton, unfollowButton);

        // Stocker les boutons pour cet utilisateur
        mUserButtons.put(user.getUuid(), new UserFollowButtons(followButton, unfollowButton));

        // Assemblage du panel utilisateur
        userBox.getChildren().addAll(avatarPane, userInfo, buttonsBox);

        return userBox;
    }

    @Override
    public void updateUserList(Set<User> users, Map<UUID, Integer> followersCountMap, Map<UUID, Integer> followingCountMap) {
        Platform.runLater(() -> {
            // Vider la liste actuelle
            mUsersContainer.getChildren().clear();
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
                // Récupérer le nombre de followers et following depuis les maps
                int followersCount = followersCountMap.getOrDefault(user.getUuid(), 0);
                int followingCount = followingCountMap.getOrDefault(user.getUuid(), 0);

                mDisplayedUsers.add(user);
                HBox userPanel = createUserPanel(user, followersCount, followingCount);
                mUsersContainer.getChildren().add(userPanel);
            }

            // Si aucun utilisateur
            if (mDisplayedUsers.isEmpty()) {
                VBox emptyBox = new VBox();
                emptyBox.setAlignment(Pos.CENTER);
                emptyBox.setPadding(new Insets(30));

                Label emptyLabel = new Label("Aucun utilisateur trouvé");
                emptyLabel.setFont(Font.font("Arial", FontPosture.ITALIC, 14));
                emptyLabel.setStyle("-fx-text-fill: #6c757d;");

                emptyBox.getChildren().add(emptyLabel);
                mUsersContainer.getChildren().add(emptyBox);
            }
        });
    }

    @Override
    public void updateFollowStatus(User connectedUser) {
        if (connectedUser == null) {
            System.out.println("Impossible de mettre à jour le statut d'abonnement : utilisateur connecté null");
            return;
        }

        Platform.runLater(() -> {
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
        private final Button followButton;
        private final Button unfollowButton;

        public UserFollowButtons(Button followButton, Button unfollowButton) {
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