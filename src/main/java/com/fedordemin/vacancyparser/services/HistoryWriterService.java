package com.fedordemin.vacancyparser.services;

import com.fedordemin.vacancyparser.entities.LogEntity;
import com.fedordemin.vacancyparser.repositories.HistoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoryWriterService {
    HistoryRepo historyRepo;

    @Autowired
    public HistoryWriterService(HistoryRepo historyRepo) {
        this.historyRepo = historyRepo;
    }

    public List<LogEntity> getAllLogs() {
        return historyRepo.findAll();
    }

    public void write(LogEntity logEntity) {
        historyRepo.save(logEntity);
    }
}
