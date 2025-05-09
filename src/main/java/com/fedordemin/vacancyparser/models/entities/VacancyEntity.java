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

    private String alternate_url;

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

    private String requirements;
    private String scheduleName;
    private String experienceName;

    public VacancyEntity() {}
}
