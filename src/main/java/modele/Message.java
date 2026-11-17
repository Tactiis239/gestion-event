package modele;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Message texte d'une personne à une autre, au sein d'un événement. */
public class Message {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private int id;
    private final String contenu;
    private final LocalDateTime date;

    public Message(int id, String contenu, LocalDateTime date) {
        if (contenu == null || contenu.isBlank()) {
            throw new IllegalArgumentException("Le message est vide");
        }
        this.id = id;
        this.contenu = contenu;
        this.date = date == null ? LocalDateTime.now() : date;
    }

    public String afficher() {
        return "[" + date.format(FORMAT) + "] : " + contenu;
    }

    /*Getters*/

    public int getId() {
        return id;
    }

    public String getContenu() {return contenu;}

    public LocalDateTime getDate() {
        return date;
    }

/*Setters*/

    public void setId(int id) {
        if (this.id != 0) {
            throw new IllegalStateException("L'identifiant est déjà attribué");
        }
        this.id = id;
    }


    @Override
    public String toString() {
        return afficher();
    }
}
