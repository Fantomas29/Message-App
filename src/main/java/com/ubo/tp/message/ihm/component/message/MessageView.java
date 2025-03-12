package main.java.com.ubo.tp.message.ihm.component.message;

import java.awt.BorderLayout;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JPanel;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
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
import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.component.AbstractComponent;

/**
 * Vue pour la gestion des messages en JavaFX
 */
public class MessageView extends AbstractComponent implements IMessageView {

    /**
     * Taille de l'avatar
     */
    protected static final int AVATAR_SIZE = 40;

    /**
     * Panel principal Swing
     */
    protected JPanel mMainPanel;

    /**
     * Panel JavaFX
     */
    protected JFXPanel jfxPanel;

    /**
     * Référence au contrôleur
     */
    private IMessageViewActionListener mActionListener;

    /**
     * Session de l'application
     */
    protected ISession mSession;

    // Composants JavaFX
    protected VBox mMessagesContainer;
    protected ScrollPane mScrollPane;
    protected TextField mSearchField;
    protected TextArea mMessageField;
    protected Label mCharCountLabel;
    protected Button mSendButton;

    /**
     * Format de date pour l'affichage des messages
     */
    protected SimpleDateFormat mDateFormat;

    /**
     * Liste des messages affichés
     */
    protected List<Message> mDisplayedMessages;

    /**
     * Constructeur
     */
    public MessageView() {
        this.mDisplayedMessages = new ArrayList<>();
        this.mDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        // Initialisation des composants graphiques
        this.initComponents();
    }

    /**
     * Définit la session de l'application
     *
     * @param session Session de l'application
     */
    public void setSession(ISession session) {
        this.mSession = session;
    }

    /**
     * Initialisation des composants graphiques
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

        // Container principal pour les messages et la saisie
        VBox centerContainer = new VBox(10);

        // Zone des messages
        createMessagesArea(centerContainer);

        // Zone de saisie de nouveau message
        createMessageInputArea(centerContainer);

        mainPane.setCenter(centerContainer);

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
        Label titleLabel = new Label("Messages");
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
     * Création de la zone des messages
     */
    private void createMessagesArea(VBox parent) {
        // Container pour les messages
        mMessagesContainer = new VBox(10);
        mMessagesContainer.setPadding(new Insets(10));

        // ScrollPane pour faire défiler les messages
        mScrollPane = new ScrollPane(mMessagesContainer);
        mScrollPane.setFitToWidth(true);
        mScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        mScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mScrollPane.setPrefHeight(400);  // Hauteur préférée
        mScrollPane.setStyle(
                "-fx-background: white; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-background-color: white; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-background-radius: 5px;"
        );

        // Pour faire défiler automatiquement vers le bas quand de nouveaux messages sont ajoutés
        mMessagesContainer.heightProperty().addListener((observable, oldValue, newValue) -> {
            mScrollPane.setVvalue(1.0);
        });

        VBox.setVgrow(mScrollPane, Priority.ALWAYS);
        parent.getChildren().add(mScrollPane);
    }

