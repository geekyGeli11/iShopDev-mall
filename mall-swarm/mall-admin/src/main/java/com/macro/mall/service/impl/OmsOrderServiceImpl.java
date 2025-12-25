package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.dao.OmsOrderDao;
import com.macro.mall.dao.OmsOrderOperateHistoryDao;
import com.macro.mall.dto.*;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.mapper.OmsOrderOperateHistoryMapper;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.model.*;
import com.macro.mall.service.OmsOrderService;
import com.macro.mall.service.QRCodeService;
import com.macro.mall.utils.PickupCodeUtil;
import com.macro.mall.common.util.WxMessageUtil;
import com.macro.mall.utils.WxMiniProgramShippingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单管理Service实现类
 * Created by macro on 2018/10/11.
 */
@Service
public class OmsOrderServiceImpl implements OmsOrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OmsOrderServiceImpl.class);
    
    @Autowired
    private OmsOrderMapper orderMapper;
    @Autowired
    private OmsOrderDao orderDao;
    @Autowired
    private UmsMemberMapper umsMemberMapper;
    @Autowired
    private OmsOrderOperateHistoryDao orderOperateHistoryDao;
    @Autowired
    private OmsOrderOperateHistoryMapper orderOperateHistoryMapper;
    @Autowired(required = false)
    private WxMessageUtil wxMessageUtil;
    @Autowired
    private WxMiniProgramShippingUtil wxMiniProgramShippingUtil;
    @Autowired
    private PickupCodeUtil pickupCodeUtil;
    @Autowired
    private QRCodeService qrCodeService;
    @Autowired
    private com.macro.mall.mapper.PmsStoreSkuStockMapper storeSkuStockMapper;
    @Autowired
    private com.macro.mall.mapper.OmsStoreAddressMapper storeAddressMapper;
    @Autowired
    private com.macro.mall.mapper.PmsStockOperationLogMapper stockOperationLogMapper;
    @Autowired
    private com.macro.mall.mapper.PmsSkuStockMapper skuStockMapper;
    @Autowired
    private com.macro.mall.mapper.PmsProductMapper productMapper;

    // 订单发货通知模板ID
    @Value("${template.order-delivery}")
    private String orderDeliveryTemplateId;

    @Override
    public List<OmsOrder> list(OmsOrderQueryParam queryParam, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        return orderDao.getList(queryParam);
    }

    @Override
    public List<OmsOrderWithItems> listWithItems(OmsOrderQueryParam queryParam, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        return orderDao.getListWithItems(queryParam);
    }

    @Override
    public int delivery(List<OmsOrderDeliveryParam> deliveryParamList) {
        //批量发货
        int count = orderDao.delivery(deliveryParamList);
        //添加操作记录
        List<OmsOrderOperateHistory> operateHistoryList = deliveryParamList.stream()
                .map(omsOrderDeliveryParam -> {
                    OmsOrderOperateHistory history = new OmsOrderOperateHistory();
                    history.setOrderId(omsOrderDeliveryParam.getOrderId());
                    history.setCreateTime(new Date());
                    history.setOperateMan("后台管理员");
                    // 根据发货方式设置不同的订单状态和备注
                    Integer orderStatus = 2; // 默认已发货
                    String note = "完成发货";
                    if (omsOrderDeliveryParam.getDeliveryMethod() != null) {
                        switch (omsOrderDeliveryParam.getDeliveryMethod()) {
                            case 1:
                                orderStatus = 2;
                                note = "完成发货 - 快递配送";
                                break;
                            case 2:
                                orderStatus = 3; // 门店自提直接完成
                                note = "完成发货 - 门店自提，订单已完成";
                                break;
                            case 3:
                                orderStatus = 2;
                                note = "完成发货 - 虚拟发货";
                                break;
                            default:
                                orderStatus = 2;
                                note = "完成发货";
                                break;
                        }
                    }
                    history.setOrderStatus(orderStatus);
                    history.setDeliveryMethod(omsOrderDeliveryParam.getDeliveryMethod());
                    history.setNote(note);
                    return history;
                }).collect(Collectors.toList());
        orderOperateHistoryDao.insertList(operateHistoryList);

        // 发送微信订阅消息通知和调用微信小程序发货接口
        deliveryParamList.forEach(deliveryParam -> {
            try {
                // 获取订单详情
                OmsOrder order = orderMapper.selectByPrimaryKey(deliveryParam.getOrderId());
                UmsMember umsMember = umsMemberMapper.selectByPrimaryKey(order.getMemberId());
                
                if (umsMember != null && umsMember.getOpenid() != null) {
                    // 获取订单商品信息用于发货接口
                    String itemDesc = getOrderProductInfo(order);
                    
                    // 1. 调用微信小程序发货信息录入接口
                    boolean shippingResult = wxMiniProgramShippingUtil.uploadShippingInfo(
                            order, umsMember, deliveryParam, itemDesc);
                    
                    if (shippingResult) {
                        LOGGER.info("微信小程序发货信息录入成功，订单号：{}", order.getOrderSn());
                    } else {
                        LOGGER.error("微信小程序发货信息录入失败，订单号：{}", order.getOrderSn());
                    }
                    
                    // 2. 发送微信订阅消息通知（无论发货接口是否成功都发送通知）
                    Map<String, String> templateData = new HashMap<>();
                    templateData.put("time1", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date())); // 发货时间
                    templateData.put("character_string7", order.getOrderSn()); // 订单号码

                    // 根据发货方式设置不同的发货单号信息
                    String deliveryInfo = "";
                    if (deliveryParam.getDeliveryMethod() != null) {
                        switch (deliveryParam.getDeliveryMethod()) {
                            case 1: // 快递配送
                                deliveryInfo = deliveryParam.getDeliverySn() != null ? deliveryParam.getDeliverySn() : "快递配送";
                                break;
                            case 2: // 门店自提
                                deliveryInfo = deliveryParam.getStoreName() != null ? "门店:" + deliveryParam.getStoreName() : "门店自提";
                                break;
                            case 3: // 虚拟发货
                                deliveryInfo = "虚拟发货";
                                break;
                            default:
                                deliveryInfo = deliveryParam.getDeliverySn() != null ? deliveryParam.getDeliverySn() : "已发货";
                                break;
                        }
                    } else {
                        deliveryInfo = deliveryParam.getDeliverySn() != null ? deliveryParam.getDeliverySn() : "已发货";
                    }
                    templateData.put("character_string2", deliveryInfo); // 发货信息

                    // 构建小程序跳转路径
                    String miniProgramPath = "/pages/order/orderDetail?id=" + order.getId();
                    
                    // 发送订阅消息（如果微信服务可用）
                    if (wxMessageUtil != null) {
                        wxMessageUtil.sendSubscribeMessage(
                                umsMember.getOpenid(),
                                orderDeliveryTemplateId,
                                templateData,
                                miniProgramPath
                        );
                    } else {
                        LOGGER.info("微信服务不可用，跳过发送订单发货通知订阅消息，订单ID：{}", order.getId());
                    }
                }
            } catch (Exception e) {
                LOGGER.error("发送订单发货通知或调用发货接口失败，订单ID：{}", deliveryParam.getOrderId(), e);
            }
        });

        return count;
    }

    /**
     * 获取订单商品信息摘要
     */
    private String getOrderProductInfo(OmsOrder order) {
        // 获取订单商品信息
        List<OmsOrderItem> orderItems = orderDao.getDetail(order.getId()).getOrderItemList();
        if (orderItems != null && !orderItems.isEmpty()) {
            OmsOrderItem firstItem = orderItems.get(0);
            if (orderItems.size() > 1) {
                return firstItem.getProductName() + "等" + orderItems.size() + "件商品";
            }
            return firstItem.getProductName();
        }
        return "暂无商品信息";
    }

    @Override
    public int close(List<Long> ids, String note) {
        OmsOrder record = new OmsOrder();
        record.setStatus(4);
        OmsOrderExample example = new OmsOrderExample();
        example.createCriteria().andDeleteStatusEqualTo(0).andIdIn(ids);
        int count = orderMapper.updateByExampleSelective(record, example);
        List<OmsOrderOperateHistory> historyList = ids.stream().map(orderId -> {
            OmsOrderOperateHistory history = new OmsOrderOperateHistory();
            history.setOrderId(orderId);
            history.setCreateTime(new Date());
            history.setOperateMan("后台管理员");
            history.setOrderStatus(4);
            history.setNote("订单关闭:"+note);
            return history;
        }).collect(Collectors.toList());
        orderOperateHistoryDao.insertList(historyList);
        return count;
    }

    @Override
    public int delete(List<Long> ids) {
        OmsOrder record = new OmsOrder();
        record.setDeleteStatus(1);
        OmsOrderExample example = new OmsOrderExample();
        example.createCriteria().andDeleteStatusEqualTo(0).andIdIn(ids);
        return orderMapper.updateByExampleSelective(record, example);
    }

    @Override
    public OmsOrderDetail detail(Long id) {
        OmsOrderDetail orderDetail = orderDao.getDetail(id);
        if (orderDetail != null && orderDetail.getOrderSn() != null) {
            // 获取库存扣除详情
            orderDetail.setStockDeductionList(orderDao.getStockDeductionByOrderSn(orderDetail.getOrderSn()));
        }
        return orderDetail;
    }

    @Override
    public int updateReceiverInfo(OmsReceiverInfoParam receiverInfoParam) {
        OmsOrder order = new OmsOrder();
        order.setId(receiverInfoParam.getOrderId());
        order.setReceiverName(receiverInfoParam.getReceiverName());
        order.setReceiverPhone(receiverInfoParam.getReceiverPhone());
        order.setReceiverPostCode(receiverInfoParam.getReceiverPostCode());
        order.setReceiverDetailAddress(receiverInfoParam.getReceiverDetailAddress());
        order.setReceiverProvince(receiverInfoParam.getReceiverProvince());
        order.setReceiverCity(receiverInfoParam.getReceiverCity());
        order.setReceiverRegion(receiverInfoParam.getReceiverRegion());
        order.setModifyTime(new Date());
        int count = orderMapper.updateByPrimaryKeySelective(order);
        //插入操作记录
        OmsOrderOperateHistory history = new OmsOrderOperateHistory();
        history.setOrderId(receiverInfoParam.getOrderId());
        history.setCreateTime(new Date());
        history.setOperateMan("后台管理员");
        history.setOrderStatus(receiverInfoParam.getStatus());
        history.setNote("修改收货人信息");
        orderOperateHistoryMapper.insert(history);
        return count;
    }

    @Override
    public int updateMoneyInfo(OmsMoneyInfoParam moneyInfoParam) {
        OmsOrder order = new OmsOrder();
        order.setId(moneyInfoParam.getOrderId());
        order.setFreightAmount(moneyInfoParam.getFreightAmount());
        order.setDiscountAmount(moneyInfoParam.getDiscountAmount());
        order.setModifyTime(new Date());
        int count = orderMapper.updateByPrimaryKeySelective(order);
        //插入操作记录
        OmsOrderOperateHistory history = new OmsOrderOperateHistory();
        history.setOrderId(moneyInfoParam.getOrderId());
        history.setCreateTime(new Date());
        history.setOperateMan("后台管理员");
        history.setOrderStatus(moneyInfoParam.getStatus());
        history.setNote("修改费用信息");
        orderOperateHistoryMapper.insert(history);
        return count;
    }

    @Override
    public int updateNote(Long id, String note, Integer status) {
        // 先获取原有订单信息
        OmsOrder existingOrder = orderMapper.selectByPrimaryKey(id);
        String originalNote = existingOrder.getNote();

        // 构建新的备注：保留原有用户备注，追加管理员备注
        String newNote;
        if (originalNote != null && !originalNote.trim().isEmpty()) {
            newNote = originalNote + "【管理员备注：" + note + "】";
        } else {
            newNote = "【管理员备注：" + note + "】";
        }

        OmsOrder order = new OmsOrder();
        order.setId(id);
        order.setNote(newNote);
        order.setModifyTime(new Date());
        int count = orderMapper.updateByPrimaryKeySelective(order);
        OmsOrderOperateHistory history = new OmsOrderOperateHistory();
        history.setOrderId(id);
        history.setCreateTime(new Date());
        history.setOperateMan("后台管理员");
        history.setOrderStatus(status);
        history.setNote("修改备注信息："+note);
        orderOperateHistoryMapper.insert(history);
        return count;
    }

    @Override
    public Map<String, Object> pickupOrder(String pickupCode, String operator) {
        // 验证核销码格式
        if (!pickupCodeUtil.isValidFormat(pickupCode)) {
            throw new RuntimeException("核销码格式不正确");
        }

        // 检查核销码是否存在
        if (!pickupCodeUtil.isValidCode(pickupCode)) {
            throw new RuntimeException("核销码不存在或已失效");
        }

        // 检查核销码是否已被使用
        if (pickupCodeUtil.isCodeUsed(pickupCode)) {
            throw new RuntimeException("核销码已被使用");
        }

        // 获取订单ID
        Long orderId = pickupCodeUtil.getOrderId(pickupCode);
        if (orderId == null) {
            throw new RuntimeException("无法获取核销码对应的订单");
        }

        // 获取订单信息
        OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 验证订单状态（必须是已支付状态）
        if (order.getStatus() < 1) {
            throw new RuntimeException("订单尚未支付，无法核销");
        }

        // 验证是否为自提订单
        if (order.getDeliveryType() == null || order.getDeliveryType() != 1) {
            throw new RuntimeException("非自提订单，无法核销");
        }

        // 检查是否已经核销
        if (order.getPickupStatus() != null && order.getPickupStatus() == 1) {
            throw new RuntimeException("订单已核销，请勿重复操作");
        }

        try {
            // 更新订单状态为已完成，并记录核销信息
            OmsOrder updateOrder = new OmsOrder();
            updateOrder.setId(orderId);
            updateOrder.setStatus(3); // 已完成
            updateOrder.setPickupStatus((byte) 1); // 已核销
            updateOrder.setPickupTime(new Date());
            updateOrder.setPickupOperator(operator);
            updateOrder.setModifyTime(new Date());

            int updateResult = orderMapper.updateByPrimaryKeySelective(updateOrder);
            if (updateResult <= 0) {
                throw new RuntimeException("更新订单状态失败");
            }

            // 标记核销码为已使用
            boolean markResult = pickupCodeUtil.markCodeAsUsed(pickupCode, operator);
            if (!markResult) {
                LOGGER.warn("标记核销码为已使用失败，但订单状态已更新，核销码:{}", pickupCode);
            }

            // 添加操作记录
            OmsOrderOperateHistory history = new OmsOrderOperateHistory();
            history.setOrderId(orderId);
            history.setCreateTime(new Date());
            history.setOperateMan(operator);
            history.setOrderStatus(3);
            history.setNote("订单核销完成，核销码:" + pickupCode);
            orderOperateHistoryMapper.insert(history);

            // 返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("orderId", orderId);
            result.put("orderSn", order.getOrderSn());
            result.put("pickupCode", pickupCode);
            result.put("pickupTime", new Date());
            result.put("operator", operator);

            LOGGER.info("订单核销成功，订单ID:{}, 核销码:{}, 操作员:{}", orderId, pickupCode, operator);
            return result;

        } catch (Exception e) {
            LOGGER.error("核销订单失败，订单ID:{}, 核销码:{}, 操作员:{}", orderId, pickupCode, operator, e);
            throw new RuntimeException("核销失败: " + e.getMessage());
        }
    }

    @Override
    public List<OmsOrder> listAll(OmsOrderQueryParam queryParam) {
        // 不使用分页插件，直接返回全部结果
        return orderDao.getList(queryParam);
    }

    @Override
    public Long count(OmsOrderQueryParam queryParam) {
        return orderDao.count(queryParam);
    }

    @Override
    public Map<String, Object> batchDeliveryFromExcel(org.springframework.web.multipart.MultipartFile file) throws Exception {
        Map<String, Object> result = new HashMap<>();
        int successCount = 0;
        int failCount = 0;
        List<String> errorMessages = new java.util.ArrayList<>();
        List<Map<String, String>> failList = new java.util.ArrayList<>();

        // 快递公司映射：中文名称 -> 英文简写
        Map<String, String> logisticsMapping = new HashMap<>();
        logisticsMapping.put("顺丰快递", "shunfeng");
        logisticsMapping.put("圆通快递", "yuantong");
        logisticsMapping.put("中通快递", "zhongtong");
        logisticsMapping.put("韵达快递", "yunda");
        logisticsMapping.put("申通快递", "shentong");
        logisticsMapping.put("极兔速递", "jtexpress");
        logisticsMapping.put("邮政快递包裹", "youzhengguonei");
        logisticsMapping.put("京东物流", "jd");
        logisticsMapping.put("EMS", "ems");
        logisticsMapping.put("菜鸟速递", "danniao");
        logisticsMapping.put("USPS", "usps");
        logisticsMapping.put("中通快运", "zhongtongkuaiyun");
        logisticsMapping.put("德邦物流", "debangwuliu");
        logisticsMapping.put("顺丰快运", "shunfengkuaiyun");
        logisticsMapping.put("跨越速运", "kuayue");
        logisticsMapping.put("京东快运", "jingdongkuaiyun");
        logisticsMapping.put("安能快运", "annengwuliu");
        logisticsMapping.put("京广速递", "jinguangsudikuaijian");
        logisticsMapping.put("百世快运", "baishiwuliu");
        logisticsMapping.put("中通国际", "zhongtongguoji");

        try {
            // 读取Excel文件
            org.apache.poi.ss.usermodel.Workbook workbook = org.apache.poi.ss.usermodel.WorkbookFactory.create(file.getInputStream());
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);

            LOGGER.info("Excel文件读取成功，总行数：{}", sheet.getLastRowNum() + 1);

            // 跳过标题行、说明行、示例行，从第四行开始读取数据
            for (int i = 3; i <= sheet.getLastRowNum(); i++) {
                org.apache.poi.ss.usermodel.Row row = sheet.getRow(i);
                if (row == null) {
                    LOGGER.info("第{}行为空，跳过", i + 1);
                    continue;
                }

                String orderSn = null; // 将变量声明移到try块外面
                try {
                    // 读取所有字段
                    orderSn = getCellValue(row.getCell(0));
                    String deliveryMethodStr = getCellValue(row.getCell(1));
                    String deliveryCompany = getCellValue(row.getCell(2));
                    String deliverySn = getCellValue(row.getCell(3));
                    String storeName = getCellValue(row.getCell(4));
                    String contactPhone = getCellValue(row.getCell(5));
                    String virtualDeliveryInfo = getCellValue(row.getCell(6));

                    LOGGER.info("第{}行数据：订单号={}, 发货方式={}, 门店名称={}", i + 1, orderSn, deliveryMethodStr, storeName);

                    if (orderSn == null || orderSn.trim().isEmpty()) {
                        LOGGER.info("第{}行订单号为空，跳过", i + 1);
                        continue; // 跳过空行
                    }

                    // 解析发货方式
                    Integer deliveryMethod = 1; // 默认快递配送
                    if (deliveryMethodStr != null && !deliveryMethodStr.trim().isEmpty()) {
                        try {
                            deliveryMethod = Integer.parseInt(deliveryMethodStr.trim());
                            if (deliveryMethod < 1 || deliveryMethod > 3) {
                                throw new NumberFormatException("发货方式必须是1、2或3");
                            }
                        } catch (NumberFormatException e) {
                            String errorMsg = "发货方式格式错误，必须是1（快递配送）、2（门店自提）或3（虚拟发货）";
                            errorMessages.add("第" + (i + 1) + "行：" + errorMsg);
                            Map<String, String> failItem = new HashMap<>();
                            failItem.put("orderSn", orderSn);
                            failItem.put("reason", errorMsg);
                            failList.add(failItem);
                            failCount++;
                            continue;
                        }
                    }

                    // 根据发货方式验证必填字段
                    String validationError = null;
                    if (deliveryMethod == 1) {
                        // 快递配送：验证物流公司和物流单号
                        if (deliveryCompany == null || deliveryCompany.trim().isEmpty()) {
                            validationError = "快递配送时物流公司不能为空";
                        } else if (deliverySn == null || deliverySn.trim().isEmpty()) {
                            validationError = "快递配送时物流单号不能为空";
                        }
                    } else if (deliveryMethod == 2) {
                        // 门店自提：验证门店名称
                        if (storeName == null || storeName.trim().isEmpty()) {
                            validationError = "门店自提时门店名称不能为空";
                        }
                    } else if (deliveryMethod == 3) {
                        // 虚拟发货：验证虚拟发货信息
                        if (virtualDeliveryInfo == null || virtualDeliveryInfo.trim().isEmpty()) {
                            validationError = "虚拟发货时发货信息不能为空";
                        }
                    }

                    if (validationError != null) {
                        errorMessages.add("第" + (i + 1) + "行：" + validationError);
                        Map<String, String> failItem = new HashMap<>();
                        failItem.put("orderSn", orderSn);
                        failItem.put("reason", validationError);
                        failList.add(failItem);
                        failCount++;
                        continue;
                    }

                    // 查找订单
                    OmsOrderExample orderExample = new OmsOrderExample();
                    orderExample.createCriteria().andOrderSnEqualTo(orderSn.trim());
                    List<OmsOrder> orders = orderMapper.selectByExample(orderExample);

                    if (orders.isEmpty()) {
                        String errorMsg = "订单号 " + orderSn + " 不存在";
                        errorMessages.add("第" + (i + 1) + "行：" + errorMsg);
                        Map<String, String> failItem = new HashMap<>();
                        failItem.put("orderSn", orderSn);
                        failItem.put("reason", errorMsg);
                        failList.add(failItem);
                        failCount++;
                        continue;
                    }

                    OmsOrder order = orders.get(0);
                    if (order.getStatus() != 1) { // 只有待发货状态的订单才能发货
                        String errorMsg = "订单 " + orderSn + " 状态不是待发货";
                        errorMessages.add("第" + (i + 1) + "行：" + errorMsg);
                        Map<String, String> failItem = new HashMap<>();
                        failItem.put("orderSn", orderSn);
                        failItem.put("reason", errorMsg);
                        failList.add(failItem);
                        failCount++;
                        continue;
                    }

                    // 执行发货操作
                    OmsOrderDeliveryParam deliveryParam = new OmsOrderDeliveryParam();
                    deliveryParam.setOrderId(order.getId());
                    deliveryParam.setDeliveryMethod(deliveryMethod);

                    // 根据发货方式设置相应字段
                    if (deliveryMethod == 1) {
                        // 快递配送：应用快递公司映射
                        String mappedDeliveryCompany = logisticsMapping.getOrDefault(deliveryCompany.trim(), deliveryCompany.trim());
                        deliveryParam.setDeliveryCompany(mappedDeliveryCompany);
                        deliveryParam.setDeliverySn(deliverySn.trim());
                    } else if (deliveryMethod == 2) {
                        // 门店自提：设置门店信息
                        deliveryParam.setStoreName(storeName.trim());
                        if (contactPhone != null && !contactPhone.trim().isEmpty()) {
                            deliveryParam.setContactPhone(contactPhone.trim());
                        }
                        // 门店自提时，将门店名称存储到deliveryCompany字段
                        deliveryParam.setDeliveryCompany(storeName.trim());
                        // 门店自提不需要物流单号，清空该字段
                        deliveryParam.setDeliverySn("");
                    } else if (deliveryMethod == 3) {
                        // 虚拟发货：设置虚拟发货信息
                        deliveryParam.setVirtualDeliveryInfo(virtualDeliveryInfo.trim());
                        // 虚拟发货时清空物流字段
                        deliveryParam.setDeliveryCompany("");
                        deliveryParam.setDeliverySn("");
                    }

                    List<OmsOrderDeliveryParam> deliveryList = java.util.Arrays.asList(deliveryParam);
                    int deliveryResult = delivery(deliveryList);

                    if (deliveryResult > 0) {
                        successCount++;
                    } else {
                        String errorMsg = "订单 " + orderSn + " 发货失败";
                        errorMessages.add("第" + (i + 1) + "行：" + errorMsg);
                        Map<String, String> failItem = new HashMap<>();
                        failItem.put("orderSn", orderSn);
                        failItem.put("reason", errorMsg);
                        failList.add(failItem);
                        failCount++;
                    }

                } catch (Exception e) {
                    String errorMsg = "处理失败 - " + e.getMessage();
                    errorMessages.add("第" + (i + 1) + "行：" + errorMsg);
                    Map<String, String> failItem = new HashMap<>();
                    failItem.put("orderSn", orderSn != null ? orderSn : "");
                    failItem.put("reason", errorMsg);
                    failList.add(failItem);
                    failCount++;
                }
            }

            workbook.close();

        } catch (Exception e) {
            LOGGER.error("批量发货Excel处理失败", e);
            throw new Exception("Excel文件处理失败：" + e.getMessage());
        }

        result.put("successCount", successCount);
        result.put("failCount", failCount);
        result.put("errorMessages", errorMessages);
        result.put("failList", failList);
        result.put("totalCount", successCount + failCount);

        return result;
    }

    @Override
    public void downloadDeliveryTemplate(jakarta.servlet.http.HttpServletResponse response) {
        try {
            // 创建Excel工作簿
            org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("发货模板");

            // 创建标题行
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("订单号");
            headerRow.createCell(1).setCellValue("发货方式");
            headerRow.createCell(2).setCellValue("物流公司");
            headerRow.createCell(3).setCellValue("物流单号");
            headerRow.createCell(4).setCellValue("门店名称");
            headerRow.createCell(5).setCellValue("联系电话");
            headerRow.createCell(6).setCellValue("虚拟发货信息");

            // 创建说明行
            org.apache.poi.ss.usermodel.Row descRow = sheet.createRow(1);
            descRow.createCell(0).setCellValue("必填");
            descRow.createCell(1).setCellValue("必填：1-快递配送，2-门店自提，3-虚拟发货");
            descRow.createCell(2).setCellValue("快递配送时必填");
            descRow.createCell(3).setCellValue("快递配送时必填");
            descRow.createCell(4).setCellValue("门店自提时必填");
            descRow.createCell(5).setCellValue("门店自提时选填");
            descRow.createCell(6).setCellValue("虚拟发货时必填");

            // 创建示例行
            org.apache.poi.ss.usermodel.Row exampleRow = sheet.createRow(2);
            exampleRow.createCell(0).setCellValue("示例订单号");
            exampleRow.createCell(1).setCellValue("1");
            exampleRow.createCell(2).setCellValue("顺丰快递");
            exampleRow.createCell(3).setCellValue("SF1234567890");
            exampleRow.createCell(4).setCellValue("示例门店");
            exampleRow.createCell(5).setCellValue("13800138000");
            exampleRow.createCell(6).setCellValue("激活码：ABC123，下载链接：http://example.com");

            // 查询待发货订单（状态为1）
            OmsOrderExample orderExample = new OmsOrderExample();
            orderExample.createCriteria()
                    .andStatusEqualTo(1);  // 待发货状态
            orderExample.setOrderByClause("create_time desc");  // 按创建时间倒序
            List<OmsOrder> pendingOrders = orderMapper.selectByExample(orderExample);

            // 填写待发货订单数据
            int rowIndex = 3; // 从第4行开始（前面有标题行、说明行、示例行）
            for (OmsOrder order : pendingOrders) {
                org.apache.poi.ss.usermodel.Row dataRow = sheet.createRow(rowIndex);
                dataRow.createCell(0).setCellValue(order.getOrderSn());  // 订单号
                dataRow.createCell(1).setCellValue("1");  // 发货方式（默认快递配送）
                dataRow.createCell(2).setCellValue("");  // 物流公司（待填写）
                dataRow.createCell(3).setCellValue("");  // 物流单号（待填写）
                dataRow.createCell(4).setCellValue("");  // 门店名称（待填写）
                dataRow.createCell(5).setCellValue("");  // 联系电话（待填写）
                dataRow.createCell(6).setCellValue("");  // 虚拟发货信息（待填写）
                rowIndex++;
            }

            // 设置列宽
            sheet.setColumnWidth(0, 4000);  // 订单号
            sheet.setColumnWidth(1, 3000);  // 发货方式
            sheet.setColumnWidth(2, 3000);  // 物流公司
            sheet.setColumnWidth(3, 4000);  // 物流单号
            sheet.setColumnWidth(4, 3000);  // 门店名称
            sheet.setColumnWidth(5, 3500);  // 联系电话
            sheet.setColumnWidth(6, 8000);  // 虚拟发货信息

            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=batch_delivery_template.xlsx");

            // 写入响应
            workbook.write(response.getOutputStream());
            workbook.close();

        } catch (Exception e) {
            LOGGER.error("下载发货模板失败", e);
            throw new RuntimeException("下载模板失败：" + e.getMessage());
        }
    }

    private String getCellValue(org.apache.poi.ss.usermodel.Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return null;
        }
    }

    @Override
    public List<OmsStoreStockInfo> getStoreStockList(Long skuId) {
        List<OmsStoreStockInfo> result = new java.util.ArrayList<>();
        
        // 获取所有门店
        com.macro.mall.model.OmsStoreAddressExample storeExample = new com.macro.mall.model.OmsStoreAddressExample();
        List<com.macro.mall.model.OmsStoreAddress> storeList = storeAddressMapper.selectByExample(storeExample);
        
        // 获取SKU信息
        com.macro.mall.model.PmsSkuStock skuStock = skuStockMapper.selectByPrimaryKey(skuId);
        String skuCode = skuStock != null ? skuStock.getSkuCode() : "";
        
        for (com.macro.mall.model.OmsStoreAddress store : storeList) {
            OmsStoreStockInfo stockInfo = new OmsStoreStockInfo();
            stockInfo.setStoreId(store.getId());
            stockInfo.setStoreName(store.getAddressName());
            stockInfo.setStoreType(store.getStoreType());
            stockInfo.setIsWarehouse(store.getIsWarehouse() != null && store.getIsWarehouse());
            stockInfo.setSkuId(skuId);
            stockInfo.setSkuCode(skuCode);
            
            // 查询该门店该SKU的库存
            com.macro.mall.model.PmsStoreSkuStockExample stockExample = new com.macro.mall.model.PmsStoreSkuStockExample();
            stockExample.createCriteria()
                    .andStoreIdEqualTo(store.getId())
                    .andSkuIdEqualTo(skuId);
            List<com.macro.mall.model.PmsStoreSkuStock> stockList = storeSkuStockMapper.selectByExample(stockExample);
            
            if (!stockList.isEmpty()) {
                stockInfo.setStock(stockList.get(0).getStock() != null ? stockList.get(0).getStock() : 0);
            } else {
                stockInfo.setStock(0);
            }
            
            result.add(stockInfo);
        }
        
        // 按库存降序排序，地库排在最后
        result.sort((a, b) -> {
            // 地库排在最后
            if (a.getIsWarehouse() && !b.getIsWarehouse()) return 1;
            if (!a.getIsWarehouse() && b.getIsWarehouse()) return -1;
            // 同类型按库存降序
            return b.getStock().compareTo(a.getStock());
        });
        
        return result;
    }

    @Override
    public boolean changeDeliveryStore(OmsChangeDeliveryStoreParam param) {
        LOGGER.info("改选发货门店 - 订单ID: {}, SKU ID: {}, 原门店: {}, 新门店: {}, 数量: {}",
                param.getOrderId(), param.getSkuId(), param.getOriginalStoreId(), param.getNewStoreId(), param.getQuantity());
        
        try {
            // 1. 验证新门店库存是否充足
            com.macro.mall.model.PmsStoreSkuStockExample newStockExample = new com.macro.mall.model.PmsStoreSkuStockExample();
            newStockExample.createCriteria()
                    .andStoreIdEqualTo(param.getNewStoreId())
                    .andSkuIdEqualTo(param.getSkuId());
            List<com.macro.mall.model.PmsStoreSkuStock> newStockList = storeSkuStockMapper.selectByExample(newStockExample);
            
            if (newStockList.isEmpty()) {
                LOGGER.error("新门店没有该SKU的库存记录");
                return false;
            }
            
            com.macro.mall.model.PmsStoreSkuStock newStock = newStockList.get(0);
            Integer newStoreCurrentStock = newStock.getStock() != null ? newStock.getStock() : 0;
            
            if (newStoreCurrentStock < param.getQuantity()) {
                LOGGER.error("新门店库存不足，当前库存: {}, 需要: {}", newStoreCurrentStock, param.getQuantity());
                return false;
            }
            
            // 2. 恢复原门店库存
            com.macro.mall.model.PmsStoreSkuStockExample originalStockExample = new com.macro.mall.model.PmsStoreSkuStockExample();
            originalStockExample.createCriteria()
                    .andStoreIdEqualTo(param.getOriginalStoreId())
                    .andSkuIdEqualTo(param.getSkuId());
            List<com.macro.mall.model.PmsStoreSkuStock> originalStockList = storeSkuStockMapper.selectByExample(originalStockExample);
            
            if (!originalStockList.isEmpty()) {
                com.macro.mall.model.PmsStoreSkuStock originalStock = originalStockList.get(0);
                Integer originalCurrentStock = originalStock.getStock() != null ? originalStock.getStock() : 0;
                Integer originalNewStock = originalCurrentStock + param.getQuantity();
                
                originalStock.setStock(originalNewStock);
                originalStock.setSaleCount(Math.max(0, (originalStock.getSaleCount() != null ? originalStock.getSaleCount() : 0) - param.getQuantity()));
                storeSkuStockMapper.updateByPrimaryKeySelective(originalStock);
                
                LOGGER.info("原门店库存恢复成功 - 门店ID: {}, 库存变化: {} -> {}", 
                        param.getOriginalStoreId(), originalCurrentStock, originalNewStock);
            }
            
            // 3. 扣减新门店库存
            Integer newBeforeStock = newStoreCurrentStock;
            Integer newAfterStock = newBeforeStock - param.getQuantity();
            
            newStock.setStock(newAfterStock);
            newStock.setSaleCount((newStock.getSaleCount() != null ? newStock.getSaleCount() : 0) + param.getQuantity());
            storeSkuStockMapper.updateByPrimaryKeySelective(newStock);
            
            LOGGER.info("新门店库存扣减成功 - 门店ID: {}, 库存变化: {} -> {}", 
                    param.getNewStoreId(), newBeforeStock, newAfterStock);
            
            // 4. 直接修改原库存操作日志记录（更新门店ID和库存信息）
            updateStockLogToNewStore(param.getOrderSn(), param.getSkuId(), param.getOriginalStoreId(), 
                    param.getNewStoreId(), newBeforeStock, newAfterStock);
            
            return true;
            
        } catch (Exception e) {
            LOGGER.error("改选发货门店失败", e);
            throw new RuntimeException("改选发货门店失败：" + e.getMessage());
        }
    }
    
    /**
     * 记录库存操作日志
     */
    private void recordStockOperation(Long storeId, Long orderId, String orderSn,
                                      Long productId, Long skuId, Integer quantity,
                                      Integer beforeStock, Integer afterStock, String reason) {
        try {
            // 获取商品和SKU信息
            com.macro.mall.model.PmsSkuStock skuStock = skuStockMapper.selectByPrimaryKey(skuId);
            
            // 获取商品信息
            String productName = "";
            String productSn = "";
            if (productId != null) {
                com.macro.mall.model.PmsProduct product = productMapper.selectByPrimaryKey(productId);
                if (product != null) {
                    productName = product.getName();
                    productSn = product.getProductSn();
                }
            }
            
            // 生成操作单号
            String operationNo = "ADMIN_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) 
                    + "_" + System.currentTimeMillis() % 1000;
            
            com.macro.mall.model.PmsStockOperationLog log = new com.macro.mall.model.PmsStockOperationLog();
            log.setOperationNo(operationNo);
            log.setOperationType((byte) (quantity > 0 ? 2 : 1)); // 2-出库, 1-入库
            log.setOperationSubtype((byte) 1); // 1-销售出库/退货入库
            log.setProductId(productId);
            log.setProductName(productName);
            log.setProductSn(productSn);
            log.setSkuId(skuId);
            log.setSkuCode(skuStock != null ? skuStock.getSkuCode() : "");
            log.setStoreId(storeId);
            log.setOrderId(orderId);
            log.setOrderSn(orderSn);
            log.setBeforeStock(beforeStock);
            log.setOperationQuantity(quantity > 0 ? -quantity : Math.abs(quantity)); // 出库为负数
            log.setAfterStock(afterStock);
            log.setOperationReason(reason);
            log.setOperatorId(0L);
            log.setOperatorName("后台管理员");
            log.setCreatedAt(new Date());
            
            stockOperationLogMapper.insert(log);
            
        } catch (Exception e) {
            LOGGER.error("记录库存操作日志失败", e);
        }
    }
    
    /**
     * 更新库存操作日志到新门店（直接修改原记录）
     * 同步修改：门店ID、库存信息、操作原因、操作人、操作时间
     */
    private void updateStockLogToNewStore(String orderSn, Long skuId, Long originalStoreId, 
                                          Long newStoreId, Integer newBeforeStock, Integer newAfterStock) {
        try {
            // 查找原库存操作日志
            com.macro.mall.model.PmsStockOperationLogExample example = new com.macro.mall.model.PmsStockOperationLogExample();
            example.createCriteria()
                    .andOrderSnEqualTo(orderSn)
                    .andSkuIdEqualTo(skuId)
                    .andStoreIdEqualTo(originalStoreId)
                    .andOperationTypeEqualTo((byte) 2)
                    .andOperationSubtypeEqualTo((byte) 1);
            example.setOrderByClause("created_at DESC");
            
            List<com.macro.mall.model.PmsStockOperationLog> logs = stockOperationLogMapper.selectByExample(example);
            
            if (!logs.isEmpty()) {
                // 取最新的一条记录进行更新
                com.macro.mall.model.PmsStockOperationLog log = logs.get(0);
                
                // 获取门店名称
                com.macro.mall.model.OmsStoreAddress newStore = storeAddressMapper.selectByPrimaryKey(newStoreId);
                String newStoreName = newStore != null ? newStore.getAddressName() : "门店" + newStoreId;
                com.macro.mall.model.OmsStoreAddress originalStore = storeAddressMapper.selectByPrimaryKey(originalStoreId);
                String originalStoreName = originalStore != null ? originalStore.getAddressName() : "门店" + originalStoreId;
                
                // 记录原始信息用于日志
                Integer originalBeforeStock = log.getBeforeStock();
                Integer originalAfterStock = log.getAfterStock();
                
                // 1. 更新门店ID
                log.setStoreId(newStoreId);
                
                // 2. 更新库存信息
                log.setBeforeStock(newBeforeStock);
                log.setAfterStock(newAfterStock);
                
                // 3. 更新操作原因，追加改选记录
                String originalReason = log.getOperationReason() != null ? log.getOperationReason() : "";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String changeInfo = String.format("【%s 改选发货门店：%s(%d→%d) → %s(%d→%d)】", 
                        sdf.format(new Date()),
                        originalStoreName, originalBeforeStock, originalAfterStock,
                        newStoreName, newBeforeStock, newAfterStock);
                log.setOperationReason(originalReason + changeInfo);
                
                // 4. 更新操作人信息
                log.setOperatorName("后台管理员（改选）");
                
                // 5. 更新操作时间
                log.setCreatedAt(new Date());
                
                stockOperationLogMapper.updateByPrimaryKeySelective(log);
                
                LOGGER.info("库存操作日志已更新 - 订单号: {}, SKU ID: {}, 原门店: {}({}->{}) -> 新门店: {}({}->{})", 
                        orderSn, skuId, 
                        originalStoreName, originalBeforeStock, originalAfterStock,
                        newStoreName, newBeforeStock, newAfterStock);
            } else {
                LOGGER.warn("未找到原库存操作日志 - 订单号: {}, SKU ID: {}, 原门店: {}", orderSn, skuId, originalStoreId);
            }
            
        } catch (Exception e) {
            LOGGER.error("更新库存操作日志失败", e);
        }
    }
}
