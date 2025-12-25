package com.macro.mall.service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 管理员通知服务接口
 * 用于向绑定了微信的管理员发送消息通知
 */
public interface AdminNotificationService {
    
    /**
     * 发送销售单待审批通知
     * @param saleNo 销售单号
     * @param storeName 门店名称
     * @param operatorName 提交人
     * @param totalAmount 销售金额
     */
    void notifySaleSubmitted(String saleNo, String storeName, String operatorName, String totalAmount);
    
    /**
     * 发送调货申请通知
     * @param transferNo 调货单号
     * @param fromStoreName 调出门店
     * @param toStoreName 调入门店
     * @param operatorName 申请人
     */
    void notifyTransferRequest(String transferNo, String fromStoreName, String toStoreName, String operatorName);
    
    /**
     * 发送退货申请通知
     * @param returnNo 退货单号
     * @param orderSn 订单编号
     * @param memberName 会员名称
     * @param reason 退货原因
     */
    void notifyReturnRequest(String returnNo, String orderSn, String memberName, String reason);
    
    /**
     * 发送自定义通知给指定管理员
     * @param adminId 管理员ID
     * @param content 消息内容
     */
    void sendNotificationToAdmin(Long adminId, String content);
    
    /**
     * 发送自定义通知给所有绑定微信的管理员
     * @param content 消息内容
     */
    void sendNotificationToAllBoundAdmins(String content);
    
    // ==================== 新增模板消息通知方法 ====================
    
    /**
     * 新订单通知
     * 通知所有管理员账号和订单对应的发货门店店长账号
     * @param orderSn 订单号
     * @param orderType 订单类型（如：普通订单、DIY订单等）
     * @param orderAmount 订单金额
     * @param orderTime 下单时间
     * @param productName 商品名称
     * @param storeId 发货门店ID
     */
    void notifyNewOrder(String orderSn, String orderType, BigDecimal orderAmount, 
                        Date orderTime, String productName, Long storeId);
    
    /**
     * 销售单审核结果通知
     * 当管理员通过或驳回销售单时，通知申请账号
     * @param saleNo 审核单号
     * @param auditResult 审核结果（通过/驳回）
     * @param auditTime 审核时间
     * @param applicantAdminId 申请人管理员ID
     */
    void notifySaleApprovalResult(String saleNo, String auditResult, Date auditTime, Long applicantAdminId);
    
    /**
     * 销售单新申请通知
     * 当有新的非系统销售申请时，通知管理员
     * @param saleNo 审核单号
     * @param auditTime 申请时间
     */
    void notifySaleNewApplication(String saleNo, Date auditTime);
    
    /**
     * 出库单审核结果通知 - 新调货申请
     * 当有新调货申请时，通知管理员和被申请调货的门店账号
     * @param transferNo 出库单号
     * @param auditTime 申请时间
     * @param customerName 客户名称（申请门店）
     * @param targetStoreId 被申请调货的门店ID
     */
    void notifyStockOutNewApplication(String transferNo, Date auditTime, String customerName, Long targetStoreId);
    
    /**
     * 出库单审核结果通知 - 确认发货
     * 当确认发货后，通知申请的门店账号
     * @param transferNo 出库单号
     * @param auditTime 发货时间
     * @param customerName 客户名称（供货门店）
     * @param applicantStoreId 申请门店ID
     */
    void notifyStockOutShipped(String transferNo, Date auditTime, String customerName, Long applicantStoreId);
    
    /**
     * 出库单审核结果通知 - 确认收货
     * 当确认收货后，通知被申请调货的门店账号
     * @param transferNo 出库单号
     * @param auditTime 收货时间
     * @param customerName 客户名称（申请门店）
     * @param targetStoreId 被申请调货的门店ID
     */
    void notifyStockOutReceived(String transferNo, Date auditTime, String customerName, Long targetStoreId);
    
    /**
     * 退款申请通知
     * 当有新的退款申请时，通知管理员账号
     * @param orderSn 订单号
     * @param productName 商品名称
     * @param refundAmount 退款金额
     * @param applyTime 申请时间
     * @param phoneNumber 联系电话
     */
    void notifyRefundApplication(String orderSn, String productName, BigDecimal refundAmount, 
                                  Date applyTime, String phoneNumber);
}
