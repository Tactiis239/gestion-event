# Gestion d'événements

Application client/serveur en Java pour organiser des événements (anniversaires, soirées, réunions) :
invitations, réponses des invités et répartition des besoins (« qui apporte quoi ? »).

Projet de POO Java, 3A FISA Info, groupe G1.

## Prérequis

- JDK 17 ou plus
- Maven

## Lancer

```
cp src/main/resources/db.properties.example src/main/resources/db.properties
mvn compile exec:java
```

`MainTestBDD` recrée la base SQLite à partir des scripts SQL et vérifie que l'accès aux données fonctionne.

## Organisation

```
src/main/java/
├── modele/     objets métier
├── serveur/    accès à la base (connexion, DAO, services)
├── reseau/     échanges client/serveur (à venir)
└── client/     interface utilisateur (à venir)
src/main/resources/
├── schema.sql, donnees_test.sql
└── db.properties.example
bdd/            MCD, MLD, optimisation
```

## Configuration

Les paramètres de connexion sont dans `db.properties`, qui n'est pas versionné.
Copiez `db.properties.example` pour le créer.