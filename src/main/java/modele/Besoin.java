package modele;

/** Ce qu'il faut acheter ou apporter pour un événement.*/
public abstract class Besoin {
    private int id;
    private String nom;
    private double prix;
    private int quantite;
    private Personne acheteur;

    protected Besoin(int id, String nom, double prix, int quantite) {
        if (nom == null || nom.isBlank()) {
            throw new IllegalArgumentException("Le nom du besoin est obligatoire");
        }
        if (prix < 0) {
            throw new IllegalArgumentException("Le prix ne peut pas être négatif");
        }
        if (quantite <= 0) {
            throw new IllegalArgumentException("La quantité doit être strictement positive");
        }
        this.id = id;
        this.nom = nom;
        this.prix = prix;
        this.quantite = quantite;
    }

    /**
     * Attribue l'achat à une personne.
     */
    public void acheter(Personne p) {
        if (p == null) {
            throw new IllegalArgumentException("L'acheteur est obligatoire");
        }
        this.acheteur = p;
    }

    public void liberer() {
        this.acheteur = null;
    }

    public boolean estAchete() {
        return acheteur != null;
    }

    public double calculerCout() {
        return prix * quantite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (this.id != 0) {
            throw new IllegalStateException("L'identifiant est déjà attribué");
        }
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public double getPrix() {
        return prix;
    }

    public int getQuantite() {
        return quantite;
    }

    public Personne getAcheteur() {
        return acheteur;
    }

    @Override
    public String toString() {
        String qui = estAchete() ? acheteur.getNomComplet() : "les organisateurs";
        return String.format("%s x%d (%.2f €) [%s] acheté par %s", nom, quantite, calculerCout(), getClass().getSimpleName(), qui);
    }
}
