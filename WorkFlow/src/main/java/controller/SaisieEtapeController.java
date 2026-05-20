package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // 1. Récupération sécurisée des paramètres
            String idWfStr = request.getParameter("id_workflow");
            String numEtapeStr = request.getParameter("num_etape");
            
            if (idWfStr == null) {
                response.sendRedirect(request.getContextPath() + "/home");
                return;
            }

            int idWf = Integer.parseInt(idWfStr);
            int numEtape = (numEtapeStr != null) ? Integer.parseInt(numEtapeStr) : 1;

            // 2. Initialisation des DAOs
            WorkflowDAO wfDao = new WorkflowDAO();
            TemplateDAO templateDao = new TemplateDAO();
            ValidationDAO validationDao = new ValidationDAO();
            RoleDAO roleDao = new RoleDAO();

            // 3. Récupération des objets métier et session
            Workflow wf = wfDao.getById(idWf);
            HttpSession session = request.getSession();
            Utilisateur user = (Utilisateur) session.getAttribute("user");

            if (wf == null) {
                response.sendRedirect(request.getContextPath() + "/home");
                return;
            }

            // 4. Gestion des rôles et habilitations (Repris de EtapeControllerglobal)
            boolean isAdmin = (user != null && (user.getRole() == 11 || roleDao.canAccessEtape(user.getRole(), 11))); // 11 = PATRON
            boolean hasAccess = false;
            if (user != null) {
                hasAccess = roleDao.canAccessEtape(user.getRole(), numEtape) || user.getRole() == numEtape;
            }

            // 5 Calcul de l'état d'avancement du Workflow
            int etapeMaxValidee = 0;
            try {
                etapeMaxValidee = validationDao.getDerniereEtapeValidee(idWf);
            } catch (Exception e) {
                e.printStackTrace();
            }

            boolean isClosed = (wf.getDateFinalisation() != null);
            // On peut éditer si on a l'accès, que le workflow n'est pas clos ET que l'étape précédente est validée (ou étape 1)
            boolean canEdit = isAdmin || (hasAccess && !isClosed && (numEtape <= etapeMaxValidee + 1));

            // 6. Récupération de la structure dynamique des données
            List<Map<String, Object>> donneesEtape = templateDao.getChampsEtDonnees(idWf, wf.getIdTemplateWorkflow(), numEtape);

            // 7 Envoi des attributs à la JSP (Unification des deux contrôleurs)
            request.setAttribute("workflow", wf);
            request.setAttribute("wf", wf); // Doublon JSP
            request.setAttribute("donneesEtape", donneesEtape); 
            request.setAttribute("numEtapeActive", numEtape);
            request.setAttribute("numEtape", numEtape); // Compatibilité
            request.setAttribute("id_workflow", idWf);
            
            request.setAttribute("currentEtape", templateDao.getEtapeConfig(wf.getIdTemplateWorkflow(), numEtape));
            request.setAttribute("etapesTemplate", templateDao.getEtapesByTemplate(wf.getIdTemplateWorkflow()));
            request.setAttribute("etapesValidees", validationDao.getListeEtapesValidees(idWf));
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

    // ==========================================
    // 2. ENREGISTREMENT ET CLÔTURE (doPost)
    // ==========================================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int idWorkflow = Integer.parseInt(request.getParameter("id_workflow"));
        int nbEtape = Integer.parseInt(request.getParameter("current_n"));
        int totalChamps = Integer.parseInt(request.getParameter("total_champs"));

        HttpSession session = request.getSession();
        Utilisateur user = (Utilisateur) session.getAttribute("user");

        ValidationDAO valDao = new ValidationDAO();
        WorkflowDAO wfDao = new WorkflowDAO();

        String queryInsert = "INSERT INTO donnee (type, attribut, commentaire, date, id_workflow, nb_etape, type_contraint_ref, id_template_donnee) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String queryUpdate = "UPDATE donnee SET attribut = ?, commentaire = ?, date = ? WHERE id_donne = ?";

        // Stockage de l'éventuel avis pour la clôture en fin de traitement
        String avisSaisi = null;

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); 

            for (int i = 0; i < totalChamps; i++) {
                String idDonneStr = request.getParameter("id_donne_" + i);
                int idTemplateDonnee = Integer.parseInt(request.getParameter("id_template_donnee_" + i));
                String type = request.getParameter("type_" + i);
                String refContrainte = request.getParameter("ref_" + i);
                
                String attribut = request.getParameter("attr_" + i);
                String commentaire = request.getParameter("comm_" + i);
                String date = request.getParameter("date_" + i);

                String attrClean = (attribut != null) ? attribut.trim() : "";
                String commClean = (commentaire != null) ? commentaire.trim() : "";
                String dateClean = (date != null) ? date.trim() : "";

                // Interception de l'attribut si c'est un champ de type "avis" ou nommé "avis" pour la clôture automatique
                if (refContrainte != null && refContrainte.equalsIgnoreCase("avis") || "avis".equalsIgnoreCase(type)) {
                    avisSaisi = attrClean;
                } 

                boolean isNew = (idDonneStr == null || idDonneStr.trim().isEmpty() || "0".equals(idDonneStr));

                // Conversion de la date pour PostgreSQL
                java.sql.Date sqlDate = null;
                if (!dateClean.isEmpty()) {
                    try {
                        sqlDate = java.sql.Date.valueOf(dateClean);
                    } catch (IllegalArgumentException e) {
                        sqlDate = null;
                    }
                }

                if (isNew) {
                    // Si le champ est totalement vide, on évite d'insérer une ligne pour rien
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

	            if (user != null) {
	                TemplateDAO templateDao = new TemplateDAO();
	                template_etape configEtape = templateDao.getEtapeConfig(idWorkflow, nbEtape);
	               
	                if (configEtape != null) {
	                    valDao.validerEtape(idWorkflow, user.getId(), configEtape.getPlace());
	
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
	            conn.commit(); // Validation des inserts/updates des données de l'étape (SQL)

	            response.sendRedirect(request.getContextPath() + "/saisie-etape?id_workflow=" + idWorkflow + "&num_etape=" + nbEtape);
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors de la sauvegarde.");
	        }
	}
}
