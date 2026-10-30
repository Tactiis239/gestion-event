package serveur.dao;

import modele.Personne;
import serveur.bdd.ConnexionBD;
import serveur.bdd.DaoException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PersonneDao implements Dao<Personne> {

    @Override
    public Optional<Personne> trouver(int id) {
        try (Connection c = ConnexionBD.get()) {
            return trouver(c, id);
        } catch (SQLException e) {
            throw new DaoException("Lecture de la personne " + id + " impossible", e);
        }
    }

    Optional<Personne> trouver(Connection c, int id) {
        try (PreparedStatement ps = c.prepareStatement("SELECT * FROM personne WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapper(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Lecture de la personne " + id + " impossible", e);
        }
    }

    public Optional<Personne> trouverParEmail(String email) {
        String sql = "SELECT * FROM personne WHERE lower(email) = lower(?)";
        try (Connection c = ConnexionBD.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email == null ? "" : email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapper(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Recherche par e-mail impossible", e);
        }
    }

    @Override
    public List<Personne> trouverTous() {
        List<Personne> resultat = new ArrayList<>();
        try (Connection c = ConnexionBD.get();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM personne ORDER BY nom, prenom");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultat.add(mapper(rs));
            }
            return resultat;
        } catch (SQLException e) {
            throw new DaoException("Lecture des personnes impossible", e);
        }
    }

    @Override
    public int creer(Connection c, Personne p) {
        String sql = "INSERT INTO personne (nom, prenom, telephone, avatar, email, mdp_hash, date_naissance) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNom());
            ps.setString(2, p.getPrenom());
            Sql.setTexte(ps, 3, p.getTelephone());
            Sql.setOctets(ps, 4, p.getAvatar());
            ps.setString(5, p.getEmail());
            ps.setString(6, p.getMdpHash());
            Sql.setTexte(ps, 7, Sql.versTexte(p.getDateNaissance()));
            ps.executeUpdate();
            int id = Sql.cleGeneree(ps);
            p.setId(id);
            return id;
        } catch (SQLException e) {
            throw new DaoException("Création de la personne impossible (e-mail déjà pris ?)", e);
        }
    }

    @Override
    public void supprimer(Connection c, int id) {
        try (PreparedStatement ps = c.prepareStatement("DELETE FROM personne WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Suppression de la personne impossible", e);
        }
    }

    private static Personne mapper(ResultSet rs) throws SQLException {
        return new Personne(
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getString("telephone"),
                rs.getBytes("avatar"),
                rs.getString("email"),
                rs.getString("mdp_hash"),
                Sql.date(rs, "date_naissance"));
    }
}
