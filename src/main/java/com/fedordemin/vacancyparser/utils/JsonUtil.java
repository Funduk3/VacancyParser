package com.fedordemin.vacancyparser.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Component
public class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    public <T> void toJsonBytes(List<T> list, String filename) throws IOException {
        Files.write(Paths.get(filename), mapper.writeValueAsBytes(list));
    }
}
