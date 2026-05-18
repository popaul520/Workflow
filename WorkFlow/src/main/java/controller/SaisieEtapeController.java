package controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import dao.TemplateDAO;
import dao.WorkflowDAO;
import dao.ValidationDAO;
import dao.DonneeDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Workflow;
import model.template_etape;
import model.Donnee;

@SuppressWarnings("serial")
@WebServlet("/saisie-etape")
public class SaisieEtapeController extends HttpServlet {
    private WorkflowDAO workflowDao = new WorkflowDAO();
    private TemplateDAO templateDao = new TemplateDAO();
    private ValidationDAO validationDao = new ValidationDAO();
    private DonneeDAO donneeDao = new DonneeDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // 1. Récupération et vérification des paramètres essentiels
            if (request.getParameter("id_workflow") == null) {
                response.sendRedirect("home?error=missingid");
                return;
            }
            
            int idWf = Integer.parseInt(request.getParameter("id_workflow"));
            
            // Si aucun numéro d'étape n'est fourni, on cible par défaut la première étape
            String numEtapeStr = request.getParameter("num_etape");
            int numEtape = (numEtapeStr != null) ? Integer.parseInt(numEtapeStr) : 1;

            // 2. Chargement du Workflow réel
            Workflow wf = WorkflowDAO.getById(idWf);
            if (wf == null) {
                response.sendRedirect("home?error=workflownotfound");
                return;
            }

            // 3. Collecte des informations du Template pour la grille supérieure
            List<template_etape> etapesTemplate = templateDao.getEtapesByTemplate(wf.getIdTemplateWorkflow());
            List<Integer> etapesValidees = validationDao.getListeEtapesValidees(idWf);

            // 4. Chargement de la configuration de l'étape demandée
            template_etape currentEtape = templateDao.getEtapeConfig(wf.getIdTemplateWorkflow(), numEtape);

            // 5. Extraction des lignes de données spécifiques déjà stockées pour cette étape
            List<Donnee> donneesEtape = donneeDao.getDonneesByEtape(idWf, numEtape);

            // 6. Calcul de l'état du dossier et des droits d'accès/modification
            boolean isClosed = (wf.getDateFinalisation() != null);
            
            // Règle de modification : possible si le dossier n'est pas clôturé ET (soit c'est l'étape 1, soit l'étape précédente est déjà validée)
            boolean canEdit = !isClosed && (numEtape == 1 || etapesValidees.contains(numEtape - 1));

            // 7. Options prédéfinies injectées pour alimenter le select HTML des contraintes de type "avis"
            List<String> optionsAvis = Arrays.asList("Faisable", "Non faisable", "À l'étude", "Sous réserve");

            // 8. Injection de tous les attributs requis par la JSP épurée
            request.setAttribute("workflow", wf);
            request.setAttribute("etapesTemplate", etapesTemplate);
            request.setAttribute("etapesValidees", etapesValidees);
            request.setAttribute("numEtapeActive", numEtape);
            request.setAttribute("currentEtape", currentEtape);
            request.setAttribute("donneesEtape", donneesEtape);
            request.setAttribute("isClosed", isClosed);
            request.setAttribute("canEdit", canEdit);
            request.setAttribute("optionsAvis", optionsAvis);

            // 9. Redirection vers la vue unique de travail
            request.getRequestDispatcher("/View/saisieEtapeDynamique.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Format des paramètres d'appel incorrect.");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors du chargement des composants de l'étape.");
        }
    }
}