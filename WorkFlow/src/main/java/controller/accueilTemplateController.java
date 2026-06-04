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

@WebServlet("/homeport")
public class accueilTemplateController extends HttpServlet {
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

        // Récupération de toutes les spécifications (Filtres + Recherche + Template)
        String templateParam = request.getParameter("template");
        String status = request.getParameter("status");
        String query = request.getParameter("q");

        // Spécification par défaut de l'accueil : Statut "tous" si vide
        if (status == null || status.trim().isEmpty()) {
            status = "tous";
        }

        try {
            // SCÉNARIO A : Aucun template spécifié -> Affichage de la mosaïque (Hub)
            if (templateParam == null || templateParam.trim().isEmpty()) {
                List<templateWorkflow> templates = templateWorkflowDao.getTemplatesActifs();
                request.setAttribute("templates", templates);
                request.getRequestDispatcher("/View/mosaiqueTemplate.jsp").forward(request, response);
                return;
            }

            // SCÉNARIO B : Un template est choisi -> Comportement identique à l'accueil global, mais cloisonné au template :)
            
            // 1. Récupération des workflows du template (on applique ici aussi la logique de décoration)
            List<WorkflowDisplay> workflowsFiltered = workflowService.getWorkflowsByTemplateDecorated(templateParam);

            // [Spécification HOME] : Filtrage dynamique de la liste selon le statut de l'onglet actif (en_cours, termine, annule)
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

            // [Spécification HOME] : Filtrage dynamique selon la barre de recherche textuelle `q` (ID ou Titre)
            if (query != null && !query.trim().isEmpty()) {
                String lowerQuery = query.toLowerCase().trim();
                workflowsFiltered.removeIf(wd -> {
                    String idStr = String.valueOf(wd.getWorkflow().getId());
                    String titre = wd.getWorkflow().getTitre() != null ? wd.getWorkflow().getTitre().toLowerCase() : "";
                    return !idStr.contains(lowerQuery) && !titre.contains(lowerQuery);
                });
            }

            // [Spécification HOME] : Chargement et filtrage de la "pendingList" (Actions urgentes) pour ce template
            List<Workflow> pendingList = WorkflowDAO.getWorkflowsEnAttenteParRole(user.getRole());
            if (pendingList != null) {
                pendingList.removeIf(w -> {
                    templateWorkflow t = templateWorkflowDao.getTemplateById(w.getIdTemplateWorkflow());
                    return t == null || !templateParam.equalsIgnoreCase(t.getTitre());
                });
            }

            // Envoi des données à la JSP d'accueil (partagée)
            request.setAttribute("selectedTemplate", templateParam);
            request.setAttribute("workflows", workflowsFiltered);
            request.setAttribute("pendingList", pendingList);
            request.setAttribute("currentStatus", status);
            request.setAttribute("roleDAO", roleDao); 

            request.getRequestDispatcher("/View/accueil.jsp").forward(request, response);
            return;

        } catch (Exception e) {
            e.printStackTrace();
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors du chargement du tableau de bord spécifié.");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}