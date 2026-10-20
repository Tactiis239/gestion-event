package serveur.dao;

import serveur.bdd.ConnexionBD;
import serveur.bdd.DaoException;
import modele.Emprunt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmpruntDao implements Dao<Emprunt> {

    @Override
    public Optional<Emprunt> trouver(int id) {
        String sql = "SELECT * FROM emprunt WHERE id = ?";
        try (Connection c = ConnexionBD.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapper(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Lecture de l'emprunt " + id + " impossible", e);
        }
    }

    @Override
    public List<Emprunt> trouverTous() {
        String sql = "SELECT * FROM emprunt ORDER BY date_emprunt DESC";
        List<Emprunt> resultat = new ArrayList<>();
        try (Connection c = ConnexionBD.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultat.add(mapper(rs));
            }
            return resultat;
        } catch (SQLException e) {
            throw new DaoException("Lecture des emprunts impossible", e);
        }
    }

    /** Emprunts non rendus d'un adhérent (utilise l'index idx_emprunt_utilisateur). */
    public List<Emprunt> trouverEnCours(int idUtilisateur) {
        String sql = "SELECT * FROM emprunt WHERE id_utilisateur = ? AND date_retour_effective IS NULL";
        List<Emprunt> resultat = new ArrayList<>();
        try (Connection c = ConnexionBD.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idUtilisateur);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultat.add(mapper(rs));
                }
            }
            return resultat;
        } catch (SQLException e) {
            throw new DaoException("Lecture des emprunts en cours impossible", e);
        }
    }

    @Override
    public int creer(Emprunt e) {
        try (Connection c = ConnexionBD.get()) {
            return creer(c, e);
        } catch (SQLException ex) {
            throw new DaoException("Création de l'emprunt impossible", ex);
        }
    }

    /** Variante transactionnelle : utilise la connexion de l'appelant, ne la ferme pas. */
    public int creer(Connection c, Emprunt e) {
        String sql = "INSERT INTO emprunt (id_media, id_utilisateur, date_emprunt, date_retour_prevue) "
                + "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, e.getIdMedia());
            ps.setInt(2, e.getIdUtilisateur());
            ps.setString(3, e.getDateEmprunt().toString());      // LocalDate -> texte ISO
            ps.setString(4, e.getDateRetourPrevue().toString());
            ps.executeUpdate();
            try (ResultSet cles = ps.getGeneratedKeys()) {
                cles.next();
                return cles.getInt(1);
            }
        } catch (SQLException ex) {
            throw new DaoException("Création de l'emprunt impossible", ex);
        }
    }

    public void enregistrerRetour(Connection c, int idEmprunt, LocalDate dateRetour) {
        String sql = "UPDATE emprunt SET date_retour_effective = ? WHERE id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, dateRetour.toString());
            ps.setInt(2, idEmprunt);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Enregistrement du retour impossible", ex);
        }
    }

    @Override
    public void supprimer(int id) {
        String sql = "DELETE FROM emprunt WHERE id = ?";
        try (Connection c = ConnexionBD.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Suppression de l'emprunt impossible", e);
        }
    }

    private static Emprunt mapper(ResultSet rs) throws SQLException {
        String retour = rs.getString("date_retour_effective"); // peut être NULL
        return new Emprunt(
                rs.getInt("id"),
                rs.getInt("id_media"),
                rs.getInt("id_utilisateur"),
                LocalDate.parse(rs.getString("date_emprunt")),
                LocalDate.parse(rs.getString("date_retour_prevue")),
                retour == null ? null : LocalDate.parse(retour));
    }
}
