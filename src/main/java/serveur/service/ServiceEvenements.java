package serveur.service;

import modele.*;
import serveur.dao.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Logique métier côté serveur.
 */
public class ServiceEvenements {
    public static final double BUDGET_TRAITEUR_DEFAUT = 1500.0;

    private final PersonneDao personnes;
    private final EvenementDao evenements;
    private final InvitationDao invitations;
    private final BesoinDao besoins;
    private final MessageDao messages;

    public ServiceEvenements(PersonneDao personnes, EvenementDao evenements, InvitationDao invitations,
                             BesoinDao besoins, MessageDao messages) {
        this.personnes = personnes;
        this.evenements = evenements;
        this.invitations = invitations;
        this.besoins = besoins;
        this.messages = messages;
    }

    /** Assemble tous les DAO pour le serveur et les tests. */
    public static ServiceEvenements creerParDefaut() {
        PersonneDao p = new PersonneDao();
        InvitationDao i = new InvitationDao();
        BesoinDao b = new BesoinDao();
        MessageDao m = new MessageDao(p);
        return new ServiceEvenements(p, new EvenementDao(p, i, b, m), i, b, m);
    }

    public synchronized Evenement trouverEvenement(int idEvenement) {
        return evenements.trouver(idEvenement)
                .orElseThrow(() -> new IllegalArgumentException("Événement inconnu : " + idEvenement));
    }

    public synchronized List<Evenement> evenementsOrganises(Personne p) {
        return evenements.trouverOrganisesPar(p.getId());
    }

    public synchronized List<Evenement> evenementsOuInvite(Personne p) {
        return evenements.trouverOuInvite(p.getId());
    }

    public synchronized List<Message> messagesDe(Personne p) {
        return messages.trouverPourPersonne(p.getId());
    }

    private Personne personneParEmail(String email) {
        return personnes.trouverParEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Aucune personne avec l'e-mail " + email));
    }

    private Personne personneParId(int id) {
        return personnes.trouver(id)
                .orElseThrow(() -> new IllegalArgumentException("Personne inconnue : " + id));
    }

    private static Besoin besoin(Evenement e, int idBesoin) {
        return e.getBesoin(idBesoin)
                .orElseThrow(() -> new IllegalArgumentException("Besoin inconnu : " + idBesoin));
    }

    public synchronized Evenement creerEvenement(Personne createur, String nom, String lieu, double prix,
                                                 String description, LocalDateTime date, TypeEvenement type) {
        Evenement e = new Evenement(0, nom, lieu, prix, description, date, type, createur);
        if (type == TypeEvenement.MARIAGE) {
            e.ajouterBesoin(new Consommable(0, "Budget traiteur", BUDGET_TRAITEUR_DEFAUT, 1, true));
        }
        Transaction.executerSansRetour(c -> {
            evenements.creer(c, e);
            for (Besoin b : e.getBesoins()) {
                besoins.creer(c, e.getId(), b);
            }
        });
        return e;
    }

    public synchronized Evenement modifierEvenement(Personne appelant, int idEvenement, String nom, String lieu,
                                                    double prix, String description, LocalDateTime date,
                                                    TypeEvenement type) {
        Evenement e = trouverEvenement(idEvenement);
        e.modifier(appelant, nom, lieu, prix, description, date, type);
        Transaction.executerSansRetour(c -> evenements.mettreAJour(c, e));
        return e;
    }

    public synchronized void supprimerEvenement(Personne appelant, int idEvenement) {
        Evenement e = trouverEvenement(idEvenement);
        if (!e.estOrganisateur(appelant)) {
            throw new AccesRefuseException("seul un organisateur peut supprimer l'événement");
        }
        Transaction.executerSansRetour(c -> evenements.supprimer(c, idEvenement));
    }

    public synchronized void ajouterOrganisateur(Personne appelant, int idEvenement, String email) {
        Evenement e = trouverEvenement(idEvenement);
        Personne p = personneParEmail(email);
        e.ajouterOrganisateur(appelant, p);
        Transaction.executerSansRetour(c -> evenements.ajouterOrganisateur(c, idEvenement, p.getId()));
    }

    public synchronized Invitation inviter(Personne appelant, int idEvenement, String email) {
        Evenement e = trouverEvenement(idEvenement);
        Personne p = personneParEmail(email);
        Invitation i = e.inviter(appelant, p);
        Transaction.executerSansRetour(c -> invitations.creer(c, idEvenement, i));
        return i;
    }

    public synchronized void repondreInvitation(Personne invite, int idEvenement, boolean accepte) {
        Evenement e = trouverEvenement(idEvenement);
        List<Besoin> liberes = e.repondreInvitation(invite, accepte);
        Invitation i = e.getInvitationDe(invite).orElseThrow();
        Transaction.executerSansRetour(c -> {
            invitations.mettreAJourStatut(c, i);
            for (Besoin b : liberes) {
                besoins.mettreAJourAcheteur(c, b);
            }
        });
    }

    public synchronized Besoin ajouterBesoin(Personne appelant, int idEvenement, Besoin b) {
        Evenement e = trouverEvenement(idEvenement);
        e.ajouterBesoin(appelant, b);
        Transaction.executerSansRetour(c -> besoins.creer(c, idEvenement, b));
        return b;
    }

    public synchronized void supprimerBesoin(Personne appelant, int idEvenement, int idBesoin) {
        Evenement e = trouverEvenement(idEvenement);
        e.supprimerBesoin(appelant, besoin(e, idBesoin));
        Transaction.executerSansRetour(c -> besoins.supprimer(c, idBesoin));
    }

    public synchronized void attribuerBesoin(Personne appelant, int idEvenement, int idBesoin, int idAcheteur) {
        Evenement e = trouverEvenement(idEvenement);
        Besoin b = besoin(e, idBesoin);
        e.attribuerBesoin(appelant, b, personneParId(idAcheteur));
        Transaction.executerSansRetour(c -> besoins.mettreAJourAcheteur(c, b));
    }

    public synchronized void prendreEnCharge(Personne appelant, int idEvenement, int idBesoin) {
        attribuerBesoin(appelant, idEvenement, idBesoin, appelant.getId());
    }

    public synchronized Message envoyerMessage(Personne envoyeur, int idEvenement, int idReceveur, String contenu) {
        Evenement e = trouverEvenement(idEvenement);
        Message m = new Message(0, envoyeur, personneParId(idReceveur), contenu, LocalDateTime.now());
        e.envoyerMessage(m);
        Transaction.executerSansRetour(c -> messages.creer(c, idEvenement, m));
        return m;
    }
}
