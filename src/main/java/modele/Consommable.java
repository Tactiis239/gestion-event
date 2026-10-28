package modele;

public class Consommable extends Besoin {
    private String perissable;

    public Consommable(int id, String nom, double prix, int quantite, Personne acheteur, String perissable) {
        super(id, nom, prix, quantite, acheteur);
        this.perissable = perissable;
    }

    public String getPerissable() {
        return perissable;
    }

    public void setPerissable(String perissable) {
        this.perissable = perissable;
    }
    
}
