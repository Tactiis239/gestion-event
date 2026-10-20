package modele;

public class JeuVideo extends Media {
    private final String plateforme;

    public JeuVideo(int id, String titre, int nbExemplaires, int nbDisponibles, String plateforme) {
        super(id, titre, nbExemplaires, nbDisponibles);
        this.plateforme = plateforme;
    }

    public String getPlateforme() { return plateforme; }

    @Override
    public TypeMedia getType() { return TypeMedia.JEU_VIDEO; }

    @Override
    public String decrire() { return "Jeu vidéo « " + getTitre() + " » sur " + plateforme; }
}
