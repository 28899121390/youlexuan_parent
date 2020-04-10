package com.offcn.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.promeg.pinyinhelper.Pinyin;
import com.offcn.pojo.TbItem;
import com.offcn.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    SolrTemplate solrTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {
        Map<String, Object> map = new HashMap<>();
        //完成高亮查询
        map.putAll(searchHighLight(searchMap));


        return map;
    }


    /**
     * 完成高亮查询
     *
     * @param searchMap
     * @return
     */
    public Map searchHighLight(Map searchMap) {

        Map map = new HashMap();
        SimpleHighlightQuery simpleHighlightQuery = new SimpleHighlightQuery();

        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));

        HighlightOptions highlightOptions = new HighlightOptions();

        highlightOptions.addField("item_title");

        highlightOptions.setSimplePrefix("<font color='red'>");

        highlightOptions.setSimplePostfix("</font>");

        simpleHighlightQuery.setHighlightOptions(highlightOptions);

        simpleHighlightQuery.addCriteria(criteria);

        /*
        *
        *添加过滤查询
        * */

        //如果获取类别不为空
        if (searchMap!=null && !searchMap.get("category").equals("")){
            //拿到类别
            String category = (String) searchMap.get("category");
            Criteria categoryCriteria=new Criteria("item_category").is(category);
            //给过滤查询器添加查询条件
            SimpleFilterQuery simpleFilterQuery=new SimpleFilterQuery(categoryCriteria);
            //将过滤查询 添加的高亮查询
            simpleHighlightQuery.addFilterQuery(simpleFilterQuery);
        }
        //如果获取品牌不为空
        if (searchMap!=null && !searchMap.get("brand").equals("")){
            //拿到品牌
            String brand = (String) searchMap.get("brand");
            Criteria brandCriteria=new Criteria("item_brand").is(brand);
            //给过滤查询器添加查询条件
            SimpleFilterQuery simpleFilterQuery=new SimpleFilterQuery(brandCriteria);
            //将过滤查询 添加的高亮查询
            simpleHighlightQuery.addFilterQuery(simpleFilterQuery);
        }
       if (searchMap!=null && searchMap.get("spec")!=null){
           Map<String,Object> specMap = (Map<String, Object>) searchMap.get("spec");
           for (String key : specMap.keySet()) {
               Criteria criteria1=new Criteria("item_spec_"+Pinyin.toPinyin(key,"").toLowerCase()).is(specMap.get(key));
               SimpleFilterQuery simpleFilterQuery=new SimpleFilterQuery(criteria1);
               simpleHighlightQuery.addFilterQuery(simpleFilterQuery);
           }
       }

        HighlightPage<TbItem> tbItems = solrTemplate.queryForHighlightPage(simpleHighlightQuery, TbItem.class);

        List<TbItem> items = tbItems.getContent();
        for (TbItem item : items) {
            List<HighlightEntry.Highlight> highlights = tbItems.getHighlights(item);
            if (highlights != null && highlights.size() > 0) {
                List<String> snipplets = highlights.get(0).getSnipplets();
                item.setTitle(snipplets.get(0));
            }
        }



        List categoryList = searchCategoryList(searchMap);

        map.put("rows", items);
        map.put("categoryList", categoryList);

        if (searchMap!=null && !searchMap.get("category").equals("")){
            //如果类别不是空
            map.putAll(searchBrandAndSpecList((String) searchMap.get("category")));
        }else {
            if (categoryList != null && categoryList.size() > 0) {
                map.putAll(searchBrandAndSpecList((String) categoryList.get(0)));
            }
        }

        return map;
    }


    private Map searchBrandAndSpecList(String category) {
        Map map = new HashMap();
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);//获取模板ID
        if (typeId != null) {
            //根据模板ID查询品牌列表
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
            map.put("brandList", brandList);//返回值添加品牌列表
            //根据模板ID查询规格列表
            List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
            map.put("specList", specList);
        }
        return map;
    }


    private List searchCategoryList(Map searchMap) {
        List<String> list = new ArrayList();
        Query query = new SimpleQuery();
        //按照关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //设置分组选项  注意商品分类不能设置分词，要不然分组结果会失败
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        //得到分组页
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //根据列得到分组结果集
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        //得到分组结果入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //得到分组入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        for (GroupEntry<TbItem> entry : content) {
            list.add(entry.getGroupValue());//将分组结果的名称封装到返回值中
        }
        return list;
    }


}
