package com.autocomplete.search.controller;

import com.autocomplete.search.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController()
public class ElasticsearchController {

    @Autowired
    SearchService searchService;

    @GetMapping(value = "search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public SearchResponse search(@RequestParam("string") final String searchString,
                                 @RequestParam("onColumn") final String onColumn,
                                 @RequestParam("size") final int returnSize) {
        return searchService.search(searchString, returnSize, onColumn);
    }

    @GetMapping(value = "transfer" , consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> transfer(@RequestParam("indexName") final String indexName,
                                        @RequestParam("onColumn") final String onColumn) {
        Map<String, String> statusMap = searchService.writeRowData(indexName, onColumn);
        return statusMap;
    }
}
