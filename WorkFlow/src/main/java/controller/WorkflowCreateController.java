package controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.WorkflowCreationService;

@WebServlet("/creer-workflow")
public class WorkflowCreateController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final WorkflowCreationService creationService = new WorkflowCreationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        // Rendu API Rest asynchrone pour charger dynamiquement les champs
        if ("getChampsJson".equals(action)) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            String idTemplateStr = request.getParameter("id_template");
            String json = "[]";

            if (idTemplateStr != null && !idTemplateStr.isEmpty()) {
                int idTemplate = Integer.parseInt(idTemplateStr);
                json = creationService.getStructureEtape1AsJson(idTemplate);
            }

            response.getWriter().write(json);
            return;
        }

        // Rendu standard du formulaire HTML
        request.setAttribute("templates", creationService.getAllTemplatesAvailable());
        request.getRequestDispatcher("/View/creerWorkflowTemplate.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            request.setCharacterEncoding("UTF-8");

            String titre = request.getParameter("titre");
            int idTemplate = Integer.parseInt(request.getParameter("id_template"));
            String totalStr = request.getParameter("total_champs");
            int totalChamps = (totalStr != null && !totalStr.isEmpty()) ? Integer.parseInt(totalStr) : 0;

            // Déclenchement de la transaction d'initialisation via le Service
            creationService.createWorkflowInstance(titre, idTemplate, totalChamps, request);

            response.sendRedirect(request.getContextPath() + "/home");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors de la création de l'instance de workflow.");
        }
    }
}