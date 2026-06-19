package controller;

import java.io.IOException;
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

            if (idWfStr == null || idWfStr.isEmpty()) {
                response.sendRedirect("homeport");
                return;
            }

            int idWorkflow = Integer.parseInt(idWfStr);
            int numEtape = (numEtapeStr != null && !numEtapeStr.isEmpty()) ? Integer.parseInt(numEtapeStr) : 1;

            HttpSession session = request.getSession();
            Utilisateur user = (Utilisateur) session.getAttribute("user");

            // Appel au service sécurisé
            Map<String, Object> context = etapeService.getEtapeSaisieContext(idWorkflow, numEtape, user);

            if (context == null) {
                response.sendRedirect("homeport");
                return;
            }

            // --- OPTIMISATION DU CONTROLEUR ---
            // Si le service a détecté que l'étape est bloquée (donneesEtape est vide ET l'user n'est pas admin),
            // on peut choisir de rediriger directement l'utilisateur ou de laisser la JSP afficher le cadenas.
            Boolean canEdit = (Boolean) context.get("canEdit");
            boolean isAdmin = (user != null && user.getRole() == 11);
            
            // Si l'utilisateur force l'URL d'une étape bloquée sans être admin, on le renvoie à l'accueil
            if (context.get("donneesEtape") != null && ((java.util.List) context.get("donneesEtape")).isEmpty() && !canEdit && !isAdmin) {
                response.sendRedirect("homeport?error=etape_bloquee");
                return;
            }

            // Injection des variables dans la JSP
            context.forEach(request::setAttribute);

            request.getRequestDispatcher("/View/saisieEtapeDynamique.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Erreur lors du chargement de l'étape de saisie", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            int idWorkflow = Integer.parseInt(request.getParameter("id_workflow"));
            int currentEtape = Integer.parseInt(request.getParameter("current_n"));
            int totalChamps = Integer.parseInt(request.getParameter("total_champs"));

            HttpSession session = request.getSession();
            Utilisateur user = (Utilisateur) session.getAttribute("user");

            // --- DOUBLE SÉCURITÉ POST ---
            // On vérifie une seconde fois que l'utilisateur a bien le droit de soumettre ce formulaire
            Map<String, Object> contextCheck = etapeService.getEtapeSaisieContext(idWorkflow, currentEtape, user);
            Boolean canEdit = (Boolean) contextCheck.get("canEdit");
            
            if (canEdit != null && canEdit) {
                // Sauvegarde autorisée
                etapeService.saveEtapeDonnees(idWorkflow, currentEtape, totalChamps, request, user);
            }

            // Redirection (Pattern PRG)
            response.sendRedirect(request.getContextPath() + "/saisie-etape?id_workflow=" + idWorkflow + "&num_etape=" + currentEtape);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Erreur lors de l'enregistrement des données de l'étape", e);
        } 
    }
}