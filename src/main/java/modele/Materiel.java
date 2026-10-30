package modele;

/** Matériel à louer ou à vendre. */
public abstract class Materiel extends Besoin {
    private final boolean aRendre;
    private EtatMateriel etat;

    protected Materiel(int id, String nom, double prix, int quantite, boolean aRendre, EtatMateriel etat) {
        super(id, nom, prix, quantite);
        if (etat == null) {
            throw new IllegalArgumentException("L'état du matériel est obligatoire");
        }
        this.aRendre = aRendre;
        this.etat = etat;
    }

    public boolean isARendre() {
        return aRendre;
    }

    public EtatMateriel getEtat() {
        return etat;
    }

    public void setEtat(EtatMateriel etat) {
        if (etat == null) {
            throw new IllegalArgumentException("L'état du matériel est obligatoire");
        }
        this.etat = etat;
    }

    @Override
    public String toString() {
        return super.toString() + " - état " + etat + (aRendre ? ", à rendre" : "");
    }
}
