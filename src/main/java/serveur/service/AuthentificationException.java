package serveur.service;

/** Échec de connexion. */
public class AuthentificationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AuthentificationException() {
        super("E-mail ou mot de passe incorrect");
    }
}
