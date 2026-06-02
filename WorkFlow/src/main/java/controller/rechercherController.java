package controller;

import java.io.IOException;
import java.util.List;

import dao.WorkflowDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Workflow;

@WebServlet("/search")
public class rechercherController extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String query = request.getParameter("q");
        WorkflowDAO wfDao = new WorkflowDAO();
        List<Workflow> results;

        if (query != null && !query.trim().isEmpty()) {
            results = wfDao.searchWorkflows(query.trim());
        } else {
            results = wfDao.getAll(); // Si recherche vide, on montre tout
        }

        // On renvoie vers a.jsp avec les résultats
        request.setAttribute("workflows", results);
        request.setAttribute("currentStatus", "Résultats de recherche pour : " + query);
        request.getRequestDispatcher("/View/accueil.jsp").forward(request, response);
    }
}