package controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import dao.DonneeDAO;
import dao.ValidationDAO;
import dao.WorkflowDAO;
import model.Donnee;
import model.Workflow;

@WebServlet(urlPatterns = {"/creer-workflowV1" })
public class WorkflowController extends HttpServlet {
    private WorkflowDAO dao = new WorkflowDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String path = request.getServletPath();


        if ("/details".equals(path)) {
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                
                //  Récupérer les infos du Workflow
                Workflow wf = WorkflowDAO.getById(id);
                if (wf != null) {
                    //  Récupérer l'historique des données
                    DonneeDAO donneeDAO = new DonneeDAO();
                    List<Donnee> listeDonnees = donneeDAO.getDonneesByWorkflow(id); 

                    request.setAttribute("wf", wf);
                    request.setAttribute("historique", listeDonnees);
                    request.getRequestDispatcher("/View/details.jsp").forward(request, response);
                } else {
                    response.sendRedirect(request.getContextPath() + "/listeWorkflows");
                }
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
            }
        }
        else if (path.equals("/login")) {
            request.getRequestDispatcher("View/login.jsp").forward(request, response);
        }
        
        else if (path.equals("/register")) {
            request.getRequestDispatcher("View/register.jsp").forward(request, response);
        }
        
        else  {
    	    chargerReferentiels(request);

            request.getRequestDispatcher("View/creerWorkflow.jsp").forward(request, response);
        } 
    }

protected void doPost(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
    
    // 1. Récupération des informations de base
    String titre = request.getParameter("titre");
    // On récupère le client pour l'objet Workflow 
    String clientPrincipal = request.getParameter("attr_client"); 

    // 2. Création de l'objet Workflow
    model.Workflow wf = new model.Workflow();
    wf.setTitre(titre);
    //wf.setClient(clientPrincipal); 
    wf.setDateCreation(new java.util.Date());
    
    dao.WorkflowDAO wfDao = new dao.WorkflowDAO();
    // La méthode create retourne l'ID
    int idWf = wfDao.create(wf); 

    if (idWf > 0) {
        dao.DonneeDAO donneeDao = new dao.DonneeDAO();

        // 3. INITIALISATION ETAPE
        for (int i = 1; i <= 10; i++) {
            donneeDao.creerEtapeWorkflow(idWf, i);
        }

        // 4. ENREGISTREMENT DES DONNÉES TECHNIQUES
        java.util.Enumeration<String> parameterNames = request.getParameterNames();
        
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            
            // On traite uniquement les champs qui commencent par "attr_" ou "comm_"
            if (paramName.startsWith("attr_") || paramName.startsWith("comm_")) {
                String valeur = request.getParameter(paramName);
                
                // On n'enregistre que si la valeur n'est pas vide
                if (valeur != null && !valeur.trim().isEmpty()) {
                    model.Donnee d = new model.Donnee();
                    
                    // Déterminer le suffixe (ex: 'client' pour 'attr_client')
                    String suffixe = paramName.substring(5);
                    
                    // RÉCUPÉRATION DU RÉFÉRENTIEL (le fameux champ caché ref_...)
                    // Si on traite attr_client, on cherche le paramètre ref_client
                    String refContrainte = request.getParameter("ref_" + suffixe);
                    
                    // Remplissage de l'objet Donnee
                    d.setType(suffixe); 
                    d.setRefTypeContraint(refContrainte); // Stocke "client", "Bool", "rayon"...
                    
                    if (paramName.startsWith("attr_")) {
                        d.setAttribut(valeur);
                    } else {
                        d.setCommentaire(valeur);
                        d.setAttribut("");
                    }
                    
                    // 5. INSERTION DANS LA TABLE 'donnee'
                    // On force l'étape à 1 car on est à la création
                    donneeDao.insertDonnee(d, idWf, 1);
                    
                    // Debug console pour vérifier que refContrainte n'est pas null
                    System.out.println("Enregistrement : " + paramName + " | Valeur: " + valeur + " | Ref: " + refContrainte);
                }
            }
        }
        ValidationDAO valDao = new ValidationDAO();
        
		try {
			valDao.validerEtape(wf.getId(), wf.getCreateur().getId(), 1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        // 6. Redirection finale vers l'accueil ou le détail
        response.sendRedirect(request.getContextPath() + "/home");
    } else {
        // Gestion d'erreur si le workflow n'a pas pu être créé
        request.setAttribute("erreur", "Impossible de créer le workflow.");
        request.getRequestDispatcher("/View/creer-workflow.jsp").forward(request, response);
    }
}

	private void chargerReferentiels(HttpServletRequest request) {
	    dao.DonneeDAO donneeDao = new dao.DonneeDAO();
	    
	    // On charge tous les types dont on a besoin pour les 10 étapes
	    request.setAttribute("optionsBool", donneeDao.getValeursContraintes("Bool"));
	    request.setAttribute("optionsAvis", donneeDao.getValeursContraintes("avis"));
	    request.setAttribute("optionsFlux", donneeDao.getValeursContraintes("flux/stock"));
	    request.setAttribute("optionsReponse", donneeDao.getValeursContraintes("reponse"));
	    request.setAttribute("optionsMarque", donneeDao.getValeursContraintes("Marque"));
	    request.setAttribute("optionsClient", donneeDao.getValeursContraintes("client"));
	    request.setAttribute("optionsFamille", donneeDao.getValeursContraintes("famille"));
	    request.setAttribute("optionsRayon", donneeDao.getValeursContraintes("rayon"));
	    request.setAttribute("optionsUO", donneeDao.getValeursContraintes("unite oeuvre"));
	    request.setAttribute("optionsRoutage", donneeDao.getValeursContraintes("Routage machine"));
	    request.setAttribute("optionsreponse", donneeDao.getValeursContraintes("reponse"));
	    request.setAttribute("optionsCapacitaire", donneeDao.getValeursContraintes("Capacitaire"));
	    request.setAttribute("optionsnormalite", donneeDao.getValeursContraintes("normalite"));
	    request.setAttribute("optionsfinalite", donneeDao.getValeursContraintes("finalite"));
	    request.setAttribute("optionsdifficulte", donneeDao.getValeursContraintes("difficulte"));

	}
}

