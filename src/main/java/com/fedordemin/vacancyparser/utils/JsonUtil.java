package com.fedordemin.vacancyparser.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.util.List;

public class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    public static <T> byte[] toJsonBytes(List<T> list) throws IOException {
        return mapper.writeValueAsBytes(list);
    }
}
