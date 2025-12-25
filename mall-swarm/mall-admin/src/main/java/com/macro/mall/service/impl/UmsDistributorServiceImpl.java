package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.dao.UmsDistributorDao;
import com.macro.mall.dto.*;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.model.UmsMember;
import com.macro.mall.service.UmsDistributorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 分销商服务实现类
 */
@Service
public class UmsDistributorServiceImpl implements UmsDistributorService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UmsDistributorServiceImpl.class);
    
    @Autowired
    private UmsDistributorDao distributorDao;
    
    @Autowired
    private UmsMemberMapper memberMapper;

    @Override
    public List<UmsDistributorListVO> list(UmsDistributorQueryParam queryParam, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        return distributorDao.selectByPage(queryParam);
    }

    @Override
    public UmsDistributorListVO getDetail(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        UmsDistributorListVO detail = distributorDao.selectDetailByUserId(userId);
        if (detail == null) {
            throw new RuntimeException("分销商信息不存在");
        }
        
        return detail;
    }

    @Override
    @Transactional
    public int updateStatus(UmsDistributorStatusParam statusParam) {
        if (statusParam == null || statusParam.getUserId() == null || statusParam.getStatus() == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        
        // 验证用户是否存在
        UmsMember member = memberMapper.selectByPrimaryKey(statusParam.getUserId());
        if (member == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 验证状态值
        if (statusParam.getStatus() < 0 || statusParam.getStatus() > 3) {
            throw new IllegalArgumentException("无效的分销商状态");
        }
        
        // 如果设置为分销商状态，需要设置审核通过时间
        if (statusParam.getStatus() == 1 && member.getApprovedTime() == null) {
            member.setApprovedTime(new Date());
            memberMapper.updateByPrimaryKey(member);
        }
        
        int result = distributorDao.updateDistributorStatus(
            statusParam.getUserId(), 
            statusParam.getStatus(), 
            statusParam.getReason()
        );
        
        if (result > 0) {
            String statusText = getStatusText(statusParam.getStatus());
            LOGGER.info("管理员更新分销商状态成功：用户ID={}, 新状态={}, 原因={}", 
                statusParam.getUserId(), statusText, statusParam.getReason());
        }
        
        return result;
    }

    @Override
    @Transactional
    public int updateLevel(UmsDistributorLevelParam levelParam) {
        if (levelParam == null || levelParam.getUserId() == null || levelParam.getDistributorLevel() == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        
        // 验证用户是否存在
        UmsMember member = memberMapper.selectByPrimaryKey(levelParam.getUserId());
        if (member == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 验证等级值
        if (levelParam.getDistributorLevel() < 0 || levelParam.getDistributorLevel() > 3) {
            throw new IllegalArgumentException("无效的分销商等级");
        }
        
        int result = distributorDao.updateDistributorLevel(
            levelParam.getUserId(), 
            levelParam.getDistributorLevel(), 
            levelParam.getReason()
        );
        
        if (result > 0) {
            String levelText = getLevelText(levelParam.getDistributorLevel());
            LOGGER.info("管理员更新分销商等级成功：用户ID={}, 新等级={}, 原因={}", 
                levelParam.getUserId(), levelText, levelParam.getReason());
        }
        
        return result;
    }

    @Override
    @Transactional
    public int batchUpdateStatus(List<Long> userIds, Byte status, String reason) {
        if (userIds == null || userIds.isEmpty()) {
            throw new IllegalArgumentException("用户ID列表不能为空");
        }
        
        if (status == null || status < 0 || status > 3) {
            throw new IllegalArgumentException("无效的分销商状态");
        }
        
        int result = distributorDao.batchUpdateStatus(userIds, status, reason);
        
        if (result > 0) {
            String statusText = getStatusText(status);
            LOGGER.info("管理员批量更新分销商状态成功：影响用户数={}, 新状态={}, 原因={}", 
                result, statusText, reason);
        }
        
        return result;
    }

    @Override
    public Map<String, Object> getStatistics() {
        return distributorDao.selectStatistics();
    }

    @Override
    @Transactional
    public int enableDistributor(Long userId, String reason) {
        UmsDistributorStatusParam param = new UmsDistributorStatusParam();
        param.setUserId(userId);
        param.setStatus((byte) 1); // 1-分销商
        param.setReason(reason);
        return updateStatus(param);
    }

    @Override
    @Transactional
    public int disableDistributor(Long userId, String reason) {
        UmsDistributorStatusParam param = new UmsDistributorStatusParam();
        param.setUserId(userId);
        param.setStatus((byte) 3); // 3-禁用
        param.setReason(reason);
        return updateStatus(param);
    }

    @Override
    @Transactional
    public int pauseDistributor(Long userId, String reason) {
        UmsDistributorStatusParam param = new UmsDistributorStatusParam();
        param.setUserId(userId);
        param.setStatus((byte) 2); // 2-暂停
        param.setReason(reason);
        return updateStatus(param);
    }
    
    /**
     * 获取状态文本
     */
    private String getStatusText(Byte status) {
        switch (status) {
            case 0: return "普通用户";
            case 1: return "分销商";
            case 2: return "暂停";
            case 3: return "禁用";
            default: return "未知状态";
        }
    }
    
    /**
     * 获取等级文本
     */
    private String getLevelText(Byte level) {
        switch (level) {
            case 0: return "普通";
            case 1: return "初级";
            case 2: return "中级";
            case 3: return "高级";
            default: return "未知等级";
        }
    }
} 