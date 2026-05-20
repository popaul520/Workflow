package controller;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

import dao.DonneeDAO;
import dao.ValidationDAO;
import dao.WorkflowDAO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Donnee;
import model.Utilisateur;

@WebServlet("/etapeController")
public class EtapeControllerglobal extends HttpServlet {

    private void chargerReferentiels(HttpServletRequest request) {
        dao.DonneeDAO donneeDao = new dao.DonneeDAO();
        
        request.setAttribute("optionsBool",       donneeDao.getValeursContraintes("Bool"));
        request.setAttribute("optionsClient",     donneeDao.getValeursContraintes("client"));
        request.setAttribute("optionsMarque",     donneeDao.getValeursContraintes("Marque"));
        request.setAttribute("optionsReponse",    donneeDao.getValeursContraintes("reponse"));
        request.setAttribute("optionsRayon",      donneeDao.getValeursContraintes("rayon"));
        request.setAttribute("optionsFamille",    donneeDao.getValeursContraintes("famille"));
        request.setAttribute("optionsUO",         donneeDao.getValeursContraintes("unite oeuvre"));
        request.setAttribute("optionsRoutage",    donneeDao.getValeursContraintes("Routage machine"));
        request.setAttribute("optionsCapacitaire", donneeDao.getValeursContraintes("Capacitaire"));
        request.setAttribute("optionsFlux",       donneeDao.getValeursContraintes("flux/stock"));
        request.setAttribute("optionsAvis",       donneeDao.getValeursContraintes("avis"));
        request.setAttribute("optionsnormalite",  donneeDao.getValeursContraintes("normalite"));
        request.setAttribute("optionsFinalite",   donneeDao.getValeursContraintes("finalite"));
        request.setAttribute("optionsDifficulte", donneeDao.getValeursContraintes("difficulte"));
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String idWfStr = request.getParameter("id_workflow");
        String nStr = request.getParameter("n");

        if (idWfStr == null || nStr == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        int idWf = Integer.parseInt(idWfStr);
        int n = Integer.parseInt(nStr);

        dao.WorkflowDAO wfDao = new dao.WorkflowDAO();
        dao.DonneeDAO donneeDao = new dao.DonneeDAO();
        dao.RoleDAO roleDao = new dao.RoleDAO();
        dao.ValidationDAO valDao = new dao.ValidationDAO();

        model.Workflow wf = wfDao.getById(idWf);
        HttpSession session = request.getSession();
        model.Utilisateur user = (model.Utilisateur) session.getAttribute("user");
        
        boolean isAdmin = (user != null && user.getRole() == 11 || roleDao.canAccessEtape(user.getRole(), 11));
        boolean hasAccess = false;
        if (user != null) {
            hasAccess = roleDao.canAccessEtape(user.getRole(), n) || user.getRole() == n;
        }

        chargerReferentiels(request);

        int etapeMax = 0;
        try {
            etapeMax = valDao.getDerniereEtapeValidee(idWf);
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean isClosed = (wf != null && wf.getDateFinalisation() != null);
        boolean canEdit = isAdmin || (hasAccess && !isClosed);

        donneeDao.creerEtapeWorkflow(idWf, n);
        List<model.Donnee> donneesEtape = donneeDao.getDonneesByEtape(idWf, n);

        request.setAttribute("wf", wf);
        request.setAttribute("numEtape", n);
        request.setAttribute("id_workflow", idWf);
        request.setAttribute("donneesEtape", donneesEtape);
        request.setAttribute("derniereEtape", etapeMax);
        request.setAttribute("isAdmin", isAdmin);
        request.setAttribute("hasAccess", hasAccess);
        request.setAttribute("canEdit", canEdit);
        request.setAttribute("isClosed", isClosed);

        if ((donneesEtape != null && !donneesEtape.isEmpty()) || isClosed || !canEdit) {
            request.getRequestDispatcher("/View/etape/visualisationDonnee.jsp").forward(request, response);
        } else {
            String[] suffixes = {"", "_entre", "_condi", "_appro", "_supply", "_logistique", "_qhe", "_DOP", "_methodes", "_cdg", "_final"};
            String suffixe = (n >= 1 && n <= suffixes.length) ? suffixes[n] : "";
            request.getRequestDispatcher("/View/etape/Etape" + n + suffixe + ".jsp").forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
    	
        String idWorkStr = request.getParameter("id_workflow");
        String nbEtapeStr = request.getParameter("current_n");

        if (idWorkStr == null || nbEtapeStr == null) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        int idWorkflow = Integer.parseInt(idWorkStr);
        int nbEtape = Integer.parseInt(nbEtapeStr);
        
        dao.DonneeDAO donneeDao = new dao.DonneeDAO();
        dao.ValidationDAO valDao = new dao.ValidationDAO();
        dao.WorkflowDAO wfDao = new dao.WorkflowDAO();
        
        HttpSession session = request.getSession();
        Utilisateur user = (Utilisateur) session.getAttribute("user");

        // Nettoyage sécurisé anti-doublons
        donneeDao.deleteDonneesByEtape(idWorkflow, nbEtape);

        java.util.Enumeration<String> parameterNames = request.getParameterNames();
        
        while (parameterNames.hasMoreElements()) {
            String pName = parameterNames.nextElement();
            
            if (pName.startsWith("type_")) {
                String suffixe = pName.substring(5); 

                String valType = request.getParameter("type_" + suffixe);
                String valRef  = request.getParameter("ref_" + suffixe);
                String valAttr = request.getParameter("attr_" + suffixe);
                String valComm = request.getParameter("comm_" + suffixe);
                String valDate = request.getParameter("date_" + suffixe);

                if (valAttr != null && !valAttr.trim().isEmpty()) {
                    model.Donnee d = new model.Donnee();
                    d.setType(valType);
                    d.setRefTypeContraint(valRef);
                    d.setAttribut(valAttr.trim());
                    d.setCommentaire(valComm);
                    
                    // =========================================================================
                    // ATTRIBUTION DE LA DATE ACTUELLE POUR LES ÉTAPES 7 ET 10
                    // =========================================================================
                    if ((nbEtape == 7 || nbEtape == 10) && "avis".equals(suffixe)) {
                        // Injection forcée de la date système actuelle (SQL Date)
                        d.setDate(new java.sql.Date(System.currentTimeMillis()));
                    } else if (valDate != null && !valDate.isEmpty()) {
                        // Traitement classique si une date provient d'un formulaire
                        try {
                            d.setDate(java.sql.Date.valueOf(valDate));
                        } catch (Exception e) {
                            System.err.println("Erreur format date pour : " + suffixe);
                        }
                    }

                    // Insertion en base de données
                    donneeDao.insertDonnee(d, idWorkflow, nbEtape);
                }
            }
        }

        // Validation fonctionnelle de l'étape et cloture
        try {
            valDao.validerEtape(idWorkflow, user.getId(), nbEtape);

            String avisFinal = request.getParameter("attr_avis");
            if (avisFinal != null) {
                boolean isDefavorable = avisFinal.equalsIgnoreCase("Non faisable") 
                                     || avisFinal.equalsIgnoreCase("Défavorable");

                if (nbEtape == 10 || (nbEtape == 7 && isDefavorable)) {
                    wfDao.finaliserWorkflow(idWorkflow);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        response.sendRedirect(request.getContextPath() + "/details?id=" + idWorkflow);
    }
}