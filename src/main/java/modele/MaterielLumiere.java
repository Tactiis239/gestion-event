package modele;

/** Matériel de lumière (projecteur, lampe, etc.). */
public class MaterielLumiere extends Materiel {
    private final int lumens;
    private final String couleur;

    public MaterielLumiere(int id, String nom, double prix, int quantite, boolean aRendre, EtatMateriel etat,
                           int lumens, String couleur) {
        super(id, nom, prix, quantite, aRendre, etat);
        this.lumens = lumens;
        this.couleur = couleur;
    }

    public int getLumens() {
        return lumens;
    }

    public String getCouleur() {
        return couleur;
    }

    @Override
    public String toString() {
        return super.toString() + ", " + lumens + " lm, " + couleur;
    }
}
