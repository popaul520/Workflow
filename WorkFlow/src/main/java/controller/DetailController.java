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
public class DetailController extends HttpServlet {
    private WorkflowDAO wfDao = new WorkflowDAO();
    private DonneeDAO donneeDao = new DonneeDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idStr = request.getParameter("id");
        
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            
            // 1. Récupérer les infos du Workflow
            Workflow wf = wfDao.getById(id);
            
            if (wf != null) {
                // 2. Récupérer l'historique des données saisies
                List<Donnee> historique = donneeDao.getDonneesByWorkflow(id); 
                request.setAttribute("wf", wf);
                request.setAttribute("historique", historique);
                request.getRequestDispatcher("/View/details.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/home");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de workflow invalide");
        }
    }
}