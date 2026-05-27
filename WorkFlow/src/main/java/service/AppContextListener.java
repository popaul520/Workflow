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
        //Runnable task = new EmailTask();
        long initialDelay = getDelayUntil8AM();
       // long period = 24 * 60 * 60; // 24h
        long period = 60 *60  *24 ; 
        System.out.println("Cela répond");
        //scheduler.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.SECONDS);
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
