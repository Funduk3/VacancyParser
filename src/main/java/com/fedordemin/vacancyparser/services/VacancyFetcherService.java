package com.fedordemin.vacancyparser.services;

import com.fedordemin.vacancyparser.models.VacancyHhRu;
import com.fedordemin.vacancyparser.models.VacancyResponseTrudVsem;
import com.fedordemin.vacancyparser.services.converters.ConvertToEntityFromTrudVsemService;
import com.fedordemin.vacancyparser.services.converters.ConverterToEntityFromHhRuService;
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
    private final ConverterToEntityFromHhRuService converterToEntityFromHhRuService;
    private final TrudVsemApiService trudVsemApiService;
    private final ConvertToEntityFromTrudVsemService convertToEntityFromTrudVsemService;
    private final VacancyRepo vacancyRepo;

    @Value("${app.hh.pages:5}")
    private int pagesToFetch;

    @Value("${app.hh.per-page:1}")
    private int perPage;

    @Autowired
    public VacancyFetcherService(HHApiService hhApiService, VacancyRepo vacancyRepo,
                                 ConverterToEntityFromHhRuService converterToEntityFromHhRuService,
                                 TrudVsemApiService trudVsemApiService,
                                 ConvertToEntityFromTrudVsemService convertToEntityFromTrudVsemService) {
        this.hhApiService = hhApiService;
        this.vacancyRepo = vacancyRepo;
        this.converterToEntityFromHhRuService = converterToEntityFromHhRuService;
        this.trudVsemApiService = trudVsemApiService;
        this.convertToEntityFromTrudVsemService = convertToEntityFromTrudVsemService;
    }

    @Scheduled(cron = "0 */30 * * * ?")
    @Transactional
    public void scheduledFetchVacancies() {
        log.info("Starting scheduled vacancy fetching");
        String defaultSearchText = "IT";
        String defaultArea = "1";
        String defaultSite = "hh.ru";
        fetchVacancies(defaultSearchText, defaultArea, defaultSite);
    }

    @Transactional
    public void fetchVacancies(String searchText, String area, String site) {
        List<VacancyEntity> entitiesToSave = new ArrayList<>();
        if (site.equalsIgnoreCase("hh.ru")) {
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
                        pageEntities.add(converterToEntityFromHhRuService.convertEntityFromHhRu(vacancyHhRu));
                    }
                    entitiesToSave.addAll(pageEntities);

                    if ((page + 1) >= response.getPages()) {
                        break;
                    }

                    Thread.sleep(100);
                }

                if (!entitiesToSave.isEmpty()) {
                    vacancyRepo.saveAll(entitiesToSave);
                    log.info("Saved {} vacancies to database from HH.ru", entitiesToSave.size());
                }
            } catch (Exception e) {
                log.error("Error fetching vacancies: {}", e.getMessage(), e);
            }
        } else if (site.equalsIgnoreCase("trudvsem.ru")) {
            try {
                log.info("Searching vacancies for text " + searchText + " and area " + area);
                VacancyResponseTrudVsem response = trudVsemApiService.searchVacancies(searchText, area);

                for (VacancyResponseTrudVsem.VacancyContainer vacancyTrudVsem : response.getResults().getVacancies()) {
                    entitiesToSave.add(
                            convertToEntityFromTrudVsemService.convertEntityFromTrudVsem(vacancyTrudVsem.getVacancy()));
                }

                if (!entitiesToSave.isEmpty()) {
                    vacancyRepo.saveAll(entitiesToSave);
                    log.info("Saved {} vacancies to database from TrudVsem", entitiesToSave.size());
                }
            } catch (Exception e) {
                log.error("Error fetching vacancies from TrudVsem: {}", e.getMessage());
            }
        } else {
            log.error("No such API");
        }
    }
}