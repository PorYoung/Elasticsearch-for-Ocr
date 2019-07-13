package com.poryoung.elasticsearch;

import com.poryoung.elasticsearch.Config.InitSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class ElasticsearchApplication {
    final
    InitSetting initSetting;

    @Autowired
    public ElasticsearchApplication(InitSetting initSetting) {
        this.initSetting = initSetting;
        initSetting.init();
    }

    public static void main(String[] args) {
        SpringApplication.run(ElasticsearchApplication.class, args);
    }

}
