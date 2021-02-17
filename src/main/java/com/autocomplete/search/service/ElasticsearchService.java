package com.autocomplete.search.service;

import com.autocomplete.search.configuration.ElasticsearchDataAutoConfiguredClient;
import com.autocomplete.search.model.RowData;
import com.autocomplete.search.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.indices.CloseIndexRequest;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class ElasticsearchService implements SearchService {

    private static final String UPLOADED = "recordsUploaded";
    private static final String INDEX_SETTING = "indexSetting.json";
    private final ElasticsearchDataAutoConfiguredClient highLevelClient;

    @Autowired
    DataService dataService;

    @Autowired
    ElasticsearchService(ElasticsearchDataAutoConfiguredClient client) {
        this.highLevelClient = client;
    }

    @Override
    public SearchResponse search(final String searchString, final int returnSize, final String onColumn) {
        SearchResponse response = null;
        try {
            HighlightBuilder highlighter = highlighter(onColumn);
            QueryStringQueryBuilder queryBuilder = searchWithAnalyser(searchString);
            SearchSourceBuilder searchSource = new SearchSourceBuilder()
                    .query(queryBuilder)
                    .size(returnSize)
                    .highlighter(highlighter);

            SearchRequest searchRequest = new SearchRequest().source(searchSource);
             response = highLevelClient.search(searchRequest);

        } catch(Exception ex) {
            log.error("Exception in searching the string" + searchString);
        }
        return response;
    }

    private static QueryStringQueryBuilder searchWithAnalyser(final String searchString) {
        return QueryBuilders.queryStringQuery(searchString)
                .field("PHONETIC_COL")
                .fuzziness(Fuzziness.AUTO);
    }

    private HighlightBuilder highlighter(String onColumn) {
        return new HighlightBuilder()
                .preTags("<em>")
                .postTags("<em>")
                .field(onColumn)
                .tagsSchema("styled")
                .encoder("html");
    }

    @Override
    public Map<String, String> writeRowData(String indexName, String onColumn) {
        List<RowData> dataList = dataService.readData();
        Map<String, String> statusMap = new HashMap<>();
        try {
            overwriteIndex(indexName, onColumn);
            int recordCount = createIndexRequest(indexName, dataList);
            refreshIndices(indexName);
            statusMap.put(UPLOADED, String.valueOf(recordCount));
        } catch (Exception ex) {
            log.error("Failed in creating index request - " + ex.getMessage());
        }
        return statusMap;
    }

    private void overwriteIndex(String indexName, String onColumn) throws IOException {

        String indexSetting = Utility.readResource(INDEX_SETTING);
        Settings setting = Settings.builder().loadFromSource(indexSetting, XContentType.JSON).build();

        XContentBuilder analyzerMapping = getAnalyzerMapping(onColumn);
        CreateIndexRequest request = new CreateIndexRequest(indexName);
        request.settings(setting);
        request.mapping(analyzerMapping);

        IndicesClient indicesClient = highLevelClient.getIndicesClient();
        if (indicesClient.exists(new GetIndexRequest(indexName), RequestOptions.DEFAULT)) {
            indicesClient.close(new CloseIndexRequest(indexName), RequestOptions.DEFAULT);
            indicesClient.delete(new DeleteIndexRequest(indexName), RequestOptions.DEFAULT);
        }
        indicesClient.create(request, RequestOptions.DEFAULT);
    }

    private int createIndexRequest(final String indexName, final List<RowData> dataList) {
        AtomicInteger recordId = new AtomicInteger(0);
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout(TimeValue.MAX_VALUE);

        dataList.forEach(rowData -> {
            try {
                XContentBuilder rowElement = XContentFactory.jsonBuilder()
                        .startObject()
                        .field("BIKEID", rowData.getBikeId())
                        .field("STARTTIME", rowData.getStartTime())
                        .field("STOPTIME", rowData.getStopTime())
                        .field("START_STATION_ID", rowData.getStartStationId())
                        .field("START_STATION_NAME", rowData.getStartStationName())
                        .field("END_STATION_ID", rowData.getEndStationId())
                        .field("END_STATION_NAME", rowData.getEndStationName())
                        .field("USERTYPE", rowData.getUserType())
                        .field("BIRTH_YEAR", rowData.getBirthYear())
                        .field("GENDER", rowData.getGender())
                        .endObject();

                IndexRequest indexRequest = new IndexRequest(indexName)
                        .id(String.valueOf(rowData.getBikeId()))
                        .source(rowElement);

                bulkRequest.add(indexRequest);
                recordId.getAndDecrement();
            }
            catch (Exception ex) {
                log.error("Failed in creating index request - " + ex.getMessage());
            }
        });
        addRecordsToElasticCluster(bulkRequest);
        return recordId.get();
    }

    private void addRecordsToElasticCluster(final BulkRequest bulkRequest) {
        try {
            BulkResponse bulkResponse = highLevelClient.createBulkIndices(bulkRequest);
            if (bulkResponse.hasFailures()) {
                Arrays.stream(bulkResponse.getItems()).parallel().forEach(
                        item -> {
                            if (item.isFailed()) {
                                log.error("FailedId:" + item.getId() + " Reason: " + item.getFailureMessage());
                            }
                        }
                );
            }
        } catch (Exception ex) {
            log.error("Exception: " + ex.getMessage());
        }
    }


    private static XContentBuilder getAnalyzerMapping(String onColumn) throws IOException {
        return XContentFactory.jsonBuilder()
                .startObject()
                .startObject("properties")
                .startObject(onColumn)
                .field("type", "text")
                .startObject("fields")
                .startObject("phonetic")
                .field("type", "text")
                .field("analyser", "phonetic_analyzer")
                .endObject()
                .endObject()
                .endObject()
                .endObject()
                .endObject();
    }

    private void refreshIndices(final String indexNAme) throws IOException {
        highLevelClient.refreshIndices(indexNAme);
    }
}
