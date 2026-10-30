-- Jeu de données de test
INSERT INTO personne (id, nom, prenom, telephone, email, mdp_hash, date_naissance) VALUES
                                                                                       (1, 'Martin', 'Alice', '0601020304', 'alice@example.org', '$2a$10$2j5/8A9e6NlUSYcBsfCLheV4IBcvYZyk9twKphl7wbDMMHH2iQ3Gy', '2002-03-14'),
                                                                                       (2, 'Durand', 'Bob',   '0605060708', 'bob@example.org',   '$2a$10$V0ah57v1nDUIm1U1g3I/zeGLFY2M3jHIBKWxWiuPOqqZXqlGOibEy', '2001-11-02'),
                                                                                       (3, 'Petit',  'Chloé', NULL,         'chloe@example.org', '$2a$10$5PcCQqURsEMYonXt28PxouEouY8l/P8X1Iraj.TavPdevKWtesP8u', '2003-07-21'),
                                                                                       (4, 'Leroy',  'David', NULL,         'david@example.org', '$2a$10$2WtPAYM7q905QLGIDRP5vevmL/JN2PiV0Tka3l0oI3LPIMS8De6ja', '1999-01-30');

-- L'anniversaire de Chloé, déjà organisé : David a accepté
INSERT INTO evenement (id, nom, lieu, prix, description, date_evenement, type) VALUES
    (1, 'Les 23 ans de Chloé', 'Valenciennes', 0, 'Surprise, ne rien dire.', '2027-07-21T19:00', 'ANNIVERSAIRE');

INSERT INTO organisateur (id_evenement, id_personne) VALUES (1, 3);

INSERT INTO invitation (id_evenement, id_personne, statut, date_invitation) VALUES
                                                                                (1, 4, 'ACCEPTEE',   '2026-10-01T12:00'),
                                                                                (1, 1, 'EN_ATTENTE', '2026-10-01T12:05');

INSERT INTO besoin (id_evenement, id_acheteur, type, nom, prix, quantite, perissable) VALUES
                                                                                          (1, 4, 'CONSOMMABLE', 'Gâteau au chocolat', 25, 1, 1),
                                                                                          (1, NULL, 'CONSOMMABLE', 'Gobelets', 0.10, 50, 0);

INSERT INTO besoin (id_evenement, id_acheteur, type, nom, prix, quantite, a_rendre, etat, lumens, couleur) VALUES
    (1, NULL, 'MATERIEL_LUMIERE', 'Guirlande LED', 12, 2, 1, 'BON', 300, 'multicolore');

INSERT INTO message (id_evenement, id_envoyeur, id_receveur, contenu, date_envoi) VALUES
    (1, 4, 3, 'Je m''occupe du gâteau !', '2026-10-02T09:30');
