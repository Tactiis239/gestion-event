package modele;

/**
 * Levée quand une personne tente une action réservée (ex. inviter sans être organisateur).
 */
public class AccesRefuseException extends RuntimeException {
    public AccesRefuseException(String message) {
        super("Accès refusé : " + message);
    }
}
