package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import dao.RoleDAO;
import dao.TemplateDAO;
import dao.TemplateDonneeDAO;
import dao.TemplateEtapeDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.template_donnee;
import model.template_etape;

@WebServlet("/workflow-design")
public class TemplateWorkflowDesignController extends HttpServlet {
    private TemplateEtapeDAO etapeDAO = new TemplateEtapeDAO();
    private TemplateDAO workflowDAO = new TemplateDAO();
    private TemplateDonneeDAO donneeDAO = new TemplateDonneeDAO();
    private RoleDAO roleDAO = new RoleDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idWfStr = request.getParameter("id_workflow");
        
        // SÉCURITÉ : Si pas d'ID, on dégage vers la liste
        if (idWfStr == null || idWfStr.isEmpty()) {
            response.sendRedirect("template-list");
            return;
        }
        
        int idWorkflow = Integer.parseInt(idWfStr);
        String action = request.getParameter("action");

        // Actions (Delete, etc.)
        if ("deleteEtape".equals(action)) {
            String idEtape = request.getParameter("id_etape");
            if (idEtape != null && !idEtape.isEmpty()) etapeDAO.delete(Integer.parseInt(idEtape));
        } 
        else if ("deleteDonnee".equals(action)) {
            String idDonnee = request.getParameter("id_donnee");
            if (idDonnee != null && !idDonnee.isEmpty()) donneeDAO.deleteDonnee(Integer.parseInt(idDonnee));
        }

        // CHARGEMENT DES DONNÉES EN MAP
        request.setAttribute("workflow", workflowDAO.getTemplateById(idWorkflow));
        request.setAttribute("roles", roleDAO.getAllRoleNames());
        
        List<template_etape> etapes = etapeDAO.getEtapesByWorkflow(idWorkflow);
        request.setAttribute("etapes", etapes);

        Map<Integer, List<Map<String, Object>>> mapDonneesSimples = new HashMap<>();
        for (template_etape e : etapes) {
            List<template_donnee> donneesReelles = donneeDAO.getDonneesByEtape(e.getId());
            List<Map<String, Object>> listePourJsp = new ArrayList<>();

            for (template_donnee d : donneesReelles) {
                Map<String, Object> row = new HashMap<>();
                row.put("id", d.getId());
                row.put("nomChamp", d.getNomChamp());
                row.put("typeComposant", d.getTypeComposant());
                row.put("ordreAffichage", d.getOrdreAffichage());
                row.put("aCommentaire", d.isACommentaire());
                row.put("aDate", d.isADate());
                row.put("estObligatoire", d.isEstObligatoire());
                listePourJsp.add(row);
            }
            mapDonneesSimples.put(e.getId(), listePourJsp);
        }
        request.setAttribute("mapDonnees", mapDonneesSimples);

        request.getRequestDispatcher("/View/templateDonnee.jsp").forward(request, response);
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String type = request.getParameter("type");
        String idWfStr = request.getParameter("id_workflow");

        if (idWfStr == null || idWfStr.isEmpty()) {
            response.sendRedirect("template-list");
            return;
        }
        int idWorkflow = Integer.parseInt(idWfStr);

        // --- LOGIQUE ENREGISTREMENT DONNÉE ---
        if ("donnee".equals(type)) {
            template_donnee d = new template_donnee();
            String idDonneeStr = request.getParameter("id_donnee");
            int idDonnee = (idDonneeStr == null || idDonneeStr.isEmpty()) ? 0 : Integer.parseInt(idDonneeStr);
            
            d.setId(idDonnee);
            d.setIdTemplateEtape(Integer.parseInt(request.getParameter("id_etape")));
            d.setNomChamp(request.getParameter("nom_champ"));
            d.setTypeComposant(request.getParameter("type_composant"));
            d.setOrdreAffichage(Integer.parseInt(request.getParameter("ordre_affichage")));
            d.setACommentaire(request.getParameter("a_commentaire") != null);
            d.setADate(request.getParameter("a_date") != null);
            d.setEstObligatoire(request.getParameter("est_obligatoire") != null);

            if (idDonnee == 0) {
                donneeDAO.addDonnee(d);
            } else {
                donneeDAO.updateDonnee(d);
            }
        } 
        // --- LOGIQUE ENREGISTREMENT ÉTAPE ---
        else if ("etape".equals(type)) {
            template_etape e = new template_etape();
            String idEtapeStr = request.getParameter("id_etape");
            int idEtape = (idEtapeStr == null || idEtapeStr.isEmpty()) ? 0 : Integer.parseInt(idEtapeStr);
            
            e.setId(idEtape);
            e.setIdTemplateWorkflow(idWorkflow);
            e.setNomEtape(request.getParameter("nom_etape"));
            e.setPlace(Integer.parseInt(request.getParameter("place")));
            e.setRoleAssocie(Integer.parseInt(request.getParameter("role_associe")));
            e.setEstFinale(request.getParameter("est_finale") != null);
            etapeDAO.saveOrUpdate(e); // Assure-toi que cette méthode existe dans ton DAO
            
        }

        response.sendRedirect("workflow-design?id_workflow=" + idWorkflow);
    }
}