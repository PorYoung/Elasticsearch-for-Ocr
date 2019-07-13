package com.poryoung.elasticsearch.Config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poryoung.elasticsearch.repository.impl.IndexESRepository;
import io.searchbox.client.JestResult;
import io.searchbox.core.Index;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 * @Title: InitSetting
 * @ProjectName elasticsearch-ocr
 * @Description: TODO
 * @date 2019/7/1219:11
 */
@Component
@Slf4j
@Data
@ConfigurationProperties(prefix = "elastic-index-config")
public class InitSetting {
    @Autowired
    IndexESRepository indexESRepository;
    private String INDEX;
    private String TYPE;
    private String INIT;

    public void init() {
        if (INDEX == null || TYPE == null || INIT == null) {
            log.error("读取配置文件失败");
            System.exit(-1);
        }
        String settingsJson = "{\n" +
                "        \"index\": {\n" +
                "           \"number_of_shards\": 1,\n" +
                "           \"number_of_replicas\": 1\n" +
                "        },\n" +
                "        \"analysis\": {\n" +
                "            \"analyzer\": {\n" +
                "                \"ik\": {\n" +
                "                    \"tokenizer\": \"ik_smart\"\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "    }";
        String mappingsJson = "{\n" +
                "    \"doc\": {\n" +
                "        \"dynamic\": true,\n" +
                "        \"properties\": {\n" +
                "            \"textResult\": {\n" +
                "                \"type\": \"nested\",\n" +
                "                \"properties\": {\n" +
                "                    \"text\": {\n" +
                "                        \"type\": \"text\",\n" +
                "                        \"analyzer\": \"ik_smart\",\n" +
                "                        \"search_analyzer\": \"ik_smart\"\n" +
                "                    }\n" +
                "                }\n" +
                "            },\n" +
                "            \"ocrText\": {\n" +
                "                \"type\": \"text\",\n" +
                "                \"analyzer\": \"ik_smart\",\n" +
                "                \"search_analyzer\": \"ik_smart\"\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";
        if (INIT.equals("initial")) {
            indexESRepository.deleteIndex(INDEX);
            JestResult jestResult = indexESRepository.createIndexMapping(INDEX, settingsJson, mappingsJson);
            if (jestResult.isSucceeded()) {
                log.info("创建索引：" + INDEX + "，类型：" + TYPE);
                indexESRepository.getIndexSettings(INDEX);
            } else {
                log.error("创建索引失败 updateIndexSettings failed");
                indexESRepository.deleteIndex(INDEX);
                System.exit(-1);
            }
        } else if (!indexESRepository.indicesExists(INDEX).isSucceeded()) {
            JestResult jestResult = indexESRepository.createIndexMapping(INDEX, settingsJson, mappingsJson);
            if (jestResult.isSucceeded()) {
                log.info("创建索引：" + INDEX + "，类型：" + TYPE);
                indexESRepository.getIndexSettings(INDEX);
            }else {
                log.error("创建索引失败");
                System.exit(-1);
            }
        } else {
            indexESRepository.getIndexSettings(INDEX);
            log.info("索引已经创建：" + INDEX + "，类型：" + TYPE);
        }
    }
}
