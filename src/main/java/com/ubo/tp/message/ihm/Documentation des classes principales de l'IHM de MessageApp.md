# Documentation des classes principales de l'IHM de MessageApp

Ce document décrit les responsabilités et les rôles des classes principales du package `/ihm` de l'application MessageApp.

## Classes principales

### MessageApp.java
**Rôle :** Classe technique principale de l'application (Contrôleur principal)
**Responsabilités :**
- Coordonne l'initialisation et le fonctionnement global de l'application
- Gère l'initialisation des différents composants (contrôleurs, vues, etc.)
- Maintient les références vers les composants principaux (database, session, etc.)
- Gère la configuration de l'application
- Coordonne les interactions entre les composants
- Dirige la navigation entre les différentes vues

MessageApp est le point d'entrée principal pour les fonctionnalités de l'application, jouant le rôle de contrôleur de niveau supérieur conformément au pattern MVC.

### MessageAppIHM.java
**Rôle :** Interface utilisateur principale (Vue principale)
**Responsabilités :**
- Crée et gère la fenêtre principale de l'application
- Initialise le look and feel de l'interface utilisateur
- Gère le CardLayout pour afficher les différentes vues
- S'occupe des transitions entre les différentes vues
- Réagit aux événements de session (connexion/déconnexion)
- Gère la barre de menu de l'application

Cette classe est responsable de tous les aspects visuels de l'application, servant de conteneur pour toutes les vues spécifiques.

### HomeController.java
**Rôle :** Contrôleur de la vue d'accueil
**Responsabilités :**
- Gère les actions utilisateur sur la vue d'accueil
- Implémente l'interface IHomeViewActionListener pour répondre aux événements de la vue
- Délègue les actions à MessageApp ou à la session selon les besoins
- Gère la navigation vers les différentes sections de l'application

Ce contrôleur sert d'intermédiaire entre la vue d'accueil et le reste de l'application.

### HomeView.java
**Rôle :** Vue d'accueil principale de l'application
**Responsabilités :**
- Affiche le dashboard principal avec les cartes de navigation
- Affiche les informations de l'utilisateur connecté
- Permet d'accéder aux différentes fonctionnalités (profil, messages, utilisateurs, etc.)
- Affiche le badge de notifications pour les messages non lus
- Communique avec le HomeController via l'interface IHomeViewActionListener

C'est la vue centrale que l'utilisateur voit après s'être connecté.

### IHomeViewActionListener.java
**Rôle :** Interface de callback pour les actions de la vue d'accueil
**Responsabilités :**
- Définit les méthodes que le contrôleur doit implémenter pour réagir aux actions de l'utilisateur
- Établit un contrat clair entre la vue et le contrôleur
- Facilite le découplage entre la vue et le contrôleur

Cette interface permet à la vue de communiquer avec son contrôleur sans avoir de dépendance directe.

### MessageAppMenuBar.java
**Rôle :** Barre de menu de l'application
**Responsabilités :**
- Crée et gère la barre de menu de l'application
- Ajoute/supprime dynamiquement des menus selon l'état de la session
- Réagit aux événements utilisateur sur les menus
- Délègue les actions aux composants appropriés

Cette classe encapsule toute la logique liée à la barre de menu, simplifiant la classe MessageAppIHM.

### MessageNotificationManager.java
**Rôle :** Gestionnaire des notifications de messages non lus
**Responsabilités :**
- Suit les messages non lus
- Notifie les observateurs du changement du nombre de messages non lus
- Fournit des méthodes pour ajouter/supprimer des écouteurs
- Permet de marquer tous les messages comme lus

Cette classe gère l'état des notifications de messages pour l'application.

### NotificationManager.java
**Rôle :** Gestionnaire d'affichage des notifications visuelles
**Responsabilités :**
- Crée et affiche des notifications visuelles pour les nouveaux messages
- Écoute les événements de nouveaux messages des utilisateurs suivis
- Gère l'apparence et le comportement des notifications (position, disparition automatique, etc.)

Cette classe se concentre sur l'aspect visuel des notifications.

## Interaction entre les composants

Dans cette architecture MVC:

1. **Modèle**: Représenté par les classes dans les packages `/datamodel` et `/core`
2. **Vue**: Implémentée par `MessageAppIHM`, `HomeView` et les autres vues spécifiques
3. **Contrôleur**: Géré par `MessageApp`, `HomeController` et les autres contrôleurs spécifiques

Le flux d'information typique est le suivant:
- L'utilisateur interagit avec une vue (par exemple `HomeView`)
- La vue notifie son contrôleur via une interface de listener (par exemple `IHomeViewActionListener`)
- Le contrôleur met à jour le modèle si nécessaire
- Le modèle notifie les observateurs des changements
- Les contrôleurs mettent à jour les vues en conséquence

L'utilisation du système d'événements (`EventManager`) facilite la communication entre les composants tout en maintenant un faible couplage.