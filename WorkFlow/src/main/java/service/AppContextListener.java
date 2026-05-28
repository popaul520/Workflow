package service;


import java.time.*;
import java.util.concurrent.*;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

@jakarta.servlet.annotation.WebListener
public class AppContextListener implements ServletContextListener {
    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        Runnable TaskTest = () -> {
        try {
            Runnable task = new EmailTask();
            task.run();
        	
        }catch(Exception e) {
            System.err.println("Erreur lors de l'exécution de l'EmailTask : " + e.getMessage());
            e.printStackTrace();
        }};
        long initialDelay = getDelayUntil8AM();
        long period = 24 * 60 * 60; // 24h = 60s * 60min *24h t'es in vrai chadock
        //long period = 10; // 24h 60s * 60min *24h t'es in vrai chadock

        System.out.println("Cela répond");
        scheduler.scheduleAtFixedRate(TaskTest, initialDelay, period, TimeUnit.SECONDS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        scheduler.shutdown();
    }
    private long getDelayUntil8AM() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRun = now.withHour(8).withMinute(0).withSecond(0);

        if (now.compareTo(nextRun) > 0) {
            nextRun = nextRun.plusDays(1);
        }

        return Duration.between(now, nextRun).getSeconds();
    }
}
