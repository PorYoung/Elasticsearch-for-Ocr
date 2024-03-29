package com.poryoung.elasticsearch.repository.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.poryoung.elasticsearch.Bean.Ocr;
import com.poryoung.elasticsearch.Bean.Page;
import com.poryoung.elasticsearch.Bean.TextResult;
import com.poryoung.elasticsearch.repository.IOcrRepository;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * @author Administrator
 * @Title: OcrESRepository
 * @ProjectName movie-elasticsearch
 * @Description: TODO
 * @date 2019/7/1121:33
 */
@Repository
@Slf4j
@Component
@Data
@ConfigurationProperties(prefix = "elastic-index-config")
public class OcrESRepository implements IOcrRepository {

    private String INDEX;

    private String TYPE;

    @Autowired
    private JestClient jestClient;

    @Override
    public boolean save(Ocr ocr) {
        Index index = new Index.Builder(ocr).index(INDEX).type(TYPE).build();
        try {
            JestResult jestResult = jestClient.execute(index);
            log.info("save返回结果{}", jestResult.getJsonString());
            return jestResult.isSucceeded();
        } catch (IOException e) {
            log.error("save异常", e);
            return false;
        }
    }

    @Override
    public Page<Ocr> multQuery(String queryString, int pageNo, int size) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        HighlightBuilder highlightBuilder = new HighlightBuilder().field("*").requireFieldMatch(false).tagsSchema("default");
        searchSourceBuilder.highlighter(highlightBuilder);
        QueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(queryString, "id", "ocrText", "textResult.text").type("best_fields");
        searchSourceBuilder.query(queryBuilder).from(from(pageNo, size)).size(size);
        log.debug("搜索DSL:{}", searchSourceBuilder.toString());
        Search search = new Search.Builder(searchSourceBuilder.toString())
                .addIndex(INDEX)
                .addType(TYPE)
                .build();
        try {
            SearchResult result = jestClient.execute(search);
            List<SearchResult.Hit<Ocr, Void>> hits = result.getHits(Ocr.class);
            List<Ocr> ocrs = hits.stream().map(hit -> {
                Ocr ocr = hit.source;
                Map<String, List<String>> highlight = hit.highlight;
                if (highlight.containsKey("id")) {
                    ocr.setId(highlight.get("id").get(0));
                }
                if (highlight.containsKey("ocrText")) {
                    ocr.setPdfUrl(highlight.get("ocrText").get(0));
                }
                return ocr;
            }).collect(toList());
            int took = result.getJsonObject().get("took").getAsInt();
            Page<Ocr> page = Page.<Ocr>builder().list(ocrs).pageNo(pageNo).size(size).total(result.getTotal()).took(took).build();
            return page;
        } catch (IOException e) {
            log.error("search异常", e);
            return null;
        }
    }

    @Override
    public Ocr get(String id) {
        Get get = new Get.Builder(INDEX, id).type(TYPE).build();
        try {
            JestResult result = jestClient.execute(get);
            if (result.isSucceeded())
                return result.getSourceAsObject(Ocr.class);
        } catch (IOException e) {
            log.error("get异常", e);
        }
        return null;
    }

    @Override
    public SearchResult jsonSearch(String json, String indexName, String typeName) {
        Search search = new Search.Builder(json).addIndex(indexName).addType(typeName).build();
        try {
            return jestClient.execute(search);
        } catch (Exception e) {
            log.warn("index:{}, type:{}, search again!! error = {}", indexName, typeName, e.getMessage());
            sleep(100);
            return jsonSearch(json, indexName, typeName);
        }
    }

    @Override
    public Page<Ocr> query(String queryString, int pageNo, int size) {
        String queryJson = "{\n" +
                "  \"query\": {\n" +
                "    \"bool\": {\n" +
                "      \"should\": [\n" +
                "        {\n" +
                "          \"match\": {\n" +
                "            \"ocrText\": \"${queryString}\"\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"nested\": {\n" +
                "            \"path\": \"textResult\",\n" +
                "            \"query\": {\n" +
                "              \"match\": {\n" +
                "                \"textResult.text\": \"${queryString}\"\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  },\n" +
                "  \"_source\": [\"ocrText\",\"pdfUrl\",\"id\"], \n" +
                "  \"highlight\": {\n" +
                "    \"fields\": {\n" +
                "      \"textResult.text\": {},\n" +
                "      \"ocrText\": {}\n" +
                "    }\n" +
                "  }\n" +
                "}";
        queryJson = queryJson.replace("${queryString}", queryString);
        log.info("QueryString: {}" + queryJson);
        Search search = new Search.Builder(queryJson).addIndex(INDEX).addType(TYPE).build();
        SearchResult searchResult = null;
        try {
            searchResult = jestClient.execute(search);

            if (searchResult.isSucceeded()) {
                List<SearchResult.Hit<Ocr, Void>> hits = searchResult.getHits(Ocr.class);
                List<Ocr> ocrs = hits.stream().map(hit -> {
                    Ocr ocr = hit.source;
                    Map<String, List<String>> highlight = hit.highlight;
                    if (highlight.containsKey("ocrText")) {
                        Object arr[] = highlight.get("ocrText").toArray();
                        List<String> ocrTexts = new ArrayList<>();
                        for (Object o : arr) {
                            ocrTexts.add((String) o);
                        }
                        ocr.setHlOcrText(ocrTexts);
                    }
                    if (highlight.containsKey("textResult.text")) {
                        Object arr[] = highlight.get("textResult.text").toArray();
                        List<String> textResults = new ArrayList<>();
                        for (Object o : arr) {
                            textResults.add((String) o);
                        }
                        ocr.setHlTextResult(textResults);
                    }
                    return ocr;
                }).collect(toList());
                int took = searchResult.getJsonObject().get("took").getAsInt();
                Page<Ocr> page = Page.<Ocr>builder().list(ocrs).pageNo(pageNo).size(size).total(searchResult.getTotal()).took(took).build();
                return page;
            } else {
                log.warn("查询失败：index:{}, type:{}, error = {}", INDEX, TYPE, searchResult.getResponseCode());
            }
        } catch (Exception e) {
            log.error("执行查询失败，抛出异常：index:{}, type:{}", INDEX, TYPE);
            return null;
        }

        return null;
    }

    @Override
    public Ocr queryDetail(String id, String queryString) {
        String queryJson = "{\n" +
                "  \"query\": {\n" +
                "    \"bool\": {\n" +
                "      \"must\": [\n" +
                "        {\n" +
                "          \"match\": {\n" +
                "            \"id\": \"${id}\"\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"nested\": {\n" +
                "            \"path\": \"textResult\",\n" +
                "            \"query\": {\n" +
                "              \"match\": {\n" +
                "                \"textResult.text\": \"${queryString}\"\n" +
                "              }\n" +
                "            },\n" +
                "            \"inner_hits\":{\n" +
                "              \"_source\":[\"textResult\"]\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  },\n" +
                "  \"highlight\": {\n" +
                "    \"fields\": {\n" +
                "      \"textResult.text\": {}\n" +
                "    }\n" +
                "  }\n" +
                "}";
        queryJson = queryJson.replace("${queryString}", queryString);
        queryJson = queryJson.replace("${id}", id);
        log.info("QueryString: {}" + queryJson);
        Search search = new Search.Builder(queryJson).addIndex(INDEX).addType(TYPE).build();
        SearchResult searchResult = null;
        try {
            searchResult = jestClient.execute(search);
            if (searchResult.isSucceeded()) {
                List<SearchResult.Hit<Ocr, Void>> hits = searchResult.getHits(Ocr.class);
                List<Ocr> ocrs = hits.stream().map(hit -> {
                    Ocr ocr = hit.source;
                    Map<String, List<String>> highlight = hit.highlight;
                    if (highlight.containsKey("textResult.text")) {
                        Object arr[] = highlight.get("textResult.text").toArray();
                        List<String> textResults = new ArrayList<>();
                        for (Object o : arr) {
                            textResults.add((String) o);
                        }
                        ocr.setHlTextResult(textResults);
                    }
                    return ocr;
                }).collect(toList());
                JsonObject innerHits = searchResult.getJsonObject().get("hits").getAsJsonObject().get("hits").getAsJsonArray().get(0).getAsJsonObject().get("inner_hits").getAsJsonObject();
                JsonArray innerHitsResultArray = innerHits.get("textResult").getAsJsonObject().get("hits").getAsJsonObject().get("hits").getAsJsonArray();
                List<TextResult> textResults = new ArrayList<>();
                for (JsonElement result : innerHitsResultArray) {
                    JsonObject source = result.getAsJsonObject().get("_source").getAsJsonObject();
                    TextResult textResult = new TextResult();
                    textResult.setCharNum(source.get("charNum").getAsInt());
                    textResult.setHandwritten(source.get("isHandwritten").getAsBoolean());
                    textResult.setLeftBottom(source.get("leftBottom").getAsString());
                    textResult.setLeftTop(source.get("leftTop").getAsString());
                    textResult.setRightBottom(source.get("rightBottom").getAsString());
                    textResult.setRightTop(source.get("rightTop").getAsString());
                    textResult.setText(source.get("text").getAsString());
                    textResults.add(textResult);
                }
                Ocr ocr = ocrs.get(0);
                ocr.setTextResult(textResults);
                return ocr;
            } else {
                log.error("执行查询失败，抛出异常：index:{}, type:{}", INDEX, TYPE);
                return null;
            }
        } catch (Exception e) {
            log.error("执行查询失败，抛出异常：index:{}, type:{}", INDEX, TYPE);
            return null;
        }
    }

    @Override
    public JestResult deleteDocumentById(String index, String type, String id) {
        Delete delete = new Delete.Builder(id).index(index).type(type).build();
        JestResult jestResult = null;
        try {
            jestResult = jestClient.execute(delete);
        } catch (IOException e) {
            log.warn("deleteDocumentById again!! error={} id={}", e.getMessage(), id);
            sleep(100);
            deleteDocumentById(index, type, id);
        }
        return jestResult;
    }

    @Override
    public Page<Ocr> getAll(int pageNo, int size) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        Search search = new Search.Builder(searchSourceBuilder.toString())
                .addIndex(INDEX)
                .addType(TYPE)
                .build();
        SearchResult result = null;
        try {
            result = jestClient.execute(search);
            log.info("本次查询共查到：" + result.getTotal() + "个关键字！");
            List<SearchResult.Hit<Ocr, Void>> hits = result.getHits(Ocr.class);
            List<Ocr> ocrs = hits.stream().map(hit -> {
                Ocr ocr = hit.source;
                return ocr;
            }).collect(toList());

            int took = result.getJsonObject().get("took").getAsInt();
            Page<Ocr> page = Page.<Ocr>builder().list(ocrs).pageNo(pageNo).size(size).total(result.getTotal()).took(took).build();
            return page;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int from(int pageNo, int size) {
        return (pageNo - 1) * size < 0 ? 0 : (pageNo - 1) * size;
    }

    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            log.error("Thread sleep failure!! error={}", e.getMessage());
        }
    }
}
