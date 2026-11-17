package modele;

import java.time.LocalDateTime;

/** Invitation d'une personne à un événement. */
public class Invitation {
    private int id;
    private StatutInvitation statut;
    private LocalDateTime dateInvitation;
    private Personne invite;

    /** Nouvelle invitation : en attente, datée de maintenant. */
    public Invitation(Personne invite) {
        if (invite == null) {
            throw new IllegalArgumentException("La personne invitée est obligatoire");
        }
        this.invite = invite;
        this.statut = StatutInvitation.EN_ATTENTE;
        this.dateInvitation = LocalDateTime.now();
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

    // Accesseurs

    public Personne getInvite() {
        return invite;
    }

    public int getId() {
        return id;
    }

    public StatutInvitation getStatut() {
        return statut;
    }

    public LocalDateTime getDateInvitation() {
        return dateInvitation;
    }

    public void setId(int id) {
        if (this.id != 0) {
            throw new IllegalStateException("L'identifiant est déjà attribué");
        }
        this.id = id;
    }
}
