package serveur.dao;

import modele.Personne;
import serveur.bdd.ConnexionBD;
import serveur.bdd.DaoException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Fait le lien entre les objets Personne et la table personne. */
public class PersonneDao implements Dao<Personne> {

    /** Base -> objet : transforme la ligne courante du ResultSet en Personne. */
    static Personne lire(ResultSet rs) throws SQLException {
        String date = rs.getString("date_naissance");
        return new Personne(
                rs.getInt("id_personne"),
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getString("telephone"),
                rs.getString("email"),
                rs.getString("mdp_hash"),
                date == null ? null : LocalDate.parse(date));   // "2002-03-14" -> LocalDate
    }

    @Override
    public Optional<Personne> trouver(int id) {
        return chercherUne("SELECT * FROM personne WHERE id_personne = ?", id);
    }

    /** Pour la connexion : retrouver le compte à partir de l'e-mail. */
    public Optional<Personne> trouverParEmail(String email) {
        return chercherUne("SELECT * FROM personne WHERE email = ?", email);
    }

    @Override
    public List<Personne> trouverTous() {
        List<Personne> liste = new ArrayList<>();
        try (Connection c = ConnexionBD.get();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM personne ORDER BY nom, prenom");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {                 // une itération par ligne
                liste.add(lire(rs));
            }
            return liste;
        } catch (SQLException e) {
            throw new DaoException("Lecture des personnes impossible", e);
        }
    }

    /** Objet -> base : INSERT, puis on donne à l'objet l'id choisi par la base. */
    @Override
    public int creer(Connection c, Personne p) {
        String sql = "INSERT INTO personne (nom, prenom, telephone, email, mdp_hash, date_naissance) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNom());
            ps.setString(2, p.getPrenom());
            ps.setString(3, p.getTelephone());
            ps.setString(4, p.getEmail());
            ps.setString(5, p.getMdpHash());
            ps.setString(6, p.getDateNaissance() == null ? null : p.getDateNaissance().toString());
            ps.executeUpdate();
            try (ResultSet cles = ps.getGeneratedKeys()) {
                cles.next();
                p.setId(cles.getInt(1));
            }
            return p.getId();
        } catch (SQLException e) {
            throw new DaoException("Création de la personne impossible", e);
        }
    }

    @Override
    public void supprimer(Connection c, int id) {
        try (PreparedStatement ps = c.prepareStatement("DELETE FROM personne WHERE id_personne = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Suppression de la personne impossible", e);
        }
    }

    /** Exécute un SELECT à un paramètre et renvoie la première ligne, ou rien. */
    private Optional<Personne> chercherUne(String sql, Object valeur) {
        try (Connection c = ConnexionBD.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setObject(1, valeur);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(lire(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Recherche de personne impossible", e);
        }
    }
}