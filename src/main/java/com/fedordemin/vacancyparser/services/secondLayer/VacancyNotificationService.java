package com.fedordemin.vacancyparser.services.secondLayer;

import com.fedordemin.vacancyparser.entities.VacancyEntity;
import com.fedordemin.vacancyparser.services.NotificationService;
import com.fedordemin.vacancyparser.utils.format.VacancyFormatterUtil;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class VacancyNotificationService {
    private final VacancyManagementService vacancyManagementService;
    private final NotificationService notificationService;
    private final VacancyFormatterUtil vacancyFormatterUtil;

    public VacancyNotificationService(VacancyManagementService vacancyManagementService,
                                      NotificationService notificationService, VacancyFormatterUtil vacancyFormatterUtil) {
        this.vacancyManagementService = vacancyManagementService;
        this.notificationService = notificationService;
        this.vacancyFormatterUtil = vacancyFormatterUtil;
    }

    public String sendNotification(String title, String company,
                                   Integer minSalary, Integer maxSalary, String area) {
        int size = 5;
        Page<VacancyEntity> initialPage = vacancyManagementService
                .getVacancies(title, company, minSalary, maxSalary, area, 0, size);
        String output = vacancyFormatterUtil.formatResult(initialPage, "default");
        System.out.println("These vacancies are already in the database:\n" + output);

        return String.valueOf(notificationService
                .startNotification(initialPage, initialPage.getContent().size()));
    }
}