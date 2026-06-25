package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Utilisateur;
import service.SaisieEtapeService;

import java.io.IOException;
import java.util.Map;

@WebServlet("/saisie-etape")
public class SaisieEtapeController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final SaisieEtapeService service = new SaisieEtapeService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int numEtape = 1; // Étape par défaut
            
            // 1. Récupération sécurisée de id_workflow
            String idWfParam = request.getParameter("id_workflow");
            if (idWfParam == null || idWfParam.trim().isEmpty()) {
                response.sendRedirect("homeport?error=missing_workflow_id");
                return;
            }
            int idWorkflow = Integer.parseInt(idWfParam);

            // 2. Récupération sécurisée et conditionnelle de num_etape
            String numEtapeParam = request.getParameter("num_etape");
            if (numEtapeParam != null && !numEtapeParam.trim().isEmpty()) {
                numEtape = Integer.parseInt(numEtapeParam);
            }

            // 3. Récupération de l'utilisateur connecté depuis la Session
            HttpSession session = request.getSession();
            Utilisateur user = (Utilisateur) session.getAttribute("user");

            // 4. Appel de la méthode contextuelle
            Map<String, Object> context = SaisieEtapeService.getEtapeSaisieContext2(idWorkflow, numEtape, user);

            if (context == null) {
                response.sendRedirect("homeport?error=workflow_not_found");
                return;
            }

            // 5. Injection dynamique des clés du contexte
            for (Map.Entry<String, Object> entry : context.entrySet()) {
                request.setAttribute(entry.getKey(), entry.getValue());
            }

            // Injecter la date actuelle ISO
            request.setAttribute("currentDateIso", java.time.LocalDate.now().toString());

            // 6. Redirection vers la vue JSP
            request.getRequestDispatcher("/View/saisieEtapeDynamique.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect("homeport?error=invalid_parameters");
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Erreur critique lors du chargement du formulaire d'étape", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int idWorkflow = Integer.parseInt(request.getParameter("id_workflow"));
            int currentN = Integer.parseInt(request.getParameter("current_n"));
            int totalChamps = Integer.parseInt(request.getParameter("total_champs"));
            String isEtapeFinale = request.getParameter("is_etape_finale");

            HttpSession session = request.getSession();
            Utilisateur user = (Utilisateur) session.getAttribute("user");

            // 1. Sauvegarde standard des données du formulaire
            service.saveEtapeDonnees(idWorkflow, currentN, totalChamps, request, user);

            // 2. Gestion explicite de l'avis de clôture définitif si l'étape est finale
            if ("true".equalsIgnoreCase(isEtapeFinale)) {
                String decisionFinale = request.getParameter("decision_finale");
                String commentaireFinal = request.getParameter("commentaire_final");
                
                if (decisionFinale != null && !decisionFinale.trim().isEmpty()) {
                    service.cloturerWorkflowStructurel(idWorkflow, decisionFinale, commentaireFinal, user);
                }
            }

            // Redirection vers le même workflow (rafraîchissement sur l'étape suivante ou actuelle)
            response.sendRedirect("saisie-etape?id_workflow=" + idWorkflow + "&num_etape=" + currentN + "&success=true");

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Erreur d'écriture lors de la soumission du formulaire", e);
        }
    }
}