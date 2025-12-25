package com.macro.mall.portal.service.impl;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import com.macro.mall.portal.domain.DiyOrderDetailVO;
import com.macro.mall.portal.domain.DiyOrderParam;
import com.macro.mall.portal.domain.DiyOrderResult;
import com.macro.mall.portal.service.PortalDiyOrderService;
import com.macro.mall.portal.service.UmsMemberBalanceService;
import com.macro.mall.portal.service.UmsMemberService;
import com.macro.mall.portal.util.StpMemberUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.util.*;
import java.util.Date;

/**
 * 小程序端DIY订单Service实现类
 * Created by macro on 2024/12/20.
 */
@Service
public class PortalDiyOrderServiceImpl implements PortalDiyOrderService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PortalDiyOrderServiceImpl.class);
    
    @Autowired
    private OmsOrderMapper orderMapper;
    
    @Autowired
    private OmsOrderItemMapper orderItemMapper;
    
    @Autowired
    private OmsOrderDiyInfoMapper orderDiyInfoMapper;
    
    @Autowired
    private UmsDiyDesignMapper designMapper;
    
    @Autowired
    private PmsProductMapper productMapper;
    
    @Autowired
    private UmsMemberReceiveAddressMapper memberReceiveAddressMapper;

    @Autowired
    private UmsMemberMapper memberMapper;

    @Autowired
    private UmsMemberService memberService;

    @Autowired
    private UmsMemberBalanceService memberBalanceService;

    @Override
    @Transactional
    public DiyOrderResult createOrder(DiyOrderParam orderParam) {
        DiyOrderResult result = new DiyOrderResult();
        
        try {
            // 1. 验证DIY设计
            UmsDiyDesign design = designMapper.selectByPrimaryKey(orderParam.getDesignId());
            if (design == null) {
                result.setStatus(1);
                result.setErrorMessage("DIY设计不存在");
                return result;
            }
            
            // 2. 验证商品信息
            PmsProduct product = productMapper.selectByPrimaryKey(orderParam.getProductId());
            if (product == null) {
                result.setStatus(1);
                result.setErrorMessage("商品不存在");
                return result;
            }
            
            // 3. 验证收货地址
            UmsMemberReceiveAddress address = memberReceiveAddressMapper.selectByPrimaryKey(orderParam.getMemberReceiveAddressId());
            if (address == null) {
                result.setStatus(1);
                result.setErrorMessage("收货地址不存在");
                return result;
            }
            
            // 4. 创建订单
            OmsOrder order = buildOrder(orderParam, product, address);
            int orderResult = orderMapper.insertSelective(order);
            if (orderResult <= 0) {
                result.setStatus(1);
                result.setErrorMessage("创建订单失败");
                return result;
            }
            
            // 5. 创建订单项
            OmsOrderItem orderItem = createOrderItem(order, product, orderParam);
            int itemResult = orderItemMapper.insertSelective(orderItem);
            if (itemResult <= 0) {
                result.setStatus(1);
                result.setErrorMessage("创建订单项失败");
                return result;
            }
            
            // 6. 创建订单DIY信息
            OmsOrderDiyInfo orderDiyInfo = createOrderDiyInfo(order, orderItem, design, product, orderParam);
            int diyResult = orderDiyInfoMapper.insertSelective(orderDiyInfo);
            if (diyResult <= 0) {
                result.setStatus(1);
                result.setErrorMessage("创建订单DIY信息失败");
                return result;
            }
            
            // 7. 处理余额支付
            if (orderParam.getPayType() != null && orderParam.getPayType() == 3) {
                // 余额支付逻辑
                BigDecimal payAmount = order.getPayAmount();
                Long currentMemberId = getCurrentMemberId();

                LOGGER.info("DIY订单余额支付检查：memberId={}, payAmount={}", currentMemberId, payAmount);

                // 检查余额是否充足
                if (!memberBalanceService.checkBalanceEnough(currentMemberId, payAmount)) {
                    LOGGER.warn("DIY订单余额不足：memberId={}, payAmount={}", currentMemberId, payAmount);
                    result.setStatus(1);
                    result.setErrorMessage("余额不足，无法完成支付");
                    return result;
                }

                // 扣除用户余额并记录消费记录
                boolean success = memberBalanceService.recordBalanceChange(
                    currentMemberId,
                    2, // 消费类型
                    payAmount,
                    "DIY_ORDER_PAY",
                    order.getOrderSn(),
                    "DIY订单支付：" + order.getOrderSn()
                );

                if (!success) {
                    result.setStatus(1);
                    result.setErrorMessage("余额支付失败");
                    return result;
                }

                // 更新订单状态为已支付
                order.setStatus(1); // 待发货
                order.setPaymentTime(new java.util.Date());
                order.setPayType(3); // 余额支付
                orderMapper.updateByPrimaryKeySelective(order);

                LOGGER.info("DIY订单余额支付成功：orderId={}, payAmount={}", order.getId(), payAmount);
            }

            // 8. 返回结果
            result.setStatus(0);
            result.setOrder(order);
            result.setDiyDesign(design);
            result.setOrderDiyInfo(orderDiyInfo);

            LOGGER.info("创建DIY订单成功：orderId={}, designId={}", order.getId(), design.getId());
            
        } catch (Exception e) {
            LOGGER.error("创建DIY订单失败", e);
            result.setStatus(1);
            result.setErrorMessage("创建订单失败：" + e.getMessage());
        }
        
        return result;
    }

    @Override
    public DiyOrderResult getDiyOrderDetail(Long orderId, Long memberId) {
        DiyOrderResult result = new DiyOrderResult();
        
        try {
            // 获取订单信息
            OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
            if (order == null || !order.getMemberId().equals(memberId)) {
                result.setStatus(1);
                result.setErrorMessage("订单不存在或无权限访问");
                return result;
            }
            
            // 获取订单DIY信息
            OmsOrderDiyInfoExample diyExample = new OmsOrderDiyInfoExample();
            diyExample.createCriteria().andOrderIdEqualTo(orderId);
            java.util.List<OmsOrderDiyInfo> diyInfoList = orderDiyInfoMapper.selectByExampleWithBLOBs(diyExample);
            
            if (!diyInfoList.isEmpty()) {
                OmsOrderDiyInfo orderDiyInfo = diyInfoList.get(0);
                
                // 获取DIY设计信息
                // 这里需要根据实际情况获取设计信息，可能需要从orderDiyInfo中获取设计数据
                
                result.setStatus(0);
                result.setOrder(order);
                result.setOrderDiyInfo(orderDiyInfo);
            } else {
                result.setStatus(1);
                result.setErrorMessage("订单DIY信息不存在");
            }
            
        } catch (Exception e) {
            LOGGER.error("获取DIY订单详情失败", e);
            result.setStatus(1);
            result.setErrorMessage("获取订单详情失败：" + e.getMessage());
        }
        
        return result;
    }

    @Override
    public int cancelDiyOrder(Long orderId, Long memberId) {
        try {
            OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
            if (order == null || !order.getMemberId().equals(memberId)) {
                return 0;
            }
            
            // 只有未支付的订单才能取消
            if (order.getStatus() != 0) {
                return 0;
            }
            
            // 更新订单状态为已取消
            OmsOrder updateOrder = new OmsOrder();
            updateOrder.setId(orderId);
            updateOrder.setStatus(4); // 已取消
            updateOrder.setModifyTime(new Date());
            
            return orderMapper.updateByPrimaryKeySelective(updateOrder);
            
        } catch (Exception e) {
            LOGGER.error("取消DIY订单失败", e);
            return 0;
        }
    }
    
    /**
     * 创建订单
     */
    private OmsOrder buildOrder(DiyOrderParam orderParam, PmsProduct product, UmsMemberReceiveAddress address) {
        OmsOrder order = new OmsOrder();
        
        // 生成订单编号
        order.setOrderSn(generateOrderSn());
        
        // 设置用户信息
        order.setMemberId(getCurrentMemberId());
        order.setMemberUsername(getCurrentMemberUsername());
        
        // 设置收货地址信息
        order.setReceiverName(address.getName());
        order.setReceiverPhone(address.getPhoneNumber());
        order.setReceiverPostCode(address.getPostCode());
        order.setReceiverProvince(address.getProvince());
        order.setReceiverCity(address.getCity());
        order.setReceiverRegion(address.getRegion());
        order.setReceiverDetailAddress(address.getDetailAddress());
        
        // 使用前端传递的价格信息（前端已经计算了DIY价格 = 基础价格 * 1.5 + 运费）
        BigDecimal totalAmount = orderParam.getTotalPrice(); // 前端传递的总价（包含运费）
        BigDecimal freightAmount = orderParam.getFreightAmount(); // 前端传递的运费
        BigDecimal totalProductAmount = totalAmount.subtract(freightAmount); // 商品总价（不含运费）

        // 处理优惠券（DIY订单可以使用优惠券）
        BigDecimal couponAmount = BigDecimal.ZERO;
        if (orderParam.getCouponId() != null) {
            // 这里应该调用优惠券服务计算优惠金额
            // couponAmount = couponService.calculateCouponAmount(orderParam.getCouponId(), totalProductAmount);
        }

        // 处理积分抵扣（DIY订单可以获得积分）
        BigDecimal integrationAmount = BigDecimal.ZERO;
        if (orderParam.getUseIntegration() != null && orderParam.getUseIntegration() > 0) {
            // 积分抵扣计算：100积分=1元
            integrationAmount = new BigDecimal(orderParam.getUseIntegration()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_DOWN);
        }

        // 计算最终支付金额
        BigDecimal payAmount = totalAmount.subtract(couponAmount).subtract(integrationAmount);
        if (payAmount.compareTo(BigDecimal.ZERO) < 0) {
            payAmount = BigDecimal.ZERO;
        }

        order.setTotalAmount(totalAmount);
        order.setPayAmount(payAmount);
        order.setFreightAmount(freightAmount);
        order.setPromotionAmount(BigDecimal.ZERO); // DIY订单不参与促销活动
        order.setIntegrationAmount(integrationAmount);
        order.setCouponAmount(couponAmount);
        order.setDiscountAmount(couponAmount.add(integrationAmount));
        
        // 设置订单状态
        order.setStatus(0); // 待付款
        order.setOrderType(0); // 正常订单
        order.setSourceType(orderParam.getSourceType());
        order.setPayType(orderParam.getPayType());
        order.setAutoConfirmDay(15);
        order.setIntegration(0);
        order.setGrowth(0);
        order.setNote(orderParam.getNote());

        // 设置学校ID：优先使用参数传递的学校ID，否则从会员信息获取
        if (orderParam.getSchoolId() != null) {
            order.setSourceSchoolId(orderParam.getSchoolId());
        } else {
            // 从当前会员信息获取学校ID
            Long currentMemberId = getCurrentMemberId();
            if (currentMemberId != null) {
                UmsMember member = memberMapper.selectByPrimaryKey(currentMemberId);
                if (member != null && member.getSchoolId() != null) {
                    order.setSourceSchoolId(member.getSchoolId());
                }
            }
        }

        // 设置时间
        order.setCreateTime(new Date());
        order.setModifyTime(new Date());
        
        return order;
    }
    
    /**
     * 创建订单项
     */
    private OmsOrderItem createOrderItem(OmsOrder order, PmsProduct product, DiyOrderParam orderParam) {
        OmsOrderItem orderItem = new OmsOrderItem();

        // 使用前端传递的价格信息
        BigDecimal totalProductAmount = orderParam.getTotalPrice().subtract(orderParam.getFreightAmount());
        BigDecimal unitPrice = totalProductAmount.divide(new BigDecimal(orderParam.getQuantity()), 2, BigDecimal.ROUND_HALF_UP);

        orderItem.setOrderId(order.getId());
        orderItem.setOrderSn(order.getOrderSn());
        orderItem.setProductId(product.getId());
        orderItem.setProductPic(product.getPic());
        orderItem.setProductName(product.getName() + "（定制版）"); // 标识为定制版
        orderItem.setProductBrand(product.getBrandName());
        orderItem.setProductSn(product.getProductSn() + "_DIY"); // 添加DIY标识
        orderItem.setProductPrice(unitPrice); // 使用计算出的单价
        orderItem.setProductQuantity(orderParam.getQuantity());
        orderItem.setProductSkuId(null); // DIY订单没有SKU选择
        orderItem.setProductSkuCode(null);
        orderItem.setProductCategoryId(product.getProductCategoryId());
        orderItem.setPromotionAmount(BigDecimal.ZERO); // DIY订单不参与促销
        orderItem.setCouponAmount(order.getCouponAmount()); // 从订单获取优惠券金额
        orderItem.setIntegrationAmount(order.getIntegrationAmount()); // 从订单获取积分抵扣
        orderItem.setRealAmount(totalProductAmount.subtract(order.getCouponAmount()).subtract(order.getIntegrationAmount()));

        // DIY订单可以获得积分和成长值（按实际价格计算）
        orderItem.setGiftIntegration(totalProductAmount.intValue()); // 1元=1积分
        orderItem.setGiftGrowth(orderParam.getQuantity()); // 1件=1成长值

        return orderItem;
    }
    
    /**
     * 创建订单DIY信息
     */
    private OmsOrderDiyInfo createOrderDiyInfo(OmsOrder order, OmsOrderItem orderItem, UmsDiyDesign design, PmsProduct product, DiyOrderParam orderParam) {
        OmsOrderDiyInfo orderDiyInfo = new OmsOrderDiyInfo();

        orderDiyInfo.setOrderId(order.getId());
        orderDiyInfo.setOrderItemId(orderItem.getId());
        orderDiyInfo.setOrderSn(order.getOrderSn());
        orderDiyInfo.setDesignId(design.getId());
        orderDiyInfo.setTemplateId(design.getTemplateId());
        orderDiyInfo.setDesignSnapshot(design.getDesignData());

        // 直接保存前端传递的customizeFaces JSON数据到final_images字段
        String finalImages = "";
        String previewImage = "";

        if (StringUtils.hasText(orderParam.getCustomizeFaces())) {
            // 直接保存完整的customizeFaces JSON数据
            finalImages = orderParam.getCustomizeFaces();

            try {
                // 解析JSON数据，提取第一张图片作为主预览图
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode facesArray = objectMapper.readTree(orderParam.getCustomizeFaces());

                if (facesArray.isArray() && facesArray.size() > 0) {
                    JsonNode firstFace = facesArray.get(0);
                    String finalImageUrl = firstFace.path("finalImage").asText();
                    String previewImageUrl = firstFace.path("previewImageUrl").asText();
                    String previewImageField = firstFace.path("previewImage").asText();

                    // 优先使用finalImage，然后是previewImageUrl，最后是previewImage
                    if (StringUtils.hasText(finalImageUrl)) {
                        previewImage = finalImageUrl;
                    } else if (StringUtils.hasText(previewImageUrl)) {
                        previewImage = previewImageUrl;
                    } else if (StringUtils.hasText(previewImageField)) {
                        previewImage = previewImageField;
                    }
                }

                LOGGER.info("保存customizeFaces JSON数据到final_images，主预览图：{}", previewImage);

            } catch (Exception e) {
                LOGGER.error("解析customizeFaces失败：{}", e.getMessage(), e);
            }
        } else {
            // 如果没有customizeFaces数据，回退到使用设计的预览图
            String designPreviewImages = design.getPreviewImages();
            if (StringUtils.hasText(designPreviewImages)) {
                String firstImage = designPreviewImages.split(",")[0];
                previewImage = firstImage;
                finalImages = designPreviewImages;
            }
        }

        // 确保字段不为null，避免数据库插入失败
        orderDiyInfo.setPreviewImage(StringUtils.hasText(previewImage) ? previewImage : "");
        orderDiyInfo.setFinalImages(StringUtils.hasText(finalImages) ? finalImages : "");

        orderDiyInfo.setProductionStatus((byte) 0); // 待生产
        orderDiyInfo.setCreateTime(new Date());

        return orderDiyInfo;
    }
    
    /**
     * 计算运费
     */
    private BigDecimal calculateFreightAmount(Integer deliveryType) {
        if (deliveryType == null) {
            deliveryType = 1; // 默认标准快递
        }

        switch (deliveryType) {
            case 1: // 标准快递
                return BigDecimal.ZERO; // 免运费
            case 2: // 顺丰快递
                return new BigDecimal("15.00");
            case 3: // 同城配送
                return new BigDecimal("20.00");
            default:
                return BigDecimal.ZERO;
        }
    }

    /**
     * 生成订单编号
     */
    private String generateOrderSn() {
        return "DIY" + System.currentTimeMillis();
    }
    
    /**
     * 计算DIY定制费用
     */
    private BigDecimal calculateDiyCustomizationFee(UmsDiyDesign design, PmsProduct product) {
        try {
            // 基础定制费用（商品价格的30%）
            BigDecimal baseFee = product.getPrice().multiply(new BigDecimal("0.3"));

            // 根据设计复杂度调整费用
            String designData = design.getDesignData();
            if (StringUtils.hasText(designData)) {
                // 解析设计数据，计算元素数量
                int elementCount = countDesignElements(designData);

                // 每个元素增加5元费用
                BigDecimal elementFee = new BigDecimal(elementCount).multiply(new BigDecimal("5"));
                baseFee = baseFee.add(elementFee);
            }

            return baseFee;

        } catch (Exception e) {
            LOGGER.error("计算DIY定制费用失败", e);
            // 返回默认费用（商品价格的20%）
            return product.getPrice().multiply(new BigDecimal("0.2"));
        }
    }

    /**
     * 计算复杂度费用
     */
    private BigDecimal calculateComplexityFee(UmsDiyDesign design) {
        try {
            String designData = design.getDesignData();
            if (!StringUtils.hasText(designData)) {
                return BigDecimal.ZERO;
            }

            BigDecimal complexityFee = BigDecimal.ZERO;

            // 解析设计数据
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode designJson = objectMapper.readTree(designData);

            // 计算文本元素复杂度费用
            complexityFee = complexityFee.add(calculateTextComplexityFee(designJson));

            // 计算图片元素复杂度费用
            complexityFee = complexityFee.add(calculateImageComplexityFee(designJson));

            // 计算特效复杂度费用
            complexityFee = complexityFee.add(calculateEffectComplexityFee(designJson));

            return complexityFee;

        } catch (Exception e) {
            LOGGER.error("计算复杂度费用失败", e);
            return BigDecimal.ZERO;
        }
    }

    /**
     * 计算材质升级费用
     */
    private BigDecimal calculateMaterialUpgradeFee(DiyOrderParam orderParam, PmsProduct product) {
        // 这里可以根据用户选择的材质升级选项计算费用
        // 暂时返回固定费用
        return new BigDecimal("10");
    }

    /**
     * 计算数量折扣
     */
    private BigDecimal calculateQuantityDiscount(BigDecimal unitPrice, Integer quantity) {
        if (quantity == null || quantity <= 1) {
            return BigDecimal.ZERO;
        }

        BigDecimal discountRate = BigDecimal.ZERO;

        // 数量折扣阶梯
        if (quantity >= 10) {
            discountRate = new BigDecimal("0.15"); // 15%折扣
        } else if (quantity >= 5) {
            discountRate = new BigDecimal("0.10"); // 10%折扣
        } else if (quantity >= 3) {
            discountRate = new BigDecimal("0.05"); // 5%折扣
        }

        return unitPrice.multiply(new BigDecimal(quantity)).multiply(discountRate);
    }

    /**
     * 统计设计元素数量
     */
    private int countDesignElements(String designData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode designJson = objectMapper.readTree(designData);

            int count = 0;

            // 统计根级别的元素
            if (designJson.has("elements")) {
                count += designJson.get("elements").size();
            }

            // 统计faces中的元素
            if (designJson.has("faces")) {
                JsonNode faces = designJson.get("faces");
                for (JsonNode face : faces) {
                    if (face.has("elements")) {
                        count += face.get("elements").size();
                    }
                }
            }

            return count;

        } catch (Exception e) {
            LOGGER.error("统计设计元素数量失败", e);
            return 0;
        }
    }

    /**
     * 计算文本元素复杂度费用
     */
    private BigDecimal calculateTextComplexityFee(JsonNode designJson) {
        BigDecimal fee = BigDecimal.ZERO;

        try {
            // 遍历所有文本元素
            List<JsonNode> textElements = findElementsByType(designJson, "text");

            for (JsonNode textElement : textElements) {
                // 基础文本费用
                fee = fee.add(new BigDecimal("2"));

                // 特殊字体费用
                if (textElement.has("fontFamily")) {
                    String fontFamily = textElement.get("fontFamily").asText();
                    if (!isStandardFont(fontFamily)) {
                        fee = fee.add(new BigDecimal("3"));
                    }
                }

                // 特效费用
                if (textElement.has("effects") && textElement.get("effects").size() > 0) {
                    fee = fee.add(new BigDecimal("5"));
                }
            }

        } catch (Exception e) {
            LOGGER.error("计算文本复杂度费用失败", e);
        }

        return fee;
    }

    /**
     * 计算图片元素复杂度费用
     */
    private BigDecimal calculateImageComplexityFee(JsonNode designJson) {
        BigDecimal fee = BigDecimal.ZERO;

        try {
            List<JsonNode> imageElements = findElementsByType(designJson, "image");

            for (JsonNode imageElement : imageElements) {
                // 基础图片费用
                fee = fee.add(new BigDecimal("8"));

                // 高分辨率图片费用
                if (imageElement.has("highRes") && imageElement.get("highRes").asBoolean()) {
                    fee = fee.add(new BigDecimal("5"));
                }
            }

        } catch (Exception e) {
            LOGGER.error("计算图片复杂度费用失败", e);
        }

        return fee;
    }

    /**
     * 计算特效复杂度费用
     */
    private BigDecimal calculateEffectComplexityFee(JsonNode designJson) {
        BigDecimal fee = BigDecimal.ZERO;

        try {
            // 检查是否使用了AI风格化
            if (designJson.has("aiStyled") && designJson.get("aiStyled").asBoolean()) {
                fee = fee.add(new BigDecimal("15"));
            }

            // 检查是否使用了特殊效果
            if (designJson.has("effects")) {
                JsonNode effects = designJson.get("effects");
                fee = fee.add(new BigDecimal(effects.size()).multiply(new BigDecimal("3")));
            }

        } catch (Exception e) {
            LOGGER.error("计算特效复杂度费用失败", e);
        }

        return fee;
    }

    /**
     * 根据类型查找设计元素
     */
    private List<JsonNode> findElementsByType(JsonNode designJson, String elementType) {
        List<JsonNode> elements = new ArrayList<>();

        try {
            // 查找根级别的元素
            if (designJson.has("elements")) {
                JsonNode rootElements = designJson.get("elements");
                for (JsonNode element : rootElements) {
                    if (element.has("type") && elementType.equals(element.get("type").asText())) {
                        elements.add(element);
                    }
                }
            }

            // 查找faces中的元素
            if (designJson.has("faces")) {
                JsonNode faces = designJson.get("faces");
                for (JsonNode face : faces) {
                    if (face.has("elements")) {
                        JsonNode faceElements = face.get("elements");
                        for (JsonNode element : faceElements) {
                            if (element.has("type") && elementType.equals(element.get("type").asText())) {
                                elements.add(element);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            LOGGER.error("查找设计元素失败", e);
        }

        return elements;
    }

    /**
     * 判断是否为标准字体
     */
    private boolean isStandardFont(String fontFamily) {
        List<String> standardFonts = Arrays.asList(
            "Arial", "Helvetica", "Times New Roman", "Courier New",
            "Verdana", "Georgia", "Palatino", "Garamond", "Bookman", "Comic Sans MS"
        );
        return standardFonts.contains(fontFamily);
    }

    /**
     * 获取当前用户ID
     */
    private Long getCurrentMemberId() {
        try {
            return StpMemberUtil.getLoginIdAsLong();
        } catch (Exception e) {
            LOGGER.error("获取当前用户ID失败", e);
            return null;
        }
    }
    
    /**
     * 获取当前用户名
     */
    private String getCurrentMemberUsername() {
        try {
            Long memberId = getCurrentMemberId();
            if (memberId != null) {
                UmsMember member = memberMapper.selectByPrimaryKey(memberId);
                if (member != null) {
                    // 优先使用nickname，如果为空则使用username
                    return (member.getNickname() != null && !member.getNickname().trim().isEmpty())
                        ? member.getNickname()
                        : member.getUsername();
                }
            }
            return "未知用户";
        } catch (Exception e) {
            LOGGER.error("获取当前用户名失败", e);
            return "未知用户";
        }
    }

    @Override
    public BigDecimal calculateDiyOrderPrice(DiyOrderParam orderParam) {
        try {
            // 获取商品信息
            PmsProduct product = productMapper.selectByPrimaryKey(orderParam.getProductId());
            if (product == null) {
                return BigDecimal.ZERO;
            }

            // 获取设计信息
            UmsDiyDesign design = designMapper.selectByPrimaryKey(orderParam.getDesignId());
            if (design == null) {
                return BigDecimal.ZERO;
            }

            // 计算基础价格
            BigDecimal basePrice = product.getPrice();

            // 计算DIY定制费用
            BigDecimal diyFee = calculateDiyCustomizationFee(design, product);

            // 计算复杂度费用
            BigDecimal complexityFee = calculateComplexityFee(design);

            // 计算材质升级费用
            BigDecimal materialUpgradeFee = calculateMaterialUpgradeFee(orderParam, product);

            // 计算单价
            BigDecimal unitPrice = basePrice.add(diyFee).add(complexityFee).add(materialUpgradeFee);

            // 计算数量折扣
            BigDecimal quantityDiscount = calculateQuantityDiscount(unitPrice, orderParam.getQuantity());

            // 计算商品总价
            BigDecimal totalProductAmount = unitPrice.multiply(new BigDecimal(orderParam.getQuantity())).subtract(quantityDiscount);

            // 计算运费
            BigDecimal freightAmount = calculateFreightAmount(orderParam.getDeliveryType());

            // 计算总价
            return totalProductAmount.add(freightAmount);

        } catch (Exception e) {
            LOGGER.error("计算DIY订单价格失败", e);
            return BigDecimal.ZERO;
        }
    }



    @Override
    public CommonPage<DiyOrderDetailVO> getDiyOrderList(Long memberId, Integer status, Integer pageNum, Integer pageSize) {
        // 这里需要实现分页查询逻辑
        // 暂时返回空结果
        return new CommonPage<>();
    }

    @Override
    public int updateProductionStatus(Long orderId, Byte productionStatus) {
        try {
            OmsOrderDiyInfoExample example = new OmsOrderDiyInfoExample();
            example.createCriteria().andOrderIdEqualTo(orderId);

            OmsOrderDiyInfo updateInfo = new OmsOrderDiyInfo();
            updateInfo.setProductionStatus(productionStatus);
            updateInfo.setUpdateTime(new Date());

            return orderDiyInfoMapper.updateByExampleSelective(updateInfo, example);

        } catch (Exception e) {
            LOGGER.error("更新订单生产状态失败", e);
            return 0;
        }
    }

    @Override
    public String getProductionStatusName(Byte productionStatus) {
        if (productionStatus == null) {
            return "未知状态";
        }

        switch (productionStatus) {
            case 0:
                return "待生产";
            case 1:
                return "生产中";
            case 2:
                return "已完成";
            default:
                return "未知状态";
        }
    }

    @Override
    public int confirmReceive(Long orderId, Long memberId) {
        try {
            OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
            if (order == null || !order.getMemberId().equals(memberId)) {
                return 0;
            }

            // 只有已发货的订单才能确认收货
            if (order.getStatus() != 2) {
                return 0;
            }

            // 更新订单状态为已完成
            OmsOrder updateOrder = new OmsOrder();
            updateOrder.setId(orderId);
            updateOrder.setStatus(3); // 已完成
            updateOrder.setReceiveTime(new Date());
            updateOrder.setModifyTime(new Date());

            return orderMapper.updateByPrimaryKeySelective(updateOrder);

        } catch (Exception e) {
            LOGGER.error("确认收货失败", e);
            return 0;
        }
    }
}
