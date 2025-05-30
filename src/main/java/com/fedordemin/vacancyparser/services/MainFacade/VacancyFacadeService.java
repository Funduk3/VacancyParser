package com.fedordemin.vacancyparser.services.MainFacade;

import com.fedordemin.vacancyparser.entities.VacancyEntity;
import com.fedordemin.vacancyparser.services.secondLayer.VacancyExportService;
import com.fedordemin.vacancyparser.services.secondLayer.VacancyFetcherService;
import com.fedordemin.vacancyparser.services.secondLayer.VacancyManagementService;
import com.fedordemin.vacancyparser.services.secondLayer.VacancyNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class VacancyFacadeService {
    private final VacancyFetcherService vacancyFetcherService;
    private final VacancyExportService vacancyExportService;
    private final VacancyManagementService vacancyManagementService;
    private final VacancyNotificationService vacancyNotificationService;

    @Autowired
    public VacancyFacadeService(VacancyFetcherService vacancyFetcherService,
                                VacancyExportService vacancyExportService,
                                VacancyManagementService vacancyManagementService,
                                VacancyNotificationService vacancyNotificationService) {
        this.vacancyFetcherService = vacancyFetcherService;
        this.vacancyExportService = vacancyExportService;
        this.vacancyManagementService = vacancyManagementService;
        this.vacancyNotificationService = vacancyNotificationService;
    }

    public Page<VacancyEntity> getVacancies(
            String title,
            String company,
            Integer minSalary,
            Integer maxSalary,
            String area,
            int page,
            int size
    ) {
        return vacancyManagementService.getVacancies(
                title, company, minSalary, maxSalary, area,
                page, size);
    }

    public VacancyEntity getVacancy(String id) {
        return vacancyManagementService.getVacancy(id);
    }

    public List<VacancyEntity> getAllVacancies() {
        return vacancyManagementService.getAllVacancies();
    }

    public boolean deleteVacancy(String id) {
        return vacancyManagementService.deleteVacancy(id);
    }

    @Transactional
    public void fetchVacancies(String searchText, String company_name, String area, String site) {
        vacancyFetcherService.fetchVacancies(searchText, company_name, area, site, true);
    }

    public String export(String fileType, String filename) {
        return vacancyExportService.export(fileType, filename);
    }

    public String sendNotification(String title, String company, Integer minSalary, Integer maxSalary, String area) {
        return vacancyNotificationService.sendNotification(title, company, minSalary, maxSalary, area);
    }
}
