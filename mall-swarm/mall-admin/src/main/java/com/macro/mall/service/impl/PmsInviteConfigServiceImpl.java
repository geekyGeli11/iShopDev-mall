package com.macro.mall.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.macro.mall.mapper.PmsSystemConfigMapper;
import com.macro.mall.mapper.PmsSystemConfigHistoryMapper;
import com.macro.mall.model.PmsSystemConfig;
import com.macro.mall.model.PmsSystemConfigExample;
import com.macro.mall.model.PmsSystemConfigHistory;
import com.macro.mall.model.PmsSystemConfigHistoryExample;
import com.macro.mall.service.PmsInviteConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 邀请配置管理Service实现类
 * Created by guanghengzhou on 2024/01/20.
 */
@Service
public class PmsInviteConfigServiceImpl implements PmsInviteConfigService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PmsInviteConfigServiceImpl.class);
    
    @Autowired
    private PmsSystemConfigMapper systemConfigMapper;
    
    @Autowired
    private PmsSystemConfigHistoryMapper configHistoryMapper;
    
    @Override
    public Map<String, Object> getAllInviteConfig() {
        PmsSystemConfigExample example = new PmsSystemConfigExample();
        example.createCriteria().andConfigKeyLike("invite.%");
        List<PmsSystemConfig> configs = systemConfigMapper.selectByExample(example);
        
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> basic = new HashMap<>();
        Map<String, Object> inviterReward = new HashMap<>();
        Map<String, Object> inviteeReward = new HashMap<>();
        Map<String, Object> antiCheat = new HashMap<>();
        
        // 设置默认值，防止配置缺失
        initDefaultValues(basic, inviterReward, inviteeReward, antiCheat);
        
        for (PmsSystemConfig config : configs) {
            String key = config.getConfigKey();
            Object value = parseConfigValue(config);
            
            if (key.startsWith("invite.param.") || key.equals("invite.reward.enable")) {
                // 将点分隔符转换为下划线格式
                String subKey = convertDotToUnderscore(key.substring(7)); // 去掉 "invite." 前缀
                basic.put(subKey, value);
            } else if (key.startsWith("invite.reward.inviter.")) {
                // 将点分隔符转换为下划线格式
                String subKey = convertDotToUnderscore(key.substring(22)); // 去掉 "invite.reward.inviter." 前缀
                inviterReward.put(subKey, value);
            } else if (key.startsWith("invite.reward.invitee.")) {
                // 将点分隔符转换为下划线格式
                String subKey = convertDotToUnderscore(key.substring(22)); // 去掉 "invite.reward.invitee." 前缀
                inviteeReward.put(subKey, value);
            } else if (key.startsWith("invite.anti_cheat.")) {
                String subKey = key.substring(18); // 去掉 "invite.anti_cheat." 前缀
                antiCheat.put(subKey, value);
            }
        }
        
        result.put("basic", basic);
        result.put("inviterReward", inviterReward);
        result.put("inviteeReward", inviteeReward);
        result.put("antiCheat", antiCheat);
        
        LOGGER.info("获取邀请配置成功，返回数据结构：{}", result);
        return result;
    }
    
    /**
     * 初始化默认配置值
     */
    private void initDefaultValues(Map<String, Object> basic, Map<String, Object> inviterReward, 
                                 Map<String, Object> inviteeReward, Map<String, Object> antiCheat) {
        // 基础配置默认值
        basic.put("param_expire_days", 3);
        basic.put("param_max_use_count", 0);
        basic.put("reward_enable", true);
        
        // 邀请者奖励默认值
        inviterReward.put("register_points", 50);
        inviterReward.put("register_desc", "邀请好友注册奖励");
        inviterReward.put("first_order_points", 100);
        inviterReward.put("first_order_cashback_rate", 0.05);
        inviterReward.put("first_order_desc", "邀请好友首单奖励");
        inviterReward.put("repurchase_commission_rate", 0.02);
        inviterReward.put("repurchase_valid_days", 180);
        inviterReward.put("repurchase_desc", "邀请好友复购分佣");
        
        // 被邀请者奖励默认值
        inviteeReward.put("register_points", 100);
        inviteeReward.put("register_coupon", "NEWUSER15");
        inviteeReward.put("register_desc", "新用户注册礼包");
        
        // 防刷机制默认值
        antiCheat.put("max_register_per_ip_daily", 5);
        antiCheat.put("max_register_per_device_daily", 3);
        antiCheat.put("reward_delay_hours", 24);
    }
    
    /**
     * 将点分隔符转换为下划线格式
     */
    private String convertDotToUnderscore(String dotSeparated) {
        return dotSeparated.replace(".", "_");
    }
    
    @Override
    @Transactional
    public int batchUpdateConfig(List<PmsSystemConfig> configs, String reason) {
        if (CollUtil.isEmpty(configs)) {
            return 0;
        }
        
        int updateCount = 0;
        String changeUser = "系统管理员"; // TODO: 从当前登录用户获取
        
        for (PmsSystemConfig config : configs) {
            if (StrUtil.isNotBlank(config.getConfigKey()) && StrUtil.isNotBlank(config.getConfigValue())) {
                int result = updateConfig(config.getConfigKey(), config.getConfigValue(), 
                                        config.getConfigDesc(), changeUser, reason);
                updateCount += result;
            }
        }
        
        LOGGER.info("批量更新邀请配置，更新数量：{}，原因：{}", updateCount, reason);
        return updateCount;
    }
    
    @Override
    public PmsSystemConfig getConfigByKey(String configKey) {
        if (StrUtil.isBlank(configKey)) {
            return null;
        }
        
        PmsSystemConfigExample example = new PmsSystemConfigExample();
        example.createCriteria().andConfigKeyEqualTo(configKey);
        List<PmsSystemConfig> configs = systemConfigMapper.selectByExample(example);
        
        return CollUtil.isNotEmpty(configs) ? configs.get(0) : null;
    }
    
    @Override
    public int updateConfig(String configKey, String configValue, String desc) {
        return updateConfig(configKey, configValue, desc, "系统管理员", "配置更新");
    }
    
    /**
     * 更新配置并记录历史
     */
    public int updateConfig(String configKey, String configValue, String desc, String changeUser, String changeReason) {
        if (StrUtil.isBlank(configKey)) {
            LOGGER.warn("配置键为空，跳过更新：{}", configKey);
            return 0;
        }
        
        // 记录传入的参数用于调试
        LOGGER.info("更新配置 - 键：{}，值：{}，描述：{}，用户：{}，原因：{}", 
                   configKey, configValue, desc, changeUser, changeReason);
        
        PmsSystemConfig existConfig = getConfigByKey(configKey);
        String oldValue = null;
        String operationType;
        int result;
        
        if (existConfig != null) {
            // 更新现有配置
            oldValue = existConfig.getConfigValue();
            LOGGER.info("更新现有配置 - 键：{}，旧值：{}，新值：{}", configKey, oldValue, configValue);
            
            existConfig.setConfigValue(configValue);
            if (StrUtil.isNotBlank(desc)) {
                existConfig.setConfigDesc(desc);
            }
            result = systemConfigMapper.updateByPrimaryKeySelective(existConfig);
            operationType = "UPDATE";
        } else {
            // 创建新配置
            LOGGER.info("创建新配置 - 键：{}，值：{}", configKey, configValue);
            
            PmsSystemConfig newConfig = new PmsSystemConfig();
            newConfig.setConfigKey(configKey);
            newConfig.setConfigValue(configValue);
            newConfig.setConfigDesc(desc);
            newConfig.setConfigType((byte)1); // 默认字符串类型
            newConfig.setIsActive((byte)1);
            newConfig.setCreatedAt(new java.util.Date());
            newConfig.setUpdatedAt(new java.util.Date());
            
            result = systemConfigMapper.insertSelective(newConfig);
            
            // 重新查询获取生成的ID
            if (result > 0) {
                existConfig = getConfigByKey(configKey);
                LOGGER.info("新配置创建成功，ID：{}，键：{}", existConfig != null ? existConfig.getId() : "null", configKey);
            } else {
                existConfig = newConfig;
            }
            operationType = "CREATE";
        }
        
        // 记录配置变更历史
        if (result > 0 && existConfig != null) {
            LOGGER.info("准备记录历史 - 配置ID：{}，键：{}，旧值：{}，新值：{}", 
                       existConfig.getId(), configKey, oldValue, configValue);
            recordConfigHistory(existConfig, oldValue, configValue, changeUser, changeReason, operationType);
        } else {
            LOGGER.warn("配置更新失败或配置对象为空，跳过历史记录 - 结果：{}，配置：{}", result, existConfig);
        }
        
        return result;
    }
    
    /**
     * 记录配置变更历史
     */
    private void recordConfigHistory(PmsSystemConfig config, String oldValue, String newValue, 
                                   String changeUser, String changeReason, String operationType) {
        try {
            LOGGER.info("开始记录配置变更历史 - 配置ID：{}，键：{}，旧值：[{}]，新值：[{}]，操作：{}", 
                       config.getId(), config.getConfigKey(), oldValue, newValue, operationType);
            
            if (config.getId() == null) {
                LOGGER.error("配置ID为空，无法记录历史 - 键：{}", config.getConfigKey());
                return;
            }
            
            PmsSystemConfigHistory history = new PmsSystemConfigHistory();
            history.setConfigId(config.getId());
            history.setConfigKey(config.getConfigKey());
            history.setConfigDesc(config.getConfigDesc());
            history.setOldValue(oldValue);
            history.setNewValue(newValue);
            history.setChangeUser(changeUser);
            history.setChangeReason(determineChangeReason(config.getConfigKey(), changeReason));
            history.setChangeTime(new java.util.Date());
            history.setOperationType(operationType);
            
            LOGGER.info("历史记录对象创建完成 - 配置ID：{}，旧值：[{}]，新值：[{}]", 
                       history.getConfigId(), history.getOldValue(), history.getNewValue());
            
            int result = configHistoryMapper.insertSelective(history);
            if (result > 0) {
                LOGGER.info("配置变更历史记录成功：{} [{}] -> [{}]", config.getConfigKey(), oldValue, newValue);
            } else {
                LOGGER.error("配置变更历史记录失败：{}", config.getConfigKey());
            }
        } catch (Exception e) {
            LOGGER.error("记录配置变更历史异常：{} - 键：{}，旧值：[{}]，新值：[{}]", 
                        e.getMessage(), config.getConfigKey(), oldValue, newValue, e);
        }
    }
    
    /**
     * 根据配置键确定变更原因
     */
    private String determineChangeReason(String configKey, String defaultReason) {
        if (StrUtil.isNotBlank(defaultReason) && !"配置更新".equals(defaultReason)) {
            return defaultReason;
        }
        
        if (configKey.contains("points")) {
            return "积分配置调整";
        } else if (configKey.contains("desc")) {
            return "描述信息更新";
        } else if (configKey.contains("enable")) {
            return "功能开关设置";
        } else if (configKey.contains("expire")) {
            return "有效期配置";
        } else if (configKey.contains("anti_cheat")) {
            return "防刷策略调整";
        } else if (configKey.contains("cashback")) {
            return "返现比例调整";
        } else if (configKey.contains("coupon")) {
            return "优惠券配置";
        } else {
            return "配置更新";
        }
    }
    
    @Override
    public List<Map<String, Object>> getConfigHistory(String configKey, Integer pageNum, Integer pageSize) {
        PmsSystemConfigHistoryExample example = new PmsSystemConfigHistoryExample();
        PmsSystemConfigHistoryExample.Criteria criteria = example.createCriteria();
        
        if (StrUtil.isNotBlank(configKey)) {
            criteria.andConfigKeyEqualTo(configKey);
        } else {
            criteria.andConfigKeyLike("invite.%");
        }
        
        // 按变更时间倒序排列，最新的在前面
        example.setOrderByClause("change_time DESC");
        
        // 启动分页
        PageHelper.startPage(pageNum, pageSize);
        List<PmsSystemConfigHistory> historyList = configHistoryMapper.selectByExample(example);
        
        // 转换为前端需要的格式
        List<Map<String, Object>> result = historyList.stream().map(history -> {
            Map<String, Object> record = new HashMap<>();
            record.put("id", history.getId());
            record.put("configKey", history.getConfigKey());
            record.put("configValue", history.getNewValue());
            record.put("configDesc", history.getConfigDesc());
            record.put("changeTime", history.getChangeTime());
            record.put("changeUser", history.getChangeUser());
            record.put("changeReason", history.getChangeReason());
            record.put("operationType", history.getOperationType());
            record.put("oldValue", history.getOldValue());
            record.put("newValue", history.getNewValue());
            return record;
        }).toList();
        
        LOGGER.info("获取配置历史记录，查询条件：{}，页码：{}，页大小：{}，结果数量：{}", configKey, pageNum, pageSize, result.size());
        return result;
    }
    
    /**
     * 为现有配置创建初始历史记录
     * 在系统首次升级后调用，为所有邀请相关配置创建基础历史记录
     */
    @Transactional
    public int createInitialHistory() {
        PmsSystemConfigExample example = new PmsSystemConfigExample();
        example.createCriteria().andConfigKeyLike("invite.%");
        List<PmsSystemConfig> configs = systemConfigMapper.selectByExample(example);
        
        int count = 0;
        for (PmsSystemConfig config : configs) {
            // 检查是否已经有历史记录
            PmsSystemConfigHistoryExample historyExample = new PmsSystemConfigHistoryExample();
            historyExample.createCriteria().andConfigKeyEqualTo(config.getConfigKey());
            List<PmsSystemConfigHistory> existingHistory = configHistoryMapper.selectByExample(historyExample);
            
            if (existingHistory.isEmpty()) {
                // 创建初始历史记录
                PmsSystemConfigHistory history = new PmsSystemConfigHistory();
                history.setConfigId(config.getId());
                history.setConfigKey(config.getConfigKey());
                history.setConfigDesc(config.getConfigDesc());
                history.setOldValue(null);
                history.setNewValue(config.getConfigValue());
                history.setChangeUser("系统");
                history.setChangeReason("系统初始化");
                history.setChangeTime(config.getCreatedAt() != null ? config.getCreatedAt() : new java.util.Date());
                history.setOperationType("CREATE");
                
                configHistoryMapper.insertSelective(history);
                count++;
            }
        }
        
        LOGGER.info("为现有配置创建初始历史记录，创建数量：{}", count);
        return count;
    }
    
    /**
     * 根据配置类型解析配置值
     */
    private Object parseConfigValue(PmsSystemConfig config) {
        String value = config.getConfigValue();
        Integer type = config.getConfigType() != null ? config.getConfigType().intValue() : null;
        
        if (type == null || type == 1) {
            return value; // 字符串
        } else if (type == 2) {
            try {
                return Integer.parseInt(value); // 数字
            } catch (NumberFormatException e) {
                return value;
            }
        } else if (type == 3) {
            return "1".equals(value) || "true".equalsIgnoreCase(value); // 布尔值
        } else {
            return value; // 其他类型
        }
    }
} 