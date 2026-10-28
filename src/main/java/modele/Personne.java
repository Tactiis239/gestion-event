package modele;

import java.time.LocalDate;

public class Personne {
    private final int id;
    private String nom;
    private String prenom;
    private String telephone;
    public byte[] avatar;
    private String email;
    private String mdpHash;
    private LocalDate dateNaissance;

    public Personne(int id, String nom, String prenom, String telephone, byte[] avatar, String email, String mdpHash, LocalDate dateNaissance) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.avatar = avatar;
        this.email = email;
        this.mdpHash = mdpHash;
        this.dateNaissance = dateNaissance;
        
    }

    public int getId() {
        return id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

     public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public String getMdpHash() {
        return mdpHash;
    }

    public void setMdpHash(String mdpHash) {
        this.mdpHash = mdpHash;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }
    
    public String getNomComplet() {
        return nom + " " + prenom;
    }
    public int getAge() {
        if (dateNaissance == null) {
            return 0; // ou une autre valeur par défaut
        }
        if (dateNaissance.getMonth().getValue() > LocalDate.now().getMonth().getValue()) {
            return LocalDate.now().getYear() - dateNaissance.getYear() - 1;
        }
        else {
            return LocalDate.now().getYear() - dateNaissance.getYear();
        }
    }

    boolean verifierMotDePasse(String motDePasse) {
        // Implémentation de la vérification du mot de passe B-Crypt
        return false; // Remplacez par la logique réelle de vérification du mot de passe
    }

    @Override
    public String toString() {
        return "Utilisateur [id=" + id + ", nom=" + nom + ", prenom=" + prenom + ", telephone=" + telephone + ", email="
                + email + ", dateNaissance=" + dateNaissance + "]";
    }
}
