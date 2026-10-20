package serveur.bdd;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

/** Exécute un script SQL du dossier resources (schema.sql, donnees_test.sql). */
public final class InitBase {

    private InitBase() { }

    public static void executerScript(String ressource) {
        String sql = lire(ressource);
        try (Connection c = ConnexionBD.get(); Statement st = c.createStatement()) {
            for (String instruction : sql.split(";")) {
                if (!instruction.isBlank()) {
                    st.execute(instruction);
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Erreur dans le script " + ressource, e);
        }
    }

    private static String lire(String ressource) {
        try (InputStream in = InitBase.class.getResourceAsStream(ressource)) {
            if (in == null) {
                throw new DaoException("Script introuvable : " + ressource, null);
            }
            String texte = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            // On retire les commentaires "-- ..." avant de découper sur les points-virgules
            return texte.lines().map(l -> l.replaceAll("--.*$", "")).collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new DaoException("Lecture impossible : " + ressource, e);
        }
    }
}
