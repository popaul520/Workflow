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
        System.out.println("=== Début de la Task Email Dynamique (Basée sur les Rôles) ===");

        try {
            // On récupère tous les utilisateurs groupés par rôle
            Map<Integer, List<Utilisateur>> usersParRole = UtilisateurDAO.getAllUsersGroupedByRole();

            // Séquence : On traite chaque rôle présent en base de données
            for (int roleId : usersParRole.keySet()) {
                
                List<Utilisateur> destinataires = usersParRole.get(roleId);
                
                // Si personne n'a ce rôle, inutile de bosser
                if (destinataires == null || destinataires.isEmpty()) {
                    continue; 
                }

                // ------------------------------------------------------------------
                // PARTICULE 1 : DOSSIERS EN ATTENTE DE VALIDATION POUR CE RÔLE
                // ------------------------------------------------------------------
                // On utilise ta méthode unifiée magique qui sait gérer le 2, 6, 11...
                List<Workflow> aValider = WorkflowDAO.getWorkflowsEnAttenteParRole(roleId);

                if (!aValider.isEmpty()) {
                    String sujet = "Dossiers de processus en attente de votre action";
                    StringBuilder corps = new StringBuilder();
                    corps.append("Bonjour,\n\nLes workflows suivants attendent une action de validation de votre part :\n\n");
                    
                    for (Workflow w : aValider) {
                        corps.append("- Workflow N°").append(w.getId()).append(" : ").append(w.getTitre()).append("\n");
                    }
                    corps.append("\nMerci de vous connecter à l'application pour les traiter.\nCordialement.");

                    // Envoi individuel et personnalisé à chaque membre ayant ce rôle
                    for (Utilisateur u : destinataires) {
                        if (u.getMail() != null && !u.getMail().isEmpty()) {
                            String msgPerso = corps.toString().replace("Bonjour,", "Bonjour " + u.getPrenom() + " " + u.getNom() + ",");
                            try {
                                MailSender.send(u.getMail(), sujet, msgPerso);
                            } catch (Exception e) {
                                System.err.println("❌ Échec d'envoi à " + u.getMail() + " : " + e.getMessage());
                            }
                        }
                    }
                    System.out.println("[Rôle " + roleId + "] " + aValider.size() + " alertes de validation envoyées.");
                }

                // ------------------------------------------------------------------
                // PARTICULE 2 : TRAITEMENT DES DOSSIERS TERMINÉS (NOTIFICATION DE FIN)
                // ------------------------------------------------------------------
                // Ici, on récupère les dossiers finalisés pour les étapes associées à ce rôle
                List<Workflow> termines = WorkflowDAO.getWorkflowsTerminesPourRole(roleId);
                if (termines != null && !termines.isEmpty()) {
                    String sujet = "Notification : Finalisation de dossiers";
                    StringBuilder corps = new StringBuilder();
                    corps.append("Bonjour,\n\nNous vous informons que les processus suivants associés à vos droits sont terminés :\n\n");
                    
                    for (Workflow w : termines) {
                        corps.append("- Workflow N°").append(w.getId()).append(" : ").append(w.getTitre()).append("\n");
                    }
                    corps.append("\nCe message est purement informatif.\nCordialement.");

                    // Envoi individuel à chaque membre du rôle
                    for (Utilisateur u : destinataires) {
                        if (u.getMail() != null && !u.getMail().isEmpty()) {
                            String msgPerso = corps.toString().replace("Bonjour,", "Bonjour " + u.getNom() + ",");
                            try {
                                MailSender.send(u.getMail(), sujet, msgPerso);
                            } catch (Exception e) {
                                System.err.println("❌ Échec d'envoi d'annonce à " + u.getMail());
                            }
                        }
                    }
                    
                    // Verrouillage pour ne pas renvoyer l'annonce le lendemain matin
                    for (Workflow w : termines) {
                        WorkflowDAO.marquerAnnonceTerminee(w.getId()); 
                    }
                    System.out.println("[Rôle " + roleId + "] " + termines.size() + " annonces de finalisation envoyées et verrouillées.");
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Erreur critique dans EmailTask :");
            e.printStackTrace();
        }
        System.out.println("===Fin de la Task Email Dynamique ===");
    }
}