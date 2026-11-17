-- Schéma SQLite de la gestion d'événements (traduction du MLD : doc/bdd/mld.md)

CREATE TABLE IF NOT EXISTS personne (
    id_personne     INTEGER PRIMARY KEY AUTOINCREMENT,
    nom             TEXT NOT NULL,
    prenom          TEXT NOT NULL,
    telephone       TEXT,
    email           TEXT NOT NULL UNIQUE COLLATE NOCASE,
    mdp_hash        TEXT NOT NULL,              -- haché bcrypt
    date_naissance  TEXT
);

CREATE TABLE IF NOT EXISTS evenement (
    id_evenement    INTEGER PRIMARY KEY AUTOINCREMENT,
    nom             TEXT NOT NULL,
    lieu            TEXT,
    prix            REAL NOT NULL DEFAULT 0 CHECK (prix >= 0),
    banniere        BLOB,
    description     TEXT,
    date_evenement  TEXT NOT NULL,
    type            TEXT NOT NULL CHECK (type IN ('ANNIVERSAIRE', 'MARIAGE', 'SOIREE', 'AUTRE'))
);

CREATE TABLE IF NOT EXISTS organiser (
    id_evenement    INTEGER NOT NULL REFERENCES evenement(id_evenement) ON DELETE CASCADE,
    id_personne     INTEGER NOT NULL REFERENCES personne(id_personne) ON DELETE CASCADE,
    PRIMARY KEY (id_evenement, id_personne)
);

CREATE TABLE IF NOT EXISTS invitation (
    id_evenement    INTEGER NOT NULL REFERENCES evenement(id_evenement) ON DELETE CASCADE,
    id_personne     INTEGER NOT NULL REFERENCES personne(id_personne) ON DELETE CASCADE,
    statut          TEXT NOT NULL DEFAULT 'EN_ATTENTE' CHECK (statut IN ('EN_ATTENTE', 'ACCEPTEE', 'REFUSEE')),
    date_invitation TEXT NOT NULL,
    PRIMARY KEY (id_evenement, id_personne)
);

CREATE TABLE IF NOT EXISTS besoin (
    id_besoin       INTEGER PRIMARY KEY AUTOINCREMENT,
    nom             TEXT NOT NULL,
    prix            REAL NOT NULL CHECK (prix >= 0),
    quantite        INTEGER NOT NULL CHECK (quantite > 0),
    type_besoin     TEXT NOT NULL CHECK (type_besoin IN ('CONSOMMABLE', 'MATERIEL_SON', 'MATERIEL_LUMIERE')),
    perissable      INTEGER CHECK (perissable IN (0, 1)),        -- consommable
    a_rendre        INTEGER CHECK (a_rendre IN (0, 1)),          -- matériel
    etat            TEXT CHECK (etat IN ('NEUF', 'BON', 'USE', 'HORS_SERVICE')),  -- matériel
    decibels        INTEGER,                                     -- matériel son
    puissance_watts INTEGER,                                     -- matériel son
    lumens          INTEGER,                                     -- matériel lumière
    couleur         TEXT,                                        -- matériel lumière
    id_evenement    INTEGER NOT NULL REFERENCES evenement(id_evenement) ON DELETE CASCADE,
    id_acheteur     INTEGER REFERENCES personne(id_personne) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS message (
    id_message      INTEGER PRIMARY KEY AUTOINCREMENT,
    contenu         TEXT NOT NULL,
    date            TEXT NOT NULL,
    id_evenement    INTEGER NOT NULL REFERENCES evenement(id_evenement) ON DELETE CASCADE,
    id_envoyeur     INTEGER NOT NULL REFERENCES personne(id_personne) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_invitation_personne ON invitation (id_personne);
CREATE INDEX IF NOT EXISTS idx_besoin_evenement    ON besoin (id_evenement);
CREATE INDEX IF NOT EXISTS idx_message_evenement   ON message (id_evenement, date);
