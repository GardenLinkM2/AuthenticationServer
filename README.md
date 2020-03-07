# Mire SSO - Documentation
Le but de ce projet est d'offrir une mire d'authentification commune, respectant les normes d'échange d'identifiants OAuth2.

## Prérequis
* Démarrer via /setup.

## Authentification
Pour s'authentifier, il faut mettre le `user_token` obtenu à la connection dans l'entête "Authorization" lors des requêtes.

Le token `access_token` sert lui pour le service qui veut accéder aux infos (ex : gardenlink), et ne PERMET PAS de consulter ou éditer les données privées d'une personne.

## Liste des endpoints

Par lisibilité, les erreurs 401 et 403 ne sont pas indiquées.

### Pagination :
Pour la pagination, vous pouvez utiliser ces 3 paramètres :
* page : Indiquer le numéro de page
* number : le nombre d'entrées dans la page
* sort : permet de filtrer (name,ASC par exemple)


### Gestion des tokens
______
#### POST /auth/token
* Permet de créer un nouveau token ( = connexion utilisateur)
* Accessible sans être connecté
* BodyParams :
```json
{
	"clientId":"your client id",
	"email":"User email",
	"password":"User password"
}
```

* Retours :
    * 400 BAD REQUEST : Erreur à l'authentification. Des détails sont disponibles dans le body.
	* 200 OK : Authentification réussie
```json
{
	"access_token":"Le token signé pour le service (clientId) désiré",
	"user_token":"Le token signé pour permettre à l'utilisateur de s'authentifier sur la mire (modification de mot de passe, etc)"
}
```
______
#### POST /token/introspect
* Permet de vérifier l'authenticité d'un token fourni
* Accessible sans être connecté
* BodyParams :
```json
{
	"token": "token à vérifier"
}
```

* Retours :
    * 400 BAD REQUEST : Le token communiqué est invalide.
	* 200 OK : Le token est valide.
```json
{
    "token": "token communiqué",
    "isAdmin": true,
    "uuid": "id utilisateur",
    "emitter": "service ayant émis le token",
    "tokenId": "id unique du token",
    "email": "adresse mail",
    "expirationTime": "date d'expiration"
}
```
		
______
#### DELETE /token/{token}
* Permet d'invalider définitivement un token, notamment en cas de compromission.  L'invalidation du access_token provoque l'invalidation du user_token et vice-versa
* Accessible sans être connecté
* PathParams : 
	* token : Le token à invalider
* Retours :
	* 200 OK : Le token ne sera plus validé par le serveur lors de l'introspection.
______
### Gestion des clients
#### GET /clients
* Récupère la liste des clients (pagination active)
* Accessible sans être connecté
* Retours :
	* 200 OK : Retourne une liste de clients
```json
{
    "content": [
        {
            "id": "clientUUID",
            "clientId": "Public client ID",
            "clientName": "Nom a afficher",
            "clientBaseURL": "https://authm2.artheriom.fr/"
        }
    ],
    "pageable": {
        "sort": {
            "sorted": false,
            "unsorted": true,
            "empty": true
        },
        "offset": 0,
        "pageSize": 20,
        "pageNumber": 0,
        "paged": true,
        "unpaged": false
    },
    "last": true,
    "totalElements": 4,
    "totalPages": 1,
    "size": 20,
    "number": 0,
    "sort": {
        "sorted": false,
        "unsorted": true,
        "empty": true
    },
    "numberOfElements": 4,
    "first": true,
    "empty": false
}
```
______
#### GET /clients/{id}
* Récupère un client précis
* Accessible sans être connecté
* PathParams : 
	* id : L'UUID du client
* Retours :
	* 404 : Le client n'existe pas
	* 200 OK
```json
{
	"id": "clientUUID",
	"clientId": "Public client ID",
    "clientName": "Nom a afficher",
    "clientBaseURL": "https://authm2.artheriom.fr/"
}
```
______
#### POST /clients/
* Créer un client
* Nécessite d'être connecté en administrateur
* Body :
```json
{
	"clientId":"clientId souhaité",
	"clientName":"Nom à afficher",
	"clientBaseURL":"https://authm2.artheriom.fr/"
}
```
* Retours :
	* 400 BAD REQUEST : Un client avec le même clientId existe déjà.
	* 201 CREATED : Le client a été créé
		* Body :
```json
{
    "client_secret": "secret à conserver précieusement !"
}
```
______
#### DELETE /clients/{id}
* Supprime un client
* Nécessite d'être connecté en administrateur
* PathParams :
	* id : l'UUID du client à supprimer
* Retours :
	* 200 OK : Supprimé.
