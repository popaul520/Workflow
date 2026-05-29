package controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

import dao.UtilisateurDAO;
import model.Utilisateur;

@WebServlet("/register")
public class registerController extends HttpServlet {
    private UtilisateurDAO dao = new UtilisateurDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        
        Utilisateur u = new Utilisateur();

        u.setLogin(request.getParameter("login"));
        u.setMdp(request.getParameter("mdp"));
        u.setNom(request.getParameter("nom"));
        u.setPrenom(request.getParameter("prenom"));
        u.setMail(request.getParameter("mail"));
        
        boolean success = dao.register(u);
        System.out.println("Register appelé !");

        if (success) {
            response.sendRedirect("login");
        } else {
            request.setAttribute("error", "Erreur lors de l'inscription");
            RequestDispatcher rd = request.getRequestDispatcher("View/register.jsp");
            rd.forward(request, response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        RequestDispatcher rd = request.getRequestDispatcher("View/register.jsp");
        rd.forward(request, response);
    }
}