# Gestion d'événements

Application client/serveur en Java pour organiser un événement (anniversaire, mariage, soirée…) :
invitations, besoins à acheter et budget. Projet de POO, 3A FISA Info, groupe G1.

Le détail est dans le [cahier des charges](doc/cahier-des-charges.md).

## Lancer

Prérequis : JDK 17+, Maven.

```
cp src/main/resources/db.properties.example src/main/resources/db.properties
mvn compile exec:java
```

Pour l'instant, `MainTestBDD` crée la base SQLite et vérifie l'accès aux données.

## Structure

| Dossier | Contenu |
|---|---|
| `src/main/java/modele/` | Classes métier |
| `src/main/java/serveur/` | Base de données (`bdd`, `dao`) et logique métier (`service`) |
| `src/main/resources/` | Scripts SQL et modèle de configuration |
| `doc/` | Cahier des charges, diagramme de classes (`uml/`), MCD et MLD (`bdd/`) |
