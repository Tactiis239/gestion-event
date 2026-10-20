package serveur.service;

import serveur.bdd.ConnexionBD;
import serveur.bdd.DaoException;
import serveur.dao.EmpruntDao;
import serveur.dao.MediaDao;
import modele.Emprunt;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Le service gère la TRANSACTION : il ouvre la connexion, la passe aux DAO,
 * puis fait commit (tout réussit) ou rollback (rien n'est modifié).
 */
public class EmpruntService {
    private final MediaDao medias;
    private final EmpruntDao emprunts;

    public EmpruntService(MediaDao medias, EmpruntDao emprunts) {
        this.medias = medias;
        this.emprunts = emprunts;
    }

    public Emprunt emprunter(int idUtilisateur, int idMedia, int jours) {
        try (Connection c = ConnexionBD.get()) {
            c.setAutoCommit(false);
            try {
                if (!medias.retirerExemplaire(c, idMedia)) {
                    throw new MediaIndisponibleException(idMedia);
                }
                LocalDate aujourdhui = LocalDate.now();
                Emprunt e = new Emprunt(0, idMedia, idUtilisateur, aujourdhui, aujourdhui.plusDays(jours), null);
                e.setId(emprunts.creer(c, e));
                c.commit();
                return e;
            } catch (RuntimeException ex) {
                c.rollback();   // le stock redevient comme avant
                throw ex;
            }
        } catch (SQLException e) {
            throw new DaoException("Emprunt impossible", e);
        }
    }

    public void rendre(int idEmprunt) {
        Emprunt e = emprunts.trouver(idEmprunt)
                .orElseThrow(() -> new IllegalArgumentException("Emprunt inconnu : " + idEmprunt));
        if (e.estRendu()) {
            throw new IllegalStateException("Cet emprunt a déjà été rendu");
        }
        try (Connection c = ConnexionBD.get()) {
            c.setAutoCommit(false);
            try {
                emprunts.enregistrerRetour(c, idEmprunt, LocalDate.now());
                medias.rendreExemplaire(c, e.getIdMedia());
                c.commit();
            } catch (RuntimeException ex) {
                c.rollback();
                throw ex;
            }
        } catch (SQLException ex) {
            throw new DaoException("Retour impossible", ex);
        }
    }
}
