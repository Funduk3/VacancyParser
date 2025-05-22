package com.fedordemin.vacancyparser.services;

import com.fedordemin.vacancyparser.models.VacancyResponseTrudVsem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class TrudVsemApiService {
    private static final Logger log = LoggerFactory.getLogger(TrudVsemApiService.class);
    private final RestTemplate restTemplate;
    private static final String API_URL = "https://opendata.trudvsem.ru/api/v1/vacancies";

    @Autowired
    public TrudVsemApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public VacancyResponseTrudVsem searchVacancies(String text, String company, String area) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "VacancyParser/1.0");

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(API_URL)
                .queryParam("text", text)
                .queryParam("company", company)
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

        VacancyResponseTrudVsem body = responseEntity.getBody();
        if (body == null) {
            log.error("Пустое тело ответа от TrudVsem");
        }
        return body;
    }
}
