package controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import dao.RoleDAO;
import dao.UtilisateurDAO;
import ldap.LdapService;
import ldap.LdapUserMapper;
import model.Utilisateur;

public class loginController extends HttpServlet {

    /**
     * GET /login
     * Affiche la page de login
     */
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/View/login.jsp")
               .forward(request, response);
    }

    /**
     * POST /login é
     * Authentification Active Directory
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        String action = request.getParameter("action");
        HttpSession session = request.getSession(true);

        try {
            if ("guest".equals(action)) {
                // --- LOGIQUE INVITÉ ---
                Utilisateur guestUser = new Utilisateur();
                guestUser.setNom("Visiteur");
                guestUser.setLogin("guest_" + System.currentTimeMillis());
                
                // On définit l'ID du rôle invité (ex: 99)
                int guestRoleId = 99; 
                guestUser.setRole(guestRoleId);

                session.setAttribute("user", guestUser);
                response.sendRedirect(request.getContextPath() + "/home");
                return;
            }

            // --- LOGIQUE CONNEXION CLASSIQUE (LDAP) ---
            String login = request.getParameter("login");
            String mdp = request.getParameter("mdp");
            String roleIdStr = request.getParameter("roleId");

            // Authentification LDAP
            Map<String, Object> adData = LdapService.authenticate(login, mdp);
            Utilisateur user = LdapUserMapper.toUtilisateur(adData);
            UtilisateurDAO modifie = new UtilisateurDAO();
            int chosenRoleId = Integer.parseInt(roleIdStr);
            // Vérification des droits en base
            RoleDAO roleDao = new RoleDAO();
            if (true) {
                user.setRole(chosenRoleId);
                modifie.updateUserRole(login,chosenRoleId );
                session.setAttribute("user", user);
                response.sendRedirect(request.getContextPath() + "/home");
            } /*else {
                throw new Exception("Droits insuffisants pour ce rôle.");
            }*/
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/View/login.jsp").forward(request, response);
        }
    }
}