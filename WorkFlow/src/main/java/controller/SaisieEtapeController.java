package controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.Utilisateur;
import service.SaisieEtapeService;

@WebServlet("/saisie-etape")
public class SaisieEtapeController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final SaisieEtapeService etapeService = new SaisieEtapeService();

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

            HttpSession session = request.getSession();
            Utilisateur user = (Utilisateur) session.getAttribute("user");

            // Récupération du contexte complet via le Service (incluant mapCatalogues depuis la table type_contraint)
            Map<String, Object> context = etapeService.getEtapeSaisieContext(idWf, numEtape, user);

            if (context == null) {
                response.sendRedirect(request.getContextPath() + "/home");
                return;
            }

            // Injection des structures chargées
            context.forEach(request::setAttribute);

            // Génération de la date système du jour pour le champ HTML5 date (Format requis: yyyy-MM-dd)
            String currentDateIso = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            request.setAttribute("currentDateIso", currentDateIso);

            request.getRequestDispatcher("/View/saisieEtapeDynamique.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur au chargement de l'étape.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String idWfStr = request.getParameter("id_workflow");
            String currentNStr = request.getParameter("current_n");
            String totalChampsStr = request.getParameter("total_champs");
            String isEtapeFinaleStr = request.getParameter("is_etape_finale");

            if (idWfStr == null || currentNStr == null || totalChampsStr == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètres obligatoires manquants.");
                return;
            }

            int idWorkflow = Integer.parseInt(idWfStr);
            int nbEtape = Integer.parseInt(currentNStr);
            int totalChamps = Integer.parseInt(totalChampsStr);
            boolean isEtapeFinale = Boolean.parseBoolean(isEtapeFinaleStr);

            HttpSession session = request.getSession();
            Utilisateur user = (Utilisateur) session.getAttribute("user");

            // 1. Sauvegarde des attributs dynamiques classiques de l'étape
            etapeService.saveEtapeDonnees(idWorkflow, nbEtape, totalChamps, request, user);

            // 2. Traitement spécifique si l'étape est marquée finale (Clôture globale du dossier)
            if (isEtapeFinale) {
                String decisionFinale = request.getParameter("decision_finale");
                String commentaireFinal = request.getParameter("commentaire_final");
                
                if ( ( decisionFinale != null && !decisionFinale.isEmpty()) && decisionFinale.compareToIgnoreCase("Non Faisable") == 0  ) {
                    // Transmission des données d'exécution au service de mise à jour du Workflow
                    etapeService.cloturerWorkflowStructurel(idWorkflow, decisionFinale, commentaireFinal, user);
                }
            }

            response.sendRedirect(request.getContextPath() + "/saisie-etape?id_workflow=" + idWorkflow + "&num_etape=" + nbEtape);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors de la sauvegarde : " + e.getMessage());
        }
    }
}