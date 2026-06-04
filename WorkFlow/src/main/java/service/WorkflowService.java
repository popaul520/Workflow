package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import dao.DBConnection;
import dao.WorkflowDAO;
import model.Workflow;
import model.WorkflowDisplay;

public class WorkflowService {

    private final WorkflowDAO wfDao = new WorkflowDAO();

    /**
     * Récupère les workflows du tableau de bord classique (filtre statut + barre de recherche)
     */
    public List<WorkflowDisplay> getDashboardWorkflows(String status, String query) {
        List<WorkflowDisplay> displays = wfDao.getWorkflowsWithDetailsByStatus(status, query);
        decorerBadges(displays);
        return displays;
    }
    
    /**
     * Récupère et décore les workflows filtrés par un template spécifique (provenant du Hub)
     */
    public List<WorkflowDisplay> getWorkflowsByTemplateDecorated(String nomTemplate) {
        List<WorkflowDisplay> displays = getWorkflowsWithDetailsByTemplate(nomTemplate);
        decorerBadges(displays);
        return displays;
    }
    
    /**
     * Extrait la logique de coloration des badges commune aux deux affichages
     */
    private void decorerBadges(List<WorkflowDisplay> displays) {
        if (displays == null) return;
        
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
    }
    
    /**
     * Requête SQL d'extraction par Template (Idéalement à placer dans WorkflowDAO)
     */
    public List<WorkflowDisplay> getWorkflowsWithDetailsByTemplate(String nomTemplate) {
        List<WorkflowDisplay> list = new ArrayList<>();
        String sql = "SELECT w.id, w.titre, w.date_creation, w.date_finalisation, w.id_template_workflow, " +
                     "       u.nom AS nom_demandeur, " +
                     "       COALESCE((SELECT MAX(te.place) FROM template_etape te WHERE te.id_template_workflow = w.id_template_workflow), 1) as etape_actuelle " +
                     "FROM workflow w " +
                     "LEFT JOIN utilisateur u ON w.id_utilisateur = u.id " +
                     "WHERE w.id_template_workflow IN (SELECT id FROM template_workflow WHERE nom = ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            ps.setString(1, nomTemplate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Workflow w = new Workflow();
                    w.setId(rs.getInt("id"));
                    w.setTitre(rs.getString("titre"));
                    w.setDateCreation(rs.getTimestamp("date_creation"));
                    w.setDateFinalisation(rs.getTimestamp("date_finalisation"));
                    w.setIdTemplateWorkflow(rs.getInt("id_template_workflow"));

                    WorkflowDisplay wd = new WorkflowDisplay();
                    wd.setWorkflow(w);
                    wd.setNomDemandeur(rs.getString("nom_demandeur") != null ? rs.getString("nom_demandeur") : "Inconnu");
                    wd.setEtapeActuelle(rs.getInt("etape_actuelle"));
                    wd.setRawAvis(""); 
                    
                    list.add(wd);
                }
            }
        } catch (Exception e) {
            System.err.println("ERREUR CRITIQUE SQL DANS SERVICE/DAO: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
}