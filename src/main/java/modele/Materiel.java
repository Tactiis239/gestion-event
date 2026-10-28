package modele;

public class Materiel extends Besoin {
    private boolean aRendre;
    private EtatMateriel etat;

    public Materiel(int id, String nom, double prix, int quantite, Personne acheteur, boolean aRendre, EtatMateriel etat) {
        super(id, nom, prix, quantite, acheteur);
        this.aRendre = aRendre;
        this.etat = etat;
    }

    public boolean isARendre() {
        return aRendre;
    }

    public void setARendre(boolean aRendre) {
        this.aRendre = aRendre;
    }

    public EtatMateriel getEtat() {
        return etat;
    }

    public void setEtat(EtatMateriel etat) {
        this.etat = etat;
    }
    
}
