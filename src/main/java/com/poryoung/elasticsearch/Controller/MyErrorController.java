package com.poryoung.elasticsearch.Controller;

import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Administrator
 * @Title: MyErrorController
 * @ProjectName elasticsearch-ocr
 * @Description: TODO
 * @date 2019/7/140:13
 */
@Component
public class MyErrorController implements ErrorViewResolver {
    @Override
    public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status, Map<String, Object> model) {
        ModelAndView mv = new ModelAndView();
        switch (status) {
            case NOT_FOUND:
            case FORBIDDEN:
            case INTERNAL_SERVER_ERROR:
                mv.setViewName("redirect:/404.html");
                break;
        }
        return mv;
    }
}
