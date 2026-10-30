package serveur.dao;

import modele.Personne;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * Pendant la lecture d'un événement, la même personne apparaît plusieurs fois.
 */
class CachePersonnes {
    private final PersonneDao dao;
    private final Connection c;
    private final Map<Integer, Personne> deja = new HashMap<>();

    CachePersonnes(PersonneDao dao, Connection c) {
        this.dao = dao;
        this.c = c;
    }

    Personne get(int id) {
        return deja.computeIfAbsent(id, cle -> dao.trouver(c, cle)
                .orElseThrow(() -> new IllegalStateException("Personne " + cle + " introuvable")));
    }
}
