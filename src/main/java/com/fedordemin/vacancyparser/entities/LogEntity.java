package com.fedordemin.vacancyparser.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "history")
public class LogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "vacancy_id")
    private String vacancyId;

    private String type;

    @Column(name = "is_by_user")
    private Boolean IsByUser;

    private LocalDateTime timestamp;
}
