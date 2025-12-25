package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.*;
import com.macro.mall.service.PmsStockTransferApprovalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 调货审核Controller
 * Created by macro on 2024-12-18.
 */
@Controller
@Tag(name = "PmsStockTransferApprovalController", description = "调货审核管理")
@RequestMapping("/stock/transfer")
public class PmsStockTransferApprovalController {

    @Autowired
    private PmsStockTransferApprovalService transferApprovalService;

    @Operation(summary = "提交调货申请")
    @RequestMapping(value = "/apply", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Long> submitTransferApproval(@RequestBody PmsStockTransferApplyParam param) {
        try {
            Long approvalId = transferApprovalService.submitTransferApproval(param);
            return CommonResult.success(approvalId, "调货申请提交成功");
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    @Operation(summary = "确认发货")
    @RequestMapping(value = "/ship/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public CommonResult confirmShipment(
            @Parameter(description = "审核记录ID") @PathVariable Long id) {
        try {
            int count = transferApprovalService.confirmShipment(id);
            if (count > 0) {
                return CommonResult.success(null, "确认发货成功");
            } else {
                return CommonResult.failed("确认发货失败");
            }
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    @Operation(summary = "确认收货")
    @RequestMapping(value = "/confirm", method = RequestMethod.PUT)
    @ResponseBody
    public CommonResult confirmReceipt(@RequestBody PmsStockTransferConfirmParam param) {
        try {
            int count = transferApprovalService.confirmReceipt(param);
            if (count > 0) {
                return CommonResult.success(null, "确认收货成功");
            } else {
                return CommonResult.failed("确认收货失败");
            }
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    @Operation(summary = "驳回调货申请")
    @RequestMapping(value = "/reject/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public CommonResult rejectTransfer(
            @Parameter(description = "审核记录ID") @PathVariable Long id,
            @Parameter(description = "驳回原因") @RequestParam String rejectReason) {
        try {
            int count = transferApprovalService.rejectTransfer(id, rejectReason);
            if (count > 0) {
                return CommonResult.success(null, "驳回成功");
            } else {
                return CommonResult.failed("驳回失败");
            }
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    @Operation(summary = "获取调货申请列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<PmsStockTransferListVO>> getTransferApprovalList(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "operationNo", required = false) String operationNo,
            @RequestParam(value = "approvalStatus", required = false) Byte approvalStatus,
            @RequestParam(value = "fromStoreId", required = false) Long fromStoreId,
            @RequestParam(value = "toStoreId", required = false) Long toStoreId,
            @RequestParam(value = "operatorId", required = false) Long operatorId,
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime,
            @RequestParam(value = "relatedStoreId", required = false) Long relatedStoreId) {
        
        PmsStockTransferQueryParam param = new PmsStockTransferQueryParam();
        param.setPageNum(pageNum);
        param.setPageSize(pageSize);
        param.setOperationNo(operationNo);
        param.setApprovalStatus(approvalStatus);
        param.setFromStoreId(fromStoreId);
        param.setToStoreId(toStoreId);
        param.setOperatorId(operatorId);
        param.setStartTime(startTime);
        param.setEndTime(endTime);
        param.setRelatedStoreId(relatedStoreId);
        
        List<PmsStockTransferListVO> list = transferApprovalService.getTransferApprovalList(param);
        return CommonResult.success(CommonPage.restPage(list));
    }

    @Operation(summary = "获取调货申请详情")
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<PmsStockTransferDetailVO> getTransferApprovalDetail(
            @Parameter(description = "审核记录ID") @PathVariable Long id) {
        try {
            PmsStockTransferDetailVO detail = transferApprovalService.getTransferApprovalDetail(id);
            return CommonResult.success(detail);
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }
}
