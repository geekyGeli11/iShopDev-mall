package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.dto.UmsPointsCouponConfigParam;
import com.macro.mall.mapper.UmsPointsCouponConfigMapper;
import com.macro.mall.model.UmsPointsCouponConfig;
import com.macro.mall.model.UmsPointsCouponConfigExample;
import com.macro.mall.service.UmsPointsCouponConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 积分换购优惠券配置Service实现类
 * Created by macro on 2024/01/20.
 */
@Service
public class UmsPointsCouponConfigServiceImpl implements UmsPointsCouponConfigService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UmsPointsCouponConfigServiceImpl.class);
    
    @Autowired
    private UmsPointsCouponConfigMapper pointsCouponConfigMapper;
    
    @Override
    public int create(UmsPointsCouponConfigParam param) {
        UmsPointsCouponConfig config = new UmsPointsCouponConfig();
        BeanUtils.copyProperties(param, config);
        config.setRemainingCount(param.getTotalCount()); // 剩余数量初始等于总数量
        config.setCreatedAt(new Date());
        config.setUpdatedAt(new Date());
        return pointsCouponConfigMapper.insertSelective(config);
    }
    
    @Override
    public int update(Long id, UmsPointsCouponConfigParam param) {
        UmsPointsCouponConfig config = new UmsPointsCouponConfig();
        BeanUtils.copyProperties(param, config);
        config.setId(id);
        config.setUpdatedAt(new Date());
        return pointsCouponConfigMapper.updateByPrimaryKeySelective(config);
    }
    
    @Override
    public int delete(Long id) {
        return pointsCouponConfigMapper.deleteByPrimaryKey(id);
    }
    
    @Override
    public int delete(List<Long> ids) {
        UmsPointsCouponConfigExample example = new UmsPointsCouponConfigExample();
        example.createCriteria().andIdIn(ids);
        return pointsCouponConfigMapper.deleteByExample(example);
    }
    
    @Override
    public List<UmsPointsCouponConfig> list(String keyword, Byte status, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        UmsPointsCouponConfigExample example = new UmsPointsCouponConfigExample();
        UmsPointsCouponConfigExample.Criteria criteria = example.createCriteria();
        
        if (!StringUtils.isEmpty(keyword)) {
            criteria.andCouponNameLike("%" + keyword + "%");
        }
        if (status != null) {
            criteria.andStatusEqualTo(status);
        }
        
        example.setOrderByClause("sort_order asc, created_at desc");
        return pointsCouponConfigMapper.selectByExample(example);
    }
    
    @Override
    public UmsPointsCouponConfig getItem(Long id) {
        return pointsCouponConfigMapper.selectByPrimaryKey(id);
    }
    
    @Override
    public int updateStatus(List<Long> ids, Byte status) {
        UmsPointsCouponConfig config = new UmsPointsCouponConfig();
        config.setStatus(status);
        config.setUpdatedAt(new Date());
        
        UmsPointsCouponConfigExample example = new UmsPointsCouponConfigExample();
        example.createCriteria().andIdIn(ids);
        return pointsCouponConfigMapper.updateByExampleSelective(config, example);
    }
    
    @Override
    public int updateCount(Long id, Integer totalCount) {
        UmsPointsCouponConfig config = pointsCouponConfigMapper.selectByPrimaryKey(id);
        if (config == null) {
            LOGGER.warn("积分换购优惠券配置不存在，ID: {}", id);
            return 0;
        }
        
        // 计算剩余数量的变化
        int countDiff = totalCount - config.getTotalCount();
        int newRemainingCount = config.getRemainingCount() + countDiff;
        
        UmsPointsCouponConfig updateConfig = new UmsPointsCouponConfig();
        updateConfig.setId(id);
        updateConfig.setTotalCount(totalCount);
        updateConfig.setRemainingCount(Math.max(0, newRemainingCount)); // 确保剩余数量不为负
        updateConfig.setUpdatedAt(new Date());
        
        return pointsCouponConfigMapper.updateByPrimaryKeySelective(updateConfig);
    }
    
    @Override
    public int reduceCount(Long id, Integer quantity) {
        UmsPointsCouponConfig config = pointsCouponConfigMapper.selectByPrimaryKey(id);
        if (config == null) {
            LOGGER.warn("积分换购优惠券配置不存在，ID: {}", id);
            return 0;
        }
        
        if (config.getRemainingCount() < quantity) {
            LOGGER.warn("优惠券数量不足，无法减少。当前数量: {}, 请求减少: {}", config.getRemainingCount(), quantity);
            return 0;
        }
        
        UmsPointsCouponConfig updateConfig = new UmsPointsCouponConfig();
        updateConfig.setId(id);
        updateConfig.setRemainingCount(config.getRemainingCount() - quantity);
        updateConfig.setUpdatedAt(new Date());
        
        return pointsCouponConfigMapper.updateByPrimaryKeySelective(updateConfig);
    }
    
    @Override
    public List<UmsPointsCouponConfig> listAllAvailable() {
        UmsPointsCouponConfigExample example = new UmsPointsCouponConfigExample();
        UmsPointsCouponConfigExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo((byte) 1); // 启用状态
        criteria.andRemainingCountGreaterThan(0); // 有剩余数量
        
        Date now = new Date();
        criteria.andStartTimeLessThanOrEqualTo(now).andEndTimeGreaterThanOrEqualTo(now); // 在活动时间内
        
        example.setOrderByClause("sort_order asc, created_at desc");
        return pointsCouponConfigMapper.selectByExample(example);
    }
    
    @Override
    public UmsPointsCouponConfig getByCouponId(Long couponId) {
        UmsPointsCouponConfigExample example = new UmsPointsCouponConfigExample();
        example.createCriteria().andCouponIdEqualTo(couponId);
        List<UmsPointsCouponConfig> configs = pointsCouponConfigMapper.selectByExample(example);
        return configs.isEmpty() ? null : configs.get(0);
    }
} 