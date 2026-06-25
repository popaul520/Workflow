package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import dao.ContrainteDonneeDAO;
import dao.DBConnection;
import dao.DonneeDAO;
import dao.TemplateDAO;
import dao.TemplateDonneeDAO;
import dao.TypeContraintDAO;
import dao.ValidationDAO;
import dao.WorkflowDAO;
import model.TypeContraint;
import model.Utilisateur;
import model.Workflow;
import model.template_donnee;
import model.template_etape;

public class SaisieEtapeService {

    private final static WorkflowDAO wfDao = new WorkflowDAO();
    private final static TemplateDAO templateDao = new TemplateDAO();
    private final static ValidationDAO validationDao = new ValidationDAO();
    private final DonneeDAO donneeDao = new DonneeDAO();
    private final TemplateDonneeDAO templateDonneeDAO = new TemplateDonneeDAO();
    private final ContrainteDonneeDAO contrainteDonneeDAO = new ContrainteDonneeDAO();

    /**
     * Charge le contexte complet d'une étape (Utilisé par le contrôleur en GET)
     */
    public static Map<String, Object> getEtapeSaisieContext2(int idWf, int numEtape, Utilisateur user) throws Exception {
        Map<String, Object> context = new HashMap<>();
        
        Workflow wf = wfDao.getById(idWf);
        if (wf == null) return null;
        boolean isAdmin = (user != null && user.getRole() == 11);
        boolean isClosed = (wf.getDateFinalisation() != null || "Clôturé".equalsIgnoreCase(wf.getStatut()));

        // Historique des validations
        List<Integer> etapesValidees = new ArrayList<>();
        try { 
            etapesValidees = validationDao.getEtapesValidees(idWf); 
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 1. Calcul des états de TOUTES les étapes du template (pour la grille de boutons)
        List<template_etape> toutesLesEtapes = templateDao.getEtapesByTemplate(wf.getIdTemplateWorkflow());
        Map<Integer, String> etatsEtapesMap = new HashMap<>(); 

        for (template_etape tEtape : toutesLesEtapes) {
            boolean accessEtape = (user != null && (isAdmin || user.getRole() == tEtape.getRoleAssocie()));
            String etat = calculerModeAffichagePourEtape(idWf, wf, tEtape, etapesValidees, isAdmin, accessEtape, isClosed);
            etatsEtapesMap.put(tEtape.getPlace(), etat);
        }
        context.put("etatsEtapesMap", etatsEtapesMap); 

        // 2. Configuration de l'étape courante demandée
        template_etape configEtape = templateDao.getEtapeConfig(wf.getIdTemplateWorkflow(), numEtape);
        boolean hasAccess = (user != null && (isAdmin || (configEtape != null && user.getRole() == configEtape.getRoleAssocie())));

        String modeAffichage = etatsEtapesMap.getOrDefault(numEtape, "BLOQUE");
        boolean canEdit = "SAISIE".equals(modeAffichage) || "EDITION".equals(modeAffichage);

     // 3. Chargement et filtrage dynamique des données de l'étape courante
     // 3. Chargement et filtrage dynamique des données de l'étape courante
        List<Map<String, Object>> donneesEtape = new ArrayList<>();
        if (!"BLOQUE".equals(modeAffichage) || isAdmin) {
            
            System.out.println("[DEBUG SERVICE] Extraction des champs pour idWf=" + idWf + ", numEtape=" + numEtape);
            List<Map<String, Object>> rawChamps = templateDao.getChampsEtDonnees(idWf, wf.getIdTemplateWorkflow(), numEtape);
            
            if (rawChamps == null || rawChamps.isEmpty()) {
                System.out.println("[WARN SERVICE] Aucun champ retourné par le DAO pour l'étape " + numEtape);
            } else {
                System.out.println("[DEBUG SERVICE] Nombre de champs bruts trouvés : " + rawChamps.size());
            }

            SaisieEtapeService instanceService = new SaisieEtapeService();
            Map<Integer, String> valeursWorkflow = instanceService.getValeursSaisiesPourWorkflow(idWf);
            System.out.println("[DEBUG SERVICE] Nombre de valeurs historisées chargées : " + valeursWorkflow.size());

            Iterator<Map<String, Object>> iterator = rawChamps.iterator();
            while (iterator.hasNext()) {
                Map<String, Object> champ = iterator.next();
                
                // --- VÉRIFICATION FORCEE DES CLÉS (Rattrapage des alias) ---
                Object idMaitreObj = champ.get("idTemplateMaitre") != null ? champ.get("idTemplateMaitre") : champ.get("id_template_maitre");
                Object etapeMaitreObj = champ.get("etapeMaitre") != null ? champ.get("etapeMaitre") : champ.get("etape_maitre");
                Object valCibleObj = champ.get("valeurCible") != null ? champ.get("valeurCible") : champ.get("valeur_cible");
                Object opObj = champ.get("operateurContrainte") != null ? champ.get("operateurContrainte") : champ.get("operateur");
                Object hasContrainteObj = champ.get("hasContrainte") != null ? champ.get("hasContrainte") : champ.get("has_contrainte");

                // Forçage de l'indicateur si une clé maître est présente en base
                boolean structureAuneContrainte = false;
                if (hasContrainteObj instanceof Boolean) {
                    structureAuneContrainte = (Boolean) hasContrainteObj;
                } else if (idMaitreObj != null) {
                    structureAuneContrainte = true;
                }

                System.out.println("[CHAMP EVALUÉ] Nom: " + champ.get("nomChamp") 
                        + " | HasContrainte calculé: " + structureAuneContrainte 
                        + " | idMaitre trouvé: " + idMaitreObj);

                if (structureAuneContrainte) {
                    Integer idTemplateMaitre = (idMaitreObj instanceof Number) ? ((Number) idMaitreObj).intValue() : null;
                    int etapeMaitre = (etapeMaitreObj instanceof Number) ? ((Number) etapeMaitreObj).intValue() : 0;
                    String valeurCible = (valCibleObj != null) ? valCibleObj.toString() : null;
                    String operateur = (opObj != null) ? opObj.toString() : null;

                    String valeurActuelleMaitre = (idTemplateMaitre != null) ? valeursWorkflow.get(idTemplateMaitre) : null;
                    
                    // Injection propre pour la JSP
                    champ.put("valeurActuelleMaitre", valeurActuelleMaitre != null ? valeurActuelleMaitre : "");
                    champ.put("hasContrainte", true); // On harmonise pour la JSP

                    // APPEL FORCE DE LA MÉTHODE
                    boolean doitAfficher = verifierContrainteServeur(true, numEtape, etapeMaitre, valeurActuelleMaitre, operateur, valeurCible);
                    
                    if (!doitAfficher) {
                        System.out.println("[FILTRAGE SERVEUR] -> CHAMP SUPPRIMÉ : " + champ.get("nomChamp"));
                        iterator.remove(); 
                    } else {
                        System.out.println("[FILTRAGE SERVEUR] -> CHAMP CONSERVÉ : " + champ.get("nomChamp"));
                    }
                }
            }
            donneesEtape = rawChamps;
        
        }
        

        // Formatage de la chaîne des étapes validées
        StringBuilder sb = new StringBuilder();
        for (Integer e : etapesValidees) { sb.append("[").append(e).append("]"); }

        // Saisie des catalogues
        SaisieEtapeService s = new SaisieEtapeService();
        context.put("mapCatalogues", s.loadCataloguesContraints());

        context.put("workflow", wf);
        context.put("donneesEtape", donneesEtape);
        context.put("numEtapeActive", numEtape);
        context.put("currentEtape", configEtape);
        context.put("etapesTemplate", toutesLesEtapes);
        context.put("etapesValideesChaine", sb.toString());        
        context.put("isAdmin", isAdmin);
        context.put("hasAccess", hasAccess);
        context.put("isClosed", isClosed);
        context.put("modeAffichage", modeAffichage); 
        context.put("canEdit", canEdit);
        context.put("optionsAvis", Arrays.asList("Faisable", "Non faisable", "À l'étude", "Sous réserve"));
        
        return context;
    }

    /**
     * Évalue si une donnée doit être affichée côté serveur.
     */
    public static boolean verifierContrainteServeur(boolean aUneContrainte, int etapeDonnee, int etapeContrainteMaitre, 
                                                    String valeurActuelleMaitre, String operateur, String valeurCible) {
        if (!aUneContrainte) {
            return true;
        }

        System.out.println(etapeDonnee + " " +  etapeContrainteMaitre  + " " + valeurActuelleMaitre + " " + operateur  + " " +valeurCible);


        if (valeurActuelleMaitre == null || valeurActuelleMaitre.trim().isEmpty()) {
            return false;
        }

        if (operateur == null || valeurCible == null) {
            return false;
        }

        try {
            String op = operateur.trim();
            // Évaluation numérique
            if (op.equals(">") || op.equals("<") || op.equals(">=") || op.equals("<=")) {
                double valSaisie = Double.parseDouble(valeurActuelleMaitre.replace(",", "."));
                double valCible = Double.parseDouble(valeurCible.replace(",", "."));

                switch (op) {
                    case ">":  return valSaisie > valCible;
                    case "<":  return valSaisie < valCible;
                    case ">=": return valSaisie >= valCible;
                    case "<=": return valSaisie <= valCible;
                }
            }
            /*
            (boolean aUneContrainte, int etapeDonnee, int etapeContrainteMaitre, 
                                                    String valeurActuelleMaitre, String operateur, String valeurCible)
             */
            
            // Évaluation textuelle / booléenne (Gestion de l'opérateur "=" PostgreSQL)
            if (op.equals("=") || op.equals("==")) {
                return valeurActuelleMaitre.equalsIgnoreCase(valeurCible.trim());
            } else if (op.equals("!=") || op.equals("≠") || op.equals("<>")) { // Blindage ici ! au cas ou
            	System.out.println(!valeurActuelleMaitre.equalsIgnoreCase(valeurCible.trim()));
                return !valeurActuelleMaitre.equalsIgnoreCase(valeurCible.trim());
            }
            
        } catch (NumberFormatException e) {
            System.err.println("Erreur format contrainte : Comparaison impossible entre " + valeurActuelleMaitre + " et " + valeurCible);
            return false;
        }

        return false;
    }

    private static String calculerModeAffichagePourEtape(int idWf, Workflow wf, template_etape etapeConfig, List<Integer> etapesValidees, boolean isAdmin, boolean hasAccess, boolean isClosed) {
        if (isClosed) return "VISUALISATION";
        if (etapeConfig == null) return "BLOQUE";

        int placeActuelle = etapeConfig.getPlace();
        Integer idEtapeAttendue = etapeConfig.getAttentePlace(); 

        boolean amontEstValide = true;
        if (placeActuelle != 1 && idEtapeAttendue != null && idEtapeAttendue > 0) {
            amontEstValide = etapesValidees.contains(idEtapeAttendue);
        }

        if (!amontEstValide) return "BLOQUE";
        
        boolean estDejaValidee = etapesValidees.contains(placeActuelle);

        if (hasAccess || isAdmin) {
            return !estDejaValidee ? "SAISIE" : "EDITION";
        } else {
            return estDejaValidee ? "VISUALISATION" : "BLOQUE";
        }
    }

    public void saveEtapeDonnees(int idWorkflow, int nbEtape, int totalChamps, 
                                 jakarta.servlet.http.HttpServletRequest request, Utilisateur user) throws Exception {
        int idUser = (user != null && user.getId() != -1) ? user.getId() : 9;
        String avisSaisi = null;

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            for (int i = 0; i < totalChamps; i++) {
                String idDonneStr = request.getParameter("id_donne_" + i);
                String idTemplateDonneeStr = request.getParameter("id_template_donnee_" + i);
                String type = request.getParameter("type_" + i);
                String refContrainte = request.getParameter("ref_" + i);
                String attribut = request.getParameter("attr_" + i);
                String commentaire = request.getParameter("comm_" + i);
                String date = request.getParameter("date_" + i);

                String attrClean = (attribut != null) ? attribut.trim() : "";
                String commClean = (commentaire != null) ? commentaire.trim() : "";
                String dateClean = (date != null) ? date.trim() : "";

                if ("avis".equalsIgnoreCase(refContrainte) || "avis".equalsIgnoreCase(type)) {
                    avisSaisi = attrClean;
                }

                boolean isNew = (idDonneStr == null || idDonneStr.trim().isEmpty() || "0".equals(idDonneStr));
                java.sql.Date sqlDate = null;
                if (!dateClean.isEmpty()) {
                    try { sqlDate = java.sql.Date.valueOf(dateClean); } catch (IllegalArgumentException e) { sqlDate = null; }
                }

                int idTemplateDonnee = (idTemplateDonneeStr != null) ? Integer.parseInt(idTemplateDonneeStr) : 0;

                if (isNew) {
                    if (attrClean.isEmpty() && commClean.isEmpty() && dateClean.isEmpty()) continue;
                    donneeDao.insertDonnee(conn, type, !attrClean.isEmpty() ? attrClean : null, 
                            !commClean.isEmpty() ? commClean : null, sqlDate, idWorkflow, nbEtape, refContrainte, idTemplateDonnee);
                } else {
                    int idDonne = Integer.parseInt(idDonneStr);
                    donneeDao.updateDonnee(conn, !attrClean.isEmpty() ? attrClean : null, 
                            !commClean.isEmpty() ? commClean : null, sqlDate, idDonne);
                }
            }

            Workflow wf = wfDao.getById(idWorkflow);
            if (wf != null) {
                template_etape configEtape = templateDao.getEtapeConfig(wf.getIdTemplateWorkflow(), nbEtape);
                if (configEtape != null) {
                    validationDao.validerEtape(idWorkflow, idUser, configEtape.getPlace());

                    if (avisSaisi != null && configEtape.isEstFinale()) {
                        if ("Non faisable".equalsIgnoreCase(avisSaisi) || "Défavorable".equalsIgnoreCase(avisSaisi) || "Sous réserve".equalsIgnoreCase(avisSaisi)) {
                            wfDao.finaliserWorkflow(idWorkflow);
                        }
                    }
                }
            } 
            conn.commit();
        }
    }

