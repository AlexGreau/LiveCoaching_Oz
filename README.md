# LiveCoaching deuxième application

Cette application est a utiliser avec l'application [LiveCoached_Oz](https://github.com/AlexGreau/LiveCoached_Oz) pour montre WearOs. Cette paire d'application servant à conduire l'expérience cherchant à prouver nos hypothèses. Voici le schéma avec captures d'écran de la logique de la paire d'applications :

![schemaOz](https://github.com/AlexGreau/LiveCoaching_Oz/blob/master/readmeImages/flow.PNG)

## Main Activity

La starting activity est la main activity. 
Cette mainActivity initialise ses champs et son interface, ainsi que son "Logger" et une premiere communication avec la montre.
Elle se charge des changements dynamiques de l'interface et de la logique générale de l'application et délègue la communication ainsi que la sauvegarde des données aux autres classes.

## Communication

Pour cette paire d'application, c'est la montre qui est à l'écoute des ordres de la tablette, elle tiendra donc le role de "server".
Dès qu'elle voudra envoyer un ordre, la tablette créera une "ClientTask", une classe qui hérite de "AsyncTask" qui gèrera l'envoi des données en arrière plan sans perturber le UiThread.

Voici le schéma des communications :
![comm](https://github.com/AlexGreau/LiveCoaching_Oz/blob/master/readmeImages/schemaOz.PNG)

Le message sera décodé par la fonction `void decodeResponse(String rep)`.
La signature de cette fonction est définie dans l'interface "Decoder" que la mainActivity implémente.
La raison derrière cette architecture est que si nous venions a rajouter une nouvelle Activity ayant besoin de décoder les messages différemment (à l'image de la première application), il suffirait de la faire implementer l'interface "Decoder" et la passer en paramètre de création de l'objet ClientTask.

Ici, la montre ne fait qu'envoyer des accusés de reception. La tablette enregistre la date où elle recoit l'accusé pour calculer le temps mis pour effectué un ordre plus tard.

## Sauvegarde des données

La sauvegarde des données se fait dans des fichiers texte, pour pouvoir les manipuler aisément plus tard.
Ces fichiers ont besoin d'être publiques et accessibles par l'ordinateur lorsqu'il est branché afin de les manipuler.
Ils sont situés dans la mémoire externe de la tablette car la mémoire interne n'est accessible que par l'application.

L'écriture dans ces fichiers fichiers se fait par la classe "Logger", qui regroupe les fonction de formattage de nos données, d'écriture et de lecture.
