package com.autocomplete.search.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.snowflake.client.jdbc.internal.google.common.io.CharStreams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


@Slf4j
@Component
public class Utility {

    public static final ObjectMapper OBJ_MAPPER = new ObjectMapper();

    private static ResourceLoader resourceLoader;

    @Autowired
    public Utility (ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public static String readResource(String path) throws IOException {

        try {
            Resource resource = resourceLoader.getResource("classpath:" + path);
            InputStream inputStream = resource.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            return CharStreams.toString(reader);
        }
        catch (IOException ex) {
            log.error("Failed to read file - " + path);
            throw new IOException(ex.getMessage());
        }
    }

    public static String convertToString(Object object) {
        try {
            return OBJ_MAPPER.writeValueAsString(object);
        } catch (Exception ex) {
            log.error("Exception in object conversion - " + ex);
        }
        return null;
    }
}
