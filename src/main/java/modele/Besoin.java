package modele;

public class Besoin {
    private final int id;
    private String nom;
    private double prix;
    private int quantite;
    private Personne acheteur;

    public Besoin(int id, String nom, double prix, int quantite, Personne acheteur) {
        this.id = id;
        this.nom = nom;
        this.prix = prix;
        this.quantite = quantite;
        this.acheteur = acheteur;
    }

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public Personne getAcheteur() {
        return acheteur;
    }

    public void setAcheteur(Personne acheteur) {
        this.acheteur = acheteur;
    }
    public boolean estAchete() {
        return acheteur != null;
    }
    public double calculerCout() {
        return prix * quantite;
    }
}
