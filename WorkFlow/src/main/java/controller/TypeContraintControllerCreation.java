package controller;

import java.io.IOException;
import java.util.List;

import dao.TypeContraintDAO;
import model.TypeContraint;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/catalogues")
public class TypeContraintControllerCreation extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // Instanciation du DAO unique pour toute la servlet
    private final TypeContraintDAO typeContraintDAO = new TypeContraintDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // Utilisation du DAO pour récupérer proprement les objets
            List<TypeContraint> listeContraintes = typeContraintDAO.getAll();
            request.setAttribute("listeContraintes", listeContraintes);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur lors du chargement des données via le DAO : " + e.getMessage());
        }

        request.getRequestDispatcher("/View/type_contraint.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");

        try {
            if ("add".equalsIgnoreCase(action)) {
                String type = request.getParameter("type");
                String valeur = request.getParameter("valeur");

                if (type != null && !type.trim().isEmpty() && valeur != null && !valeur.trim().isEmpty()) {
                    // Création de l'objet métier (l'ID sera généré automatiquement par le SERIAL de PostgreSQL)
                    TypeContraint newTc = new TypeContraint(0, type.toLowerCase().trim(), valeur.trim());
                    typeContraintDAO.create(newTc);
                }
            } 
            else if ("update".equalsIgnoreCase(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                String type = request.getParameter("type");
                String valeur = request.getParameter("valeur");

                if (type != null && !type.trim().isEmpty() && valeur != null && !valeur.trim().isEmpty()) {
                    // Instanciation de l'objet à modifier avec son ID existant
                    TypeContraint updatedTc = new TypeContraint(id, type.toLowerCase().trim(), valeur.trim());
                    typeContraintDAO.update(updatedTc);
                }
            } 
            else if ("delete".equalsIgnoreCase(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                typeContraintDAO.delete(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // On peut logger l'exception ici
        }

        // Redirection post-traitement vers l'URL propre
        response.sendRedirect(request.getContextPath() + "/catalogues");
    }
}