package modele;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Un événement et ses règles de gestion.
 * Il possède ses invitations, ses besoins et ses messages, et a au moins un organisateur.
 * Une action non autorisée lève AccesRefuseException.
 */
public class Evenement {
    private int id;
    private String nom;
    private String lieu;
    private double prix;
    private byte[] banniere;
    private String description;
    private LocalDateTime dateEvenement;
    private TypeEvenement type;

    private final List<Personne> organisateurs = new ArrayList<>();
    private final List<Invitation> invitations = new ArrayList<>();
    private final List<Besoin> besoins = new ArrayList<>();
    private final List<Message> messages = new ArrayList<>();

    /** Le créateur devient le premier organisateur (un événement a toujours au moins un organisateur). */
    public Evenement(int id, Personne createur, String nom, String lieu, double prix, byte[] banniere,
                     String description, LocalDateTime dateEvenement, TypeEvenement type) {
        if (createur == null || nom == null || nom.isBlank() || dateEvenement == null || type == null) {
            throw new IllegalArgumentException("Le créateur, le nom, la date et le type sont obligatoires");
        }
        if (prix < 0) {
            throw new IllegalArgumentException("Le prix ne peut pas être négatif");
        }
        this.id = id;
        this.nom = nom;
        this.lieu = lieu;
        this.prix = prix;
        this.banniere = banniere;
        this.description = description;
        this.dateEvenement = dateEvenement;
        this.type = type;
        organisateurs.add(createur);
    }

    public void modifier(Personne organisateur, String nom, String lieu, double prix, String description,
                         LocalDateTime dateEvenement, TypeEvenement type) {
        if (!estOrganisateur(organisateur)) {
            throw new AccesRefuseException("seul un organisateur peut modifier l'événement");
        }
        if (nom == null || nom.isBlank() || dateEvenement == null || type == null) {
            throw new IllegalArgumentException("Le nom, la date et le type sont obligatoires");
        }
        if (prix < 0) {
            throw new IllegalArgumentException("Le prix ne peut pas être négatif");
        }
        this.nom = nom;
        this.lieu = lieu;
        this.prix = prix;
        this.description = description;
        this.dateEvenement = dateEvenement;
        this.type = type;
    }

    public Invitation inviter(Personne organisateur, Personne p) {
        if (!estOrganisateur(organisateur)) {
            throw new AccesRefuseException("seul un organisateur peut inviter");
        }
        if (p == null) {
            throw new IllegalArgumentException("La personne à inviter est obligatoire");
        }
        if (estOrganisateur(p)) {
            throw new IllegalStateException("Cette personne est déjà organisatrice");
        }
        for (Invitation invitation : invitations) {
            if (invitation.getInvite() == p) {
                throw new IllegalStateException("Cette personne est déjà invitée");
            }
        }
        Invitation invitation = new Invitation(p);
        invitations.add(invitation);
        return invitation;
    }

    public void repondreInvitation(Personne invite, boolean accepte) {
        for (Invitation invitation : invitations) {
            if (invitation.getInvite() == invite) {
                if (accepte) {
                    invitation.accepter();
                } else {
                    invitation.refuser();
                }
                return;
            }
        }
        throw new AccesRefuseException("cette personne n'est pas invitée");
    }

    public void ajouterOrganisateur(Personne organisateur, Personne p) {
        if (!estOrganisateur(organisateur)) {
            throw new AccesRefuseException("seul un organisateur peut ajouter un co-organisateur");
        }
        if (p == null) {
            throw new IllegalArgumentException("La personne à ajouter est obligatoire");
        }
        if (!estOrganisateur(p)) {
            organisateurs.add(p);
        }
    }

    public boolean estOrganisateur(Personne p) {
        return organisateurs.contains(p);
    }

    /** Participant = organisateur, ou invité qui a accepté. */
    public boolean estParticipant(Personne p) {
        return getParticipants().contains(p);
    }

    public void ajouterBesoin(Personne organisateur, Besoin b) {
        if (!estOrganisateur(organisateur)) {
            throw new AccesRefuseException("seul un organisateur peut ajouter un besoin");
        }
        if (b == null) {
            throw new IllegalArgumentException("Le besoin est obligatoire");
        }
        besoins.add(b);
    }

    public void supprimerBesoin(Personne organisateur, Besoin b) {
        if (!estOrganisateur(organisateur)) {
            throw new AccesRefuseException("seul un organisateur peut supprimer un besoin");
        }
        besoins.remove(b);
    }

    public void attribuerBesoin(Personne organisateur, Besoin b, Personne acheteur) {
        if (!estOrganisateur(organisateur)) {
            throw new AccesRefuseException("seul un organisateur peut attribuer un besoin");
        }
        if (!besoins.contains(b)) {
            throw new IllegalArgumentException("Ce besoin n'appartient pas à l'événement");
        }
        if (!estParticipant(acheteur)) {
            throw new IllegalStateException("Un besoin ne peut être attribué qu'à un participant");
        }
        b.acheter(acheteur);
    }

    public void envoyerMessage(Message m) {
        if (m == null) {
            throw new IllegalArgumentException("Le message est obligatoire");
        }
        if (!estParticipant(m.getEnvoyeur())) {
            throw new AccesRefuseException("seuls les participants peuvent envoyer un message");
        }
        messages.add(m);
    }

    public List<Personne> getParticipants() {
        List<Personne> participants = new ArrayList<>(organisateurs);
        for (Invitation invitation : invitations) {
            if (invitation.estAcceptee()) {
                participants.add(invitation.getInvite());
            }
        }
        return participants;
    }

    /** Budget = somme des coûts des besoins (polymorphisme : chaque besoin calcule son coût). */
    public double calculerBudget() {
        double total = 0;
        for (Besoin b : besoins) {
            total += b.calculerCout();
        }
        return total;
    }

    // Accesseurs

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getLieu() {
        return lieu;
    }

    public double getPrix() {
        return prix;
    }

    public byte[] getBanniere() {
        return banniere;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getDateEvenement() {
        return dateEvenement;
    }

    public TypeEvenement getType() {
        return type;
    }

    public void setId(int id) {
        if (this.id != 0) {
            throw new IllegalStateException("L'identifiant est déjà attribué");
        }
        this.id = id;
    }

    public List<Personne> getOrganisateurs() {
        return Collections.unmodifiableList(organisateurs);
    }

    public List<Invitation> getInvitations() {
        return Collections.unmodifiableList(invitations);
    }

    public List<Besoin> getBesoins() {
        return Collections.unmodifiableList(besoins);
    }

    public List<Message> getMessages() {
        return Collections.unmodifiableList(messages);
    }
}
