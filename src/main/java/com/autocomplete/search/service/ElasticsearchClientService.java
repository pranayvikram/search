package com.autocomplete.search.service;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class ElasticsearchClientService {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    public ElasticsearchClientService(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    public void refreshIndices(String indexName) throws IOException {
        restHighLevelClient.indices().refresh(new RefreshRequest(indexName), RequestOptions.DEFAULT);
    }

    public SearchResponse search(SearchRequest request) throws IOException {
        return this.restHighLevelClient.search(request, RequestOptions.DEFAULT);
    }

    public IndicesClient getIndicesClient() {
        return restHighLevelClient.indices();
    }

    public BulkResponse createBulkIndices(BulkRequest bulkRequest) throws IOException {
        return restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
    }
}
