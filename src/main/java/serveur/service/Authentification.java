package serveur.service;

import modele.AccesRefuseException;
import modele.Personne;
import serveur.dao.PersonneDao;

import java.util.Optional;

public class Authentification {
    private final PersonneDao dao;

    public Authentification(PersonneDao dao) {
        this.dao = dao;
    }

    public Personne connecter(String email, String mdp) {
        Optional<Personne> personne = dao.trouverParEmail(email);

        if(personne.isEmpty() || !personne.get().seConnecter(email, mdp)) {
            throw new AccesRefuseException("email ou mot de passe incorrect");
        }

        return personne.get();
    }
}