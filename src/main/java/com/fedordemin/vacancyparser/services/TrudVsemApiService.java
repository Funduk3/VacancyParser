package com.fedordemin.vacancyparser.services;

import com.fedordemin.vacancyparser.entities.VacancyEntity;
import com.fedordemin.vacancyparser.models.VacancyResponseTrudVsem;
import com.fedordemin.vacancyparser.services.converters.ConverterToEntityFromTrudVsemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Component
public class TrudVsemApiService {
    private static final Logger log = LoggerFactory.getLogger(TrudVsemApiService.class);
    private final RestTemplate restTemplate;
    private final ConverterToEntityFromTrudVsemService converterToEntityFromTrudVsemService;
    private final HistoryWriterService historyWriterService;

    private static final String API_URL = "https://opendata.trudvsem.ru/api/v1/vacancies";

    @Autowired
    public TrudVsemApiService(RestTemplate restTemplate, ConverterToEntityFromTrudVsemService converterToEntityFromTrudVsemService, HistoryWriterService historyWriterService) {
        this.restTemplate = restTemplate;
        this.converterToEntityFromTrudVsemService = converterToEntityFromTrudVsemService;
        this.historyWriterService = historyWriterService;
    }

    public VacancyResponseTrudVsem searchVacancies(String text, String area) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "VacancyParser/1.0");

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(API_URL)
                .queryParam("text", text)
                .queryParam("area", area)
                .queryParam("limit", 10);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<VacancyResponseTrudVsem> responseEntity =
                restTemplate.exchange(
                        builder.toUriString(),
                        HttpMethod.GET,
                        entity,
                        VacancyResponseTrudVsem.class
                );

        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException(
                    "Ошибка при запросе TrudVsem: HTTP " + responseEntity.getStatusCode()
            );
        }
        log.error("1");
        VacancyResponseTrudVsem body = responseEntity.getBody();
        if (body == null) {
            log.error("Пустое тело ответа от TrudVsem");
        }
        log.error(body.toString());
        return body;
    }

    public List<VacancyEntity> fetchTrudVsemApi(String searchText, String area,
                                                Boolean isByUser) {
        List<VacancyEntity> entitiesReceived = new ArrayList<>();
        VacancyResponseTrudVsem response = searchVacancies(searchText, area);
        log.error(response.toString());
        for (VacancyResponseTrudVsem.VacancyContainer vacancyTrudVsem : response.getResults().getVacancies()) {
            VacancyEntity vacancyEntity = converterToEntityFromTrudVsemService.
                    convertEntityFromTrudVsem(vacancyTrudVsem.getVacancy());
            entitiesReceived.add(vacancyEntity);
            historyWriterService.saveToHistory(isByUser, vacancyEntity);
        }
        return entitiesReceived;
    }
}
