package modele;

/** Levée quand une personne tente une action qui ne lui est pas autorisée. */
public class AccesRefuseException extends RuntimeException {

    public AccesRefuseException(String message) {
        super("Accès refusé : " + message);
    }
}
