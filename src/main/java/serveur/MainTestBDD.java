package serveur;

import modele.AccesRefuseException;
import modele.Besoin;
import modele.Consommable;
import modele.EtatMateriel;
import modele.Evenement;
import modele.MaterielSon;
import modele.Message;
import modele.Personne;
import modele.StatutInvitation;
import modele.TypeEvenement;
import serveur.bdd.ConnexionBD;
import serveur.bdd.InitBase;
import serveur.dao.PersonneDao;
import serveur.service.AuthService;
import serveur.service.AuthentificationException;
import serveur.service.ServiceEvenements;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;

/**
 * Livrable BDD : prouve que la base, les DAO, les règles métier et les transactions fonctionnent,
 * en rejouant le scénario de validation (section 11 du cahier des charges).
 * Chaque vérification lève une erreur si le résultat n'est pas celui attendu.
 */
public class MainTestBDD {

    private static int verifications = 0;

    public static void main(String[] args) throws Exception {
        // Repartir d'une base vide (le nom doit correspondre à db.url dans db.properties)
        Files.deleteIfExists(Path.of("evenements.db"));
        InitBase.initialiser();
        System.out.println("1. Base créée et remplie");
        InitBase.initialiser();
        verifier(compter("SELECT COUNT(*) FROM personne") == 4,
                "second démarrage : données conservées, comptes de test non réinsérés (F4, F30)");

        PersonneDao personnes = new PersonneDao();
        AuthService auth = new AuthService(personnes);
        ServiceEvenements service = ServiceEvenements.creerParDefaut();

        // ------------------------------------------------------------ authentification
        System.out.println("\n2. Authentification (bcrypt)");
        Personne alice = auth.seConnecter("alice@example.org", "motdepasse");
        Personne bob = auth.seConnecter("BOB@example.org", "motdepasse");
        Personne chloe = auth.seConnecter("chloe@example.org", "motdepasse");
        System.out.println("   Connectés : " + alice.getNomComplet() + ", " + bob.getNomComplet()
                + ", " + chloe.getNomComplet() + " (" + alice.getAge() + " ans pour Alice)");
        String msgMauvaisMdp = echec(() -> auth.seConnecter("alice@example.org", "mauvais"),
                AuthentificationException.class);
        String msgEmailInconnu = echec(() -> auth.seConnecter("inconnu@example.org", "motdepasse"),
                AuthentificationException.class);
        verifier(msgMauvaisMdp.equals(msgEmailInconnu), "même message pour mauvais mdp et e-mail inconnu (F2)");
        Personne emma = auth.creerCompte("Moreau", "Emma", "emma@example.org", "secret", null);
        verifier(auth.seConnecter(" emma@example.org ", "secret").equals(emma),
                "compte créé (haché bcrypt) puis connexion, espaces autour de l'e-mail ignorés");
        echec(() -> auth.creerCompte("Moreau", "Emma", "EMMA@example.org", "autre", null), IllegalStateException.class);
        verifier(true, "même e-mail à la casse près refusé");

        // ------------------------------------------------------------ données de test
        System.out.println("\n3. Données de test relues (Factory sur la colonne type)");
        Evenement anniv = service.consulterEvenement(alice, 1);
        System.out.println("   " + anniv);
        for (Besoin b : anniv.getBesoins()) {
            System.out.println("     - " + b);
        }
        verifier(anniv.getBesoins().size() == 3, "3 besoins de types différents relus");
        verifier(service.evenementsOuInvite(alice).size() == 1, "Alice voit l'anniversaire où elle est invitée");

        // ------------------------------------------------------------ scénario
        System.out.println("\n4. Scénario de validation");
        Evenement soiree = service.creerEvenement(alice, "Soirée de fin de semestre", "Chez Alice", 0,
                "Apportez vos jeux !", LocalDateTime.now().plusMonths(2).withSecond(0).withNano(0),
                TypeEvenement.SOIREE);
        int idSoiree = soiree.getId();
        System.out.println("   Alice crée : " + soiree);
        verifier(service.evenementsOrganises(alice).size() == 1, "Alice est organisatrice de la soirée (règle 4)");

        service.inviter(alice, idSoiree, "bob@example.org");
        echec(() -> service.inviter(alice, idSoiree, "bob@example.org"), IllegalStateException.class);
        verifier(true, "double invitation refusée (F14)");

        service.repondreInvitation(bob, idSoiree, true);
        System.out.println("   Bob accepte l'invitation");

        Besoin jus = service.ajouterBesoin(alice, idSoiree, new Consommable(0, "Bouteille de jus", 2, 6, false));
        Besoin enceinte = service.ajouterBesoin(alice, idSoiree,
                new MaterielSon(0, "Enceinte", 30, 1, true, EtatMateriel.BON, 95, 60));
        echec(() -> service.ajouterBesoin(bob, idSoiree, new Consommable(0, "Chips", 1.5, 3, false)),
                AccesRefuseException.class);
        verifier(true, "Bob (invité) ne peut pas ajouter de besoin (règle 1)");

        echec(() -> service.attribuerBesoin(bob, idSoiree, jus.getId(), alice.getId()), AccesRefuseException.class);
        verifier(true, "Bob (invité) ne peut pas attribuer un besoin à quelqu'un d'autre (règle 1)");
        service.prendreEnCharge(bob, idSoiree, enceinte.getId());
        echec(() -> service.ajouterOrganisateur(alice, idSoiree, "bob@example.org"), IllegalStateException.class);
        verifier(true, "Bob, déjà invité, ne peut pas devenir co-organisateur");
        echec(() -> service.prendreEnCharge(chloe, idSoiree, jus.getId()), IllegalStateException.class);
        verifier(true, "Chloé (non invitée) ne peut pas prendre de besoin (règle 3)");

        String refus = echec(() -> service.inviter(bob, idSoiree, "chloe@example.org"), AccesRefuseException.class);
        System.out.println("   Bob tente d'inviter Chloé -> " + refus);
        echec(() -> service.inviter(bob, idSoiree, "inconnu@example.org"), AccesRefuseException.class);
        verifier(true, "un invité ne peut pas savoir si un e-mail existe (droits vérifiés avant la recherche)");

        service.envoyerMessage(alice, idSoiree, bob.getId(), "Merci pour l'enceinte !");
        echec(() -> service.envoyerMessage(chloe, idSoiree, alice.getId(), "Je peux venir ?"),
                AccesRefuseException.class);
        verifier(true, "Chloé (non participante) ne peut pas écrire dans la soirée (règle 3)");
        echec(() -> service.consulterEvenement(chloe, idSoiree), AccesRefuseException.class);
        verifier(true, "Chloé (ni organisatrice ni invitée) ne peut pas consulter la soirée (F6)");

        // ------------------------------------------------------------ relecture = redémarrage du serveur
        System.out.println("\n5. Relecture complète depuis la base (comme après un redémarrage)");
        ServiceEvenements apresRedemarrage = ServiceEvenements.creerParDefaut();
        Evenement relue = apresRedemarrage.consulterEvenement(alice, idSoiree);
        System.out.println("   " + relue);
        for (Besoin b : relue.getBesoins()) {
            System.out.println("     - " + b);
        }
        verifier(relue.getInvitationDe(bob).orElseThrow().getStatut() == StatutInvitation.ACCEPTEE,
                "invitation de Bob ACCEPTEE");
        verifier(bob.equals(relue.getBesoin(enceinte.getId()).orElseThrow().getAcheteur()),
                "Bob achète l'enceinte");
        verifier(relue.calculerBudget() == 42.0, "budget total = 42 €");
        verifier(relue.getParticipants().size() == 2, "2 participants (Alice + Bob)");
        for (Message m : apresRedemarrage.messagesDe(bob)) {
            System.out.println("   Message reçu par Bob : " + m.afficher());
        }
        verifier(apresRedemarrage.messagesDe(bob).size() == 1, "Bob a 1 message (F24)");

        // ------------------------------------------------------------ transaction
        System.out.println("\n6. Transaction : Bob refuse après avoir accepté (F20)");
        service.repondreInvitation(bob, idSoiree, false);
        Evenement apresRefus = service.consulterEvenement(alice, idSoiree);
        verifier(!apresRefus.getBesoin(enceinte.getId()).orElseThrow().estAchete(),
                "l'enceinte n'a plus d'acheteur : elle revient aux organisateurs");
        verifier(apresRefus.getInvitationDe(bob).orElseThrow().getStatut() == StatutInvitation.REFUSEE,
                "invitation REFUSEE");

        // ------------------------------------------------------------ modification, besoins (F7, F17, F18)
        System.out.println("\n6 bis. Modification et gestion des besoins par l'organisatrice");
        service.modifierEvenement(alice, idSoiree, "Soirée de fin d'année", "Chez Alice", 5, null,
                apresRefus.getDateEvenement(), TypeEvenement.SOIREE);
        echec(() -> service.modifierEvenement(bob, idSoiree, "Piratée", null, 0, null,
                apresRefus.getDateEvenement(), TypeEvenement.AUTRE), AccesRefuseException.class);
        verifier(service.consulterEvenement(alice, idSoiree).getNom().equals("Soirée de fin d'année"),
                "Alice modifie la soirée, Bob ne peut pas (F7, règle 1)");
        service.attribuerBesoin(alice, idSoiree, jus.getId(), alice.getId());
        verifier(alice.equals(service.consulterEvenement(alice, idSoiree).getBesoin(jus.getId()).orElseThrow().getAcheteur()),
                "Alice attribue le jus à une participante (F18)");
        service.supprimerBesoin(alice, idSoiree, jus.getId());
        verifier(service.consulterEvenement(alice, idSoiree).getBesoin(jus.getId()).isEmpty(), "Alice supprime le jus (F17)");

        // ------------------------------------------------------------ mariage, co-organisateur
        System.out.println("\n7. Mariage (budget traiteur automatique) et co-organisateur");
        Evenement mariage = service.creerEvenement(chloe, "Mariage de Chloé", "Lille", 0, null,
                LocalDateTime.now().plusYears(1).withSecond(0).withNano(0), TypeEvenement.MARIAGE);
        service.ajouterOrganisateur(chloe, mariage.getId(), "david@example.org");
        Evenement mariageRelu = service.consulterEvenement(chloe, mariage.getId());
        System.out.println("   " + mariageRelu + ", organisateurs : " + mariageRelu.getOrganisateurs().size());
        verifier(mariageRelu.getBesoins().size() == 1, "budget traiteur ajouté une fois");
        verifier(mariageRelu.getOrganisateurs().size() == 2, "David est co-organisateur");

        // ------------------------------------------------------------ suppression
        System.out.println("\n8. Suppression de la soirée (ON DELETE CASCADE)");
        echec(() -> service.supprimerEvenement(bob, idSoiree), AccesRefuseException.class);
        service.supprimerEvenement(alice, idSoiree);
        verifier(compter("SELECT COUNT(*) FROM besoin WHERE id_evenement = " + idSoiree) == 0
                        && compter("SELECT COUNT(*) FROM invitation WHERE id_evenement = " + idSoiree) == 0
                        && compter("SELECT COUNT(*) FROM message WHERE id_evenement = " + idSoiree) == 0
                        && compter("SELECT COUNT(*) FROM organisateur WHERE id_evenement = " + idSoiree) == 0,
                "besoins, invitations, messages et organisateurs supprimés avec l'événement");

        System.out.println("\nTout fonctionne (" + verifications + " vérifications).");
    }

    // ------------------------------------------------------------------ outils de test

    private static void verifier(boolean condition, String description) {
        if (!condition) {
            throw new AssertionError("ÉCHEC : " + description);
        }
        verifications++;
        System.out.println("   OK  " + description);
    }

    /** Vérifie que l'action lève bien l'exception attendue et renvoie son message. */
    private static String echec(Runnable action, Class<? extends RuntimeException> attendue) {
        try {
            action.run();
        } catch (RuntimeException e) {
            if (attendue.isInstance(e)) {
                return e.getMessage();
            }
            throw new AssertionError("Exception inattendue : " + e, e);
        }
        throw new AssertionError("Une " + attendue.getSimpleName() + " était attendue");
    }

    private static int compter(String sql) throws Exception {
        try (Connection c = ConnexionBD.get(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            return rs.getInt(1);
        }
    }
}
