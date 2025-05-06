package com.fedordemin.vacancyparser.services;

import com.fedordemin.vacancyparser.models.VacancyHhRu;
import org.springframework.transaction.annotation.Transactional;

import com.fedordemin.vacancyparser.models.VacancyResponseHhRu;
import com.fedordemin.vacancyparser.models.entities.VacancyEntity;
import com.fedordemin.vacancyparser.repositories.VacancyRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VacancyFetcherService {
    private static final Logger log = LoggerFactory.getLogger(VacancyFetcherService.class);

    private final HHApiService hhApiService;
    private final VacancyRepo vacancyRepo;

    @Value("${app.hh.pages:5}")
    private int pagesToFetch;

    @Value("${app.hh.per-page:1}")
    private int perPage;

    @Autowired
    public VacancyFetcherService(HHApiService hhApiService, VacancyRepo vacancyRepo) {
        this.hhApiService = hhApiService;
        this.vacancyRepo = vacancyRepo;
    }

    @Scheduled(cron = "0 */30 * * * ?")
    @Transactional
    public void scheduledFetchVacancies() {
        log.info("Starting scheduled vacancy fetching");
        String defaultSearchText = "IT";
        String defaultArea = "1";
        fetchVacancies(defaultSearchText, defaultArea);
    }

    @Transactional
    public int fetchVacancies(String searchText, String area) {
        List<VacancyEntity> entitiesToSave = new ArrayList<>();
        int totalFetched = 0;

        try {
            for (int page = 0; page < pagesToFetch; page++) {
                log.info("Fetching page {} of vacancies", page);
                VacancyResponseHhRu response = hhApiService.searchVacancies(searchText, area, page, perPage);
                log.info("Searching vacancies for text " + searchText + " and area " + area);
                log.info(String.valueOf(response));
                if (response == null || response.getItems() == null || response.getItems().isEmpty()) {
                    log.info("No more vacancies to fetch");
                    break;
                }

                List<VacancyEntity> pageEntities = new ArrayList<>();
                for (VacancyHhRu vacancyHhRu : response.getItems()) {
                    pageEntities.add(convertToEntity(vacancyHhRu));
                }

                entitiesToSave.addAll(pageEntities);
                totalFetched += pageEntities.size();

                if ((page + 1) >= response.getPages()) {
                    break;
                }

                Thread.sleep(100);
            }

            if (!entitiesToSave.isEmpty()) {
                vacancyRepo.saveAll(entitiesToSave);
                log.info("Saved {} vacancies to database", entitiesToSave.size());
            }

        } catch (Exception e) {
            log.error("Error fetching vacancies: {}", e.getMessage(), e);
        }
        return totalFetched;
    }

    private VacancyEntity convertToEntity(VacancyHhRu vacancyHhRu) {
        try {
            VacancyEntity entity = new VacancyEntity();
            entity.setId(vacancyHhRu.getId());
            entity.setName(vacancyHhRu.getName());
            entity.setAlternate_url(vacancyHhRu.getUrl());
            if (vacancyHhRu.getEmployer() != null) {
                entity.setEmployerId(vacancyHhRu.getEmployer().getId());
                entity.setEmployerName(vacancyHhRu.getEmployer().getName());
            }
            if (vacancyHhRu.getSalary() != null) {
                entity.setSalaryFrom(vacancyHhRu.getSalary().getFrom());
                entity.setSalaryTo(vacancyHhRu.getSalary().getTo());
                entity.setSalaryCurrency(vacancyHhRu.getSalary().getCurrency());
                entity.setSalaryGross(vacancyHhRu.getSalary().getGross());
            }
            if (vacancyHhRu.getAddress() != null) {
                entity.setCity(vacancyHhRu.getAddress().getCity());
                entity.setStreet(vacancyHhRu.getAddress().getStreet());
            }
            if (vacancyHhRu.getSchedule() != null) {
                entity.setScheduleName(vacancyHhRu.getSchedule().getName());
            }
            if (vacancyHhRu.getExperience() != null) {
                entity.setExperienceName(vacancyHhRu.getExperience().getName());
            }
            if (vacancyHhRu.getSnippet() != null) {
                entity.setRequirements(vacancyHhRu.getSnippet().getRequirement());
            }
            entity.setPublishedAt(vacancyHhRu.getPublishedAt());
            entity.setDescription(vacancyHhRu.getDescription());

            return entity;
        } catch (Exception e) {
            log.warn("Error converting vacancy {}: {}", vacancyHhRu.getId(), e.getMessage());
            return null;
        }
    }

    private List<VacancyEntity> convertToEntities(List<VacancyHhRu> vacancies) {
        List<VacancyEntity> entities = new ArrayList<>();
        for (VacancyHhRu vacancyHhRu : vacancies) {
            VacancyEntity entity = convertToEntity(vacancyHhRu);
            if (entity != null) {
                entities.add(entity);
            }
        }
        return entities;
    }
}