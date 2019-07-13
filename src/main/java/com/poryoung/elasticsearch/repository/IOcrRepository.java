package com.poryoung.elasticsearch.repository;

import com.poryoung.elasticsearch.Bean.Ocr;
import com.poryoung.elasticsearch.Bean.Page;
import io.searchbox.client.JestResult;
import io.searchbox.core.SearchResult;

public interface IOcrRepository {
    boolean save(Ocr ocr);

    Page<Ocr> query(String queryString, int pageNo, int size);

    Ocr get(String id);

    JestResult deleteDocumentById(String index, String type, String id);

    SearchResult jsonSearch(String json, String indexName, String typeName);

    Page<Ocr> getAll(int pageNo, int size);

}
