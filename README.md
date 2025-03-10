Cette implémentation respecte la spécification SRS-MAP-MSG-004 en affichant une notification visuelle lorsqu'un utilisateur suivi publie un nouveau message.
Lorsqu'un nouveau message est ajouté à la base de données, MessageNotificationObserver le détecte
Si le message est émis par un utilisateur suivi par l'utilisateur connecté, il déclenche un événement FollowedUserMessageEvent
NotificationManager écoute cet événement et affiche une notification qui disparaît automatiquement après 5 secondes