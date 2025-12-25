package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.portal.domain.OmsOrderReturnApplyParam;
import com.macro.mall.portal.domain.OmsOrderReturnApplyDeliveryParam;
import com.macro.mall.portal.domain.OmsOrderReturnApplyMultiStepParam;
import com.macro.mall.portal.service.OmsPortalOrderReturnApplyService;
import com.macro.mall.portal.service.OmsPortalOrderReturnReasonService;
import com.macro.mall.model.OmsOrderReturnReason;
import com.macro.mall.model.OmsOrderReturnApply;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

import java.util.List;

/**
 * 申请退货管理Controller
 * Created by macro on 2018/10/17.
 */
@Controller
@Tag(name = "OmsPortalOrderReturnApplyController", description = "申请退货管理")
@RequestMapping("/returnApply")
public class OmsPortalOrderReturnApplyController {
    @Autowired
    private OmsPortalOrderReturnApplyService returnApplyService;
    
    @Autowired
    private OmsPortalOrderReturnReasonService returnReasonService;

    @Operation(summary = "申请退货")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult create(@RequestBody OmsOrderReturnApplyParam returnApply) {
        int count = returnApplyService.create(returnApply);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
    
    @Operation(summary = "获取退货原因列表")
    @RequestMapping(value = "/returnReasons", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<OmsOrderReturnReason>> getReturnReasons() {
        List<OmsOrderReturnReason> enabledReasons = returnReasonService.getEnabledReasons();
        return CommonResult.success(enabledReasons);
    }
    
    @Operation(summary = "获取订单的退货申请列表")
    @RequestMapping(value = "/order/{orderId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<OmsOrderReturnApply>> getOrderReturnApplyList(@PathVariable Long orderId) {
        List<OmsOrderReturnApply> returnApplyList = returnApplyService.getByOrderId(orderId);
        return CommonResult.success(returnApplyList);
    }
    
    @Operation(summary = "更新退货申请快递信息")
    @RequestMapping(value = "/updateDelivery", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateDeliveryInfo(@RequestBody OmsOrderReturnApplyDeliveryParam deliveryParam) {
        int count = returnApplyService.updateDeliveryInfo(deliveryParam);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed("更新快递信息失败");
    }

    @Operation(summary = "多步骤申请售后")
    @RequestMapping(value = "/createMultiStep", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult createMultiStep(@Valid @RequestBody OmsOrderReturnApplyMultiStepParam returnApply) {
        try {
            int count = returnApplyService.createMultiStep(returnApply);
            if (count > 0) {
                return CommonResult.success(count, "售后申请提交成功");
            }
            return CommonResult.failed("售后申请提交失败");
        } catch (Exception e) {
            return CommonResult.failed("售后申请提交失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取售后申请详情")
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<OmsOrderReturnApply> getReturnApplyDetail(@PathVariable Long id) {
        OmsOrderReturnApply returnApply = returnApplyService.getReturnApplyDetail(id);
        if (returnApply != null) {
            return CommonResult.success(returnApply);
        }
        return CommonResult.failed("售后申请不存在");
    }

    @Operation(summary = "取消售后申请")
    @RequestMapping(value = "/cancel/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Object> cancelReturnApply(@PathVariable Long id) {
        try {
            int result = returnApplyService.cancelReturnApply(id);
            if (result > 0) {
                return CommonResult.success(null, "取消成功");
            }
            return CommonResult.failed("取消失败");
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    @Operation(summary = "更新订单售后状态")
    @RequestMapping(value = "/updateOrderAfterSaleStatus", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateOrderAfterSaleStatus(@RequestParam Long orderId, @RequestParam Integer afterSaleStatus) {
        int count = returnApplyService.updateOrderAfterSaleStatus(orderId, afterSaleStatus);
        if (count > 0) {
            return CommonResult.success(count, "订单售后状态更新成功");
        }
        return CommonResult.failed("订单售后状态更新失败");
    }

    @Operation(summary = "更新售后申请状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateReturnApplyStatus(@RequestParam Long returnApplyId,
                                               @RequestParam Integer status,
                                               @RequestParam(required = false) String handleNote) {
        try {
            int count = returnApplyService.updateReturnApplyStatus(returnApplyId, status, handleNote);
            if (count > 0) {
                return CommonResult.success(count, "售后申请状态更新成功");
            }
            return CommonResult.failed("售后申请状态更新失败");
        } catch (Exception e) {
            return CommonResult.failed("售后申请状态更新失败：" + e.getMessage());
        }
    }

    @Operation(summary = "更新退款处理状态")
    @RequestMapping(value = "/updateRefundStatus", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateRefundProcessStatus(@RequestParam Long returnApplyId,
                                                 @RequestParam Byte refundProcessStatus,
                                                 @RequestParam(required = false) String refundTransactionId,
                                                 @RequestParam(required = false) String refundFailReason) {
        try {
            int count = returnApplyService.updateRefundProcessStatus(returnApplyId, refundProcessStatus, refundTransactionId, refundFailReason);
            if (count > 0) {
                return CommonResult.success(count, "退款状态更新成功");
            }
            return CommonResult.failed("退款状态更新失败");
        } catch (Exception e) {
            return CommonResult.failed("退款状态更新失败：" + e.getMessage());
        }
    }

    @Operation(summary = "根据订单ID和状态查询售后申请")
    @RequestMapping(value = "/listByOrder", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<OmsOrderReturnApply>> getByOrderIdAndStatus(@RequestParam Long orderId,
                                                                         @RequestParam(required = false) Integer status) {
        List<OmsOrderReturnApply> returnApplyList = returnApplyService.getByOrderIdAndStatus(orderId, status);
        return CommonResult.success(returnApplyList);
    }

    @Operation(summary = "获取用户的售后申请列表")
    @RequestMapping(value = "/userList", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<OmsOrderReturnApply>> getUserReturnApplyList(@RequestParam Long memberId,
                                                                          @RequestParam(required = false) Integer status,
                                                                          @RequestParam(defaultValue = "1") Integer pageNum,
                                                                          @RequestParam(defaultValue = "10") Integer pageSize) {
        List<OmsOrderReturnApply> returnApplyList = returnApplyService.getUserReturnApplyList(memberId, status, pageNum, pageSize);
        return CommonResult.success(returnApplyList);
    }

    @Operation(summary = "用户填写退货快递信息")
    @RequestMapping(value = "/fillDeliveryInfo", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult fillReturnDeliveryInfo(@RequestParam Long returnApplyId,
                                              @RequestParam String deliveryCompany,
                                              @RequestParam String deliverySn,
                                              @RequestParam(required = false) String deliveryNote) {
        try {
            int count = returnApplyService.fillReturnDeliveryInfo(returnApplyId, deliveryCompany, deliverySn, deliveryNote);
            if (count > 0) {
                return CommonResult.success(count, "快递信息提交成功");
            }
            return CommonResult.failed("快递信息提交失败");
        } catch (Exception e) {
            return CommonResult.failed("快递信息提交失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取退货地址信息")
    @RequestMapping(value = "/returnAddress/{returnApplyId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Map<String, Object>> getReturnAddress(@PathVariable Long returnApplyId) {
        try {
            Map<String, Object> addressInfo = returnApplyService.getReturnAddress(returnApplyId);
            return CommonResult.success(addressInfo);
        } catch (Exception e) {
            return CommonResult.failed("获取退货地址失败：" + e.getMessage());
        }
    }

    @Operation(summary = "查询快递物流信息")
    @RequestMapping(value = "/deliveryTracking", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Map<String, Object>> getDeliveryTrackingInfo(@RequestParam String deliveryCompany,
                                                                     @RequestParam String deliverySn) {
        try {
            Map<String, Object> trackingInfo = returnApplyService.getDeliveryTrackingInfo(deliveryCompany, deliverySn);
            return CommonResult.success(trackingInfo);
        } catch (Exception e) {
            return CommonResult.failed("查询物流信息失败：" + e.getMessage());
        }
    }
}
