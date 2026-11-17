package modele;

/** Matériel d'éclairage. */
public class MaterielLumiere extends Materiel {
    private int lumens;
    private String couleur;

    public MaterielLumiere(int id, String nom, double prix, int quantite, boolean aRendre, EtatMateriel etat,
                           int lumens, String couleur) {
        super(id, nom, prix, quantite, aRendre, etat);
        this.lumens = lumens;
        this.couleur = couleur;
    }

    // Accesseurs

    public int getLumens() {
        return lumens;
    }

    public String getCouleur() {
        return couleur;
    }
}
