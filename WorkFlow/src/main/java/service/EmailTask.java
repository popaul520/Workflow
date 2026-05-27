package service;

import java.util.List;
import java.util.Map;
import dao.UtilisateurDAO;
import dao.WorkflowDAO;
import model.Workflow;
import model.Utilisateur;

public class EmailTask implements Runnable {

    @Override
    public void run() {
        System.out.println("=== Début de la Task Email Dynamique ===");

        try {
            // On récupère tous les utilisateurs groupés par rôle
            Map<Integer, List<Utilisateur>> usersParRole = UtilisateurDAO.getAllUsersGroupedByRole();

            // Séquence : On traite chaque rôle de 1 à 12, l'un après l'autre
            for (int roleId = 1; roleId <= 12; roleId++) {
                int etapeConcernee = roleId; // Rôle = Étape

                List<Utilisateur> destinataires = usersParRole.get(roleId);
                // Si personne n'a ce rôle, inutile de faire les requêtes SQL
                if (destinataires == null || destinataires.isEmpty()) {
                    continue; 
                }

                // ------------------------------------------------------------------
                // PARTICULE 1 : TRAITEMENT DES DOSSIERS EN ATTENTE DE VALIDATION
                // ------------------------------------------------------------------
                List<Workflow> aValider = WorkflowDAO.getWorkflowsEnAttentePourEtape(etapeConcernee);

                if (!aValider.isEmpty()) {
                    String sujet = "Dossiers de processus en attente de validation (Étape " + etapeConcernee + ")";
                    StringBuilder corps = new StringBuilder();
                    corps.append("Bonjour,\n\nLes workflows suivants attendent votre validation pour l'étape " + etapeConcernee + " :\n\n");
                    for (Workflow w : aValider) {
                        corps.append("- Workflow N°").append(w.getId()).append(" : ").append(w.getTitre()).append("\n");
                    }
                    corps.append("\nMerci de vous connecter à l'application pour les traiter.\nCordialement.");

                    // Envoi individuel à chaque membre du rôle
                    for (Utilisateur u : destinataires) {
                        String msgPerso = corps.toString().replace("Bonjour,", "Bonjour " + u.getPrenom() + " " + u.getNom() + ",");
                        MailSender.send(u.getMail(), sujet, msgPerso);
                    }
                    System.out.println("[Rôle " + roleId + "] " + aValider.size() + " alertes de validation envoyées.");
                }

                // ------------------------------------------------------------------
                // PARTICULE 2 : TRAITEMENT DES DOSSIERS TERMINÉS / CRÉATION ANNONCE
                // ------------------------------------------------------------------
                List<Workflow> termines = WorkflowDAO.getWorkflowsTerminesPourEtape(etapeConcernee);

                if (!termines.isEmpty()) {
                    String sujet = "Notification : Fin de l'étape " + etapeConcernee;
                    StringBuilder corps = new StringBuilder();
                    corps.append("Bonjour,\n\nNous vous informons que l'étape " + etapeConcernee + " est désormais terminée pour :\n\n");
                    for (Workflow w : termines) {
                        corps.append("- Workflow N°").append(w.getId()).append(" : ").append(w.getTitre()).append("\n");
                    }
                    corps.append("\nCe message est purement informatif.\nCordialement.");

                    // Envoi individuel à chaque membre du rôle
                    for (Utilisateur u : destinataires) {
                        String msgPerso = corps.toString().replace("Bonjour,", "Bonjour " + u.getPrenom() + " " + u.getNom() + ",");
                        MailSender.send(u.getMail(), sujet, msgPerso);
                    }
                                        for (Workflow w : termines) {
                        WorkflowDAO.marquerAnnonceTerminee(w.getId()); // Appel de la méthode corrigée
                    }
                    System.out.println("[Rôle " + roleId + "] " + termines.size() + " annonces de finalisation envoyées et verrouillées en BDD.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("=== Fin de la Task Email Dynamique ===");
    }
}