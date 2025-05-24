package com.fedordemin.vacancyparser.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.List;

@Data
public class EmployerResponseHhRu {
    private List<EmployerHhRu> items;
    private int found;
    private int pages;
    private int perPage;
    private int page;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class EmployerHhRu {
        @Id
        private String id;
        private String name;
    }
}
