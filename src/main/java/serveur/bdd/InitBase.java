package serveur.bdd;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

/** Prépare la base au démarrage à partir des scripts du classpath (bdd/schema.sql, bdd/donnees_test.sql). */
public final class InitBase {

    private InitBase() { }

    /**
     * Crée les tables manquantes, puis insère les comptes de test si la base est vide (F4).
     * Sans effet sur une base déjà remplie : les données sont conservées entre deux démarrages (F30).
     */
    public static void initialiser() {
        // Une seule transaction : si une insertion échoue, rien n'est gardé et le prochain démarrage recommence
        Transaction.executerSansRetour(c -> {
            executerScript(c, "/schema.sql");
            if (estVide(c)) {
                executerScript(c, "/donnees_test.sql");
            }
        });
    }

    private static boolean estVide(Connection c) {
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM personne")) {
            return rs.next() && rs.getInt(1) == 0;
        } catch (SQLException e) {
            throw new DaoException("Lecture de la table personne impossible", e);
        }
    }

    private static void executerScript(Connection c, String ressource) {
        String sql = lire(ressource);
        try (Statement st = c.createStatement()) {
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
