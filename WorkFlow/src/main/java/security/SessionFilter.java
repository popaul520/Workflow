package security;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import model.Utilisateur;
import java.io.IOException;

@WebFilter("/*") // S'applique à toutes les URLs de l'application
public class SessionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpSession session = req.getSession();

        // On vérifie si l'utilisateur est déjà en session
        Utilisateur user = (Utilisateur) session.getAttribute("user");

        if (user == null) {
            // Création de l'utilisateur Invité (uniquement en mémoire)
            Utilisateur guest = new Utilisateur();
            guest.setId(-1);
            guest.setNom("Invite");
            guest.setPrenom("Visiteur");
            guest.setRole("GUEST"); // Rôle restreint
            guest.setLogin("guest");
            
            // On le place en session
            session.setAttribute("user", guest);
        }

        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}