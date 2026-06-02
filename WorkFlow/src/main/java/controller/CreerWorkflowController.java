package controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import dao.DonneeDAO;
import dao.ValidationDAO;
import dao.WorkflowDAO;
import model.Utilisateur; // Adapte le package de ton modèle Utilisateur s'il est différent

@WebServlet("/creer-workflow")
public class CreerWorkflowController extends HttpServlet {
    private WorkflowDAO wfDao = new WorkflowDAO();
    private DonneeDAO donneeDao = new DonneeDAO();
    private ValidationDAO valDao = new ValidationDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Chargement des listes déroulantes (référentiels)
        chargerReferentiels(request);
        request.getRequestDispatcher("View/creerWorkflow.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Récupération de l'utilisateur connecté via la session
        HttpSession session = request.getSession();
        Utilisateur user = (Utilisateur) session.getAttribute("user");
        
        if (user == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Vous devez être connecté pour créer un workflow.");
            return;
        }

        // 2. Récupération des informations de base
        String titre = request.getParameter("titre");

        // 3. Création et configuration de l'objet Workflow
        model.Workflow wf = new model.Workflow();
        wf.setTitre(titre);
        wf.setDateCreation(new java.util.Date());

        // =========================================================================
        // AJOUT : Association de l'ID utilisateur connecté au Workflow
        // =========================================================================

        wf.setIdUtilisateur(String.valueOf(user.getId())); 

        // Insertion du Workflow et récupération de son ID de série (SERIAL)
        int idWf = wfDao.create(wf); 

        if (idWf > 0) {
            // 4. INITIALISATION DES ÉTAPES SUIVIES (1 à 10)
            for (int i = 1; i <= 10; i++) {
                donneeDao.creerEtapeWorkflow(idWf, i);
            }

            // 5. ENREGISTREMENT DES DONNÉES TECHNIQUES
            java.util.Enumeration<String> parameterNames = request.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                String paramName = parameterNames.nextElement();
                
                if (paramName.startsWith("attr_") || paramName.startsWith("comm_")) {
                    String valeur = request.getParameter(paramName);
                    
                    if (valeur != null && !valeur.trim().isEmpty()) {
                        model.Donnee d = new model.Donnee();
                        String suffixe = paramName.substring(5);
                        String refContrainte = request.getParameter("ref_" + suffixe);
                        
                        d.setType(suffixe); 
                        d.setRefTypeContraint(refContrainte);
                        
                        if (paramName.startsWith("attr_")) {
                            d.setAttribut(valeur);
                        } else {
                            d.setCommentaire(valeur);
                            d.setAttribut("");
                        }
                        
                        // Enregistrement des attributs initiaux liés à l'étape 1
                        donneeDao.insertDonnee(d, idWf, 1);
                    }
                }
            }

            // ==========================================
            //  INSERTION DE LA VALIDATION POUR L'ÉTAPE 1
            // ==========================================
            try {
                // On utilise 'idWf' (le bon ID généré) et 'user.getId()' (l'ID de session)
                valDao.validerEtape(idWf, user.getId(), 1);
                System.out.println("Validation étape 1 enregistrée avec succès pour le Workflow ID: " + idWf);
            } catch (SQLException e) {
                System.err.println("Erreur lors de l'enregistrement de la validation de l'étape 1.");
                e.printStackTrace();
            }

            // 6. Redirection finale vers l'accueil
            response.sendRedirect(request.getContextPath() + "/home");
            
        } else {
            request.setAttribute("erreur", "Impossible de créer le workflow.");
            chargerReferentiels(request); // Recharger au cas où pour ré-afficher la page
            request.getRequestDispatcher("/View/creerWorkflow.jsp").forward(request, response);
        }
    }

    private void chargerReferentiels(HttpServletRequest request) {
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
        request.setAttribute("optionsCapacitaire", donneeDao.getValeursContraintes("Capacitaire"));
        request.setAttribute("optionsnormalite", donneeDao.getValeursContraintes("normalite"));
        request.setAttribute("optionsfinalite", donneeDao.getValeursContraintes("finalite"));
        request.setAttribute("optionsdifficulte", donneeDao.getValeursContraintes("difficulte"));
        request.setAttribute("optionsSaisonalite", donneeDao.getValeursContraintes("saisonalite"));
    }
}