package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.*;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.service.OmsOrderService;
import com.macro.mall.service.OmsOrderLogisticsService;
import com.macro.mall.service.OmsOrderRefundService;
import com.macro.mall.util.CsvExportUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 订单管理Controller
 * Created by macro on 2018/10/11.
 */
@Controller
@Tag(name = "OmsOrderController", description = "订单管理")
@RequestMapping("/order")
public class OmsOrderController {
    @Autowired
    private OmsOrderService orderService;

    @Autowired
    private OmsOrderLogisticsService omsOrderLogisticsService;

    @Autowired
    private OmsOrderRefundService orderRefundService;

    @Operation(summary = "查询订单")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<OmsOrderWithItems>> list(OmsOrderQueryParam queryParam,
                                                   @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                                   @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<OmsOrderWithItems> orderList = orderService.listWithItems(queryParam, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(orderList));
    }

    @Operation(summary = "批量发货")
    @RequestMapping(value = "/update/delivery", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delivery(@RequestBody List<OmsOrderDeliveryParam> deliveryParamList) {
        int count = orderService.delivery(deliveryParamList);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "批量关闭订单")
    @RequestMapping(value = "/update/close", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult close(@RequestParam("ids") List<Long> ids, @RequestParam String note) {
        int count = orderService.close(ids, note);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "批量删除订单")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(@RequestParam("ids") List<Long> ids) {
        int count = orderService.delete(ids);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "获取订单详情:订单信息、商品信息、操作记录")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<OmsOrderDetail> detail(@PathVariable Long id) {
        OmsOrderDetail orderDetailResult = orderService.detail(id);
        return CommonResult.success(orderDetailResult);
    }

    @Operation(summary = "修改收货人信息")
    @RequestMapping(value = "/update/receiverInfo", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateReceiverInfo(@RequestBody OmsReceiverInfoParam receiverInfoParam) {
        int count = orderService.updateReceiverInfo(receiverInfoParam);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "修改订单费用信息")
    @RequestMapping(value = "/update/moneyInfo", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateReceiverInfo(@RequestBody OmsMoneyInfoParam moneyInfoParam) {
        int count = orderService.updateMoneyInfo(moneyInfoParam);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "备注订单")
    @RequestMapping(value = "/update/note", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateNote(@RequestParam("id") Long id,
                                   @RequestParam("note") String note,
                                   @RequestParam("status") Integer status) {
        int count = orderService.updateNote(id, note, status);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "根据订单ID查询物流信息")
    @GetMapping("/{orderId}/logistics")
    @ResponseBody
    public CommonResult<OmsOrderLogisticsInfo> getLogisticsInfo(@PathVariable Long orderId) throws Exception {
        return CommonResult.success(omsOrderLogisticsService.getLogisticsInfo(orderId));
    }

    @Operation(summary = "核销订单")
    @RequestMapping(value = "/pickup", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Map<String, Object>> pickupOrder(@RequestParam String pickupCode,
                                                         @RequestParam(required = false, defaultValue = "后台管理员") String operator) {
        try {
            Map<String, Object> result = orderService.pickupOrder(pickupCode, operator);
            return CommonResult.success(result, "核销成功");
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    @Operation(summary = "导出订单数据")
    @GetMapping(value = "/export")
    public CommonResult<?> exportOrders(OmsOrderQueryParam queryParam, HttpServletResponse response) throws IOException {
        // 判断数据规模
        long total = orderService.count(queryParam);
        final int PAGE_SIZE = 3000;
        long threshold = 100_000; // 10万行以内同步导出
        // 表头
        LinkedHashMap<String, String> headerMap = buildHeader();
        if (total <= threshold) {
            // 同步导出：分页拉取并直接流式写出 CSV
            int PAGE_SIZE_INT = PAGE_SIZE; // for lambda capture
            java.util.concurrent.atomic.AtomicInteger pageNo = new java.util.concurrent.atomic.AtomicInteger(1);
            // 使用listWithItems方法获取包含商品信息的订单数据
            List<OmsOrderWithItems> firstBatch = orderService.listWithItems(queryParam, PAGE_SIZE_INT, pageNo.get());
            List<Map<String, Object>> firstData = convertBatchWithItems(firstBatch);
            CsvExportUtil.exportCsv("订单数据", headerMap, firstData, () -> {
                int next = pageNo.incrementAndGet();
                List<OmsOrderWithItems> batch = orderService.listWithItems(queryParam, PAGE_SIZE_INT, next);
                return batch.isEmpty() ? null : convertBatchWithItems(batch);
            }, response);
            return null;
        } else {
            // TODO: 采用异步导出方案，返回任务ID。这里只返回提示信息示例。
            return CommonResult.success(null, "数据量过大，已转入后台异步导出，请稍后在导出列表查看");
        }
    }

    private LinkedHashMap<String, String> buildHeader() {
        LinkedHashMap<String, String> headerMap = new LinkedHashMap<>();
        headerMap.put("orderSn", "订单编号");
        headerMap.put("memberUsername", "用户账号");
        headerMap.put("totalAmount", "订单金额");
        headerMap.put("payType", "支付方式");
        headerMap.put("sourceType", "订单来源");
        headerMap.put("deliveryType", "取货方式");
        headerMap.put("status", "订单状态");
        headerMap.put("isGift", "是否礼物");
        headerMap.put("createTime", "提交时间");
        // 商品信息字段
        headerMap.put("productName", "商品名称");
        headerMap.put("productSkuCode", "商品SKU编码");
        headerMap.put("productPrice", "商品销售价格");
        headerMap.put("productQuantity", "商品数量");
        headerMap.put("productAttr", "商品规格");
        headerMap.put("productRealAmount", "商品实付金额");
        // 收货信息字段
        headerMap.put("receiverName", "收货人姓名");
        headerMap.put("receiverPhone", "收货人手机");
        headerMap.put("receiverProvince", "省份");
        headerMap.put("receiverCity", "城市");
        headerMap.put("receiverRegion", "地区");
        headerMap.put("receiverDetailAddress", "详细地址");
        headerMap.put("deliverySn", "发货单号");
        headerMap.put("deliveryStore", "发货门店");
        headerMap.put("confirmStatus", "是否确认收货");
        headerMap.put("receiveTime", "确认收货时间");
        headerMap.put("storeId", "门店ID");
        headerMap.put("storeName", "门店名称");
        headerMap.put("storeAddress", "门店地址");
        headerMap.put("autoConfirmDay", "自动确认收货天数");
        headerMap.put("integration", "获得积分");
        headerMap.put("promotionInfo", "优惠信息");
        headerMap.put("useIntegration", "使用积分");
        headerMap.put("couponAmount", "优惠券抵扣金额");
        return headerMap;
    }

    /**
     * 将订单批次转换为导出数据（包含商品信息）
     * 一个订单如果有多个商品，会展开成多行
     */
    private List<Map<String, Object>> convertBatchWithItems(List<OmsOrderWithItems> batch) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Map<String, Object>> result = new java.util.ArrayList<>();

        for (OmsOrderWithItems order : batch) {
            // 获取订单商品列表
            List<com.macro.mall.model.OmsOrderItem> orderItems = order.getOrderItemList();

            // 如果订单没有商品，也导出一行（只有订单信息，商品信息为空）
            if (orderItems == null || orderItems.isEmpty()) {
                Map<String, Object> map = buildOrderBaseMap(order, sdf);
                // 商品信息字段留空
                map.put("productName", "");
                map.put("productSkuCode", "");
                map.put("productPrice", "");
                map.put("productQuantity", "");
                map.put("productAttr", "");
                map.put("productRealAmount", "");
                result.add(map);
            } else {
                // 订单有商品，每个商品一行
                for (com.macro.mall.model.OmsOrderItem item : orderItems) {
                    Map<String, Object> map = buildOrderBaseMap(order, sdf);
                    // 添加商品信息
                    map.put("productName", item.getProductName() != null ? item.getProductName() : "");
                    map.put("productSkuCode", item.getProductSkuCode() != null ? item.getProductSkuCode() : "");
                    map.put("productPrice", item.getProductPrice() != null ? item.getProductPrice() : "");
                    map.put("productQuantity", item.getProductQuantity() != null ? item.getProductQuantity() : "");
                    map.put("productAttr", formatProductAttr(item.getProductAttr()));
                    map.put("productRealAmount", item.getRealAmount() != null ? item.getRealAmount() : "");
                    result.add(map);
                }
            }
        }

        return result;
    }

    /**
     * 格式化商品规格JSON为可读文本
     * 例如: [{"key":"重量","value":"680g"}] -> 重量:680g
     */
    private String formatProductAttr(String productAttr) {
        if (productAttr == null || productAttr.trim().isEmpty()) {
            return "";
        }

        try {
            // 简单的JSON解析，将 [{"key":"重量","value":"680g"}] 转换为 "重量:680g"
            StringBuilder result = new StringBuilder();
            // 使用正则表达式提取key和value
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\"key\":\"([^\"]+)\",\"value\":\"([^\"]+)\"");
            java.util.regex.Matcher matcher = pattern.matcher(productAttr);

            while (matcher.find()) {
                if (result.length() > 0) {
                    result.append("; ");
                }
                result.append(matcher.group(1)).append(":").append(matcher.group(2));
            }

            return result.length() > 0 ? result.toString() : productAttr;
        } catch (Exception e) {
            // 如果解析失败，返回原始字符串
            return productAttr;
        }
    }

    /**
     * 构建订单基础信息Map
     */
    private Map<String, Object> buildOrderBaseMap(OmsOrderWithItems order, java.text.SimpleDateFormat sdf) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("orderSn", order.getOrderSn());
        map.put("memberUsername", order.getMemberUsername());
        map.put("totalAmount", order.getTotalAmount());
        map.put("payType", convertPayType(order.getPayType()));
        map.put("sourceType", convertSourceType(order.getSourceType()));
        map.put("deliveryType", convertDeliveryType(order.getDeliveryType()));
        map.put("status", convertStatus(order.getStatus()));
        map.put("isGift", order.getIsGift() != null && order.getIsGift() ? "是" : "否");
        map.put("createTime", order.getCreateTime()==null? "" : sdf.format(order.getCreateTime()));
        map.put("receiverName", order.getReceiverName());
        map.put("receiverPhone", order.getReceiverPhone());
        map.put("receiverProvince", order.getReceiverProvince());
        map.put("receiverCity", order.getReceiverCity());
        map.put("receiverRegion", order.getReceiverRegion());
        map.put("receiverDetailAddress", order.getReceiverDetailAddress());
        map.put("deliverySn", order.getDeliverySn());
        // 发货门店：优先使用deliveryCompany，如果为空则使用storeName
        String deliveryStore = order.getDeliveryCompany();
        if (deliveryStore == null || deliveryStore.trim().isEmpty()) {
            deliveryStore = order.getStoreName();
        }
        map.put("deliveryStore", deliveryStore != null ? deliveryStore : "");
        map.put("confirmStatus", order.getConfirmStatus()!=null && order.getConfirmStatus()==1? "已确认":"未确认");
        map.put("receiveTime", order.getReceiveTime()==null? "" : sdf.format(order.getReceiveTime()));
        map.put("storeId", order.getStoreId());
        map.put("storeName", order.getStoreName());
        map.put("storeAddress", order.getStoreAddress());
        map.put("autoConfirmDay", order.getAutoConfirmDay());
        map.put("integration", order.getIntegration());
        map.put("promotionInfo", order.getPromotionInfo());
        map.put("useIntegration", order.getUseIntegration());
        map.put("couponAmount", order.getCouponAmount());
        return map;
    }

    // =================== 辅助方法 ===================
    private String convertPayType(Integer value) {
        if (value == null) {
            return "未支付";
        }
        return switch (value) {
            case 1 -> "支付宝";
            case 2 -> "微信";
            case 3 -> "余额支付";
            default -> "未支付";
        };
    }

    private String convertSourceType(Integer value) {
        if (value == null) return "PC订单";
        return switch (value) {
            case 1 -> "小程序订单";
            case 2 -> "自助设备订单";
            default -> "PC订单";
        };
    }

    private String convertDeliveryType(Integer value) {
        if (value == null) return "快递配送";
        return value == 1 ? "门店自取" : "快递配送";
    }

    private String convertStatus(Integer value) {
        if (value == null) return "待付款";
        return switch (value) {
            case 1 -> "待发货";
            case 2 -> "已发货";
            case 3 -> "已完成";
            case 4 -> "已关闭";
            case 5 -> "无效订单";
            default -> "待付款";
        };
    }

    @Operation(summary = "批量发货Excel上传")
    @RequestMapping(value = "/batchDelivery", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Map<String, Object>> batchDelivery(@RequestParam("file") MultipartFile file) {
        try {
            Map<String, Object> result = orderService.batchDeliveryFromExcel(file);
            return CommonResult.success(result);
        } catch (Exception e) {
            return CommonResult.failed("批量发货失败：" + e.getMessage());
        }
    }

    @Operation(summary = "下载发货模板")
    @RequestMapping(value = "/deliveryTemplate", method = RequestMethod.GET)
    @ResponseBody
    public void downloadDeliveryTemplate(HttpServletResponse response) {
        orderService.downloadDeliveryTemplate(response);
    }

    @Operation(summary = "获取指定SKU在各门店的库存信息")
    @RequestMapping(value = "/storeStock/{skuId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<OmsStoreStockInfo>> getStoreStockList(@PathVariable Long skuId) {
        List<OmsStoreStockInfo> stockList = orderService.getStoreStockList(skuId);
        return CommonResult.success(stockList);
    }

    @Operation(summary = "改选发货门店")
    @RequestMapping(value = "/changeDeliveryStore", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult changeDeliveryStore(@RequestBody OmsChangeDeliveryStoreParam param) {
        try {
            boolean result = orderService.changeDeliveryStore(param);
            if (result) {
                return CommonResult.success(null, "改选发货门店成功");
            }
            return CommonResult.failed("改选发货门店失败，请检查新门店库存是否充足");
        } catch (Exception e) {
            return CommonResult.failed("改选发货门店失败：" + e.getMessage());
        }
    }

    @Operation(summary = "订单退款", description = "对已完成的自助订单进行退款，支持微信、支付宝、余额退款")
    @RequestMapping(value = "/refund", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<com.macro.mall.dto.OmsOrderRefundResult> refundOrder(@RequestBody com.macro.mall.dto.OmsOrderRefundParam refundParam) {
        try {
            com.macro.mall.dto.OmsOrderRefundResult result = orderRefundService.processRefund(refundParam);
            if (result.isSuccess()) {
                return CommonResult.success(result, "退款处理成功");
            } else {
                return CommonResult.failed(result.getErrorMsg());
            }
        } catch (Exception e) {
            return CommonResult.failed("退款处理失败：" + e.getMessage());
        }
    }

    @Operation(summary = "检查订单是否可退款")
    @RequestMapping(value = "/canRefund/{orderId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Boolean> canRefund(@PathVariable Long orderId) {
        boolean canRefund = orderRefundService.canRefund(orderId);
        return CommonResult.success(canRefund);
    }
}
