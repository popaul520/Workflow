package service;

import java.sql.Connection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.DBConnection;
import dao.DonneeDAO;
import dao.TemplateDAO;
import dao.ValidationDAO;
import dao.WorkflowDAO;
import model.Utilisateur;
import model.Workflow;
import model.template_etape;

public class SaisieEtapeService {

    private final WorkflowDAO wfDao = new WorkflowDAO();
    private final TemplateDAO templateDao = new TemplateDAO();
    private final ValidationDAO validationDao = new ValidationDAO();
    private final DonneeDAO donneeDao = new DonneeDAO();

    /**
     * Prépare toutes les données requises pour l'affichage de l'étape
     */
    public Map<String, Object> getEtapeSaisieContext(int idWf, int numEtape, Utilisateur user) throws Exception {
        Map<String, Object> context = new HashMap<>();

        Workflow wf = wfDao.getById(idWf);
        if (wf == null) return null;

        boolean isAdmin = (user != null && user.getRole() == 11);
        boolean hasAccess = false;

        template_etape configEtape = templateDao.getEtapeConfig(wf.getIdTemplateWorkflow(), numEtape);
        if (user != null) {
            if (isAdmin) {
                hasAccess = true;
            } else if (configEtape != null) {
                hasAccess = (user.getRole() == configEtape.getRoleAssocie());
            }
        }

        int etapeMaxValidee = 0;
        try {
            etapeMaxValidee = validationDao.getDerniereEtapeValidee(idWf);
        } catch (Exception e) {
            // Log managé
        }

        boolean isClosed = (wf.getDateFinalisation() != null);
        boolean canEdit = (hasAccess && !isClosed && (numEtape <= etapeMaxValidee + 1));

        List<Map<String, Object>> donneesEtape = templateDao.getChampsEtDonnees(idWf, wf.getIdTemplateWorkflow(), numEtape);

        context.put("workflow", wf);
        context.put("donneesEtape", donneesEtape);
        context.put("numEtapeActive", numEtape);
        context.put("currentEtape", configEtape);
        context.put("etapesTemplate", templateDao.getEtapesByTemplate(wf.getIdTemplateWorkflow()));
        context.put("derniereEtape", etapeMaxValidee);
        context.put("isAdmin", isAdmin);
        context.put("hasAccess", hasAccess);
        context.put("isClosed", isClosed);
        context.put("canEdit", canEdit);
        context.put("optionsAvis", Arrays.asList("Faisable", "Non faisable", "À l'étude", "Sous réserve"));

        return context;
    }

    /**
     * Gère la sauvegarde transactionnelle des formulaires dynamiques
     */
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
                    try {
                        sqlDate = java.sql.Date.valueOf(dateClean);
                    } catch (IllegalArgumentException e) {
                        sqlDate = null;
                    }
                }

                int idTemplateDonnee = (idTemplateDonneeStr != null) ? Integer.parseInt(idTemplateDonneeStr) : 0;

                if (isNew) {
                    if (attrClean.isEmpty() && commClean.isEmpty() && dateClean.isEmpty()) {
                        continue;
                    }
                    donneeDao.insertDonnee(conn, type, !attrClean.isEmpty() ? attrClean : null, 
                            !commClean.isEmpty() ? commClean : null, sqlDate, idWorkflow, nbEtape, refContrainte, idTemplateDonnee);
                } else {
                    int idDonne = Integer.parseInt(idDonneStr);
                    donneeDao.updateDonnee(conn, !attrClean.isEmpty() ? attrClean : null, 
                            !commClean.isEmpty() ? commClean : null, sqlDate, idDonne);
                }
            }

            // Gestion des validations et finalisation automatique
            Workflow wf = wfDao.getById(idWorkflow);
            if (wf != null) {
                template_etape configEtape = templateDao.getEtapeConfig(wf.getIdTemplateWorkflow(), nbEtape);
                if (configEtape != null) {
                    validationDao.validerEtape(idWorkflow, idUser, configEtape.getPlace());

                    if (avisSaisi != null && configEtape.isEstFinale()) {
                        boolean isDefavorable = "Non faisable".equalsIgnoreCase(avisSaisi)
                                || "Défavorable".equalsIgnoreCase(avisSaisi)
                                || "Sous réserve".equalsIgnoreCase(avisSaisi);

                        if (isDefavorable) {
                            wfDao.finaliserWorkflow(idWorkflow);
                        }
                    }
                }
            }
            conn.commit();
        }
    }
}