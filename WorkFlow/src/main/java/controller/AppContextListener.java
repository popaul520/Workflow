package controller; // Assure-toi que le package est correct

import dao.RoleDAO;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import model.Utilisateur;

@WebListener // <-- CETTE ANNOTATION EST OBLIGATOIRE
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("********** INITIALISATION DU DAO **********");
        // On l'enregistre sous le nom exact "roleDAO"
        sce.getServletContext().setAttribute("roleDAO", new RoleDAO());

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Optionnel
    }
}