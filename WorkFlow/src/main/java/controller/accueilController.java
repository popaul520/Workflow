package controller;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.RoleDAO;
import dao.ValidationDAO;
import dao.WorkflowDAO;
import model.Workflow;
import model.WorkflowDisplay;
import model.Utilisateur;

@WebServlet("/home")
public class accueilController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final WorkflowDAO wfDao = new WorkflowDAO();
    private final RoleDAO roleDao = new RoleDAO(); 

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Utilisateur user = (Utilisateur) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String status = request.getParameter("status");
        String query = request.getParameter("q");
        
        if (status == null || status.isEmpty()) {
            status = "en_cours"; 
        }

        try {
            // 1. Récupération des dossiers nécessitant une action (1 seule requête)
            List<Workflow> pendingList = WorkflowDAO.getWorkflowsEnAttenteParRole(user.getRole());

            // 2. Récupération des dossiers de l'onglet actif (1 seule requête agrégée)
            List<WorkflowDisplay> workflowsFiltered;
            if (query != null && !query.trim().isEmpty()) {
                workflowsFiltered = wfDao.searchWorkflowsWithDetails(query.trim());
            } else {
                workflowsFiltered = wfDao.getWorkflowsWithDetailsByStatus(status);
            }

            // 3. Application des styles visuels (En mémoire vive, 0ms)
            for (WorkflowDisplay wd : workflowsFiltered) {
                computeBadgeStyle(wd);
            }
           
            // 4. Passage à la vue JSP
            request.setAttribute("workflows", workflowsFiltered); 
            request.setAttribute("pendingList", pendingList);    
            request.setAttribute("currentStatus", status);       
            request.setAttribute("roleDAO", roleDao); 
            
            request.getRequestDispatcher("/View/accueil.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void computeBadgeStyle(WorkflowDisplay wd) {
        String cleanAvis = (wd.getRawAvis() != null) ? wd.getRawAvis().trim() : "";
        int etapeActuelle = wd.getEtapeActuelle();
        
        // CORRECTION : On utilise l'instance injectée validationDao au lieu de la classe
        // Et on récupère directement l'ID du workflow depuis wd
        int totalEtapesValidees = ValidationDAO.getTotalEtapesValidees(wd.getWorkflow().getId()); 
        
        boolean isRefuse = "Non faisable".equalsIgnoreCase(cleanAvis) || "Défavorable".equalsIgnoreCase(cleanAvis);

        String badgeBg = "#edf2f7"; 
        String badgeText = "#718096";
        String libelleEtape = "";

        // 1. SI LE NOMBRE TOTAL DE LIGNES VALIDÉES EST INFÉRIEUR OU ÉGAL À 7
        if (totalEtapesValidees <= 7) {
            badgeBg = "#ebf8ff"; 
            badgeText = "#2c5282";
            
            // Calcul : nb_etape_validation - 1
            int etapesMoinsUn = (totalEtapesValidees > 0) ? (totalEtapesValidees - 1) : 0;
            
            // Affichage demandé : nb_etape-1 / 9
            libelleEtape = etapesMoinsUn + " / 9" + " étape";
        } 
        // 2. SI LE TOTAL EST SUPÉRIEUR À 7 : ON APPLIQUE LA LOGIQUE FINALE
        else {
            if ("Favorable".equalsIgnoreCase(cleanAvis)) {
                badgeBg = "#c6f6d5"; 
                badgeText = "#22543d";
                libelleEtape = "Favorable";
            }
            else if ("Faisable sous condition".equalsIgnoreCase(cleanAvis) || "Faisable s.c.".equalsIgnoreCase(cleanAvis)) {
                badgeBg = "#feebc8"; 
                badgeText = "#744210";
                libelleEtape = "Faisable sous condition";
            } 
            else if (isRefuse) {
                badgeBg = "#fed7d7"; 
                badgeText = "#822727";
                libelleEtape = "Non Faisable";
            }
            // Si aucun avis final n'est encore enregistré (Avis vide)
            else if (cleanAvis.isEmpty()) {
                if (etapeActuelle >= 7 && etapeActuelle < 10) {
                    badgeBg = "#e2e8f0"; 
                    badgeText = "#4a5568"; 
                    libelleEtape = "Décision...";
                } else {
                    libelleEtape = "Étape " + etapeActuelle;
                }
            }
        }

        // Assignation des styles calculés à l'objet
        wd.setBadgeBg(badgeBg);
        wd.setBadgeText(badgeText);
        wd.setLibelleEtape(libelleEtape);
    }

    protected void javaPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}