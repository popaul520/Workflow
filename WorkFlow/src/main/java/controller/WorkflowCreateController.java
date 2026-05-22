package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import dao.TemplateDAO;
import dao.DBConnection;

@SuppressWarnings("serial")

@WebServlet("/creer-workflow")
public class WorkflowCreateController extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String action = request.getParameter("action");

		if ("getChampsJson".equals(action)) {

			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");

			String idTemplateStr = request.getParameter("id_template");
			String json = "[]";

			if (idTemplateStr != null && !idTemplateStr.isEmpty()) {
				int idTemplate = Integer.parseInt(idTemplateStr);
				json = chargerStructureEtape1Json(idTemplate);
			}

			response.getWriter().write(json);
			return;
		}

		TemplateDAO templateDao = new TemplateDAO();
		request.setAttribute("templates", templateDao.getAllTemplates());

		request.getRequestDispatcher("/View/creerWorkflowTemplate.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			request.setCharacterEncoding("UTF-8");

			String titre = request.getParameter("titre");
			int idTemplate = Integer.parseInt(request.getParameter("id_template"));

			String totalStr = request.getParameter("total_champs");
			int totalChamps = (totalStr != null && !totalStr.isEmpty()) ? Integer.parseInt(totalStr) : 0;

			Connection conn = DBConnection.getConnection();
			conn.setAutoCommit(false);

			// ✅ INSERT WORKFLOW
			String sqlWf = "INSERT INTO workflow (titre, id_template_workflow, date_creation, statut) VALUES (?, ?, CURRENT_TIMESTAMP, 'En cours')";

			int idWorkflow = 0;

			PreparedStatement ps = conn.prepareStatement(sqlWf, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, titre);
			ps.setInt(2, idTemplate);
			ps.executeUpdate();

			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				idWorkflow = rs.getInt(1);
			}

			//  INSERT DONNEES
			if (totalChamps > 0) {
				String sqlD = "INSERT INTO donnee(type, attribut, commentaire, date, id_workflow, nb_etape, type_contraint_ref, id_template_donnee) VALUES (?, ?, ?, ?, ?, 1, ?, ?)";

				PreparedStatement psD = conn.prepareStatement(sqlD);

				for (int i = 0; i < totalChamps; i++) {

					psD.setString(1, request.getParameter("type_" + i));
					psD.setString(2, request.getParameter("attr_" + i));
					psD.setString(3, request.getParameter("comm_" + i));

					String dateStr = request.getParameter("date_" + i);
					if (dateStr != null && !dateStr.isEmpty()) {
						psD.setDate(4, java.sql.Date.valueOf(dateStr));
					} else {
						psD.setDate(4, null);
					}

					psD.setInt(5, idWorkflow);
					psD.setString(6, request.getParameter("ref_" + i));
					psD.setInt(7, Integer.parseInt(request.getParameter("id_template_donnee_" + i)));

					psD.addBatch();
				}

				psD.executeBatch();
			}

			// INSERT VALIDATION
			String sqlVal = "INSERT INTO validation (id_personne, date, etape, id_workflow) VALUES (?, CURRENT_DATE, 1, ?)";

			PreparedStatement psVal = conn.prepareStatement(sqlVal);
			psVal.setInt(1, 1); // user
			psVal.setInt(2, idWorkflow);
			psVal.executeUpdate();

			conn.commit();

			response.sendRedirect("home");

		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(500);
		}
	}

	private String chargerStructureEtape1Json(int idTemplate) {

		StringBuilder sb = new StringBuilder();
		sb.append("[");

		String sql =
			    "SELECT td.id, td.nom_champ, td.type_composant, td.est_obligatoire, " +
			    "td.a_commentaire, td.a_date, td.ref_contrainte " +
			    "FROM template_donnee td " +
			    "JOIN template_etape te ON td.id_template_etape = te.id " +
			    "WHERE te.id_template_workflow = ? AND te.place = 1 " +
			    "ORDER BY td.ordre_affichage";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, idTemplate);
			ResultSet rs = ps.executeQuery();

			boolean first = true;

			while (rs.next()) {

				if (!first)
					sb.append(",");
				sb.append("{");

				sb.append("\"idTemplateDonnee\":").append(rs.getInt("id")).append(",");

				sb.append("\"nomChamp\":\"").append(safe(rs.getString("nom_champ"))).append("\",");

				String ref = rs.getString("ref_contrainte");
				sb.append("\"refContrainte\":\"").append(safe(ref)).append("\",");

				sb.append("\"typeComposant\":\"")
				  .append(safe(rs.getString("type_composant")))
				  .append("\",");
				sb.append("\"estObligatoire\":").append(rs.getBoolean("est_obligatoire")).append(",");
				sb.append("\"aCommentaire\":").append(rs.getBoolean("a_commentaire")).append(",");
				sb.append("\"aDate\":").append(rs.getBoolean("a_date"));
				sb.append("}");

				first = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		sb.append("]");
		return sb.toString();
	}
	
	private String safe(String s) {
	    if (s == null) return "";
	    return s.replace("\"", "\\\"");
	}
}
