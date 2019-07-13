package com.poryoung.elasticsearch.Bean;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Page<T> {

    private int pageNo;

    private int size;

    private List<T> list;

    private long total;

    private int took;

    public boolean hasNext() {
        return pageNo * size < total;
    }

    public boolean hasPrevious() {
        return pageNo > 1;
    }

}
