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

## Tests des APIs avec Bruno

Le dossier `api-tests/` contient une collection Bruno pour tester les APIs des microservices. Bruno est un client API natif versionné en Git (alternative légère à Postman). Les fichiers `.yml` définissent les requêtes HTTP (GET, POST, PUT, DELETE) pour chaque endpoint. Pour utiliser la collection : dupliquer le fichier `api-tests/environments/local.example.yml` en `local.yml` (ce fichier contient les variables d'environnement : hosts des services, identifiants d'authentification), puis ouvrir le dossier `api-tests/` dans Bruno. Les requêtes chargent automatiquement les variables depuis `local.yml` et permettent de tester les endpoints sans passer par l'interface web.

## Architecture

Gateway -> Microservice Frontend SSR (couche d'agrégation) -> Microservices Backend (patient-service, note-praticien-service, evaluation-risque-service)

Les architectures microservices utilisent généralement les frameworks frontend modernes comme Angular et Next.js, qui utilisent un mix de SPA et SSR. 

Ici, utiliser le moteur de template Thymeleaf est suffisant et permet de générer les vues côté serveur (SSR) au sein du microservice frontend. 
Celui-ci sert de couche d'agrégation : il génère les vues grâce à des appels aux API REST des microservices backend.

Le rôle du gateway ici est de servir de porte d'entrée aux requêtes http, et de centraliser l'authentification.
Avoir le gateway comme porte d'entrée de chaque microservice aurait ajouté un appel supplémentaire par requête, 
ce qui aurait créé un goulot d'étranglement et n'aurait pas été respectueux des recommandations Green Code. 

## Évaluation du risque de diabète

Le service `evaluation-risque-service` calcule le niveau de risque (`Aucun`, `Limite`, `Danger`, `Précoce`) à partir de l'âge, du genre et du nombre de termes déclencheurs présents dans les notes du praticien. Trois décisions ont été prises pour clarifier des points laissés implicites par les besoins fonctionnels.

### Comptage : termes distincts, pas occurrences

Les 12 termes déclencheurs peuvent être comptés de deux façons : nombre d'occurrences (un terme répété compte plusieurs fois) ou nombre de termes distincts (chaque déclencheur compte pour 1 peu importe sa fréquence). La vérification faite sur les 4 patients de test tranche en faveur des **termes distincts** : la note du patient TestAucun contient "Poids" deux fois dans un contexte sain ("Poids égal ou inférieur au poids recommandé"). En comptant les occurrences, le patient bascule à tort en `Limite` (2-5 déclencheurs). En comptant les termes distincts, on a un seul déclencheur, ce qui est cohérent avec le niveau attendu `Aucun`. 

### Zone grise 0-1 déclencheur pour les patients >30 ans

La spec définit `Aucun` = 0 déclencheur et `Limite` = 2-5 déclencheurs pour les patients de plus de 30 ans, mais ne dit rien du cas à 1 déclencheur. Le patient TestAucun (59 ans, F) tombe précisément dans ce cas (1 déclencheur distinct, niveau attendu `Aucun`). Décision : pour les patients >30 ans, **0 ou 1 déclencheur → Aucun**.

### Stratégie de matching : normalisation + `contains()`

Chaque constante de l'enum `Declencheur` porte un `Set<String>` de mots-clés. La détection se fait en deux temps :

1. **Normalisation** de la note (et des mots-clés au démarrage) : passage en minuscules + suppression des diacritiques via `Normalizer.normalize(s, Form.NFD).replaceAll("\\p{M}", "")`. Cela rend égaux `Cholestérol`, `cholesterol` et `CHOLESTÉROL` lors de la comparaison.
2. **Recherche** : pour chaque déclencheur, on teste si un de ses mots-clés normalisés est `contains()` dans la note normalisée.

Justification :

- **Tolérance gratuite aux flexions** : `contains("anorma")` attrape "anormal", "anormale", "anormales", "anormaux" sans avoir à les énumérer. Des faux positifs sont possibles, comme "non-fumeur", mais ils sont inexistants sur le jeu de test.
- **Green Code** : solution sans dépendance externe (`java.text.Normalizer` est dans le JDK), normalisation des mots-clés effectuée une seule fois au chargement de l'enum, faible empreinte CPU et mémoire. 

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

