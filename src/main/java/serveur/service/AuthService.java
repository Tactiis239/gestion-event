package serveur.service;

import modele.Personne;
import org.mindrot.jbcrypt.BCrypt;
import serveur.bdd.Transaction;
import serveur.dao.PersonneDao;

import java.time.LocalDate;

/** Connexion (F1, F2, F31) et création de compte (comptes de test : pas d'inscription en ligne, F4). */
public class AuthService {
    private final PersonneDao personnes;

    public AuthService(PersonneDao personnes) {
        this.personnes = personnes;
    }

    /**
     * Renvoie la personne si les identifiants sont bons.
     * Même exception que l'e-mail soit inconnu ou le mot de passe faux (F2).
     */
    public Personne seConnecter(String email, String mdp) {
        return personnes.trouverParEmail(email)
                .filter(p -> p.seConnecter(email, mdp))
                .orElseThrow(AuthentificationException::new);
    }

    /**
     * Création d'un compte. Le mot de passe est haché avec bcrypt.
     * synchronized : la vérification d'unicité de l'e-mail et l'insertion ne sont pas entrelacées entre clients.
     */
    public synchronized Personne creerCompte(String nom, String prenom, String email, String mdp, LocalDate dateNaissance) {
        if (mdp == null || mdp.isBlank()) {
            throw new IllegalArgumentException("Le mot de passe est obligatoire");
        }
        if (personnes.trouverParEmail(email).isPresent()) {
            throw new IllegalStateException("Un compte existe déjà avec cet e-mail");
        }
        String hash = BCrypt.hashpw(mdp, BCrypt.gensalt());
        Personne p = new Personne(0, nom, prenom, null, null, email, hash, dateNaissance);
        Transaction.executer(c -> personnes.creer(c, p));
        return p;
    }
}
