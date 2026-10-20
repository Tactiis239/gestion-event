package serveur.service;

/** Exception métier : plus aucun exemplaire disponible. L'IHM l'affichera comme un message clair. */
public class MediaIndisponibleException extends RuntimeException {
    public MediaIndisponibleException(int idMedia) {
        super("Plus aucun exemplaire disponible pour le média " + idMedia);
    }
}
