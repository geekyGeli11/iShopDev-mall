package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.OmsOrderReturnApplyResult;
import com.macro.mall.dto.OmsReturnApplyQueryParam;
import com.macro.mall.dto.OmsUpdateStatusParam;
import com.macro.mall.model.OmsOrderReturnApply;
import com.macro.mall.service.OmsOrderReturnApplyService;
import com.macro.mall.service.OmsRefundRecordService;
import com.macro.mall.model.OmsRefundRecord;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单退货申请管理
 * Created by macro on 2018/10/18.
 */
@Controller
@Tag(name = "OmsOrderReturnApplyController", description = "订单退货申请管理")
@RequestMapping("/returnApply")
public class OmsOrderReturnApplyController {
    @Autowired
    private OmsOrderReturnApplyService returnApplyService;
    
    @Autowired
    private OmsRefundRecordService refundRecordService;

    @Operation(summary = "分页查询退货申请")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<OmsOrderReturnApplyResult>> list(OmsReturnApplyQueryParam queryParam,
                                                              @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                                              @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<OmsOrderReturnApplyResult> returnApplyList = returnApplyService.listWithDetails(queryParam, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(returnApplyList));
    }

    @Operation(summary = "批量删除申请")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(@RequestParam("ids") List<Long> ids) {
        int count = returnApplyService.delete(ids);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "获取退货申请详情")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult getItem(@PathVariable Long id) {
        OmsOrderReturnApplyResult result = returnApplyService.getItem(id);
        return CommonResult.success(result);
    }

    @Operation(summary = "修改申请状态")
    @RequestMapping(value = "/update/status/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateStatus(@PathVariable Long id, @RequestBody OmsUpdateStatusParam statusParam) {
        int count = returnApplyService.updateStatus(id, statusParam);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "退款处理")
    @RequestMapping(value = "/refund/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult processRefund(@PathVariable Long id, @RequestBody RefundParam refundParam) {
        try {
            boolean result = returnApplyService.processRefund(id, refundParam);
            if (result) {
                return CommonResult.success("退款成功");
            } else {
                return CommonResult.failed("退款失败");
            }
        } catch (Exception e) {
            return CommonResult.failed("退款处理异常：" + e.getMessage());
        }
    }
    
    @Operation(summary = "查询退款记录")
    @RequestMapping(value = "/refund/records/{returnApplyId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<OmsRefundRecord>> getRefundRecords(@PathVariable Long returnApplyId) {
        List<OmsRefundRecord> records = refundRecordService.getByReturnApplyId(returnApplyId);
        return CommonResult.success(records);
    }

    @Operation(summary = "获取未处理售后申请数量")
    @RequestMapping(value = "/pending/count", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Integer> getPendingReturnApplyCount() {
        Integer count = returnApplyService.getPendingReturnApplyCount();
        return CommonResult.success(count);
    }

    @Operation(summary = "根据订单ID获取售后申请列表")
    @RequestMapping(value = "/order/{orderId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<OmsOrderReturnApplyResult>> getReturnApplyByOrderId(@PathVariable Long orderId) {
        List<OmsOrderReturnApplyResult> returnApplyList = returnApplyService.getReturnApplyByOrderId(orderId);
        return CommonResult.success(returnApplyList);
    }

    /**
     * 退款参数
     */
    public static class RefundParam {
        private Long returnApplyId;
        private Long orderId;
        private String orderSn;
        private Double refundAmount;
        private String refundReason;

        // Getters and Setters
        public Long getReturnApplyId() {
            return returnApplyId;
        }

        public void setReturnApplyId(Long returnApplyId) {
            this.returnApplyId = returnApplyId;
        }

        public Long getOrderId() {
            return orderId;
        }

        public void setOrderId(Long orderId) {
            this.orderId = orderId;
        }

        public String getOrderSn() {
            return orderSn;
        }

        public void setOrderSn(String orderSn) {
            this.orderSn = orderSn;
        }

        public Double getRefundAmount() {
            return refundAmount;
        }

        public void setRefundAmount(Double refundAmount) {
            this.refundAmount = refundAmount;
        }

        public String getRefundReason() {
            return refundReason;
        }

        public void setRefundReason(String refundReason) {
            this.refundReason = refundReason;
        }
    }

}
