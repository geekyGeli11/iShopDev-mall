package com.macro.mall.service.impl;

import cn.hutool.core.util.StrUtil;
import com.macro.mall.mapper.UmsAdminMapper;
import com.macro.mall.model.UmsAdmin;
import com.macro.mall.model.UmsAdminExample;
import com.macro.mall.service.AdminNotificationService;
import com.macro.mall.service.WechatServiceAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 管理员通知服务实现
 * 支持模板消息和客服消息两种方式：
 * 1. 模板消息：无时效限制，但需要审核
 * 2. 客服消息：48小时内互动限制，但无需审核
 * 
 * 策略：优先使用模板消息，失败时降级到客服消息
 */
@Service
public class AdminNotificationServiceImpl implements AdminNotificationService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminNotificationServiceImpl.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    @Autowired
    private UmsAdminMapper adminMapper;
    
    @Autowired
    private WechatServiceAccountService wechatServiceAccountService;
    
    // 销售单审批通知模板ID（旧的，保持兼容）
    @Value("${wechat.service-account.template.sale-approval:}")
    private String saleApprovalTemplateId;
    
    // 调货申请通知模板ID（旧的，保持兼容）
    @Value("${wechat.service-account.template.transfer-request:}")
    private String transferRequestTemplateId;
    
    // 退货申请通知模板ID（旧的，保持兼容）
    @Value("${wechat.service-account.template.return-request:}")
    private String returnRequestTemplateId;
    
    // ==================== 新模板ID配置 ====================
    
    // 新订单通知模板ID
    @Value("${wechat.service-account.template.new-order:}")
    private String newOrderTemplateId;
    
    // 销售单审核结果通知模板ID
    @Value("${wechat.service-account.template.sale-approval:}")
    private String saleApprovalResultTemplateId;
    
    // 出库单审核结果通知模板ID
    @Value("${wechat.service-account.template.stock-out:}")
    private String stockOutTemplateId;
    
    // 退款申请通知模板ID
    @Value("${wechat.service-account.template.sale-recharge:}")
    private String refundApplicationTemplateId;
    
    // 后台管理URL（用于模板消息跳转）
    @Value("${admin.web.url:}")
    private String adminWebUrl;
    
    // ==================== 原有方法（保持兼容） ====================
    
    @Override
    @Async
    public void notifySaleSubmitted(String saleNo, String storeName, String operatorName, String totalAmount) {
        String textContent = String.format(
            "📋 新销售单待审批\n\n" +
            "单号：%s\n" +
            "门店：%s\n" +
            "提交人：%s\n" +
            "金额：¥%s\n\n" +
            "请登录后台进行审批",
            saleNo, storeName, operatorName, totalAmount
        );
        
        Map<String, String> templateData = null;
        if (StrUtil.isNotEmpty(saleApprovalTemplateId)) {
            templateData = new HashMap<>();
            templateData.put("first", "您有新的销售单待审批");
            templateData.put("keyword1", saleNo);
            templateData.put("keyword2", storeName);
            templateData.put("keyword3", operatorName);
            templateData.put("keyword4", "¥" + totalAmount);
            templateData.put("remark", "请及时登录后台处理");
        }
        
        String jumpUrl = StrUtil.isNotEmpty(adminWebUrl) ? adminWebUrl + "/#/pms/nonSystemSale" : null;
        sendNotificationToAllBoundAdmins(textContent, saleApprovalTemplateId, templateData, jumpUrl);
    }
    
    @Override
    @Async
    public void notifyTransferRequest(String transferNo, String fromStoreName, String toStoreName, String operatorName) {
        String textContent = String.format(
            "🔄 新调货申请\n\n" +
            "单号：%s\n" +
            "调出：%s\n" +
            "调入：%s\n" +
            "申请人：%s\n\n" +
            "请登录后台进行处理",
            transferNo, fromStoreName, toStoreName, operatorName
        );
        
        Map<String, String> templateData = null;
        if (StrUtil.isNotEmpty(transferRequestTemplateId)) {
            templateData = new HashMap<>();
            templateData.put("first", "您有新的调货申请待处理");
            templateData.put("keyword1", transferNo);
            templateData.put("keyword2", fromStoreName + " → " + toStoreName);
            templateData.put("keyword3", operatorName);
            templateData.put("remark", "请及时登录后台处理");
        }
        
        sendNotificationToAllBoundAdmins(textContent, transferRequestTemplateId, templateData, null);
    }
    
    @Override
    @Async
    public void notifyReturnRequest(String returnNo, String orderSn, String memberName, String reason) {
        String textContent = String.format(
            "↩️ 新退货申请\n\n" +
            "退货单号：%s\n" +
            "订单编号：%s\n" +
            "申请人：%s\n" +
            "原因：%s\n\n" +
            "请登录后台进行处理",
            returnNo, orderSn, memberName, reason != null ? reason : "未填写"
        );
        
        Map<String, String> templateData = null;
        if (StrUtil.isNotEmpty(returnRequestTemplateId)) {
            templateData = new HashMap<>();
            templateData.put("first", "您有新的退货申请待处理");
            templateData.put("keyword1", returnNo);
            templateData.put("keyword2", orderSn);
            templateData.put("keyword3", memberName);
            templateData.put("keyword4", reason != null ? reason : "未填写");
            templateData.put("remark", "请及时登录后台处理");
        }
        
        sendNotificationToAllBoundAdmins(textContent, returnRequestTemplateId, templateData, null);
    }
    
    @Override
    @Async
    public void sendNotificationToAdmin(Long adminId, String content) {
        if (adminId == null || StrUtil.isEmpty(content)) {
            return;
        }
        
        UmsAdmin admin = adminMapper.selectByPrimaryKey(adminId);
        if (admin == null || StrUtil.isEmpty(admin.getWxServiceOpenid())) {
            LOGGER.debug("管理员未绑定微信，跳过通知：adminId={}", adminId);
            return;
        }
        
        sendMessageToAdmin(admin, content, null, null, null);
    }
    
    @Override
    @Async
    public void sendNotificationToAllBoundAdmins(String content) {
        sendNotificationToAllBoundAdmins(content, null, null, null);
    }

    
    // ==================== 新增模板消息通知方法 ====================
    
    @Override
    @Async
    public void notifyNewOrder(String orderSn, String orderType, BigDecimal orderAmount, 
                               Date orderTime, String productName, Long storeId) {
        LOGGER.info("发送新订单通知：orderSn={}, storeId={}", orderSn, storeId);
        
        String timeStr = orderTime != null ? DATE_FORMAT.format(orderTime) : DATE_FORMAT.format(new Date());
        String amountStr = orderAmount != null ? "¥" + orderAmount.setScale(2, BigDecimal.ROUND_HALF_UP) : "¥0.00";
        
        // 构建客服消息内容（降级方案）
        String textContent = String.format(
            "🛒 新订单通知\n\n" +
            "订单号：%s\n" +
            "订单类型：%s\n" +
            "订单金额：%s\n" +
            "下单时间：%s\n" +
            "商品名称：%s\n\n" +
            "请及时处理",
            orderSn, orderType, amountStr, timeStr, truncateString(productName, 20)
        );
        
        // 构建模板消息数据
        // 模板字段：character_string2(订单号), thing3(订单类型), amount5(订单金额), time6(下单时间), thing4(商品名称)
        Map<String, String> templateData = null;
        if (StrUtil.isNotEmpty(newOrderTemplateId)) {
            templateData = new HashMap<>();
            templateData.put("character_string2", orderSn);
            templateData.put("thing3", truncateString(orderType, 20));
            templateData.put("amount5", amountStr);
            templateData.put("time6", timeStr);
            templateData.put("thing4", truncateString(productName, 20));
        }
        
        String jumpUrl = StrUtil.isNotEmpty(adminWebUrl) ? adminWebUrl + "/#/oms/order" : null;
        
        // 获取需要通知的管理员列表
        List<UmsAdmin> adminsToNotify = getAdminsForNewOrderNotification(storeId);
        
        if (adminsToNotify.isEmpty()) {
            LOGGER.warn("没有需要通知的管理员，orderSn={}", orderSn);
            return;
        }
        
        sendNotificationToAdminList(adminsToNotify, textContent, newOrderTemplateId, templateData, jumpUrl);
    }
    
    @Override
    @Async
    public void notifySaleApprovalResult(String saleNo, String auditResult, Date auditTime, Long applicantAdminId) {
        LOGGER.info("发送销售单审核结果通知：saleNo={}, result={}, applicantId={}", saleNo, auditResult, applicantAdminId);
        
        String timeStr = auditTime != null ? DATE_FORMAT.format(auditTime) : DATE_FORMAT.format(new Date());
        
        String textContent = String.format(
            "📋 销售单审核结果\n\n" +
            "审核单号：%s\n" +
            "审核结果：%s\n" +
            "审核时间：%s\n\n" +
            "点击查看详情",
            saleNo, auditResult, timeStr
        );
        
        // 模板字段：time1(审核时间), character_string3(审核单号), const2(审核结果)
        Map<String, String> templateData = null;
        if (StrUtil.isNotEmpty(saleApprovalResultTemplateId)) {
            templateData = new HashMap<>();
            templateData.put("time1", timeStr);
            templateData.put("character_string3", saleNo);
            templateData.put("const2", auditResult);
        }
        
        String jumpUrl = StrUtil.isNotEmpty(adminWebUrl) ? adminWebUrl + "/#/pms/nonSystemSale" : null;
        
        // 通知申请人
        if (applicantAdminId != null) {
            UmsAdmin applicant = adminMapper.selectByPrimaryKey(applicantAdminId);
            if (applicant != null && StrUtil.isNotEmpty(applicant.getWxServiceOpenid())) {
                sendMessageToAdmin(applicant, textContent, saleApprovalResultTemplateId, templateData, jumpUrl);
            } else {
                LOGGER.debug("申请人未绑定微信，跳过通知：adminId={}", applicantAdminId);
            }
        }
    }
    
    @Override
    @Async
    public void notifySaleNewApplication(String saleNo, Date auditTime) {
        LOGGER.info("发送销售单新申请通知：saleNo={}", saleNo);
        
        String timeStr = auditTime != null ? DATE_FORMAT.format(auditTime) : DATE_FORMAT.format(new Date());
        
        String textContent = String.format(
            "📋 新销售单申请\n\n" +
            "审核单号：%s\n" +
            "申请时间：%s\n" +
            "审核结果：待审核\n\n" +
            "请及时处理",
            saleNo, timeStr
        );
        
        // 模板字段：time1(审核时间), character_string3(审核单号), const2(审核结果)
        Map<String, String> templateData = null;
        if (StrUtil.isNotEmpty(saleApprovalResultTemplateId)) {
            templateData = new HashMap<>();
            templateData.put("time1", timeStr);
            templateData.put("character_string3", saleNo);
            templateData.put("const2", "待审核");
        }
        
        String jumpUrl = StrUtil.isNotEmpty(adminWebUrl) ? adminWebUrl + "/#/pms/nonSystemSale" : null;
        
        // 通知所有管理员账号
        List<UmsAdmin> admins = getAdminAccounts();
        sendNotificationToAdminList(admins, textContent, saleApprovalResultTemplateId, templateData, jumpUrl);
    }
    
    @Override
    @Async
    public void notifyStockOutNewApplication(String transferNo, Date auditTime, String customerName, Long targetStoreId) {
        LOGGER.info("发送出库单新申请通知：transferNo={}, targetStoreId={}", transferNo, targetStoreId);
        
        String timeStr = auditTime != null ? DATE_FORMAT.format(auditTime) : DATE_FORMAT.format(new Date());
        
        String textContent = String.format(
            "📦 新调货申请\n\n" +
            "出库单号：%s\n" +
            "申请时间：%s\n" +
            "申请门店：%s\n\n" +
            "请及时处理",
            transferNo, timeStr, customerName
        );
        
        // 模板字段：character_string1(出库单号), time5(审核时间), thing2(客户名称)
        Map<String, String> templateData = null;
        if (StrUtil.isNotEmpty(stockOutTemplateId)) {
            templateData = new HashMap<>();
            templateData.put("character_string1", transferNo);
            templateData.put("time5", timeStr);
            templateData.put("thing2", truncateString(customerName, 20));
        }
        
        String jumpUrl = StrUtil.isNotEmpty(adminWebUrl) ? adminWebUrl + "/#/pms/stockTransfer" : null;
        
        // 通知管理员和被申请调货的门店账号
        List<UmsAdmin> adminsToNotify = new ArrayList<>();
        adminsToNotify.addAll(getAdminAccounts());
        if (targetStoreId != null) {
            adminsToNotify.addAll(getStoreAdmins(targetStoreId));
        }
        
        // 去重
        adminsToNotify = deduplicateAdmins(adminsToNotify);
        sendNotificationToAdminList(adminsToNotify, textContent, stockOutTemplateId, templateData, jumpUrl);
    }
    
    @Override
    @Async
    public void notifyStockOutShipped(String transferNo, Date auditTime, String customerName, Long applicantStoreId) {
        LOGGER.info("发送出库单发货通知：transferNo={}, applicantStoreId={}", transferNo, applicantStoreId);
        
        String timeStr = auditTime != null ? DATE_FORMAT.format(auditTime) : DATE_FORMAT.format(new Date());
        
        String textContent = String.format(
            "📦 调货已发货\n\n" +
            "出库单号：%s\n" +
            "发货时间：%s\n" +
            "供货门店：%s\n\n" +
            "请及时确认收货",
            transferNo, timeStr, customerName
        );
        
        Map<String, String> templateData = null;
        if (StrUtil.isNotEmpty(stockOutTemplateId)) {
            templateData = new HashMap<>();
            templateData.put("character_string1", transferNo);
            templateData.put("time5", timeStr);
            templateData.put("thing2", truncateString(customerName, 20));
        }
        
        String jumpUrl = StrUtil.isNotEmpty(adminWebUrl) ? adminWebUrl + "/#/pms/stockTransfer" : null;
        
        // 通知申请门店账号
        if (applicantStoreId != null) {
            List<UmsAdmin> storeAdmins = getStoreAdmins(applicantStoreId);
            sendNotificationToAdminList(storeAdmins, textContent, stockOutTemplateId, templateData, jumpUrl);
        }
    }
    
    @Override
    @Async
    public void notifyStockOutReceived(String transferNo, Date auditTime, String customerName, Long targetStoreId) {
        LOGGER.info("发送出库单收货通知：transferNo={}, targetStoreId={}", transferNo, targetStoreId);
        
        String timeStr = auditTime != null ? DATE_FORMAT.format(auditTime) : DATE_FORMAT.format(new Date());
        
        String textContent = String.format(
            "📦 调货已收货\n\n" +
            "出库单号：%s\n" +
            "收货时间：%s\n" +
            "收货门店：%s\n\n" +
            "调货流程已完成",
            transferNo, timeStr, customerName
        );
        
        Map<String, String> templateData = null;
        if (StrUtil.isNotEmpty(stockOutTemplateId)) {
            templateData = new HashMap<>();
            templateData.put("character_string1", transferNo);
            templateData.put("time5", timeStr);
            templateData.put("thing2", truncateString(customerName, 20));
        }
        
        String jumpUrl = StrUtil.isNotEmpty(adminWebUrl) ? adminWebUrl + "/#/pms/stockTransfer" : null;
        
        // 通知被申请调货的门店账号
        if (targetStoreId != null) {
            List<UmsAdmin> storeAdmins = getStoreAdmins(targetStoreId);
            sendNotificationToAdminList(storeAdmins, textContent, stockOutTemplateId, templateData, jumpUrl);
        }
    }
    
    @Override
    @Async
    public void notifyRefundApplication(String orderSn, String productName, BigDecimal refundAmount, 
                                         Date applyTime, String phoneNumber) {
        LOGGER.info("发送退款申请通知：orderSn={}", orderSn);
        
        String timeStr = applyTime != null ? DATE_FORMAT.format(applyTime) : DATE_FORMAT.format(new Date());
        String amountStr = refundAmount != null ? "¥" + refundAmount.setScale(2, BigDecimal.ROUND_HALF_UP) : "¥0.00";
        
        String textContent = String.format(
            "💰 新退款申请\n\n" +
            "订单号：%s\n" +
            "商品名称：%s\n" +
            "退款金额：%s\n" +
            "申请时间：%s\n" +
            "联系电话：%s\n\n" +
            "请及时处理",
            orderSn, truncateString(productName, 20), amountStr, timeStr, phoneNumber != null ? phoneNumber : "未提供"
        );
        
        // 模板字段：character_string2(订单号), thing3(商品名称), amount4(退款金额), time5(申请时间), phone_number6(联系电话)
        Map<String, String> templateData = null;
        if (StrUtil.isNotEmpty(refundApplicationTemplateId)) {
            templateData = new HashMap<>();
            templateData.put("character_string2", orderSn);
            templateData.put("thing3", truncateString(productName, 20));
            templateData.put("amount4", amountStr);
            templateData.put("time5", timeStr);
            templateData.put("phone_number6", phoneNumber != null ? phoneNumber : "未提供");
        }
        
        String jumpUrl = StrUtil.isNotEmpty(adminWebUrl) ? adminWebUrl + "/#/oms/returnApply" : null;
        
        // 通知所有管理员账号
        List<UmsAdmin> admins = getAdminAccounts();
        sendNotificationToAdminList(admins, textContent, refundApplicationTemplateId, templateData, jumpUrl);
    }

    
    // ==================== 私有辅助方法 ====================
    
    /**
     * 获取新订单需要通知的管理员列表
     * 包括：所有管理员账号 + 订单对应门店的店长账号
     */
    private List<UmsAdmin> getAdminsForNewOrderNotification(Long storeId) {
        List<UmsAdmin> result = new ArrayList<>();
        
        // 1. 获取所有管理员账号（adminType=0）
        result.addAll(getAdminAccounts());
        
        // 2. 获取订单对应门店的店长账号（adminType=1 且 storeId匹配）
        if (storeId != null) {
            result.addAll(getStoreAdmins(storeId));
        }
        
        // 去重
        return deduplicateAdmins(result);
    }
    
    /**
     * 获取所有管理员账号（adminType=0）
     */
    private List<UmsAdmin> getAdminAccounts() {
        UmsAdminExample example = new UmsAdminExample();
        example.createCriteria()
            .andStatusEqualTo(1)
            .andAdminTypeEqualTo(false)  // 管理账号
            .andWxServiceOpenidIsNotNull();
        
        List<UmsAdmin> admins = adminMapper.selectByExample(example);
        // 过滤掉openid为空字符串的
        admins.removeIf(admin -> StrUtil.isEmpty(admin.getWxServiceOpenid()));
        return admins;
    }
    
    /**
     * 获取指定门店的店长账号（adminType=1 且 storeId匹配）
     */
    private List<UmsAdmin> getStoreAdmins(Long storeId) {
        if (storeId == null) {
            return new ArrayList<>();
        }
        
        UmsAdminExample example = new UmsAdminExample();
        example.createCriteria()
            .andStatusEqualTo(1)
            .andAdminTypeEqualTo(true)  // 门店账号
            .andStoreIdEqualTo(storeId)
            .andWxServiceOpenidIsNotNull();
        
        List<UmsAdmin> admins = adminMapper.selectByExample(example);
        // 过滤掉openid为空字符串的
        admins.removeIf(admin -> StrUtil.isEmpty(admin.getWxServiceOpenid()));
        return admins;
    }
    
    /**
     * 管理员列表去重（按ID）
     */
    private List<UmsAdmin> deduplicateAdmins(List<UmsAdmin> admins) {
        Map<Long, UmsAdmin> adminMap = new LinkedHashMap<>();
        for (UmsAdmin admin : admins) {
            if (admin.getId() != null && !adminMap.containsKey(admin.getId())) {
                adminMap.put(admin.getId(), admin);
            }
        }
        return new ArrayList<>(adminMap.values());
    }
    
    /**
     * 发送通知给管理员列表
     */
    private void sendNotificationToAdminList(List<UmsAdmin> admins, String textContent, 
                                              String templateId, Map<String, String> templateData, String jumpUrl) {
        if (admins == null || admins.isEmpty()) {
            LOGGER.warn("管理员列表为空，无法发送通知");
            return;
        }
        
        LOGGER.info("开始发送通知给 {} 位管理员", admins.size());
        
        int successCount = 0;
        int failCount = 0;
        
        for (UmsAdmin admin : admins) {
            if (StrUtil.isEmpty(admin.getWxServiceOpenid())) {
                continue;
            }
            
            boolean success = sendMessageToAdmin(admin, textContent, templateId, templateData, jumpUrl);
            if (success) {
                successCount++;
            } else {
                failCount++;
            }
        }
        
        LOGGER.info("通知发送完成：成功={}, 失败={}", successCount, failCount);
    }
    
    /**
     * 发送通知给所有绑定微信的管理员
     */
    private void sendNotificationToAllBoundAdmins(String textContent, String templateId, 
                                                   Map<String, String> templateData, String jumpUrl) {
        UmsAdminExample example = new UmsAdminExample();
        example.createCriteria()
            .andStatusEqualTo(1)
            .andWxServiceOpenidIsNotNull();
        
        List<UmsAdmin> boundAdmins = adminMapper.selectByExample(example);
        boundAdmins.removeIf(admin -> StrUtil.isEmpty(admin.getWxServiceOpenid()));
        
        if (boundAdmins.isEmpty()) {
            LOGGER.warn("没有绑定微信的管理员，无法发送通知");
            return;
        }
        
        sendNotificationToAdminList(boundAdmins, textContent, templateId, templateData, jumpUrl);
    }
    
    /**
     * 发送消息给单个管理员
     * 策略：优先模板消息，失败则降级到客服消息
     */
    private boolean sendMessageToAdmin(UmsAdmin admin, String textContent, String templateId,
                                       Map<String, String> templateData, String jumpUrl) {
        String openId = admin.getWxServiceOpenid();
        
        // 1. 尝试发送模板消息
        if (StrUtil.isNotEmpty(templateId) && templateData != null) {
            try {
                Long msgId = wechatServiceAccountService.sendTemplateMessage(openId, templateId, templateData, jumpUrl);
                if (msgId != null) {
                    LOGGER.debug("模板消息发送成功：adminId={}, msgId={}", admin.getId(), msgId);
                    return true;
                }
            } catch (Exception e) {
                LOGGER.warn("模板消息发送失败，尝试客服消息：adminId={}, error={}", admin.getId(), e.getMessage());
            }
        }
        
        // 2. 降级到客服消息
        try {
            wechatServiceAccountService.sendTextMessage(openId, textContent);
            LOGGER.debug("客服消息发送成功：adminId={}", admin.getId());
            return true;
        } catch (Exception e) {
            LOGGER.warn("客服消息发送失败（可能超过48小时未互动）：adminId={}, nickname={}, error={}", 
                admin.getId(), admin.getNickName(), e.getMessage());
            return false;
        }
    }
    
    /**
     * 截断字符串（微信模板消息字段有长度限制）
     */
    private String truncateString(String str, int maxLength) {
        if (str == null) {
            return "";
        }
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }
}
