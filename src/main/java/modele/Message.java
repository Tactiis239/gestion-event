package modele;

import java.util.Date;

public class Message {
    private final int id;
    private String contenu;
    private Date LocalDateTime;
    private Personne envoyeur;
    private Personne receveur;

    public Message(int id, String contenu, Date LocalDateTime, Personne envoyeur, Personne receveur) {
        this.id = id;
        this.contenu = contenu;
        this.LocalDateTime = LocalDateTime;
        this.envoyeur = envoyeur;
        this.receveur = receveur;
    }

    public int getId() {
        return id;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Date getLocalDateTime() {
        return LocalDateTime;
    }

    public void setLocalDateTime(Date LocalDateTime) {
        this.LocalDateTime = LocalDateTime;
    }

    public Personne getEnvoyeur() {
        return envoyeur;
    }

    public void setEnvoyeur(Personne envoyeur) {
        this.envoyeur = envoyeur;
    }

    public Personne getReceveur() {
        return receveur;
    }

    public void setReceveur(Personne receveur) {
        this.receveur = receveur;
    }

    public String Afficher () {
        return "Message{" +
                "id=" + id +
                ", contenu='" + contenu + '\'' +
                ", LocalDateTime=" + LocalDateTime +
                ", envoyeur=" + envoyeur.getNom() + " " + envoyeur.getPrenom() +
                ", receveur=" + receveur.getNom() + " " + receveur.getPrenom() +
                '}';
    }
}
