package controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/profil")
public class profilController extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (request.getSession().getAttribute("user") == null) {
            response.sendRedirect("login");
        } else {
            request.getRequestDispatcher("View/profil.jsp").forward(request, response);
        }
    }
}
