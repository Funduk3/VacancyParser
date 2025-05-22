package com.fedordemin.vacancyparser.services;

import com.fedordemin.vacancyparser.entities.LogEntity;
import com.fedordemin.vacancyparser.repositories.HistoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public LogEntity getLogById(String id) {
        Optional<LogEntity> log = historyRepo.findById(id);
        return log.orElse(null);
    }

    public void write(LogEntity logEntity) {
        historyRepo.save(logEntity);
    }
}
