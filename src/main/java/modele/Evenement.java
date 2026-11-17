package modele;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Un événement et ses règles métier.
 * Il possède ses invitations, besoins et messages.
 * Il référence ses organisateurs, qui existent sans lui.
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

    public Evenement(int id, String nom, String lieu, double prix, byte[] banniere,
                     String description, LocalDateTime dateEvenement, TypeEvenement type) {
        if (nom == null || nom.isBlank() || lieu == null || lieu.isBlank() || dateEvenement == null || type == null) {
            throw new IllegalArgumentException("Événement incomplet");
        }
        this.id = id;
        this.nom = nom;
        this.lieu = lieu;
        this.prix = prix;
        this.banniere = banniere;
        this.description = description;
        this.dateEvenement = dateEvenement;
        this.type = type;
    }

    /* Aucune création de l'événement DAO pour l'instant*/

    public void modifier(Personne organisateur, String nom, String lieu, double prix, byte[] banniere,
                         String description, LocalDateTime dateEvenement, TypeEvenement type) {
        if (nom == null || nom.isBlank() || lieu == null || lieu.isBlank() || dateEvenement == null || type == null) {
            throw new IllegalArgumentException("Événement incomplet");
        }
        this.nom = nom;
        this.lieu = lieu;
        this.prix = prix;
        this.banniere = banniere;
        this.description = description;
        this.dateEvenement = dateEvenement;
        this.type = type;
    }

    public void inviter(Personne organisateur, Personne personne) {
        if (!organisateurs.contains(organisateur)) {
            throw new IllegalArgumentException("L'utilisateur n'est pas un organisateur de cet événement");
        }
        if (personne == null) {
            throw new IllegalArgumentException("La personne à inviter est invalide");
        }
        invitations.add(new Invitation(personne));
    }

    public void repondreInvitation(Personne invite, boolean accepte){
        //TODO: repondre à l'invitation de l'invité (accepter ou refuser)
    }

    public void ajouterOrganisateur(Personne organisateur, Personne personne) {
        if (!organisateurs.contains(organisateur)) {
            throw new IllegalArgumentException("L'utilisateur n'est pas un organisateur de cet événement");
        }
        if (personne == null) {
            throw new IllegalArgumentException("La personne à ajouter est invalide");
        }
        organisateurs.add(personne);
    }

    public boolean estOrganisateur(Personne personne) {
        if (!organisateurs.contains(personne)) {
            throw new IllegalArgumentException("L'utilisateur n'est pas un organisateur de cet événement");
        }
        return true;
    }

    public boolean estParticipant(Personne personne) {
        //TODO: vérifier si la personne est un participant de l'événement (invité et a accepté l'invitation)
        return true;
    }

    public void ajouterBesoin(Personne organisateur, Besoin besoin){
        if (!organisateurs.contains(organisateur)) {
            throw new IllegalArgumentException("L'utilisateur n'est pas un organisateur de cet événement");
        }
        besoins.add(besoin);
    }

    public void supprimerBesoin(Personne organisateur, Besoin besoin){
        if (!organisateurs.contains(organisateur)) {
            throw new IllegalArgumentException("L'utilisateur n'est pas un organisateur de cet événement");
        }
        besoins.remove(besoin);
    }
     public void attribuerBesoin(Personne organisateur, Besoin besoin, Personne acheteur) {
         //TODO: retourner la liste des participants à l'événement
     }

     public void envoyerMessage (Personne organisateur, String contenu){
         if (!estOrganisateur(organisateur)) {
             throw new IllegalArgumentException("L'utilisateur n'est pas un organisateur de cet événement");
         }
        if (contenu == null || contenu.isBlank()) {
            throw new IllegalArgumentException("Le message est vide");
        }
         //TODO: envoyer le message à tous les participants de l'événement
     }

     public double calculerBudget(){
        double total = 0;
        /*boucle for qui calcule le prix total des besoins de l'évènement*/
        for(Besoin besoin : besoins){
            total = total + besoin.getPrix();
        }
        return total;
     }

     /* Getters */

    public List<Message> getMessages() {
        return messages;
    }

    public List<Besoin> getBesoins() {
        return besoins;
    }

    public List<Invitation> getInvitations() {
        return invitations;
    }

    public List<Personne> getOrganisateurs() {
        return organisateurs;
    }

    public TypeEvenement getType() {
        return type;
    }

    public LocalDateTime getDateEvenement() {
        return dateEvenement;
    }

    public String getDescription() {
        return description;
    }

    public byte[] getBanniere() {
        return banniere;
    }

    public double getPrix() {
        return prix;
    }

    public String getLieu() {
        return lieu;
    }

    public String getNom() {
        return nom;
    }

    public int getId() {
        return id;
    }


    /* Setters */

    public void setId(int id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public void setBanniere(byte[] banniere) {
        this.banniere = banniere;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDateEvenement(LocalDateTime dateEvenement) {
        this.dateEvenement = dateEvenement;
    }

    public void setType(TypeEvenement type) {
        this.type = type;
    }
}