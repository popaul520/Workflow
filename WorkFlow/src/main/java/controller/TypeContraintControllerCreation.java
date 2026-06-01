package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/catalogues")
public class TypeContraintControllerCreation extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        List<Map<String, Object>> listeContraintes = new ArrayList<>();
        String sql = "SELECT id, type, valeur FROM public.type_contraint ORDER BY type ASC, id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", rs.getInt("id"));
                item.put("type", rs.getString("type"));
                item.put("valeur", rs.getString("valeur"));
                listeContraintes.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur lors du chargement des données : " + e.getMessage());
        }

        request.setAttribute("listeContraintes", listeContraintes);
        request.getRequestDispatcher("/View/type_contraint.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");

        try (Connection conn = DBConnection.getConnection()) {
            if ("add".equalsIgnoreCase(action)) {
                String type = request.getParameter("type");
                String valeur = request.getParameter("valeur");

                if (type != null && !type.trim().isEmpty() && valeur != null && !valeur.trim().isEmpty()) {
                    String sql = "INSERT INTO public.type_contraint (type, valeur) VALUES (?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, type.trim().toLowerCase()); // En minuscule pour la cohérence
                        ps.setString(2, valeur.trim());
                        ps.executeUpdate();
                    }
                }
            } else if ("delete".equalsIgnoreCase(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                String sql = "DELETE FROM public.type_contraint WHERE id = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Optionnel : passer l'erreur en session pour l'afficher après le redirect
        }

        // Redirection pour éviter la double soumission du formulaire au rafraîchissement
        response.sendRedirect(request.getContextPath() + "/admin/catalogues");
    }
}