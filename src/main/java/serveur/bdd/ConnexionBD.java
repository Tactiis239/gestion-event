package serveur.bdd;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Point d'accès unique à la base SQLite. L'URL vient de db.properties.
 * Chaque appel à get() ouvre une nouvelle connexion : à fermer avec try-with-resources.
 */
public final class ConnexionBD {

    private static final String URL = chargerUrl();

    private ConnexionBD() { }

    private static String chargerUrl() {
        Properties p = new Properties();
        try (InputStream in = ConnexionBD.class.getResourceAsStream("/db.properties")) {
            if (in == null) {
                throw new DaoException("db.properties introuvable : copiez src/main/resources/db.properties.example "
                        + "dans src/main/resources/db.properties", null);
            }
            p.load(in);
        } catch (IOException e) {
            throw new DaoException("Lecture de db.properties impossible", e);
        }
        return p.getProperty("db.url");
    }

    public static Connection get() {
        try {
            Connection c = DriverManager.getConnection(URL);
            try (Statement st = c.createStatement()) {
                // SQLite ignore les clés étrangères par défaut : il faut les activer à chaque connexion
                st.execute("PRAGMA foreign_keys = ON");
                // Plusieurs clients en même temps : attendre la fin d'une écriture au lieu d'échouer
                st.execute("PRAGMA busy_timeout = 5000");
            } catch (SQLException e) {
                c.close();
                throw e;
            }
            return c;
        } catch (SQLException e) {
            throw new DaoException("Connexion à la base impossible : " + URL, e);
        }
    }
}
