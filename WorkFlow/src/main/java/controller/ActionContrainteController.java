package controller;

import java.io.IOException;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ContrainteDonneeService;

@WebServlet("/action-contrainte")
public class ActionContrainteController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ContrainteDonneeService service = new ContrainteDonneeService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            String idDonneeStr = request.getParameter("id_donnee");
            String action = request.getParameter("action");

            if (idDonneeStr == null) {
                response.sendRedirect(request.getContextPath() + "/template-list");
                return;
            }

            int idDonneeCible = Integer.parseInt(idDonneeStr);

            // Gestion de la suppression (DELETE)
            if ("DELETE".equalsIgnoreCase(action)) {
                int idContrainte = Integer.parseInt(request.getParameter("id_contrainte"));
                service.supprimerContrainte(idDonneeCible, idContrainte);
                response.sendRedirect(request.getContextPath() + "/action-contrainte?id_donnee=" + idDonneeCible);
                return;
            }

            // Chargement global du contexte pour la JSP
            Map<String, Object> context = service.getPageContext(idDonneeCible);
            context.forEach(request::setAttribute);

            request.getRequestDispatcher("/View/gestionContrainteDonnee.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            int idDonneeCible = Integer.parseInt(request.getParameter("id_donnee_cible"));
            int idDonneeSource = Integer.parseInt(request.getParameter("id_donnee_source"));
            int idCondition = Integer.parseInt(request.getParameter("id_condition"));
            String valeurContrainte = request.getParameter("valeur_contrainte");
            String action = request.getParameter("action"); 

            int idContrainteType = 1; // Ton identifiant de type

            // Récupération sécurisée des paramètres d'étapes
            String etapeCibleStr = request.getParameter("select_etape_actuelle");
            String etapeSourceStr = request.getParameter("select_etape");

            if (etapeCibleStr == null || etapeSourceStr == null) {
                throw new IllegalArgumentException("Les paramètres d'étapes cibles ou sources sont manquants dans la requête.");
            }

            int idEtapeCible = Integer.parseInt(etapeCibleStr); 
            int idEtapeSource = Integer.parseInt(etapeSourceStr);

            if ("PUT".equalsIgnoreCase(action)) {
                int idContrainte = Integer.parseInt(request.getParameter("id_jointure"));
                service.modifierContrainte(idContrainte, idDonneeSource, idCondition, valeurContrainte);
            } else {
                // Appel à ton service mis à jour
                service.ajouterContrainte(idDonneeCible, idDonneeSource, idCondition, valeurContrainte, idEtapeCible, idEtapeSource, idContrainteType);
            }

            response.sendRedirect(request.getContextPath() + "/action-contrainte?id_donnee=" + idDonneeCible);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors de l'enregistrement : " + e.getMessage());
        }
    }
}