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
import dao.RoleDAO; 
import dao.UtilisateurDAO; // Assure-toi d'importer ton DAO Utilisateur pour récupérer les noms
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
        RoleDAO roleDao = new RoleDAO(); 
        UtilisateurDAO uDao = new UtilisateurDAO(); // Instancié pour récupérer l'identité du demandeur

        String status = request.getParameter("status");
        String query = request.getParameter("q");
        
        if (status == null || status.isEmpty()) {
            status = "en_cours"; 
        }

        try {
            // --- 1. RÉCUPÉRATION DES WORKFLOWS BRUTS (FILTRÉS) ---
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

            // --- 2. TRAITEMENT DES WORKFLOWS ET CALCUL DES BADGES DYNAMIQUE ---
            List<WorkflowDisplay> workflowsFiltered = new ArrayList<>();
            
            for (Workflow wf : allWorkflows) {
                boolean isClosed = (wf.getDateFinalisation() != null);
                
                // Calcul de l'étape actuelle du dossier
                int etape = 0;
                try { 
                    etape = vDao.getDerniereEtapeValidee(wf.getId()); 
                } catch(Exception e) { 
                    etape = 1; 
                }

                // Récupération dynamique du texte de décision lié à l'étape en cours
                String avis = "";
                if (etape == 10) {
                    avis = dDao.getValeurAttribut(wf.getId(), 10, "Avis D.C.D.");
                } else if (etape == 7) {
                    avis = dDao.getValeurAttribut(wf.getId(), 7, "Avis D.O.P.");
                } else {
                    // Lecture générique séquentielle des attributs textuels de décision
                    avis = dDao.getValeurAttribut(wf.getId(), etape, "faisable");
                    if (avis == null || avis.isEmpty()) {
                        avis = dDao.getValeurAttribut(wf.getId(), etape, "avis production");
                    }
                    if (avis == null || avis.isEmpty()) {
                        avis = dDao.getValeurAttribut(wf.getId(), etape, "Avis logistique");
                    }
                    if (avis == null || avis.isEmpty()) {
                        avis = dDao.getValeurAttribut(wf.getId(), etape, "Avis Q.H.E.");
                    }
                }

                String cleanAvis = (avis != null) ? avis.trim() : "";
                boolean isRefuse = "Non faisable".equalsIgnoreCase(cleanAvis) || "Défavorable".equalsIgnoreCase(cleanAvis);

                // Valeurs par défaut du Badge (Bleu / En cours)
                String badgeBg = "#ebf8ff"; 
                String badgeText = "#2c5282";
                String libelleEtape = "Étape " + etape + "/9";

                // Calcul dynamique des styles de badges basés sur l'avis
                if ("Faisable".equalsIgnoreCase(cleanAvis)) {
                    badgeBg = "#c6f6d5"; // Vert
                    badgeText = "#22543d";
                    libelleEtape = (etape == 10) ? "Terminé" : "Faisable";
                } 
                else if ("Faisable sous condition".equalsIgnoreCase(cleanAvis) || "Faisable s.c.".equalsIgnoreCase(cleanAvis)) {
                    badgeBg = "#feebc8"; // Orange
                    badgeText = "#744210";
                    libelleEtape = "Faisable s.c.";
                } 
                else if (isRefuse) {
                    badgeBg = "#fed7d7"; // Rouge
                    badgeText = "#822727";
                    libelleEtape = (etape == 10) ? "Refusé" : "Non Faisable";
                }
                // Si l'étape n'a pas encore reçu de réponse
                else if (cleanAvis.isEmpty()) {
                    if (etape >= 7) {
                        badgeBg = "#e2e8f0"; // Gris
                        badgeText = "#4a5568";
                        libelleEtape = "Décision...";
                    } else {
                        badgeBg = "#edf2f7"; // Gris clair
                        badgeText = "#718096";
                        libelleEtape = "Étape " + etape + " en cours";
                    }
                }

                // Filtrage d'affichage selon le scope de l'onglet actif
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
                    // Résolution propre du nom du demandeur
                    String nomDemandeur = "Inconnu";
                    try {
                        Utilisateur demandeur = uDao.findByid( Integer.parseInt(wf.getIdUtilisateur()));
                        if (demandeur != null) {
                            nomDemandeur = demandeur.getNom(); // Ajuste selon tes getters
                        }
                    } catch (Exception e) {
                        nomDemandeur = "ID User : " + wf.getIdUtilisateur();
                    }

                    workflowsFiltered.add(new WorkflowDisplay(wf, badgeBg, badgeText, libelleEtape, nomDemandeur));
                }
            }

            // --- 3. RÉCUPÉRATION DES TÂCHES EN ATTENTE D'ACTION ---
            List<Workflow> pendingList = WorkflowDAO.getWorkflowsEnAttenteParRole(user.getRole());
           
            // --- 4. EXPORTATION DES ATRIBUTS & ROUTAGE ---
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

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    // Wrapper POJO d'affichage pour nettoyer la JSP
    public static class WorkflowDisplay {
        private final Workflow workflow;
        private final String badgeBg;
        private final String badgeText;
        private final String libelleEtape;
        private final String nomDemandeur;

        public WorkflowDisplay(Workflow workflow, String badgeBg, String badgeText, String libelleEtape, String nomDemandeur) {
            this.workflow = workflow;
            this.badgeBg = badgeBg;
            this.badgeText = badgeText;
            this.libelleEtape = libelleEtape;
            this.nomDemandeur = nomDemandeur;
        }

        public Workflow getWorkflow() { return workflow; }
        public String getBadgeBg() { return badgeBg; }
        public String getBadgeText() { return badgeText; }
        public String getLibelleEtape() { return libelleEtape; }
        public String getNomDemandeur() { return nomDemandeur; }
    }
}