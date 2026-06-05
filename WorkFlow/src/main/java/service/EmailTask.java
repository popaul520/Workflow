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
        System.out.println("=== Début de la Task Email Dynamique Unifiée ===");

        try {
            // 1. Récupération globale des workflows terminés à annoncer (Recherche unique)
            List<Workflow> terminesGlobaux = WorkflowDAO.getWorkflowsTerminesPourRole(0); 
            boolean aDesTermines = (terminesGlobaux != null && !terminesGlobaux.isEmpty());

            // 2. Récupération de tous les utilisateurs groupés par rôle
            Map<Integer, List<Utilisateur>> usersParRole = UtilisateurDAO.getAllUsersGroupedByRole();

            // 3. Traitement par rôle
            for (int roleId : usersParRole.keySet()) {
                
                // EXCLUSION : Si c'est le rôle 0, on n'envoie rien du tout
                if (roleId == 0) {
                    System.out.println("Saut du rôle 0 (Exclu des envois)");
                    continue;
                }

                List<Utilisateur> destinataires = usersParRole.get(roleId);
                if (destinataires == null || destinataires.isEmpty()) {
                    continue; 
                }

                // 4. Récupération des dossiers spécifiques à valider pour CE rôle
                List<Workflow> aValider = WorkflowDAO.getWorkflowsEnAttenteParRole(roleId);

                // Si rien à valider ET aucun workflow terminé globalement, on n'envoie pas de mail vide
                if (aValider.isEmpty() && !aDesTermines) {
                    continue;
                }

                // 5. Construction du corps de l'email unique
                String sujet = "Point sur vos dossiers de processus et workflows";
                StringBuilder templateCorps = new StringBuilder();
                templateCorps.append("Bonjour %USER%,\n\nVoici le point sur les dossiers de l'application :\n\n");

                // SECTION 1 : À valider (Propre à ce rôle)
                if (!aValider.isEmpty()) {
                    templateCorps.append(" À VALIDER (Action requise de votre part) :\n");
                    for (Workflow w : aValider) {
                        templateCorps.append("• N°").append(w.getId()).append(" : ").append(w.getTitre()).append("\n");
                    }
                    templateCorps.append("\n Merci de vous connecter à l'application pour les traiter.\n\n");
                }

                // SECTION 2 : Terminés (Visibles par tout le monde sauf rôle 0)
                if (aDesTermines) {
                    templateCorps.append(" DOSSIERS FINALISÉS (Pour information générale) :\n");
                    for (Workflow w : terminesGlobaux) {
                        templateCorps.append("• N°").append(w.getId()).append(" : ").append(w.getTitre()).append("\n");
                    }
                    templateCorps.append("\n");
                }

                templateCorps.append("Cordialement,\nL'équipe Support informatique.");
                String corpsDeBase = templateCorps.toString();

                // 6. Envoi personnalisé aux membres de ce rôle
                for (Utilisateur u : destinataires) {
                    if (u.getMail() != null && !u.getMail().isEmpty()) {
                        String msgPerso = corpsDeBase.replace("%USER%", u.getNom());
                        try {
                            MailSender.send(u.getMail(), sujet, msgPerso);
                        } catch (Exception e) {
                            System.err.println("❌ Échec d'envoi à " + u.getMail() + " : " + e.getMessage());
                        }
                    }
                }
                System.out.println("[Rôle " + roleId + "] Récapitulatif envoyé.");
            }

            // 7. VERROUILLAGE : Une fois que tous les rôles admissibles ont été bouclés,
            // on marque les workflows terminés comme annoncés pour ne plus les ré-envoyer demain.
            if (aDesTermines) {
                for (Workflow w : terminesGlobaux) {
                    WorkflowDAO.marquerAnnonceTerminee(w.getId()); 
                }
                System.out.println("🔒 " + terminesGlobaux.size() + " annonces de fin définitivement verrouillées en BDD.");
            }

        } catch (Exception e) {
            System.err.println("Erreur critique dans EmailTask :");
            e.printStackTrace();
        }
        System.out.println("=== Fin de la Task Email Dynamique Unifiée ===");
    }
}