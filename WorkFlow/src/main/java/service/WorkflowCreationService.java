package service;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dao.DBConnection;
import dao.TemplateDAO;
import dao.WorkflowCreationDAO;

public class WorkflowCreationService {

    private final TemplateDAO templateDao = new TemplateDAO();
    private final WorkflowCreationDAO creationDao = new WorkflowCreationDAO();

    public List<?> getAllTemplatesAvailable() {
        return templateDao.getAllTemplates();
    }

    /**
     * Génère une chaîne JSON propre et échappée pour la structure de l'étape 1
     */
    public String getStructureEtape1AsJson(int idTemplate) {
        try {
            List<Map<String, Object>> structure = creationDao.getStructureEtape1(idTemplate);
            
            // Construction du JSON via Stream pour éviter les concaténations manuelles fragiles
            return structure.stream().map(m -> {
                return String.format(
                    "{\"idTemplateDonnee\":%d,\"nomChamp\":\"%s\",\"refContrainte\":\"%s\","
                    + "\"typeComposant\":\"%s\",\"estObligatoire\":%b,\"aCommentaire\":%b,\"aDate\":%b}",
                    m.get("idTemplateDonnee"),
                    safeJson((String) m.get("nomChamp")),
                    safeJson((String) m.get("refContrainte")),
                    safeJson((String) m.get("typeComposant")),
                    m.get("estObligatoire"),
                    m.get("aCommentaire"),
                    m.get("aDate")
                );
            }).collect(Collectors.joining(",", "[", "]"));
            
        } catch (Exception e) {
            e.printStackTrace();
            return "[]";
        }
    }

    /**
     * Pilote l'intégralité de la transaction de création du Workflow initialisé
     */
    public void createWorkflowInstance(String titre, int idTemplate, int totalChamps, 
                                       jakarta.servlet.http.HttpServletRequest request) throws Exception {
        
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Insertion du Workflow master
                int idWorkflow = creationDao.insertWorkflow(conn, titre, idTemplate);

                // 2. Traitement et injection des données du formulaire d'étape 1
                if (totalChamps > 0) {
                    creationDao.insertDonneesBatch(conn, idWorkflow, totalChamps, request);
                }

                // 3. Validation de l'initiateur (Etape 1)
                int currentUserId = 1; // Id utilisateur simulé ou extrait de la session
                creationDao.insertValidationInitiale(conn, idWorkflow, currentUserId);

                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        }
    }

    private String safeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}