package com.macro.mall.portal.dao;

import com.macro.mall.model.SmsFreightTemplate;
import com.macro.mall.model.SmsFreightTemplateRegion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 运费模板DAO
 * Created by AI Assistant
 */
@Mapper
public interface SmsFreightTemplateDao {
    
    /**
     * 根据ID查询运费模板
     */
    SmsFreightTemplate selectByPrimaryKey(Long id);
    
    /**
     * 查询所有运费模板
     */
    List<SmsFreightTemplate> selectAll();
    
    /**
     * 根据省份查询运费模板
     */
    List<SmsFreightTemplate> selectByProvince(@Param("province") String province);
    
    /**
     * 查询默认运费模板
     */
    SmsFreightTemplate selectDefaultTemplate();
    
    /**
     * 获取模板匹配的区域规则
     * @param templateId 运费模板ID
     * @param province 省份
     * @return 匹配的区域规则
     */
    SmsFreightTemplateRegion getMatchingRegion(@Param("templateId") Long templateId, @Param("province") String province);
    
    /**
     * 获取模板的默认区域规则
     * @param templateId 运费模板ID
     * @return 默认区域规则
     */
    SmsFreightTemplateRegion getDefaultRegion(@Param("templateId") Long templateId);
} 