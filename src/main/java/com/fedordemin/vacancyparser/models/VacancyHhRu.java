package com.fedordemin.vacancyparser.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VacancyHhRu {
    private String id;
    private String name;
    private Salary salary;
    private Employer employer;
    private String description;
    private Address address;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private LocalDateTime published_at;

    @JsonProperty("alternate_url")
    private String url;

    private Snippet snippet;
    private Schedule schedule;
    private Experience experience;

    @Data
    public class Salary {
        private Integer from;
        private Integer to;
        private String currency;
        private Boolean gross;
    }

    @Data
    public class Employer {
        private String id;
        private String name;
        private String url;
        private Boolean trusted;
    }

    @Data
    public class Address {
        private String city;
        private String street;
    }

    @Data
    public class Snippet {
        private String requirement;
        private String responsibility;
    }

    @Data
    public class Schedule {
        private String id;
        private String name;
    }

    @Data
    public class Experience {
        private String id;
        private String name;
    }
}
