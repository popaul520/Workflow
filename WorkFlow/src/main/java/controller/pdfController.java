package controller;

import java.io.IOException;


import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

import dao.DonneeDAO;
import model.Donnee;
import security.PdfDocument;

@WebServlet("/downloadPdf")
public class pdfController extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Sécurité : On vérifie que l'utilisateur est connecté
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // 2. Récupération de l'ID du workflow passé en paramètre
        String idWfStr = request.getParameter("id");
        if (idWfStr == null || idWfStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        try {
            int idWf = Integer.parseInt(idWfStr);
            DonneeDAO dao = new DonneeDAO();
            
            // On récupère toutes les données du workflow (Assure-toi d'avoir cette méthode dans ton DAO)
            List<Donnee> listeComplete = dao.getAllDonneesByWorkflow(idWf);

            // 3. Appel de ta classe security.PdfDocument pour générer le byte[]
            PdfDocument pdfGenerator = new PdfDocument();
            byte[] pdfContent = pdfGenerator.creationPdf(idWf, listeComplete);

            // 4. Configuration de la réponse HTTP pour forcer le téléchargement
            response.setContentType("application/pdf");
            // "attachment" force le téléchargement au lieu d'ouvrir dans l'onglet
            response.setHeader("Content-Disposition", "attachment; filename=\"Recap_Workflow_" + idWf + ".pdf\"");
            response.setContentLength(pdfContent.length);

            // 5. Envoi des octets du PDF vers le navigateur
            response.getOutputStream().write(pdfContent);
            response.getOutputStream().flush();

        } catch (Exception e) {
            e.printStackTrace();
            // En cas d'erreur, on renvoie une erreur 500
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors de la génération du PDF");
        }
    }
}