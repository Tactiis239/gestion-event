package modele;

import java.time.LocalDate;

public class Emprunt {
    private int id;
    private final int idMedia;
    private final int idUtilisateur;
    private final LocalDate dateEmprunt;
    private final LocalDate dateRetourPrevue;
    private final LocalDate dateRetourEffective; // null tant que le média n'est pas rendu

    public Emprunt(int id, int idMedia, int idUtilisateur, LocalDate dateEmprunt,
                   LocalDate dateRetourPrevue, LocalDate dateRetourEffective) {
        this.id = id;
        this.idMedia = idMedia;
        this.idUtilisateur = idUtilisateur;
        this.dateEmprunt = dateEmprunt;
        this.dateRetourPrevue = dateRetourPrevue;
        this.dateRetourEffective = dateRetourEffective;
    }

    public boolean estRendu() {
        return dateRetourEffective != null;
    }

    /** Le retard se CALCULE, il n'est pas stocké en base (pas de colonne EN_RETARD). */
    public boolean estEnRetard(LocalDate aujourdhui) {
        return !estRendu() && aujourdhui.isAfter(dateRetourPrevue);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdMedia() { return idMedia; }
    public int getIdUtilisateur() { return idUtilisateur; }
    public LocalDate getDateEmprunt() { return dateEmprunt; }
    public LocalDate getDateRetourPrevue() { return dateRetourPrevue; }
    public LocalDate getDateRetourEffective() { return dateRetourEffective; }

    @Override
    public String toString() {
        return "Emprunt #" + id + " (média " + idMedia + ", à rendre avant le " + dateRetourPrevue + ")";
    }
}
