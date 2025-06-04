package com.fedordemin.vacancyparser.services.secondLayer;

import com.fedordemin.vacancyparser.services.api.HHApiService;
import com.fedordemin.vacancyparser.services.api.TrudVsemApiService;
import org.springframework.transaction.annotation.Transactional;

import com.fedordemin.vacancyparser.entities.VacancyEntity;
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
    private final TrudVsemApiService trudVsemApiService;
    private final VacancyRepo vacancyRepo;

    @Value("${app.hh.pages:2}")
    private int pagesToFetch;

    @Value("${app.hh.per-page:10}")
    private int perPage;

    @Autowired
    public VacancyFetcherService(HHApiService hhApiService, VacancyRepo vacancyRepo,
                                 TrudVsemApiService trudVsemApiService) {
        this.hhApiService = hhApiService;
        this.vacancyRepo = vacancyRepo;
        this.trudVsemApiService = trudVsemApiService;
    }

    @Scheduled(cron = "0 */5 * * * ?")
    @Transactional
    public void scheduledFetchVacancies() throws Exception {
        log.info("Starting scheduled vacancy fetching");
        String defaultSite = "hh.ru";
        fetchVacancies(null, null, null, defaultSite, false);
    }

    @Transactional
    public void fetchVacancies(String searchText, String company_name, String area, String site, Boolean isByUser) {
        List<VacancyEntity> entitiesToSave = new ArrayList<>();
        if (site.equalsIgnoreCase("hh.ru")) {
            entitiesToSave = hhApiService.fetchHhRu(searchText, company_name, area, isByUser, pagesToFetch, perPage);
        } else if (site.equalsIgnoreCase("trudvsem.ru")) {
            entitiesToSave = trudVsemApiService.fetchTrudVsemApi(searchText, area, isByUser);
        } else {
            throw new IllegalArgumentException("Unsupported site: " + site);
        }
        if (!entitiesToSave.isEmpty()) {
            vacancyRepo.saveAll(entitiesToSave);
            log.info("Saved {} vacancies to database from HH.ru", entitiesToSave.size());
        }
    }
}