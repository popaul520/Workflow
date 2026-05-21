package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.Workflow;
import model.template_etape;
import model.Utilisateur;
import dao.WorkflowDAO;
import dao.TemplateDAO;
import dao.ValidationDAO;
import dao.RoleDAO;
import dao.DBConnection;

@SuppressWarnings("serial")
@WebServlet("/saisie-etape")
public class SaisieEtapeController extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			String idWfStr = request.getParameter("id_workflow");
			String numEtapeStr = request.getParameter("num_etape");

			if (idWfStr == null) {
				response.sendRedirect(request.getContextPath() + "/home");
				return;
			}

			int idWf = Integer.parseInt(idWfStr);
			int numEtape = (numEtapeStr != null) ? Integer.parseInt(numEtapeStr) : 1;

			WorkflowDAO wfDao = new WorkflowDAO();
			TemplateDAO templateDao = new TemplateDAO();
			ValidationDAO validationDao = new ValidationDAO();

			Workflow wf = wfDao.getById(idWf);
			HttpSession session = request.getSession();
			Utilisateur user = (Utilisateur) session.getAttribute("user");

			if (wf == null) {
				response.sendRedirect(request.getContextPath() + "/home");
				return;
			}

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
				e.printStackTrace();
			}

			boolean isClosed = (wf.getDateFinalisation() != null);
			boolean canEdit = (hasAccess && !isClosed && (numEtape <= etapeMaxValidee + 1));

			List<Map<String, Object>> donneesEtape = templateDao.getChampsEtDonnees(idWf, wf.getIdTemplateWorkflow(),
					numEtape);

			request.setAttribute("workflow", wf);
			request.setAttribute("donneesEtape", donneesEtape);
			request.setAttribute("numEtapeActive", numEtape);
			request.setAttribute("currentEtape", configEtape);
			request.setAttribute("etapesTemplate", templateDao.getEtapesByTemplate(wf.getIdTemplateWorkflow()));
			request.setAttribute("derniereEtape", etapeMaxValidee);

			request.setAttribute("isAdmin", isAdmin);
			request.setAttribute("hasAccess", hasAccess);
			request.setAttribute("isClosed", isClosed);
			request.setAttribute("canEdit", canEdit);
			request.setAttribute("optionsAvis", Arrays.asList("Faisable", "Non faisable", "À l'étude", "Sous réserve"));

			request.getRequestDispatcher("/View/saisieEtapeDynamique.jsp").forward(request, response);

		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur au chargement de l'étape.");
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("====== [doPost] Entrée dans le contrôleur de sauvegarde ======");

		try {
			String idWfStr = request.getParameter("id_workflow");
			String currentNStr = request.getParameter("current_n");
			String totalChampsStr = request.getParameter("total_champs");

			System.out.println("👉 Données reçues -> id_workflow: " + idWfStr + " | current_n: " + currentNStr
					+ " | total_champs: " + totalChampsStr);

			if (idWfStr == null || currentNStr == null || totalChampsStr == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètres obligatoires manquants.");
				return;
			}

			int idWorkflow = Integer.parseInt(idWfStr);
			int nbEtape = Integer.parseInt(currentNStr);
			int totalChamps = Integer.parseInt(totalChampsStr);

			HttpSession session = request.getSession();
			Utilisateur user = (Utilisateur) session.getAttribute("user");
			ValidationDAO valDao = new ValidationDAO();
			WorkflowDAO wfDao = new WorkflowDAO();
			TemplateDAO templateDao = new TemplateDAO();

			String queryInsert = "INSERT INTO donnee (type, attribut, commentaire, date, id_workflow, nb_etape, type_contraint_ref, id_template_donnee) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
			String queryUpdate = "UPDATE donnee SET attribut = ?, commentaire = ?, date = ? WHERE id_donne = ?";

			int iduser = (user != null && user.getId() != -1) ? user.getId() : 9;
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

					if ((refContrainte != null && refContrainte.equalsIgnoreCase("avis"))
							|| "avis".equalsIgnoreCase(type)) {
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
						try (PreparedStatement ps = conn.prepareStatement(queryInsert)) {
							ps.setString(1, type);
							ps.setString(2, !attrClean.isEmpty() ? attrClean : null);
							ps.setString(3, !commClean.isEmpty() ? commClean : null);
							ps.setDate(4, sqlDate);
							ps.setInt(5, idWorkflow);
							ps.setInt(6, nbEtape);
							ps.setString(7, refContrainte);
							ps.setInt(8, idTemplateDonnee);
							ps.executeUpdate();
						}
					} else {
						int idDonne = Integer.parseInt(idDonneStr);
						try (PreparedStatement ps = conn.prepareStatement(queryUpdate)) {
							ps.setString(1, !attrClean.isEmpty() ? attrClean : null);
							ps.setString(2, !commClean.isEmpty() ? commClean : null);
							ps.setDate(3, sqlDate);
							ps.setInt(4, idDonne);
							ps.executeUpdate();
						}
					}
				}

				Workflow wf = wfDao.getById(idWorkflow);
				if (wf != null) {
					template_etape configEtape = templateDao.getEtapeConfig(wf.getIdTemplateWorkflow(), nbEtape);

					if (configEtape != null) {
						valDao.validerEtape(idWorkflow, iduser, configEtape.getPlace());

						if (avisSaisi != null && configEtape.isEstFinale()) {
							boolean isDefavorable = avisSaisi.equalsIgnoreCase("Non faisable")
									|| avisSaisi.equalsIgnoreCase("Défavorable")
									|| avisSaisi.equalsIgnoreCase("Sous réserve");

							if (isDefavorable) {
								wfDao.finaliserWorkflow(idWorkflow);
							}
						}
					}
				}

				conn.commit();
				response.sendRedirect(
						request.getContextPath() + "/saisie-etape?id_workflow=" + idWorkflow + "&num_etape=" + nbEtape);
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Erreur lors de la sauvegarde : " + e.getMessage());
		}
	}
}