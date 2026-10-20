package serveur.service;

import serveur.dao.UtilisateurDao;
import modele.Role;
import modele.Utilisateur;
import serveur.securite.Hacheur;

import java.util.Optional;

public class AuthService {
    private final UtilisateurDao utilisateurs;

    public AuthService(UtilisateurDao utilisateurs) {
        this.utilisateurs = utilisateurs;
    }

    /** Crée un compte : le sel est généré ici, le mot de passe en clair n'est jamais conservé. */
    public int inscrire(String email, String nom, String motDePasse, Role role) {
        String sel = Hacheur.genererSel();
        String hash = Hacheur.hacher(motDePasse, sel);
        return utilisateurs.creer(new Utilisateur(0, email, nom, hash, sel, role));
    }

    /**
     * Retourne l'utilisateur si les identifiants sont bons, sinon Optional.empty().
     * Même résultat pour « email inconnu » et « mauvais mot de passe » : on ne renseigne pas l'attaquant.
     */
    public Optional<Utilisateur> seConnecter(String email, String motDePasse) {
        return utilisateurs.trouverParEmail(email)
                .filter(u -> Hacheur.verifier(motDePasse, u.getSel(), u.getMdpHash()));
    }
}
