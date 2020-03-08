# Mire SSO - Documentation
Le but de ce projet est d'offrir une mire d'authentification commune, respectant les normes d'échange d'identifiants OAuth2.

## Prérequis
* Démarrer via /setup.

## Authentification
Pour s'authentifier, il faut mettre le `user_token` obtenu à la connection dans l'entête "Authorization" lors des requêtes.

Le token `access_token` sert lui pour le service qui veut accéder aux infos (ex : gardenlink), et ne PERMET PAS de consulter ou éditer les données privées d'une personne.

## Documentation API

La documentation à jour est disponible [en cliquant ici](https://app.swaggerhub.com/apis-docs/Artheriom/AuthenticationServer/1.5.0)