package com.fedordemin.vacancyparser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VacancyParserApplication {
    public static void main(String[] args) {
        SpringApplication.run(VacancyParserApplication.class, args);
    }
}
