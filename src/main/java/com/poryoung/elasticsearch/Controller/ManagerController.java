package com.poryoung.elasticsearch.Controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.poryoung.elasticsearch.Bean.Ocr;
import com.poryoung.elasticsearch.Bean.Page;
import com.poryoung.elasticsearch.Bean.TextResult;
import com.poryoung.elasticsearch.Config.Utils;
import com.poryoung.elasticsearch.repository.impl.OcrESRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 * @Title: ManagerController
 * @ProjectName elasticsearch-ocr
 * @Description: TODO
 * @date 2019/7/1223:36
 */
@Controller
@Slf4j
@Component
@Data
@ConfigurationProperties(prefix = "ocr-file")
public class ManagerController {
    @Autowired
    OcrESRepository ocrESRepository;

    private String OCRPATH;

    @GetMapping("/reader/{pass}")
    public String readJsonFile(@PathVariable("pass") String pass,
                               @RequestParam(value = "ig", required = false, defaultValue = "0") int ig,
                               Model model) throws IOException {
        if (!pass.equals("1234")) {
            return "pass error!";
        }
        if (OCRPATH == null) {
            log.error("读取OCR文件路径失败");
            System.exit(-1);
        }
        String path = OCRPATH;
        List<File> filelist = new ArrayList<File>();
        Utils.getFileList(path, filelist);
        for (File file : filelist) {
            String input = FileUtils.readFileToString(file, "UTF-8");
            JSONObject jsonObject = JSONObject.parseObject(input);
            String filename = file.getName().split("\\.")[0];
            // 判断是否存在该条记录以及是否忽略存在继续插入数据
            if (ig == 0 && ocrESRepository.get(filename) != null) {
                continue;
            }
            Ocr ocr = new Ocr(filename);
            ocr.setOcrText(jsonObject.getString("ocrText"));
            ocr.setPdfUrl(jsonObject.getString("pdfURL"));
            JSONArray jsonArray = jsonObject.getJSONArray("textResult");
            List<TextResult> textResult = new ArrayList<TextResult>();
            for (Object object : jsonArray) {
                TextResult textResultSub = new TextResult();
                JSONObject jsonObjectSub = (JSONObject) object;
                textResultSub.setCharNum(jsonObjectSub.getInteger("charNum"));
                textResultSub.setHandwritten(jsonObjectSub.getBoolean("isHandwritten"));
                textResultSub.setLeftBottom(jsonObjectSub.getString("leftBottom"));
                textResultSub.setLeftTop(jsonObjectSub.getString("leftTop"));
                textResultSub.setRightBottom(jsonObjectSub.getString("rightBottom"));
                textResultSub.setRightTop(jsonObjectSub.getString("rightTop"));
                textResultSub.setText(jsonObjectSub.getString("text"));
                textResult.add(textResultSub);
            }
            log.info("读取文件：" + textResult.toString());
            ocr.setTextResult(textResult);
            ocrESRepository.save(ocr);
        }
        model.addAttribute("infoContent", "读取ocr文件完毕");
        model.addAttribute("infoList", filelist.toArray());
        return "info";
    }

    @GetMapping("/listAll")
    public String listAll(@RequestParam(value = "pn", required = false, defaultValue = "1") int pageNo,
                          Model model) {
        log.info("listAll参数pn:{}", pageNo);
        Page<Ocr> page = ocrESRepository.getAll(pageNo, 10);
        model.addAttribute("page", page);
        return "list";
    }
}
