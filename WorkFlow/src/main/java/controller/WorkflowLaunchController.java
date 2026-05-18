package controller;

import java.io.IOException;

import dao.TemplateDAO;
import dao.WorkflowDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@WebServlet("/lancer-workflow")
public class WorkflowLaunchController extends HttpServlet {
    private TemplateDAO templateDao = new TemplateDAO();
    private WorkflowDAO workflowDao = new WorkflowDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // On récupère les templates pour que l'utilisateur choisisse
        request.setAttribute("templates", templateDao.getAllTemplates());
        request.getRequestDispatcher("/View/choisirTemplate.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int idTemplate = Integer.parseInt(request.getParameter("id_template"));
        String titre = request.getParameter("titre");

        // 1. Création du Workflow réel basé sur le Template
        model.Workflow newWf = new model.Workflow();
        newWf.setTitre(titre);
        newWf.setIdTemplateWorkflow(idTemplate); // Lien vers le modèle choisi
        newWf.setStatut("EN_COURS");
        
        int idGenere = workflowDao.create(newWf);

        if (idGenere > 0) {
            // 2. Redirection DIRECTE vers le contrôleur de saisie pour l'étape 1
            // On passe l'ID du workflow nouvellement créé
            response.sendRedirect("saisie-etape?id_workflow=" + idGenere + "&num_etape=1");
        } else {
            response.sendRedirect("lancer-workflow?error=1");
        }
    }
}