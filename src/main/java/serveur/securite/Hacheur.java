package serveur.securite;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Hachage de mots de passe avec PBKDF2 (fourni par le JDK, aucune bibliothèque).
 * Un sel aléatoire par compte + un grand nombre d'itérations : on ne stocke jamais le mot de passe.
 * N'utilisez PAS SHA-256 seul pour un mot de passe : il est beaucoup trop rapide à attaquer.
 */
public final class Hacheur {

    private static final int ITERATIONS = 310_000;
    private static final int LONGUEUR_BITS = 256;
    private static final int TAILLE_SEL = 16;
    private static final SecureRandom ALEA = new SecureRandom();

    private Hacheur() { }

    public static String genererSel() {
        byte[] sel = new byte[TAILLE_SEL];
        ALEA.nextBytes(sel);
        return Base64.getEncoder().encodeToString(sel);
    }

    public static String hacher(String motDePasse, String selBase64) {
        PBEKeySpec spec = new PBEKeySpec(motDePasse.toCharArray(),
                Base64.getDecoder().decode(selBase64), ITERATIONS, LONGUEUR_BITS);
        try {
            byte[] hash = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Algorithme de hachage indisponible", e);
        } finally {
            spec.clearPassword();
        }
    }

    /** Comparaison en temps constant : ne révèle pas à partir de quel caractère les hachés diffèrent. */
    public static boolean verifier(String motDePasse, String selBase64, String hashAttendu) {
        byte[] calcule = hacher(motDePasse, selBase64).getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(calcule, hashAttendu.getBytes(StandardCharsets.UTF_8));
    }
}
