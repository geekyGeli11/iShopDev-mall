package com.macro.mall.portal.service;

import com.macro.mall.model.OmsGiftRecord;

import java.util.List;

/**
 * 礼物记录管理Service
 */
public interface OmsGiftRecordService {
    
    /**
     * 获取当前用户的送礼记录
     * @param pageNum 页码
     * @param pageSize
     * @return
     */
    List<OmsGiftRecord> listSentGifts(Integer pageNum, Integer pageSize);
    
    /**
     * 获取当前用户的收礼记录
     */
    List<OmsGiftRecord> listReceivedGifts(Integer pageNum, Integer pageSize);
    
    /**
     * 根据ID获取礼物详情
     */
    OmsGiftRecord getDetail(Long id);
    
    /**
     * 更新礼物记录的收礼人ID
     * @param id 礼物记录ID
     * @param receiverId 收礼人ID
     * @return 是否更新成功
     */
    boolean updateReceiverId(Long id, Long receiverId);
} 