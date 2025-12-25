package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.dto.UmsPointsProductConfigParam;
import com.macro.mall.mapper.UmsPointsProductConfigMapper;
import com.macro.mall.model.UmsPointsProductConfig;
import com.macro.mall.model.UmsPointsProductConfigExample;
import com.macro.mall.service.UmsPointsProductConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 积分换购商品配置Service实现类
 * Created by macro on 2024/01/20.
 */
@Service
public class UmsPointsProductConfigServiceImpl implements UmsPointsProductConfigService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UmsPointsProductConfigServiceImpl.class);
    
    @Autowired
    private UmsPointsProductConfigMapper pointsProductConfigMapper;
    
    @Override
    public int create(UmsPointsProductConfigParam param) {
        UmsPointsProductConfig config = new UmsPointsProductConfig();
        BeanUtils.copyProperties(param, config);
        config.setRemainingStock(param.getTotalStock()); // 剩余库存初始等于总库存
        config.setCreatedAt(new Date());
        config.setUpdatedAt(new Date());
        return pointsProductConfigMapper.insertSelective(config);
    }
    
    @Override
    public int update(Long id, UmsPointsProductConfigParam param) {
        UmsPointsProductConfig config = new UmsPointsProductConfig();
        BeanUtils.copyProperties(param, config);
        config.setId(id);
        config.setUpdatedAt(new Date());
        return pointsProductConfigMapper.updateByPrimaryKeySelective(config);
    }
    
    @Override
    public int delete(Long id) {
        return pointsProductConfigMapper.deleteByPrimaryKey(id);
    }
    
    @Override
    public int delete(List<Long> ids) {
        UmsPointsProductConfigExample example = new UmsPointsProductConfigExample();
        example.createCriteria().andIdIn(ids);
        return pointsProductConfigMapper.deleteByExample(example);
    }
    
    @Override
    public List<UmsPointsProductConfig> list(String keyword, Byte status, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        UmsPointsProductConfigExample example = new UmsPointsProductConfigExample();
        UmsPointsProductConfigExample.Criteria criteria = example.createCriteria();
        
        if (!StringUtils.isEmpty(keyword)) {
            criteria.andProductNameLike("%" + keyword + "%");
        }
        if (status != null) {
            criteria.andStatusEqualTo(status);
        }
        
        example.setOrderByClause("sort_order asc, created_at desc");
        return pointsProductConfigMapper.selectByExample(example);
    }
    
    @Override
    public UmsPointsProductConfig getItem(Long id) {
        return pointsProductConfigMapper.selectByPrimaryKey(id);
    }
    
    @Override
    public int updateStatus(List<Long> ids, Byte status) {
        UmsPointsProductConfig config = new UmsPointsProductConfig();
        config.setStatus(status);
        config.setUpdatedAt(new Date());
        
        UmsPointsProductConfigExample example = new UmsPointsProductConfigExample();
        example.createCriteria().andIdIn(ids);
        return pointsProductConfigMapper.updateByExampleSelective(config, example);
    }
    
    @Override
    public int updateStock(Long id, Integer totalStock) {
        UmsPointsProductConfig config = pointsProductConfigMapper.selectByPrimaryKey(id);
        if (config == null) {
            LOGGER.warn("积分换购商品配置不存在，ID: {}", id);
            return 0;
        }
        
        // 计算剩余库存的变化
        int stockDiff = totalStock - config.getTotalStock();
        int newRemainingStock = config.getRemainingStock() + stockDiff;
        
        UmsPointsProductConfig updateConfig = new UmsPointsProductConfig();
        updateConfig.setId(id);
        updateConfig.setTotalStock(totalStock);
        updateConfig.setRemainingStock(Math.max(0, newRemainingStock)); // 确保剩余库存不为负
        updateConfig.setUpdatedAt(new Date());
        
        return pointsProductConfigMapper.updateByPrimaryKeySelective(updateConfig);
    }
    
    @Override
    public int reduceStock(Long id, Integer quantity) {
        UmsPointsProductConfig config = pointsProductConfigMapper.selectByPrimaryKey(id);
        if (config == null) {
            LOGGER.warn("积分换购商品配置不存在，ID: {}", id);
            return 0;
        }
        
        if (config.getRemainingStock() < quantity) {
            LOGGER.warn("库存不足，无法减少。当前库存: {}, 请求减少: {}", config.getRemainingStock(), quantity);
            return 0;
        }
        
        UmsPointsProductConfig updateConfig = new UmsPointsProductConfig();
        updateConfig.setId(id);
        updateConfig.setRemainingStock(config.getRemainingStock() - quantity);
        updateConfig.setUpdatedAt(new Date());
        
        return pointsProductConfigMapper.updateByPrimaryKeySelective(updateConfig);
    }
    
    @Override
    public List<UmsPointsProductConfig> listAllAvailable() {
        UmsPointsProductConfigExample example = new UmsPointsProductConfigExample();
        UmsPointsProductConfigExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo((byte) 1); // 上架状态
        criteria.andRemainingStockGreaterThan(0); // 有剩余库存
        
        Date now = new Date();
        criteria.andStartTimeLessThanOrEqualTo(now).andEndTimeGreaterThanOrEqualTo(now); // 在活动时间内
        
        example.setOrderByClause("sort_order asc, created_at desc");
        return pointsProductConfigMapper.selectByExample(example);
    }
    
    @Override
    public UmsPointsProductConfig getByProductId(Long productId) {
        UmsPointsProductConfigExample example = new UmsPointsProductConfigExample();
        example.createCriteria().andProductIdEqualTo(productId);
        List<UmsPointsProductConfig> configs = pointsProductConfigMapper.selectByExample(example);
        return configs.isEmpty() ? null : configs.get(0);
    }
} 