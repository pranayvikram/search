package com.autocomplete.search.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RowData {

    int bikeId;
    String startTime;
    String stopTime;
    int startStationId;
    String startStationName;
    int endStationId;
    String endStationName;
    String userType;
    int birthYear;
    int gender;
}
