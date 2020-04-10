package com.offcn.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.offcn.pojo.TbItem;
import com.offcn.search.service.ItemSearchService2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemSearchServiceImpl2 implements ItemSearchService2 {
   @Autowired
    SolrTemplate solrTemplate;


    /**
     * 实现搜索功能
     * @param searchMap
     * @return
     */
    @Override
    public Map search(Map searchMap) {

        Map map=new HashMap();
        String keywords=null;
        if (searchMap!=null){
          keywords=(String) searchMap.get("keywords");
        }
        //查询对象
        Query query=new SimpleQuery();
        //构建查询条件
        Criteria criteria=new Criteria("item_title").is(keywords);
        query.addCriteria(criteria);
        // 参数1 查询对象 参数2 返回值的泛型
        ScoredPage<TbItem> scoredPage = solrTemplate.queryForPage(query, TbItem.class);
        List<TbItem> items = scoredPage.getContent();
        map.put("rows",items);
        return map;
    }
}
