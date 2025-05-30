package com.fedordemin.vacancyparser.models.HhRu;

import lombok.Data;

import java.util.List;

@Data
public class VacancyResponseHhRu {
    private List<VacancyHhRu> items;
    private int found;
    private int pages;
    private int perPage;
    private int page;
}