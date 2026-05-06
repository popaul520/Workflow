package controller;

import java.io.IOException;
import java.sql.SQLException;
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
        ValidationDAO vDao = new ValidationDAO();
        DonneeDAO dDao = new DonneeDAO();

        String status = request.getParameter("status");
        String query = request.getParameter("q");
        
        if (status == null || status.isEmpty()) {
            status = "en_cours"; 
        }

        try {
            // 1. CHARGEMENT DE LA LISTE INITIALE (Sortie de la boucle !)
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

            List<Workflow> workflowsFiltered = new ArrayList<>();
            List<Workflow> pendingList = new ArrayList<>();

            // 2. TRAITEMENT UNIQUE DE LA LISTE
            for (Workflow wf : allWorkflows) {
                // On récupère les infos d'étape
                List<Integer> etapesValidees = vDao.getListeEtapesValidees(wf.getId());
                int lastStep = vDao.getDerniereEtapeValidee(wf.getId());
                boolean isClosed = (wf.getDateFinalisation() != null);

                // --- LOGIQUE A : REMPLISSAGE "EN ATTENTE DE VOTRE ACTION" ---
                // On vérifie les actions prioritaires (étapes 2 à 6 non faites)
                if (!isClosed) {
                    for (int i = 2; i <= 6; i++) {
                        if (!etapesValidees.contains(i) && user.canAccessStep(i)) {
                            pendingList.add(wf);
                            break; 
                        }
                    }
                }

                // --- LOGIQUE B : FILTRAGE POUR LE TABLEAU PRINCIPAL ---
                if ("tous".equals(status) || "en_cours".equals(status)) {
                    // Si on est en "en_cours", le DAO a déjà filtré les NULL, on ajoute direct
                    workflowsFiltered.add(wf);
                } 
                else if (isClosed) {
                    // Cas "termine" ou "annule" : on vérifie l'avis final
                    int etapeDecision = (lastStep >= 10) ? 10 : 7;
                    String avis = dDao.getValeurAttribut(wf.getId(), etapeDecision, "Avis D.O.P.");
                    if (avis == null || avis.trim().isEmpty()) {
                        avis = dDao.getValeurAttribut(wf.getId(), 10, "Avis D.C.D.");
                    }

                    boolean isRefuse = "Non faisable".equalsIgnoreCase(avis != null ? avis.trim() : "");

                    if ("termine".equals(status) && !isRefuse) {
                        workflowsFiltered.add(wf);
                    } else if ("annule".equals(status) && isRefuse) {
                        workflowsFiltered.add(wf);
                    }
                }
            }

            // 3. ENVOI DES ATTRIBUTS
            request.setAttribute("workflows", workflowsFiltered); 
            request.setAttribute("pendingList", pendingList);    
            request.setAttribute("currentStatus", status);       
            
            request.getRequestDispatcher("/View/accueil.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}