package modele;

/** Matériel de sonorisation. */
public class MaterielSon extends Materiel {
    private int decibels;
    private int puissanceWatts;

    public MaterielSon(int id, String nom, double prix, int quantite, boolean aRendre, EtatMateriel etat,
                       int decibels, int puissanceWatts) {
        super(id, nom, prix, quantite, aRendre, etat);
        this.decibels = decibels;
        this.puissanceWatts = puissanceWatts;
    }

    // Accesseurs

    public int getDecibels() {
        return decibels;
    }

    public int getPuissanceWatts() {
        return puissanceWatts;
    }
}
