package serveur.service;

import serveur.bdd.ConnexionBD;
import serveur.bdd.DaoException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Exécute un bloc d'écritures en une seule transaction : tout est validé ou rien.
 * Le service ouvre la transaction, les DAO font le SQL sur la connexion reçue.
 */
final class Transaction {
    private Transaction() { }

    static <T> T executer(Function<Connection, T> travail) {
        try (Connection c = ConnexionBD.get()) {
            c.setAutoCommit(false);
            try {
                T resultat = travail.apply(c);
                c.commit();
                return resultat;
            } catch (RuntimeException ex) {
                c.rollback();
                throw ex;
            }
        } catch (SQLException e) {
            throw new DaoException("Transaction impossible", e);
        }
    }

    static void executerSansRetour(Consumer<Connection> travail) {
        executer(c -> {
            travail.accept(c);
            return null;
        });
    }
}
