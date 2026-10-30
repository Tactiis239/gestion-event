package modele;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Un événement et ses règles métier
 * Il possède ses invitations, besoins et messages.
 * Il référence ses organisateurs, qui existent sans lui..
 */
public class Evenement {
    private int id;
    private String nom;
    private String lieu;
    private double prix;
    private byte[] logo;
    private byte[] banniere;
    private String description;
    private LocalDateTime dateEvenement;
    private TypeEvenement type;

    private final List<Personne> organisateurs = new ArrayList<>();
    private final List<Invitation> invitations = new ArrayList<>();
    private final List<Besoin> besoins = new ArrayList<>();
    private final List<Message> messages = new ArrayList<>();

    public Evenement(int id, String nom, String lieu, double prix, String description,
                     LocalDateTime dateEvenement, TypeEvenement type, Personne createur) {
        this(id, nom, lieu, prix, description, dateEvenement, type, List.of(createur));
    }

    public Evenement(int id, String nom, String lieu, double prix, String description,
                     LocalDateTime dateEvenement, TypeEvenement type, List<Personne> organisateurs) {
        if (organisateurs == null || organisateurs.isEmpty()) {
            throw new IllegalArgumentException("Un événement a toujours au moins un organisateur");
        }
        this.id = id;
        appliquerInfos(nom, lieu, prix, description, dateEvenement, type);
        this.organisateurs.addAll(organisateurs);
    }

