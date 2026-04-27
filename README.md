# MediLabo Solutions

Application microservices de détection du risque de diabète de type 2.

## Lancement de l'application

### Prérequis 
1. Docker desktop qui tourne
2. Java 25

### Instructions
#### Au tout premier lancement 
1. Cloner le projet

2. Dupliquer le fichier env.properties.example et le renommer en env.properties

3. A la racine du projet, lancer "docker compose up -d --build"

4. Puis via un shell unix (git bash par ex) :
"docker exec -i patient_service_db mysql -u patient_service -p$MYSQL_PASSWORD < ./patient-service/src/main/resources/init.sql"
Cela permet d'ajouter des données dans la table du patient-service.

5. Taper localhost:8080 (port du gateway) dans la barre d'adresse du navigateur pour tomber sur la page d'accueil

#### Aux lancements suivants
1. A la racine du projet, lancer "docker compose up -d"

2. Taper localhost:8080 (port du gateway) dans la barre d'adresse du navigateur pour tomber sur la page d'accueil

## Architecture

Gateway -> Microservice Frontend SSR (couche d'agrégation) -> Microservices Backend (patient-service, note-service, evaluation-risques-service)

Les architectures microservices utilisent généralement les frameworks frontend modernes comme Angular et Next.js, qui utilisent un mix de SPA et SSR. 

Ici, utiliser le moteur de template Thymeleaf est suffisant et permet de générer les vues côté serveur (SSR) au sein du microservice frontend. 
Celui-ci sert de couche d'agrégation : il génère les vues grâce à des appels aux API REST des microservices backend.

Le rôle du gateway ici est de servir de porte d'entrée aux requêtes http, et de centraliser l'authentification.
Avoir le gateway comme porte d'entrée de chaque microservice aurait ajouté un appel supplémentaire par requête, 
ce qui aurait créé un goulot d'étranglement et n'aurait pas été respectueux des recommandations Green Code. 

## Conventions de langue (approche DDD par gradient)

Décision prise en début de projet, inspirée de l'Ubiquitous Language (Eric Evans, DDD) : le code doit parler la langue des experts métier.

| Élément | Langue | Exemple |
|--------|--------|---------|
| Entités, attributs métier | Français | `Patient`, `dateNaissance`, `genre` |
| Suffixes techniques | Anglais | `Controller`, `Repository`, `Service` |
| Verbes de méthodes | Anglais | `findByNom()`, `save()` |
| Endpoints REST | Français (termes métier) | `/api/patients`, `/api/evaluation-risque` |
| Messages de commits | Anglais | `feat: add patient service` |
| Documentation (README, Javadoc, commentaires) | Français | — |
| Logs applicatifs | Français | — |

**Principe directeur** : plus on est proche du domaine métier → français. Plus on est proche de l'infrastructure technique → anglais.

## Green Code

### PUT vs PATCH pour la mise à jour des patients

La mise à jour des patients utilise **PUT** (remplacement complet) plutôt que **PATCH** (mise à jour partielle).

**Trade-off identifié :** PATCH serait plus économe — moins de données transférées sur le réseau et possibilité de requêtes SQL ciblées (`UPDATE ... SET telephone = ?` au lieu de réécrire tous les champs). Cependant, PATCH ajoute une complexité d'implémentation significative (gestion de l'ambiguïté entre champ absent et champ null, parsing partiel du JSON). Pour un objet `Patient` à 7 champs, le gain énergétique est négligeable face au coût en complexité et en maintenabilité du code. Ce choix serait à réévaluer si les entités devenaient plus volumineuses ou si le trafic augmentait.

### Gestion du CRUD

**Enregistrement :**
Au lieu d'ajouter une vérification de l'existence du patient dans les méthodes de création de patients, ce qui impliquerait la succession de 2 requêtes SQL (SELECT puis INSERT), la contrainte d'unicité mise au niveau de la base de donnée permet à celle-ci de gérer le cas où le patient existe déjà en n'utilisant qu'une requête (INSERT). On attrape ensuite l'exception technique DataIntegrityViolation au niveau du global handler.

**Mise à jour :**
Pour la mise à jour du patient, au lieu de récupérer dans la base le patient puis d'enregistrer la version mise à jour, le tout dans le contrôleur, ce qui impliquerait la succession de 2 transactions et 3 requêtes SQL (SELECT pour la transaction 1, puis SELECT et UPDATE pour la transaction 2), on gère les 2 requêtes (SELECT puis UPDATE) via 1 seule transaction dans le service.

**Suppression :**
Pour les suppressions d'entités, on vérifie leur existence (existsById) en base au lieu de les récupérer (findById). La récupération complète est inutile et plus coûteuse en énergie. 

### Optimisation des Dockerfiles

* Les dockerfiles ont été optimisés de sorte à mettre en cache les dépendances Maven -> moins de téléchargements réseaux à chaque modification du code source, builds plus rapides, moins d'énergie consommée.
* Une image JRE, plus légère, a été utilisée pour exécuter les applications à la place d’un JDK complet, ce qui réduit la quantité de données téléchargées, stockées et transférées sur le réseau.

### Gestion des plugins

La dépendance Lombok, bien que nécessaire à la compilation n'a pas besoin de figurer dans le .jar final d'un service, c'est pourquoi on l'exclue du spring-boot-maven-plugin.

