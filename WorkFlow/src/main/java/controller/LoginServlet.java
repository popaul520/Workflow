package controller;


import ldap.LdapService;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Map;

public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp)
            throws ServletException, IOException {

        String login = req.getParameter("login");
        String password = req.getParameter("password");

        try {
            Map<String, Object> user =
                    LdapService.authenticate(login, password);

            HttpSession session = req.getSession();
            session.setAttribute("user", user);

            resp.sendRedirect("home");

        } catch (Exception e) {
            req.setAttribute("error", "Identifiants invalides");
            req.getRequestDispatcher("/WEB-INF/jsp/login.jsp")
                .forward(req, resp);
        }
    }
}