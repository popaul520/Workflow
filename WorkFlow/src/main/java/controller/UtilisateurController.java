package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.UtilisateurService;
import model.Utilisateur;
import model.Role;
import java.io.IOException;
import java.util.List;

@WebServlet("/modifierRole")
public class UtilisateurController extends HttpServlet {
    private UtilisateurService service = new UtilisateurService();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Chargement simultané de tous les utilisateurs et de tous les rôles disponibles
        List<Utilisateur> listeUsers = service.recupererTousLesUtilisateurs();
        List<Role> listeRoles = service.recupererTousLesRoles();
        
        request.setAttribute("utilisateurs", listeUsers);
        request.setAttribute("roles", listeRoles);
        
        // Envoi vers l'interface de gestion générale
        request.getRequestDispatcher("/View/UtilisateurRole.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int idUser = Integer.parseInt(request.getParameter("id_utilisateur"));
        int idRole = Integer.parseInt(request.getParameter("id_role"));

        boolean succes = service.changerRoleUtilisateur(idUser, idRole);

        if (succes) {
            request.getSession().setAttribute("messageSucces", "Le rôle de l'utilisateur #" + idUser + " a bien été modifié.");
        } else {
            request.getSession().setAttribute("messageErreur", "Impossible de mettre à jour le rôle de l'utilisateur #" + idUser);
        }

        // Redirection fluide sur la même page
        response.sendRedirect(request.getContextPath() + "/modifierRole");
    }
}