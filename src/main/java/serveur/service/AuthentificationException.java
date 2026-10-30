package serveur.service;

/** Échec de connexion. */
public class AuthentificationException extends RuntimeException {
    public AuthentificationException() {
        super("E-mail ou mot de passe incorrect");
    }
}
