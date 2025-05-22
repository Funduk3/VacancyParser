package com.fedordemin.vacancyparser.services;

import com.fedordemin.vacancyparser.models.VacancyHhRu;
import com.fedordemin.vacancyparser.models.VacancyResponseTrudVsem;
import com.fedordemin.vacancyparser.entities.LogEntity;
import com.fedordemin.vacancyparser.services.converters.ConverterToEntityFromTrudVsemService;
import com.fedordemin.vacancyparser.services.converters.ConverterToEntityFromHhRuService;
import org.springframework.transaction.annotation.Transactional;

import com.fedordemin.vacancyparser.models.VacancyResponseHhRu;
import com.fedordemin.vacancyparser.entities.VacancyEntity;
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
    private final ConverterToEntityFromHhRuService converterToEntityFromHhRuService;
    private final TrudVsemApiService trudVsemApiService;
    private final ConverterToEntityFromTrudVsemService converterToEntityFromTrudVsemService;
    private final HistoryWriterService historyWriterService;
    private final VacancyComparator vacancyComparator;
    private final VacancyRepo vacancyRepo;

    @Value("${app.hh.pages:1}")
    private int pagesToFetch;

    @Value("${app.hh.per-page:10}")
    private int perPage;

    @Autowired
    public VacancyFetcherService(HHApiService hhApiService, VacancyRepo vacancyRepo,
                                 ConverterToEntityFromHhRuService converterToEntityFromHhRuService,
                                 TrudVsemApiService trudVsemApiService,
                                 ConverterToEntityFromTrudVsemService converterToEntityFromTrudVsemService,
                                 HistoryWriterService historyWriterService, VacancyComparator vacancyComparator) {
        this.hhApiService = hhApiService;
        this.vacancyRepo = vacancyRepo;
        this.converterToEntityFromHhRuService = converterToEntityFromHhRuService;
        this.trudVsemApiService = trudVsemApiService;
        this.converterToEntityFromTrudVsemService = converterToEntityFromTrudVsemService;
        this.historyWriterService = historyWriterService;
        this.vacancyComparator = vacancyComparator;
    }

    @Scheduled(cron = "0 */30 * * * ?")
    @Transactional
    public void scheduledFetchVacancies() {
        log.info("Starting scheduled vacancy fetching");
        String defaultSearchText = "IT";
        String defaultCompany = null;
        String defaultArea = "1";
        String defaultSite = "hh.ru";
        fetchVacancies(defaultSearchText, defaultCompany, defaultArea, defaultSite, false);
    }

    public List<VacancyEntity> fetchHhRu(String searchText, String company,
                                         String area, Boolean isByUser) {
        List<VacancyEntity> entitiesReceived = new ArrayList<>();
        for (int page = 0; page < pagesToFetch; page++) {
            log.info("Fetching page {} of vacancies", page);
            VacancyResponseHhRu response = hhApiService.searchVacancies(searchText, company, area, page, perPage);
            log.info(String.valueOf(response));
            if (response == null || response.getItems() == null || response.getItems().isEmpty()) {
                log.info("No more vacancies to fetch");
                break;
            }

            List<VacancyEntity> pageEntities = new ArrayList<>();
            for (VacancyHhRu vacancyHhRu : response.getItems()) {
                VacancyEntity vacancyEntity = converterToEntityFromHhRuService.
                        convertEntityFromHhRu(vacancyHhRu);
                boolean isDuplicate = false;
                for (VacancyEntity entity : entitiesReceived) {
                    if (vacancyComparator.isDuplicate(vacancyEntity, entity)) {
                        isDuplicate = true;
                        break;
                    }
                }
                if (!isDuplicate) {
                    entitiesReceived.add(vacancyEntity);
                    saveToHistory(isByUser, pageEntities, vacancyEntity);
                }
            }
            entitiesReceived.addAll(pageEntities);

            if ((page + 1) >= response.getPages()) {
                break;
            }
        }
        return entitiesReceived;
    }

    public List<VacancyEntity> fetchTrudVsemApi(String searchText, String company, String area,
                                                Boolean isByUser) {
        List<VacancyEntity> entitiesReceived = new ArrayList<>();
        VacancyResponseTrudVsem response = trudVsemApiService.searchVacancies(searchText, company, area);

        for (VacancyResponseTrudVsem.VacancyContainer vacancyTrudVsem : response.getResults().getVacancies()) {
            VacancyEntity vacancyEntity = converterToEntityFromTrudVsemService.
                    convertEntityFromTrudVsem(vacancyTrudVsem.getVacancy());
            entitiesReceived.add(vacancyEntity);
            saveToHistory(isByUser, vacancyEntity);
        }
        return entitiesReceived;
    }

    private void saveToHistory(Boolean isByUser, VacancyEntity vacancyEntity) {
        LogEntity logEntity = new LogEntity();
        logEntity.setVacancyId(vacancyEntity.getId());
        logEntity.setType("added");
        logEntity.setTimestamp(LocalDateTime.now());
        logEntity.setIsByUser(isByUser);
        historyWriterService.write(logEntity);
    }

    @Transactional
    public void fetchVacancies(String searchText, String company, String area, String site, Boolean isByUser) {
        List<VacancyEntity> entitiesToSave = new ArrayList<>();
        if (site.equalsIgnoreCase("hh.ru")) {
            entitiesToSave = fetchHhRu(searchText, company, area, site, isByUser);
        } else if (site.equalsIgnoreCase("trudvsem.ru")) {
            entitiesToSave = fetchTrudVsemApi(searchText, company, area, site, isByUser);
        }
        if (!entitiesToSave.isEmpty()) {
            vacancyRepo.saveAll(entitiesToSave);
            log.info("Saved {} vacancies to database from HH.ru", entitiesToSave.size());
        }
    }
}