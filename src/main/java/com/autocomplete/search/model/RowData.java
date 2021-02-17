package com.autocomplete.search.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RowData {
    String id;
    String firstName;
    String lastNAme;
    String ssn;
    String comment;
}
