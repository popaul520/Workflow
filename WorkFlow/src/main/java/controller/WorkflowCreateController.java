package controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Enumeration;
import dao.DonneeDAO;
import dao.TemplateDAO;
import dao.WorkflowDAO;
import dao.ValidationDAO;
import model.Donnee;
import model.Workflow;


@SuppressWarnings("serial")
@WebServlet("/creer-workflow")
public class WorkflowCreateController extends HttpServlet {
    private WorkflowDAO wfDao = new WorkflowDAO();
    private DonneeDAO donneeDao = new DonneeDAO();
    private TemplateDAO templateDao = new TemplateDAO(); // Ajout du DAO de gestion des templates

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. Récupération de tous les templates existants pour alimenter la liste déroulante
        request.setAttribute("templates", templateDao.getAllTemplates());
        
        // 2. Affichage de la page de sélection initiale
        request.getRequestDispatcher("View/creerWorkflowTemplate.jsp").forward(request, response);
    } 

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String titre = request.getParameter("titre");
        String idTemplateStr = request.getParameter("id_template");
        
        if (titre == null || titre.trim().isEmpty() || idTemplateStr == null || idTemplateStr.isEmpty()) {
            request.setAttribute("erreur", "Le titre et le choix du modèle de template sont obligatoires.");
            doGet(request, response);
            return;
        }

        int idTemplate = Integer.parseInt(idTemplateStr);
        
        // 1. Création de l'instance de Workflow liée au Template sélectionné
        Workflow wf = new Workflow();
        wf.setTitre(titre);
        wf.setIdTemplateWorkflow(idTemplate); // Liaison dynamique avec le modèle choisi
        wf.setDateCreation(new java.util.Date());
        
        int idWf = wfDao.create(wf); 

        if (idWf > 0) {
            // 2. Initialisation structurelle des étapes en base
            for (int i = 1; i <= 10; i++) {
                donneeDao.creerEtapeWorkflow(idWf, i);
            }

            // 3. REDIRECTION IMMÉDIATE vers l'étape numéro 1 associée à ce template
            response.sendRedirect(request.getContextPath() + "/saisie-etape?id_workflow=" + idWf + "&num_etape=1");
        } else {
            request.setAttribute("erreur", "Impossible de générer le workflow.");
            doGet(request, response);
        }
    }
}