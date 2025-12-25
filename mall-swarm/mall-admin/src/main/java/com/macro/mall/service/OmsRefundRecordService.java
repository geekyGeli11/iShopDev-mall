package com.macro.mall.service;

import com.macro.mall.model.OmsRefundRecord;
import java.util.List;

/**
 * 退款记录管理Service
 * Created by macro on 2024/01/01.
 */
public interface OmsRefundRecordService {
    
    /**
     * 根据退货申请ID查询退款记录
     */
    List<OmsRefundRecord> getByReturnApplyId(Long returnApplyId);
    
    /**
     * 根据退款单号查询退款记录
     */
    OmsRefundRecord getByRefundSn(String refundSn);
    
    /**
     * 创建退款记录
     */
    int create(OmsRefundRecord refundRecord);
    
    /**
     * 更新退款记录状态
     */
    int updateStatus(Long id, Byte status);
} 