package modele;

import java.time.LocalDateTime;
import java.util.List;

public class Evenement {
    private int id;
    private String nom;
    private String lieu;
    private double prix;
    private byte[] logo;
    private byte[] banniere;
    private String description;
    private LocalDateTime dateEvenement;
    private int capaciteMax;
    private TypeEvenement type;
    private List<Message> message;
    private List<Besoin> besoin;
    private List<Invitation> invitation;



    public Evenement(int id, String nom, String lieu, double prix, byte[] logo, byte[] banniere, String description, LocalDateTime dateEvenement, int capaciteMax, TypeEvenement type) {
        this.id = id;
        this.nom = nom;
        this.lieu = lieu;
        this.prix = prix;
        this.logo = logo;
        this.banniere = banniere;
        this.description = description;
        this.dateEvenement = dateEvenement;
        this.capaciteMax = capaciteMax;
        this.type = type;
    }

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

    public byte[] getLogo() {
        return logo;
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

    public int getCapaciteMax() {
        return capaciteMax;
    }

    public TypeEvenement getType() {
        return type;
    }


    public void ajouterOrganisateur(Personne appelant, Personne organisateur) {
        // Logique pour ajouter un utilisateur à l'événement
    }

    public void ajouterParticipant(Personne appelant, Personne participant) {
        // Logique pour ajouter un utilisateur à l'événement
    }

    public boolean estOrganisateur(Personne personne) {
        // Logique pour vérifier si une personne est organisateur de l'événement
        return false;
    }

    public boolean estParticipant(Personne personne) {
        // Logique pour vérifier si une personne est participant de l'événement
        return false;
    }

    public void ajouterBesoin(Besoin besoin) {
        // Logique pour ajouter un besoin à l'événement
    }

    public void envoyerMessage(Personne appelant, String message) {
        // Logique pour envoyer un message à tous les participants de l'événement
    }
    public void inviter(Personne appelant, String message) {
        // Logique pour envoyer un message à tous les participants de l'événement
    }
}
