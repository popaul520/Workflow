package security;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import model.Utilisateur;
import java.io.IOException;

@WebFilter("/*") 
public class SessionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        String uri = req.getRequestURI();

        // 1. SÉCURITÉ & PERFORMANCE : Conversion en minuscules pour s'affranchir de la sensibilité à la casse du serveur
        String uriLower = uri.toLowerCase();
        if (uriLower.contains("/pdf") || uriLower.contains("/fonts/") || uriLower.contains("/css/") || uriLower.contains("/js/")) {
            chain.doFilter(request, response);
            return; 
        }

        HttpSession session = req.getSession();

        // 2. Vérification / Initialisation de la session utilisateur
        Utilisateur user = (Utilisateur) session.getAttribute("user");

        if (user == null) {
            // Création de l'utilisateur Invité (uniquement en mémoire)
            Utilisateur guest = new Utilisateur();
            guest.setId(-1);
            guest.setNom("Invite");
            guest.setPrenom("Visiteur");
            guest.setRole(12); // Rôle restreint de consultation
            guest.setLogin("guest");
            
            session.setAttribute("user", guest);
        }

        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}