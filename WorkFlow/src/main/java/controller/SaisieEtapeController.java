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
                response.sendRedirect(request.getContextPath() + "/homeport");
                return;
            }

            int idWf = Integer.parseInt(idWfStr);
            int numEtape = (numEtapeStr != null) ? Integer.parseInt(numEtapeStr) : 1;

            HttpSession session = request.getSession();
            Utilisateur user = (Utilisateur) session.getAttribute("user");

            Map<String, Object> context = etapeService.getEtapeSaisieContext(idWf, numEtape, user);

            if (context == null) {
                response.sendRedirect(request.getContextPath() + "/homeport");
                return;
            }

            context.forEach(request::setAttribute);

            // Sécurité : Si service utilise encore 'derniereEtape' (un entier), 
            // on s'assure qu'il soit bien présent pour la JSP
            if (context.get("derniereEtape") == null) {
                request.setAttribute("derniereEtape", numEtape); 
            }

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
            etapeService.saveEtapeDonnees(idWorkflow, nbEtape, totalChamps, request, user);
            if (isEtapeFinale) {
                String decisionFinale = request.getParameter("decision_finale");
                String commentaireFinal = request.getParameter("commentaire_final");
                
                if (decisionFinale != null && !decisionFinale.trim().isEmpty()) {
                    String decisionStyle = decisionFinale.trim();
                    etapeService.cloturerWorkflowStructurel(idWorkflow, decisionStyle, commentaireFinal, user);
                }
            }
            // CORRECTION : On renvoie explicitement vers l'étape courante pour éviter le retour à l'étape 1
            response.sendRedirect(request.getContextPath() + "/saisie-etape?id_workflow=" + idWorkflow + "&num_etape=" + nbEtape);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors de la sauvegarde : " + e.getMessage());
        }
    }
}