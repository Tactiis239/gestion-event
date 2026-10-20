package serveur.bdd;

/**
 * Exception non vérifiée qui enveloppe les erreurs SQL.
 * Les couches supérieures (services, IHM) n'ont ainsi pas à connaître java.sql.
 */
public class DaoException extends RuntimeException {
    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
