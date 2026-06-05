package service;

import java.time.*;
import java.util.concurrent.*;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {
    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Un seul thread dédié suffit amplement pour notre tâche quotidienne
        scheduler = Executors.newSingleThreadScheduledExecutor();
        
        Runnable taskWrapper = () -> {
            try {
                Runnable task = new EmailTask();
                task.run();
            } catch (Throwable t) {
                // TRÈS IMPORTANT : On catch Throwable et non Exception pour immuniser le Scheduler
                t.printStackTrace();
            }
        };

        long initialDelay = getDelayUntil8AM();
        long period = 24 * 60 * 60; // 24 heures en secondes et oui mathématique

        System.out.println("Premier lancement dans : " + (initialDelay / 60) + " minutes (à 8h00).");
        
        scheduler.scheduleAtFixedRate(taskWrapper, initialDelay, period, TimeUnit.SECONDS);
    } 
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println(" Arrêt du serveur : fermeture du scheduler d'e-mails.");
        if (scheduler != null) {
            scheduler.shutdown();
            try {
                // On attend gentiment 5 seconde pour l'envoie
            	if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
            }
        }
    }

    private long getDelayUntil8AM() {
        LocalDateTime now = LocalDateTime.now();
        // On fixe la cible à 8h00:00 pile
        LocalDateTime nextRun = now.withHour(17).withMinute(0).withSecond(0).withNano(0);

        // Si 8h est déjà passé aujourd'hui, on reporte au lendemain
        if (now.isAfter(nextRun)) {
            nextRun = nextRun.plusDays(1);
        } 

        return Duration.between(now, nextRun).getSeconds();
    }
}