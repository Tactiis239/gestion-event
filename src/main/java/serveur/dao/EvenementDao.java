package serveur.dao;

import modele.Evenement;
import modele.Personne;
import modele.TypeEvenement;
import serveur.bdd.ConnexionBD;
import serveur.bdd.DaoException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Fait le lien entre les objets Evenement et les tables evenement / organiser. */
public class EvenementDao implements Dao<Evenement> {

    private static Evenement lire(Connection c, ResultSet rs) throws SQLException {
        int idEvenement = rs.getInt("id_evenement");
        List<Personne> organisateurs = chargerOrganisateurs(c, idEvenement);
        if (organisateurs.isEmpty()) {
            throw new DaoException("L'événement " + idEvenement + " n'a aucun organisateur", null);
        }

        Personne createur = organisateurs.get(0);
        Evenement evenement = new Evenement(
                rs.getInt("id_evenement"),
                createur,
                rs.getString("nom"),
                rs.getString("lieu"),
                rs.getDouble("prix"),
                rs.getBytes("banniere"),
                rs.getString("description"),
                LocalDateTime.parse(rs.getString("date_evenement")),
                TypeEvenement.valueOf(rs.getString("type"))
        );
        for (int i = 1; i < organisateurs.size(); i++) {
            evenement.ajouterOrganisateur(createur, organisateurs.get(i));
        }
        return evenement;
    }

    @Override
    public Optional<Evenement> trouver(int id) {
        return chercherUnEvenement("SELECT * FROM evenement WHERE id_evenement = ?", id);
    }

    @Override
    public List<Evenement> trouverTous() {
        List<Evenement> liste = new ArrayList<>();
        try (Connection c = ConnexionBD.get();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM evenement ORDER BY date_evenement");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {                 // une itération par ligne
                liste.add(lire(c, rs));
            }
            return liste;
        } catch (SQLException e) {
            throw new DaoException("Lecture des événements impossible", e);
        }
    }

    @Override
    public int creer(Connection c, Evenement evenement) {
        String sql = "INSERT INTO evenement (nom, lieu, prix, banniere, description, date_evenement, type) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, evenement.getNom());
            ps.setString(2, evenement.getLieu());
            ps.setDouble(3, evenement.getPrix());
            ps.setBytes(4, evenement.getBanniere());
            ps.setString(5, evenement.getDescription());
            ps.setString(6, evenement.getDateEvenement().toString());
            ps.setString(7, evenement.getType().toString());
            ps.executeUpdate();
            try (ResultSet cles = ps.getGeneratedKeys()) {
                if (!cles.next()) {
                    throw new DaoException("La base n'a pas retourné l'identifiant de l'événement", null);
                }
                evenement.setId(cles.getInt(1));
            }

            String sqlOrganisateur = "INSERT INTO organiser (id_evenement, id_personne) VALUES (?, ?)";
            try (PreparedStatement psOrganisateur = c.prepareStatement(sqlOrganisateur)) {
                for (Personne organisateur : evenement.getOrganisateurs()) {
                    psOrganisateur.setInt(1, evenement.getId());
                    psOrganisateur.setInt(2, organisateur.getId());
                    psOrganisateur.addBatch();
                }
                psOrganisateur.executeBatch();
            }
            return evenement.getId();
        } catch (SQLException e) {
            throw new DaoException("Création de l'événement impossible", e);
        }
    }

    @Override
    public void supprimer(Connection c, int id) {
        try (PreparedStatement ps = c.prepareStatement("DELETE FROM evenement WHERE id_evenement = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Suppression de l'événement impossible", e);
        }
    }

    /** Récupère les personnes associées à l'événement dans la table organiser. */
    private static List<Personne> chargerOrganisateurs(Connection c, int idEvenement) throws SQLException {
        String sql = """
                SELECT p.*
                FROM personne p
                JOIN organiser o ON o.id_personne = p.id_personne
                WHERE o.id_evenement = ?
                """;
        List<Personne> organisateurs = new ArrayList<>();

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idEvenement);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    organisateurs.add(PersonneDao.lire(rs));
                }
            }
        }
        return organisateurs;
    }

    private Optional<Evenement> chercherUnEvenement(String sql, Object valeur) {
        try (Connection c = ConnexionBD.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setObject(1, valeur);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(lire(c, rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Recherche de l'événement impossible", e);
        }
    }
}
