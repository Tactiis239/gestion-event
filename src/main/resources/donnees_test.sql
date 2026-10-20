-- Jeu de données de test. Les utilisateurs sont créés en Java (le hachage ne se fait pas en SQL).
INSERT INTO media (titre, type, plateforme, nb_pages, nb_exemplaires, nb_disponibles) VALUES
    ('Zelda : Breath of the Wild', 'JEU_VIDEO', 'Switch', NULL, 2, 2),
    ('Celeste',                    'JEU_VIDEO', 'PC',     NULL, 1, 1),
    ('Les Misérables',             'LIVRE',     NULL,     1900, 3, 3);
