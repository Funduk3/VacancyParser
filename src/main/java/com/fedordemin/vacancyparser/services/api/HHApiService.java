package com.fedordemin.vacancyparser.services.api;

import com.fedordemin.vacancyparser.entities.VacancyEntity;
import com.fedordemin.vacancyparser.models.HhRu.EmployerResponseHhRu;
import com.fedordemin.vacancyparser.models.HhRu.VacancyHhRu;
import com.fedordemin.vacancyparser.models.HhRu.VacancyResponseHhRu;
import com.fedordemin.vacancyparser.services.HistoryWriterService;
import com.fedordemin.vacancyparser.services.converters.ConverterToEntityFromHhRuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.fedordemin.vacancyparser.utils.comparator.VacancyComparator.isDuplicate;

@Component
public class HHApiService {
    private static final Logger log = LoggerFactory.getLogger(HHApiService.class);

    private final RestTemplate restTemplate;
    private final ConverterToEntityFromHhRuService converterToEntityFromHhRuService;
    private final HistoryWriterService historyWriterService;
    private static final String API_URL = "https://api.hh.ru/vacancies";
    private static final String EMPLOYER_API_URL = "https://api.hh.ru/employers";

    @Autowired
    public HHApiService(RestTemplate restTemplate, ConverterToEntityFromHhRuService converterToEntityFromHhRuService,
                        HistoryWriterService historyWriterService) {
        this.restTemplate = restTemplate;
        this.converterToEntityFromHhRuService = converterToEntityFromHhRuService;
        this.historyWriterService = historyWriterService;
    }

    public VacancyResponseHhRu searchVacancies(String text, String company_id, String area, int page, int perPage) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "VacancyParser/1.0");

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(API_URL);

        if (text != null) {
            builder.queryParam("text", text);
        }
        if (company_id != null) {
            builder.queryParam("employer_id", company_id);
        }
        if (area != null) {
            builder.queryParam("area", area);
        }
        builder.queryParam("search_field", "name");
        builder.queryParam("page", page);
        builder.queryParam("per_page", perPage);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<VacancyResponseHhRu> response = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    entity,
                    VacancyResponseHhRu.class
            );
            try {
                if (response.getBody() != null) {
                    if (response.getStatusCode().is2xxSuccessful()) {
                        return response.getBody();
                    } else {
                        log.error("Ошибка при запросе вакансий: HTTP {}", response.getStatusCode());
                    }
                }
            } catch (Exception e) {
                log.error("Не смогли найти");
            }
        } catch (RestClientException e) {
            log.error("Исключение при вызове API hh.ru", e);
        }
        return null;
    }

    public List<VacancyEntity> fetchHhRu(String searchText, String company_name,
                                         String area, Boolean isByUser, Integer pagesToFetch,
                                         Integer perPage) {
        List<VacancyEntity> entitiesReceived = new ArrayList<>();
        for (int page = 0; page < pagesToFetch; page++) {
            log.info("Fetching page {} of vacancies", page);
            String company_id = null;
            if (company_name != null) {
                company_id = getCompanyIdByName(company_name);
            }
            VacancyResponseHhRu response = searchVacancies(searchText, company_id, area, page, perPage);
            log.error(String.valueOf(response));
            if (response == null || response.getItems() == null || response.getItems().isEmpty()) {
                throw new IllegalStateException("There are no vacancies found for the given criteria.");
            }
            for (VacancyHhRu vacancyHhRu : response.getItems()) {
                log.warn(vacancyHhRu.toString());
                VacancyEntity vacancyEntity = converterToEntityFromHhRuService.
                        convertEntityFromHhRu(vacancyHhRu);
                boolean isDuplicate = false;
                for (VacancyEntity entity : entitiesReceived) {
                    if (isDuplicate(vacancyEntity, entity)) {
                        isDuplicate = true;
                        break;
                    }
                }
                if (!isDuplicate) {
                    entitiesReceived.add(vacancyEntity);
                    historyWriterService.saveToHistory(isByUser, vacancyEntity);
                }
            }
            if ((page + 1) >= response.getPages()) {
                break;
            }
        }
        return entitiesReceived;
    }

    public String getCompanyIdByName(String companyName) {
        final int PER_PAGE = 20;
        final String USER_AGENT = "VacancyParser/1.0";

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", USER_AGENT);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        URI uri = UriComponentsBuilder
                .fromHttpUrl(EMPLOYER_API_URL)
                .queryParam("text", companyName)
                .queryParam("per_page", PER_PAGE)
                .queryParam("page", 0)
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();
        ResponseEntity<EmployerResponseHhRu> response;
        response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                entity,
                EmployerResponseHhRu.class
        );
        var items = Objects.requireNonNull(response.getBody()).getItems();
        log.error("Response from HH API: {}", response);
        for (EmployerResponseHhRu.EmployerHhRu emp : items) {
            if (emp.getName().equalsIgnoreCase(companyName.trim())) {
                log.info("Найден работодатель '{}' с ID {}", emp.getName(), emp.getId());
                return emp.getId();
            }
        }
        log.warn("Работодатель '{}' не найден ни на одной из страниц.", companyName);
        return null;
    }
}