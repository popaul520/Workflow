package controller; // Assure-toi que le package est correct

import dao.RoleDAO;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import model.Utilisateur;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("********** INITIALISATION DU DAO **********");
        sce.getServletContext().setAttribute("roleDAO", new RoleDAO());
        

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}