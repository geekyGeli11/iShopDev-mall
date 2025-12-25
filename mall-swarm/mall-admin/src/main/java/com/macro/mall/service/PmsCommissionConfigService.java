package com.macro.mall.service;

import com.macro.mall.dto.*;
import com.macro.mall.model.PmsCommissionConfig;

import java.util.List;
import java.util.Map;

/**
 * 佣金配置服务接口
 */
public interface PmsCommissionConfigService {
    
    /**
     * 分页查询佣金配置列表
     * @param queryParam 查询参数
     * @param pageSize 每页大小
     * @param pageNum 页码
     * @return 佣金配置列表
     */
    List<PmsCommissionConfigVO> list(PmsCommissionConfigQueryParam queryParam, Integer pageSize, Integer pageNum);
    
    /**
     * 获取佣金配置详情
     * @param id 配置ID
     * @return 佣金配置详情
     */
    PmsCommissionConfigVO getDetail(Long id);
    
    /**
     * 创建佣金配置
     * @param configParam 配置参数
     * @return 创建结果
     */
    int create(PmsCommissionConfigParam configParam);
    
    /**
     * 更新佣金配置
     * @param configParam 配置参数
     * @return 更新结果
     */
    int update(PmsCommissionConfigParam configParam);
    
    /**
     * 删除佣金配置
     * @param id 配置ID
     * @return 删除结果
     */
    int delete(Long id);
    
    /**
     * 批量删除佣金配置
     * @param ids 配置ID列表
     * @return 删除结果
     */
    int batchDelete(List<Long> ids);
    
    /**
     * 更新配置状态
     * @param id 配置ID
     * @param isActive 启用状态
     * @return 更新结果
     */
    int updateStatus(Long id, Byte isActive);
    
    /**
     * 批量更新配置状态
     * @param ids 配置ID列表
     * @param isActive 启用状态
     * @return 更新结果
     */
    int batchUpdateStatus(List<Long> ids, Byte isActive);
    
    /**
     * 获取佣金配置统计数据
     * @return 统计数据
     */
    Map<String, Object> getStatistics();
    
    /**
     * 根据商品分类ID获取生效的佣金配置
     * @param productCategoryId 商品分类ID
     * @return 佣金配置信息
     */
    PmsCommissionConfigVO getActiveByCategoryId(Long productCategoryId);
    
    /**
     * 获取全局默认佣金配置
     * @return 佣金配置信息
     */
    PmsCommissionConfigVO getGlobalDefault();
    
    /**
     * 检查配置名称是否重复
     * @param configName 配置名称
     * @param excludeId 排除的配置ID
     * @return 是否重复
     */
    boolean checkConfigNameExists(String configName, Long excludeId);
} 