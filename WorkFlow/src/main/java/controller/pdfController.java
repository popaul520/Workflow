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
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String idWfStr = request.getParameter("id");
        if (idWfStr == null || idWfStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        try {
            int idWf = Integer.parseInt(idWfStr);

            DonneeDAO dao = new DonneeDAO();
            List<Donnee> listeComplete = dao.getAllDonneesByWorkflow(idWf);

            PdfDocument pdfGenerator = new PdfDocument();
            byte[] pdfContent = pdfGenerator.creationPdf(idWf, listeComplete);

            // Vérification utile
            System.out.println("PDF taille = " + pdfContent.length);

            if (pdfContent == null || pdfContent.length == 0) {
                response.sendError(HttpServletResponse.SC_NO_CONTENT, "PDF vide");
                return;
            }

            // Headers HTTP
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", 
                "attachment; filename=\"Recap_Workflow_" + idWf + ".pdf\"");
            response.setContentLength(pdfContent.length);

            ServletOutputStream out = response.getOutputStream();
            out.write(pdfContent);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Erreur lors de la génération du PDF");
        }
    }
}