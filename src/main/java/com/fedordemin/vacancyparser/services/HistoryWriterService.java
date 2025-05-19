package com.fedordemin.vacancyparser.services;

import com.fedordemin.vacancyparser.models.entities.LogEntity;
import com.fedordemin.vacancyparser.repositories.HistoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HistoryWriterService {
    HistoryRepo historyRepo;

    @Autowired
    public HistoryWriterService(HistoryRepo historyRepo) {
        this.historyRepo = historyRepo;
    }

    public void write(LogEntity logEntity) {
        historyRepo.save(logEntity);
    }
}
