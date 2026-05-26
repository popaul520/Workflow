package service;

import java.util.List;
import model.Demande;
import model.Utilisateur;

public class EmailService {

    public static void envoyerEmail(Utilisateur user, List<Demande> demandes, int role) throws Exception {

        String contenu = "Bonjour " + user.getPrenom() + " " + user.getNom() + ",\n\n";
        contenu += "Voici vos demandes du jour :\n\n";

        for (Demande d : demandes) {
            contenu += "- " + d.getDescription() + "\n";
        }

        if (role == 1) {
            contenu += "\nMerci de valider ces demandes.";
        } else if (role == 2) {
            contenu += "\nMerci de traiter ces demandes.";
        } else {
            contenu += "\nMerci de consulter ces demandes.";
        }

        // Appel du MailSender unifié
        MailSender.send(user.getMail(), "Demandes quotidiennes", contenu);
    }
}