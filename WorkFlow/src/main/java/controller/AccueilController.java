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
import dao.WorkflowDAO;
import model.WorkflowDisplay;
import model.Workflow;
import model.Utilisateur;
import service.WorkflowService;

@WebServlet("/home")
public class AccueilController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // Injection / Instanciation des services et DAO nécessaires
    private final WorkflowService workflowService = new WorkflowService();
    private final RoleDAO roleDao = new RoleDAO(); 

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Utilisateur user = (Utilisateur) session.getAttribute("user");

        // 1. Gardien de Sécurité : Redirection si l'utilisateur n'est pas connecté
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Récupération des critères de filtrage et de recherche
        String status = request.getParameter("status");
        String query = request.getParameter("q");

        // Normalisation du statut par défaut pour éviter les comportements imprévus dans la vue
        if (status == null || status.trim().isEmpty()) {
            status = "tous";
        }

        try {
            // 2. Orchestration Métier via la couche Service
            List<WorkflowDisplay> workflowsFiltered = workflowService.getDashboardWorkflows(status, query);
            List<Workflow> pendingList = WorkflowDAO.getWorkflowsEnAttenteParRole(user.getRole());

            // 3. Alimentation du contexte de requête pour la JSP
            request.setAttribute("workflows", workflowsFiltered);
            request.setAttribute("pendingList", pendingList);
            request.setAttribute("currentStatus", status);
            request.setAttribute("roleDAO", roleDao); 

            // 4. Passage de relais à la Vue
            request.getRequestDispatcher("/View/accueil.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors du chargement du tableau de bord.");
        }
    }
   
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}