package service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.RoleDAO;
import dao.TemplateDAO;
import dao.TemplateDonneeDAO;
import dao.TemplateEtapeDAO;
import dao.TypeContraintDAO;
import model.TypeContraint;
import model.template_donnee;
import model.template_etape;

public class TemplateWorkflowDesignService {

    private final TemplateEtapeDAO etapeDAO = new TemplateEtapeDAO();
    private final TemplateDAO workflowDAO = new TemplateDAO();
    private final TemplateDonneeDAO donneeDAO = new TemplateDonneeDAO();
    private final RoleDAO roleDAO = new RoleDAO();
    private final TypeContraintDAO typeContraintDAO = new TypeContraintDAO();

    /**
     * Gère les suppressions demandées dans l'URL (GET).
     */
    public void executeDeleteActions(String action, String idEtape, String idDonnee, int idWorkflow) {
        if ("deleteEtape".equals(action) && idEtape != null && !idEtape.isEmpty()) {
            etapeDAO.delete(Integer.parseInt(idEtape), idWorkflow); 
        } else if ("deleteDonnee".equals(action) && idDonnee != null && !idDonnee.isEmpty()) {
            donneeDAO.deleteDonnee(Integer.parseInt(idDonnee));
        }
    }

    /**
     * Construit tout le dictionnaire de données nécessaire au Designer de la JSP.
     */
    public Map<String, Object> getDesignerContext(int idWorkflow) {
        Map<String, Object> context = new HashMap<>();

        context.put("workflow", workflowDAO.getTemplateById(idWorkflow));
        context.put("roles", roleDAO.getAllRoleNames());
        
        // 1. Extraction propre des types de contraintes uniques
        List<TypeContraint> toutesLesContraintes = typeContraintDAO.getAll();
        List<String> listeUniqueTypes = new ArrayList<>();
        for (TypeContraint tc : toutesLesContraintes) {
            if (!listeUniqueTypes.contains(tc.getType())) {
                listeUniqueTypes.add(tc.getType());
            }
        }
        context.put("listeUniqueTypesContraints", listeUniqueTypes);

        // 2. Récupération des étapes
        List<template_etape> etapes = etapeDAO.getEtapesByWorkflow(idWorkflow);
        context.put("etapes", etapes);

        // 3. Mapping structurel des champs pour l'affichage dynamique
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
        context.put("mapDonnees", mapDonneesSimples);

        return context;
    }

    /**
     * Crée une contrainte à la volée depuis le designer.
     */
    public void addQuickTypeContraint(String typeNom, String valeurNom) {
        if (typeNom != null && valeurNom != null && !typeNom.trim().isEmpty() && !valeurNom.trim().isEmpty()) {
            TypeContraint tc = new TypeContraint();
            tc.setType(typeNom.trim());
            tc.setValeur(valeurNom.trim());
            typeContraintDAO.create(tc);
        }
    }

    /**
     * Crée ou met à jour la configuration d'un champ de donnée (Formulaire).
     */
    public void saveOrUpdateDonnee(template_donnee d) {
        donneeDAO.ajusterPositionEtSauvegarder(d);
    }

    /**
     * Crée ou met à jour la configuration d'une étape (Workflow).
     */
    public void saveOrUpdateEtape(template_etape e) {
        etapeDAO.ajusterPositionEtSauvegarder(e);
    }
}