package controller;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import dao.RoleDAO;
import dao.WorkflowDAO;
import dao.TemplateDAO; // Import du nouveau DAO
import model.WorkflowDisplay;
import model.Workflow;
import model.Utilisateur;
import model.templateWorkflow; // Utilise le nouveau modèle aligné
import service.WorkflowService;

@WebServlet("/homeport")
public class accueilTemplateController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private final WorkflowService workflowService = new WorkflowService();
    private final RoleDAO roleDao = new RoleDAO(); 
    private final TemplateDAO templateWorkflowDao = new TemplateDAO(); // Instance DAO

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Utilisateur user = (Utilisateur) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String templateParam = request.getParameter("template");
        String status = request.getParameter("status");
        String query = request.getParameter("q");

        if (status == null || status.trim().isEmpty()) {
            status = "tous";
        }

        try {
            // SCÉNARIO A : Récupération DYNAMIQUE depuis la base de données
            if (templateParam == null || templateParam.trim().isEmpty()) {
                List<templateWorkflow> templates = templateWorkflowDao.getTemplatesActifs();
                request.setAttribute("templates", templates);
                request.getRequestDispatcher("/View/mosaiqueTemplate.jsp").forward(request, response);
                return;
            }

            // SCÉNARIO B : Dashboard filtré
            List<WorkflowDisplay> workflowsFiltered = workflowService.getDashboardWorkflows(status, query); 
            List<Workflow> pendingList = WorkflowDAO.getWorkflowsEnAttenteParRole(user.getRole());

            request.setAttribute("selectedTemplate", templateParam);
            request.setAttribute("workflows", workflowsFiltered);
            request.setAttribute("pendingList", pendingList);
            request.setAttribute("currentStatus", status);
            request.setAttribute("roleDAO", roleDao); 

            request.getRequestDispatcher("/View/accueil.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors du chargement.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}