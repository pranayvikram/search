package com.autocomplete.search.configuration;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.inject.Singleton;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.elasticsearch.ReactiveElasticsearchRestClientAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

import java.io.IOException;

@Slf4j
@Singleton
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(value = ElasticsearchRestTemplate.class)
@AutoConfigureAfter(value = {ElasticsearchRestClientAutoConfiguration.class,
                             ReactiveElasticsearchRestClientAutoConfiguration.class})
public class ElasticsearchDataAutoConfiguredClient {

    @Value("${elasticsearch.host:localhost}")
    public String host;

    @Value("${elasticsearch.port:9300}")
    public int port;

    private final RestHighLevelClient restHighLevelClient;

    public ElasticsearchDataAutoConfiguredClient(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    private int timeout = 60;

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



    /*@Bean
    public RestHighLevelClient client() {

        System.out.println("host:" + host + "port:" + port);
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("elastic", "changeme"));

        RestClientBuilder builder = RestClient.builder(new HttpHost("localhost", 9200, "http"))
                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
        builder.setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder.setConnectTimeout(timeout * 1000).setSocketTimeout(timeout * 1000)
                .setConnectionRequestTimeout(0));

        RestHighLevelClient client = new RestHighLevelClient(builder);
        return client;


    }*/

    /*@Bean(destroyMethod = "close")
    public RestHighLevelClient client() {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9500, "http")));
        return client;
    }*/


}
