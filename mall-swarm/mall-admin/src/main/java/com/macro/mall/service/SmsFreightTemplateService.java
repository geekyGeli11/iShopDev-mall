package com.macro.mall.service;

import com.macro.mall.dto.*;
import com.macro.mall.model.SmsFreightTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 运费模板Service
 * Created by AI Assistant
 */
public interface SmsFreightTemplateService {
    
    /**
     * 创建运费模板
     */
    @Transactional
    int create(SmsFreightTemplateParam param);
    
    /**
     * 更新运费模板
     */
    @Transactional
    int update(Long id, SmsFreightTemplateParam param);
    
    /**
     * 删除运费模板
     */
    @Transactional
    int delete(Long id);
    
    /**
     * 批量删除运费模板
     */
    @Transactional
    int delete(List<Long> ids);
    
    /**
     * 分页查询运费模板
     */
    List<SmsFreightTemplate> list(SmsFreightTemplateQueryParam queryParam, Integer pageNum, Integer pageSize);
    
    /**
     * 获取运费模板详情
     */
    SmsFreightTemplateVO getTemplateDetail(Long id);
    
    /**
     * 获取所有启用的运费模板
     */
    List<SmsFreightTemplate> listAllEnabled();
    
    /**
     * 修改运费模板状态
     */
    @Transactional
    int updateStatus(List<Long> ids, Byte status);
    
    /**
     * 计算运费
     */
    SmsFreightCalculateResult calculateFreight(SmsFreightCalculateParam param);
    
    /**
     * 获取默认运费模板
     */
    SmsFreightTemplate getDefaultTemplate();
    
    /**
     * 设置默认运费模板
     */
    @Transactional
    int setDefaultTemplate(Long id);
    
    /**
     * 创建默认运费模板（系统初始化时使用）
     */
    @Transactional
    SmsFreightTemplate createDefaultTemplate();
    
    /**
     * 刷新全部商品运费模板
     */
    @Transactional
    int refreshAllProductFreightTemplate(Long templateId);
} 