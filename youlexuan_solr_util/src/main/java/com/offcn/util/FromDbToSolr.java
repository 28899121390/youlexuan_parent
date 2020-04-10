package com.offcn.util;

import com.alibaba.fastjson.JSON;
import com.github.promeg.pinyinhelper.Pinyin;
import com.offcn.mapper.TbItemMapper;
import com.offcn.pojo.TbItem;
import com.offcn.pojo.TbItemExample.Criteria;
import com.offcn.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FromDbToSolr {

    @Autowired
    TbItemMapper tbItemMapper;

    @Autowired
    SolrTemplate solrTemplate;


    public void importItemData() {
        TbItemExample example = new TbItemExample();
        Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");
        List<TbItem> tbItems = tbItemMapper.selectByExample(example);

        for (TbItem tbItem : tbItems) {
            String spec = tbItem.getSpec();
            Map<String,String> map = JSON.parseObject(spec, Map.class);
            Map<String,String> pinyinMap=new HashMap<String, String>();
            for (String key : map.keySet()) {
                //把key转换为拼音
                String pinyinKey = Pinyin.toPinyin(key, "");
                pinyinMap.put(pinyinKey.toLowerCase(),map.get(key));
            }
            tbItem.setSpecMap(pinyinMap);
        }
        solrTemplate.saveBeans(tbItems);
        solrTemplate.commit();

    }

    public static void main(String[] args) {

        ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext-*.xml");
        FromDbToSolr fromDbToSolr = (FromDbToSolr) classPathXmlApplicationContext.getBean("fromDbToSolr");
        fromDbToSolr.importItemData();
    }

}
