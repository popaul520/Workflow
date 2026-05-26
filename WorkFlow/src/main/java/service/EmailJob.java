package service;

import org.quartz.Job;
import org.quartz.JobExecutionContext;

import dao.DemandeDAO;
import dao.UtilisateurDAO;
import model.Demande;
import model.Utilisateur;

import java.util.List;
import java.util.Map;

public class EmailJob implements Job {

    @Override
    public void execute(JobExecutionContext context) {

        try {
            Map<Integer, List<Demande>> demandesParRole =
                DemandeDAO.getDemandesParRole();

            for (Integer role : demandesParRole.keySet()) {

                List<Utilisateur> users =
                    UtilisateurDAO.getUsersByRole(role);

                List<Demande> demandes =
                    demandesParRole.get(role);

                for (Utilisateur u : users) {
                    EmailService.envoyerEmail(u, demandes, role);
                }

                for (Demande d : demandes) {
                    DemandeDAO.marquerEnvoye(d.getId());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
