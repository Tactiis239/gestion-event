package modele;

/** Matériel à apporter, éventuellement à rendre après l'événement. */
public abstract class Materiel extends Besoin {
    private boolean aRendre;
    private EtatMateriel etat;

    protected Materiel(int id, String nom, double prix, int quantite, boolean aRendre, EtatMateriel etat) {
        super(id, nom, prix, quantite);
        if (etat == null) {
            throw new IllegalArgumentException("L'état du matériel est obligatoire");
        }
        this.aRendre = aRendre;
        this.etat = etat;
    }

    // Accesseurs

    public boolean isARendre() {
        return aRendre;
    }

    public EtatMateriel getEtat() {
        return etat;
    }
}
