package modele;

import java.time.LocalDateTime;

/** Invitation d'une personne à un événement. */
public class Invitation {
    private int id;
    private final Personne invite;
    private StatutInvitation statut;
    private final LocalDateTime dateInvitation;

    public Invitation(Personne invite) {
        this(0, invite, StatutInvitation.EN_ATTENTE, LocalDateTime.now());
    }

    public Invitation(int id, Personne invite, StatutInvitation statut, LocalDateTime dateInvitation) {
        if (invite == null || statut == null || dateInvitation == null) {
            throw new IllegalArgumentException("Invitation incomplète");
        }
        this.id = id;
        this.invite = invite;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (this.id != 0) {
            throw new IllegalStateException("L'identifiant est déjà attribué");
        }
        this.id = id;
    }

    public Personne getInvite() {
        return invite;
    }

    public StatutInvitation getStatut() {
        return statut;
    }

    public LocalDateTime getDateInvitation() {
        return dateInvitation;
    }

    @Override
    public String toString() {
        return invite.getNomComplet() + " : " + statut;
    }
}
