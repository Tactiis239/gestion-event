package modele;

public class MaterielLumiere extends Materiel {
    private int lumens;
    private String couleur;

    public MaterielLumiere(int id, String nom, double prix, int quantite, Personne acheteur, boolean aRendre, EtatMateriel etat, int lumens, String couleur) {
        super(id, nom, prix, quantite, acheteur, aRendre, etat);
        this.lumens = lumens;
        this.couleur = couleur;
    }

    public int getLumens() {
        return lumens;
    }

    public void setLumens(int lumens) {
        this.lumens = lumens;
    }

    public String getCouleur() {
        return couleur;
    }

    public void setCouleur(String couleur) {
        this.couleur = couleur;
    }
}
