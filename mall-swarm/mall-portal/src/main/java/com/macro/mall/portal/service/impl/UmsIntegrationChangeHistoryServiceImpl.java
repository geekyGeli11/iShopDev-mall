package com.macro.mall.portal.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.mapper.UmsIntegrationChangeHistoryMapper;
import com.macro.mall.model.UmsIntegrationChangeHistory;
import com.macro.mall.model.UmsIntegrationChangeHistoryExample;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.service.UmsIntegrationChangeHistoryService;
import com.macro.mall.portal.service.UmsMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 用户积分变动历史记录Service实现类
 */
@Service
public class UmsIntegrationChangeHistoryServiceImpl implements UmsIntegrationChangeHistoryService {
    
    @Autowired
    private UmsIntegrationChangeHistoryMapper historyMapper;
    
    @Autowired
    private UmsMemberService memberService;
    
    @Override
    public List<UmsIntegrationChangeHistory> list(Integer type, Integer pageNum, Integer pageSize) {
        // 获取当前登录用户
        UmsMember currentMember = memberService.getCurrentMember();
        
        // 查询该用户的积分变动历史
        PageHelper.startPage(pageNum, pageSize);
        UmsIntegrationChangeHistoryExample example = new UmsIntegrationChangeHistoryExample();
        UmsIntegrationChangeHistoryExample.Criteria criteria = example.createCriteria();
        criteria.andMemberIdEqualTo(currentMember.getId());
        
        // 根据type筛选积分变动记录
        if (type != null) {
            if (type == 1) {
                // 获取积分的记录 (changeType=0 增加积分)
                criteria.andChangeTypeEqualTo(0);
            } else if (type == 2) {
                // 消耗积分的记录 (changeType=1 减少积分)
                criteria.andChangeTypeEqualTo(1);
            } else if (type == 3) {
                // 近三个月的记录
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.MONTH, -3);
                Date threeMonthsAgo = calendar.getTime();
                criteria.andCreateTimeGreaterThanOrEqualTo(threeMonthsAgo);
            }
            // type=0时查询全部，不需要额外条件
        }
        
        example.setOrderByClause("create_time desc");
        return historyMapper.selectByExample(example);
    }
    
    @Override
    public UmsIntegrationChangeHistory getItem(Long id) {
        // 获取当前登录用户
        UmsMember currentMember = memberService.getCurrentMember();
        
        // 查询该记录是否属于当前用户
        UmsIntegrationChangeHistoryExample example = new UmsIntegrationChangeHistoryExample();
        example.createCriteria()
               .andIdEqualTo(id)
               .andMemberIdEqualTo(currentMember.getId());
        List<UmsIntegrationChangeHistory> historyList = historyMapper.selectByExample(example);
        if (!historyList.isEmpty()) {
            return historyList.get(0);
        }
        return null;
    }
    
    @Override
    public List<UmsIntegrationChangeHistory> listByMemberId(Long memberId, Integer pageNum, Integer pageSize) {
        // 查询指定会员的积分变动历史
        PageHelper.startPage(pageNum, pageSize);
        UmsIntegrationChangeHistoryExample example = new UmsIntegrationChangeHistoryExample();
        example.createCriteria().andMemberIdEqualTo(memberId);
        example.setOrderByClause("create_time desc");
        return historyMapper.selectByExample(example);
    }
    
    @Override
    public Integer getCurrentIntegration() {
        // 获取当前登录用户
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember != null) {
            Integer integration = currentMember.getIntegration();
            return integration != null ? integration : 0;
        }
        return 0;
    }
} 