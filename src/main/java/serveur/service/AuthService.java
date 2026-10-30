package serveur.service;

import modele.Personne;
import org.mindrot.jbcrypt.BCrypt;
import serveur.dao.PersonneDao;

import java.time.LocalDate;

public class AuthService {
    private final PersonneDao personnes;

    public AuthService(PersonneDao personnes) {
        this.personnes = personnes;
    }

    /**
     * Renvoie la personne si les identifiants sont bons.
     */
    public Personne seConnecter(String email, String mdp) {
        return personnes.trouverParEmail(email)
                .filter(p -> p.seConnecter(email, mdp))
                .orElseThrow(AuthentificationException::new);
    }

    /** Création d'un compte. Le mot de passe est haché avec bcrypt. */
    public Personne creerCompte(String nom, String prenom, String email, String mdp, LocalDate dateNaissance) {
        String hash = BCrypt.hashpw(mdp, BCrypt.gensalt());
        Personne p = new Personne(0, nom, prenom, null, null, email, hash, dateNaissance);
        Transaction.executer(c -> personnes.creer(c, p));
        return p;
    }
}
