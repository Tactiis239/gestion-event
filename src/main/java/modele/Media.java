package modele;

/** Classe mère abstraite : le polymorphisme se fait sur getType() et decrire(). */
public abstract class Media {
    private int id;
    private final String titre;
    private final int nbExemplaires;
    private final int nbDisponibles;

    protected Media(int id, String titre, int nbExemplaires, int nbDisponibles) {
        this.id = id;
        this.titre = titre;
        this.nbExemplaires = nbExemplaires;
        this.nbDisponibles = nbDisponibles;
    }

    public abstract TypeMedia getType();

    public abstract String decrire();

    public boolean estDisponible() {
        return nbDisponibles > 0;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitre() { return titre; }
    public int getNbExemplaires() { return nbExemplaires; }
    public int getNbDisponibles() { return nbDisponibles; }

    @Override
    public String toString() {
        return "#" + id + " " + decrire() + " (" + nbDisponibles + "/" + nbExemplaires + " dispo)";
    }
}
