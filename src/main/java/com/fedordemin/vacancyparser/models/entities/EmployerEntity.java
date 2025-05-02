package com.fedordemin.vacancyparser.models.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "employers")
@Data
public class EmployerEntity {
    @Id
    private String id;

    private String name;
    private String url;
    private Boolean trusted;
}
