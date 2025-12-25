package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.mapper.SmsIntegrationPromotionMapper;
import com.macro.mall.model.SmsIntegrationPromotion;
import com.macro.mall.model.SmsIntegrationPromotionExample;
import com.macro.mall.service.SmsIntegrationPromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 积分营销活动管理Service实现类
 * Created by macro on 2024/12/20.
 */
@Service
public class SmsIntegrationPromotionServiceImpl implements SmsIntegrationPromotionService {
    
    @Autowired
    private SmsIntegrationPromotionMapper integrationPromotionMapper;

    @Override
    public int create(SmsIntegrationPromotion integrationPromotion) {
        integrationPromotion.setCreateTime(new Date());
        integrationPromotion.setUpdateTime(new Date());
        integrationPromotion.setUsedIntegration(0);
        return integrationPromotionMapper.insert(integrationPromotion);
    }

    @Override
    public int update(Long id, SmsIntegrationPromotion integrationPromotion) {
        integrationPromotion.setId(id);
        integrationPromotion.setUpdateTime(new Date());
        return integrationPromotionMapper.updateByPrimaryKeySelective(integrationPromotion);
    }

    @Override
    public int delete(Long id) {
        return integrationPromotionMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int deleteBatch(List<Long> ids) {
        SmsIntegrationPromotionExample example = new SmsIntegrationPromotionExample();
        example.createCriteria().andIdIn(ids);
        return integrationPromotionMapper.deleteByExample(example);
    }

    @Override
    public SmsIntegrationPromotion getItem(Long id) {
        return integrationPromotionMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<SmsIntegrationPromotion> list(String name, Boolean status, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        SmsIntegrationPromotionExample example = new SmsIntegrationPromotionExample();
        SmsIntegrationPromotionExample.Criteria criteria = example.createCriteria();

        if (StringUtils.hasText(name)) {
            criteria.andNameLike("%" + name + "%");
        }
        if (status != null) {
            criteria.andStatusEqualTo(status);
        }

        example.setOrderByClause("priority desc, create_time desc");
        return integrationPromotionMapper.selectByExample(example);
    }

    @Override
    public int updateStatus(Long id, Boolean status) {
        SmsIntegrationPromotion integrationPromotion = new SmsIntegrationPromotion();
        integrationPromotion.setId(id);
        integrationPromotion.setStatus(status);
        integrationPromotion.setUpdateTime(new Date());
        return integrationPromotionMapper.updateByPrimaryKeySelective(integrationPromotion);
    }

    @Override
    public List<SmsIntegrationPromotion> listEnabled() {
        SmsIntegrationPromotionExample example = new SmsIntegrationPromotionExample();
        example.createCriteria().andStatusEqualTo(true);
        example.setOrderByClause("priority desc, create_time desc");
        return integrationPromotionMapper.selectByExample(example);
    }

    @Override
    public List<SmsIntegrationPromotion> getCurrentActive() {
        Date now = new Date();
        SmsIntegrationPromotionExample example = new SmsIntegrationPromotionExample();
        example.createCriteria()
                .andStatusEqualTo(true)
                .andStartTimeLessThanOrEqualTo(now)
                .andEndTimeGreaterThanOrEqualTo(now);
        example.setOrderByClause("priority desc, create_time desc");
        return integrationPromotionMapper.selectByExample(example);
    }

    @Override
    public SmsIntegrationPromotion getApplicablePromotion(BigDecimal orderAmount, List<Long> productIds, List<Long> categoryIds) {
        List<SmsIntegrationPromotion> activePromotions = getCurrentActive();
        
        for (SmsIntegrationPromotion promotion : activePromotions) {
            // 检查订单金额限制
            if (promotion.getMinOrderAmount() != null && 
                orderAmount.compareTo(promotion.getMinOrderAmount()) < 0) {
                continue;
            }
            
            // 检查适用范围
            if (isPromotionApplicable(promotion, productIds, categoryIds)) {
                return promotion;
            }
        }
        
        return null;
    }
    
    /**
     * 检查营销活动是否适用于当前订单
     */
    private boolean isPromotionApplicable(SmsIntegrationPromotion promotion, List<Long> productIds, List<Long> categoryIds) {
        Integer applicableType = promotion.getApplicableType();

        // 全部商品适用 (0表示全部商品)
        if (applicableType == null || applicableType == 0) {
            return true;
        }

        String applicableIds = promotion.getApplicableIds();
        if (!StringUtils.hasText(applicableIds)) {
            return applicableType == 0; // 如果没有指定ID，只有全部商品类型才适用
        }

        List<String> idList = Arrays.asList(applicableIds.split(","));

        if (applicableType == 1) {
            // 指定分类：检查分类ID是否匹配
            if (categoryIds != null) {
                for (Long categoryId : categoryIds) {
                    if (idList.contains(categoryId.toString())) {
                        return true;
                    }
                }
            }
        } else if (applicableType == 2) {
            // 指定商品：检查商品ID是否匹配
            if (productIds != null) {
                for (Long productId : productIds) {
                    if (idList.contains(productId.toString())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
