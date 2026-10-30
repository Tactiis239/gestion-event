package modele;

import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDate;
import java.time.Period;

/** Classe représentant une personne. */
public class Personne {
    private int id;
    private String nom;
    private String prenom;
    private String telephone;
    private byte[] avatar;
    private String email;
    private String mdpHash;
    private LocalDate dateNaissance;

    public Personne(int id, String nom, String prenom, String telephone, byte[] avatar,
                    String email, String mdpHash, LocalDate dateNaissance) {
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
        this.email = email;
        this.mdpHash = mdpHash;
        this.dateNaissance = dateNaissance;
    }

    public boolean seConnecter(String email, String mdp) {
        if (email == null || mdp == null || !this.email.equalsIgnoreCase(email)) {
            return false;
        }
        return BCrypt.checkpw(mdp, mdpHash);
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

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
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

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
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
