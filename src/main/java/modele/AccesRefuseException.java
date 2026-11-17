package modele;

/**
 * Levée quand une personne tente une action réservée (ex. inviter sans être organisateur).
 */
public class AccesRefuseException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AccesRefuseException(String message) {
        super("Accès refusé : " + message);
    }
}
