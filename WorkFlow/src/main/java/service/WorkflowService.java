package service;

import java.util.List;
import dao.WorkflowDAO;
import model.WorkflowDisplay;

public class WorkflowService {

    private final WorkflowDAO wfDao = new WorkflowDAO();

    public List<WorkflowDisplay> getDashboardWorkflows(String status, String query) {
        // 1. Récupération de la base de données optimisée
        List<WorkflowDisplay> displays = wfDao.getWorkflowsWithDetailsByStatus(status, query);
        
        // 2. Détermination visuelle des badges
        for (WorkflowDisplay wd : displays) {
            int etape = wd.getEtapeActuelle();
            String cleanAvis = (wd.getRawAvis() != null) ? wd.getRawAvis().trim() : "";
            boolean isRefuse = "Non faisable".equalsIgnoreCase(cleanAvis) || "Défavorable".equalsIgnoreCase(cleanAvis);
            
            String badgeBg = "#ebf8ff"; 
            String badgeText = "#2c5282";
            String libelleEtape = "Étape " + etape + "/10"; 

            if (wd.getWorkflow().getDateFinalisation() != null) {
                if (isRefuse) {
                    badgeBg = "#fed7d7"; badgeText = "#822727"; libelleEtape = "Refusé";
                } else {
                    badgeBg = "#c6f6d5"; badgeText = "#22543d"; libelleEtape = "Terminé";
                }
            } else {
                if ("Faisable".equalsIgnoreCase(cleanAvis)) {
                    badgeBg = "#c6f6d5"; badgeText = "#22543d"; libelleEtape = "Faisable (Étape " + etape + ")";
                } else if ("Faisable sous condition".equalsIgnoreCase(cleanAvis) || "Faisable s.c.".equalsIgnoreCase(cleanAvis)) {
                    badgeBg = "#feebc8"; badgeText = "#744210"; libelleEtape = "Faisable s.c.";
                } else if (cleanAvis.isEmpty()) {
                    if (etape >= 7) {
                        badgeBg = "#e2e8f0"; badgeText = "#4a5568"; libelleEtape = "Décision...";
                    } else {
                        badgeBg = "#edf2f7"; badgeText = "#718096"; libelleEtape = "Étape " + etape + " en cours";
                    }
                }
            }

            wd.setBadgeBg(badgeBg);
            wd.setBadgeText(badgeText);
            wd.setLibelleEtape(libelleEtape);
        }
        
        return displays;
    }    
}