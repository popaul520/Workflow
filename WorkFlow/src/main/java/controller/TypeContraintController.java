package controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import dao.TypeContraintDAO;
import model.TypeContraint;

@WebServlet("/type-contraint")
public class TypeContraintController extends HttpServlet {
    private TypeContraintDAO tcDao = new TypeContraintDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        // Gestion de la suppression rapide en GET
        if ("delete".equals(action)) {
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                tcDao.delete(id);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            response.sendRedirect(request.getContextPath() + "/type-contraint");
            return;
        }

        // Chargement initial et affichage global
        List<TypeContraint> listeContraintes = tcDao.getAll();
        request.setAttribute("listeContraintes", listeContraintes);
        request.getRequestDispatcher("View/typeContraint.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        String idStr = request.getParameter("id_contraint");
        String type = request.getParameter("type_nom");
        String valeur = request.getParameter("valeur_nom");

        if (type != null && valeur != null && !type.trim().isEmpty() && !valeur.trim().isEmpty()) {
            TypeContraint tc = new TypeContraint();
            tc.setType(type.trim());
            tc.setValeur(valeur.trim());

            if (idStr == null || "0".equals(idStr) || idStr.isEmpty()) {
                // Création si ID vaut 0 ou absent
                tcDao.create(tc);
            } else {
                // Modification si ID existant
                tc.setId(Integer.parseInt(idStr));
                tcDao.update(tc);
            }
        }
        
        response.sendRedirect(request.getContextPath() + "/type-contraint");
    }
}