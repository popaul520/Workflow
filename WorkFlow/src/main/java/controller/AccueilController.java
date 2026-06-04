package controller;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.RoleDAO;
import dao.WorkflowDAO;
import dao.TemplateDAO;
import model.WorkflowDisplay;
import model.Workflow;
import model.Utilisateur;
import model.templateWorkflow;
import service.WorkflowService;

@WebServlet("/home")
public class AccueilController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private final WorkflowService workflowService = new WorkflowService();
    private final RoleDAO roleDao = new RoleDAO(); 
    private final TemplateDAO templateWorkflowDao = new TemplateDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Utilisateur user = (Utilisateur) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Récupération de tous les paramètres de filtrage et recherche
        String templateParam = request.getParameter("template");
        String status = request.getParameter("status");
        String query = request.getParameter("q");

        // Valeur par défaut pour le statut si le paramètre est manquant
        if (status == null || status.trim().isEmpty()) {
            status = "tous";
        }

        try {
            List<WorkflowDisplay> workflowsFiltered;

            // 1. STRATÉGIE DE RÉCUPÉRATION DU FLUX PRINCIPAL
            if (templateParam != null && !templateParam.trim().isEmpty()) {
                // Scénario : On est filtré sur un template spécifique depuis le hub
                workflowsFiltered = workflowService.getWorkflowsByTemplateDecorated(templateParam);
                request.setAttribute("selectedTemplate", templateParam);
            } else {
                // Scénario : Accueil classique global (on charge tout, indépendamment du template)
                // Note : getDashboardWorkflows doit idéalement gérer l'état initial sans filtre SQL strict sur le statut 
                // pour laisser le filtrage Java en aval s'en occuper uniformément, ou utiliser "tous" + query.
                workflowsFiltered = workflowService.getDashboardWorkflows("tous", query);
            }

            // 2. FILTRAGE DYNAMIQUE PAR ONGLET (en_cours, termine, annule)
            final String activeStatus = status;
            if (!"tous".equalsIgnoreCase(activeStatus)) {
                workflowsFiltered.removeIf(wd -> {
                    boolean isTermine = wd.getWorkflow().getDateFinalisation() != null;
                    String cleanAvis = (wd.getRawAvis() != null) ? wd.getRawAvis().trim() : "";
                    boolean isAnnule = "Non faisable".equalsIgnoreCase(cleanAvis) || "Défavorable".equalsIgnoreCase(cleanAvis);

                    if ("termine".equalsIgnoreCase(activeStatus)) {
                        return !isTermine || isAnnule;
                    } else if ("annule".equalsIgnoreCase(activeStatus)) {
                        return !isAnnule;
                    } else if ("en_cours".equalsIgnoreCase(activeStatus)) {
                        return isTermine;
                    }
                    return false;
                });
            }

            // 3. FILTRAGE DYNAMIQUE DE LA BARRE DE RECHERCHE `q` (Si pas déjà fait côté base de données)
            if (query != null && !query.trim().isEmpty()) {
                String lowerQuery = query.toLowerCase().trim();
                workflowsFiltered.removeIf(wd -> {
                    String idStr = String.valueOf(wd.getWorkflow().getId());
                    String titre = wd.getWorkflow().getTitre() != null ? wd.getWorkflow().getTitre().toLowerCase() : "";
                    return !idStr.contains(lowerQuery) && !titre.contains(lowerQuery);
                });
            }

            // 4. RÉCUPÉRATION ET FILTRAGE DE LA LISTE "EN ATTENTE D'ACTION"
            List<Workflow> pendingList = WorkflowDAO.getWorkflowsEnAttenteParRole(user.getRole());
            if (pendingList != null && templateParam != null && !templateParam.trim().isEmpty()) {
                // Si on a choisi un template, on filtre aussi les actions urgentes liées à ce template
                pendingList.removeIf(w -> {
                    templateWorkflow t = templateWorkflowDao.getTemplateById(w.getIdTemplateWorkflow());
                    return t == null || !templateParam.equalsIgnoreCase(t.getTitre());
                });
            }

            // Envoi des données à la JSP d'accueil unique
            request.setAttribute("workflows", workflowsFiltered);
            request.setAttribute("pendingList", pendingList);
            request.setAttribute("currentStatus", status);
            request.setAttribute("roleDAO", roleDao); 

            request.getRequestDispatcher("/View/accueil.jsp").forward(request, response);
            return;

        } catch (Exception e) {
            e.printStackTrace();
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors du chargement du tableau de bord.");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}