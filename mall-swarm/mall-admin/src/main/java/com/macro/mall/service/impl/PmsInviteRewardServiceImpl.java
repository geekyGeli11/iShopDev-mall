package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.dao.PmsInviteRewardDao;
import com.macro.mall.mapper.PmsInviteRewardMapper;
import com.macro.mall.model.PmsInviteReward;
import com.macro.mall.model.PmsInviteRewardExample;
import com.macro.mall.service.PmsInviteRewardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

@Service  
public class PmsInviteRewardServiceImpl implements PmsInviteRewardService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PmsInviteRewardServiceImpl.class);
    
    @Autowired
    private PmsInviteRewardMapper rewardMapper;
    
    @Autowired
    private PmsInviteRewardDao rewardDao;
    
    @Override
    public List<Map<String, Object>> list(Long userId, Integer userType, Integer rewardType,
                                          Integer triggerType, Integer status, Integer commissionType,
                                          String startTime, String endTime,
                                          Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        
        // 使用自定义Dao进行关联查询
        List<Map<String, Object>> result = rewardDao.getInviteRewardsList(
                userId, userType, rewardType, triggerType, status, commissionType, startTime, endTime);
        
        return result;
    }
    
    @Override
    public Map<String, Object> getDetail(Long id) {
        // 使用自定义Dao获取详情
        return rewardDao.getInviteRewardDetail(id);
    }
    
    @Override
    public int updateStatus(Long id, Integer status, String remark) {
        PmsInviteReward reward = new PmsInviteReward();
        reward.setId(id);
        reward.setStatus(status.byteValue());
        reward.setSendResult(remark);
        reward.setUpdatedAt(new Date());
        
        return rewardMapper.updateByPrimaryKeySelective(reward);
    }
    
    @Override
    public int retrySendReward(Long id) {
        try {
            PmsInviteReward reward = rewardMapper.selectByPrimaryKey(id);
            if (reward == null) {
                LOGGER.warn("奖励记录不存在，ID：{}", id);
                return 0;
            }
            
            // 检查奖励状态，只有失败的才能重试
            if (reward.getStatus() != 2) { // 2表示失败
                LOGGER.warn("奖励状态不允许重试，ID：{}，状态：{}", id, reward.getStatus());
                return 0;
            }
            
            // 更新状态为处理中
            PmsInviteReward updateReward = new PmsInviteReward();
            updateReward.setId(id);
            updateReward.setStatus((byte)0); // 0表示处理中
            updateReward.setSendResult("重试发放中");
            updateReward.setUpdatedAt(new Date());
            
            int result = rewardMapper.updateByPrimaryKeySelective(updateReward);
            
            // TODO: 这里应该调用实际的奖励发放逻辑
            // 暂时模拟发放成功
            if (result > 0) {
                updateReward.setStatus((byte)1); // 1表示成功
                updateReward.setSendResult("重试发放成功");
                updateReward.setSendTime(new Date());
                rewardMapper.updateByPrimaryKeySelective(updateReward);
            }
            
            LOGGER.info("重试发放奖励：ID={}，结果={}", id, result);
            return result;
        } catch (Exception e) {
            LOGGER.error("重试发放奖励失败，ID：{}", id, e);
            return 0;
        }
    }
    
    @Override
    public int batchSendReward(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        
        int successCount = 0;
        for (Long id : ids) {
            try {
                int result = retrySendReward(id);
                if (result > 0) {
                    successCount++;
                }
            } catch (Exception e) {
                LOGGER.error("批量发放奖励失败，ID：{}", id, e);
            }
        }
        
        LOGGER.info("批量发放奖励完成：成功数={}，总数={}", successCount, ids.size());
        return successCount;
    }
    
    @Override
    public List<Map<String, Object>> exportRewardRecords(Long userId, Integer userType, Integer rewardType,
                                                          Integer triggerType, Integer status, Integer commissionType,
                                                          String startTime, String endTime) {
        // 导出时不分页，获取所有数据
        return rewardDao.getInviteRewardsList(userId, userType, rewardType, triggerType, status, commissionType, startTime, endTime);
    }

    @Override
    public List<Map<String, Object>> exportRewardRecords(Long userId, Integer userType, Integer rewardType,
                                                          Integer triggerType, Integer status, Integer commissionType,
                                                          String startTime, String endTime, Integer pageSize, Integer pageNum) {
        // 分页导出
        PageHelper.startPage(pageNum, pageSize);
        return rewardDao.getInviteRewardsList(userId, userType, rewardType, triggerType, status, commissionType, startTime, endTime);
    }

    @Override
    public long countRewardRecords(Long userId, Integer userType, Integer rewardType,
                                   Integer triggerType, Integer status, Integer commissionType,
                                   String startTime, String endTime) {
        // 统计记录数量
        return rewardDao.countInviteRewardsList(userId, userType, rewardType, triggerType, status, commissionType, startTime, endTime);
    }
    
    @Override
    public Map<String, Object> getRewardSummary() {
        // 使用自定义Dao获取统计数据
        return rewardDao.getRewardSummaryStats();
    }
    
    @Override
    public Map<String, Object> getCommissionStatistics(String startTime, String endTime) {
        try {
            // 使用自定义Dao获取佣金统计数据
            return rewardDao.getCommissionStatistics(startTime, endTime);
        } catch (Exception e) {
            LOGGER.error("获取佣金统计数据失败", e);
            // 返回默认的空统计数据
            Map<String, Object> result = new HashMap<>();
            result.put("inviteRewardTotal", 0);
            result.put("level1CommissionTotal", 0);
            result.put("level2CommissionTotal", 0);
            result.put("totalCommission", 0);
            return result;
        }
    }
    
    @Override
    public List<Map<String, Object>> getCommissionTrend(Integer commissionType, String startTime, 
                                                        String endTime, String granularity) {
        try {
            // 使用自定义Dao获取佣金趋势数据
            return rewardDao.getCommissionTrend(commissionType, startTime, endTime, granularity);
        } catch (Exception e) {
            LOGGER.error("获取佣金趋势数据失败", e);
            // 返回空的趋势数据
            return new ArrayList<>();
        }
    }
} 