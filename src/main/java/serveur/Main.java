package serveur;

import modele.Personne;
import serveur.bdd.ConnexionBD;
import serveur.bdd.InitBase;
import serveur.dao.PersonneDao;
import serveur.service.Authentification;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/** Point d'entrée provisoire : crée la base et affiche le nombre de lignes de chaque table. */
public class Main {

    private static final String[] TABLES = {"personne", "evenement", "organiser", "invitation", "besoin", "message"};

    public static void main(String[] args) throws SQLException {
        InitBase.initialiser();
        System.out.println("Base prête :");
        try (Connection c = ConnexionBD.get(); Statement st = c.createStatement()) {
            for (String table : TABLES) {
                try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + table)) {
                    rs.next();
                    System.out.println("  " + table + " : " + rs.getInt(1) + " ligne(s)");
                }
            }
        }

        // --- Test PersonneDao ---
        PersonneDao personnes = new PersonneDao();
        System.out.println("\nPersonneDao :");
        System.out.println("  par e-mail : " + personnes.trouverParEmail("alice@example.org").map(Personne::getNomComplet).orElse("introuvable"));
        System.out.println("  par id 2   : " + personnes.trouver(2).map(Personne::getNomComplet).orElse("introuvable"));
        System.out.println("  id 999     : " + personnes.trouver(999).map(Personne::getNomComplet).orElse("introuvable"));
        System.out.println("  connexion  : " + personnes.trouverParEmail("alice@example.org").get().seConnecter("alice@example.org", "motdepasse"));
        System.out.println("  tous       : " + personnes.trouverTous().size() + " personne(s)");

        Authentification authentification = new Authentification(personnes);

        try {
            Personne personne = authentification.connecter("alice@example.org", "vrai_mdp");
            System.out.println("Connecté en tant que " + personne.getNomComplet());
        } catch (Exception e) {
            System.out.println(e.toString());
            System.out.println("Échec de la connexion (mail ou mot de passe incorrect)");
        }

        try {
            Personne personne = authentification.connecter("alice@example.com", "motdepasse");
            System.out.println("Connecté en tant que " + personne.getNomComplet());
        } catch (Exception e) {
            System.out.println(e.toString());
            System.out.println("Échec de la connexion (mail ou mot de passe incorrect)");
        }

        try {
            Personne personne = authentification.connecter("alice@example.org", "motdepasse");
            System.out.println("Connecté en tant que " + personne.getNomComplet());
        } catch (Exception e) {
            System.out.println(e.toString());
            System.out.println("Échec de la connexion (mail ou mot de passe incorrect)");
        }
    }
}
