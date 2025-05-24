package com.fedordemin.vacancyparser.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vacancies")
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

    private LocalDateTime published_at;
    @Column(columnDefinition = "TEXT")
    private String requirements;
    private String scheduleName;
    private String experienceName;
}
