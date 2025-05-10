package com.fedordemin.vacancyparser.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VacancyResponseTrudVsem {
    private String status;
    private RequestInfo request;
    private Meta meta;
    private Results results;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RequestInfo {
        private String api;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Meta {
        private Integer total;
        private Integer limit;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Results {
        private List<VacancyContainer> vacancies;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VacancyContainer {
        private VacancyTrudVsem vacancy;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Region {
        @JsonProperty("region_code")
        private String regionCode;
        private String name;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Company {
        private String companycode;
        private String email;

        @JsonProperty("hr-agency")
        private boolean hrAgency;

        private String inn;
        private String kpp;
        private String name;
        private String ogrn;
        private String url;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Category {
        private String specialisation;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Requirement {
        private String education;
        private Integer experience;
        private String qualification;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Addresses {
        private List<Address> address;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Address {
        private String location;
        private String lng;
        private String lat;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Contact {
        @JsonProperty("contact_type")
        private String contactType;

        @JsonProperty("contact_value")
        private String contactValue;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WorkPlaceType {
        private boolean workPlaceForeign;
        private boolean workPlaceOrdinary;
        private boolean workPlaceQuota;
        private boolean workPlaceSpecial;

        @JsonProperty("workPlaceSocialProtected")
        private List<String> workPlaceSocialProtected;
    }
}
