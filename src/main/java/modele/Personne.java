package modele;

import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDate;
import java.time.Period;

/** Classe représentant une personne. */
public class Personne {
    private int id;
    private final String nom;
    private final String prenom;
    private final String telephone;
    private final byte[] avatar;
    private final String email;
    private final String mdpHash;
    private final LocalDate dateNaissance;

    public Personne(int id, String nom, String prenom, String telephone, byte[] avatar,
                    String email, String mdpHash, LocalDate dateNaissance) {
        if (nom == null || nom.isBlank() || prenom == null || prenom.isBlank()) {
            throw new IllegalArgumentException("Le nom et le prénom sont obligatoires");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("L'e-mail est obligatoire");
        }
        if (mdpHash == null || mdpHash.isBlank()) {
            throw new IllegalArgumentException("Le haché du mot de passe est obligatoire");
        }
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.avatar = avatar;
        this.email = normaliserEmail(email);
        this.mdpHash = mdpHash;
        this.dateNaissance = dateNaissance;
    }

    public boolean seConnecter(String email, String mdp) {
        if (email == null || mdp == null || !this.email.equals(normaliserEmail(email))) {
            return false;
        }
        return BCrypt.checkpw(mdp, mdpHash);
    }

    /** Forme unique d'un e-mail (espaces retirés, minuscules), à l'écriture comme à la recherche. */
    public static String normaliserEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    public String getNomComplet() {
        return prenom + " " + nom;
    }

    public int getAge() {
        if (dateNaissance == null) {
            return 0;
        }
        return Period.between(dateNaissance, LocalDate.now()).getYears();
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

    public String getPrenom() {
        return prenom;
    }

    public String getTelephone() {
        return telephone;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public String getEmail() {
        return email;
    }

    public String getMdpHash() {
        return mdpHash;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Personne autre)) return false;
        return id != 0 && id == autre.id;
    }

    @Override
    public int hashCode() {
        return id != 0 ? Integer.hashCode(id) : System.identityHashCode(this);
    }

    @Override
    public String toString() {
        return "Personne[id=" + id + ", " + getNomComplet() + ", " + email + "]";
    }
}
