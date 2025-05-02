package com.fedordemin.vacancyparser.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class VacancyResponse {
    private List<Vacancy> items;
    private int found;
    private int pages;
    private int perPage;
    private int page;
    private String alternateUrl;
}