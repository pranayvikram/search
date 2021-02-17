package com.autocomplete.search.service;

import com.autocomplete.search.model.RowData;
import com.autocomplete.search.properties.AppProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Slf4j
@Service
public class SnowflakeService implements DataService {

    private static int fetchSize = 1000;

    @Autowired
    static AppProperties appProperties;

    public SnowflakeService(final AppProperties appProperties) {
        SnowflakeService.appProperties = appProperties;
    }

    private Connection getSnowflakeConnection() throws SQLException {
        return DriverManager.getConnection(appProperties.getDatasourceUrl(), getConfiguration());
    }

    private Properties getConfiguration() {
        Properties prop = new Properties();
        prop.put("user", appProperties.getUsername());
        prop.put("password", appProperties.getPassword());
        prop.put("db", appProperties.getDb());
        prop.put("schema", appProperties.getSchema());
        prop.put("warehouse", appProperties.getWarehouse());
        prop.put("role", appProperties.getRole());
        return prop;
    }

    @Override
    public List<RowData> readData() {

        List<RowData> dataList = new ArrayList<>();
        try {
            Connection conn = getSnowflakeConnection();

            PreparedStatement statement = conn.prepareStatement(getQuery());
            statement.setFetchSize(fetchSize);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                RowData data = RowData.builder()
                        .id(resultSet.getString("ID"))
                        .firstName(resultSet.getString("FIRST_NAME"))
                        .lastNAme(resultSet.getString("LAST_NAME"))
                        .ssn(resultSet.getString("SSN"))
                        .comment(resultSet.getString("COMMENT"))
                        .build();
                dataList.add(data);
            }
            conn.close();
        } catch (SQLException se) {
            System.out.println("Connection error!!");
        }
        System.out.println("Success: " + dataList);
        return dataList;
    }

    private String getQuery() {
        return new String("SELECT * FROM SALES_DB.DEV1.AUTHORITIES");
    }
}
