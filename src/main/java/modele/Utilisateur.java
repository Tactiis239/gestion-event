package modele;

public class Utilisateur {
    private int id;
    private final String email;
    private final String nom;
    private final String mdpHash;
    private final String sel;
    private final Role role;

    public Utilisateur(int id, String email, String nom, String mdpHash, String sel, Role role) {
        this.id = id;
        this.email = email;
        this.nom = nom;
        this.mdpHash = mdpHash;
        this.sel = sel;
        this.role = role;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getEmail() { return email; }
    public String getNom() { return nom; }
    public String getMdpHash() { return mdpHash; }
    public String getSel() { return sel; }
    public Role getRole() { return role; }

    @Override
    public String toString() {
        return nom + " <" + email + "> [" + role + "]";
    }
}