    private Map<String, List<String>> loadCataloguesContraints() {
        Map<String, List<String>> mapCatalogues = new HashMap<>();
        String sql = "SELECT type, valeur FROM public.type_contraint ORDER BY type, id ASC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String refContrainte = rs.getString("type"); 
                String valeur = rs.getString("valeur");
                if (refContrainte != null && valeur != null) {
                    mapCatalogues.computeIfAbsent(refContrainte, k -> new ArrayList<>()).add(valeur);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mapCatalogues;
    }

    public Map<Integer, String> getValeursSaisiesPourWorkflow(int idWorkflow) {
        Map<Integer, String> map = new HashMap<>();
        String sql = "SELECT id_template_donnee, attribut FROM donnee WHERE id_workflow = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idWorkflow);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getInt("id_template_donnee"), rs.getString("attribut"));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    public void cloturerWorkflowStructurel(int idWorkflow, String decision, String commentaire, Utilisateur user) throws Exception {
        String sql = "UPDATE public.workflow SET date_finalisation = CURRENT_DATE, commentaire = ?, statut = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, commentaire != null ? commentaire.trim() : "");
            ps.setString(2, decision != null ? decision.trim() : "");
            ps.setInt(3, idWorkflow);
            ps.executeUpdate();
        }
    }
}