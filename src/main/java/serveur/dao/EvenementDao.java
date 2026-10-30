package serveur.dao;

import modele.*;
import serveur.bdd.ConnexionBD;
import serveur.bdd.DaoException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Tables evenement + organisateur.
 */
public class EvenementDao implements Dao<Evenement> {
    private final PersonneDao personnes;
    private final InvitationDao invitations;
    private final BesoinDao besoins;
    private final MessageDao messages;

    public EvenementDao(PersonneDao personnes, InvitationDao invitations, BesoinDao besoins, MessageDao messages) {
        this.personnes = personnes;
        this.invitations = invitations;
        this.besoins = besoins;
        this.messages = messages;
    }

    @Override
    public Optional<Evenement> trouver(int id) {
        try (Connection c = ConnexionBD.get()) {
            return trouver(c, id, new CachePersonnes(personnes, c));
        } catch (SQLException e) {
            throw new DaoException("Lecture de l'événement " + id + " impossible", e);
        }
    }

    @Override
    public List<Evenement> trouverTous() {
        return lireListe("SELECT id FROM evenement ORDER BY date_evenement", null);
    }

    /** F6 : événements organisés par la personne. */
    public List<Evenement> trouverOrganisesPar(int idPersonne) {
        return lireListe("SELECT e.id FROM evenement e JOIN organisateur o ON o.id_evenement = e.id "
                + "WHERE o.id_personne = ? ORDER BY e.date_evenement", idPersonne);
    }

    /** F6, F15 : événements auxquels la personne est invitée (utilise idx_invitation_personne). */
    public List<Evenement> trouverOuInvite(int idPersonne) {
        return lireListe("SELECT e.id FROM evenement e JOIN invitation i ON i.id_evenement = e.id "
                + "WHERE i.id_personne = ? ORDER BY e.date_evenement", idPersonne);
    }

    private List<Evenement> lireListe(String sql, Integer parametre) {
        List<Evenement> resultat = new ArrayList<>();
        try (Connection c = ConnexionBD.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (parametre != null) {
                ps.setInt(1, parametre);
            }
            List<Integer> ids = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt(1));
                }
            }
            CachePersonnes cache = new CachePersonnes(personnes, c);
            for (int id : ids) {
                trouver(c, id, cache).ifPresent(resultat::add);
            }
            return resultat;
        } catch (SQLException e) {
            throw new DaoException("Lecture des événements impossible", e);
        }
    }

    private Optional<Evenement> trouver(Connection c, int id, CachePersonnes cache) throws SQLException {
        Evenement e;
        try (PreparedStatement ps = c.prepareStatement("SELECT * FROM evenement WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                e = new Evenement(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("lieu"),
                        rs.getDouble("prix"),
                        rs.getString("description"),
                        Sql.dateHeure(rs, "date_evenement"),
                        TypeEvenement.valueOf(rs.getString("type")),
                        lireOrganisateurs(c, id, cache));
                e.chargerVisuels(rs.getBytes("logo"), rs.getBytes("banniere"));
            }
        }
        for (Invitation i : invitations.trouverParEvenement(c, id, cache)) {
            e.chargerInvitation(i);
        }
        for (Besoin b : besoins.trouverParEvenement(c, id, cache)) {
            e.ajouterBesoin(b);
        }
        for (Message m : messages.trouverParEvenement(c, id, cache)) {
            e.chargerMessage(m);
        }
        return Optional.of(e);
    }

    private List<Personne> lireOrganisateurs(Connection c, int idEvenement, CachePersonnes cache)
            throws SQLException {
        List<Personne> resultat = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT id_personne FROM organisateur WHERE id_evenement = ?")) {
            ps.setInt(1, idEvenement);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultat.add(cache.get(rs.getInt(1)));
                }
            }
        }
        return resultat;
    }

    /** INSERT de l'événement ET de ses organisateurs : à appeler dans une transaction (règle 4). */
    @Override
    public int creer(Connection c, Evenement e) {
        String sql = "INSERT INTO evenement (nom, lieu, prix, logo, banniere, description, date_evenement, type) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            remplir(ps, e);
            ps.executeUpdate();
            int id = Sql.cleGeneree(ps);
            e.setId(id);
            for (Personne p : e.getOrganisateurs()) {
                ajouterOrganisateur(c, id, p.getId());
            }
            return id;
        } catch (SQLException ex) {
            throw new DaoException("Création de l'événement impossible", ex);
        }
    }

    public void mettreAJour(Connection c, Evenement e) {
        String sql = "UPDATE evenement SET nom = ?, lieu = ?, prix = ?, logo = ?, banniere = ?, "
                + "description = ?, date_evenement = ?, type = ? WHERE id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            remplir(ps, e);
            ps.setInt(9, e.getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Modification de l'événement impossible", ex);
        }
    }

    private static void remplir(PreparedStatement ps, Evenement e) throws SQLException {
        ps.setString(1, e.getNom());
        Sql.setTexte(ps, 2, e.getLieu());
        ps.setDouble(3, e.getPrix());
        Sql.setOctets(ps, 4, e.getLogo());
        Sql.setOctets(ps, 5, e.getBanniere());
        Sql.setTexte(ps, 6, e.getDescription());
        ps.setString(7, Sql.versTexte(e.getDateEvenement()));
        ps.setString(8, e.getType().name());
    }

    public void ajouterOrganisateur(Connection c, int idEvenement, int idPersonne) {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO organisateur (id_evenement, id_personne) VALUES (?, ?)")) {
            ps.setInt(1, idEvenement);
            ps.setInt(2, idPersonne);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Ajout de l'organisateur impossible", ex);
        }
    }

    @Override
    public void supprimer(Connection c, int id) {
        try (PreparedStatement ps = c.prepareStatement("DELETE FROM evenement WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Suppression de l'événement impossible", ex);
        }
    }
}
