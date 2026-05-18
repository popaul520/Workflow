package controller;

import java.io.IOException;

import dao.RoleDAO;
import dao.TemplateDAO;
import dao.TemplateEtapeDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Utilisateur;
import model.template_etape; 

@WebServlet("/template-etapes")
public class TemplateEtapeController extends HttpServlet {
    private TemplateEtapeDAO etapeDAO = new TemplateEtapeDAO();
    private TemplateDAO workflowDAO = new TemplateDAO();
    private RoleDAO roleDAO = new RoleDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int idWorkflow = Integer.parseInt(request.getParameter("id_workflow"));
        
        String action = request.getParameter("action");
        if ("delete".equals(action)) {
            etapeDAO.delete(Integer.parseInt(request.getParameter("id_etape")));
        }

        request.setAttribute("workflow", workflowDAO.getTemplateById(idWorkflow));
        request.setAttribute("etapes", etapeDAO.getEtapesByWorkflow(idWorkflow));
        request.setAttribute("roles", roleDAO.getAllRoleNames());
        
        request.getRequestDispatcher("/View/templateEtape.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        template_etape e = new template_etape();
        e.setId(Integer.parseInt(request.getParameter("id_etape")));
        e.setIdTemplateWorkflow(Integer.parseInt(request.getParameter("id_workflow")));
        e.setNomEtape(request.getParameter("nom_etape"));
        e.setPlace(Integer.parseInt(request.getParameter("place")));
        e.setAttentePlace(Integer.parseInt(request.getParameter("attente_place")));
        e.setRoleAssocie(Integer.parseInt(request.getParameter("role_associe")));
        e.setEstFinale(request.getParameter("est_finale") != null);
        etapeDAO.saveOrUpdate(e);
        response.sendRedirect("template-etapes?id_workflow=" + e.getIdTemplateWorkflow());
    }
}