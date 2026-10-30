package serveur.dao;

import modele.*;
import serveur.bdd.DaoException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** DAO pour la table besoin. */
public class BesoinDao {

    public int creer(Connection c, int idEvenement, Besoin b) {
        String sql = "INSERT INTO besoin (id_evenement, id_acheteur, type, nom, prix, quantite, perissable, "
                + "a_rendre, etat, decibels, puissance_watts, lumens, couleur) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idEvenement);
            Sql.setEntier(ps, 2, b.estAchete() ? b.getAcheteur().getId() : null);
            ps.setString(3, typeDe(b));
            ps.setString(4, b.getNom());
            ps.setDouble(5, b.getPrix());
            ps.setInt(6, b.getQuantite());
            // Colonnes propres à une sous-classe : NULL pour les autres
            Sql.setBooleen(ps, 7, b instanceof Consommable co ? co.isPerissable() : null);
            Sql.setBooleen(ps, 8, b instanceof Materiel m ? m.isARendre() : null);
            Sql.setTexte(ps, 9, b instanceof Materiel m ? m.getEtat().name() : null);
            Sql.setEntier(ps, 10, b instanceof MaterielSon s ? s.getDecibels() : null);
            Sql.setEntier(ps, 11, b instanceof MaterielSon s ? s.getPuissanceWatts() : null);
            Sql.setEntier(ps, 12, b instanceof MaterielLumiere l ? l.getLumens() : null);
            Sql.setTexte(ps, 13, b instanceof MaterielLumiere l ? l.getCouleur() : null);
            ps.executeUpdate();
            int id = Sql.cleGeneree(ps);
            b.setId(id);
            return id;
        } catch (SQLException e) {
            throw new DaoException("Création du besoin impossible", e);
        }
    }

    /** Enregistre l'acheteur actuel du besoin. */
    public void mettreAJourAcheteur(Connection c, Besoin b) {
        try (PreparedStatement ps = c.prepareStatement("UPDATE besoin SET id_acheteur = ? WHERE id = ?")) {
            if (b.estAchete()) ps.setInt(1, b.getAcheteur().getId()); else ps.setNull(1, Types.INTEGER);
            ps.setInt(2, b.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Mise à jour de l'acheteur impossible", e);
        }
    }

    public void supprimer(Connection c, int idBesoin) {
        try (PreparedStatement ps = c.prepareStatement("DELETE FROM besoin WHERE id = ?")) {
            ps.setInt(1, idBesoin);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Suppression du besoin impossible", e);
        }
    }

    /** Besoins d'un événement. */
    List<Besoin> trouverParEvenement(Connection c, int idEvenement, CachePersonnes personnes) {
        List<Besoin> resultat = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement("SELECT * FROM besoin WHERE id_evenement = ? ORDER BY id")) {
            ps.setInt(1, idEvenement);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Besoin b = mapper(rs);
                    int idAcheteur = rs.getInt("id_acheteur");
                    if (!rs.wasNull()) {
                        b.acheter(personnes.get(idAcheteur));
                    }
                    resultat.add(b);
                }
            }
            return resultat;
        } catch (SQLException e) {
            throw new DaoException("Lecture des besoins impossible", e);
        }
    }

    /**
     * Valeur de la colonne type selon la sous-classe.
     */
    private static String typeDe(Besoin b) {
        if (b instanceof Consommable) return "CONSOMMABLE";
        if (b instanceof MaterielSon) return "MATERIEL_SON";
        if (b instanceof MaterielLumiere) return "MATERIEL_LUMIERE";
        throw new IllegalArgumentException("Type de besoin non géré par la base : " + b.getClass().getSimpleName());
    }

    /** Instancie la bonne sous-classe d'après la colonne type. */
    private static Besoin mapper(ResultSet rs) throws SQLException {
        String type = rs.getString("type");
        int id = rs.getInt("id");
        String nom = rs.getString("nom");
        double prix = rs.getDouble("prix");
        int quantite = rs.getInt("quantite");
        return switch (type) {
            case "CONSOMMABLE" -> new Consommable(id, nom, prix, quantite, rs.getInt("perissable") == 1);
            case "MATERIEL_SON" -> new MaterielSon(id, nom, prix, quantite, rs.getInt("a_rendre") == 1,
                    EtatMateriel.valueOf(rs.getString("etat")), rs.getInt("decibels"), rs.getInt("puissance_watts"));
            case "MATERIEL_LUMIERE" -> new MaterielLumiere(id, nom, prix, quantite, rs.getInt("a_rendre") == 1,
                    EtatMateriel.valueOf(rs.getString("etat")), rs.getInt("lumens"), rs.getString("couleur"));
            default -> throw new IllegalStateException("Type de besoin inconnu en base : " + type);
        };
    }
}
