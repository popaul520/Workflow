package controller;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

import dao.DonneeDAO;
import dao.ValidationDAO; 
import dao.WorkflowDAO;    
import dao.TemplateEtapeDAO; // Importation du DAO pour l'étape finale
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
    private static final long serialVersionUID = 1L;

    private void chargerReferentiels(HttpServletRequest request) {
        dao.DonneeDAO donneeDao = new dao.DonneeDAO();
        
        // --- TYPES STANDARDS (Blocs 1 à 6) ---
        request.setAttribute("optionsBool",       donneeDao.getValeursContraintes("Bool"));
        request.setAttribute("optionsClient",     donneeDao.getValeursContraintes("client"));
        request.setAttribute("optionsMarque",     donneeDao.getValeursContraintes("Marque"));
        request.setAttribute("optionsReponse",    donneeDao.getValeursContraintes("reponse"));
        request.setAttribute("optionsRayon",      donneeDao.getValeursContraintes("rayon"));
        request.setAttribute("optionsFamille",    donneeDao.getValeursContraintes("famille"));
        
        // --- TYPES TECHNIQUES (Logistique / DOP / Méthodes) ---
        request.setAttribute("optionsUO",         donneeDao.getValeursContraintes("unite oeuvre"));
        request.setAttribute("optionsRoutage",    donneeDao.getValeursContraintes("Routage machine"));
        request.setAttribute("optionsCapacitaire", donneeDao.getValeursContraintes("Capacitaire"));
        request.setAttribute("optionsFlux",       donneeDao.getValeursContraintes("flux/stock"));
        // --- TYPES DE VALIDATION (QHE / CDG / Final) ---
        request.setAttribute("optionsAvis",       donneeDao.getValeursContraintes("avis"));
        request.setAttribute("optionsnormalite",  donneeDao.getValeursContraintes("normalite"));
        request.setAttribute("optionsFinalite",   donneeDao.getValeursContraintes("finalite"));
        request.setAttribute("optionsDifficulte", donneeDao.getValeursContraintes("difficulte"));
        request.setAttribute("optionsSaisonalite", donneeDao.getValeursContraintes("saisonalite"));
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        //1. récupération paramètre
        String idWfStr = request.getParameter("id_workflow");
        String nStr = request.getParameter("n");

        if (idWfStr == null || nStr == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        int idWf = Integer.parseInt(idWfStr);
        int n = Integer.parseInt(nStr);

        // 2. Initialisation des DAOs
        dao.WorkflowDAO wfDao = new dao.WorkflowDAO();
        dao.DonneeDAO donneeDao = new dao.DonneeDAO();
        dao.RoleDAO roleDao = new dao.RoleDAO(); 
        dao.ValidationDAO valDao = new dao.ValidationDAO();

        // 3. Récupération des objets métier
        model.Workflow wf = wfDao.getById(idWf);
        HttpSession session = request.getSession();
        model.Utilisateur user = (model.Utilisateur) session.getAttribute("user");
        
        boolean isAdmin = (user != null && user.getRole() == 11 || roleDao.canAccessEtape(user.getRole(), 11));   // 11 = ADMIN
        boolean hasAccess = false;
        if (user != null) {
            hasAccess = roleDao.canAccessEtape(user.getRole(), n) || user.getRole() == n;
        }

        // 4. Chargement pour les listes optionsAvis, optionsBool, etc.
        chargerReferentiels(request);

        // 5. Calcul de l'état du workflow et des droits
        int etapeMax = 0;
        try {
            etapeMax = valDao.getDerniereEtapeValidee(idWf);
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean isClosed = (wf != null && wf.getDateFinalisation() != null);
        boolean canEdit = isAdmin || (hasAccess && !isClosed);

        // 6. On force la création de l'étape si elle n'existe pas
        donneeDao.creerEtapeWorkflow(idWf, n);
        
        // 7. Récupération des données après création potentielle
        List<model.Donnee> donneesEtape = donneeDao.getDonneesByEtape(idWf, n);

        // 8. Envoi des attributs à la JSP
        request.setAttribute("wf", wf);
        request.setAttribute("numEtape", n);
        request.setAttribute("id_workflow", idWf);
        request.setAttribute("donneesEtape", donneesEtape);
        request.setAttribute("derniereEtape", etapeMax);
        request.setAttribute("isAdmin", isAdmin);
        request.setAttribute("hasAccess", hasAccess);
        request.setAttribute("canEdit", canEdit);
        request.setAttribute("isClosed", isClosed);

        // 9. LOGIQUE DE ROUTAGE :
        if ( (donneesEtape != null && !donneesEtape.isEmpty()) || isClosed || !canEdit ) {
            request.getRequestDispatcher("/View/etape/visualisationDonnee.jsp").forward(request, response);
        } else {
            String[] suffixes = {"", "_entre", "_condi", "_appro", "_supply", "_logistique", "_qhe", "_DOP", "_methodes", "_cdg", "_final"};
            String suffixe = (n >= 1 && n <= suffixes.length) ? suffixes[n] : "";
            request.getRequestDispatcher("/View/etape/Etape" + n + suffixe + ".jsp").forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Récupération des IDs de base
    	String idWorkStr = request.getParameter("id_workflow");
    	String nbEtapeStr = request.getParameter("num_etape"); 
    	
    	System.out.println("ducoup pas la");
    	if (idWorkStr == null || nbEtapeStr == null) {
    	    response.sendRedirect(request.getContextPath() + "/homeport"); 
    	    return;
    	}

        int idWorkflow = Integer.parseInt(idWorkStr);
        int nbEtape = Integer.parseInt(nbEtapeStr);
        
        dao.DonneeDAO donneeDao = new dao.DonneeDAO();
        dao.ValidationDAO valDao = new dao.ValidationDAO();
        dao.WorkflowDAO wfDao = new dao.WorkflowDAO();
        
        HttpSession session = request.getSession();
        Utilisateur user = (Utilisateur) session.getAttribute("user");

        // 2. NETTOYAGE : Sécurité pour éviter les doublons
        donneeDao.deleteDonneesByEtape(idWorkflow, nbEtape);

        // Variable pour intercepter l'avis soumis (peu importe son nom ou index)
        String avisSelectionne = null;

        // 3. BOUCLE DYNAMIQUE sur les paramètres d'envoi
        java.util.Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String pName = parameterNames.nextElement();
            
            // Extraction et détection de l'avis global (soit attr_avis, soit attr_0, attr_1 lié à un composant d'avis)
            if (pName.startsWith("attr_")) {
                String potentialAvis = request.getParameter(pName);
                if ("Favorable".equalsIgnoreCase(potentialAvis) || 
                    "Faisable sous condition".equalsIgnoreCase(potentialAvis) || 
                    "Non faisable".equalsIgnoreCase(potentialAvis)) {
                    avisSelectionne = potentialAvis;
                }
            }
            System.out.println(avisSelectionne);
            
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
                    
                    if (valDate != null && !valDate.isEmpty()) {
                        try {
                            d.setDate(java.sql.Date.valueOf(valDate));
                        } catch (Exception e) {
                            System.err.println("Erreur format date pour : " + suffixe);
                        }
                    }
                    donneeDao.insertDonnee(d, idWorkflow, nbEtape);
                }
            }
            System.out.println("On arrive validation");
        }

        // 4. VALIDATION ET CLÔTURE DYNAMIQUE PAR TEMPLATE
        try {
            // Enregistrement de la validation de l'étape courante
            valDao.validerEtape(idWorkflow, user.getId(), nbEtape);

            // Détermination dynamique de l'étape finale configurée pour ce template de workflow
            int derniereEtapeTemplate = dao.TemplateEtapeDAO.getNumeroDerniereEtape(idWorkflow);

            boolean isDefavorable = "Non faisable".equalsIgnoreCase(avisSelectionne) 
                                 || "Défavorable".equalsIgnoreCase(avisSelectionne);

            // RÈGLE DE CLÔTURE :
            // - Soit l'avis est négatif ("Non faisable") -> Clôture immédiate (Annulation du dossier)
            // - Soit nous venons de valider la dernière étape théorique -> Clôture définitive (Succès)
            if (isDefavorable || nbEtape >= derniereEtapeTemplate) {
            	System.out.println("gg cela fonctionne");
                wfDao.finaliserWorkflow(idWorkflow);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        response.sendRedirect(request.getContextPath() + "/details?id=" + idWorkflow);
    }
}