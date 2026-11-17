package modele;

import java.time.LocalDateTime;

/** Invitation d'une personne à un événement. */
public class Invitation {
    private int id;
    private StatutInvitation statut;
    private final LocalDateTime dateInvitation;

    public Invitation(Personne invite) {
        this(0, StatutInvitation.EN_ATTENTE, LocalDateTime.now());
    }

    public Invitation(int id, StatutInvitation statut, LocalDateTime dateInvitation) {
        if (statut == null || dateInvitation == null) {
            throw new IllegalArgumentException("Invitation incomplète");
        }
        this.id = id;
        this.statut = statut;
        this.dateInvitation = dateInvitation;
    }

    public void accepter() {
        statut = StatutInvitation.ACCEPTEE;
    }

    public void refuser() {
        statut = StatutInvitation.REFUSEE;
    }

    public boolean estAcceptee() {
        return statut == StatutInvitation.ACCEPTEE;
    }

    /*Getters*/

    public int getId() {
        return id;
    }

    public StatutInvitation getStatut() {
        return statut;
    }

    public LocalDateTime getDateInvitation() {
        return dateInvitation;
    }

    /*Setters*/

    public void setId(int id) {
        if (this.id != 0) {
            throw new IllegalStateException("L'identifiant est déjà attribué");
        }
        this.id = id;
    }

    public void setStatut(StatutInvitation statut) {
        this.statut = statut;
    }
}
