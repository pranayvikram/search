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

    private static final int fetchSize = 1000;

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
                RowData rowData = RowData.builder()
                        .bikeId(resultSet.getInt("BIKEID"))
                        .startTime(resultSet.getString("STARTTIME"))
                        .stopTime(resultSet.getString("STOPTIME"))
                        .startStationId(resultSet.getInt("START_STATION_ID"))
                        .startStationName(resultSet.getString("START_STATION_NAME"))
                        .endStationId(resultSet.getInt("END_STATION_ID"))
                        .endStationName(resultSet.getString("END_STATION_NAME"))
                        .userType(resultSet.getString("USERTYPE"))
                        .birthYear(resultSet.getInt("BIRTH_YEAR"))
                        .gender(resultSet.getInt("GENDER"))
                        .build();
                dataList.add(rowData);
            }
            conn.close();
        } catch (SQLException se) {
            log.error("Connection error!!");
        }
        log.info("Data read complete - record size: " + dataList.size());
        return dataList;
    }

    private String getQuery() {
        StringBuilder query = new StringBuilder()
                .append(" SELECT BIKEID, STARTTIME, STOPTIME, START_STATION_ID, START_STATION_NAME,")
                .append(" END_STATION_ID, END_STATION_NAME, USERTYPE, BIRTH_YEAR, GENDER")
                .append(" FROM CITIBIKE.SCHEMA.TRIPS")
                .append(" LIMIT 1000");
        return query.toString();
    }
}
