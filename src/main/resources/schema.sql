-- Schéma d'exemple (SQLite). Adaptez-le à votre sujet.
-- Syntaxe MariaDB/PostgreSQL : remplacer AUTOINCREMENT par AUTO_INCREMENT / SERIAL.

CREATE TABLE IF NOT EXISTS utilisateur (
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    email     TEXT NOT NULL UNIQUE,
    nom       TEXT NOT NULL,
    mdp_hash  TEXT NOT NULL,                 -- jamais le mot de passe en clair
    sel       TEXT NOT NULL,                 -- sel aléatoire propre à chaque compte
    role      TEXT NOT NULL CHECK (role IN ('ADHERENT', 'ADMIN'))
);

-- Héritage en "table unique" : une colonne type + colonnes propres aux sous-classes (NULL sinon)
CREATE TABLE IF NOT EXISTS media (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    titre           TEXT NOT NULL,
    type            TEXT NOT NULL CHECK (type IN ('JEU_VIDEO', 'LIVRE')),
    plateforme      TEXT,                    -- JeuVideo uniquement
    nb_pages        INTEGER,                 -- Livre uniquement
    nb_exemplaires  INTEGER NOT NULL CHECK (nb_exemplaires >= 0),
    nb_disponibles  INTEGER NOT NULL,
    CHECK (nb_disponibles >= 0 AND nb_disponibles <= nb_exemplaires)
);

CREATE TABLE IF NOT EXISTS emprunt (
    id                    INTEGER PRIMARY KEY AUTOINCREMENT,
    id_media              INTEGER NOT NULL,
    id_utilisateur        INTEGER NOT NULL,
    date_emprunt          TEXT NOT NULL,     -- ISO : 2026-10-20
    date_retour_prevue    TEXT NOT NULL,
    date_retour_effective TEXT,              -- NULL tant que le média n'est pas rendu
    FOREIGN KEY (id_media) REFERENCES media(id),
    FOREIGN KEY (id_utilisateur) REFERENCES utilisateur(id),
    CHECK (date_retour_prevue > date_emprunt)
);

-- Index sur les clés étrangères (à justifier avec EXPLAIN dans optimisation.md)
CREATE INDEX IF NOT EXISTS idx_emprunt_utilisateur ON emprunt (id_utilisateur);
CREATE INDEX IF NOT EXISTS idx_emprunt_media       ON emprunt (id_media);
