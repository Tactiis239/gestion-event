# MLD — Gestion d'événements

Déduit du MCD ([`mcd.dot`](mcd.dot)). Notation : **#** clé primaire, ***** clé étrangère.

```
PERSONNE   (#id_personne, nom, prenom, telephone, email, mdp_hash, date_naissance)

EVENEMENT  (#id_evenement, nom, lieu, prix, banniere, description, date_evenement, type)

ORGANISER  (#id_evenement*, #id_personne*)

INVITATION (#id_evenement*, #id_personne*, statut, date_invitation)

BESOIN     (#id_besoin, nom, prix, quantite, type_besoin,
            perissable, a_rendre, etat, decibels, puissance_watts, lumens, couleur,
            id_evenement*, id_acheteur*)

MESSAGE    (#id_message, contenu, date, id_evenement*, id_envoyeur*)
```

## Règles de passage

| Élément du MCD | Traduction | Raison |
|---|---|---|
| ORGANISER (0,n – 1,n) | Table de jonction `ORGANISER` | Association plusieurs-à-plusieurs. |
| INVITER (0,n – 0,n), porteuse de `statut`, `date_invitation` | Table `INVITATION`, clé (`id_evenement`, `id_personne`) | La clé composée empêche d'inviter deux fois la même personne. |
| CONTENIR (1,1 côté BESOIN) | `id_evenement` dans `BESOIN` (non nul) | Un besoin appartient à un seul événement. |
| ACHETER (0,1 côté BESOIN) | `id_acheteur` dans `BESOIN` (nullable) | `NULL` = pas encore pris en charge. |
| ECRIRE / CONCERNER (1,1 côté MESSAGE) | `id_envoyeur`, `id_evenement` dans `MESSAGE` | Un message a un auteur et un événement. |
| Héritage BESOIN → CONSOMMABLE / MATERIEL → SON / LUMIERE | Une seule table `BESOIN` + colonne `type_besoin` (`CONSOMMABLE`, `MATERIEL_SON`, `MATERIEL_LUMIERE`) | Peu d'attributs spécifiques ; une seule requête pour lire la `List<Besoin>`. Les colonnes qui ne concernent pas le type restent à `NULL`. |
| Énumérations `TypeEvenement`, `StatutInvitation`, `EtatMateriel` | Colonnes texte contrôlées par `CHECK` | Valeurs fixes connues à la conception. |

## Contraintes

- `email` unique.
- `prix >= 0`, `quantite > 0`.
- Suppression d'un événement : ses organisateurs, invitations, besoins et messages sont supprimés (`ON DELETE CASCADE`).
