package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.dao.UmsDistributorApplyDao;
import com.macro.mall.dto.UmsDistributorApplyListVO;
import com.macro.mall.dto.UmsDistributorApplyQueryParam;
import com.macro.mall.dto.UmsDistributorApplyReviewParam;
import com.macro.mall.mapper.UmsDistributorApplyMapper;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.model.UmsDistributorApply;
import com.macro.mall.model.UmsMember;
import com.macro.mall.model.UmsDistributorApplyExample;
import com.macro.mall.service.UmsAdminService;
import com.macro.mall.service.UmsDistributorApplyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分销商申请服务实现类
 */
@Service
public class UmsDistributorApplyServiceImpl implements UmsDistributorApplyService {

    private static final Logger log = LoggerFactory.getLogger(UmsDistributorApplyServiceImpl.class);

    @Autowired
    private UmsDistributorApplyDao distributorApplyDao;

    @Autowired
    private UmsDistributorApplyMapper distributorApplyMapper;

    @Autowired
    private UmsMemberMapper memberMapper;

    @Autowired
    private UmsAdminService adminService;

    @Override
    public List<UmsDistributorApplyListVO> list(UmsDistributorApplyQueryParam queryParam, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        return distributorApplyDao.selectByPage(queryParam);
    }

    @Override
    public UmsDistributorApply getDetail(Long id) {
        return distributorApplyMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional
    public int reviewApply(UmsDistributorApplyReviewParam reviewParam, Long reviewerId) {
        UmsDistributorApply apply = new UmsDistributorApply();
        apply.setId(reviewParam.getApplyId());
        apply.setStatus(reviewParam.getStatus());
        apply.setReviewTime(new Date());
        apply.setReviewerId(reviewerId);
        apply.setReviewComment(reviewParam.getReviewComment());
        apply.setUpdatedAt(new Date());

        int result = distributorApplyMapper.updateByPrimaryKeySelective(apply);

        // 如果审核通过，更新用户的分销商状态
        if (result > 0 && reviewParam.getStatus() == 1) {
            // 获取申请信息
            UmsDistributorApply fullApply = distributorApplyMapper.selectByPrimaryKey(reviewParam.getApplyId());
            if (fullApply != null) {
                // 更新用户为分销商
                UmsMember member = new UmsMember();
                member.setId(fullApply.getUserId());
                member.setIsDistributor(Byte.valueOf("1")); // 设置为分销商
                member.setDistributorLevel(Byte.valueOf("1")); // 设置为初级分销商
                member.setApprovedTime(new Date());
                
                memberMapper.updateByPrimaryKeySelective(member);
                log.info("用户ID {} 审核通过，已设置为分销商", fullApply.getUserId());
            }
        }

        return result;
    }

    @Override
    @Transactional
    public int batchReview(List<Long> ids, Byte status, String reviewComment, Long reviewerId) {
        int result = distributorApplyDao.batchReview(ids, status, reviewComment, reviewerId);
        
        // 如果是批量通过，需要更新所有对应用户的分销商状态
        if (result > 0 && status == 1) {
            for (Long id : ids) {
                UmsDistributorApply apply = distributorApplyMapper.selectByPrimaryKey(id);
                if (apply != null && apply.getStatus() == 1) { // 确认状态为已通过
                    UmsMember member = new UmsMember();
                    member.setId(apply.getUserId());
                    member.setIsDistributor(Byte.valueOf("1"));
                    member.setDistributorLevel(Byte.valueOf("1"));
                    member.setApprovedTime(new Date());
                    
                    memberMapper.updateByPrimaryKeySelective(member);
                    log.info("批量审核：用户ID {} 已设置为分销商", apply.getUserId());
                }
            }
        }
        
        return result;
    }

    @Override
    @Transactional
    public int deleteApply(Long id) {
        return distributorApplyMapper.deleteByPrimaryKey(id);
    }

    @Override
    public Object getStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 总申请数
        UmsDistributorApplyExample totalExample = new UmsDistributorApplyExample();
        long totalCount = distributorApplyMapper.countByExample(totalExample);
        statistics.put("totalCount", totalCount);
        
        // 待审核数
        UmsDistributorApplyExample pendingExample = new UmsDistributorApplyExample();
        pendingExample.createCriteria().andStatusEqualTo(Byte.valueOf("0"));
        long pendingCount = distributorApplyMapper.countByExample(pendingExample);
        statistics.put("pendingCount", pendingCount);
        
        // 已通过数
        UmsDistributorApplyExample approvedExample = new UmsDistributorApplyExample();
        approvedExample.createCriteria().andStatusEqualTo(Byte.valueOf("1"));
        long approvedCount = distributorApplyMapper.countByExample(approvedExample);
        statistics.put("approvedCount", approvedCount);
        
        // 已拒绝数
        UmsDistributorApplyExample rejectedExample = new UmsDistributorApplyExample();
        rejectedExample.createCriteria().andStatusEqualTo(Byte.valueOf("2"));
        long rejectedCount = distributorApplyMapper.countByExample(rejectedExample);
        statistics.put("rejectedCount", rejectedCount);
        
        // 通过率
        double approveRate = totalCount > 0 ? (double) approvedCount / totalCount * 100 : 0;
        statistics.put("approveRate", Math.round(approveRate * 100.0) / 100.0);
        
        return statistics;
    }
} 