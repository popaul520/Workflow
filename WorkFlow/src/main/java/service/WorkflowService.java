package service;

import java.util.ArrayList;
import java.util.List;
import dao.DonneeDAO;
import dao.ValidationDAO;
import dao.WorkflowDAO;
import model.WorkflowDisplay;
import model.Workflow;

public class WorkflowService {

    private final WorkflowDAO wfDao = new WorkflowDAO();
    private final DonneeDAO dDao = new DonneeDAO();
    private final ValidationDAO vDao = new ValidationDAO();

    /**
     * Récupère et traite les workflows pour l'affichage avec filtres et badges calculés.
     */
    public List<WorkflowDisplay> getDashboardWorkflows(String status, String query) {
        String finalStatus = (status == null || status.isEmpty()) ? "en_cours" : status;
        List<Workflow> allWorkflows = fetchRawWorkflows(finalStatus, query);
        List<WorkflowDisplay> filteredList = new ArrayList<>();

        for (Workflow wf : allWorkflows) {
            boolean isClosed = (wf.getDateFinalisation() != null);
            int etape = 0;
            
            try {
                etape = vDao.getDerniereEtapeValidee(wf.getId());
            } catch (Exception e) {
                etape = 1; // Étape initiale par défaut si historique vide
            }

            // Détermination dynamique des styles de badges
            String badgeBg = "#ebf8ff"; // Bleu
            String badgeText = "#2c5282";
            String libelleEtape = "Étape " + etape; // Devient dynamique sans le "/9" en dur
            boolean isRefuse = false;

            // Analyse des avis sur les étapes clés de décision
            if (etape >= 7) { 
                String avis = dDao.getValeurAttribut(wf.getId(), etape, "Avis D.O.P.");
                if (avis == null || avis.trim().isEmpty()) {
                    avis = dDao.getValeurAttribut(wf.getId(), etape, "Avis D.C.D.");
                }

                String cleanAvis = (avis != null) ? avis.trim() : "";
                isRefuse = "Non faisable".equalsIgnoreCase(cleanAvis);

                if ("faisable".equalsIgnoreCase(cleanAvis)) {
                    badgeBg = "#c6f6d5"; // Vert
                    badgeText = "#22543d";
                    libelleEtape = isClosed ? "Terminé" : "Faisable";
                } else if ("Faisable sous condition".equalsIgnoreCase(cleanAvis)) {
                    badgeBg = "#feebc8"; // Orange
                    badgeText = "#744210";
                    libelleEtape = "Faisable s.c.";
                } else if (isRefuse) {
                    badgeBg = "#fed7d7"; // Rouge
                    badgeText = "#822727";
                    libelleEtape = isClosed ? "Refusé" : "Non Faisable";
                } else {
                    badgeBg = "#e2e8f0"; // Gris
                    badgeText = "#4a5568";
                    libelleEtape = "Décision...";
                }
            }

            // Application du filtre de l'onglet actif
            if (shouldDisplay(finalStatus, isClosed, isRefuse)) {
                filteredList.add(new WorkflowDisplay(wf, badgeBg, badgeText, libelleEtape));
            }
        }
        return filteredList;
    }

    /**
     * Centralise la récupération brute selon l'action utilisateur.
     */
    private List<Workflow> fetchRawWorkflows(String status, String query) {
        if (query != null && !query.trim().isEmpty()) {
            return wfDao.searchWorkflows(query.trim());
        }
        switch (status) {
            case "en_cours":  return wfDao.getWorkflowsEnCours();
            case "termine":
            case "annule":    return wfDao.getWorkflowsFinalises();
            default:          return wfDao.getAll();
        }
    }

    /**
     * Logique de filtrage des onglets.
     */
    private boolean shouldDisplay(String status, boolean isClosed, boolean isRefuse) {
        if ("tous".equals(status)) return true;
        if ("en_cours".equals(status) && !isClosed) return true;
        if (isClosed) {
            if ("termine".equals(status) && !isRefuse) return true;
            if ("annule".equals(status) && isRefuse) return true;
        }
        return false;
    }
}