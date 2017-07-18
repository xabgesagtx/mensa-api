package com.github.xabgesagtx.mensa.scrape;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.xabgesagtx.mensa.model.Allergen;
import com.google.common.base.Charsets;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.List;

public class TestUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    public static void printAsJson(Object object) throws JsonProcessingException {
        System.out.println(MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(object));
    }

    public static String readStringFromClasspath(String path) throws IOException {
        return IOUtils.toString(TestUtils.class.getResourceAsStream(path), Charsets.UTF_8);
    }

    public static <T> T readObjectFromClasspath(String path, TypeReference<T> reference) throws IOException {
        return MAPPER.readValue(TestUtils.class.getResourceAsStream(path), reference);
    }

}
