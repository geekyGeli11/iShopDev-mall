package com.macro.mall.portal.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.mapper.OmsGiftRecordMapper;
import com.macro.mall.model.OmsGiftRecord;
import com.macro.mall.model.OmsGiftRecordExample;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.dao.OmsGiftRecordDao;
import com.macro.mall.portal.service.OmsGiftRecordService;
import com.macro.mall.portal.service.UmsMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 礼物记录管理Service实现类
 */
@Service
public class OmsGiftRecordServiceImpl implements OmsGiftRecordService {
    
    @Autowired
    private OmsGiftRecordMapper giftRecordMapper;
    
    @Autowired
    private OmsGiftRecordDao giftRecordDao;
    
    @Autowired
    private UmsMemberService memberService;
    
    @Override
    public List<OmsGiftRecord> listSentGifts(Integer pageNum, Integer pageSize) {
        // 获取当前登录用户
        UmsMember currentMember = memberService.getCurrentMember();
        int offset = (pageNum - 1) * pageSize;
        
        // 查询用户送出的礼物记录
        return giftRecordDao.getSentGiftList(currentMember.getId(), offset, pageSize);
    }
    
    @Override
    public List<OmsGiftRecord> listReceivedGifts(Integer pageNum, Integer pageSize) {
        // 获取当前登录用户
        UmsMember currentMember = memberService.getCurrentMember();
        int offset = (pageNum - 1) * pageSize;
        
        // 查询用户收到的礼物记录
        return giftRecordDao.getReceivedGiftList(currentMember.getId(), offset, pageSize);
    }
    
    @Override
    public OmsGiftRecord getDetail(Long id) {
        return giftRecordMapper.selectByPrimaryKey(id);
    }
    
    @Override
    public boolean updateReceiverId(Long id, Long receiverId) {
        if (id == null || receiverId == null) {
            return false;
        }
        
        // 根据ID获取礼物记录
        OmsGiftRecord giftRecord = giftRecordMapper.selectByPrimaryKey(id);
        if (giftRecord == null) {
            return false;
        }
        
        // 更新收礼人ID和更新时间
        giftRecord.setReceiverId(receiverId);
        giftRecord.setUpdateTime(new Date());
        
        // 保存更新
        int count = giftRecordMapper.updateByPrimaryKey(giftRecord);
        return count > 0;
    }
} 