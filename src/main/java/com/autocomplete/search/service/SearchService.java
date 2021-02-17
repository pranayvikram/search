package com.autocomplete.search.service;

import org.elasticsearch.action.search.SearchResponse;

import java.util.Map;

public interface SearchService {

    SearchResponse search(String searchString, int returnCount, String onColumn);

    Map<String, String> writeRowData(String indexName, String onColumn);
}
