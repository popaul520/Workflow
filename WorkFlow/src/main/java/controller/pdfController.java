package controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import dao.DonneeDAO;
import model.Donnee;
import security.PdfDocument;

@WebServlet("/downloadPdf")
public class pdfController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Sécurité : Vérification de la session
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // 2. Récupération du paramètre de l'ID du workflow
        String idWfStr = request.getParameter("id");
        if (idWfStr == null || idWfStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        try {
            int idWf = Integer.parseInt(idWfStr);
            DonneeDAO dao = new DonneeDAO();
            List<Donnee> listeComplete = dao.getAllDonneesByWorkflow(idWf);

            // 3. Génération du tableau d'octets PDF
            PdfDocument pdfGenerator = new PdfDocument();
            byte[] pdfContent = pdfGenerator.creationPdf(idWf, listeComplete);

            // 4. Configuration stricte des en-têtes HTTP pour le téléchargement binaire
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"Recap_Workflow_" + idWf + ".pdf\"");
            response.setContentLength(pdfContent.length);

            // 5. Écriture directe dans le flux de sortie
            try (OutputStream os = response.getOutputStream()) {
                os.write(pdfContent);
                os.flush();
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors de la generation du PDF");
            }
        }
    }
}