package com.fedordemin.vacancyparser.services;

import com.fedordemin.vacancyparser.entities.VacancyEntity;
import jakarta.annotation.PreDestroy;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class NotificationService {
    private ScheduledExecutorService scheduler;

    @PreDestroy
    public void cleanup() {
        stopNotifications();
    }

    public void stopNotifications() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
            }
        }
    }

    public AtomicReference<String> startNotification(Page<VacancyEntity> page, Integer size) {
        stopNotifications();

        scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            return thread;
        });
        AtomicReference<String> newVacancy = new AtomicReference<>();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                if (page.getContent().size() > size) {
                    newVacancy.set(String.valueOf(
                            page.getContent().get(page.getContent().size() - 1)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
        return newVacancy;
    }
}
