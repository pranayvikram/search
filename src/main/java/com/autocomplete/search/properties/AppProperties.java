package com.autocomplete.search.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("app")
public class AppProperties {

    private String username;
    private String password;
    private String db;
    private String schema;
    private String warehouse;
    private String role;
    private String datasourceUrl;
    private String driverClassName;
}
