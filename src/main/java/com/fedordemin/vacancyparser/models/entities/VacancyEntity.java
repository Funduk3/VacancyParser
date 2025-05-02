package com.fedordemin.vacancyparser.models.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "vacancies")
@Data
public class VacancyEntity {
    @Id
    private String id;

    private String name;
    private Integer salaryFrom;
    private Integer salaryTo;
    private String salaryCurrency;
    private Boolean salaryGross;

    private String employerId;
    private String employerName;

    private String description;
    private String city;
    private String street;

    private LocalDateTime publishedAt;

    public VacancyEntity(String _id, String _name, String _employer_name, Integer _salaryFrom, Integer _salaryTo, String vs,
                         LocalDateTime _publishedAt, String _description) {
        this.id = _id;
        this.name = _name;
        this.employerName = _employer_name;
        this.salaryFrom = _salaryFrom;
        this.salaryTo = _salaryTo;
        this.publishedAt = _publishedAt;
        this.description = _description;
    }

    public VacancyEntity() {}
}
