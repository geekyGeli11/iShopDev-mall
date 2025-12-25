package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.mapper.UmsMemberBalanceHistoryMapper;
import com.macro.mall.model.UmsMemberBalanceHistory;
import com.macro.mall.model.UmsMemberBalanceHistoryExample;
import com.macro.mall.service.UmsMemberBalanceHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 用户余额记录管理Service实现类
 */
@Service
public class UmsMemberBalanceHistoryServiceImpl implements UmsMemberBalanceHistoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UmsMemberBalanceHistoryServiceImpl.class);

    @Autowired
    private UmsMemberBalanceHistoryMapper balanceHistoryMapper;

    @Override
    public List<UmsMemberBalanceHistory> getRechargeHistory(Long memberId, Integer status, String startDate, String endDate, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        UmsMemberBalanceHistoryExample example = new UmsMemberBalanceHistoryExample();
        UmsMemberBalanceHistoryExample.Criteria criteria = example.createCriteria();

        criteria.andMemberIdEqualTo(memberId);
        
        // 充值相关的业务类型
        criteria.andBusinessTypeIn(List.of("recharge", "recharge_bonus"));

        if (StringUtils.hasText(startDate)) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date start = sdf.parse(startDate);
                criteria.andCreateTimeGreaterThanOrEqualTo(start);
            } catch (Exception e) {
                LOGGER.warn("解析开始时间失败: {}", startDate, e);
            }
        }

        if (StringUtils.hasText(endDate)) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date end = sdf.parse(endDate + " 23:59:59");
                criteria.andCreateTimeLessThanOrEqualTo(end);
            } catch (Exception e) {
                LOGGER.warn("解析结束时间失败: {}", endDate, e);
            }
        }

        example.setOrderByClause("create_time desc");
        return balanceHistoryMapper.selectByExample(example);
    }

    @Override
    public List<UmsMemberBalanceHistory> getConsumptionHistory(Long memberId, String businessType, Integer changeType, String startDate, String endDate, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        UmsMemberBalanceHistoryExample example = new UmsMemberBalanceHistoryExample();
        UmsMemberBalanceHistoryExample.Criteria criteria = example.createCriteria();

        criteria.andMemberIdEqualTo(memberId);

        if (StringUtils.hasText(businessType)) {
            criteria.andBusinessTypeEqualTo(businessType);
        }

        if (changeType != null) {
            criteria.andChangeTypeEqualTo(changeType.byteValue());
        }

        if (StringUtils.hasText(startDate)) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date start = sdf.parse(startDate);
                criteria.andCreateTimeGreaterThanOrEqualTo(start);
            } catch (Exception e) {
                LOGGER.warn("解析开始时间失败: {}", startDate, e);
            }
        }

        if (StringUtils.hasText(endDate)) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date end = sdf.parse(endDate + " 23:59:59");
                criteria.andCreateTimeLessThanOrEqualTo(end);
            } catch (Exception e) {
                LOGGER.warn("解析结束时间失败: {}", endDate, e);
            }
        }

        example.setOrderByClause("create_time desc");
        return balanceHistoryMapper.selectByExample(example);
    }
} 