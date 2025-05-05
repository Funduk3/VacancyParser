package com.fedordemin.vacancyparser.services;

import com.fedordemin.vacancyparser.models.Vacancy;
import com.fedordemin.vacancyparser.models.VacancyResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class HHApiService {
    private static final Logger log = LoggerFactory.getLogger(HHApiService.class);

    private final RestTemplate restTemplate;
    private static final String API_URL = "https://api.hh.ru/vacancies";

    @Autowired
    public HHApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public VacancyResponse searchVacancies(String text, String area, int page, int perPage) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "VacancyParser/1.0");

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(API_URL)
                .queryParam("text", text)
                .queryParam("area", area)
                .queryParam("page", page)
                .queryParam("per_page", perPage);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<VacancyResponse> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity,
                VacancyResponse.class
        );
        return response.getBody();
    }
}