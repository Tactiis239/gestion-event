# Optimisation : index justifiés par `EXPLAIN QUERY PLAN`

Les trois index de `schema.sql` accélèrent des requêtes réellement exécutées par les DAO.
Pour chacun, on compare le plan d'exécution SQLite **sans** puis **avec** l'index.

## Méthode

1. Créer une base avec `schema.sql` privé de ses lignes `CREATE INDEX`, puis la remplir avec `donnees_test.sql`.
2. Lancer `EXPLAIN QUERY PLAN` sur la requête du DAO (les `?` remplacés par une valeur).
3. Recommencer sur une base créée avec le `schema.sql` complet.

```
grep -v "CREATE INDEX" bdd/schema.sql > /tmp/sans_index.sql
sqlite3 sans.db < /tmp/sans_index.sql && sqlite3 sans.db < bdd/donnees_test.sql
sqlite3 avec.db < bdd/schema.sql      && sqlite3 avec.db < bdd/donnees_test.sql
```

Lecture du plan : `SCAN` = la table entière est parcourue ; `SEARCH ... USING INDEX` = SQLite va directement aux
lignes utiles grâce à l'index.

---

## Index 1 : `idx_invitation_personne ON invitation (id_personne)`

**Requête** (`EvenementDao.trouverOuInvite`, écran « Mes invitations », F15) :

```sql
SELECT e.id FROM evenement e JOIN invitation i ON i.id_evenement = e.id
WHERE i.id_personne = 2 ORDER BY e.date_evenement;
```

| Sans l'index | Avec l'index |
|---|---|
| `SCAN i USING COVERING INDEX sqlite_autoindex_invitation_1` | `SEARCH i USING INDEX idx_invitation_personne (id_personne=?)` |
| `SEARCH e USING INTEGER PRIMARY KEY (rowid=?)` | `SEARCH e USING INTEGER PRIMARY KEY (rowid=?)` |
| `USE TEMP B-TREE FOR ORDER BY` | `USE TEMP B-TREE FOR ORDER BY` |

**Pourquoi :** l'index `UNIQUE (id_evenement, id_personne)` commence par `id_evenement` : il ne sert pas à chercher
par personne, SQLite le parcourt en entier. Avec `idx_invitation_personne`, il ne lit que les invitations de la personne.

---

## Index 2 : `idx_besoin_evenement ON besoin (id_evenement)`

**Requête** (`BesoinDao`, liste des besoins d'un événement, F19, F21, F22) :

```sql
SELECT * FROM besoin WHERE id_evenement = 1 ORDER BY id;
```

| Sans l'index | Avec l'index |
|---|---|
| `SCAN besoin` | `SEARCH besoin USING INDEX idx_besoin_evenement (id_evenement=?)` |

**Pourquoi :** c'est la requête la plus fréquente (chaque ouverture d'un événement). Sans index, tous les besoins
de tous les événements sont lus. Le `ORDER BY id` ne coûte rien : dans un index SQLite, les lignes d'une même valeur
sont déjà rangées par `rowid` (`id`).

---

## Index 3 : `idx_message_receveur ON message (id_receveur, date_envoi)`

**Requête** (`MessageDao.trouverPourPersonne`, messages d'une personne triés par date, F24) :

```sql
SELECT * FROM message WHERE id_receveur = 2
UNION ALL SELECT * FROM message WHERE id_envoyeur = 2 AND id_receveur <> 2
ORDER BY date_envoi;
```

| Sans l'index | Avec l'index |
|---|---|
| `MERGE (UNION ALL)` | `MERGE (UNION ALL)` |
| ├ LEFT : `SCAN message` + `USE TEMP B-TREE FOR ORDER BY` | ├ LEFT : `SEARCH message USING INDEX idx_message_receveur (id_receveur=?)` |
| └ RIGHT : `SCAN message` + `USE TEMP B-TREE FOR ORDER BY` | └ RIGHT : `SCAN message` + `USE TEMP B-TREE FOR ORDER BY` |

**Pourquoi :** l'index sur `(id_receveur, date_envoi)` sert à la fois au filtre et au tri : la partie « messages reçus »
n'a plus ni parcours complet ni tri temporaire.

**Limite :** la partie « messages envoyés » (`id_envoyeur`) reste un `SCAN`. Un index sur `(id_envoyeur, date_envoi)`
la corrigerait, mais chaque index ralentit les écritures : on ne l'ajoute que si l'écran des messages envoyés devient
fréquent.

---

## Index non ajouté

`EvenementDao.trouverOrganisesPar` (`WHERE o.id_personne = ?` sur `organisateur`) fait un `SCAN o` : la clé primaire
`(id_evenement, id_personne)` commence par `id_evenement`. La table ne contient qu'une ou deux lignes par événement,
le gain serait faible : c'est la prochaine piste si le nombre d'événements augmente.
