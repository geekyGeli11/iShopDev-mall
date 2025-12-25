package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.dto.UmsSigninConfigParam;
import com.macro.mall.dto.UmsSigninLogQueryParam;
import com.macro.mall.dto.UmsSigninStatisticsResult;
import com.macro.mall.mapper.UmsSigninRewardConfigMapper;
import com.macro.mall.mapper.UmsMemberSigninLogMapper;
import com.macro.mall.model.UmsSigninRewardConfig;
import com.macro.mall.model.UmsSigninRewardConfigExample;
import com.macro.mall.model.UmsMemberSigninLog;
import com.macro.mall.model.UmsMemberSigninLogExample;
import com.macro.mall.service.UmsSigninService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 签到管理Service实现类
 * Created by guanghengzhou on 2024/12/23.
 */
@Service
public class UmsSigninServiceImpl implements UmsSigninService {
    
    @Autowired
    private UmsSigninRewardConfigMapper signinRewardConfigMapper;
    
    @Autowired
    private UmsMemberSigninLogMapper memberSigninLogMapper;
    
    @Override
    public UmsSigninRewardConfig getConfig() {
        // 获取启用的配置
        UmsSigninRewardConfigExample example = new UmsSigninRewardConfigExample();
        example.createCriteria().andIsActiveEqualTo((byte) 1);
        example.setOrderByClause("id desc");
        
        List<UmsSigninRewardConfig> configs = signinRewardConfigMapper.selectByExample(example);
        if (configs != null && !configs.isEmpty()) {
            return configs.get(0);
        }
        
        // 如果没有配置，返回默认配置
        UmsSigninRewardConfig defaultConfig = new UmsSigninRewardConfig();
        defaultConfig.setConfigName("默认签到配置");
        defaultConfig.setBasePoints(5);
        defaultConfig.setIncrementPoints(3);
        defaultConfig.setMaxDailyPoints(30);
        defaultConfig.setContinuousDaysForReward(30);
        defaultConfig.setCycleDays(30);
        defaultConfig.setIsActive((byte) 1);
        
        return defaultConfig;
    }
    
    @Override
    public int updateConfig(UmsSigninConfigParam configParam) {
        UmsSigninRewardConfig config = new UmsSigninRewardConfig();
        BeanUtils.copyProperties(configParam, config);
        
        if (configParam.getId() != null) {
            // 更新现有配置
            return signinRewardConfigMapper.updateByPrimaryKeySelective(config);
        } else {
            // 新增配置，先禁用其他配置
            UmsSigninRewardConfigExample example = new UmsSigninRewardConfigExample();
            example.createCriteria().andIsActiveEqualTo((byte) 1);
            
            UmsSigninRewardConfig disableConfig = new UmsSigninRewardConfig();
            disableConfig.setIsActive((byte) 0);
            signinRewardConfigMapper.updateByExampleSelective(disableConfig, example);
            
            // 插入新配置
            return signinRewardConfigMapper.insertSelective(config);
        }
    }
    
    @Override
    public List<UmsMemberSigninLog> getLogs(UmsSigninLogQueryParam queryParam) {
        PageHelper.startPage(queryParam.getPageNum(), queryParam.getPageSize());
        
        UmsMemberSigninLogExample example = new UmsMemberSigninLogExample();
        UmsMemberSigninLogExample.Criteria criteria = example.createCriteria();
        
        // 用户名筛选
        if (queryParam.getMemberUsername() != null && !queryParam.getMemberUsername().trim().isEmpty()) {
            criteria.andMemberUsernameLike("%" + queryParam.getMemberUsername() + "%");
        }
        
        // 签到月份筛选
        if (queryParam.getSigninMonth() != null && !queryParam.getSigninMonth().trim().isEmpty()) {
            criteria.andSigninMonthEqualTo(queryParam.getSigninMonth());
        }
        
        // 日期范围筛选
        if (queryParam.getStartDate() != null && !queryParam.getStartDate().trim().isEmpty()) {
            criteria.andSigninDateGreaterThanOrEqualTo(java.sql.Date.valueOf(queryParam.getStartDate()));
        }
        if (queryParam.getEndDate() != null && !queryParam.getEndDate().trim().isEmpty()) {
            criteria.andSigninDateLessThanOrEqualTo(java.sql.Date.valueOf(queryParam.getEndDate()));
        }
        
        example.setOrderByClause("signin_date desc, signin_time desc");
        
        return memberSigninLogMapper.selectByExample(example);
    }
    
    @Override
    public UmsSigninStatisticsResult getStatistics(String startDate, String endDate) {
        // TODO: 实现统计逻辑，需要自定义SQL查询
        // 这里先返回一个空的结果对象，具体实现需要在Mapper中添加自定义查询方法
        
        UmsSigninStatisticsResult result = new UmsSigninStatisticsResult();
        result.setTotalSigninUsers(0L);
        result.setTotalSigninCount(0L);
        result.setTodaySigninUsers(0L);
        result.setMonthSigninUsers(0L);
        result.setContinuous7DaysUsers(0L);
        result.setContinuous30DaysUsers(0L);
        result.setTotalPointsGiven(0L);
        result.setMonthPointsGiven(0L);
        result.setContinuousRewardCount(0L);
        
        // TODO: 需要在UmsMemberSigninLogMapper中添加统计查询方法
        // result = memberSigninLogMapper.getStatistics(startDate, endDate);
        
        return result;
    }
    
    @Override
    public String exportLogs(UmsSigninLogQueryParam queryParam) {
        // TODO: 实现导出逻辑
        // 这里先返回一个提示信息，具体实现需要集成Excel导出功能
        return "导出功能待实现";
    }
} 