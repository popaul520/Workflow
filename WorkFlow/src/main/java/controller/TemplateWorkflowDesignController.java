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
import dao.TypeContraintDAO; 
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.template_donnee;
import model.template_etape;
import model.TypeContraint; 
@WebServlet("/workflow-design")
public class TemplateWorkflowDesignController extends HttpServlet {
    private TemplateEtapeDAO etapeDAO = new TemplateEtapeDAO();
    private TemplateDAO workflowDAO = new TemplateDAO();
    private TemplateDonneeDAO donneeDAO = new TemplateDonneeDAO();
    private RoleDAO roleDAO = new RoleDAO();
    private TypeContraintDAO typeContraintDAO = new TypeContraintDAO(); 

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idWfStr = request.getParameter("id_workflow");
        
        if (idWfStr == null || idWfStr.isEmpty()) {
            response.sendRedirect("template-list");
            return;
        }
        
        int idWorkflow = Integer.parseInt(idWfStr);
        String action = request.getParameter("action");
        
        if ("deleteEtape".equals(action)) {
            String idEtape = request.getParameter("id_etape");
            if (idEtape != null && !idEtape.isEmpty()) {
                etapeDAO.delete(Integer.parseInt(idEtape), idWorkflow); 
            }
        } 
        else if ("deleteDonnee".equals(action)) {
            String idDonnee = request.getParameter("id_donnee");
            if (idDonnee != null && !idDonnee.isEmpty()) {
                donneeDAO.deleteDonnee(Integer.parseInt(idDonnee));
            }
        }

        request.setAttribute("workflow", workflowDAO.getTemplateById(idWorkflow));
        request.setAttribute("roles", roleDAO.getAllRoleNames());
        
        // Récupération et extraction d'une liste unique des familles de contraintes (ex: client, Bool, rayon...)
        List<TypeContraint> toutesLesContraintes = typeContraintDAO.getAll();
        List<String> listeUniqueTypes = new ArrayList<>();
        for (TypeContraint tc : toutesLesContraintes) {
            if (!listeUniqueTypes.contains(tc.getType())) {
                listeUniqueTypes.add(tc.getType());
            }
        }
        request.setAttribute("listeUniqueTypesContraints", listeUniqueTypes);

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
                // Injection de la référence contrainte existante dans la table ou la map pour la JSP
                //row.put("refTypeContraint", d.getRefTypeContraint()); 
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

        // CAS PARTICULIER : AJOUT RAPIDE D'UN TYPE CONTRAINT DIRECTEMENT DEPUIS LE DESIGNER
        if ("quick_type_contraint".equals(type)) {
            String typeNom = request.getParameter("type_nom");
            String valeurNom = request.getParameter("valeur_nom");
            if (typeNom != null && valeurNom != null && !typeNom.trim().isEmpty() && !valeurNom.trim().isEmpty()) {
                TypeContraint tc = new TypeContraint();
                tc.setType(typeNom.trim());
                tc.setValeur(valeurNom.trim());
                typeContraintDAO.create(tc);
            }
            // Recharge la même page de design
            response.sendRedirect("workflow-design?id_workflow=" + idWfStr);
            return;
        }

        if (idWfStr == null || idWfStr.isEmpty()) {
            response.sendRedirect("template-list");
            return;
        }
        int idWorkflow = Integer.parseInt(idWfStr);
        
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
            
            // Interception du référentiel sélectionné dans la bulle
            String refTypeContraint = request.getParameter("ref_type_contraint");
            //d.setRefTypeContraint(refTypeContraint != null && !refTypeContraint.isEmpty() ? refTypeContraint : null);

            donneeDAO.ajusterPositionEtSauvegarder(d);
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
            if (attenteStr != null && !attenteStr.trim().isEmpty() && !"-1".equals(attenteStr)) {
                e.setAttentePlace(Integer.parseInt(attenteStr));
            } else {
                e.setAttentePlace(0);
            }
            etapeDAO.ajusterPositionEtSauvegarder(e);
        }

        response.sendRedirect("workflow-design?id_workflow=" + idWorkflow);
    }
}