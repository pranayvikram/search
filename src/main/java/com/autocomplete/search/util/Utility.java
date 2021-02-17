package com.autocomplete.search.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.snowflake.client.jdbc.internal.google.common.io.CharStreams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


@Slf4j
public class Utility {

    public static final ObjectMapper OBJ_MAPPER = new ObjectMapper();

    @Autowired
    private static ResourceLoader resourceLoader;

    public static String readResource(String path) throws IOException {

        String error;
        Resource resource = resourceLoader.getResource(path);
        InputStream inputStream = resource.getInputStream();
        try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            return CharStreams.toString(reader);
        }
        catch (Exception ex ) {
            error = ex.getCause().getMessage();
            log.error("Failed to read file - " + path);
        }
        throw new IOException(error);
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
