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

4. Les données de test vont être chargée automatiquement par docker au premier lancement grâce aux init.sql et init.js. 

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

Le service `evaluation-risque-service` calcule le niveau de risque (`Aucun`, `Limite`, `Danger`, `Précoce`) à partir de l'âge, du genre et du nombre de termes déclencheurs présents dans les notes du praticien. Plusieurs décisions ont été prises pour clarifier des points laissés implicites par les besoins fonctionnels.

### Comptage : termes distincts sur l'ensemble des notes du patient

Les 12 termes déclencheurs peuvent être comptés de plusieurs façons : occurrences (un terme répété compte plusieurs fois), distincts par note (chaque déclencheur compte pour 1 par note, sommé sur toutes les notes), ou distincts sur l'ensemble des notes du patient (chaque déclencheur compte pour 1 au total, peu importe le nombre de notes ou d'occurrences qui le mentionnent). Décision retenue : **distincts sur l'ensemble des notes du patient**.

Justifications :

- **Élimine les faux positifs intra-note** : la note du patient TestAucun contient "Poids" deux fois dans un contexte sain ("Poids égal ou inférieur au poids recommandé"). En comptant les occurrences, le patient bascule à tort en `Limite` (2-5 déclencheurs). En comptant les termes distincts, on a un seul déclencheur, ce qui est cohérent avec le niveau attendu `Aucun`.
- **Sémantique médicale** : une condition mentionnée dans plusieurs consultations reste la même observation médicale, pas un signal cumulatif. "Audition anormale" notée à deux consultations successives = 1 déclencheur, pas 2.
- **Stabilité** : ajouter une note de suivi qui répète un déclencheur déjà connu ne fait pas basculer le niveau de risque.

Les 4 patients de test valident le comptage global aussi bien que le comptage par note (les deux interprétations donnent les bons niveaux), donc le choix se fait sur la sémantique.

### Zone grise 0-1 déclencheur

La spec définit `Aucun` = 0 déclencheur et `Limite` = 2-5 déclencheurs pour les patients de plus de 30 ans, mais ne dit rien du cas à 1 déclencheur. Le patient TestAucun (59 ans, F) tombe précisément dans ce cas (1 déclencheur distinct, niveau attendu `Aucun`). Décision : pour tous les patients, **0 ou 1 déclencheur → Aucun**.

### Zone grise : jeunes patients sous le seuil DANGER

La spec définit pour les patients ≤ 30 ans uniquement les seuils DANGER (M : ≥3, F : ≥4) et PRECOCE (M : ≥5, F : ≥7). Elle ne dit rien d'un jeune homme à 2 déclencheurs ou d'une jeune femme à 2-3 déclencheurs. `Limite` est explicitement réservé aux > 30 ans. Décision : **jeune patient sous le seuil DANGER de son genre → Aucun**, par cohérence avec la logique "pas assez de signaux pour alerter".

### Table récapitulative des seuils

| Âge | Genre | Nb déclencheurs distincts | Niveau |
|-----|-------|---------------------------|--------|
| Tous | Tous | 0-1 | Aucun |
| > 30 ans | Tous | 2-5 | Limite |
| > 30 ans | Tous | 6-7 | Danger |
| > 30 ans | Tous | ≥ 8 | Précoce |
| ≤ 30 ans | M | 2 | Aucun *(hors spec)* |
| ≤ 30 ans | M | 3-4 | Danger |
| ≤ 30 ans | M | ≥ 5 | Précoce |
| ≤ 30 ans | F | 2-3 | Aucun *(hors spec)* |
| ≤ 30 ans | F | 4-6 | Danger |
| ≤ 30 ans | F | ≥ 7 | Précoce |

### Stratégie de matching : normalisation + `contains()`

Chaque constante de l'enum `Declencheur` porte un `Set<String>` de mots-clés. La détection se fait en deux temps :

1. **Normalisation** de la note (et des mots-clés au démarrage) : passage en minuscules + suppression des diacritiques via `Normalizer.normalize(s, Form.NFD).replaceAll("\\p{M}", "")`. Cela rend égaux `Cholestérol`, `cholesterol` et `CHOLESTÉROL` lors de la comparaison.
2. **Recherche** : pour chaque déclencheur, on teste si un de ses mots-clés normalisés est `contains()` dans la note normalisée.

Justification :

- **Tolérance gratuite aux flexions** : `contains("anorma")` attrape "anormal", "anormale", "anormales", "anormaux" sans avoir à les énumérer. Des faux positifs sont possibles, comme "non-fumeur" pour "fumeur", mais ils sont inexistants sur le jeu de test.
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

### Cache des valeurs de l'enum `Declencheur`

Lors du comptage des déclencheurs, on parcourt l'ensemble des constantes de l'enum `Declencheur` pour chaque évaluation de risque. L'appel `Declencheur.values()` retourne **une nouvelle copie défensive du tableau à chaque invocation**. 
Pour 12 constantes ce coût est faible, mais il est **strictement inutile** : l'enum est immuable, le tableau est toujours le même.

La liste est donc mise en cache une fois pour toutes au chargement de la classe :

### Validation fail-fast de `dateNaissance`

Avant l'appel à `note-praticien-service`, on vérifie que `dateNaissance` n'est pas `null` via `Objects.requireNonNull(...)`. Si la donnée patient est invalide, **l'appel HTTP aux notes n'a pas lieu** — économie d'un aller-retour réseau, de bande passante et de temps CPU côté `note-praticien-service`. Le message d'erreur est construit via un `Supplier<String>` (`() -> "..."`) pour n'être évalué qu'en cas d'échec, évitant la concaténation de chaîne dans le cas nominal.

### Optimisation des Dockerfiles

- Les dockerfiles ont été optimisés de sorte à mettre en cache les dépendances Maven -> moins de téléchargements réseaux à chaque modification du code source, builds plus rapides, moins d'énergie consommée.
- Une image JRE, plus légère, a été utilisée pour exécuter les applications à la place d’un JDK complet, ce qui réduit la quantité de données téléchargées, stockées et transférées sur le réseau.

### Gestion des plugins

La dépendance Lombok, bien que nécessaire à la compilation n'a pas besoin de figurer dans le .jar final d'un service, c'est pourquoi on l'exclue du spring-boot-maven-plugin.

### Optimisation des healthchecks Docker

- J'ai envisagé de désactiver les healthchecks après le démarrage pour économiser des cycles CPU. J'ai écarté cette option car elle empêche la détection des pannes en runtime (ex : perte de connexion DB). 
En contrepartie, j'ai porté interval de 30s par défaut à 2min.
- `wget --spider` : la commande ne télécharge pas le corps de la réponse HTTP, seulement les headers. Moins d'IO, moins de mémoire, moins de CPU côté serveur comme côté client.

