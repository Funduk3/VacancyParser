// java
package com.fedordemin.vacancyparser;

import com.fedordemin.vacancyparser.entities.VacancyEntity;
import com.fedordemin.vacancyparser.services.NotificationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.jupiter.api.Assertions.*;

public class NotificationServiceTest {

    private final NotificationService notificationService = new NotificationService();

    @AfterEach
    void tearDown() {
        notificationService.stopNotifications();
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testStartNotificationNoNewVacancy() throws InterruptedException {
        VacancyEntity vacancy = new VacancyEntity();
        List<VacancyEntity> list = Collections.singletonList(vacancy);
        Page<VacancyEntity> page = new PageImpl<>(list);

        // Передаём size равное кол-ву вакансий в странице
        AtomicReference<String> newVacancy = notificationService.startNotification(page, list.size());
        TimeUnit.MILLISECONDS.sleep(500);

        assertNull(newVacancy.get(), "Новая вакансия не должна быть обнаружена");
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testStartNotificationWithNewVacancy() throws InterruptedException {
        VacancyEntity vacancy1 = new VacancyEntity() {
            @Override
            public String toString() {
                return "Vacancy 1";
            }
        };
        VacancyEntity vacancy2 = new VacancyEntity() {
            @Override
            public String toString() {
                return "Vacancy 2";
            }
        };
        // Создаем страницу с двумя вакансиями, но ожидаем, что начальное значение size меньше фактического количества
        List<VacancyEntity> list = Arrays.asList(vacancy1, vacancy2);
        Page<VacancyEntity> page = new PageImpl<>(list);

        // Передаем размер меньше количества вакансий для симуляции появления новой вакансии
        AtomicReference<String> newVacancy = notificationService.startNotification(page, 1);

        long startTime = System.currentTimeMillis();
        while (newVacancy.get() == null && System.currentTimeMillis() - startTime < 1000) {
            TimeUnit.MILLISECONDS.sleep(50);
        }
        assertEquals("Vacancy 2", newVacancy.get(), "Новая вакансия должна быть обнаружена");
    }
}