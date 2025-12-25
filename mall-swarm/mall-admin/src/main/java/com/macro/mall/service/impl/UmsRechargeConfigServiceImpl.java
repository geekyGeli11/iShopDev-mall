package com.macro.mall.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.macro.mall.dto.UmsRechargeConfigParam;
import com.macro.mall.dto.UmsRechargeConfigResult;
import com.macro.mall.mapper.UmsBalanceConfigMapper;
import com.macro.mall.mapper.UmsMemberBalanceHistoryMapper;
import com.macro.mall.model.UmsBalanceConfig;
import com.macro.mall.model.UmsBalanceConfigExample;
import com.macro.mall.model.UmsMemberBalanceHistory;
import com.macro.mall.model.UmsMemberBalanceHistoryExample;
import com.macro.mall.service.UmsRechargeConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 充值配置管理Service实现类
 */
@Service
public class UmsRechargeConfigServiceImpl implements UmsRechargeConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UmsRechargeConfigServiceImpl.class);

    @Autowired
    private UmsBalanceConfigMapper balanceConfigMapper;

    @Autowired
    private UmsMemberBalanceHistoryMapper balanceHistoryMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public UmsRechargeConfigResult getRechargeConfig() {
        try {
            UmsRechargeConfigResult config = new UmsRechargeConfigResult();

            // 获取充值金额配置（使用数据库中实际存在的字段）
            config.setMinAmount(new BigDecimal(getConfigValue("recharge.min_amount", "1.00")));
            config.setMaxAmount(new BigDecimal(getConfigValue("recharge.max_amount", "50000.00")));

            // 解析快速充值选项（JSON格式）
            String quickAmountsJson = getConfigValue("recharge.quick_amounts",
                "[{\"amount\":100,\"bonusBalance\":0.00,\"bonusIntegration\":0,\"description\":\"充值100元无赠送\"}]");

            List<UmsRechargeConfigResult.QuickAmountOption> quickAmountOptions = parseQuickAmounts(quickAmountsJson);
            config.setQuickAmounts(quickAmountOptions);

            // 获取赠送相关配置（使用数据库中实际存在的字段）
            config.setEnableBonus(getConfigBoolean("recharge.enable_bonus", true));
            config.setBonusBalanceRate(new BigDecimal(getConfigValue("recharge.bonus_balance_rate", "0.00")));
            config.setBonusIntegrationRate(new BigDecimal(getConfigValue("recharge.bonus_integration_rate", "0.00")));
            config.setBonusMinAmount(new BigDecimal(getConfigValue("recharge.bonus_min_amount", "50.00")));

            // 获取功能开关配置（只使用数据库中实际存在的字段）
            config.setEnableConsume(getConfigBoolean("balance.enable_consume", true));
            // 注意：balance.enable_recharge 在数据库中不存在，设为默认值
            config.setEnableRecharge(true);

            // 配置元信息设为默认值（这些字段已从DTO中移除）

            return config;
        } catch (Exception e) {
            LOGGER.error("获取充值配置失败", e);
            throw new RuntimeException("获取充值配置失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRechargeConfig(UmsRechargeConfigParam param) {
        try {
            // 验证参数
            validateConfigParam(param);

            // 更新基础配置（只更新数据库中实际存在的字段）
            if (param.getMinAmount() != null) {
                updateConfigItem("recharge.min_amount", param.getMinAmount().toString(), "最小充值金额");
            }
            if (param.getMaxAmount() != null) {
                updateConfigItem("recharge.max_amount", param.getMaxAmount().toString(), "最大充值金额");
            }

            // 更新快速充值选项
            if (param.getQuickAmounts() != null) {
                String quickAmountsJson = convertQuickAmountsToJson(param.getQuickAmounts());
                updateConfigItem("recharge.quick_amounts", quickAmountsJson, "快速充值选项配置");
            }

            // 更新赠送配置（只更新数据库中实际存在的字段）
            if (param.getEnableBonus() != null) {
                updateConfigItem("recharge.enable_bonus", param.getEnableBonus() ? "1" : "0", "是否启用赠送功能");
            }
            if (param.getBonusBalanceRate() != null) {
                updateConfigItem("recharge.bonus_balance_rate", param.getBonusBalanceRate().toString(), "赠送余额比例");
            }
            if (param.getBonusIntegrationRate() != null) {
                updateConfigItem("recharge.bonus_integration_rate", param.getBonusIntegrationRate().toString(), "赠送积分比例");
            }
            if (param.getBonusMinAmount() != null) {
                updateConfigItem("recharge.bonus_min_amount", param.getBonusMinAmount().toString(), "享受赠送的最小充值金额");
            }

            // 更新功能开关（只更新数据库中实际存在的字段）
            if (param.getEnableConsume() != null) {
                updateConfigItem("balance.enable_consume", param.getEnableConsume() ? "1" : "0", "是否启用消费功能");
            }
            // 注意：balance.enable_recharge 在数据库中不存在，跳过更新

            // 跳过版本号和更新信息的更新（这些字段在数据库中不存在）

            return true;
        } catch (Exception e) {
            LOGGER.error("更新充值配置失败", e);
            throw new RuntimeException("更新充值配置失败: " + e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> getConfigHistory(Integer pageNum, Integer pageSize) {
        try {
            PageHelper.startPage(pageNum, pageSize);

            UmsBalanceConfigExample example = new UmsBalanceConfigExample();
            // 只查询实际存在的配置项
            example.createCriteria().andConfigKeyIn(Arrays.asList(
                "recharge.min_amount",
                "recharge.max_amount",
                "recharge.quick_amounts",
                "recharge.enable_bonus",
                "recharge.bonus_balance_rate",
                "recharge.bonus_integration_rate",
                "recharge.bonus_min_amount",
                "balance.enable_consume"
            ));
            example.setOrderByClause("update_time DESC");

            List<UmsBalanceConfig> configs = balanceConfigMapper.selectByExample(example);

            return configs.stream().map(config -> {
                Map<String, Object> item = new HashMap<>();
                item.put("id", config.getId());
                item.put("configKey", config.getConfigKey());
                item.put("configValue", config.getConfigValue());
                item.put("configDesc", config.getConfigDesc());
                item.put("status", config.getStatus());
                item.put("createTime", config.getCreateTime());
                item.put("updateTime", config.getUpdateTime());
                return item;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error("获取配置历史失败", e);
            throw new RuntimeException("获取配置历史失败: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> validateQuickAmounts(List<Map<String, Object>> quickAmounts) {
        Map<String, Object> result = new HashMap<>();
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        try {
            if (quickAmounts == null || quickAmounts.isEmpty()) {
                errors.add("快速充值选项不能为空");
            } else {
                Set<BigDecimal> amounts = new HashSet<>();
                
                for (int i = 0; i < quickAmounts.size(); i++) {
                    Map<String, Object> option = quickAmounts.get(i);
                    
                    // 验证金额
                    Object amountObj = option.get("amount");
                    if (amountObj == null) {
                        errors.add("第" + (i + 1) + "个选项缺少金额");
                        continue;
                    }
                    
                    BigDecimal amount = new BigDecimal(amountObj.toString());
                    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                        errors.add("第" + (i + 1) + "个选项金额必须大于0");
                    }
                    
                    if (amounts.contains(amount)) {
                        errors.add("第" + (i + 1) + "个选项金额重复：" + amount);
                    }
                    amounts.add(amount);
                    
                    // 验证赠送金额
                    Object bonusBalanceObj = option.get("bonusBalance");
                    if (bonusBalanceObj != null) {
                        BigDecimal bonusBalance = new BigDecimal(bonusBalanceObj.toString());
                        if (bonusBalance.compareTo(BigDecimal.ZERO) < 0) {
                            errors.add("第" + (i + 1) + "个选项赠送余额不能为负数");
                        }
                        
                        if (bonusBalance.compareTo(amount) > 0) {
                            warnings.add("第" + (i + 1) + "个选项赠送余额大于充值金额，请确认");
                        }
                    }
                    
                    // 验证赠送积分
                    Object bonusIntegrationObj = option.get("bonusIntegration");
                    if (bonusIntegrationObj != null) {
                        Integer bonusIntegration = Integer.valueOf(bonusIntegrationObj.toString());
                        if (bonusIntegration < 0) {
                            errors.add("第" + (i + 1) + "个选项赠送积分不能为负数");
                        }
                    }
                }
            }
            
            result.put("valid", errors.isEmpty());
            result.put("errors", errors);
            result.put("warnings", warnings);
            
        } catch (Exception e) {
            LOGGER.error("验证快速充值选项失败", e);
            result.put("valid", false);
            result.put("errors", Arrays.asList("验证失败：" + e.getMessage()));
            result.put("warnings", new ArrayList<>());
        }
        
        return result;
    }

    @Override
    public Map<String, Object> getRechargeStatistics(String startDate, String endDate) {
        try {
            Map<String, Object> statistics = new HashMap<>();

            UmsMemberBalanceHistoryExample example = new UmsMemberBalanceHistoryExample();
            UmsMemberBalanceHistoryExample.Criteria criteria = example.createCriteria();
            criteria.andBusinessTypeIn(Arrays.asList("recharge", "recharge_bonus"));

            // 添加时间范围条件
            if (StringUtils.hasText(startDate)) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date start = sdf.parse(startDate);
                    criteria.andCreateTimeGreaterThanOrEqualTo(start);
                } catch (Exception e) {
                    LOGGER.warn("解析开始时间失败: {}", startDate, e);
                }
            }

            if (StringUtils.hasText(endDate)) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date end = sdf.parse(endDate + " 23:59:59");
                    criteria.andCreateTimeLessThanOrEqualTo(end);
                } catch (Exception e) {
                    LOGGER.warn("解析结束时间失败: {}", endDate, e);
                }
            }

            List<UmsMemberBalanceHistory> historyList = balanceHistoryMapper.selectByExample(example);

            // 统计数据
            BigDecimal totalRechargeAmount = BigDecimal.ZERO;
            BigDecimal totalBonusAmount = BigDecimal.ZERO;
            int totalRechargeCount = 0;
            int totalBonusCount = 0;

            for (UmsMemberBalanceHistory history : historyList) {
                if ("recharge".equals(history.getBusinessType())) {
                    totalRechargeAmount = totalRechargeAmount.add(history.getAmount());
                    totalRechargeCount++;
                } else if ("recharge_bonus".equals(history.getBusinessType())) {
                    totalBonusAmount = totalBonusAmount.add(history.getAmount());
                    totalBonusCount++;
                }
            }

            statistics.put("totalRechargeAmount", totalRechargeAmount);
            statistics.put("totalBonusAmount", totalBonusAmount);
            statistics.put("totalRechargeCount", totalRechargeCount);
            statistics.put("totalBonusCount", totalBonusCount);
            statistics.put("averageRechargeAmount", totalRechargeCount > 0 ?
                totalRechargeAmount.divide(new BigDecimal(totalRechargeCount), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);

            return statistics;
        } catch (Exception e) {
            LOGGER.error("获取充值统计数据失败", e);
            throw new RuntimeException("获取充值统计数据失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resetRechargeConfig() {
        try {
            // 重置为默认配置（只更新数据库中实际存在的字段）
            updateConfigItem("recharge.min_amount", "1.00", "最小充值金额");
            updateConfigItem("recharge.max_amount", "50000.00", "最大充值金额");
            updateConfigItem("recharge.quick_amounts",
                "[{\"amount\":100,\"bonusBalance\":0.00,\"bonusIntegration\":0,\"description\":\"充值100元无赠送\"}," +
                "{\"amount\":200,\"bonusBalance\":10.00,\"bonusIntegration\":20,\"description\":\"充值200元赠送10元+20积分\"}," +
                "{\"amount\":500,\"bonusBalance\":50.00,\"bonusIntegration\":100,\"description\":\"充值500元赠送50元+100积分\"}]",
                "快速充值选项配置");
            updateConfigItem("recharge.enable_bonus", "1", "是否启用赠送功能");
            updateConfigItem("recharge.bonus_balance_rate", "0.00", "赠送余额比例");
            updateConfigItem("recharge.bonus_integration_rate", "0.00", "赠送积分比例");
            updateConfigItem("recharge.bonus_min_amount", "50.00", "享受赠送的最小充值金额");
            updateConfigItem("balance.enable_consume", "1", "是否启用消费功能");

            // 跳过不存在的字段：balance.enable_recharge 和版本管理相关字段

            return true;
        } catch (Exception e) {
            LOGGER.error("重置充值配置失败", e);
            throw new RuntimeException("重置充值配置失败: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> previewRechargeConfig(UmsRechargeConfigParam param) {
        try {
            Map<String, Object> preview = new HashMap<>();

            // 预览基础信息
            preview.put("minAmount", param.getMinAmount());
            preview.put("maxAmount", param.getMaxAmount());
            preview.put("enableBonus", param.getEnableBonus());
            preview.put("enableRecharge", param.getEnableRecharge());
            preview.put("enableConsume", param.getEnableConsume());

            // 预览快速充值选项
            if (param.getQuickAmounts() != null) {
                List<Map<String, Object>> previewOptions = new ArrayList<>();

                for (UmsRechargeConfigParam.QuickAmountOption option : param.getQuickAmounts()) {
                    Map<String, Object> previewOption = new HashMap<>();
                    previewOption.put("amount", option.getAmount());
                    previewOption.put("bonusBalance", option.getBonusBalance());
                    previewOption.put("bonusIntegration", option.getBonusIntegration());
                    previewOption.put("description", option.getDescription());
                    previewOption.put("enabled", option.getEnabled());

                    // 计算总价值
                    BigDecimal totalValue = option.getAmount();
                    if (option.getBonusBalance() != null) {
                        totalValue = totalValue.add(option.getBonusBalance());
                    }
                    previewOption.put("totalValue", totalValue);

                    // 计算优惠比例
                    if (option.getBonusBalance() != null && option.getBonusBalance().compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal discountRate = option.getBonusBalance().divide(option.getAmount(), 4, RoundingMode.HALF_UP);
                        previewOption.put("discountRate", discountRate.multiply(new BigDecimal("100")));
                    } else {
                        previewOption.put("discountRate", BigDecimal.ZERO);
                    }

                    previewOptions.add(previewOption);
                }

                preview.put("quickAmounts", previewOptions);
            }

            // 预览配置影响
            List<String> impacts = new ArrayList<>();
            if (param.getEnableRecharge() != null && !param.getEnableRecharge()) {
                impacts.add("关闭充值功能将影响用户充值");
            }
            if (param.getEnableConsume() != null && !param.getEnableConsume()) {
                impacts.add("关闭消费功能将影响用户使用余额支付");
            }
            if (param.getEnableBonus() != null && !param.getEnableBonus()) {
                impacts.add("关闭赠送功能将取消所有充值奖励");
            }

            preview.put("impacts", impacts);
            preview.put("previewTime", new Date());

            return preview;
        } catch (Exception e) {
            LOGGER.error("预览充值配置失败", e);
            throw new RuntimeException("预览充值配置失败: " + e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> getConfigItems() {
        try {
            UmsBalanceConfigExample example = new UmsBalanceConfigExample();
            // 只查询实际存在的配置项
            example.createCriteria().andConfigKeyIn(Arrays.asList(
                "recharge.min_amount",
                "recharge.max_amount",
                "recharge.quick_amounts",
                "recharge.enable_bonus",
                "recharge.bonus_balance_rate",
                "recharge.bonus_integration_rate",
                "recharge.bonus_min_amount",
                "balance.enable_consume"
            )).andStatusEqualTo((byte) 1);
            example.setOrderByClause("config_key ASC");

            List<UmsBalanceConfig> configs = balanceConfigMapper.selectByExample(example);

            return configs.stream().map(config -> {
                Map<String, Object> item = new HashMap<>();
                item.put("id", config.getId());
                item.put("configKey", config.getConfigKey());
                item.put("configValue", config.getConfigValue());
                item.put("configDesc", config.getConfigDesc());
                item.put("status", config.getStatus());
                item.put("createTime", config.getCreateTime());
                item.put("updateTime", config.getUpdateTime());
                return item;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error("获取配置项失败", e);
            throw new RuntimeException("获取配置项失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateConfigItem(String configKey, String configValue, String configDesc) {
        try {
            UmsBalanceConfigExample example = new UmsBalanceConfigExample();
            example.createCriteria().andConfigKeyEqualTo(configKey);
            List<UmsBalanceConfig> configs = balanceConfigMapper.selectByExample(example);

            if (configs.isEmpty()) {
                // 创建新配置
                UmsBalanceConfig config = new UmsBalanceConfig();
                config.setConfigKey(configKey);
                config.setConfigValue(configValue);
                config.setConfigDesc(configDesc);
                config.setStatus((byte) 1);
                config.setCreateTime(new Date());
                config.setUpdateTime(new Date());

                return balanceConfigMapper.insertSelective(config) > 0;
            } else {
                // 更新现有配置
                UmsBalanceConfig config = configs.get(0);
                config.setConfigValue(configValue);
                if (StringUtils.hasText(configDesc)) {
                    config.setConfigDesc(configDesc);
                }
                config.setUpdateTime(new Date());

                return balanceConfigMapper.updateByPrimaryKeySelective(config) > 0;
            }
        } catch (Exception e) {
            LOGGER.error("更新配置项失败, key: {}, value: {}", configKey, configValue, e);
            throw new RuntimeException("更新配置项失败: " + e.getMessage());
        }
    }

    @Override
    public String getConfigValue(String configKey, String defaultValue) {
        try {
            UmsBalanceConfigExample example = new UmsBalanceConfigExample();
            example.createCriteria().andConfigKeyEqualTo(configKey).andStatusEqualTo((byte) 1);
            List<UmsBalanceConfig> configs = balanceConfigMapper.selectByExample(example);

            if (!configs.isEmpty()) {
                return configs.get(0).getConfigValue();
            }
        } catch (Exception e) {
            LOGGER.warn("获取配置失败, key: {}", configKey, e);
        }
        return defaultValue;
    }

    @Override
    public boolean getConfigBoolean(String configKey, boolean defaultValue) {
        String value = getConfigValue(configKey, String.valueOf(defaultValue));
        return "1".equals(value) || "true".equalsIgnoreCase(value);
    }

    /**
     * 解析快速充值选项JSON配置
     * 兼容数据库存储格式（只包含基础字段）
     */
    private List<UmsRechargeConfigResult.QuickAmountOption> parseQuickAmounts(String quickAmountsJson) {
        try {
            TypeReference<List<Map<String, Object>>> typeRef = new TypeReference<List<Map<String, Object>>>() {};
            List<Map<String, Object>> jsonList = objectMapper.readValue(quickAmountsJson, typeRef);

            AtomicInteger index = new AtomicInteger(0);
            return jsonList.stream().map(map -> {
                UmsRechargeConfigResult.QuickAmountOption option = new UmsRechargeConfigResult.QuickAmountOption();
                option.setAmount(new BigDecimal(map.get("amount").toString()));
                option.setBonusBalance(new BigDecimal(map.getOrDefault("bonusBalance", "0.00").toString()));
                option.setBonusIntegration(Integer.valueOf(map.getOrDefault("bonusIntegration", "0").toString()));
                option.setDescription(map.getOrDefault("description", "").toString());

                // 管理字段：从数据库JSON中读取（如果存在），否则使用默认值
                option.setEnabled(Boolean.valueOf(map.getOrDefault("enabled", "true").toString()));
                option.setSort(Integer.valueOf(map.getOrDefault("sort", String.valueOf(index.incrementAndGet())).toString()));

                return option;
            }).collect(Collectors.toList());

        } catch (Exception e) {
            LOGGER.warn("解析快速充值选项JSON失败，使用默认配置: {}", quickAmountsJson, e);

            // 返回默认配置
            UmsRechargeConfigResult.QuickAmountOption defaultOption = new UmsRechargeConfigResult.QuickAmountOption();
            defaultOption.setAmount(new BigDecimal("100"));
            defaultOption.setBonusBalance(BigDecimal.ZERO);
            defaultOption.setBonusIntegration(0);
            defaultOption.setDescription("充值100元无赠送");
            defaultOption.setEnabled(true);
            defaultOption.setSort(1);

            return Arrays.asList(defaultOption);
        }
    }

    /**
     * 将快速充值选项转换为JSON
     * 只存储基础字段到数据库，保持与mall-portal格式一致
     */
    private String convertQuickAmountsToJson(List<UmsRechargeConfigParam.QuickAmountOption> quickAmounts) {
        try {
            // 过滤掉未启用的选项，并按sort排序
            List<UmsRechargeConfigParam.QuickAmountOption> enabledOptions = quickAmounts.stream()
                .filter(option -> option.getEnabled() == null || option.getEnabled())
                .sorted((a, b) -> {
                    int sortA = a.getSort() != null ? a.getSort() : 999;
                    int sortB = b.getSort() != null ? b.getSort() : 999;
                    return Integer.compare(sortA, sortB);
                })
                .collect(Collectors.toList());

            List<Map<String, Object>> jsonList = enabledOptions.stream().map(option -> {
                Map<String, Object> map = new HashMap<>();
                // 只存储基础字段，与mall-portal格式保持一致
                map.put("amount", option.getAmount());
                map.put("bonusBalance", option.getBonusBalance() != null ? option.getBonusBalance() : BigDecimal.ZERO);
                map.put("bonusIntegration", option.getBonusIntegration() != null ? option.getBonusIntegration() : 0);
                map.put("description", option.getDescription() != null ? option.getDescription() : "");
                return map;
            }).collect(Collectors.toList());

            return objectMapper.writeValueAsString(jsonList);
        } catch (Exception e) {
            LOGGER.error("转换快速充值选项为JSON失败", e);
            throw new RuntimeException("转换快速充值选项为JSON失败: " + e.getMessage());
        }
    }

    /**
     * 验证配置参数
     */
    private void validateConfigParam(UmsRechargeConfigParam param) {
        if (param.getMinAmount() != null && param.getMaxAmount() != null) {
            if (param.getMinAmount().compareTo(param.getMaxAmount()) > 0) {
                throw new IllegalArgumentException("最小充值金额不能大于最大充值金额");
            }
        }

        if (param.getMinAmount() != null && param.getMinAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("最小充值金额必须大于0");
        }

        if (param.getMaxAmount() != null && param.getMaxAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("最大充值金额必须大于0");
        }

        if (param.getBonusBalanceRate() != null && param.getBonusBalanceRate().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("赠送余额比例不能为负数");
        }

        if (param.getBonusIntegrationRate() != null && param.getBonusIntegrationRate().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("赠送积分比例不能为负数");
        }

        if (param.getBonusMinAmount() != null && param.getBonusMinAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("享受赠送的最小充值金额不能为负数");
        }
    }
}
