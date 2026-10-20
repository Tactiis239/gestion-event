package serveur;

import serveur.bdd.InitBase;
import serveur.dao.EmpruntDao;
import serveur.dao.MediaDao;
import serveur.dao.UtilisateurDao;
import modele.Emprunt;
import modele.Media;
import modele.Role;
import modele.Utilisateur;
import serveur.service.AuthService;
import serveur.service.EmpruntService;
import serveur.service.MediaIndisponibleException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** Livrable de S2 : ce programme prouve que la base, les DAO et la transaction fonctionnent. */
public class MainTestBDD {

    public static void main(String[] args) throws Exception {
        // Repartir d'une base vide à chaque lancement (SQLite : un simple fichier)
        Files.deleteIfExists(Path.of("evenements.db"));
        InitBase.executerScript("/schema.sql");
        InitBase.executerScript("/donnees_test.sql");
        System.out.println("1. Base créée et remplie");

        UtilisateurDao utilisateurs = new UtilisateurDao();
        MediaDao medias = new MediaDao();
        EmpruntDao emprunts = new EmpruntDao();
        AuthService auth = new AuthService(utilisateurs);
        EmpruntService service = new EmpruntService(medias, emprunts);

        System.out.println("\n2. Catalogue (polymorphisme : chaque sous-classe se décrit elle-même)");
        for (Media m : medias.trouverTous()) {
            System.out.println("   " + m);
        }

        System.out.println("\n3. Comptes et authentification");
        int idAlice = auth.inscrire("alice@example.org", "Alice", "motdepasse123", Role.ADHERENT);
        int idBob = auth.inscrire("bob@example.org", "Bob", "autreMotDePasse", Role.ADHERENT);
        Optional<Utilisateur> ok = auth.seConnecter("alice@example.org", "motdepasse123");
        Optional<Utilisateur> ko = auth.seConnecter("alice@example.org", "mauvais");
        System.out.println("   Bon mot de passe     -> " + ok.map(Utilisateur::toString).orElse("REFUSÉ"));
        System.out.println("   Mauvais mot de passe -> " + ko.map(Utilisateur::toString).orElse("REFUSÉ"));

        System.out.println("\n4. Transaction : emprunt du seul exemplaire de « Celeste »");
        int idCeleste = medias.rechercherParTitre("Celeste").get(0).getId();
        Emprunt e1 = service.emprunter(idAlice, idCeleste, 14);
        System.out.println("   Alice : " + e1);
        try {
            service.emprunter(idBob, idCeleste, 14);
            System.out.println("   ERREUR : Bob n'aurait pas dû pouvoir emprunter !");
        } catch (MediaIndisponibleException ex) {
            System.out.println("   Bob   : refusé -> " + ex.getMessage());
        }
        System.out.println("   Stock après : " + medias.trouver(idCeleste).orElseThrow());

        System.out.println("\n5. Emprunts en cours d'Alice, retard calculé (pas stocké)");
        List<Emprunt> enCours = emprunts.trouverEnCours(idAlice);
        for (Emprunt e : enCours) {
            System.out.println("   " + e + " | en retard aujourd'hui : " + e.estEnRetard(LocalDate.now())
                    + " | en retard dans 30 jours : " + e.estEnRetard(LocalDate.now().plusDays(30)));
        }

        System.out.println("\n6. Retour (transaction : retour + stock)");
        service.rendre(e1.getId());
        System.out.println("   Stock après retour : " + medias.trouver(idCeleste).orElseThrow());
        System.out.println("   Emprunts en cours d'Alice : " + emprunts.trouverEnCours(idAlice).size());

        System.out.println("\nTout fonctionne.");
    }
}
