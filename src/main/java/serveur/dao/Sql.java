package serveur.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Conversions Java <-> SQLite partagées par les DAO. */
final class Sql {
    private Sql() { }

    static int cleGeneree(PreparedStatement ps) throws SQLException {
        try (ResultSet cles = ps.getGeneratedKeys()) {
            cles.next();
            return cles.getInt(1);
        }
    }

    static void setTexte(PreparedStatement ps, int i, String v) throws SQLException {
        if (v == null) ps.setNull(i, Types.VARCHAR); else ps.setString(i, v);
    }

    static void setEntier(PreparedStatement ps, int i, Integer v) throws SQLException {
        if (v == null) ps.setNull(i, Types.INTEGER); else ps.setInt(i, v);
    }

    static void setBooleen(PreparedStatement ps, int i, Boolean v) throws SQLException {
        if (v == null) ps.setNull(i, Types.INTEGER); else ps.setInt(i, v ? 1 : 0);
    }

    static void setOctets(PreparedStatement ps, int i, byte[] v) throws SQLException {
        if (v == null) ps.setNull(i, Types.BLOB); else ps.setBytes(i, v);
    }

    static String versTexte(LocalDateTime d) {
        return d == null ? null : d.withSecond(0).withNano(0).toString();
    }

    static String versTexte(LocalDate d) {
        return d == null ? null : d.toString();
    }

    static LocalDateTime dateHeure(ResultSet rs, String colonne) throws SQLException {
        String v = rs.getString(colonne);
        return v == null ? null : LocalDateTime.parse(v);
    }

    static LocalDate date(ResultSet rs, String colonne) throws SQLException {
        String v = rs.getString(colonne);
        return v == null ? null : LocalDate.parse(v);
    }
}
