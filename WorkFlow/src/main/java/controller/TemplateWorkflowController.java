package controller;

import java.io.IOException;

import dao.TemplateDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Utilisateur;

@WebServlet("/template-workflow")
public class TemplateWorkflowController extends HttpServlet {
    
    // GET : Affiche le formulaire (vide ou pré-rempli)
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        
        if (idParam != null) {
            // MODE MODIFICATION
            int id = Integer.parseInt(idParam);
            TemplateDAO dao = new TemplateDAO();
            request.setAttribute("template", dao.getTemplateById(id));
        }
        // Sinon, requestAttribute "template" reste null -> MODE CRÉATION
        
        request.getRequestDispatcher("/View/templateWorkflow.jsp").forward(request, response);
    }

    // POST : Traite l'enregistrement
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idStr = request.getParameter("id");
        String nom = request.getParameter("nom");
        int version = Integer.parseInt(request.getParameter("version"));
        String description = request.getParameter("description");
        boolean estActif = request.getParameter("est_actif") != null;
        
        Utilisateur createur = (Utilisateur) request.getSession().getAttribute("user");

        TemplateDAO dao = new TemplateDAO();
        
        if (idStr == null || idStr.isEmpty()) {
            // INSERT
            dao.createTemplate(nom, version, description, createur.getId(), estActif);
        } else {
            // UPDATE
            int id = Integer.parseInt(idStr);
            dao.updateTemplate(id, nom, version, description, estActif);
        }
        if (idStr == null || idStr.isEmpty()) {
            int newId = dao.getLastGeneratedId(); 
            response.sendRedirect("template-design?id_workflow=" + newId);
        } else {
            response.sendRedirect("template-design?id_workflow=" + idStr);

        }
    }
}