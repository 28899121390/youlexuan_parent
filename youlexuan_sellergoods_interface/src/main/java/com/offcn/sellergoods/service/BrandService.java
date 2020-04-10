package com.offcn.sellergoods.service;

import java.util.List;
import java.util.Map;

import com.offcn.pojo.TbBrand;

import com.offcn.entity.PageResult;

/**
 * 服务层接口
 *
 * @author Administrator
 */
public interface BrandService {

    //  查询品牌的所有数据
    public List<Map<String, Object>> selectOptionList();

    /**
     * 返回全部列表
     *
     * @return
     */
    public List<TbBrand> findAll();


    /**
     * 返回分页列表
     *
     * @return
     */
    public PageResult findPage(int pageNum, int pageSize);


    /**
     * 增加
     */
    public void add(TbBrand brand);


    /**
     * 修改
     */
    public void update(TbBrand brand);


    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    public TbBrand findOne(Long id);


    /**
     * 批量删除
     *
     * @param ids
     */
    public void delete(Long[] ids);

    /**
     * 分页
     *
     * @param pageNum  当前页 码
     * @param pageSize 每页记录数
     * @return
     */
    public PageResult findPage(TbBrand brand, int pageNum, int pageSize);

}
