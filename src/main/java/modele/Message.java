package modele;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Message écrit par un participant sur un événement. */
public class Message {
    private int id;
    private String contenu;
    private LocalDateTime date;
    private Personne envoyeur;

    /** Nouveau message, daté de maintenant. */
    public Message(Personne envoyeur, String contenu) {
        if (envoyeur == null) {
            throw new IllegalArgumentException("L'envoyeur est obligatoire");
        }
        if (contenu == null || contenu.isBlank()) {
            throw new IllegalArgumentException("Le message est vide");
        }
        this.envoyeur = envoyeur;
        this.contenu = contenu;
        this.date = LocalDateTime.now();
    }

    public String getContenu() {
        return contenu;
    }

    public String afficher() {
        return "[" + date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "] "
                + envoyeur.getNomComplet() + " : " + contenu;
    }

    // Accesseurs

    public Personne getEnvoyeur() {
        return envoyeur;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setId(int id) {
        if (this.id != 0) {
            throw new IllegalStateException("L'identifiant est déjà attribué");
        }
        this.id = id;
    }
}
