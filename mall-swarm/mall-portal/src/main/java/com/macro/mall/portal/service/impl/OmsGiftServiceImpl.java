package com.macro.mall.portal.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.PageHelper;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.exception.Asserts;
import com.macro.mall.common.service.RedisService;
import com.macro.mall.mapper.OmsGiftRecordMapper;
import com.macro.mall.mapper.OmsOrderItemMapper;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.model.OmsGiftRecord;
import com.macro.mall.model.OmsGiftRecordExample;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.OmsOrderItem;
import com.macro.mall.model.OmsOrderItemExample;
import com.macro.mall.model.UmsMember;
import com.macro.mall.model.UmsMemberReceiveAddress;
import com.macro.mall.portal.domain.ConfirmOrderResult;
import com.macro.mall.portal.domain.GiftOrderDetail;
import com.macro.mall.portal.domain.OmsOrderDetail;
import com.macro.mall.portal.domain.OrderParam;
import com.macro.mall.portal.dto.DirectBuyParam;
import com.macro.mall.portal.dto.GiftOrderParam;
import com.macro.mall.portal.service.OmsGiftService;
import com.macro.mall.portal.service.OmsPortalOrderService;
import com.macro.mall.portal.service.UmsMemberReceiveAddressService;
import com.macro.mall.portal.service.UmsMemberService;
import com.macro.mall.portal.component.GiftExpireRemindSender;
import com.macro.mall.common.util.WxMessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 送礼业务Service实现类
 */
