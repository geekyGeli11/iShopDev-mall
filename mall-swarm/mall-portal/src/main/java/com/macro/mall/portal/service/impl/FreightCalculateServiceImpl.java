package com.macro.mall.portal.service.impl;

import com.macro.mall.mapper.SmsFreightTemplateMapper;
import com.macro.mall.mapper.SmsFreightTemplateRegionMapper;
import com.macro.mall.model.SmsFreightTemplate;
import com.macro.mall.model.SmsFreightTemplateExample;
import com.macro.mall.model.SmsFreightTemplateRegion;
import com.macro.mall.portal.dao.SmsFreightTemplateDao;
import com.macro.mall.portal.dto.SmsFreightCalculateParam;
import com.macro.mall.portal.dto.SmsFreightCalculateResult;
import com.macro.mall.portal.service.FreightCalculateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 运费计算服务实现类（本地实现）
 * Created by AI Assistant
 */
@Service
public class FreightCalculateServiceImpl implements FreightCalculateService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FreightCalculateServiceImpl.class);
    
    @Autowired
    private SmsFreightTemplateMapper freightTemplateMapper;
    
    @Autowired
    private SmsFreightTemplateRegionMapper freightTemplateRegionMapper;
    
    @Autowired
    private SmsFreightTemplateDao freightTemplateDao;
    
    @Override
    public SmsFreightCalculateResult calculateFreight(SmsFreightCalculateParam param) {
        try {
            LOGGER.info("开始计算运费，参数: {}", param);
            
            SmsFreightCalculateResult result = new SmsFreightCalculateResult();
            result.setDetails(new ArrayList<>());
            
            // 首先计算整个订单的总金额、总件数、总重量，用于判断整单包邮条件
            BigDecimal orderTotalAmount = BigDecimal.ZERO;
            BigDecimal orderTotalCount = BigDecimal.ZERO;
            BigDecimal orderTotalWeight = BigDecimal.ZERO;
            BigDecimal orderTotalVolume = BigDecimal.ZERO;
            
            for (SmsFreightCalculateParam.FreightCalculateItemParam item : param.getItems()) {
                orderTotalCount = orderTotalCount.add(new BigDecimal(item.getQuantity()));
                if (item.getPrice() != null) {
                    orderTotalAmount = orderTotalAmount.add(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
                }
                if (item.getWeight() != null) {
                    orderTotalWeight = orderTotalWeight.add(item.getWeight().multiply(new BigDecimal(item.getQuantity())));
                }
                if (item.getVolume() != null) {
                    orderTotalVolume = orderTotalVolume.add(item.getVolume().multiply(new BigDecimal(item.getQuantity())));
                }
            }
            
            LOGGER.info("订单总金额: {}, 总件数: {}, 总重量: {}kg", orderTotalAmount, orderTotalCount, orderTotalWeight);
            
            // 获取默认运费模板，检查整单是否满足包邮条件
            SmsFreightTemplate defaultTemplate = getDefaultTemplate();
            if (checkFreeShipping(defaultTemplate, orderTotalCount, orderTotalWeight, orderTotalVolume, orderTotalAmount)) {
                String freeReason = getFreeShippingReason(defaultTemplate, orderTotalCount, orderTotalWeight, orderTotalVolume, orderTotalAmount);
                result.setTotalFreight(BigDecimal.ZERO);
                result.setIsFreeShipping(true);
                result.setFreeShippingReason(freeReason);
                LOGGER.info("整单满足包邮条件，原因: {}", freeReason);
                return result;
            }
            
            // 不满足整单包邮条件，按运费模板分组计算运费
            Map<Long, List<SmsFreightCalculateParam.FreightCalculateItemParam>> templateGroups = 
                param.getItems().stream()
                    .collect(Collectors.groupingBy(item -> 
                        item.getFreightTemplateId() != null ? item.getFreightTemplateId() : defaultTemplate.getId()));
            
            BigDecimal totalFreight = BigDecimal.ZERO;
            boolean hasFreeShipping = false;
            List<String> freeShippingReasons = new ArrayList<>();
            
            for (Map.Entry<Long, List<SmsFreightCalculateParam.FreightCalculateItemParam>> entry : templateGroups.entrySet()) {
                Long templateId = entry.getKey();
                List<SmsFreightCalculateParam.FreightCalculateItemParam> items = entry.getValue();
                
                SmsFreightTemplate template = freightTemplateMapper.selectByPrimaryKey(templateId);
                if (template == null || template.getStatus() != 1) {
                    template = defaultTemplate;
                }
                
                SmsFreightCalculateResult.FreightCalculateDetail detail = calculateTemplateFreight(template, items, param);
                result.getDetails().add(detail);
                
                if (detail.getIsFree()) {
                    hasFreeShipping = true;
                    freeShippingReasons.add(detail.getFreeReason());
                } else {
                    totalFreight = totalFreight.add(detail.getTemplateFreight());
                }
            }
            
            result.setTotalFreight(totalFreight);
            result.setIsFreeShipping(hasFreeShipping && totalFreight.compareTo(BigDecimal.ZERO) == 0);
            if (!freeShippingReasons.isEmpty()) {
                result.setFreeShippingReason(String.join(", ", freeShippingReasons));
            }
            
            LOGGER.info("运费计算完成，总运费: {}, 是否包邮: {}", result.getTotalFreight(), result.getIsFreeShipping());
            return result;
            
        } catch (Exception e) {
            LOGGER.error("本地运费计算异常", e);
            // 返回默认运费
            return createDefaultFreightResult();
        }
    }
    
    /**
     * 获取默认运费模板
     */
    private SmsFreightTemplate getDefaultTemplate() {
        SmsFreightTemplateExample example = new SmsFreightTemplateExample();
        example.createCriteria().andIsDefaultEqualTo(true).andStatusEqualTo((byte) 1);
        List<SmsFreightTemplate> templates = freightTemplateMapper.selectByExample(example);
        
        if (!templates.isEmpty()) {
            return templates.get(0);
        }
        
        // 如果没有默认模板，查找第一个启用的模板设为默认
        SmsFreightTemplateExample enabledExample = new SmsFreightTemplateExample();
        enabledExample.createCriteria().andStatusEqualTo((byte) 1);
        enabledExample.setOrderByClause("id asc");
        List<SmsFreightTemplate> enabledTemplates = freightTemplateMapper.selectByExample(enabledExample);
        
        if (!enabledTemplates.isEmpty()) {
            SmsFreightTemplate firstTemplate = enabledTemplates.get(0);
            // 设置第一个模板为默认模板
            firstTemplate.setIsDefault(true);
            freightTemplateMapper.updateByPrimaryKeySelective(firstTemplate);
            return firstTemplate;
        }
        
        // 如果不存在任何启用的模板，创建一个默认模板
        return createDefaultTemplate();
    }
    
    /**
     * 创建默认运费模板
     */
    private SmsFreightTemplate createDefaultTemplate() {
        SmsFreightTemplate defaultTemplate = new SmsFreightTemplate();
        defaultTemplate.setName("默认运费模板");
        defaultTemplate.setChargeType((byte) 1); // 按件数计费
        defaultTemplate.setDeliveryType((byte) 1); // 快递配送
        defaultTemplate.setFreeType((byte) 0); // 不包邮
        defaultTemplate.setFreeAmount(BigDecimal.ZERO);
        defaultTemplate.setFreeCount(0);
        defaultTemplate.setFreeWeight(BigDecimal.ZERO);
        defaultTemplate.setStatus((byte) 1);
        defaultTemplate.setIsDefault(true); // 设置为默认模板
        defaultTemplate.setSort(0);
        defaultTemplate.setRemark("系统默认运费模板");
        defaultTemplate.setCreateTime(new Date());
        defaultTemplate.setUpdateTime(new Date());
        
        freightTemplateMapper.insertSelective(defaultTemplate);
        
        // 创建默认区域规则（全国统一运费）
        SmsFreightTemplateRegion defaultRegion = new SmsFreightTemplateRegion();
        defaultRegion.setTemplateId(defaultTemplate.getId());
        defaultRegion.setRegionType((byte) 1); // 省份
        defaultRegion.setRegionIds(null); // 全国通用规则，region_ids 为 null
        defaultRegion.setRegionNames("全国");
        defaultRegion.setFirstCount(BigDecimal.ONE);
        defaultRegion.setFirstAmount(new BigDecimal("10.00")); // 首件10元
        defaultRegion.setAdditionalCount(BigDecimal.ONE);
        defaultRegion.setAdditionalAmount(new BigDecimal("5.00")); // 续件5元
        defaultRegion.setSort(0);
        
        freightTemplateRegionMapper.insertSelective(defaultRegion);
        
        LOGGER.info("创建默认运费模板成功，模板ID: {}", defaultTemplate.getId());
        return defaultTemplate;
    }
    
    /**
     * 计算单个模板的运费
     */
    private SmsFreightCalculateResult.FreightCalculateDetail calculateTemplateFreight(
            SmsFreightTemplate template, 
            List<SmsFreightCalculateParam.FreightCalculateItemParam> items,
            SmsFreightCalculateParam param) {
        
        SmsFreightCalculateResult.FreightCalculateDetail detail = new SmsFreightCalculateResult.FreightCalculateDetail();
        detail.setTemplateId(template.getId());
        detail.setTemplateName(template.getName());
        detail.setProductIds(items.stream().map(SmsFreightCalculateParam.FreightCalculateItemParam::getProductId).collect(Collectors.toList()));
        detail.setIsFree(false);
        
        // 计算商品总量
        BigDecimal totalCount = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;
        BigDecimal totalVolume = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        for (SmsFreightCalculateParam.FreightCalculateItemParam item : items) {
            totalCount = totalCount.add(new BigDecimal(item.getQuantity()));
            if (item.getWeight() != null) {
                totalWeight = totalWeight.add(item.getWeight().multiply(new BigDecimal(item.getQuantity())));
            }
            if (item.getVolume() != null) {
                totalVolume = totalVolume.add(item.getVolume().multiply(new BigDecimal(item.getQuantity())));
            }
            if (item.getPrice() != null) {
                totalAmount = totalAmount.add(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
            }
        }
        
        // 检查包邮条件
        if (checkFreeShipping(template, totalCount, totalWeight, totalVolume, totalAmount)) {
            detail.setIsFree(true);
            detail.setFreeReason(getFreeShippingReason(template, totalCount, totalWeight, totalVolume, totalAmount));
            detail.setTemplateFreight(BigDecimal.ZERO);
            detail.setCalculation("满足包邮条件，免运费");
            return detail;
        }
        
        // 获取匹配的区域规则
        SmsFreightTemplateRegion matchedRegion = getMatchedRegion(template.getId(), param);
        if (matchedRegion == null) {
            detail.setTemplateFreight(BigDecimal.ZERO);
            detail.setCalculation("未找到匹配的配送区域");
            return detail;
        }
        
        // 计算运费
        BigDecimal freight = calculateRegionFreight(template, matchedRegion, totalCount, totalWeight, totalVolume);
        detail.setTemplateFreight(freight);
        detail.setCalculation(String.format("基于%s规则计算", matchedRegion.getRegionNames()));
        
        return detail;
    }
    
    /**
     * 检查是否满足包邮条件
     */
    private boolean checkFreeShipping(SmsFreightTemplate template, BigDecimal totalCount, BigDecimal totalWeight, 
                                     BigDecimal totalVolume, BigDecimal totalAmount) {
        if (template.getFreeType() == null || template.getFreeType() == 0) {
            return false;
        }
        
        switch (template.getFreeType()) {
            case 1: // 满金额包邮
                return template.getFreeAmount() != null && totalAmount.compareTo(template.getFreeAmount()) >= 0;
            case 2: // 满件数包邮
                return template.getFreeCount() != null && totalCount.compareTo(new BigDecimal(template.getFreeCount())) >= 0;
            case 3: // 满重量包邮
                return template.getFreeWeight() != null && totalWeight.compareTo(template.getFreeWeight()) >= 0;
            case 4: // 无条件包邮
                return true;
            default:
                return false;
        }
    }
    
    /**
     * 获取包邮原因
     */
    private String getFreeShippingReason(SmsFreightTemplate template, BigDecimal totalCount, BigDecimal totalWeight, 
                                        BigDecimal totalVolume, BigDecimal totalAmount) {
        switch (template.getFreeType()) {
            case 1:
                return String.format("满%s元包邮", template.getFreeAmount());
            case 2:
                return String.format("满%s件包邮", template.getFreeCount());
            case 3:
                return String.format("满%skg包邮", template.getFreeWeight());
            case 4:
                return "无条件包邮";
            default:
                return "包邮";
        }
    }
    
    /**
     * 获取匹配的区域规则
     */
    private SmsFreightTemplateRegion getMatchedRegion(Long templateId, SmsFreightCalculateParam param) {
        // 优先使用自定义DAO查询匹配的区域
        if (param.getReceiverProvince() != null) {
            SmsFreightTemplateRegion matchedRegion = freightTemplateDao.getMatchingRegion(templateId, param.getReceiverProvince());
            if (matchedRegion != null) {
                return matchedRegion;
            }
        }
        
        // 如果没有匹配的，使用默认区域
        return freightTemplateDao.getDefaultRegion(templateId);
    }
    
    /**
     * 计算区域运费
     */
    private BigDecimal calculateRegionFreight(SmsFreightTemplate template, SmsFreightTemplateRegion region, 
                                            BigDecimal totalCount, BigDecimal totalWeight, BigDecimal totalVolume) {
        BigDecimal freight = BigDecimal.ZERO;
        
        if (template.getChargeType() == 4) { // 固定运费
            return region.getFirstAmount();
        }
        
        BigDecimal calculateCount;
        BigDecimal firstCount;
        BigDecimal additionalCount;
        
        if (template.getChargeType() == 1) { // 按件数
            calculateCount = totalCount;
            firstCount = region.getFirstCount() != null ? region.getFirstCount() : BigDecimal.ONE;
            additionalCount = region.getAdditionalCount() != null ? region.getAdditionalCount() : BigDecimal.ONE;
        } else if (template.getChargeType() == 2) { // 按重量
            calculateCount = totalWeight;
            firstCount = region.getFirstCount() != null ? region.getFirstCount() : BigDecimal.ONE;
            additionalCount = region.getAdditionalCount() != null ? region.getAdditionalCount() : BigDecimal.ONE;
        } else if (template.getChargeType() == 3) { // 按体积
            calculateCount = totalVolume;
            firstCount = region.getFirstCount() != null ? region.getFirstCount() : new BigDecimal("0.001"); // 默认首体积0.001m³
            additionalCount = region.getAdditionalCount() != null ? region.getAdditionalCount() : new BigDecimal("0.001"); // 默认续体积0.001m³
        } else {
            calculateCount = totalCount; // 默认按件数
            firstCount = BigDecimal.ONE;
            additionalCount = BigDecimal.ONE;
        }
        
        // 首件/首重/首体积费用
        freight = freight.add(region.getFirstAmount() != null ? region.getFirstAmount() : BigDecimal.ZERO);
        
        // 计算续件/续重/续体积费用
        BigDecimal remainingCount = calculateCount.subtract(firstCount);
        if (remainingCount.compareTo(BigDecimal.ZERO) > 0) {
            // 向上取整计算续件/续重/续体积数量
            BigDecimal additionalUnits = remainingCount.divide(additionalCount, 0, RoundingMode.UP);
            BigDecimal additionalAmount = region.getAdditionalAmount() != null ? region.getAdditionalAmount() : BigDecimal.ZERO;
            freight = freight.add(additionalAmount.multiply(additionalUnits));
        }
        
        return freight;
    }
    
    /**
     * 创建默认运费结果（当计算失败时使用）
     */
    private SmsFreightCalculateResult createDefaultFreightResult() {
        SmsFreightCalculateResult result = new SmsFreightCalculateResult();
        result.setTotalFreight(new BigDecimal("10.00")); // 默认运费10元
        result.setIsFreeShipping(false);
        result.setFreeShippingReason("");
        result.setDetails(Collections.emptyList());
        
        LOGGER.info("使用默认运费: {}", result.getTotalFreight());
        return result;
    }
} 