package com.fedordemin.vacancyparser;

import com.fedordemin.vacancyparser.config.HttpClientConfig;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

public class HttpClientConfigTest {

    @Test
    void testRestTemplateTimeouts() {
        HttpClientConfig config = new HttpClientConfig();
        RestTemplate restTemplate = config.restTemplate();

        assertNotNull(restTemplate, "RestTemplate должен быть не null");
        assertTrue(restTemplate.getRequestFactory() instanceof SimpleClientHttpRequestFactory,
                "Ожидается, что RequestFactory является SimpleClientHttpRequestFactory");

        SimpleClientHttpRequestFactory factory = (SimpleClientHttpRequestFactory) restTemplate.getRequestFactory();

        int connectTimeout = (int) ReflectionTestUtils.getField(factory, "connectTimeout");
        int readTimeout = (int) ReflectionTestUtils.getField(factory, "readTimeout");

        assertEquals(5000, connectTimeout, "ConnectTimeout должен быть 5000 мс");
        assertEquals(5000, readTimeout, "ReadTimeout должен быть 5000 мс");
    }
}