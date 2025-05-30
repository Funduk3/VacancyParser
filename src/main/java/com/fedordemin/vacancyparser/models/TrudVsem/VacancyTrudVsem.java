package com.fedordemin.vacancyparser.models.TrudVsem;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import java.time.LocalDate;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VacancyTrudVsem {
    private String id;
    private Integer salary_min;
    private Integer salary_max;
    @JsonProperty("job-name")
    private String name;
    private String employment;
    private String duty;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("creation-date")
    private LocalDate creationDate;

    @JsonProperty("vac_url")
    private String url;

    private VacancyTrudVsem.Company company;
    private VacancyTrudVsem.Category category;
    private VacancyTrudVsem.Addresses.Address address;
    private VacancyTrudVsem.Requirement requirement;

    @Data
    public class Company {
        private String id;
        private String name;
        private String url;
    }

    @Data
    public class Category {
        private String specialisation;
    }

    @Data
    public class Addresses {
        @Data
        public class Address {
            private String location;
        }
    }

    @Data
    public class Requirement {
        private String education;
        private Integer experience;
    }
}
