package com.poryoung.elasticsearch.Controller;

import com.poryoung.elasticsearch.Bean.Ocr;
import com.poryoung.elasticsearch.Bean.Page;
import com.poryoung.elasticsearch.repository.impl.OcrESRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Administrator
 * @Title: SearchController
 * @ProjectName elasticsearch-ocr
 * @Description: TODO
 * @date 2019/7/1317:10
 */
@Controller
@Slf4j
public class SearchController {
    @Autowired
    OcrESRepository ocrESRepository;

    @GetMapping("/")
    public String index(){
        return "index";
    }

    @GetMapping("/s")
    public String search(@RequestParam("wd") String wd,
                         @RequestParam(value = "pn", required = false, defaultValue = "1") int pageNo,
                         Model model) {
        log.info("搜索参数wd:{}, pn:{}", wd, pageNo);
        Page<Ocr> page = ocrESRepository.query(wd, pageNo, 10);
        if (page == null) {
            return "Error!";
        }
        model.addAttribute("page", page);
        model.addAttribute("wd", wd);
        return "search";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") String id,
                         @RequestParam("wd") String wd,
                         Model model) {
        Ocr ocr = ocrESRepository.queryDetail(id, wd);
        if (ocr == null) {
            return "404";
        }
        model.addAttribute("ocr", ocr);
        return "detail";
    }
}
