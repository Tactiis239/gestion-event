package modele;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Message texte d'une personne à une autre, au sein d'un événement. */
public class Message {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private int id;
    private final Personne envoyeur;
    private final Personne receveur;
    private final String contenu;
    private final LocalDateTime date;

    public Message(int id, Personne envoyeur, Personne receveur, String contenu, LocalDateTime date) {
        if (envoyeur == null || receveur == null) {
            throw new IllegalArgumentException("Envoyeur et receveur sont obligatoires");
        }
        if (contenu == null || contenu.isBlank()) {
            throw new IllegalArgumentException("Le message est vide");
        }
        this.id = id;
        this.envoyeur = envoyeur;
        this.receveur = receveur;
        this.contenu = contenu;
        this.date = date == null ? LocalDateTime.now() : date;
    }

    public String afficher() {
        return "[" + date.format(FORMAT) + "] " + envoyeur.getNomComplet() + " -> "
                + receveur.getNomComplet() + " : " + contenu;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (this.id != 0) {
            throw new IllegalStateException("L'identifiant est déjà attribué");
        }
        this.id = id;
    }

    public Personne getEnvoyeur() {
        return envoyeur;
    }

    public Personne getReceveur() {
        return receveur;
    }

    public String getContenu() {
        return contenu;
    }

    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public String toString() {
        return afficher();
    }
}
