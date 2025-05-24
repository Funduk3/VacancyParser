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
    private Boolean isByUser;

    private LocalDateTime timestamp;

    public LogEntity() {
    }

    private LogEntity(Builder builder) {
        this.vacancyId = builder.vacancyId;
        this.type = builder.type;
        this.isByUser = builder.isByUser;
        this.timestamp = builder.timestamp;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String vacancyId;
        private String type;
        private Boolean isByUser;
        private LocalDateTime timestamp;

        public Builder vacancyId(String vacancyId) {
            this.vacancyId = vacancyId;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder isByUser(Boolean isByUser) {
            this.isByUser = isByUser;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public LogEntity build() {
            return new LogEntity(this);
        }
    }
}
