package modele;

public class MaterielSon extends Materiel {
    private int decibels;
    private int puissanceWatts;

    public MaterielSon(int id, String nom, double prix, int quantite, Personne acheteur, boolean aRendre, EtatMateriel etat, int decibels, int puissanceWatts) {
        super(id, nom, prix, quantite, acheteur, aRendre, etat);
        this.decibels = decibels;
        this.puissanceWatts = puissanceWatts;
    }

    public int getDecibels() {
        return decibels;
    }

    public void setDecibels(int decibels) {
        this.decibels = decibels;
    }

    public int getPuissanceWatts() {
        return puissanceWatts;
    }

    public void setPuissanceWatts(int puissanceWatts) {
        this.puissanceWatts = puissanceWatts;
    }
}
