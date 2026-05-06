package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Enumeration;

import dao.ValidationDAO;
import dao.WorkflowDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Etape;
import model.Utilisateur;
import model.Workflow;

@WebServlet("/etape") // Attention au nom, assure-toi qu'il matche tes appels Fetch/Form
public class etapeController extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Ta logique actuelle pour charger le fragment d'étape...
        // (Généralement tu récupères n et id_workflow ici pour charger les données)
        
        request.getRequestDispatcher("View/etape.jsp").forward(request, response);
    }
/*
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        String idWorkStr = request.getParameter("id_workflow");
        String nbEtapeStr = request.getParameter("current_n");

        int idWorkflow = Integer.parseInt(idWorkStr);
        int nbEtape = Integer.parseInt(nbEtapeStr);
        
        dao.DonneeDAO donneeDao = new dao.DonneeDAO();
        dao.ValidationDAO valDao = new dao.ValidationDAO(); // Nouveau
        dao.WorkflowDAO wfDao = new dao.WorkflowDAO();     // Nouveau

        HttpSession session = request.getSession();
        Utilisateur user = (Utilisateur) session.getAttribute("user");

        // --- 1. SAUVEGARDE DES DONNÉES (Ton code existant) ---
        boolean isUpdateMode = request.getParameter("idDonne_0") != null;
        if (!isUpdateMode) {
            donneeDao.deleteDonneesByEtape(idWorkflow, nbEtape);
        }

        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String pName = params.nextElement();
            if (pName.startsWith("type_")) {
                String suffixe = pName.substring(5); 
                // ... (ton code de récupération valType, valAttr, etc.)
                // ... (ton code de d.insertDonnee ou updateDonnee)
            }
        }

        // --- 2. LOGIQUE DE VALIDATION ET PROGRESSION (Nouveau) ---
        try {
            // On enregistre qui a validé cette étape et quand
            valDao.validerEtape(idWorkflow, user.getId(), nbEtape);

            // Cas particuliers pour la fin du workflow
            if (nbEtape == 7) {
                String decision = request.getParameter("attr_decision_7"); // Un champ spécifique à l'étape 7
                if ("OUI_FIN".equals(decision)) {
                    wfDao.finaliserWorkflow(idWorkflow);
                }
            } else if (nbEtape == 10) {
                wfDao.finaliserWorkflow(idWorkflow);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.sendRedirect(request.getContextPath() + "/details?id=" + idWorkflow);
    }*/
}