    /**
     * Création de la zone de saisie de message
     */
    private void createMessageInputArea(VBox parent) {
        VBox messageInputBox = new VBox(5);
        messageInputBox.setPadding(new Insets(10));
        messageInputBox.setStyle(
                "-fx-background-color: white; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-background-radius: 5px;"
        );

        // Titre
        Label inputTitle = new Label("Nouveau message");
        inputTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        inputTitle.setStyle("-fx-text-fill: #2c3e50;");

        // Champ de saisie
        mMessageField = new TextArea();
        mMessageField.setPrefRowCount(3);
        mMessageField.setWrapText(true);
        mMessageField.setStyle(
                "-fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 3px; " +
                        "-fx-font-size: 14px;"
        );

        // Écouteur pour la mise à jour du compteur de caractères
        mMessageField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateCharacterCount();
        });

        // Barre inférieure avec compteur et bouton
        HBox bottomBar = new HBox(10);
        bottomBar.setPadding(new Insets(5, 0, 0, 0));
        bottomBar.setAlignment(Pos.CENTER_RIGHT);

        // Compteur de caractères
        mCharCountLabel = new Label("0/200");
        mCharCountLabel.setFont(Font.font("Arial", FontPosture.ITALIC, 12));

        // Bouton d'envoi
        mSendButton = new Button("Envoyer");
        mSendButton.setStyle(
                "-fx-background-color: #4e73df; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 8px 15px; " +
                        "-fx-background-radius: 5px;"
        );
        mSendButton.setOnAction(e -> {
            if (mActionListener != null) {
                mActionListener.onSendMessageRequested(mMessageField.getText());
            }
        });

        // Ajout du spacer pour pousser le compteur à gauche et le bouton à droite
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        bottomBar.getChildren().addAll(mCharCountLabel, spacer, mSendButton);

        // Assemblage du panel de saisie
        messageInputBox.getChildren().addAll(inputTitle, mMessageField, bottomBar);
        parent.getChildren().add(messageInputBox);
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
                mActionListener.onSearchMessagesRequested(newValue.trim());
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
                mActionListener.onSearchMessagesRequested(mSearchField.getText().trim());
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
                mActionListener.onRefreshMessagesRequested();
            }
        });

        searchBar.getChildren().addAll(searchLabel, mSearchField, searchButton, resetButton);

        return searchBar;
    }

    /**
     * Met à jour le compteur de caractères
     */
    protected void updateCharacterCount() {
        int count = mMessageField.getText().length();
        mCharCountLabel.setText(count + "/200");

        // Changer la couleur selon le nombre de caractères
        if (count > 200) {
            mCharCountLabel.setStyle("-fx-text-fill: red;");
            mSendButton.setDisable(true);
        } else {
            mCharCountLabel.setStyle("-fx-text-fill: black;");
            mSendButton.setDisable(false);
        }
    }

    /**
     * Crée un panel pour un message
     */
    protected VBox createMessagePanel(Message message) {
        // Panel principal pour le message
        VBox messageBox = new VBox(5);
        messageBox.setPadding(new Insets(10));

        // Récupération de l'utilisateur connecté
        User connectedUser = null;
        if (mSession != null) {
            connectedUser = mSession.getConnectedUser();
        }

        // Vérification si le message est de l'utilisateur connecté
        boolean isMyMessage = false;
        if (connectedUser != null && message.getSender() != null) {
            isMyMessage = connectedUser.getUuid().equals(message.getSender().getUuid());
        }

        // Style selon l'émetteur du message
        if (isMyMessage) {
            messageBox.setStyle(
                    "-fx-background-color: #e6ffe6; " +
                            "-fx-border-color: #32b432; " +
                            "-fx-border-width: 0 0 0 3; " +
                            "-fx-background-radius: 5px; " +
                            "-fx-border-radius: 5px;"
            );
        } else {
            messageBox.setStyle(
                    "-fx-background-color: #fafafa; " +
                            "-fx-border-color: #e0e0e0; " +
                            "-fx-border-width: 1; " +
                            "-fx-background-radius: 5px; " +
                            "-fx-border-radius: 5px;"
            );
        }

        // En-tête du message
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        // Avatar
        StackPane avatarPane = new StackPane();
        avatarPane.setMinSize(AVATAR_SIZE, AVATAR_SIZE);
        avatarPane.setMaxSize(AVATAR_SIZE, AVATAR_SIZE);

        // Récupération de l'utilisateur émetteur
        User sender = message.getSender();

        // Avatar avec effet circulaire
        Circle avatarCircle = new Circle(AVATAR_SIZE / 2);
        avatarCircle.setFill(Color.LIGHTGRAY);

        ImageView avatarImageView = new ImageView();
        avatarImageView.setFitWidth(AVATAR_SIZE);
        avatarImageView.setFitHeight(AVATAR_SIZE);
        avatarImageView.setPreserveRatio(true);

        // Label de repli pour l'avatar
        Label avatarFallbackLabel = new Label("?");
        avatarFallbackLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        avatarFallbackLabel.setStyle("-fx-text-fill: #6c757d;");

        // Chargement de l'avatar si disponible
        if (sender.getAvatarPath() != null && !sender.getAvatarPath().isEmpty()) {
            File avatarFile = new File(sender.getAvatarPath());
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

        // Informations sur l'émetteur
        VBox senderInfo = new VBox(2);

        // Nom de l'émetteur
        Label nameLabel = new Label(sender.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        nameLabel.setStyle("-fx-text-fill: #2c3e50;");

        // Tag de l'émetteur
        Label tagLabel = new Label("@" + sender.getUserTag());
        tagLabel.setFont(Font.font("Arial", FontPosture.ITALIC, 12));
        tagLabel.setStyle("-fx-text-fill: #6c757d;");

        senderInfo.getChildren().addAll(nameLabel, tagLabel);

        // Date du message
        Label dateLabel = new Label(mDateFormat.format(new Date(message.getEmissionDate())));
        dateLabel.setFont(Font.font("Arial", FontPosture.ITALIC, 11));
        dateLabel.setStyle("-fx-text-fill: #6c757d;");

        // Spacer pour pousser la date à droite
        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        // Assemblage de l'en-tête
        headerBox.getChildren().addAll(avatarPane, senderInfo, headerSpacer, dateLabel);

        // Zone de texte du message
        Text messageText = new Text(message.getText());
        messageText.setFont(Font.font("Arial", 14));

        TextFlow textFlow = new TextFlow(messageText);
        textFlow.setPadding(new Insets(5, 5, 5, 5 + AVATAR_SIZE + 10)); // Indentation pour aligner avec l'en-tête

        // Assemblage du panel de message
        messageBox.getChildren().addAll(headerBox, textFlow);

        return messageBox;
    }

    @Override
    public void updateMessageList(Set<Message> messages) {
        Platform.runLater(() -> {
            // Vider la liste actuelle
            mMessagesContainer.getChildren().clear();
            mDisplayedMessages.clear();

            // Convertir le Set en List pour pouvoir trier
            List<Message> sortedMessages = new ArrayList<>(messages);

            // Trier les messages par date (du plus ancien au plus récent pour un affichage style chat)
            sortedMessages.sort((m1, m2) -> Long.compare(m1.getEmissionDate(), m2.getEmissionDate()));

            // Ajouter les messages
            for (Message message : sortedMessages) {
                mDisplayedMessages.add(message);
                VBox messagePanel = createMessagePanel(message);
                mMessagesContainer.getChildren().add(messagePanel);
            }

            // Si aucun message
            if (mDisplayedMessages.isEmpty()) {
                Label emptyLabel = new Label("Aucun message à afficher");
                emptyLabel.setFont(Font.font("Arial", FontPosture.ITALIC, 14));
                emptyLabel.setStyle("-fx-text-fill: #6c757d;");
                emptyLabel.setPadding(new Insets(20));
                emptyLabel.setMaxWidth(Double.MAX_VALUE);
                emptyLabel.setAlignment(Pos.CENTER);
                mMessagesContainer.getChildren().add(emptyLabel);
            }

            // Faire défiler automatiquement vers le bas pour voir les messages les plus récents
            mScrollPane.setVvalue(1.0);
        });
    }

    @Override
    public void resetMessageField() {
        Platform.runLater(() -> {
            mMessageField.setText("");
            updateCharacterCount();
        });
    }

    @Override
    public JComponent getComponent() {
        return mMainPanel;
    }

    public void setActionListener(IMessageViewActionListener listener) {
        this.mActionListener = listener;
    }
}