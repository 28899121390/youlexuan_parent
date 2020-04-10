package com.offcn.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.search.service.ItemSearchService2;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("search2")
public class SearchController2 {

    @Reference
    ItemSearchService2 searchService2;

    @RequestMapping("search")
    public Map search(@RequestBody Map searchMap) {
        return searchService2.search(searchMap);
    }
}
