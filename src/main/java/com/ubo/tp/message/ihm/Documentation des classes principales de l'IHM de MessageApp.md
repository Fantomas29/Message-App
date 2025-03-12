# Explication du fonctionnement de MessageApp

## Vue d'ensemble de l'architecture

MessageApp est une application Java Swing organisée selon le pattern MVC (Modèle-Vue-Contrôleur) qui permet aux utilisateurs de s'inscrire, se connecter, publier des messages et suivre d'autres utilisateurs. L'application utilise un système d'événements centralisé pour faciliter la communication entre ses différents composants.

## Le système d'événements

Le cœur de l'application repose sur un système d'événements personnalisé qui permet un découplage efficace entre les composants. Voici comment il fonctionne:

### 1. Composants principaux du système d'événements

- **EventManager (Singleton)**: Un gestionnaire centralisé qui associe des types d'événements à leurs listeners et distribue les événements.
- **IEvent**: Interface marqueur que tous les événements implémentent.
- **IEventListener**: Interface pour les listeners qui réagissent aux événements.

```java
// Exemple d'utilisation du EventManager
// Enregistrement d'un listener
EventManager.getInstance().addListener(UserLoggedInEvent.class, event -> {
    // Traitement de l'événement
});

// Émission d'un événement
EventManager.getInstance().fireEvent(new UserLoggedInEvent(user));
```

### 2. Catégories d'événements

L'application définit plusieurs types d'événements organisés en classes:

- **NavigationEvents**: Gèrent la navigation entre les différentes vues
    - `ShowLoginViewEvent`
    - `ShowMainViewEvent`
    - `ShowProfileViewEvent`
    - `ShowUserListViewEvent`
    - `ShowMessageViewEvent`

- **SessionEvents**: Gèrent les changements liés à la session utilisateur
    - `UserLoggedInEvent`
    - `UserLoggedOutEvent`
    - `UserProfileUpdatedEvent`

- **MessageEvents**: Gèrent les notifications de nouveaux messages
    - `FollowedUserMessageEvent`

## Flux de communication dans l'application

La communication entre composants s'effectue de trois manières:

1. **Communication directe**: Les contrôleurs possèdent des références vers leurs vues et peuvent les appeler directement.

2. **Listeners d'actions**: Les vues définissent des interfaces d'écouteurs (comme `ILoginViewActionListener`) que les contrôleurs implémentent. Exemple:

```java
// Dans LoginView
public void login() {
    String userTag = mLoginUserTagField.getText().trim();
    String password = new String(mLoginPasswordField.getPassword());

    if (mActionListener != null) {
        mActionListener.onLoginRequested(userTag, password);
    }
}

// Dans LoginController (qui implémente ILoginViewActionListener)
@Override
public void onLoginRequested(String userTag, String password) {
    this.loginUser(userTag, password);
}
```

3. **Système d'événements**: Utilisé pour la communication entre composants non directement liés.

```java
// Lors d'une connexion réussie dans LoginController
mSession.connect(foundUser);
EventManager.getInstance().fireEvent(new SessionEvents.UserLoggedInEvent(foundUser));

// Dans MessageAppIHM, qui écoute cet événement
EventManager.getInstance().addListener(SessionEvents.UserLoggedInEvent.class, 
    event -> onUserLogin(event.getUser()));
```

## Cycle de vie typique d'une action utilisateur

Prenons l'exemple d'une connexion utilisateur:

1. L'utilisateur saisit ses identifiants dans `LoginView` et clique sur "Se connecter"
2. `LoginView` appelle `mActionListener.onLoginRequested()`
3. `LoginController` (qui implémente l'interface listener) vérifie les identifiants
4. Si la connexion réussit, le contrôleur:
    - Met à jour la session: `mSession.connect(foundUser)`
    - Émet un événement: `EventManager.getInstance().fireEvent(new UserLoggedInEvent(foundUser))`
5. Tous les composants qui écoutent cet événement réagissent:
    - `MessageAppIHM` affiche la vue principale (home)
    - `HomeView` met à jour les informations utilisateur
    - D'autres composants peuvent également réagir à cet événement

## Avantages de cette architecture

1. **Découplage**: Les composants peuvent communiquer sans dépendances directes
2. **Extensibilité**: Facile d'ajouter de nouveaux composants qui réagissent aux événements existants
3. **Maintenance**: Les responsabilités sont clairement séparées
4. **Testabilité**: Les composants peuvent être testés indépendamment

Cette architecture événementielle permet à l'application d'être modulaire et flexible, tout en maintenant une séparation claire des responsabilités selon le pattern MVC.