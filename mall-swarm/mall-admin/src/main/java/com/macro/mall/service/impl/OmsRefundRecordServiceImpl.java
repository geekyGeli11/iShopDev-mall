package com.macro.mall.service.impl;

import com.macro.mall.mapper.OmsRefundRecordMapper;
import com.macro.mall.model.OmsRefundRecord;
import com.macro.mall.model.OmsRefundRecordExample;
import com.macro.mall.service.OmsRefundRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 退款记录管理Service实现类
 * Created by macro on 2024/01/01.
 */
@Service
public class OmsRefundRecordServiceImpl implements OmsRefundRecordService {
    
    @Autowired
    private OmsRefundRecordMapper refundRecordMapper;
    
    @Override
    public List<OmsRefundRecord> getByReturnApplyId(Long returnApplyId) {
        OmsRefundRecordExample example = new OmsRefundRecordExample();
        example.createCriteria().andReturnApplyIdEqualTo(returnApplyId);
        example.setOrderByClause("refund_time desc");
        return refundRecordMapper.selectByExample(example);
    }
    
    @Override
    public OmsRefundRecord getByRefundSn(String refundSn) {
        OmsRefundRecordExample example = new OmsRefundRecordExample();
        example.createCriteria().andRefundSnEqualTo(refundSn);
        List<OmsRefundRecord> records = refundRecordMapper.selectByExample(example);
        return records.isEmpty() ? null : records.get(0);
    }
    
    @Override
    public int create(OmsRefundRecord refundRecord) {
        return refundRecordMapper.insertSelective(refundRecord);
    }
    
    @Override
    public int updateStatus(Long id, Byte status) {
        OmsRefundRecord record = new OmsRefundRecord();
        record.setId(id);
        record.setStatus(status);
        return refundRecordMapper.updateByPrimaryKeySelective(record);
    }
} 