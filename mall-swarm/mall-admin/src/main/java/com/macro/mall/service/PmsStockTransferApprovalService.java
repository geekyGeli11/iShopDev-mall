package com.macro.mall.service;

import com.macro.mall.dto.*;
import com.macro.mall.model.PmsStockOperationApproval;

import java.util.List;

/**
 * 调货审核Service
 * Created by macro on 2024-12-18.
 */
public interface PmsStockTransferApprovalService {
    
    /**
     * 提交调货申请（不变更库存）
     * @param param 申请参数
     * @return 创建的审核记录ID
     */
    Long submitTransferApproval(PmsStockTransferApplyParam param);
    
    /**
     * 确认发货（供货门店库存减少，创建出库记录）
     * @param approvalId 审核记录ID
     * @return 操作结果
     */
    int confirmShipment(Long approvalId);
    
    /**
     * 确认收货（收货门店库存增加，创建入库记录）
     * @param param 确认参数
     * @return 操作结果
     */
    int confirmReceipt(PmsStockTransferConfirmParam param);
    
    /**
     * 驳回调货申请
     * @param approvalId 审核记录ID
     * @param rejectReason 驳回原因
     * @return 操作结果
     */
    int rejectTransfer(Long approvalId, String rejectReason);
    
    /**
     * 获取调货申请列表
     * @param param 查询参数
     * @return 调货申请列表
     */
    List<PmsStockTransferListVO> getTransferApprovalList(PmsStockTransferQueryParam param);
    
    /**
     * 获取调货申请详情
     * @param approvalId 审核记录ID
     * @return 调货申请详情
     */
    PmsStockTransferDetailVO getTransferApprovalDetail(Long approvalId);
    
    /**
     * 获取调货申请记录（原始数据）
     * @param approvalId 审核记录ID
     * @return 审核记录
     */
    PmsStockOperationApproval getApprovalById(Long approvalId);
    
    /**
     * 检查当前用户是否有权限确认发货
     * @param approval 审核记录
     * @return 是否有权限
     */
    boolean canConfirmShipment(PmsStockOperationApproval approval);
    
    /**
     * 检查当前用户是否有权限确认收货
     * @param approval 审核记录
     * @return 是否有权限
     */
    boolean canConfirmReceipt(PmsStockOperationApproval approval);
    
    /**
     * 检查当前用户是否有权限驳回
     * @param approval 审核记录
     * @return 是否有权限
     */
    boolean canReject(PmsStockOperationApproval approval);
}
