package serveur.dao;

import serveur.bdd.ConnexionBD;
import serveur.bdd.DaoException;
import modele.JeuVideo;
import modele.Livre;
import modele.Media;
import modele.TypeMedia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MediaDao implements Dao<Media> {

    @Override
    public Optional<Media> trouver(int id) {
        String sql = "SELECT * FROM media WHERE id = ?";
        try (Connection c = ConnexionBD.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapper(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Lecture du média " + id + " impossible", e);
        }
    }

    @Override
    public List<Media> trouverTous() {
        return rechercherParTitre("");
    }

    /** Exemple de recherche : LIKE avec un paramètre, jamais de concaténation dans le SQL. */
    public List<Media> rechercherParTitre(String morceau) {
        String sql = "SELECT * FROM media WHERE titre LIKE ? ORDER BY titre";
        List<Media> resultat = new ArrayList<>();
        try (Connection c = ConnexionBD.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + morceau + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultat.add(mapper(rs));
                }
            }
            return resultat;
        } catch (SQLException e) {
            throw new DaoException("Recherche de médias impossible", e);
        }
    }

    @Override
    public int creer(Media m) {
        String sql = "INSERT INTO media (titre, type, plateforme, nb_pages, nb_exemplaires, nb_disponibles) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = ConnexionBD.get();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, m.getTitre());
            ps.setString(2, m.getType().name());
            // Colonnes propres à une sous-classe : NULL pour les autres types
            if (m instanceof JeuVideo j) {
                ps.setString(3, j.getPlateforme());
            } else {
                ps.setNull(3, Types.VARCHAR);
            }
            if (m instanceof Livre l) {
                ps.setInt(4, l.getNbPages());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setInt(5, m.getNbExemplaires());
            ps.setInt(6, m.getNbDisponibles());
            ps.executeUpdate();
            try (ResultSet cles = ps.getGeneratedKeys()) {
                cles.next();
                return cles.getInt(1);
            }
        } catch (SQLException e) {
            throw new DaoException("Création du média impossible", e);
        }
    }

    @Override
    public void supprimer(int id) {
        String sql = "DELETE FROM media WHERE id = ?";
        try (Connection c = ConnexionBD.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Suppression du média impossible (encore emprunté ?)", e);
        }
    }

    // ---- Variantes TRANSACTIONNELLES : la connexion est fournie par l'appelant (le service) ----

    /**
     * UPDATE conditionnel : réussit seulement s'il reste un exemplaire.
     * Deux emprunts simultanés du dernier exemplaire : un seul obtient 1 ligne modifiée.
     */
    public boolean retirerExemplaire(Connection c, int idMedia) {
        String sql = "UPDATE media SET nb_disponibles = nb_disponibles - 1 WHERE id = ? AND nb_disponibles > 0";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idMedia);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new DaoException("Mise à jour du stock impossible", e);
        }
    }

    public void rendreExemplaire(Connection c, int idMedia) {
        String sql = "UPDATE media SET nb_disponibles = nb_disponibles + 1 WHERE id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idMedia);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Mise à jour du stock impossible", e);
        }
    }

    /** Factory : choisit la bonne sous-classe d'après la colonne type. */
    private static Media mapper(ResultSet rs) throws SQLException {
        TypeMedia type = TypeMedia.valueOf(rs.getString("type"));
        int id = rs.getInt("id");
        String titre = rs.getString("titre");
        int total = rs.getInt("nb_exemplaires");
        int dispo = rs.getInt("nb_disponibles");
        return switch (type) {
            case JEU_VIDEO -> new JeuVideo(id, titre, total, dispo, rs.getString("plateforme"));
            case LIVRE -> new Livre(id, titre, total, dispo, rs.getInt("nb_pages"));
        };
    }
}
