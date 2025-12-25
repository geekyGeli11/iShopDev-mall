package com.macro.mall.portal.service.impl;

import com.macro.mall.mapper.OmsOrderReturnApplyMapper;
import com.macro.mall.mapper.OmsOrderReturnApplyDetailMapper;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.mapper.OmsOrderItemMapper;
import com.macro.mall.mapper.OmsCompanyAddressMapper;
import com.macro.mall.model.*;
import com.macro.mall.portal.domain.OmsOrderReturnApplyParam;
import com.macro.mall.portal.domain.OmsOrderReturnApplyDeliveryParam;
import com.macro.mall.portal.domain.OmsOrderReturnApplyMultiStepParam;
import com.macro.mall.portal.dto.RefundNotificationParam;
import com.macro.mall.portal.service.AdminNotificationFeignService;
import com.macro.mall.portal.service.OmsPortalOrderReturnApplyService;
import com.macro.mall.portal.service.UmsMemberService;
import com.macro.mall.mapper.OmsOrderOperateHistoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 订单退货管理Service实现类
 * Created by macro on 2018/10/17.
 */
@Service
public class OmsPortalOrderReturnApplyServiceImpl implements OmsPortalOrderReturnApplyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OmsPortalOrderReturnApplyServiceImpl.class);

    @Autowired
    private OmsOrderReturnApplyMapper returnApplyMapper;
    @Autowired
    private OmsOrderReturnApplyDetailMapper returnApplyDetailMapper;
    @Autowired
    private OmsOrderMapper orderMapper;
    @Autowired
    private OmsOrderItemMapper orderItemMapper;
    @Autowired
    private UmsMemberService memberService;
    @Autowired
    private OmsCompanyAddressMapper companyAddressMapper;
    @Autowired
    private OmsOrderOperateHistoryMapper orderOperateHistoryMapper;
    @Autowired(required = false)
    private AdminNotificationFeignService adminNotificationFeignService;
    
    @Override
    public int create(OmsOrderReturnApplyParam returnApply) {
        OmsOrderReturnApply realApply = new OmsOrderReturnApply();
        BeanUtils.copyProperties(returnApply, realApply);
        
        // 获取当前用户信息
        UmsMember currentMember = memberService.getCurrentMember();
        
        // 获取订单信息
        OmsOrder order = orderMapper.selectByPrimaryKey(returnApply.getOrderId());
        
        // 获取订单项信息来计算退款金额
        OmsOrderItemExample orderItemExample = new OmsOrderItemExample();
        orderItemExample.createCriteria()
                .andOrderIdEqualTo(returnApply.getOrderId())
                .andProductIdEqualTo(returnApply.getProductId());
        List<OmsOrderItem> orderItemList = orderItemMapper.selectByExample(orderItemExample);
        
        // 设置基本信息
        realApply.setCreateTime(new Date());
        realApply.setStatus(0);
        
        // 设置用户信息
        if (currentMember != null) {
            realApply.setMemberUsername(currentMember.getUsername());
        }
        
        // 设置订单信息
        if (order != null) {
            realApply.setOrderSn(order.getOrderSn());
        }
        
        // 设置商品信息和退款金额
        if (!orderItemList.isEmpty()) {
            OmsOrderItem orderItem = orderItemList.get(0);
            
            // 如果前端没有传递这些信息，从订单项中获取
            if (realApply.getProductPic() == null || realApply.getProductPic().isEmpty()) {
                realApply.setProductPic(orderItem.getProductPic());
            }
            if (realApply.getProductName() == null || realApply.getProductName().isEmpty()) {
                realApply.setProductName(orderItem.getProductName());
            }
            if (realApply.getProductBrand() == null || realApply.getProductBrand().isEmpty()) {
                realApply.setProductBrand(orderItem.getProductBrand());
            }
            if (realApply.getProductAttr() == null || realApply.getProductAttr().isEmpty()) {
                realApply.setProductAttr(orderItem.getProductAttr());
            }
            if (realApply.getProductPrice() == null) {
                realApply.setProductPrice(orderItem.getProductPrice());
            }
            if (realApply.getProductRealPrice() == null) {
                realApply.setProductRealPrice(orderItem.getRealAmount());
            }
            
            // 计算退款金额：实际支付单价 * 退货数量
            BigDecimal returnAmount = orderItem.getRealAmount().multiply(new BigDecimal(returnApply.getProductCount()));
            realApply.setReturnAmount(returnAmount);
        }
        
        return returnApplyMapper.insert(realApply);
    }
    
    @Override
    public List<OmsOrderReturnApply> getByOrderId(Long orderId) {
        OmsOrderReturnApplyExample example = new OmsOrderReturnApplyExample();
        example.createCriteria().andOrderIdEqualTo(orderId);
        example.setOrderByClause("create_time desc");
        return returnApplyMapper.selectByExample(example);
    }

    @Override
    public int updateDeliveryInfo(OmsOrderReturnApplyDeliveryParam deliveryParam) {
        OmsOrderReturnApply returnApply = new OmsOrderReturnApply();
        returnApply.setId(deliveryParam.getReturnApplyId());
        returnApply.setDeliveryCompany(deliveryParam.getDeliveryCompany());
        returnApply.setDeliverySn(deliveryParam.getDeliverySn());
        return returnApplyMapper.updateByPrimaryKeySelective(returnApply);
    }

    @Override
    @Transactional
    public int createMultiStep(OmsOrderReturnApplyMultiStepParam returnApply) {
        // 获取当前用户信息
        UmsMember currentMember = memberService.getCurrentMember();

        // 获取订单信息
        OmsOrder order = orderMapper.selectByPrimaryKey(returnApply.getOrderId());
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 创建主申请记录
        OmsOrderReturnApply mainApply = new OmsOrderReturnApply();
        mainApply.setOrderId(returnApply.getOrderId());
        mainApply.setOrderSn(order.getOrderSn());
        mainApply.setCreateTime(new Date());
        mainApply.setStatus(0); // 待处理
        mainApply.setReturnType(returnApply.getReturnType());
        mainApply.setRefundProcessStatus((byte) 0); // 未退款
        mainApply.setReason(returnApply.getReason());
        mainApply.setDescription(returnApply.getDescription());
        mainApply.setProofPics(returnApply.getProofPics());
        mainApply.setReturnName(returnApply.getReturnName());
        mainApply.setReturnPhone(returnApply.getReturnPhone());

        // 设置用户信息
        if (currentMember != null) {
            mainApply.setMemberUsername(currentMember.getUsername());
        }

        // 计算总退款金额
        BigDecimal totalReturnAmount = BigDecimal.ZERO;
        for (OmsOrderReturnApplyMultiStepParam.ReturnProductItem item : returnApply.getProductItems()) {
            if (item.getReturnAmount() != null) {
                totalReturnAmount = totalReturnAmount.add(item.getReturnAmount());
            }
        }
        mainApply.setReturnAmount(totalReturnAmount);

        // 如果只有一个商品，设置商品信息到主表（兼容性）
        if (returnApply.getProductItems().size() == 1) {
            OmsOrderReturnApplyMultiStepParam.ReturnProductItem firstItem = returnApply.getProductItems().get(0);

            // 获取订单项信息
            OmsOrderItem orderItem = orderItemMapper.selectByPrimaryKey(firstItem.getOrderItemId());
            if (orderItem != null) {
                mainApply.setProductId(firstItem.getProductId());
                mainApply.setProductPic(orderItem.getProductPic());
                mainApply.setProductName(orderItem.getProductName());
                mainApply.setProductBrand(orderItem.getProductBrand());
                mainApply.setProductAttr(orderItem.getProductAttr());
                mainApply.setProductCount(firstItem.getProductCount());
                mainApply.setProductPrice(orderItem.getProductPrice());
                mainApply.setProductRealPrice(orderItem.getRealAmount());
            }
        }

        // 插入主申请记录
        int result = returnApplyMapper.insert(mainApply);

        // 插入申请详情记录
        for (OmsOrderReturnApplyMultiStepParam.ReturnProductItem item : returnApply.getProductItems()) {
            // 获取订单项信息
            OmsOrderItem orderItem = orderItemMapper.selectByPrimaryKey(item.getOrderItemId());
            if (orderItem != null) {
                OmsOrderReturnApplyDetail detail = new OmsOrderReturnApplyDetail();
                detail.setReturnApplyId(mainApply.getId());
                detail.setOrderItemId(item.getOrderItemId());
                detail.setProductId(item.getProductId());
                detail.setProductName(orderItem.getProductName());
                detail.setProductPic(orderItem.getProductPic());
                detail.setProductAttr(orderItem.getProductAttr());
                detail.setProductPrice(orderItem.getProductPrice());
                detail.setProductQuantity(item.getProductCount());
                detail.setReturnAmount(item.getReturnAmount());
                detail.setCreateTime(new Date());

                returnApplyDetailMapper.insert(detail);
            }
        }

        // 更新订单售后状态
        updateOrderAfterSaleStatus(returnApply.getOrderId(), 1); // 售后申请中

        // 发送退款申请通知给管理员
        try {
            sendRefundNotificationToAdmin(mainApply, order, currentMember);
        } catch (Exception e) {
            LOGGER.error("发送退款申请通知失败，申请ID: {}", mainApply.getId(), e);
            // 通知发送失败不影响申请提交
        }

        return result;
    }
    
    /**
     * 发送退款申请通知给管理员
     */
    private void sendRefundNotificationToAdmin(OmsOrderReturnApply returnApply, OmsOrder order, UmsMember member) {
        if (adminNotificationFeignService == null) {
            LOGGER.debug("管理员通知服务未配置，跳过退款申请通知发送");
            return;
        }
        
        try {
            RefundNotificationParam param = new RefundNotificationParam();
            param.setOrderSn(returnApply.getOrderSn());
            param.setProductName(returnApply.getProductName() != null ? returnApply.getProductName() : "商品");
            param.setRefundAmount(returnApply.getReturnAmount());
            param.setApplyTime(returnApply.getCreateTime());
            param.setPhoneNumber(returnApply.getReturnPhone() != null ? returnApply.getReturnPhone() : 
                                 (member != null ? member.getPhone() : "未提供"));
            
            adminNotificationFeignService.notifyRefundApplication(param);
            LOGGER.info("退款申请通知请求已发送，订单号: {}", returnApply.getOrderSn());
        } catch (Exception e) {
            LOGGER.error("发送退款申请通知失败，订单号: {}", returnApply.getOrderSn(), e);
        }
    }

    @Override
    public int updateOrderAfterSaleStatus(Long orderId, Integer afterSaleStatus) {
        OmsOrder order = new OmsOrder();
        order.setId(orderId);
        order.setAfterSaleStatus(afterSaleStatus);
        return orderMapper.updateByPrimaryKeySelective(order);
    }

    @Override
    public OmsOrderReturnApply getReturnApplyDetail(Long returnApplyId) {
        OmsOrderReturnApply returnApply = returnApplyMapper.selectByPrimaryKey(returnApplyId);

        // 如果是退货退款类型，需要获取退货地址信息
        if (returnApply != null && returnApply.getReturnType() != null && returnApply.getReturnType() == 2) {
            // 获取退货地址信息
            if (returnApply.getCompanyAddressId() != null) {
                // 从公司地址表获取地址信息
                OmsCompanyAddress companyAddress = companyAddressMapper.selectByPrimaryKey(returnApply.getCompanyAddressId());
                if (companyAddress != null) {
                    // 将地址信息设置到返回对象中（通过handleNote字段传递，前端解析）
                    String addressInfo = String.format("退货地址信息|收货人:%s|电话:%s|地址:%s %s %s %s",
                        companyAddress.getName(),
                        companyAddress.getPhone(),
                        companyAddress.getProvince(),
                        companyAddress.getCity(),
                        companyAddress.getRegion(),
                        companyAddress.getDetailAddress()
                    );

                    // 如果handleNote为空，直接设置；否则追加
                    if (returnApply.getHandleNote() == null || returnApply.getHandleNote().trim().isEmpty()) {
                        returnApply.setHandleNote(addressInfo);
                    } else if (!returnApply.getHandleNote().contains("退货地址信息")) {
                        returnApply.setHandleNote(returnApply.getHandleNote() + "\n" + addressInfo);
                    }
                }
            }
        }

        return returnApply;
    }

    @Override
    public int cancelReturnApply(Long returnApplyId) {
        // 只有待审核状态(status=0)的申请才能取消
        OmsOrderReturnApply returnApply = returnApplyMapper.selectByPrimaryKey(returnApplyId);
        if (returnApply == null) {
            throw new RuntimeException("售后申请不存在");
        }

        if (returnApply.getStatus() != 0) {
            throw new RuntimeException("只有待审核状态的申请才能取消");
        }

        // 更新申请状态为已取消(status=4)
        OmsOrderReturnApply updateApply = new OmsOrderReturnApply();
        updateApply.setId(returnApplyId);
        updateApply.setStatus(4); // 4表示已取消
        updateApply.setHandleNote("用户主动取消");
        updateApply.setHandleTime(new Date());

        int result = returnApplyMapper.updateByPrimaryKeySelective(updateApply);

        // 同时更新订单的售后状态
        if (result > 0) {
            OmsOrder order = new OmsOrder();
            order.setId(returnApply.getOrderId());
            order.setAfterSaleStatus(0); // 恢复为无售后状态
            orderMapper.updateByPrimaryKeySelective(order);
        }

        return result;
    }

    @Override
    public int updateReturnApplyStatus(Long returnApplyId, Integer status, String handleNote) {
        OmsOrderReturnApply returnApply = new OmsOrderReturnApply();
        returnApply.setId(returnApplyId);
        returnApply.setStatus(status);
        returnApply.setHandleNote(handleNote);
        returnApply.setHandleTime(new Date());

        // 根据状态更新订单的售后状态
        OmsOrderReturnApply existingApply = returnApplyMapper.selectByPrimaryKey(returnApplyId);
        if (existingApply != null) {
            Integer orderAfterSaleStatus;
            switch (status) {
                case 0:
                    orderAfterSaleStatus = 1; // 售后申请中
                    break;
                case 1:
                    orderAfterSaleStatus = 2; // 售后处理中
                    break;
                case 2:
                    orderAfterSaleStatus = 3; // 售后完成
                    break;
                case 3:
                    orderAfterSaleStatus = 4; // 售后拒绝
                    break;
                default:
                    orderAfterSaleStatus = 1;
            }
            updateOrderAfterSaleStatus(existingApply.getOrderId(), orderAfterSaleStatus);
        }

        return returnApplyMapper.updateByPrimaryKeySelective(returnApply);
    }

    @Override
    public int updateRefundProcessStatus(Long returnApplyId, Byte refundProcessStatus, String refundTransactionId, String refundFailReason) {
        OmsOrderReturnApply returnApply = new OmsOrderReturnApply();
        returnApply.setId(returnApplyId);
        returnApply.setRefundProcessStatus(refundProcessStatus);
        returnApply.setRefundTransactionId(refundTransactionId);
        returnApply.setRefundFailReason(refundFailReason);

        // 如果退款成功，设置退款时间
        if (refundProcessStatus == 2) {
            returnApply.setRefundTime(new Date());
            returnApply.setRefundStatus((byte) 1); // 兼容原有字段
            returnApply.setStatus(2); // 申请状态设置为已完成
        }

        int result = returnApplyMapper.updateByPrimaryKeySelective(returnApply);

        // 如果退款成功，检查并更新订单状态
        if (result > 0 && refundProcessStatus == 2) {
            try {
                // 获取完整的退货申请信息
                OmsOrderReturnApply fullReturnApply = returnApplyMapper.selectByPrimaryKey(returnApplyId);
                if (fullReturnApply != null && fullReturnApply.getOrderId() != null) {
                    checkAndUpdateOrderStatusAfterRefund(fullReturnApply.getOrderId());
                }
            } catch (Exception e) {
                // 订单状态更新失败不影响退款状态更新
                LOGGER.error("Portal端退款成功后更新订单状态失败，退货申请ID: {}", returnApplyId, e);
            }
        }

        return result;
    }

    @Override
    public List<OmsOrderReturnApply> getByOrderIdAndStatus(Long orderId, Integer status) {
        OmsOrderReturnApplyExample example = new OmsOrderReturnApplyExample();
        OmsOrderReturnApplyExample.Criteria criteria = example.createCriteria();
        criteria.andOrderIdEqualTo(orderId);
        if (status != null) {
            criteria.andStatusEqualTo(status);
        }
        example.setOrderByClause("create_time desc");
        return returnApplyMapper.selectByExample(example);
    }

    @Override
    public List<OmsOrderReturnApply> getUserReturnApplyList(Long memberId, Integer status, Integer pageNum, Integer pageSize) {
        // 这里需要通过订单表关联查询，暂时简化实现
        // 实际项目中可能需要创建专门的DAO方法
        OmsOrderReturnApplyExample example = new OmsOrderReturnApplyExample();
        OmsOrderReturnApplyExample.Criteria criteria = example.createCriteria();

        if (status != null) {
            criteria.andStatusEqualTo(status);
        }

        example.setOrderByClause("create_time desc");

        // 这里简化处理，实际应该通过订单表关联查询用户的申请
        return returnApplyMapper.selectByExample(example);
    }

    @Override
    @Transactional
    public int fillReturnDeliveryInfo(Long returnApplyId, String deliveryCompany, String deliverySn, String deliveryNote) {
        // 验证售后申请是否存在且状态正确
        OmsOrderReturnApply returnApply = returnApplyMapper.selectByPrimaryKey(returnApplyId);
        if (returnApply == null) {
            throw new RuntimeException("售后申请不存在");
        }

        if (returnApply.getStatus() != 1) {
            throw new RuntimeException("当前状态不允许填写快递信息");
        }

        // 更新快递信息
        OmsOrderReturnApply updateApply = new OmsOrderReturnApply();
        updateApply.setId(returnApplyId);
        updateApply.setDeliveryCompany(deliveryCompany);
        updateApply.setDeliverySn(deliverySn);
        // 将快递备注信息存储到处理备注字段中
        if (deliveryNote != null && !deliveryNote.trim().isEmpty()) {
            updateApply.setHandleNote("用户快递备注：" + deliveryNote);
        }

        return returnApplyMapper.updateByPrimaryKeySelective(updateApply);
    }

    @Override
    public Map<String, Object> getReturnAddress(Long returnApplyId) {
        OmsOrderReturnApply returnApply = returnApplyMapper.selectByPrimaryKey(returnApplyId);
        if (returnApply == null) {
            throw new RuntimeException("售后申请不存在");
        }

        // 获取退货地址信息
        Map<String, Object> addressInfo = new HashMap<>();

        if (returnApply.getCompanyAddressId() != null) {
            // 从公司地址表获取退货地址
            // 这里需要查询公司地址表，暂时返回模拟数据
            addressInfo.put("receiverName", "客服中心");
            addressInfo.put("receiverPhone", "400-123-4567");
            addressInfo.put("receiverAddress", "广东省深圳市南山区科技园南区软件产业基地");
            addressInfo.put("receiverPostCode", "518000");
            addressInfo.put("note", "请在包裹上注明退货申请单号：" + returnApply.getId());
        } else {
            // 使用默认退货地址
            addressInfo.put("receiverName", "退货处理中心");
            addressInfo.put("receiverPhone", "400-123-4567");
            addressInfo.put("receiverAddress", "广东省深圳市南山区科技园南区软件产业基地");
            addressInfo.put("receiverPostCode", "518000");
            addressInfo.put("note", "请在包裹上注明退货申请单号：" + returnApply.getId());
        }

        return addressInfo;
    }

    @Override
    public Map<String, Object> getDeliveryTrackingInfo(String deliveryCompany, String deliverySn) {
        // 这里应该调用第三方物流查询接口
        // 暂时返回模拟数据
        Map<String, Object> trackingInfo = new HashMap<>();
        trackingInfo.put("deliveryCompany", deliveryCompany);
        trackingInfo.put("deliverySn", deliverySn);
        trackingInfo.put("status", "运输中");

        List<Map<String, Object>> trackingList = new ArrayList<>();
        Map<String, Object> track1 = new HashMap<>();
        track1.put("time", "2024-01-15 10:00:00");
        track1.put("status", "已揽收");
        track1.put("location", "深圳市");
        track1.put("description", "快件已在深圳市揽收");
        trackingList.add(track1);

        Map<String, Object> track2 = new HashMap<>();
        track2.put("time", "2024-01-15 14:30:00");
        track2.put("status", "运输中");
        track2.put("location", "广州市");
        track2.put("description", "快件正在运输途中");
        trackingList.add(track2);

        trackingInfo.put("trackingList", trackingList);

        return trackingInfo;
    }

    /**
     * 检查并更新订单状态（Portal端退款成功后）
     * 如果订单的所有退款金额达到订单支付金额，则将订单状态更新为已关闭
     *
     * @param orderId 订单ID
     */
    private void checkAndUpdateOrderStatusAfterRefund(Long orderId) {
        try {
            // 1. 查询订单信息
            OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
            if (order == null) {
                LOGGER.warn("Portal端：订单不存在，无法更新状态，订单ID: {}", orderId);
                return;
            }

            // 如果订单已经是已关闭状态，无需重复更新
            if (order.getStatus() != null && order.getStatus() == 4) {
                LOGGER.info("Portal端：订单已经是已关闭状态，无需更新，订单ID: {}", orderId);
                return;
            }

            // 2. 查询该订单所有已完成且退款成功的退货申请
            OmsOrderReturnApplyExample example = new OmsOrderReturnApplyExample();
            example.createCriteria()
                    .andOrderIdEqualTo(orderId)
                    .andStatusEqualTo(2) // 已完成
                    .andRefundStatusEqualTo((byte) 1); // 已退款

            List<OmsOrderReturnApply> completedRefunds = returnApplyMapper.selectByExample(example);

            // 3. 计算总退款金额
            BigDecimal totalRefundAmount = completedRefunds.stream()
                    .map(OmsOrderReturnApply::getReturnAmount)
                    .filter(amount -> amount != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 4. 获取订单支付金额
            BigDecimal orderPayAmount = order.getPayAmount();
            if (orderPayAmount == null) {
                orderPayAmount = BigDecimal.ZERO;
            }

            LOGGER.info("Portal端订单退款检查，订单ID: {}, 订单支付金额: {}, 总退款金额: {}",
                orderId, orderPayAmount, totalRefundAmount);

            // 5. 如果总退款金额 >= 订单支付金额，更新订单状态为已关闭
            if (totalRefundAmount.compareTo(orderPayAmount) >= 0) {
                OmsOrder updateOrder = new OmsOrder();
                updateOrder.setId(orderId);
                updateOrder.setStatus(4); // 已关闭
                updateOrder.setModifyTime(new Date());

                int updateResult = orderMapper.updateByPrimaryKeySelective(updateOrder);
                if (updateResult > 0) {
                    // 记录操作历史
                    OmsOrderOperateHistory history = new OmsOrderOperateHistory();
                    history.setOrderId(orderId);
                    history.setCreateTime(new Date());
                    history.setOperateMan("系统");
                    history.setOrderStatus(4);
                    history.setNote(String.format("Portal端退款完成，订单自动关闭（退款金额：¥%.2f）", totalRefundAmount));
                    orderOperateHistoryMapper.insert(history);

                    LOGGER.info("Portal端订单状态已更新为已关闭，订单ID: {}, 总退款金额: {}", orderId, totalRefundAmount);
                } else {
                    LOGGER.error("Portal端更新订单状态失败，订单ID: {}", orderId);
                }
            } else {
                LOGGER.info("Portal端部分退款，订单状态保持不变，订单ID: {}, 剩余金额: {}",
                    orderId, orderPayAmount.subtract(totalRefundAmount));
            }

        } catch (Exception e) {
            LOGGER.error("Portal端检查并更新订单状态异常，订单ID: {}", orderId, e);
        }
    }
}
