package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkflowCreationDAO {

    /**
     * Extrait la structure de l'étape 1 sous forme de liste de dictionnaires
     */
    public List<Map<String, Object>> getStructureEtape1(int idTemplate) throws SQLException {
        List<Map<String, Object>> champs = new ArrayList<>();
        String sql = "SELECT td.id, td.nom_champ, td.type_composant, td.est_obligatoire, "
                   + "td.a_commentaire, td.a_date, td.ref_contrainte "
                   + "FROM template_donnee td "
                   + "JOIN template_etape te ON td.id_template_etape = te.id "
                   + "WHERE te.id_template_workflow = ? AND te.place = 1 "
                   + "ORDER BY td.ordre_affichage";

        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idTemplate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> champ = new HashMap<>();
                    champ.put("idTemplateDonnee", rs.getInt("id"));
                    champ.put("nomChamp", rs.getString("nom_champ"));
                    champ.put("refContrainte", rs.getString("ref_contrainte"));
                    champ.put("typeComposant", rs.getString("type_composant"));
                    champ.put("estObligatoire", rs.getBoolean("est_obligatoire"));
                    champ.put("aCommentaire", rs.getBoolean("a_commentaire"));
                    champ.put("aDate", rs.getBoolean("a_date"));
                    champs.add(champ);
                }
            }
        }
        return champs;
    }

    /**
     * Crée l'entité Workflow principale et retourne son ID généré
     */
    public int insertWorkflow(Connection conn, String titre, int idTemplate) throws SQLException {
        String sql = "INSERT INTO workflow (titre, id_template_workflow, date_creation, statut) VALUES (?, ?, CURRENT_TIMESTAMP, 'En cours')";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, titre);
            ps.setInt(2, idTemplate);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Échec de la création du workflow, aucun ID généré.");
    }

    /**
     * Insère les données initiales du formulaire de l'étape 1 en mode Batch
     */
    public void insertDonneesBatch(Connection conn, int idWorkflow, int totalChamps, jakarta.servlet.http.HttpServletRequest request) throws SQLException {
        String sql = "INSERT INTO donnee(type, attribut, commentaire, date, id_workflow, nb_etape, type_contraint_ref, id_template_donnee) "
                   + "VALUES (?, ?, ?, ?, ?, 1, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < totalChamps; i++) {
                ps.setString(1, request.getParameter("type_" + i));
                ps.setString(2, request.getParameter("attr_" + i));
                ps.setString(3, request.getParameter("comm_" + i));

                String dateStr = request.getParameter("date_" + i);
                if (dateStr != null && !dateStr.isEmpty()) {
                    ps.setDate(4, java.sql.Date.valueOf(dateStr));
                } else {
                    ps.setDate(4, null);
                }

                ps.setInt(5, idWorkflow);
                ps.setString(6, request.getParameter("ref_" + i));
                ps.setInt(7, Integer.parseInt(request.getParameter("id_template_donnee_" + i)));

                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    /**
     * Enregistre la validation automatique de l'étape 1
     */
    public void insertValidationInitiale(Connection conn, int idWorkflow, int idUser) throws SQLException {
        String sql = "INSERT INTO validation (id_personne, date, etape, id_workflow) VALUES (?, CURRENT_DATE, 1, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            ps.setInt(2, idWorkflow);
            ps.executeUpdate();
        }
    }
}