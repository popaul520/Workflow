package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import dao.DonneeDAO;
import dao.ValidationDAO;
import dao.WorkflowDAO;
import dao.RoleDAO; // Ajout du DAO pour la gestion des rôles si nécessaire
import model.Workflow;
import model.Utilisateur;

@WebServlet("/home")
public class accueilController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Utilisateur user = (Utilisateur) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        WorkflowDAO wfDao = new WorkflowDAO();
        DonneeDAO dDao = new DonneeDAO();
        ValidationDAO vDao = new ValidationDAO();
        RoleDAO roleDao = new RoleDAO(); // Instancié pour le passer à la JSP

        String status = request.getParameter("status");
        String query = request.getParameter("q");
        
        if (status == null || status.isEmpty()) {
            status = "en_cours"; 
        }

        try {
            // --- 1. RÉCUPÉRATION DES WORKFLOWS BRUTS ---
            List<Workflow> allWorkflows;
            if (query != null && !query.trim().isEmpty()) {
                allWorkflows = wfDao.searchWorkflows(query.trim());
            } else if ("en_cours".equals(status)) {
                allWorkflows = wfDao.getWorkflowsEnCours();
            } else if ("termine".equals(status) || "annule".equals(status)) {
                allWorkflows = wfDao.getWorkflowsFinalises();
            } else {
                allWorkflows = wfDao.getAll();
            }

            // --- 2. TRAITEMENT DES WORKFLOWS ET CALCUL DES BADGES (Côté Serveur) ---
            List<WorkflowDisplay> workflowsFiltered = new ArrayList<>();
            
            for (Workflow wf : allWorkflows) {
                boolean isClosed = (wf.getDateFinalisation() != null);
                
                // Calcul de l'étape actuelle
                int etape = 0;
                try { 
                    etape = vDao.getDerniereEtapeValidee(wf.getId()); 
                } catch(Exception e) { 
                    etape = 1; 
                }

                // Détermination des styles de badges et libellés
                String badgeBg = "#ebf8ff"; // Bleu par défaut
                String badgeText = "#2c5282";
                String libelleEtape = "Étape " + etape + "/9";
                boolean isRefuse = false;

                if (etape == 10 || (isClosed && etape == 7)) {
                    int etapeDecision = (etape == 10) ? 10 : 7; 
                    String avis = dDao.getValeurAttribut(wf.getId(), etapeDecision, "Avis D.O.P."); 
                    if (avis == null || avis.trim().isEmpty()) {
                        avis = dDao.getValeurAttribut(wf.getId(), 10, "Avis D.C.D."); 
                    }

                    String cleanAvis = (avis != null) ? avis.trim() : "";
                    isRefuse = "Non faisable".equalsIgnoreCase(cleanAvis);

                    if ("faisable".equalsIgnoreCase(cleanAvis)) {
                        badgeBg = "#c6f6d5"; // Vert
                        badgeText = "#22543d";
                        libelleEtape = (etape == 10) ? "Terminé" : "Faisable";
                    } else if ("Faisable sous condition".equalsIgnoreCase(cleanAvis)) {
                        badgeBg = "#feebc8"; // Orange
                        badgeText = "#744210";
                        libelleEtape = "Faisable s.c.";
                    } else if (isRefuse) {
                        badgeBg = "#fed7d7"; // Rouge
                        badgeText = "#822727";
                        libelleEtape = (etape == 10) ? "Refusé" : "Non Faisable";
                    }
                } else if (etape >= 7) {
                    badgeBg = "#e2e8f0"; // Gris
                    badgeText = "#4a5568";
                    libelleEtape = "Décision...";
                }

                // Filtrage selon le statut de l'onglet demandé
                boolean afficher = false;
                if ("tous".equals(status)) {
                    afficher = true;
                } else if ("en_cours".equals(status) && !isClosed) {
                    afficher = true;
                } else if (isClosed) {
                    if ("termine".equals(status) && !isRefuse) {
                        afficher = true;
                    } else if ("annule".equals(status) && isRefuse) {
                        afficher = true;
                    }
                }

                if (afficher) {
                    // On encapsule le workflow avec ses données d'affichage précalculées
                    workflowsFiltered.add(new WorkflowDisplay(wf, badgeBg, badgeText, libelleEtape));
                }
            }

            // --- 3. RÉCUPÉRATION DES TÂCHES EN ATTENTE ---
            List<Workflow> pendingList = WorkflowDAO.getWorkflowsEnAttenteParRole(user.getRole());
           
            // --- 4. ENVOI DES DONNÉES PRÉ-CALCULÉES À LA JSP ---
            request.setAttribute("workflows", workflowsFiltered); 
            request.setAttribute("pendingList", pendingList);    
            request.setAttribute("currentStatus", status);       
            request.setAttribute("roleDAO", roleDao); // Permet d'utiliser roleDAO dans la JSP via JSTL
            
            request.getRequestDispatcher("/View/accueil.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    // Classe Wrapper interne pour stocker les calculs d'affichage et alléger la JSP
    public static class WorkflowDisplay {
        private Workflow workflow;
        private String badgeBg;
        private String badgeText;
        private String libelleEtape;

        public WorkflowDisplay(Workflow workflow, String badgeBg, String badgeText, String libelleEtape) {
            this.workflow = workflow;
            this.badgeBg = badgeBg;
            this.badgeText = badgeText;
            this.libelleEtape = libelleEtape;
        }

        public Workflow getWorkflow() { return workflow; }
        public String getBadgeBg() { return badgeBg; }
        public String getBadgeText() { return badgeText; }
        public String getLibelleEtape() { return libelleEtape; }
    }
}