package com.fedordemin.vacancyparser.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    @Column(columnDefinition = "TEXT")
    private String description;
    private String city;
    private String street;

    private LocalDateTime publishedAt;
    @Column(columnDefinition = "TEXT")
    private String requirements;
    private String scheduleName;
    private String experienceName;
}
