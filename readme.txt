==========AUTEUR  : Navarna============= 

I - Installation :
utiliser le Makefile avec la commande : make
II – Utilisation
Tout d'abord lancer le serveur. (java ServeurMain )
Puis lancer le client, suivre les indications du client . (java Client)
Pour lancer un nouveau Hôte : 2 possibilitées
- soit le créer par le serveur sur demande du client .
- Soit lancer l'hôte dans un autre processus :
java HostMain ServeurIP ServeurPort nomPartie HostIP HostPort
avec ServeurPort = 7777
exemple (java HostMain 127.0.0.1 7777 ''Jouons !!!!!'' 127.0.0.1 7000 )
