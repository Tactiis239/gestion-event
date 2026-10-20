# Kit de démarrage : base de données + JDBC + DAO

Projet Maven d'exemple pour la séance 2 (POO Java, 3A FISA Info). Il montre, sur un sujet de médiathèque,
comment relier une base SQL à Java proprement. **Adaptez-le à votre sujet, ne le recopiez pas tel quel.**

## Lancer

Prérequis : JDK 17 ou plus, Maven.

```
mvn compile exec:java
```

`MainTestBDD` recrée la base (fichier `kit.db`, SQLite), la remplit, puis déroule 6 scénarios :
catalogue, authentification, emprunt transactionnel, refus du dernier exemplaire, retard calculé, retour.

## Structure

| Dossier / fichier | Rôle |
|---|---|
| `resources/schema.sql` | Tables, clés, contraintes `CHECK`, index |
| `resources/donnees_test.sql` | Données de test (les comptes sont créés en Java : le hachage ne se fait pas en SQL) |
| `resources/db.properties.example` | Modèle de configuration. Copiez-le en `db.properties` (non versionné) |
| `bdd/ConnexionBD` | Point d'accès unique à la base, paramètres lus depuis `db.properties` |
| `bdd/InitBase` | Exécute un script SQL du dossier `resources` |
| `bdd/DaoException` | Enveloppe les `SQLException` : les couches hautes ne connaissent pas `java.sql` |
| `modele/` | Objets métier. `Media` est abstraite (`JeuVideo`, `Livre`) : héritage et polymorphisme |
| `dao/` | Un DAO par table, tout le SQL est ici. `MediaDao.mapper` est une Factory |
| `service/EmpruntService` | Gère la transaction (`commit` / `rollback`) |
| `service/AuthService` | Inscription et connexion |
| `securite/Hacheur` | PBKDF2 avec sel aléatoire (JDK uniquement) |

## Les quatre règles à retenir

1. **`PreparedStatement` partout.** Jamais de SQL construit par concaténation.
2. **`try-with-resources`** pour `Connection`, `PreparedStatement` et `ResultSet`.
3. **Le DAO fait le SQL, le service fait la logique et la transaction.** Les variantes de DAO qui prennent
   une `Connection` en paramètre servent à participer à une transaction du service.
4. **Ne stockez pas ce qui se calcule.** Voir `Emprunt.estEnRetard` : le retard est calculé, pas stocké.

## Changer de SGBD

1. Dans `pom.xml`, remplacez la dépendance `sqlite-jdbc` par `mariadb-java-client` ou `postgresql`.
2. Dans `db.properties`, changez `db.url`, `db.user` et `db.password` (exemples dans le fichier).
3. Dans `schema.sql`, remplacez `AUTOINCREMENT` par `AUTO_INCREMENT` (MariaDB) ou `GENERATED ALWAYS AS IDENTITY` (PostgreSQL).
   Pour les dates, préférez le type `DATE` natif de ces SGBD à du texte.
4. Dans `ConnexionBD`, le `PRAGMA foreign_keys` ne sert qu'à SQLite : il est déjà isolé.

## À faire pour votre rendu de S2

- Remplacez les tables et classes d'exemple par celles de **votre** sujet.
- Ajoutez au moins 2 index justifiés par un `EXPLAIN` avant/après (fichier `optimisation.md`).
- Écrivez au moins une transaction qui a du sens pour votre métier.
- Gardez `MainTestBDD` : il doit prouver que votre base et vos DAO fonctionnent.

## Limites connues

- `InitBase` découpe le script sur les `;` : n'en mettez pas dans les chaînes ni les commentaires.
- Une connexion par appel de DAO, sans pool : suffisant pour ce TP, pas pour une vraie application.
- Le code d'exemple n'a pas de tests JUnit : c'est un bon sujet pour S3 et S4.
