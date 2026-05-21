package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.Utilisateur;
import dao.TemplateDAO;
import dao.DBConnection;

@SuppressWarnings("serial")
@WebServlet("/creer-workflow")
public class WorkflowCreateController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        // Traitement AJAX pour extraire la structure de l'étape 1
        if ("getChampsJson".equals(action)) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            int idTemplate = Integer.parseInt(request.getParameter("id_template"));
            
            String json = chargerStructureEtape1Json(idTemplate);
            try (PrintWriter out = response.getWriter()) {
                out.print(json);
                out.flush();
            }
            return;
        }

        // Chargement initial normal de la page
        TemplateDAO templateDao = new TemplateDAO();
        request.setAttribute("templates", templateDao.getAllTemplates());
        request.getRequestDispatcher("/View/creerWorkflowTemplate.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String titre = request.getParameter("titre");
        String idTemplateStr = request.getParameter("id_template");
        String commentaireInitial = request.getParameter("commentaire_initial");
        String totalChampsStr = request.getParameter("total_champs");

        if (titre == null || idTemplateStr == null || titre.trim().isEmpty()) {
            request.setAttribute("erreur", "Les champs obligatoires du projet sont absents.");
            doGet(request, response);
            return;
        }

        int idTemplate = Integer.parseInt(idTemplateStr);
        int totalChamps = (totalChampsStr != null) ? Integer.parseInt(totalChampsStr) : 0;

        HttpSession session = request.getSession();
        Utilisateur user = (Utilisateur) session.getAttribute("user");
        int idUser = (user != null && user.getId() != -1) ? user.getId() : 9; // 9 = Fallback par défaut

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Mode transactionnel complet

            // 1. Insertion de la ligne principale du Workflow
            String sqlWf = "INSERT INTO workflow (titre, id_template_workflow, date_creation, etat) VALUES (?, ?, CURRENT_TIMESTAMP, 'En cours')";
            int idWorkflow = 0;
            
            try (PreparedStatement ps = conn.prepareStatement(sqlWf, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, titre.trim());
                ps.setInt(2, idTemplate);
                ps.executeUpdate();
                
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        idWorkflow = rs.getInt(1);
                    }
                }
            }

            if (idWorkflow == 0) {
                throw new SQLException("Échec de la génération de l'ID du Workflow.");
            }

            // 2. Traitement et stockage des champs dynamiques de l'étape 1
            if (totalChamps > 0) {
                String sqlDonnee = "INSERT INTO donnee (type, attribut, commentaire, date, id_workflow, nb_etape, type_contraint_ref, id_template_donnee) VALUES (?, ?, ?, ?, ?, 1, ?, ?)";
                
                try (PreparedStatement psDonnee = conn.prepareStatement(sqlDonnee)) {
                    for (int i = 0; i < totalChamps; i++) {
                        String idTemplateDonneeStr = request.getParameter("id_template_donnee_" + i);
                        String type = request.getParameter("type_" + i);
                        String refContrainte = request.getParameter("ref_" + i);
                        String attribut = request.getParameter("attr_" + i);
                        String commentaire = request.getParameter("comm_" + i);
                        String date = request.getParameter("date_" + i);

                        String attrClean = (attribut != null) ? attribut.trim() : "";
                        String commClean = (commentaire != null) ? commentaire.trim() : "";
                        String dateClean = (date != null) ? date.trim() : "";

                        java.sql.Date sqlDate = null;
                        if (!dateClean.isEmpty()) {
                            try {
                                sqlDate = java.sql.Date.valueOf(dateClean);
                            } catch (IllegalArgumentException e) {
                                sqlDate = null;
                            }
                        }

                        psDonnee.setString(1, type);
                        psDonnee.setString(2, !attrClean.isEmpty() ? attrClean : null);
                        psDonnee.setString(3, !commClean.isEmpty() ? commClean : null);
                        psDonnee.setDate(4, sqlDate);
                        psDonnee.setInt(5, idWorkflow);
                        psDonnee.setString(6, refContrainte);
                        psDonnee.setInt(7, Integer.parseInt(idTemplateDonneeStr));
                        psDonnee.addBatch();
                    }
                    psDonnee.executeBatch();
                }
            }

            // 3. Validation automatique immédiate de l'étape 1 en base de données
            String sqlValidation = "INSERT INTO validation (id_personne, date, etape, id_workflow) VALUES (?, CURRENT_DATE, 1, ?)";
            try (PreparedStatement psVal = conn.prepareStatement(sqlValidation)) {
                psVal.setInt(1, idUser);
                psVal.setInt(2, idWorkflow);
                psVal.executeUpdate();
            }

            conn.commit();
            
            // Redirection vers l'espace de suivi global (sur l'étape 2 puisqu'on vient de clore la 1)
            response.sendRedirect(request.getContextPath() + "/saisie-etape?id_workflow=" + idWorkflow + "&num_etape=2");

        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur à la création du projet : " + e.getMessage());
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    /**
     * Extrait la configuration de l'étape 1 pour la renvoyer sous forme de chaîne JSON brute.
     */
    private String chargerStructureEtape1Json(int idTemplate) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        
        String sql = "SELECT id, nom_champ, ref_contrainte, type_composant, est_obligatoire, a_commentaire, a_date " +
                     "FROM template_donnee WHERE id_template_workflow = ? AND nb_etape = 1 ORDER BY id ASC";
                     
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            //pour construction propre avec les null pour pouvoir contourner
            ps.setInt(1, idTemplate);
            try (ResultSet rs = ps.executeQuery()) {
                boolean first = true;
                while(rs.next()) {
                    if (!first) sb.append(",");
                    sb.append("{");
                    sb.append("\"idTemplateDonnee\":").append(rs.getInt("id")).append(",");
                    sb.append("\"nomChamp\":\"").append(rs.getString("nom_champ").replace("\"", "\\\"")).append("\",");
                    sb.append("\"refContrainte\":\"").append(rs.getString("type_contraint_ref")).append("\",");
                    sb.append("\"typeComposant\":\"").append(rs.getString("type_composant") != null ? rs.getString("type_composant") : "text").append("\",");
                    sb.append("\"estObligatoire\":").append(rs.getBoolean("est_obligatoire")).append(",");
                    sb.append("\"aCommentaire\":").append(rs.getBoolean("a_commentaire")).append(",");
                    sb.append("\"aDate\":").append(rs.getBoolean("a_date"));
                    sb.append("}");
                    first = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        sb.append("]");
        return sb.toString();
    }
}