package serveur.bdd;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Point d'accès unique à la base. Les paramètres viennent de db.properties,
 * jamais écrits en dur dans le code.
 * Chaque appel à get() ouvre une nouvelle connexion : à fermer avec try-with-resources.
 */
public final class ConnexionBD {

    private static final Properties PROPS = charger();

    private ConnexionBD() { }

    private static Properties charger() {
        Properties p = new Properties();
        try (InputStream in = ConnexionBD.class.getResourceAsStream("/db.properties")) {
            if (in == null) {
                throw new DaoException("db.properties introuvable : copiez db.properties.example", null);
            }
            p.load(in);
        } catch (IOException e) {
            throw new DaoException("Lecture de db.properties impossible", e);
        }
        return p;
    }

    public static Connection get() {
        String url = PROPS.getProperty("db.url");
        try {
            if (url.startsWith("jdbc:sqlite")) {
                Connection c = DriverManager.getConnection(url);
                // SQLite ignore les clés étrangères par défaut : il faut les activer à chaque connexion
                try (Statement st = c.createStatement()) {
                    st.execute("PRAGMA foreign_keys = ON");
                }
                return c;
            }
            return DriverManager.getConnection(url, PROPS.getProperty("db.user"), PROPS.getProperty("db.password"));
        } catch (SQLException e) {
            throw new DaoException("Connexion à la base impossible : " + url, e);
        }
    }
}
