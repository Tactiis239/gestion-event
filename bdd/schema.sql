-- Schéma SQLite de l'application de gestion d'événements.

CREATE TABLE IF NOT EXISTS personne (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    nom             TEXT NOT NULL,
    prenom          TEXT NOT NULL,
    telephone       TEXT,
    avatar          BLOB,
    email           TEXT NOT NULL UNIQUE,
    mdp_hash        TEXT NOT NULL,              -- haché bcrypt
    date_naissance  TEXT
);

CREATE TABLE IF NOT EXISTS evenement (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    nom             TEXT NOT NULL,
    lieu            TEXT,
    prix            REAL NOT NULL DEFAULT 0 CHECK (prix >= 0),
    logo            BLOB,
    banniere        BLOB,
    description     TEXT,
    date_evenement  TEXT NOT NULL,
    type            TEXT NOT NULL CHECK (type IN ('ANNIVERSAIRE', 'MARIAGE', 'SOIREE', 'AUTRE'))
);

CREATE TABLE IF NOT EXISTS organisateur (
    id_evenement    INTEGER NOT NULL REFERENCES evenement(id) ON DELETE CASCADE,
    id_personne     INTEGER NOT NULL REFERENCES personne(id) ON DELETE CASCADE,
    PRIMARY KEY (id_evenement, id_personne)
);

CREATE TABLE IF NOT EXISTS invitation (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    id_evenement    INTEGER NOT NULL REFERENCES evenement(id) ON DELETE CASCADE,
    id_personne     INTEGER NOT NULL REFERENCES personne(id) ON DELETE CASCADE,
    statut          TEXT NOT NULL DEFAULT 'EN_ATTENTE' CHECK (statut IN ('EN_ATTENTE', 'ACCEPTEE', 'REFUSEE')),
    date_invitation TEXT NOT NULL,
    UNIQUE (id_evenement, id_personne)
);

CREATE TABLE IF NOT EXISTS besoin (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    id_evenement    INTEGER NOT NULL REFERENCES evenement(id) ON DELETE CASCADE,
    id_acheteur     INTEGER REFERENCES personne(id) ON DELETE SET NULL,
    type            TEXT NOT NULL CHECK (type IN ('CONSOMMABLE', 'MATERIEL_SON', 'MATERIEL_LUMIERE')),
    nom             TEXT NOT NULL,
    prix            REAL NOT NULL CHECK (prix >= 0),
    quantite        INTEGER NOT NULL CHECK (quantite > 0),
    perissable      INTEGER CHECK (perissable IN (0, 1)),
    a_rendre        INTEGER CHECK (a_rendre IN (0, 1)),
    etat            TEXT CHECK (etat IN ('NEUF', 'BON', 'USE', 'HORS_SERVICE')),
    decibels        INTEGER,
    puissance_watts INTEGER,
    lumens          INTEGER,
    couleur         TEXT,
    CHECK (type <> 'CONSOMMABLE' OR (perissable IS NOT NULL AND a_rendre IS NULL AND etat IS NULL
           AND decibels IS NULL AND puissance_watts IS NULL AND lumens IS NULL AND couleur IS NULL)),
    CHECK (type = 'CONSOMMABLE' OR (a_rendre IS NOT NULL AND etat IS NOT NULL AND perissable IS NULL)),
    CHECK (type <> 'MATERIEL_SON' OR (lumens IS NULL AND couleur IS NULL)),
    CHECK (type <> 'MATERIEL_LUMIERE' OR (decibels IS NULL AND puissance_watts IS NULL))
);

CREATE TABLE IF NOT EXISTS message (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    id_evenement    INTEGER NOT NULL REFERENCES evenement(id) ON DELETE CASCADE,
    id_envoyeur     INTEGER NOT NULL REFERENCES personne(id) ON DELETE CASCADE,
    id_receveur     INTEGER NOT NULL REFERENCES personne(id) ON DELETE CASCADE,
    contenu         TEXT NOT NULL,
    date_envoi      TEXT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_invitation_personne ON invitation (id_personne);
CREATE INDEX IF NOT EXISTS idx_besoin_evenement    ON besoin (id_evenement);
CREATE INDEX IF NOT EXISTS idx_message_receveur    ON message (id_receveur, date_envoi);
