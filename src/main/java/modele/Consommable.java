package modele;

/** Consommable = besoin périssable ou non. */
public class Consommable extends Besoin {
    private final boolean perissable;

    public Consommable(int id, String nom, double prix, int quantite, boolean perissable) {
        super(id, nom, prix, quantite);
        this.perissable = perissable;
    }

    public boolean isPerissable() {
        return perissable;
    }

    @Override
    public String toString() {
        return super.toString() + (perissable ? " - périssable" : "");
    }
}
