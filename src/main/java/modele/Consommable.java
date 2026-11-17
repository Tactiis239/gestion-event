package modele;

/** Besoin consommable (boisson, nourriture…), périssable ou non. */
public class Consommable extends Besoin {
    private boolean perissable;

    public Consommable(int id, String nom, double prix, int quantite, boolean perissable) {
        super(id, nom, prix, quantite);
        this.perissable = perissable;
    }

    // Accesseurs

    public boolean isPerissable() {
        return perissable;
    }
}
