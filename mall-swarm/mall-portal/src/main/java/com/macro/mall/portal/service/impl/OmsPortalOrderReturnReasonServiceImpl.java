package com.macro.mall.portal.service.impl;

import com.macro.mall.mapper.OmsOrderReturnReasonMapper;
import com.macro.mall.model.OmsOrderReturnReason;
import com.macro.mall.model.OmsOrderReturnReasonExample;
import com.macro.mall.portal.service.OmsPortalOrderReturnReasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Portal端退货原因管理Service实现类
 * Created by macro on 2018/10/17.
 */
@Service
public class OmsPortalOrderReturnReasonServiceImpl implements OmsPortalOrderReturnReasonService {
    
    @Autowired
    private OmsOrderReturnReasonMapper returnReasonMapper;
    
    @Override
    public List<OmsOrderReturnReason> getEnabledReasons() {
        OmsOrderReturnReasonExample example = new OmsOrderReturnReasonExample();
        example.createCriteria().andStatusEqualTo(1); // 只查询启用的退货原因
        example.setOrderByClause("sort desc");
        return returnReasonMapper.selectByExample(example);
    }
} 