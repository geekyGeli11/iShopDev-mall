package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.dto.*;
import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import com.macro.mall.service.SmsFreightTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 运费模板Service实现类
 * Created by AI Assistant
 */
@Service
public class SmsFreightTemplateServiceImpl implements SmsFreightTemplateService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SmsFreightTemplateServiceImpl.class);
    
    private final SmsFreightTemplateMapper freightTemplateMapper;
    private final SmsFreightTemplateRegionMapper freightTemplateRegionMapper;
    private final PmsProductMapper productMapper;
    
    public SmsFreightTemplateServiceImpl(SmsFreightTemplateMapper freightTemplateMapper, 
                                       SmsFreightTemplateRegionMapper freightTemplateRegionMapper,
                                       PmsProductMapper productMapper) {
        this.freightTemplateMapper = freightTemplateMapper;
        this.freightTemplateRegionMapper = freightTemplateRegionMapper;
        this.productMapper = productMapper;
    }
    
    @Override
    public int create(SmsFreightTemplateParam param) {
        // 如果设置为默认模板，先将其他模板设置为非默认
        if (param.getIsDefault() != null && param.getIsDefault()) {
            clearAllDefaultTemplate();
        }
        
        SmsFreightTemplate template = new SmsFreightTemplate();
        BeanUtils.copyProperties(param, template);
        template.setCreateTime(new Date());
        template.setUpdateTime(new Date());
        
        int count = freightTemplateMapper.insertSelective(template);
        
        // 保存区域规则
        if (count > 0 && !CollectionUtils.isEmpty(param.getRegionRules())) {
            saveRegionRules(template.getId(), param.getRegionRules());
        }
        
        return count;
    }
    
    @Override
    public int update(Long id, SmsFreightTemplateParam param) {
        // 如果设置为默认模板，先将其他模板设置为非默认
        if (param.getIsDefault() != null && param.getIsDefault()) {
            clearAllDefaultTemplate();
        }
        
        SmsFreightTemplate template = new SmsFreightTemplate();
        BeanUtils.copyProperties(param, template);
        template.setId(id);
        template.setUpdateTime(new Date());
        
        int count = freightTemplateMapper.updateByPrimaryKeySelective(template);
        
        if (count > 0) {
            // 删除原有区域规则
            SmsFreightTemplateRegionExample regionExample = new SmsFreightTemplateRegionExample();
            regionExample.createCriteria().andTemplateIdEqualTo(id);
            freightTemplateRegionMapper.deleteByExample(regionExample);
            
            // 保存新的区域规则
            if (!CollectionUtils.isEmpty(param.getRegionRules())) {
                saveRegionRules(id, param.getRegionRules());
            }
        }
        
        return count;
    }
    
    @Override
    public int delete(Long id) {
        // 检查是否有商品在使用该模板
        PmsProductExample productExample = new PmsProductExample();
        productExample.createCriteria().andFreightTemplateIdEqualTo(id);
        long productCount = productMapper.countByExample(productExample);
        
        if (productCount > 0) {
            throw new RuntimeException("该运费模板正在被 " + productCount + " 个商品使用，无法删除");
        }
        
        // 删除区域规则
        SmsFreightTemplateRegionExample regionExample = new SmsFreightTemplateRegionExample();
        regionExample.createCriteria().andTemplateIdEqualTo(id);
        freightTemplateRegionMapper.deleteByExample(regionExample);
        
        // 删除模板
        return freightTemplateMapper.deleteByPrimaryKey(id);
    }
    
    @Override
    public int delete(List<Long> ids) {
        // 检查是否有商品在使用这些模板
        PmsProductExample productExample = new PmsProductExample();
        productExample.createCriteria().andFreightTemplateIdIn(ids);
        long productCount = productMapper.countByExample(productExample);
        
        if (productCount > 0) {
            throw new RuntimeException("这些运费模板正在被 " + productCount + " 个商品使用，无法删除");
        }
        
        // 删除区域规则
        SmsFreightTemplateRegionExample regionExample = new SmsFreightTemplateRegionExample();
        regionExample.createCriteria().andTemplateIdIn(ids);
        freightTemplateRegionMapper.deleteByExample(regionExample);
        
        // 删除模板
        SmsFreightTemplateExample templateExample = new SmsFreightTemplateExample();
        templateExample.createCriteria().andIdIn(ids);
        return freightTemplateMapper.deleteByExample(templateExample);
    }
    
    @Override
    public List<SmsFreightTemplate> list(SmsFreightTemplateQueryParam queryParam, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        SmsFreightTemplateExample example = new SmsFreightTemplateExample();
        example.setOrderByClause("is_default desc, sort desc, create_time desc");
        SmsFreightTemplateExample.Criteria criteria = example.createCriteria();
        
        if (StringUtils.hasText(queryParam.getKeyword())) {
            criteria.andNameLike("%" + queryParam.getKeyword() + "%");
        }
        if (queryParam.getStatus() != null) {
            criteria.andStatusEqualTo(queryParam.getStatus());
        }
        if (queryParam.getChargeType() != null) {
            criteria.andChargeTypeEqualTo(queryParam.getChargeType());
        }
        if (queryParam.getDeliveryType() != null) {
            criteria.andDeliveryTypeEqualTo(queryParam.getDeliveryType());
        }
        
        return freightTemplateMapper.selectByExample(example);
    }
    
    @Override
    public SmsFreightTemplateVO getTemplateDetail(Long id) {
        SmsFreightTemplate template = freightTemplateMapper.selectByPrimaryKey(id);
        if (template == null) {
            return null;
        }
        
        SmsFreightTemplateVO vo = new SmsFreightTemplateVO();
        BeanUtils.copyProperties(template, vo);
        
        // 获取区域规则
        SmsFreightTemplateRegionExample regionExample = new SmsFreightTemplateRegionExample();
        regionExample.createCriteria().andTemplateIdEqualTo(id);
        regionExample.setOrderByClause("sort asc, id asc");
        List<SmsFreightTemplateRegion> regionRules = freightTemplateRegionMapper.selectByExampleWithBLOBs(regionExample);
        vo.setRegionRules(regionRules);
        
        // 获取应用商品数量
        PmsProductExample productExample = new PmsProductExample();
        productExample.createCriteria().andFreightTemplateIdEqualTo(id);
        long productCount = productMapper.countByExample(productExample);
        vo.setProductCount(productCount);
        
        return vo;
    }
    
    @Override
    public List<SmsFreightTemplate> listAllEnabled() {
        SmsFreightTemplateExample example = new SmsFreightTemplateExample();
        example.createCriteria().andStatusEqualTo((byte) 1);
        example.setOrderByClause("sort desc, create_time desc");
        return freightTemplateMapper.selectByExample(example);
    }
    
    @Override
    public int updateStatus(List<Long> ids, Byte status) {
        SmsFreightTemplate template = new SmsFreightTemplate();
        template.setStatus(status);
        template.setUpdateTime(new Date());
        
        SmsFreightTemplateExample example = new SmsFreightTemplateExample();
        example.createCriteria().andIdIn(ids);
        return freightTemplateMapper.updateByExampleSelective(template, example);
    }
    
    @Override
    public SmsFreightCalculateResult calculateFreight(SmsFreightCalculateParam param) {
        SmsFreightCalculateResult result = new SmsFreightCalculateResult();
        result.setTotalFreight(BigDecimal.ZERO);
        result.setIsFreeShipping(false);
        result.setDetails(new ArrayList<>());
        
        // 按运费模板分组商品
        Map<Long, List<SmsFreightCalculateParam.FreightCalculateItemParam>> templateGroups = 
            param.getItems().stream()
                .collect(Collectors.groupingBy(item -> 
                    item.getFreightTemplateId() != null ? item.getFreightTemplateId() : getDefaultTemplate().getId()));
        
        BigDecimal totalFreight = BigDecimal.ZERO;
        boolean hasFreeShipping = false;
        List<String> freeShippingReasons = new ArrayList<>();
        
        for (Map.Entry<Long, List<SmsFreightCalculateParam.FreightCalculateItemParam>> entry : templateGroups.entrySet()) {
            Long templateId = entry.getKey();
            List<SmsFreightCalculateParam.FreightCalculateItemParam> items = entry.getValue();
            
            SmsFreightTemplate template = freightTemplateMapper.selectByPrimaryKey(templateId);
            if (template == null || template.getStatus() != 1) {
                template = getDefaultTemplate();
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
        
        return result;
    }
    
    @Override
    public SmsFreightTemplate getDefaultTemplate() {
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
            // 直接执行设置默认模板的逻辑，避免自调用
            setDefaultTemplateInternal(firstTemplate.getId());
            firstTemplate.setIsDefault(true); // 更新返回对象的状态
            return firstTemplate;
        }
        
        // 如果不存在任何启用的模板，创建一个默认模板
        return createDefaultTemplate();
    }
    
    @Override
    public int setDefaultTemplate(Long id) {
        // 检查模板是否存在且启用
        SmsFreightTemplate template = freightTemplateMapper.selectByPrimaryKey(id);
        if (template == null) {
            throw new RuntimeException("运费模板不存在");
        }
        if (template.getStatus() != 1) {
            throw new RuntimeException("只有启用的模板才能设置为默认模板");
        }
        
        // 先将所有模板设置为非默认
        clearAllDefaultTemplate();
        
        // 将指定模板设置为默认
        SmsFreightTemplate updateTemplate = new SmsFreightTemplate();
        updateTemplate.setId(id);
        updateTemplate.setIsDefault(true);
        updateTemplate.setUpdateTime(new Date());
        
        int count = freightTemplateMapper.updateByPrimaryKeySelective(updateTemplate);
        
        LOGGER.info("设置运费模板为默认成功，模板ID: {}, 模板名称: {}", id, template.getName());
        return count;
    }
    
    /**
     * 清除所有模板的默认状态
     */
    private void clearAllDefaultTemplate() {
        SmsFreightTemplate updateTemplate = new SmsFreightTemplate();
        updateTemplate.setIsDefault(false);
        updateTemplate.setUpdateTime(new Date());
        
        SmsFreightTemplateExample example = new SmsFreightTemplateExample();
        example.createCriteria().andIsDefaultEqualTo(true);
        freightTemplateMapper.updateByExampleSelective(updateTemplate, example);
    }
    
    @Override
    public SmsFreightTemplate createDefaultTemplate() {
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
        defaultRegion.setRegionIds("全国");
        defaultRegion.setRegionNames("全国");
        defaultRegion.setFirstAmount(new BigDecimal("10.00")); // 首件10元
        defaultRegion.setFirstCount(BigDecimal.ONE);
        defaultRegion.setAdditionalAmount(new BigDecimal("5.00")); // 续件5元
        defaultRegion.setAdditionalCount(BigDecimal.ONE);
        defaultRegion.setSort(0);
        
        freightTemplateRegionMapper.insertSelective(defaultRegion);
        
        LOGGER.info("创建默认运费模板成功，模板ID: {}", defaultTemplate.getId());
        return defaultTemplate;
    }
    
    /**
     * 保存区域规则
     */
    private void saveRegionRules(Long templateId, List<SmsFreightTemplateRegionParam> regionRules) {
        for (SmsFreightTemplateRegionParam regionParam : regionRules) {
            SmsFreightTemplateRegion region = new SmsFreightTemplateRegion();
            BeanUtils.copyProperties(regionParam, region);
            region.setTemplateId(templateId);
            freightTemplateRegionMapper.insertSelective(region);
        }
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
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        for (SmsFreightCalculateParam.FreightCalculateItemParam item : items) {
            totalCount = totalCount.add(new BigDecimal(item.getQuantity()));
            if (item.getWeight() != null) {
                totalWeight = totalWeight.add(item.getWeight().multiply(new BigDecimal(item.getQuantity())));
            }
            if (item.getPrice() != null) {
                totalAmount = totalAmount.add(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
            }
        }
        
        // 检查包邮条件
        if (checkFreeShipping(template, totalCount, totalWeight, totalAmount)) {
            detail.setIsFree(true);
            detail.setFreeReason(getFreeShippingReason(template, totalCount, totalWeight, totalAmount));
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
        BigDecimal freight = calculateRegionFreight(template, matchedRegion, totalCount, totalWeight);
        detail.setTemplateFreight(freight);
        detail.setCalculation(String.format("基于%s规则计算", matchedRegion.getRegionNames()));
        
        return detail;
    }
    
    /**
     * 检查是否满足包邮条件
     */
    private boolean checkFreeShipping(SmsFreightTemplate template, BigDecimal totalCount, BigDecimal totalWeight, BigDecimal totalAmount) {
        if (template.getFreeType() == null || template.getFreeType() == 0) {
            return false;
        }
        
        return switch (template.getFreeType()) {
            case 1 -> // 满金额包邮
                template.getFreeAmount() != null && totalAmount.compareTo(template.getFreeAmount()) >= 0;
            case 2 -> // 满件数包邮
                template.getFreeCount() != null && totalCount.compareTo(new BigDecimal(template.getFreeCount())) >= 0;
            case 3 -> // 满重量包邮
                template.getFreeWeight() != null && totalWeight.compareTo(template.getFreeWeight()) >= 0;
            default -> false;
        };
    }
    
    /**
     * 获取包邮原因
     */
    @SuppressWarnings("unused") // totalCount和totalWeight参数保留供将来扩展使用
    private String getFreeShippingReason(SmsFreightTemplate template, BigDecimal totalCount, BigDecimal totalWeight, BigDecimal totalAmount) {
        return switch (template.getFreeType()) {
            case 1 -> String.format("满%s元包邮", template.getFreeAmount());
            case 2 -> String.format("满%s件包邮", template.getFreeCount());
            case 3 -> String.format("满%skg包邮", template.getFreeWeight());
            default -> "包邮";
        };
    }
    
    /**
     * 获取匹配的区域规则
     */
    private SmsFreightTemplateRegion getMatchedRegion(Long templateId, SmsFreightCalculateParam param) {
        SmsFreightTemplateRegionExample example = new SmsFreightTemplateRegionExample();
        example.createCriteria().andTemplateIdEqualTo(templateId);
        example.setOrderByClause("sort asc, id asc");
        
        List<SmsFreightTemplateRegion> regions = freightTemplateRegionMapper.selectByExampleWithBLOBs(example);
        
        for (SmsFreightTemplateRegion region : regions) {
            if (isRegionMatched(region, param)) {
                return region;
            }
        }
        
        // 如果没有匹配的，返回第一个作为默认
        return regions.isEmpty() ? null : regions.get(0);
    }
    
    /**
     * 判断区域是否匹配
     */
    private boolean isRegionMatched(SmsFreightTemplateRegion region, SmsFreightCalculateParam param) {
        if (region.getRegionType() == 3) { // 距离范围
            // 这里需要实现地理位置距离计算
            if (param.getReceiverLongitude() != null && param.getReceiverLatitude() != null 
                && param.getMerchantLongitude() != null && param.getMerchantLatitude() != null) {
                double distance = calculateDistance(
                    param.getMerchantLatitude().doubleValue(), param.getMerchantLongitude().doubleValue(),
                    param.getReceiverLatitude().doubleValue(), param.getReceiverLongitude().doubleValue()
                );
                return distance >= region.getDistanceStart().doubleValue() && distance <= region.getDistanceEnd().doubleValue();
            }
        } else {
            // 省份/城市匹配
            String regionNames = region.getRegionNames();
            if (regionNames != null && (regionNames.contains("全国") || 
                regionNames.contains(param.getReceiverProvince()) || 
                (param.getReceiverCity() != null && regionNames.contains(param.getReceiverCity())))) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 计算区域运费
     */
    private BigDecimal calculateRegionFreight(SmsFreightTemplate template, SmsFreightTemplateRegion region, 
                                            BigDecimal totalCount, BigDecimal totalWeight) {
        BigDecimal freight = BigDecimal.ZERO;
        
        if (template.getChargeType() == 4) { // 固定运费
            return region.getFirstAmount();
        }
        
        BigDecimal calculateCount;
        if (template.getChargeType() == 1) { // 按件数
            calculateCount = totalCount;
        } else if (template.getChargeType() == 2) { // 按重量
            calculateCount = totalWeight;
        } else {
            calculateCount = totalCount; // 默认按件数
        }
        
        // 首件/首重费用
        freight = freight.add(region.getFirstAmount());
        
        // 计算续件/续重费用
        BigDecimal remainingCount = calculateCount.subtract(region.getFirstCount());
        if (remainingCount.compareTo(BigDecimal.ZERO) > 0) {
            // 向上取整计算续件/续重数量
            BigDecimal additionalUnits = remainingCount.divide(region.getAdditionalCount(), 0, RoundingMode.UP);
            freight = freight.add(region.getAdditionalAmount().multiply(additionalUnits));
        }
        
        return freight;
    }
    
    /**
     * 计算两点间距离（单位：公里）
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 地球半径（公里）
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }

    /**
     * 内部方法：设置运费模板为默认
     */
    private int setDefaultTemplateInternal(Long id) {
        // 检查模板是否存在且启用
        SmsFreightTemplate template = freightTemplateMapper.selectByPrimaryKey(id);
        if (template == null) {
            throw new RuntimeException("运费模板不存在");
        }
        if (template.getStatus() != 1) {
            throw new RuntimeException("只有启用的模板才能设置为默认模板");
        }
        
        // 先将所有模板设置为非默认
        clearAllDefaultTemplate();
        
        // 将指定模板设置为默认
        SmsFreightTemplate updateTemplate = new SmsFreightTemplate();
        updateTemplate.setId(id);
        updateTemplate.setIsDefault(true);
        updateTemplate.setUpdateTime(new Date());
        
        int count = freightTemplateMapper.updateByPrimaryKeySelective(updateTemplate);
        
        LOGGER.info("设置运费模板为默认成功，模板ID: {}, 模板名称: {}", id, template.getName());
        return count;
    }
    
    @Override
    public int refreshAllProductFreightTemplate(Long templateId) {
        // 检查模板是否存在且启用
        SmsFreightTemplate template = freightTemplateMapper.selectByPrimaryKey(templateId);
        if (template == null) {
            throw new RuntimeException("运费模板不存在");
        }
        if (template.getStatus() != 1) {
            throw new RuntimeException("只有启用的模板才能应用到商品");
        }
        
        // 更新所有商品的运费模板ID
        PmsProduct updateProduct = new PmsProduct();
        updateProduct.setFreightTemplateId(templateId);
        
        PmsProductExample example = new PmsProductExample();
        // 更新所有商品（不加条件限制）
        example.createCriteria();
        
        int count = productMapper.updateByExampleSelective(updateProduct, example);
        
        LOGGER.info("刷新全部商品运费模板成功，模板ID: {}, 模板名称: {}, 更新商品数量: {}", templateId, template.getName(), count);
        return count;
    }
} 