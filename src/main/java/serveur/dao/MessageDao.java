package serveur.dao;

import modele.Message;
import serveur.bdd.ConnexionBD;
import serveur.bdd.DaoException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDao {
    private final PersonneDao personnes;

    public MessageDao(PersonneDao personnes) {
        this.personnes = personnes;
    }

    public int creer(Connection c, int idEvenement, Message m) {
        String sql = "INSERT INTO message (id_evenement, id_envoyeur, id_receveur, contenu, date_envoi) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idEvenement);
            ps.setInt(2, m.getEnvoyeur().getId());
            ps.setInt(3, m.getReceveur().getId());
            ps.setString(4, m.getContenu());
            ps.setString(5, m.getDate().withNano(0).toString());
            ps.executeUpdate();
            int id = Sql.cleGeneree(ps);
            m.setId(id);
            return id;
        } catch (SQLException e) {
            throw new DaoException("Envoi du message impossible", e);
        }
    }

    public List<Message> trouverPourPersonne(int idPersonne) {
        String sql = "SELECT * FROM message WHERE id_receveur = ? "
                + "UNION ALL SELECT * FROM message WHERE id_envoyeur = ? AND id_receveur <> ? "
                + "ORDER BY date_envoi";
        try (Connection c = ConnexionBD.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idPersonne);
            ps.setInt(2, idPersonne);
            ps.setInt(3, idPersonne);
            return lire(ps, new CachePersonnes(personnes, c));
        } catch (SQLException e) {
            throw new DaoException("Lecture des messages impossible", e);
        }
    }

    List<Message> trouverParEvenement(Connection c, int idEvenement, CachePersonnes cache) {
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT * FROM message WHERE id_evenement = ? ORDER BY date_envoi")) {
            ps.setInt(1, idEvenement);
            return lire(ps, cache);
        } catch (SQLException e) {
            throw new DaoException("Lecture des messages impossible", e);
        }
    }

    private static List<Message> lire(PreparedStatement ps, CachePersonnes cache) throws SQLException {
        List<Message> resultat = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultat.add(new Message(
                        rs.getInt("id"),
                        cache.get(rs.getInt("id_envoyeur")),
                        cache.get(rs.getInt("id_receveur")),
                        rs.getString("contenu"),
                        Sql.dateHeure(rs, "date_envoi")));
            }
        }
        return resultat;
    }
}
