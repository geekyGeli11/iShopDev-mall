package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.dao.PmsCommissionConfigDao;
import com.macro.mall.dto.*;
import com.macro.mall.mapper.PmsCommissionConfigMapper;
import com.macro.mall.model.PmsCommissionConfig;
import com.macro.mall.model.PmsCommissionConfigExample;
import com.macro.mall.service.PmsCommissionConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 佣金配置服务实现类
 */
@Service
public class PmsCommissionConfigServiceImpl implements PmsCommissionConfigService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PmsCommissionConfigServiceImpl.class);
    
    @Autowired
    private PmsCommissionConfigMapper commissionConfigMapper;
    
    @Autowired
    private PmsCommissionConfigDao commissionConfigDao;

    @Override
    public List<PmsCommissionConfigVO> list(PmsCommissionConfigQueryParam queryParam, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        return commissionConfigDao.selectByPage(queryParam);
    }

    @Override
    public PmsCommissionConfigVO getDetail(Long id) {
        return commissionConfigDao.selectDetailById(id);
    }

    @Override
    @Transactional
    public int create(PmsCommissionConfigParam configParam) {
        // 检查配置名称是否重复
        if (checkConfigNameExists(configParam.getConfigName(), null)) {
            throw new RuntimeException("配置名称已存在");
        }
        
        // 创建配置对象
        PmsCommissionConfig config = new PmsCommissionConfig();
        BeanUtils.copyProperties(configParam, config);
        
        // 处理时间字段
        if (StringUtils.hasText(configParam.getStartTime())) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                config.setStartTime(sdf.parse(configParam.getStartTime()));
            } catch (Exception e) {
                LOGGER.error("解析开始时间失败: {}", configParam.getStartTime(), e);
                throw new RuntimeException("开始时间格式错误");
            }
        }
        
        if (StringUtils.hasText(configParam.getEndTime())) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                config.setEndTime(sdf.parse(configParam.getEndTime()));
            } catch (Exception e) {
                LOGGER.error("解析结束时间失败: {}", configParam.getEndTime(), e);
                throw new RuntimeException("结束时间格式错误");
            }
        }
        
        // 设置创建时间和更新时间
        Date now = new Date();
        config.setCreatedAt(now);
        config.setUpdatedAt(now);
        
        int count = commissionConfigMapper.insertSelective(config);
        LOGGER.info("创建佣金配置成功: {}", config.getId());
        return count;
    }

    @Override
    @Transactional
    public int update(PmsCommissionConfigParam configParam) {
        if (configParam.getId() == null) {
            throw new RuntimeException("配置ID不能为空");
        }
        
        // 检查配置是否存在
        PmsCommissionConfig existConfig = commissionConfigMapper.selectByPrimaryKey(configParam.getId());
        if (existConfig == null) {
            throw new RuntimeException("配置不存在");
        }
        
        // 检查配置名称是否重复
        if (checkConfigNameExists(configParam.getConfigName(), configParam.getId())) {
            throw new RuntimeException("配置名称已存在");
        }
        
        // 更新配置对象
        PmsCommissionConfig config = new PmsCommissionConfig();
        BeanUtils.copyProperties(configParam, config);
        
        // 处理时间字段
        if (StringUtils.hasText(configParam.getStartTime())) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                config.setStartTime(sdf.parse(configParam.getStartTime()));
            } catch (Exception e) {
                LOGGER.error("解析开始时间失败: {}", configParam.getStartTime(), e);
                throw new RuntimeException("开始时间格式错误");
            }
        }
        
        if (StringUtils.hasText(configParam.getEndTime())) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                config.setEndTime(sdf.parse(configParam.getEndTime()));
            } catch (Exception e) {
                LOGGER.error("解析结束时间失败: {}", configParam.getEndTime(), e);
                throw new RuntimeException("结束时间格式错误");
            }
        }
        
        // 设置更新时间
        config.setUpdatedAt(new Date());
        
        int count = commissionConfigMapper.updateByPrimaryKeySelective(config);
        LOGGER.info("更新佣金配置成功: {}", config.getId());
        return count;
    }

    @Override
    @Transactional
    public int delete(Long id) {
        // 检查配置是否存在
        PmsCommissionConfig config = commissionConfigMapper.selectByPrimaryKey(id);
        if (config == null) {
            throw new RuntimeException("配置不存在");
        }
        
        // 检查是否有关联数据（这里可以扩展更多检查）
        
        int count = commissionConfigMapper.deleteByPrimaryKey(id);
        LOGGER.info("删除佣金配置成功: {}", id);
        return count;
    }

    @Override
    @Transactional
    public int batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        
        PmsCommissionConfigExample example = new PmsCommissionConfigExample();
        example.createCriteria().andIdIn(ids);
        
        int count = commissionConfigMapper.deleteByExample(example);
        LOGGER.info("批量删除佣金配置成功，共删除: {} 条", count);
        return count;
    }

    @Override
    @Transactional
    public int updateStatus(Long id, Byte isActive) {
        int count = commissionConfigDao.updateStatus(id, isActive);
        LOGGER.info("更新佣金配置状态成功: {}, 状态: {}", id, isActive);
        return count;
    }

    @Override
    @Transactional
    public int batchUpdateStatus(List<Long> ids, Byte isActive) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        
        int count = commissionConfigDao.batchUpdateStatus(ids, isActive);
        LOGGER.info("批量更新佣金配置状态成功，共更新: {} 条", count);
        return count;
    }

    @Override
    public Map<String, Object> getStatistics() {
        return commissionConfigDao.selectStatistics();
    }

    @Override
    public PmsCommissionConfigVO getActiveByCategoryId(Long productCategoryId) {
        return commissionConfigDao.selectActiveByCategoryId(productCategoryId);
    }

    @Override
    public PmsCommissionConfigVO getGlobalDefault() {
        return commissionConfigDao.selectGlobalDefault();
    }

    @Override
    public boolean checkConfigNameExists(String configName, Long excludeId) {
        int count = commissionConfigDao.checkConfigNameExists(configName, excludeId);
        return count > 0;
    }
} 