package controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import dao.DonneeDAO;
import dao.WorkflowDAO;
import model.Donnee;
import model.Workflow;

@WebServlet("/details")
public class DetailsWorkflowController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            
            // Récupérer les informations du Workflow
            Workflow wf = WorkflowDAO.getById(id);
            if (wf != null) {
                // Récupérer l'historique des données
                DonneeDAO donneeDAO = new DonneeDAO();
                List<Donnee> listeDonnees = donneeDAO.getDonneesByWorkflow(id); 

                request.setAttribute("wf", wf);
                request.setAttribute("historique", listeDonnees);
                request.getRequestDispatcher("/View/details.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/listeWorkflows");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de workflow invalide");
        }
    }
}