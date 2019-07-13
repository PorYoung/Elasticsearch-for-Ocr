package com.poryoung.elasticsearch.repository;

import io.searchbox.client.JestResult;

/**
 * @author Administrator
 * @Title: IIndexRepository
 * @ProjectName elasticsearch-ocr
 * @Description: TODO
 * @date 2019/7/1219:49
 */
public interface IIndexRepository {
    /**
     * 删除索引
     *
     * @param type ：当前删除document名称
     * @return
     */
    JestResult deleteIndex(String type);

    JestResult indicesExists(String index);

    boolean updateIndexSettings(String index);

    boolean getIndexSettings(String index);

    String getMapping(String indexName, String typeName);

    JestResult createIndex(String index);

    //清除缓存
    JestResult clearCache();

    /**
     * 关闭索引
     *
     * @param index ：文档表示的对象类别
     * @return
     */
    JestResult closeIndex(String index);

    //优化索引
    JestResult optimizeIndex();

    //刷新索引
    JestResult flushIndex();

    //查看节点信息
    JestResult nodesInfo();

    //查看集群健康信息
    JestResult health();

    //节点状态
    JestResult nodesStats();

    boolean updateIndexSettings(String index, String json);

    JestResult createIndexMapping(String index, String settinsJson, String mappingsJson);
}