______
#### GET /clients/{id}/regenerateSecret
* Permet de créer un nouveau secret pour un client. **ATTENTION : Créer un nouveau secret invalidera de facto tout les tokens émis pour ce client !**
* Nécessite d'être connecté en administrateur
* PathParams :
	* id : l'UUID du client à régénérer
* Retours :
	* 200 OK : Nouveau secret généré
		* Body :
```json
{
    "client_secret": "secret à conserver précieusement !"
}
```
______
### Gestion des utilisateurs
#### GET /users
* Récupère la liste des utilisateurs (pagination)
* Nécessite d'être connecté en utilisateur (admin ou pas)
* Retours :
	* 200 OK : Une liste d'utilisateurs. Si l'utilisateur n'est pas connecté ou n'est pas admin, les champs `email` et `phone` seront masqués.
		* Body :
```json
{
    "content": [
        {
            "id": "aae336f0-48de-4d87-8adb-a770c83fe894",
            "firstName": "Flo",
            "lastName": "For",
            "email": "hidden",
            "avatar": "",
            "phone": "hidden",
            "admin": true,
            "newsletter" : true
        }
    ],
    "pageable": {
        "sort": {
            "sorted": false,
            "unsorted": true,
            "empty": true
        },
        "offset": 0,
        "pageSize": 20,
        "pageNumber": 0,
        "paged": true,
        "unpaged": false
    },
    "last": true,
    "totalElements": 1,
    "totalPages": 1,
    "size": 20,
    "number": 0,
    "sort": {
        "sorted": false,
        "unsorted": true,
        "empty": true
    },
    "numberOfElements": 1,
    "first": true,
    "empty": false
}
```
______
#### GET /users/{id}
* Récupère un utilisatur
* Ne nécessite pas de connexion
* PathParams :
	* id : l'UUID de l'utilisateur
* Retours :
	* 404 NOT FOUND : Aucun utilisateur n'a été trouvé
	* 200 OK : Un utilisateur. Si l'utilisateur n'est pas connecté ou n'est pas admin, les champs `email` et `phone` seront masqués. Si l'utilisateur demande son propre profil, alors tout les champs sont affichés
```json
{
    "id": "aae336f0-48de-4d87-8adb-a770c83fe894",
    "firstName": "Flo",
    "lastName": "For",
    "email": "hidden",
    "avatar": "",
    "phone": "hidden",
    "admin": true,
    "newsletter" : true

}
```
______
#### DELETE /users/{id}
* Supprime un utilisateur
* Requiert d'être en admin, ou d'être connecté en tant que l'utilisateur à supprimer
* PathParams:
 	* id : l'UUID de l'utilisateur
* Retours :
	* 200 OK : L'utilisateur est supprimé, ses tokens révoqués
______
#### PUT /users/{id}
* Met à jour un utilisateur
* Requiert d'être en admin, ou d'être connecté en tant que l'utilisateur à mettre à jour
* PathParams :
	* id : l'UUID de l'utilisateur
* Body (les champs sont facultatifs):
```json
{
	"password":"password",
	"phone":"phone",
	"email":"mail",
	"avatar":"avatar url",
	"newsletter" : true
}
```
* Retours :
	* 200 OK : Mis à jour.
______
#### POST /users
* Permet de créer un utilisateur
* Pas besoin de connexion
* Body :
```json
{
	"firstName":"Prénom",
	"lastName":"Nom",
	"password":"Mot de passe",
	"phone":"Téléphone",
	"email":"Email",
	"avatar":"Avatar (facultatif)",
	"newsletter" : true
}
```
* Retours :
	* 201 CREATED : Utilisateur créé
	* 400 BAD REQUEST : Echec de création (voir le body pour + de détails)
______
### Mot de passe perdu
#### GET /lostpassword/{email}
* Permet de demander un lien pour créer un nouveau mdp par email
* PathParam :
	* email : l'email du compte
* Retours :
	* 200 OK : Mail envoyé
	* 400 BAD REQUEST : Erreur du client (mail existant)
	* 504 GATEWAY TIMEOUT : Erreur d'envoi du mail - réessayer plus tard.
______
#### POST /newpassword/{token}
* Permet de créer un nouveau mdp pour l'utilisateur ayant le token {token}
* PathParam :
	* token : le token reçu par mail
* Body :
```json
{
	"password":"new password"
}
```
* Retours :
	* 200 OK : Mot de passe mis à jour
	* 400 BAD REQUEST : Erreur (voir le body pour + d'infos)
______
### Setup
#### GET /setup
* Permet d'initialiser un utilisateur (admin) et le client account par défaut.
* Ne fonctionne que si aucun compte ni client n'est en base.
* Retours :
    * 200 OK
```json
{
  "clientID": "Generated client id",
  "clientSecret": "Generated client secret", 
  "email": "Generated user email",
  "password": "Generated password"
}
```