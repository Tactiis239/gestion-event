package serveur.dao;

import serveur.bdd.ConnexionBD;
import serveur.bdd.DaoException;
import modele.Role;
import modele.Utilisateur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UtilisateurDao implements Dao<Utilisateur> {

    @Override
    public Optional<Utilisateur> trouver(int id) {
        String sql = "SELECT * FROM utilisateur WHERE id = ?";
        try (Connection c = ConnexionBD.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapper(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Lecture de l'utilisateur " + id + " impossible", e);
        }
    }

    public Optional<Utilisateur> trouverParEmail(String email) {
        String sql = "SELECT * FROM utilisateur WHERE email = ?";
        try (Connection c = ConnexionBD.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapper(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Recherche par email impossible", e);
        }
    }

    @Override
    public List<Utilisateur> trouverTous() {
        String sql = "SELECT * FROM utilisateur ORDER BY nom";
        List<Utilisateur> resultat = new ArrayList<>();
        try (Connection c = ConnexionBD.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultat.add(mapper(rs));
            }
            return resultat;
        } catch (SQLException e) {
            throw new DaoException("Lecture des utilisateurs impossible", e);
        }
    }

    @Override
    public int creer(Utilisateur u) {
        String sql = "INSERT INTO utilisateur (email, nom, mdp_hash, sel, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = ConnexionBD.get();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getEmail());
            ps.setString(2, u.getNom());
            ps.setString(3, u.getMdpHash());
            ps.setString(4, u.getSel());
            ps.setString(5, u.getRole().name());
            ps.executeUpdate();
            try (ResultSet cles = ps.getGeneratedKeys()) {
                cles.next();
                return cles.getInt(1);
            }
        } catch (SQLException e) {
            throw new DaoException("Création de l'utilisateur impossible (email déjà pris ?)", e);
        }
    }

    @Override
    public void supprimer(int id) {
        String sql = "DELETE FROM utilisateur WHERE id = ?";
        try (Connection c = ConnexionBD.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Suppression de l'utilisateur impossible", e);
        }
    }

    /** Convertit une ligne SQL en objet : le seul endroit qui connaît les noms de colonnes. */
    private static Utilisateur mapper(ResultSet rs) throws SQLException {
        return new Utilisateur(
                rs.getInt("id"),
                rs.getString("email"),
                rs.getString("nom"),
                rs.getString("mdp_hash"),
                rs.getString("sel"),
                Role.valueOf(rs.getString("role")));
    }
}
