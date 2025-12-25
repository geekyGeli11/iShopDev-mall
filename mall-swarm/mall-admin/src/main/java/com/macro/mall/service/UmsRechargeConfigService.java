package com.macro.mall.service;

import com.macro.mall.dto.UmsRechargeConfigParam;
import com.macro.mall.dto.UmsRechargeConfigResult;

import java.util.List;
import java.util.Map;

/**
 * 充值配置管理Service
 */
public interface UmsRechargeConfigService {

    /**
     * 获取充值配置
     * @return 充值配置信息
     */
    UmsRechargeConfigResult getRechargeConfig();

    /**
     * 更新充值配置
     * @param param 配置参数
     * @return 更新结果
     */
    boolean updateRechargeConfig(UmsRechargeConfigParam param);

    /**
     * 获取配置历史记录
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 历史记录列表
     */
    List<Map<String, Object>> getConfigHistory(Integer pageNum, Integer pageSize);

    /**
     * 验证快速充值选项配置
     * @param quickAmounts 快速充值选项列表
     * @return 验证结果
     */
    Map<String, Object> validateQuickAmounts(List<Map<String, Object>> quickAmounts);

    /**
     * 获取充值统计数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计数据
     */
    Map<String, Object> getRechargeStatistics(String startDate, String endDate);

    /**
     * 重置充值配置为默认值
     * @return 重置结果
     */
    boolean resetRechargeConfig();

    /**
     * 预览充值配置效果
     * @param param 配置参数
     * @return 预览结果
     */
    Map<String, Object> previewRechargeConfig(UmsRechargeConfigParam param);

    /**
     * 获取所有配置项
     * @return 配置项列表
     */
    List<Map<String, Object>> getConfigItems();

    /**
     * 更新单个配置项
     * @param configKey 配置键
     * @param configValue 配置值
     * @param configDesc 配置描述
     * @return 更新结果
     */
    boolean updateConfigItem(String configKey, String configValue, String configDesc);

    /**
     * 获取配置项值
     * @param configKey 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    String getConfigValue(String configKey, String defaultValue);

    /**
     * 获取布尔配置项值
     * @param configKey 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    boolean getConfigBoolean(String configKey, boolean defaultValue);
}
