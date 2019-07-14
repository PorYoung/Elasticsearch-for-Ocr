package com.poryoung.elasticsearch.Bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Administrator
 * @Title: TextResult
 * @ProjectName movie-elasticsearch
 * @Description: TODO
 * @date 2019/7/1121:25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextResult {
    private int charNum;
    private boolean isHandwritten;
    private String leftBottom;
    private String leftTop;
    private String rightBottom;
    private String rightTop;
    private String text;

}
