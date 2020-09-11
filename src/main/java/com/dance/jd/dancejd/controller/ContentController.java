package com.dance.jd.dancejd.controller;

import com.dance.jd.dancejd.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author ZYGisComputer
 */
@RestController
public class ContentController {

    @Autowired
    private ContentService contentService;

    /**
     * 存储到ES
     * @param keyword
     * @return
     */
    @GetMapping("/parse/{keyword}")
    public boolean parse(@PathVariable String keyword){
        try {
            return contentService.parseContent(keyword);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 检索
     * @param keyword
     * @param pageNo
     * @param pageSize
     * @return
     * @throws IOException
     */
    @GetMapping("/search/{keyword}/{pageNo}/{pageSize}")
    public List<Map<String,Object>> search(@PathVariable String keyword,@PathVariable Integer pageNo,@PathVariable Integer pageSize) throws IOException {
        return contentService.searchPage(keyword,pageNo,pageSize);
    }

}
