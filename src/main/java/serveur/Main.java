package serveur;

import serveur.bdd.ConnexionBD;
import serveur.bdd.InitBase;

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
    }
}
