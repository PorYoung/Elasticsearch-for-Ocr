package com.poryoung.elasticsearch.Bean;

import io.searchbox.annotations.JestId;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Administrator
 * @Title: Ocr
 * @ProjectName movie-elasticsearch
 * @Description: TODO
 * @date 2019/7/1121:23
 */
@Data
@NoArgsConstructor
public class Ocr {
    @JestId
    private String id;

    private String ocrText;
    private String pdfUrl;
    private List<TextResult> textResult;

    private List<String> hlOcrText;
    private List<String> hlTextResult;

    public Ocr(String id) {
        this.id = id;
    }

    public Ocr(String id, String ocrText, String pdfUrl, List<TextResult> textResult) {
        this.id = id;
        this.ocrText = ocrText;
        this.pdfUrl = pdfUrl;
        this.textResult = textResult;
    }
}
