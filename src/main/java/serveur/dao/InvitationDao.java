package serveur.dao;

import modele.Invitation;
import modele.StatutInvitation;
import serveur.bdd.DaoException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvitationDao {

    public int creer(Connection c, int idEvenement, Invitation i) {
        String sql = "INSERT INTO invitation (id_evenement, id_personne, statut, date_invitation) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idEvenement);
            ps.setInt(2, i.getInvite().getId());
            ps.setString(3, i.getStatut().name());
            ps.setString(4, Sql.versTexte(i.getDateInvitation()));
            ps.executeUpdate();
            int id = Sql.cleGeneree(ps);
            i.setId(id);
            return id;
        } catch (SQLException e) {
            throw new DaoException("Création de l'invitation impossible (déjà invité ?)", e);
        }
    }

    public void mettreAJourStatut(Connection c, Invitation i) {
        try (PreparedStatement ps = c.prepareStatement("UPDATE invitation SET statut = ? WHERE id = ?")) {
            ps.setString(1, i.getStatut().name());
            ps.setInt(2, i.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Mise à jour de l'invitation impossible", e);
        }
    }

    List<Invitation> trouverParEvenement(Connection c, int idEvenement, CachePersonnes personnes) {
        String sql = "SELECT * FROM invitation WHERE id_evenement = ? ORDER BY date_invitation";
        List<Invitation> resultat = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idEvenement);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultat.add(new Invitation(
                            rs.getInt("id"),
                            personnes.get(rs.getInt("id_personne")),
                            StatutInvitation.valueOf(rs.getString("statut")),
                            Sql.dateHeure(rs, "date_invitation")));
                }
            }
            return resultat;
        } catch (SQLException e) {
            throw new DaoException("Lecture des invitations impossible", e);
        }
    }
}
