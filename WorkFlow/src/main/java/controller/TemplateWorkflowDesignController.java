package controller;

import java.io.IOException;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import model.template_donnee;
import model.template_etape;
import service.TemplateWorkflowDesignService;

@WebServlet("/workflow-design")
public class TemplateWorkflowDesignController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final TemplateWorkflowDesignService designService = new TemplateWorkflowDesignService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idWfStr = request.getParameter("id_workflow");
        
        if (idWfStr == null || idWfStr.isEmpty()) {
            response.sendRedirect("template-list");
            return;
        }
        
        int idWorkflow = Integer.parseInt(idWfStr);
        String action = request.getParameter("action");
        String idEtape = request.getParameter("id_etape");
        String idDonnee = request.getParameter("id_donnee");
        
        try {
            // 1. Délégation des suppressions éventuelles
            designService.executeDeleteActions(action, idEtape, idDonnee, idWorkflow);

            // 2. Chargement du contexte de rendu complet
            Map<String, Object> context = designService.getDesignerContext(idWorkflow);
            context.forEach(request::setAttribute);

            request.getRequestDispatcher("/View/templateDonnee.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors du traitement du designer.");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String type = request.getParameter("type");
        String idWfStr = request.getParameter("id_workflow");

        // Cas particulier : Ajout d'une contrainte rapide à la volée
        if ("quick_type_contraint".equals(type)) {
            designService.addQuickTypeContraint(request.getParameter("type_nom"), request.getParameter("valeur_nom"));
            response.sendRedirect("workflow-design?id_workflow=" + idWfStr);
            return;
        }

        if (idWfStr == null || idWfStr.isEmpty()) {
            response.sendRedirect("template-list");
            return;
        }
        int idWorkflow = Integer.parseInt(idWfStr);
        
        try {
            // CAS 1 : GESTION DES DONNÉES
            if ("donnee".equals(type)) {
                String idDonneeStr = request.getParameter("id_donnee");
                int idDonnee = (idDonneeStr == null || idDonneeStr.isEmpty()) ? 0 : Integer.parseInt(idDonneeStr);
                
                template_donnee d = new template_donnee();
                d.setId(idDonnee);
                d.setIdTemplateEtape(Integer.parseInt(request.getParameter("id_etape")));
                d.setNomChamp(request.getParameter("nom_champ"));
                d.setTypeComposant(request.getParameter("type_composant"));
                d.setOrdreAffichage(Integer.parseInt(request.getParameter("ordre_affichage")));
                d.setACommentaire(request.getParameter("a_commentaire") != null);
                d.setADate(request.getParameter("a_date") != null);
                d.setEstObligatoire(request.getParameter("est_obligatoire") != null);
                
                designService.saveOrUpdateDonnee(d);
            } 
            // CAS 2 : GESTION DES ÉTAPES 
            else if ("etape".equals(type)) {
                String idEtapeStr = request.getParameter("id_etape");
                int idEtape = (idEtapeStr == null || idEtapeStr.isEmpty()) ? 0 : Integer.parseInt(idEtapeStr);
                
                template_etape e = new template_etape();
                e.setId(idEtape);
                e.setIdTemplateWorkflow(idWorkflow);
                e.setNomEtape(request.getParameter("nom_etape"));
                e.setPlace(Integer.parseInt(request.getParameter("place")));
                e.setRoleAssocie(Integer.parseInt(request.getParameter("role_associe")));
                e.setEstFinale(request.getParameter("est_finale") != null);
                
                String attenteStr = request.getParameter("attente_place");
                e.setAttentePlace((attenteStr != null && !attenteStr.trim().isEmpty() && !"-1".equals(attenteStr)) 
                        ? Integer.parseInt(attenteStr) : 0);
                
                designService.saveOrUpdateEtape(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.sendRedirect("workflow-design?id_workflow=" + idWorkflow);
    }
}