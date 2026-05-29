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
}