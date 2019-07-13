package com.poryoung.elasticsearch.repository.impl;

import com.google.gson.JsonObject;
import com.poryoung.elasticsearch.repository.IIndexRepository;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.cluster.Health;
import io.searchbox.cluster.NodesInfo;
import io.searchbox.cluster.NodesStats;
import io.searchbox.indices.*;
import io.searchbox.indices.mapping.GetMapping;
import io.searchbox.indices.mapping.PutMapping;
import io.searchbox.indices.settings.GetSettings;
import io.searchbox.indices.settings.UpdateSettings;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * @author Administrator
 * @Title: IndexESRepository
 * @ProjectName elasticsearch-ocr
 * @Description: TODO
 * @date 2019/7/1219:52
 */
@Component
@Slf4j
public class IndexESRepository implements IIndexRepository {

    @Autowired
    private JestClient jestClient;

    @Override
    public JestResult deleteIndex(String index) {
        DeleteIndex deleteIndex = new DeleteIndex.Builder(index).build();
        JestResult result = null;
        try {
            result = jestClient.execute(deleteIndex);
            log.info("deleteIndex == " + result.getJsonString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    /**
     * 创建index
     */
    public JestResult createIndex(String index) {
        JestResult jestResult = null;
        try {
            jestResult = jestClient.execute(new CreateIndex.Builder(index).build());
            log.info("createIndex:{}" + jestResult.isSucceeded());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jestResult;
    }

    @Override
    /**
     * 设置index的mapping（设置数据类型和分词方式）
     */
    public JestResult createIndexMapping(String index,String settinsJson, String mappingsJson) {
        JestResult jestResult = null;
        try {
            CreateIndex createIndex = new CreateIndex.Builder(index)
                    .settings(settinsJson)
                    .mappings(mappingsJson)
                    .build();
            jestResult = jestClient.execute(createIndex);
            log.info("createIndexMapping result:{}" + jestResult.isSucceeded());
            if (!jestResult.isSucceeded()) {
                log.error("settingIndexMapping error:{}" + jestResult.getErrorMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jestResult;
    }

    @Override
    /**
     * 获取index的mapping
     */
    public String getMapping(String indexName, String typeName) {
        GetMapping.Builder builder = new GetMapping.Builder();
        builder.addIndex(indexName).addType(typeName);
        try {
            JestResult result = jestClient.execute(builder.build());
            return result.getSourceAsObject(JsonObject.class).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    /**
     * 获取索引index设置setting
     */
    public boolean getIndexSettings(String index) {
        try {
            JestResult jestResult = jestClient.execute(new GetSettings.Builder().addIndex(index).build());
            log.info(jestResult.getJsonString());
            return jestResult.isSucceeded();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    /**
     * 更改索引index设置setting
     */
    public boolean updateIndexSettings(String index) {
        String source;
        XContentBuilder mapBuilder = null;
        try {
            mapBuilder = XContentFactory.jsonBuilder();
            mapBuilder.startObject().startObject("index").field("max_result_window", "1000000").endObject().endObject();
            source = mapBuilder.toString();
            JestResult jestResult = jestClient.execute(new UpdateSettings.Builder(source).build());
            log.info(jestResult.getJsonString());
            return jestResult.isSucceeded();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateIndexSettings(String index, String json) {
        String source;
        XContentBuilder mapBuilder = null;
        try {
            mapBuilder = XContentFactory.jsonBuilder();
            log.info(json);
            XContentParser parser = parser = XContentFactory.xContent(XContentType.JSON).createParser(NamedXContentRegistry.EMPTY, DeprecationHandler.THROW_UNSUPPORTED_OPERATION, json);
            mapBuilder.copyCurrentStructure(parser);
            source = mapBuilder.toString();
            JestResult jestResult = jestClient.execute(new UpdateSettings.Builder(source).build());
            log.info(jestResult.getJsonString());
            return jestResult.isSucceeded();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public JestResult clearCache() {
        ClearCache closeIndex = new ClearCache.Builder().build();
        JestResult result = null;
        try {
            result = jestClient.execute(closeIndex);
            log.info("clearCache == " + result.getJsonString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public JestResult closeIndex(String index) {
        CloseIndex closeIndex = new CloseIndex.Builder(index).build();
        JestResult result = null;
        try {
            result = jestClient.execute(closeIndex);
            log.info("closeIndex == " + result.getJsonString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public JestResult optimizeIndex() {
        Optimize optimize = new Optimize.Builder().build();
        JestResult result = null;
        try {
            result = jestClient.execute(optimize);
            log.info("optimizeIndex == " + result.getJsonString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public JestResult flushIndex() {
        Flush flush = new Flush.Builder().build();
        JestResult result = null;
        try {
            result = jestClient.execute(flush);
            log.info("flushIndex == " + result.getJsonString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public JestResult indicesExists(String index) {
        IndicesExists indicesExists = new IndicesExists.Builder(index).build();
        JestResult result = null;
        try {
            result = jestClient.execute(indicesExists);
            log.info("indicesExists == " + result.getJsonString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public JestResult nodesInfo() {
        NodesInfo nodesInfo = new NodesInfo.Builder().build();
        JestResult result = null;
        try {
            result = jestClient.execute(nodesInfo);
            log.info("nodesInfo == " + result.getJsonString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public JestResult health() {
        Health health = new Health.Builder().build();
        JestResult result = null;
        try {
            result = jestClient.execute(health);
            log.info("health == " + result.getJsonString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public JestResult nodesStats() {
        NodesStats nodesStats = new NodesStats.Builder().build();
        JestResult result = null;
        try {
            result = jestClient.execute(nodesStats);
            log.info("nodesStats == " + result.getJsonString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
