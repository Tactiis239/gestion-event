package serveur.dao;

import java.util.List;
import java.util.Optional;

/** Contrat commun à tous les DAO : un DAO par table, tout le SQL reste ici. */
public interface Dao<T> {
    Optional<T> trouver(int id);

    List<T> trouverTous();

    /** Insère l'objet et retourne l'identifiant généré par la base. */
    int creer(T objet);

    void supprimer(int id);
}
