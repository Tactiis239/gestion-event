package modele;

public class Livre extends Media {
    private final int nbPages;

    public Livre(int id, String titre, int nbExemplaires, int nbDisponibles, int nbPages) {
        super(id, titre, nbExemplaires, nbDisponibles);
        this.nbPages = nbPages;
    }

    public int getNbPages() { return nbPages; }

    @Override
    public TypeMedia getType() { return TypeMedia.LIVRE; }

    @Override
    public String decrire() { return "Livre « " + getTitre() + " » (" + nbPages + " p.)"; }
}
