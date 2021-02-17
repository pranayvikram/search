package com.autocomplete.search.controller;

import com.autocomplete.search.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController("/")
public class ElasticsearchController {

    @Autowired
    SearchService searchService;

    @GetMapping(value = "search", produces = MediaType.APPLICATION_JSON_VALUE)
    public SearchResponse search(@PathVariable("string") final String searchString,
                                 @PathVariable("size") final int returnSize) {
        return searchService.search(searchString, returnSize);
    }

    @GetMapping(value = "transfer", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> transfer(@PathVariable("indexName") final String indexName) {
        Map<String, String> statusMap = searchService.writeRowData(indexName);
        return statusMap;
    }
}
