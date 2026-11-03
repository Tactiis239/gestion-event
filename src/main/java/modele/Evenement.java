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
        this(id, nom, lieu, prix, description, dateEvenement, type,
                createur == null ? List.of() : List.of(createur));
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

    private boolean estPasse() {
        return dateEvenement.isBefore(LocalDateTime.now());
    }

    /** Règle 1 : lève AccesRefuseException si l'appelant n'est pas organisateur. */
    public void verifierOrganisateur(Personne appelant, String action) {
        if (!estOrganisateur(appelant)) {
            throw new AccesRefuseException("seul un organisateur peut " + action);
        }
    }

    public void ajouterOrganisateur(Personne appelant, Personne p) {
        verifierOrganisateur(appelant, "ajouter un co-organisateur");
        verifierNiOrganisateurNiInvite(p);
        organisateurs.add(p);
    }

    public void modifier(Personne appelant, String nom, String lieu, double prix, String description,
                         LocalDateTime dateEvenement, TypeEvenement type) {
        verifierOrganisateur(appelant, "modifier l'événement");
        appliquerInfos(nom, lieu, prix, description, dateEvenement, type);
    }

    /** Une personne est soit organisatrice, soit invitée, jamais les deux. */
    private void verifierNiOrganisateurNiInvite(Personne p) {
        if (estOrganisateur(p)) {
            throw new IllegalStateException(p.getNomComplet() + " est déjà organisateur");
        }
        if (getInvitationDe(p).isPresent()) {
            throw new IllegalStateException(p.getNomComplet() + " est déjà invité(e)");
        }
    }

    public Invitation inviter(Personne appelant, Personne p) {
        verifierOrganisateur(appelant, "inviter");
        verifierNiOrganisateurNiInvite(p);
        Invitation i = new Invitation(p);
        invitations.add(i);
        return i;
    }

    /** F15, F20 : refuser après avoir accepté rend ses besoins aux organisateurs. */
    public void repondreInvitation(Personne invite, boolean accepte) {
        Invitation i = getInvitationDe(invite)
                .orElseThrow(() -> new AccesRefuseException("vous n'êtes pas invité(e) à cet événement"));
        if (estPasse()) {
            throw new IllegalStateException("L'événement est passé : la réponse ne peut plus changer");
        }
        if (accepte) {
            i.accepter();
            return;
        }
        i.refuser();
        for (Besoin b : besoinsAchetesPar(invite)) {
            b.acheter(null);
        }
    }

    public List<Besoin> besoinsAchetesPar(Personne p) {
        return besoins.stream().filter(b -> p.equals(b.getAcheteur())).toList();
    }

    private void verifierBesoinDeLEvenement(Besoin b) {
        if (!besoins.contains(b)) {
            throw new IllegalArgumentException("Ce besoin n'appartient pas à l'événement");
        }
    }

    public Optional<Invitation> getInvitationDe(Personne p) {
        return invitations.stream().filter(i -> i.getInvite().equals(p)).findFirst();
    }

    public void ajouterBesoin(Personne appelant, Besoin b) {
        verifierOrganisateur(appelant, "ajouter un besoin");
        chargerBesoin(b);
    }

    public void supprimerBesoin(Personne appelant, Besoin b) {
        verifierOrganisateur(appelant, "supprimer un besoin");
        verifierBesoinDeLEvenement(b);
        besoins.remove(b);
    }

    public void attribuerBesoin(Personne appelant, Besoin b, Personne acheteur) {
        if (acheteur == null) {
            throw new IllegalArgumentException("L'acheteur est obligatoire");
        }
        verifierBesoinDeLEvenement(b);
        // Les droits d'abord (règle 5 : AccesRefuseException), puis les autres règles
        if (!estOrganisateur(appelant) && !appelant.equals(acheteur)) {
            throw new AccesRefuseException("seul un organisateur attribue un besoin à quelqu'un d'autre");
        }
        if (!estParticipant(acheteur)) {
            throw new IllegalStateException(acheteur.getNomComplet()
                    + " n'est pas participant (il faut avoir accepté l'invitation)");
        }
        if (!estOrganisateur(appelant) && b.estAchete()) {
            throw new IllegalStateException("Ce besoin est déjà pris en charge par "
                    + b.getAcheteur().getNomComplet());
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

    // Chargement depuis la base (DAO) : les règles ont été vérifiées à l'écriture

    public void chargerBesoin(Besoin b) {
        if (b == null) {
            throw new IllegalArgumentException("Besoin vide");
        }
        besoins.add(b);
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
            if (estParticipant(i.getInvite())) {
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
