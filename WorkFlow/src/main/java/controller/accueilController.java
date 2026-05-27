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

        String status = request.getParameter("status");
        String query = request.getParameter("q");
        
        if (status == null || status.isEmpty()) {
            status = "en_cours"; 
        }

        try {
            // --- 1. FILTRAGE DU TABLEAU PRINCIPAL 
        	
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
            for (Workflow wf : allWorkflows) {
                boolean isClosed = (wf.getDateFinalisation() != null);

                if ("tous".equals(status) || ("en_cours".equals(status) && !isClosed)) {
                    workflowsFiltered.add(wf);
                } 
                else if (isClosed) {
                    int lastStep = vDao.getDerniereEtapeValidee(wf.getId());
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

            // On appelle ta méthode statique en lui passant l'ID du rôle de l'utilisateur connecté
            List<Workflow> pendingList = WorkflowDAO.getWorkflowsEnAttenteParRole(user.getRole());
           
            // --- 3. ENVOI DES DONNÉES À LA JSP ---
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