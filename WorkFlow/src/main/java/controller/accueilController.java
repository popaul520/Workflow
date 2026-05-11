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
import dao.RoleDAO; // Importation nécessaire
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
        RoleDAO rDao = new RoleDAO();

        String status = request.getParameter("status");
        String query = request.getParameter("q");
        
        if (status == null || status.isEmpty()) {
            status = "en_cours"; 
        }

        try {
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

            for (Workflow wf : allWorkflows) {
                List<Integer> etapesValidees = vDao.getListeEtapesValidees(wf.getId());
                int lastStep = vDao.getDerniereEtapeValidee(wf.getId());
                boolean isClosed = (wf.getDateFinalisation() != null);

                // --- LOGIQUE A : ACTIONS EN ATTENTE ---
                if (!isClosed) {
                    
                    // 1. BLOC PARALLÈLE (Étapes 2 à 6)
                    // On ne peut commencer que si l'étape 1 est faite
                    if (etapesValidees.contains(1)) {
                        boolean bloc2to6Complet = true;
                        for (int i = 2; i <= 6; i++) {
                            if (!etapesValidees.contains(i)) {
                                bloc2to6Complet = false;
                                // Si l'utilisateur a le droit sur cette étape non faite, on l'ajoute
                                if (rDao.canAccessEtape(user.getRole(), i)) {
                                    if (!pendingList.contains(wf)) pendingList.add(wf);
                                }
                            }
                        }

                        // 2. BLOC EN SÉRIE (Étapes 7 à 10)
                        // Le verrou : l'étape 7 ne s'ouvre que si le bloc 1-6 est TOTALEMENT fini
                        if (bloc2to6Complet) {
                            int nextStep = (lastStep < 7) ? 7 : lastStep + 1;
                            
                            if (nextStep <= 10 && rDao.canAccessEtape(user.getRole(), nextStep)) {
                                if (!pendingList.contains(wf)) pendingList.add(wf);
                            }
                        }
                    }
                }

                // --- LOGIQUE B : FILTRAGE TABLEAU PRINCIPAL ---
                if ("tous".equals(status) || ("en_cours".equals(status) && !isClosed)) {
                    workflowsFiltered.add(wf);
                } 
                else if (isClosed) {
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