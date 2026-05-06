package controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Map;

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
     * POST /login
     * Authentification Active Directory
     */
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String login = request.getParameter("login");
        String mdp   = request.getParameter("mdp");

        try {
            // ✅ AUTHENTIFICATION ACTIVE DIRECTORY (LDAP)

        Map<String, Object> adData = LdapService.authenticate(login, mdp);

		Utilisateur user = LdapUserMapper.toUtilisateur(adData);
		
		HttpSession session = request.getSession(true);
		session.setAttribute("user", user);


            // ✅ Redirection vers la page protégée
            response.sendRedirect(request.getContextPath() + "/home");

        } catch (Exception e) {
            // ❌ Échec LDAP
            request.setAttribute("error",
                    "Identifiants Active Directory incorrects");

            request.getRequestDispatcher("/View/login.jsp")
                   .forward(request, response);
        }
    }
}