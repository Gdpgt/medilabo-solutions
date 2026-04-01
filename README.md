# MediLabo Solutions

Application microservices de détection du risque de diabète de type 2.

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
