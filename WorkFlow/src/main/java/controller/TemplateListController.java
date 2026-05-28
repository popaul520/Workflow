package controller;

import java.io.IOException;

import dao.TemplateDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/template-list")
public class TemplateListController extends HttpServlet {
    private TemplateDAO dao = new TemplateDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        String idStr = request.getParameter("id");

        // --- GESTION DE LA SUPPRESSION ---
        if ("delete".equals(action) && idStr != null) {
            try {
                int id = Integer.parseInt(idStr);
                dao.deleteTemplate(id);
                // On redirige l'URL après suppression
                response.sendRedirect("template-list");
                return;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        // --- AFFICHAGE DE LA LISTE ---
        request.setAttribute("templates", dao.getAllTemplates());
        request.getRequestDispatcher("/View/templateList.jsp").forward(request, response);
    }
}
