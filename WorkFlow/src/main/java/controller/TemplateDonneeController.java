package controller;

import java.io.IOException;

import dao.TemplateDonneeDAO;
import dao.TemplateEtapeDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.template_donnee;

@SuppressWarnings("serial")
@WebServlet("/template-donnees")
public class TemplateDonneeController extends HttpServlet {
    private TemplateDonneeDAO donneeDAO = new TemplateDonneeDAO();
    private TemplateEtapeDAO etapeDAO = new TemplateEtapeDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int idEtape = Integer.parseInt(request.getParameter("id_etape"));
        
        // Suppression si demandée
        if ("delete".equals(request.getParameter("action"))) {
            donneeDAO.deleteDonnee(Integer.parseInt(request.getParameter("id")));
        }

        request.setAttribute("etape", etapeDAO.getEtapeById(idEtape)); // À ajouter dans ton EtapeDAO
        request.setAttribute("donnees", donneeDAO.getDonneesByEtape(idEtape));
        request.getRequestDispatcher("/View/templateDonnee.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        template_donnee d = new template_donnee();
        int idEtape = Integer.parseInt(request.getParameter("id_etape"));
        
        d.setIdTemplateEtape(idEtape);
        d.setNomChamp(request.getParameter("nom_champ"));
        d.setTypeComposant(request.getParameter("type_composant"));
        d.setOrdreAffichage(Integer.parseInt(request.getParameter("ordre_affichage")));
        d.setACommentaire(request.getParameter("a_commentaire") != null);
        d.setADate(request.getParameter("a_date") != null);
        d.setEstObligatoire(request.getParameter("est_obligatoire") != null);
        d.setRefContrainte(request.getParameter("ref_contrainte"));

        donneeDAO.addDonnee(d);
        response.sendRedirect("template-donnees?id_etape=" + idEtape);
    }
}