    private void appliquerInfos(String nom, String lieu, double prix, String description,
                                LocalDateTime dateEvenement, TypeEvenement type) {
        if (nom == null || nom.isBlank()) {
            throw new IllegalArgumentException("Le nom de l'événement est obligatoire");
        }
        if (dateEvenement == null) {
            throw new IllegalArgumentException("La date de l'événement est obligatoire");
        }
        if (type == null) {
            throw new IllegalArgumentException("Le type de l'événement est obligatoire");
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

    public boolean estOrganisateur(Personne p) {
        return organisateurs.contains(p);
    }

    public boolean estParticipant(Personne p) {
        return estOrganisateur(p) || getInvitationDe(p).map(Invitation::estAcceptee).orElse(false);
    }

    public boolean estPasse() {
        return dateEvenement.isBefore(LocalDateTime.now());
    }

    private void verifierOrganisateur(Personne appelant, String action) {
        if (!estOrganisateur(appelant)) {
            throw new AccesRefuseException("seul un organisateur peut " + action);
        }
    }

    public void ajouterOrganisateur(Personne appelant, Personne p) {
        verifierOrganisateur(appelant, "ajouter un co-organisateur");
        if (estOrganisateur(p)) {
            throw new IllegalStateException(p.getNomComplet() + " est déjà organisateur");
        }
        organisateurs.add(p);
    }

    public void modifier(Personne appelant, String nom, String lieu, double prix, String description,
                         LocalDateTime dateEvenement, TypeEvenement type) {
        verifierOrganisateur(appelant, "modifier l'événement");
        appliquerInfos(nom, lieu, prix, description, dateEvenement, type);
    }

    public void modifierVisuels(Personne appelant, byte[] logo, byte[] banniere) {
        verifierOrganisateur(appelant, "modifier les visuels");
        this.logo = logo;
        this.banniere = banniere;
    }

    public Invitation inviter(Personne appelant, Personne p) {
        verifierOrganisateur(appelant, "inviter");
        if (estOrganisateur(p)) {
            throw new IllegalStateException(p.getNomComplet() + " est organisateur de l'événement");
        }
        if (getInvitationDe(p).isPresent()) {
            throw new IllegalStateException(p.getNomComplet() + " est déjà invité(e)");
        }
        Invitation i = new Invitation(p);
        invitations.add(i);
        return i;
    }

    public List<Besoin> repondreInvitation(Personne invite, boolean accepte) {
        Invitation i = getInvitationDe(invite)
                .orElseThrow(() -> new AccesRefuseException("vous n'êtes pas invité(e) à cet événement"));
        if (estPasse()) {
            throw new IllegalStateException("L'événement est passé : la réponse ne peut plus changer");
        }
        if (accepte) {
            i.accepter();
            return List.of();
        }
        i.refuser();
        List<Besoin> liberes = new ArrayList<>();
        for (Besoin b : besoins) {
            if (invite.equals(b.getAcheteur())) {
                liberes.add(b);
            }
        }
        return liberes;
    }

    public Optional<Invitation> getInvitationDe(Personne p) {
        return invitations.stream().filter(i -> i.getInvite().equals(p)).findFirst();
    }

    public void ajouterBesoin(Besoin b) {
        if (b == null) {
            throw new IllegalArgumentException("Besoin vide");
        }
        besoins.add(b);
    }

    public void ajouterBesoin(Personne appelant, Besoin b) {
        verifierOrganisateur(appelant, "ajouter un besoin");
        ajouterBesoin(b);
    }

    public void supprimerBesoin(Personne appelant, Besoin b) {
        verifierOrganisateur(appelant, "supprimer un besoin");
        if (!besoins.remove(b)) {
            throw new IllegalArgumentException("Ce besoin n'appartient pas à l'événement");
        }
    }

    public void attribuerBesoin(Personne appelant, Besoin b, Personne acheteur) {
        if (!besoins.contains(b)) {
            throw new IllegalArgumentException("Ce besoin n'appartient pas à l'événement");
        }
        if (!estParticipant(acheteur)) {
            throw new IllegalStateException(acheteur.getNomComplet()
                    + " n'est pas participant (il faut avoir accepté l'invitation)");
        }
        if (!estOrganisateur(appelant)) {
            if (!appelant.equals(acheteur)) {
                throw new AccesRefuseException("seul un organisateur attribue un besoin à quelqu'un d'autre");
            }
            if (b.estAchete()) {
                throw new IllegalStateException("Ce besoin est déjà pris en charge par "
                        + b.getAcheteur().getNomComplet());
            }
        }
        b.acheter(acheteur);
    }

    public Optional<Besoin> getBesoin(int idBesoin) {
        return besoins.stream().filter(b -> b.getId() == idBesoin).findFirst();
    }

    public double calculerBudget() {
        double total = 0;
        for (Besoin b : besoins) {
            total += b.calculerCout();
        }
        return total;
    }

    public void envoyerMessage(Message m) {
        if (!estParticipant(m.getEnvoyeur())) {
            throw new AccesRefuseException("seuls les participants peuvent envoyer un message");
        }
        if (!estParticipant(m.getReceveur())) {
            throw new IllegalStateException(m.getReceveur().getNomComplet() + " n'est pas participant");
        }
        messages.add(m);
    }

    public void chargerInvitation(Invitation i) {
        invitations.add(i);
    }

    public void chargerMessage(Message m) {
        messages.add(m);
    }

    public List<Personne> getParticipants() {
        List<Personne> participants = new ArrayList<>(organisateurs);
        for (Invitation i : invitations) {
            if (i.estAcceptee()) {
                participants.add(i.getInvite());
            }
        }
        return participants;
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
        List<Message> tries = new ArrayList<>(messages);
        tries.sort(Comparator.comparing(Message::getDate));
        return tries;
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

    public String getNom() {
        return nom;
    }

    public String getLieu() {
        return lieu;
    }

    public double getPrix() {
        return prix;
    }

    public byte[] getLogo() {
        return logo;
    }

    public byte[] getBanniere() {
        return banniere;
    }

    public void chargerVisuels(byte[] logo, byte[] banniere) {
        this.logo = logo;
        this.banniere = banniere;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Evenement autre)) return false;
        return id != 0 && id == autre.id;
    }

    @Override
    public int hashCode() {
        return id != 0 ? Integer.hashCode(id) : System.identityHashCode(this);
    }

    @Override
    public String toString() {
        return String.format("%s « %s » le %s à %s (%d besoin(s), budget %.2f €)",
                type, nom, dateEvenement.toLocalDate(), lieu, besoins.size(), calculerBudget());
    }
}
