package modele;

import java.time.LocalDate;

public class Invitation {
    private String statut;
    private Personne invite;
    private String prenom;
    private LocalDate dateInvitation;

    public Invitation(String statut, Personne invite, String prenom, LocalDate dateInvitation) {
        this.statut = statut;
        this.invite = invite;
        this.prenom = prenom;
        this.dateInvitation = dateInvitation;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Personne getInvite() {
        return invite;
    }

    public void setInvite(Personne invite) {
        this.invite = invite;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public LocalDate getDateInvitation() {
        return dateInvitation;
    }

    public void setDateInvitation(LocalDate dateInvitation) {
        this.dateInvitation = dateInvitation;
    }

    @Override
    public String toString() {
        return "Invitation [statut=" + statut + ", invite=" + invite + ", prenom=" + prenom + ", dateInvitation=" + dateInvitation + "]";
    }
}
