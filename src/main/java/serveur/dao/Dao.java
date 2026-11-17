package serveur.dao;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * Contrat commun des DAO principaux : tout le SQL reste dans les DAO.
 */
public interface Dao<T> {
    Optional<T> trouver(int id);

    List<T> trouverTous();

    int creer(Connection c, T objet);

    void supprimer(Connection c, int id);
}
