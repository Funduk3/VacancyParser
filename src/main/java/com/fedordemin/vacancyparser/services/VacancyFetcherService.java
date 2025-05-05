package com.fedordemin.vacancyparser.services;

import org.springframework.transaction.annotation.Transactional;

import com.fedordemin.vacancyparser.models.Vacancy;
import com.fedordemin.vacancyparser.models.VacancyResponse;
import com.fedordemin.vacancyparser.models.entities.VacancyEntity;
import com.fedordemin.vacancyparser.repositories.VacancyRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class VacancyFetcherService {
    private static final Logger log = LoggerFactory.getLogger(VacancyFetcherService.class);

    private final HHApiService hhApiService;
    private final VacancyRepo vacancyRepo;

    private String defaultSearchText = "IT";
    private String defaultArea = "1";

    @Value("${app.hh.pages:5}")
    private int pagesToFetch;

    @Value("${app.hh.per-page:10}")
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
        fetchVacancies(defaultSearchText, defaultArea);
    }

    @Transactional
    public int fetchVacancies(String searchText, String area) {
        List<VacancyEntity> entitiesToSave = new ArrayList<>();
        int totalFetched = 0;

        try {
            for (int page = 0; page < pagesToFetch; page++) {
                log.info("Fetching page {} of vacancies", page);
                VacancyResponse response = hhApiService.searchVacancies(searchText, area, page, perPage);
                log.info("Searching vacancies for text " + searchText + " and area " + area);
                log.info(String.valueOf(response));
                if (response == null || response.getItems() == null || response.getItems().isEmpty()) {
                    log.info("No more vacancies to fetch");
                    break;
                }

                List<VacancyEntity> pageEntities = new ArrayList<>();
                for (Vacancy vacancy : response.getItems()) {
                    pageEntities.add(convertToEntity(vacancy));
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

    private VacancyEntity convertToEntity(Vacancy vacancy) {
        try {
            VacancyEntity entity = new VacancyEntity();
            entity.setId(vacancy.getId());
            entity.setName(vacancy.getName());
            if (vacancy.getEmployer() != null) {
                entity.setEmployerId(vacancy.getEmployer().getId());
                entity.setEmployerName(vacancy.getEmployer().getName());
            }
            if (vacancy.getSalary() != null) {
                entity.setSalaryFrom(vacancy.getSalary().getFrom());
                entity.setSalaryTo(vacancy.getSalary().getTo());
                entity.setSalaryCurrency(vacancy.getSalary().getCurrency());
                entity.setSalaryGross(vacancy.getSalary().getGross());
            }
            if (vacancy.getAddress() != null) {
                entity.setCity(vacancy.getAddress().getCity());
                entity.setStreet(vacancy.getAddress().getStreet());
            }
            entity.setPublishedAt(vacancy.getPublishedAt());
            entity.setDescription(vacancy.getDescription());

            return entity;
        } catch (Exception e) {
            log.warn("Error converting vacancy {}: {}", vacancy.getId(), e.getMessage());
            return null;
        }
    }

    private List<VacancyEntity> convertToEntities(List<Vacancy> vacancies) {
        List<VacancyEntity> entities = new ArrayList<>();

        for (Vacancy vacancy : vacancies) {
            VacancyEntity entity = convertToEntity(vacancy);
            if (entity != null) {
                entities.add(entity);
            }
        }

        return entities;
    }
}