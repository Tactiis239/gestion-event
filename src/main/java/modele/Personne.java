package modele;

import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDate;
import java.time.Period;

/** Une personne : utilisateur de l'application. */
public class Personne {
    private int id;
    private String nom;
    private String prenom;
    private String telephone;
    private String email;
    private String mdpHash;
    private LocalDate dateNaissance;

    public Personne(int id, String nom, String prenom, String telephone, String email, String mdpHash,
                    LocalDate dateNaissance) {
        if (nom == null || nom.isBlank() || prenom == null || prenom.isBlank()) {
            throw new IllegalArgumentException("Le nom et le prénom sont obligatoires");
        }
        if (email == null || email.isBlank() || mdpHash == null || mdpHash.isBlank()) {
            throw new IllegalArgumentException("L'e-mail et le mot de passe sont obligatoires");
        }
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.email = email;
        this.mdpHash = mdpHash;
        this.dateNaissance = dateNaissance;
    }

    /** Vrai si l'e-mail correspond et si le mot de passe correspond au haché bcrypt. */
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

    // Accesseurs

    public int getId() {
        return id;
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

    public String getEmail() {
        return email;
    }

    public String getMdpHash() {
        return mdpHash;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setId(int id) {
        if (this.id != 0) {
            throw new IllegalStateException("L'identifiant est déjà attribué");
        }
        this.id = id;
    }
}
