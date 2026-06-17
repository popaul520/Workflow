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
import dao.TemplateEtapeDAO;
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
        // ================= REQUISITION DE SÉCURITÉ : FILTRAGE PAR RÔLE =================
        // Récupération des IDs de templates autorisés pour le rôle de l'utilisateur connecté

		List<Integer> templatesAutorisesIds = TemplateEtapeDAO.templateWorkflowByRole(user.getRole());
        String templateParam = request.getParameter("template");
        String status = request.getParameter("status");
        String query = request.getParameter("q");
        if (status == null || status.trim().isEmpty()) {
            status = "tous";
        }
        try {
            // SCÉNARIO A : Aucun template spécifié -> Affichage de la mosaïque (Hub) filtrée
            if (templateParam == null || templateParam.trim().isEmpty()) {
                List<templateWorkflow> templates = templateWorkflowDao.getTemplatesActifs();
                
                // Sécurité : On retire de la mosaïque les templates pour lesquels le rôle n'est pas configuré
                if (templates != null) {
                    templates.removeIf(t -> !templatesAutorisesIds.contains(t.getId()));
                }
                
                request.setAttribute("templates", templates);
                request.getRequestDispatcher("/View/mosaiqueTemplate.jsp").forward(request, response);
                return;
            }
            
            /*
            // SCÉNARIO B : Un template est choisi -> Vérification du cloisonnement de sécurité par rôle
            templateWorkflow templateActuel = templateWorkflowDao.getTemplateByTitre(templateParam.trim()); 
            
            // Si le template demandé n'existe pas ou si l'ID du template n'est pas dans la liste des autorisations du rôle
            if (templateActuel == null || !templatesAutorisesIds.contains(templateActuel.getId())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès refusé : Votre rôle ne vous permet pas d'accéder à ce template de workflow.");
                return;
            }
            */

            // 1. Récupération des workflows du template (décorés)
            List<WorkflowDisplay> workflowsFiltered = workflowService.getWorkflowsByTemplateDecorated(templateParam);
            // Filtrage dynamique de la liste selon le statut de l'onglet actif (en_cours, termine, annule)
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
            // Filtrage dynamique selon la barre de recherche textuelle `q` (ID ou Titre)
            if (query != null && !query.trim().isEmpty()) {
                String lowerQuery = query.toLowerCase().trim();
                workflowsFiltered.removeIf(wd -> {
                    String idStr = String.valueOf(wd.getWorkflow().getId());
                    String titre = wd.getWorkflow().getTitre() != null ? wd.getWorkflow().getTitre().toLowerCase() : "";
                    return !idStr.contains(lowerQuery) && !titre.contains(lowerQuery);
                });
            }
            // Chargement et filtrage de la "pendingList" (Actions urgentes) pour ce template
            List<Workflow> pendingList = WorkflowDAO.getWorkflowsEnAttenteParRole(user.getRole());
            if (pendingList != null) {
                pendingList.removeIf(w -> {
                    templateWorkflow t = templateWorkflowDao.getTemplateById(w.getIdTemplateWorkflow());
                    return t == null || !templateParam.equalsIgnoreCase(t.getTitre());
                });
            }
            // Envoi des données à la JSP d'accueil
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
        
        // 1. Récupération des paramètres du formulaire
        int idWorkflow = Integer.parseInt(request.getParameter("id_workflow"));
        int currentEtape = Integer.parseInt(request.getParameter("current_n"));
        
        // Traitement et enregistrement des champs dynamiques (attr_0, attr_1...)
        //    et insertion de la ligne dans la table 'validation'.
        
        // 3. Extraction et détection d'un avis bloquant/négatif dans les paramètres soumis
        boolean estAnnule = false;
        java.util.Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            if (paramName.startsWith("attr_")) {
                String val = request.getParameter(paramName);
                if ("Non faisable".equalsIgnoreCase(val) || "Défavorable".equalsIgnoreCase(val)) {
                    estAnnule = true;
                    break;
                }
            }
        }

        // 4. Vérification du cycle de vie du workflow
        int derniereEtape = dao.TemplateEtapeDAO.getNumeroDerniereEtape(idWorkflow);

        if (estAnnule) {
            // Option A : Décision négative -> Clôture immédiate (Le workflow passe en "annulé")
            dao.TemplateDAO.finaliserWorkflow(idWorkflow);
            response.sendRedirect(request.getContextPath() + "/homeport?status=annule&template=" + request.getParameter("template"));
            return;
            
        } else if (currentEtape >= derniereEtape) {
            // Option B : Dernière étape atteinte -> Clôture finale (Le workflow passe en "terminé")
            dao.TemplateDAO.finaliserWorkflow(idWorkflow);
            response.sendRedirect(request.getContextPath() + "/homeport?status=termine&template=" + request.getParameter("template"));
            return;
            
        } else {
            // Option C : Étape intermédiaire franchie -> Le workflow reste "en cours"
            response.sendRedirect(request.getContextPath() + "/homeport?status=en_cours&template=" + request.getParameter("template"));
            return;
        }
    }
}