@Service
public class OmsGiftServiceImpl implements OmsGiftService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OmsGiftServiceImpl.class);

    // 从配置中读取礼物投递模板ID
    @Value("${template.gift-delivery:}")
    private String giftDeliveryTemplateId;

    @Autowired
    private UmsMemberService memberService;
    
    @Lazy
    @Autowired
    private OmsPortalOrderService portalOrderService;
    
    @Autowired
    private OmsGiftRecordMapper giftRecordMapper;
    
    @Autowired
    private OmsOrderMapper orderMapper;

    @Autowired
    private OmsOrderItemMapper orderItemMapper;
    
    @Autowired
    private UmsMemberReceiveAddressService memberReceiveAddressService;
    
    @Autowired
    private RedisService redisService;

    @Autowired
    private GiftExpireRemindSender giftExpireRemindSender;

    @Autowired(required = false)
    private WxMessageUtil wxMessageUtil;

    @Override
    @Transactional
    public void handleGiftOrderPaySuccess(Long orderId, String giftMessage, String giftPic) {
        LOGGER.info("开始处理送礼订单支付成功，订单ID:{}, 礼品消息:{}, 礼品图片:{}", orderId, giftMessage, giftPic);
        
        // 查询是否存在送礼记录
        List<OmsGiftRecord> giftRecords = giftRecordMapper.selectByExample(createOrderIdCriteria(orderId));
        if (!giftRecords.isEmpty()) {
            // 如果已存在记录，检查是否需要更新礼品消息和图片
            OmsGiftRecord existingRecord = giftRecords.get(0);
            boolean needUpdate = false;
            OmsGiftRecord updateRecord = new OmsGiftRecord();
            updateRecord.setId(existingRecord.getId());
            
            // 检查是否需要更新礼品消息
            if (giftMessage != null && !giftMessage.trim().isEmpty() && 
                (existingRecord.getGiftMessage() == null || existingRecord.getGiftMessage().trim().isEmpty())) {
                updateRecord.setGiftMessage(giftMessage);
                needUpdate = true;
                LOGGER.info("准备更新礼品消息，记录ID:{}, 新消息:{}", existingRecord.getId(), giftMessage);
            }
            
            // 检查是否需要更新礼品图片
            if (giftPic != null && !giftPic.trim().isEmpty() && 
                (existingRecord.getGiftImage() == null || existingRecord.getGiftImage().trim().isEmpty())) {
                updateRecord.setGiftImage(giftPic);
                needUpdate = true;
                LOGGER.info("准备更新礼品图片，记录ID:{}, 新图片:{}", existingRecord.getId(), giftPic);
            }
            
            if (needUpdate) {
                updateRecord.setUpdateTime(new Date());
                try {
                    int updateResult = giftRecordMapper.updateByPrimaryKeySelective(updateRecord);
                    LOGGER.info("送礼记录更新成功，记录ID:{}, 更新结果:{}", existingRecord.getId(), updateResult);
                } catch (Exception e) {
                    LOGGER.error("送礼记录更新失败，记录ID:{}, 错误信息:{}", existingRecord.getId(), e.getMessage(), e);
                    throw e;
                }
            } else {
                LOGGER.info("订单{}已存在送礼记录，且无需更新礼品信息，记录ID:{}", orderId, existingRecord.getId());
            }
            return;
        }
        LOGGER.info("订单{}未找到已存在的送礼记录，继续处理", orderId);
        
        // 查询订单信息
        OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            LOGGER.error("订单{}不存在", orderId);
            return;
        }
        if (!order.getIsGift()) {
            LOGGER.info("订单{}不是送礼订单，isGift:{}", orderId, order.getIsGift());
            return;
        }
        LOGGER.info("订单{}信息查询成功，订单号:{}, 会员ID:{}", orderId, order.getOrderSn(), order.getMemberId());
        
        // 查询订单项信息
        OmsOrderItemExample example = new OmsOrderItemExample();
        example.createCriteria().andOrderIdEqualTo(orderId);
        List<OmsOrderItem> orderItemList = orderItemMapper.selectByExample(example);
        LOGGER.info("订单{}的订单项数量:{}", orderId, orderItemList.size());
        
        // 从订单信息中获取用户信息，避免在回调时调用getCurrentMember()
        UmsMember orderMember = memberService.getById(order.getMemberId());
        if (orderMember == null) {
            LOGGER.error("订单{}对应的用户{}不存在", orderId, order.getMemberId());
            return;
        }
        LOGGER.info("用户信息查询成功，用户ID:{}, 昵称:{}, 用户名:{}", orderMember.getId(), orderMember.getNickname(), orderMember.getUsername());
        
        // 创建送礼记录
        OmsGiftRecord record = new OmsGiftRecord();
        record.setOrderId(order.getId());
        record.setOrderSn(order.getOrderSn());
        record.setSenderId(orderMember.getId());
        // 优先使用昵称，如果昵称为空则使用用户名
        String senderName = (orderMember.getNickname() != null && !orderMember.getNickname().trim().isEmpty()) 
                ? orderMember.getNickname() 
                : orderMember.getUsername();
        record.setSenderName(senderName);
        // 收礼人暂时为空，领取时再设置
        record.setReceiverId(null);
        // 设置送礼消息和图片
        record.setGiftMessage(giftMessage != null ? giftMessage : order.getNote()); // 优先使用传入的消息，否则使用订单备注
        record.setGiftImage(giftPic);
        record.setGiftTime(new Date());
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        
        // 使用订单项信息
        if (!orderItemList.isEmpty()) {
            OmsOrderItem orderItem = orderItemList.get(0);
            record.setProductId(orderItem.getProductId());
            record.setProductPic(orderItem.getProductPic());
            record.setProductName(orderItem.getProductName());
            // 设置礼品金额为订单支付金额
            record.setGiftAmount(order.getPayAmount());
            LOGGER.info("设置礼品信息，商品ID:{}, 商品名称:{}, 礼品金额:{}", orderItem.getProductId(), orderItem.getProductName(), order.getPayAmount());
        }
        
        LOGGER.info("准备插入送礼记录，送礼人:{}, 礼品消息:{}, 礼品图片:{}", senderName, record.getGiftMessage(), record.getGiftImage());
        
        try {
            int insertResult = giftRecordMapper.insert(record);
            LOGGER.info("送礼记录插入成功，订单ID:{}, 送礼人:{}, 插入结果:{}, 记录ID:{}", orderId, senderName, insertResult, record.getId());
            
            // 发送24小时后的过期提醒延迟消息
            if (record.getId() != null) {
                giftExpireRemindSender.sendGiftExpireRemindMessage(record.getId());
                LOGGER.info("已发送礼物过期提醒延迟消息，礼物记录ID:{}", record.getId());
            }
        } catch (Exception e) {
            LOGGER.error("送礼记录插入失败，订单ID:{}, 错误信息:{}", orderId, e.getMessage(), e);
            throw e; // 重新抛出异常，让事务回滚
        }
    }

    @Override
    @Transactional
    public CommonResult receiveGift(Long giftRecordId) {
        // 获取当前会员
        UmsMember currentMember = memberService.getCurrentMember();
        
        // 查询礼品记录
        OmsGiftRecord giftRecord = giftRecordMapper.selectByPrimaryKey(giftRecordId);
        if (giftRecord == null) {
            return CommonResult.failed("礼品记录不存在");
        }
        
        // 检查礼品是否已被领取
        if (giftRecord.getReceiverId() != null) {
            return CommonResult.failed("该礼品已被领取");
        }
        
        // 更新礼品记录
        OmsGiftRecord updateRecord = new OmsGiftRecord();
        updateRecord.setId(giftRecordId);
        updateRecord.setReceiverId(currentMember.getId());
        updateRecord.setUpdateTime(new Date());
        
        giftRecordMapper.updateByPrimaryKeySelective(updateRecord);
        
        // 同步更新关联订单的member_id为收礼人
        OmsOrder updateOrder = new OmsOrder();
        updateOrder.setId(giftRecord.getOrderId());
        updateOrder.setMemberId(currentMember.getId());
        updateOrder.setMemberUsername(currentMember.getUsername());
        orderMapper.updateByPrimaryKeySelective(updateOrder);
        
        return CommonResult.success("礼品领取成功");
    }

    @Override
    @Transactional
    public CommonResult updateGiftOrderAddress(Long giftRecordId, Long memberReceiveAddressId) {
        // 获取当前会员
        UmsMember currentMember = memberService.getCurrentMember();
        
        // 查询礼品记录
        OmsGiftRecord giftRecord = giftRecordMapper.selectByPrimaryKey(giftRecordId);
        if (giftRecord == null) {
            return CommonResult.failed("礼品记录不存在");
        }
        
        // 检查礼品是否已被当前用户领取
        if (giftRecord.getReceiverId() == null || !currentMember.getId().equals(giftRecord.getReceiverId())) {
            return CommonResult.failed("您没有权限更新该礼品的收货地址");
        }
        
        // 查询订单信息
        OmsOrder order = orderMapper.selectByPrimaryKey(giftRecord.getOrderId());
        if (order == null) {
            return CommonResult.failed("订单不存在");
        }
        
        // 查询收货地址信息
        UmsMemberReceiveAddress address = memberReceiveAddressService.getItem(memberReceiveAddressId);
        if (address == null) {
            return CommonResult.failed("收货地址不存在");
        }
        
        // 更新订单收货信息
        OmsOrder updateOrder = new OmsOrder();
        updateOrder.setId(order.getId());
        updateOrder.setReceiverName(address.getName());
        updateOrder.setReceiverPhone(address.getPhoneNumber());
        updateOrder.setReceiverPostCode(address.getPostCode());
        updateOrder.setReceiverProvince(address.getProvince());
        updateOrder.setReceiverCity(address.getCity());
        updateOrder.setReceiverRegion(address.getRegion());
        updateOrder.setReceiverDetailAddress(address.getDetailAddress());
        // 更新为收礼人的信息
        updateOrder.setMemberId(currentMember.getId());
        updateOrder.setMemberUsername(currentMember.getUsername());
        
        orderMapper.updateByPrimaryKeySelective(updateOrder);
        
        return CommonResult.success("收货地址更新成功");
    }

    @Override
    public CommonResult listSentGifts(Integer pageNum, Integer pageSize) {
        UmsMember currentMember = memberService.getCurrentMember();
        PageHelper.startPage(pageNum, pageSize);
        
        OmsGiftRecordExample example = createSenderIdCriteria(currentMember.getId());
        // 按送礼时间倒序排列，最新的在最前面
        example.setOrderByClause("gift_time DESC");
        
        List<OmsGiftRecord> giftRecordList = giftRecordMapper.selectByExample(example);
        return CommonResult.success(CommonPage.restPage(giftRecordList));
    }

    @Override
    public CommonResult listReceivedGifts(Integer pageNum, Integer pageSize) {
        UmsMember currentMember = memberService.getCurrentMember();
        PageHelper.startPage(pageNum, pageSize);
        
        OmsGiftRecordExample example = createReceiverIdCriteria(currentMember.getId());
        // 按送礼时间倒序排列，最新的在最前面
        example.setOrderByClause("gift_time DESC");
        
        List<OmsGiftRecord> giftRecordList = giftRecordMapper.selectByExample(example);
        return CommonResult.success(CommonPage.restPage(giftRecordList));
    }

    @Override
    public CommonResult getGiftDetail(Long giftRecordId) {
        OmsGiftRecord giftRecord = giftRecordMapper.selectByPrimaryKey(giftRecordId);
        if (giftRecord == null) {
            return CommonResult.failed("礼品记录不存在");
        }
        return CommonResult.success(giftRecord);
    }

    @Override
    public CommonResult<GiftOrderDetail> getGiftOrderDetail(Long orderId) {
        
        // 查询订单详情
        OmsOrderDetail orderDetail = portalOrderService.detail(orderId);
        if (orderDetail == null) {
            return CommonResult.failed("订单不存在");
        }
        
        // 检查订单是否为送礼订单
        if (orderDetail.getIsGift() == null || !orderDetail.getIsGift()) {
            return CommonResult.failed("该订单不是送礼订单");
        }
        
        // 创建送礼订单详情对象
        GiftOrderDetail giftOrderDetail = new GiftOrderDetail();
        BeanUtil.copyProperties(orderDetail, giftOrderDetail);
        
        // 查询送礼记录
        OmsGiftRecordExample example = new OmsGiftRecordExample();
        example.createCriteria().andOrderIdEqualTo(orderId);
        List<OmsGiftRecord> giftRecords = giftRecordMapper.selectByExample(example);
        
        if (CollUtil.isEmpty(giftRecords)) {
            return CommonResult.failed("送礼记录不存在");
        }
        
        // 使用第一条送礼记录（通常一个订单只有一条送礼记录）
        OmsGiftRecord giftRecord = giftRecords.get(0);
        giftOrderDetail.setGiftRecord(giftRecord);
        
        // 判断礼物是否已被接收
        boolean isReceived = giftRecord.getReceiverId() != null;
        giftOrderDetail.setIsReceived(isReceived);
        
        // 判断礼物是否已过期（24小时后过期）
        Date giftTime = giftRecord.getGiftTime();
        Date expireTime = DateUtil.offsetHour(giftTime, 24);
        // 如果礼物已被领取，则不论时间如何，都不会过期
        boolean isExpired = isReceived ? false : new Date().after(expireTime);
        giftOrderDetail.setIsExpired(isExpired);
        giftOrderDetail.setExpireTime(expireTime);
        
        // 重新查询订单商品信息，确保完整性
        if (orderDetail.getOrderItemList() == null || orderDetail.getOrderItemList().isEmpty()) {
            OmsOrderItemExample orderItemExample = new OmsOrderItemExample();
            orderItemExample.createCriteria().andOrderIdEqualTo(orderId);
            List<OmsOrderItem> orderItemList = orderItemMapper.selectByExample(orderItemExample);
            giftOrderDetail.setOrderItemList(orderItemList);
        }
        
        LOGGER.info("送礼订单详情查询成功, 订单ID:{}, 礼物记录ID:{}, 是否已接收:{}, 是否已过期:{}", 
                 orderId, giftRecord.getId(), isReceived, isExpired);
        
        return CommonResult.success(giftOrderDetail);
    }
    
    /**
     * 创建按订单ID查询的条件
     */
    private OmsGiftRecordExample createOrderIdCriteria(Long orderId) {
        OmsGiftRecordExample example = new OmsGiftRecordExample();
        example.createCriteria().andOrderIdEqualTo(orderId);
        return example;
    }
    
    /**
     * 创建按送礼人ID查询的条件
     */
    private OmsGiftRecordExample createSenderIdCriteria(Long senderId) {
        OmsGiftRecordExample example = new OmsGiftRecordExample();
        example.createCriteria().andSenderIdEqualTo(senderId);
        return example;
    }
    
    /**
     * 创建按收礼人ID查询的条件
     */
    private OmsGiftRecordExample createReceiverIdCriteria(Long receiverId) {
        OmsGiftRecordExample example = new OmsGiftRecordExample();
        example.createCriteria().andReceiverIdEqualTo(receiverId);
        return example;
    }

    @Override
    public void handleGiftExpireRemind(Long giftRecordId) {
        LOGGER.info("开始处理礼物过期提醒，礼物记录ID：{}", giftRecordId);
        
        try {
            // 查询礼物记录
            OmsGiftRecord giftRecord = giftRecordMapper.selectByPrimaryKey(giftRecordId);
            if (giftRecord == null) {
                LOGGER.warn("礼物记录不存在，跳过过期提醒处理，记录ID：{}", giftRecordId);
                return;
            }
            
            // 检查礼物是否已被领取
            if (giftRecord.getReceiverId() != null) {
                LOGGER.info("礼物已被领取，跳过过期提醒，记录ID：{}", giftRecordId);
                return;
            }
            
            // 检查礼物是否确实已过期（24小时后）
            Date giftTime = giftRecord.getGiftTime();
            if (giftTime == null) {
                LOGGER.warn("礼物时间为空，跳过过期提醒，记录ID：{}", giftRecordId);
                return;
            }
            
            long currentTime = System.currentTimeMillis();
            long giftTimeMs = giftTime.getTime();
            long expireTimeMs = 24 * 60 * 60 * 1000L; // 24小时
            
            if (currentTime - giftTimeMs < expireTimeMs) {
                LOGGER.info("礼物尚未过期，跳过过期提醒，记录ID：{}", giftRecordId);
                return;
            }
            
            // 查询发送者信息，用于发送订阅消息
            UmsMember senderMember = memberService.getById(giftRecord.getSenderId());
            if (senderMember == null || senderMember.getOpenid() == null) {
                LOGGER.warn("发送者信息不完整或无openid，无法发送订阅消息，记录ID：{}", giftRecordId);
                return;
            }
            
            // 构建订阅消息数据
            Map<String, String> templateData = new HashMap<>();
            templateData.put("thing1", giftRecord.getProductName() != null ? giftRecord.getProductName() : "精美礼品"); // 礼品名称
            templateData.put("phrase2", "即将过期"); // 状态
            templateData.put("date3", cn.hutool.core.date.DateUtil.formatDateTime(new Date())); // 提醒时间
            templateData.put("thing4", "您的礼物还未被领取，即将过期，请及时处理"); // 备注
            
            // 构建小程序跳转路径，让用户点击卡片能进入到gift-confirm页面
            String miniProgramPath = "/pages/gift-bag/gift-confirm?orderId=" + giftRecord.getOrderId() + 
                                   "&fromGiftList=1&userRole=sender";
            
            // 发送订阅消息（如果微信服务可用）
            if (wxMessageUtil != null) {
                boolean sendResult = wxMessageUtil.sendSubscribeMessage(
                    senderMember.getOpenid(), 
                    giftDeliveryTemplateId, 
                    templateData, 
                    miniProgramPath
                );
                
                if (sendResult) {
                    LOGGER.info("礼物过期提醒订阅消息发送成功，记录ID：{}", giftRecordId);
                } else {
                    LOGGER.warn("礼物过期提醒订阅消息发送失败，记录ID：{}", giftRecordId);
                }
            } else {
                LOGGER.info("微信服务不可用，跳过发送礼物过期提醒订阅消息，记录ID：{}", giftRecordId);
            }
            
        } catch (Exception e) {
            LOGGER.error("处理礼物过期提醒异常，记录ID：{}", giftRecordId, e);
        }
    }
} 