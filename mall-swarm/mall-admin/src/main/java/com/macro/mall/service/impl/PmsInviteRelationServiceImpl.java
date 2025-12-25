package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.dao.PmsInviteRelationDao;
import com.macro.mall.mapper.PmsInviteRelationMapper;
import com.macro.mall.model.PmsInviteRelation;
import com.macro.mall.model.PmsInviteRelationExample;
import com.macro.mall.service.PmsInviteRelationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PmsInviteRelationServiceImpl implements PmsInviteRelationService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PmsInviteRelationServiceImpl.class);
    
    @Autowired
    private PmsInviteRelationMapper inviteRelationMapper;
    
    @Autowired
    private PmsInviteRelationDao inviteRelationDao;
    
    @Override
    public List<Map<String, Object>> list(Long inviterId, Long inviteeId, Integer status, 
                                          String startTime, String endTime, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        
        // 使用自定义Dao进行关联查询
        List<Map<String, Object>> result = inviteRelationDao.getInviteRelationsList(
                inviterId, inviteeId, status, startTime, endTime);
        
        return result;
    }
    
    @Override
    public Map<String, Object> getDetail(Long id) {
        // 使用自定义Dao获取详情
        return inviteRelationDao.getInviteRelationDetail(id);
    }
    
    @Override
    public int updateStatus(Long id, Integer status, String remark) {
        PmsInviteRelation relation = new PmsInviteRelation();
        relation.setId(id);
        relation.setStatus(status.byteValue());
        relation.setUpdatedAt(new Date());
        
        return inviteRelationMapper.updateByPrimaryKeySelective(relation);
    }
    
    @Override
    public int batchUpdateStatus(List<Long> ids, Integer status, String remark) {
        int successCount = 0;
        for (Long id : ids) {
            try {
                int result = updateStatus(id, status, remark);
                if (result > 0) {
                    successCount++;
                }
            } catch (Exception e) {
                LOGGER.error("批量更新邀请关系状态失败，关系ID：{}", id, e);
            }
        }
        return successCount;
    }
    
    @Override
    public List<Map<String, Object>> exportInviteRecords(Long inviterId, Long inviteeId, 
                                                          Integer status, String startTime, String endTime) {
        // 导出时不分页，获取所有数据
        return inviteRelationDao.getInviteRelationsList(inviterId, inviteeId, status, startTime, endTime);
    }
    
    @Override
    public Map<String, Object> getInviteOverview() {
        // 使用自定义Dao获取统计数据
        return inviteRelationDao.getInviteOverviewStats();
    }
} 