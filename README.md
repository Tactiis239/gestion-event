# Gestion d'événements

Application client/serveur en Java pour organiser des événements (anniversaires, soirées, réunions) :
invitations, réponses des invités et répartition des besoins (« qui apporte quoi ? »).

Projet de POO Java, 3A FISA Info, groupe G1.

## Prérequis

- JDK 17 ou plus
- Maven

## Lancer

```
cp bdd/db.properties.example src/main/resources/db.properties
mvn compile exec:java
```

`MainTestBDD` recrée la base SQLite à partir des scripts SQL et vérifie que l'accès aux données fonctionne.

## Configuration

Les paramètres de connexion sont dans `src/main/resources/db.properties`, qui n'est pas versionné.
Copiez `bdd/db.properties.example` pour le créer.

## Structure

| Dossier | Contenu |
|---|---|
| `bdd/` | Base de données (jalon 2) : MCD, MLD, scripts SQL, optimisation. Voir [`bdd/README.md`](bdd/README.md) |
| `doc/` | Cahier des charges et diagrammes UML |
| `src/main/java/modele/` | Objets métier |
| `src/main/java/serveur/` | Accès à la base (`bdd`, `dao`) et logique métier (`service`) |