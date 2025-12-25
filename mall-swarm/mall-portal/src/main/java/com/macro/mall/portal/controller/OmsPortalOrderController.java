package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.portal.domain.ConfirmOrderResult;
import com.macro.mall.portal.domain.OmsOrderDetail;
import com.macro.mall.portal.domain.OrderParam;
import com.macro.mall.portal.dto.DirectBuyParam;
import com.macro.mall.portal.dto.GiftOrderParam;
import com.macro.mall.portal.dto.OrderLogisticsInfo;
import com.macro.mall.portal.service.OmsPortalOrderService;
import com.macro.mall.portal.service.OrderLogisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 订单管理Controller
 * Created by macro on 2018/8/30.
 */
@Controller
@Tag(name = "OmsPortalOrderController", description = "订单管理")
@RequestMapping("/order")
public class OmsPortalOrderController {
    private static final Logger LOGGER = LoggerFactory.getLogger(OmsPortalOrderController.class);

    @Autowired
    private OmsPortalOrderService portalOrderService;

    @Autowired
    private OrderLogisticsService orderLogisticsService;

    @Operation(summary = "根据购物车信息生成确认单信息")
    @Parameter(name = "storeId", description = "门店ID，用于校验门店库存", in = ParameterIn.QUERY, schema = @Schema(type = "integer"))
    @RequestMapping(value = "/generateConfirmOrder", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<ConfirmOrderResult> generateConfirmOrder(@RequestBody List<Long> cartIds,
                                                                 @RequestParam(required = false) Long storeId) {
        ConfirmOrderResult confirmOrderResult;
        if (storeId != null) {
            confirmOrderResult = portalOrderService.generateConfirmOrder(cartIds, storeId);
        } else {
            confirmOrderResult = portalOrderService.generateConfirmOrder(cartIds);
        }
        return CommonResult.success(confirmOrderResult);
    }

    @Operation(summary = "直接购买生成确认单信息")
    @Parameter(name = "storeId", description = "门店ID，用于校验门店库存", in = ParameterIn.QUERY, schema = @Schema(type = "integer"))
    @RequestMapping(value = "/generateDirectConfirmOrder", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<ConfirmOrderResult> generateDirectConfirmOrder(@RequestBody DirectBuyParam directBuyParam,
                                                                       @RequestParam(required = false) Long storeId) {
        ConfirmOrderResult confirmOrderResult;
        if (storeId != null) {
            confirmOrderResult = portalOrderService.generateDirectConfirmOrder(directBuyParam, storeId);
        } else {
            confirmOrderResult = portalOrderService.generateDirectConfirmOrder(directBuyParam);
        }
        return CommonResult.success(confirmOrderResult);
    }

    @Operation(summary = "根据订单ID查询物流信息")
    @GetMapping("/{orderId}/logistics")
    @ResponseBody
    public CommonResult<OrderLogisticsInfo> getLogisticsInfo(@PathVariable Long orderId) throws Exception {
        return CommonResult.success(orderLogisticsService.getLogisticsInfo(orderId));
    }

    @Operation(summary = "根据购物车信息生成订单")
    @RequestMapping(value = "/generateOrder", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult generateOrder(@RequestBody OrderParam orderParam) {
        try {
            Map<String, Object> result = portalOrderService.generateOrder(orderParam);
            return CommonResult.success(result, "下单成功");
        } catch (Exception e) {
            LOGGER.error("创建订单异常:", e);
            // 提取异常信息返回给前端
            String errorMessage = e.getMessage();
            if (errorMessage == null || errorMessage.isEmpty()) {
                errorMessage = "创建订单失败";
            }
            return CommonResult.failed(errorMessage);
        }
    }

    @Operation(summary = "创建送礼订单")
    @RequestMapping(value = "/generateGiftOrder", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Map<String, Object>> generateGiftOrder(@RequestBody GiftOrderParam giftOrderParam) {
        try {
            Map<String, Object> result = portalOrderService.generateGiftOrder(giftOrderParam);
            return CommonResult.success(result, "下单成功");
        } catch (Exception e) {
            LOGGER.error("创建送礼订单异常:", e);
            // 提取异常信息返回给前端
            String errorMessage = e.getMessage();
            if (errorMessage == null || errorMessage.isEmpty()) {
                errorMessage = "创建订单失败";
            }
            return CommonResult.failed(errorMessage);
        }
    }

    @Operation(summary = "用户支付成功的回调")
    @RequestMapping(value = "/paySuccess", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult paySuccess(@RequestParam Long orderId,
                                   @RequestParam Integer payType,
                                   @RequestParam(required = false) String giftMessage,
                                   @RequestParam(required = false) String giftPic) {
        Map<String, Object> result = portalOrderService.paySuccess(orderId, payType, giftMessage, giftPic);
        return CommonResult.success(result, "支付成功");
    }

    @Operation(summary = "自动取消超时订单")
    @RequestMapping(value = "/cancelTimeOutOrder", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult cancelTimeOutOrder() {
        portalOrderService.cancelTimeOutOrder();
        return CommonResult.success(null);
    }

    @Operation(summary = "取消单个超时订单")
    @RequestMapping(value = "/cancelOrder", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult cancelOrder(Long orderId) {
        portalOrderService.sendDelayMessageCancelOrder(orderId);
        return CommonResult.success(null);
    }

    @Operation(summary = "按状态分页获取用户订单列表")
    @Parameter(name = "status", description = "订单状态：-1->全部；0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->售后",
            in = ParameterIn.QUERY, schema = @Schema(type = "integer",defaultValue = "-1",allowableValues = {"-1","0","1","2","3","4","5"}))
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<OmsOrderDetail>> list(@RequestParam Integer status,
                                                   @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                                   @RequestParam(required = false, defaultValue = "5") Integer pageSize) {
        CommonPage<OmsOrderDetail> orderPage = portalOrderService.list(status,pageNum,pageSize);
        return CommonResult.success(orderPage);
    }

    @Operation(summary = "根据ID获取订单详情")
    @RequestMapping(value = "/detail/{orderId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<OmsOrderDetail> detail(@PathVariable Long orderId) {
        OmsOrderDetail orderDetail = portalOrderService.detail(orderId);
        return CommonResult.success(orderDetail);
    }

    @Operation(summary = "用户取消订单")
    @RequestMapping(value = "/cancelUserOrder", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult cancelUserOrder(Long orderId) {
        portalOrderService.cancelOrder(orderId);
        return CommonResult.success(null);
    }

    @Operation(summary = "用户确认收货")
    @RequestMapping(value = "/confirmReceiveOrder", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult confirmReceiveOrder(Long orderId) {
        portalOrderService.confirmReceiveOrder(orderId);
        return CommonResult.success(null);
    }

    @Operation(summary = "用户删除订单")
    @RequestMapping(value = "/deleteOrder", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult deleteOrder(Long orderId) {
        portalOrderService.deleteOrder(orderId);
        return CommonResult.success(null);
    }

    @Operation(summary = "核销订单")
    @RequestMapping(value = "/pickup", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult pickupOrder(@RequestParam String pickupCode,
                                   @RequestParam(required = false, defaultValue = "前台") String operator) {
        try {
            Map<String, Object> result = portalOrderService.pickupOrder(pickupCode, operator);
            return CommonResult.success(result, "核销成功");
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    @Operation(summary = "获取核销码二维码")
    @RequestMapping(value = "/qrcode/{pickupCode}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<String> getPickupQRCode(@PathVariable String pickupCode) {
        try {
            String qrCodeBase64 = portalOrderService.getPickupQRCode(pickupCode);
            return CommonResult.success(qrCodeBase64);
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }
}
