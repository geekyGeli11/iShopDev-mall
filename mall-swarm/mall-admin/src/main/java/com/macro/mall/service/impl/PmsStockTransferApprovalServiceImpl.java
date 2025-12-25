package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.dto.*;
import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import com.macro.mall.service.AdminNotificationService;
import com.macro.mall.service.PmsStockTransferApprovalService;
import com.macro.mall.service.UmsAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 调货审核Service实现类
 * Created by macro on 2024-12-18.
 */
@Service
public class PmsStockTransferApprovalServiceImpl implements PmsStockTransferApprovalService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PmsStockTransferApprovalServiceImpl.class);
    
    // 审核状态常量
    private static final byte STATUS_APPLIED = 0;      // 已申请（待发货）
    private static final byte STATUS_SHIPPED = 1;      // 已发货（待确认）
    private static final byte STATUS_REJECTED = 2;     // 已驳回
    private static final byte STATUS_CONFIRMED = 3;    // 已确认收货
    
    // 操作类型常量
    private static final byte OPERATION_TYPE_TRANSFER = 4;  // 调货
    
    @Autowired
    private PmsStockOperationApprovalMapper approvalMapper;
    
    @Autowired
    private PmsStockOperationApprovalItemMapper approvalItemMapper;
    
    @Autowired
    private PmsStockOperationLogMapper stockOperationLogMapper;
    
    @Autowired
    private PmsStoreSkuStockMapper storeSkuStockMapper;
    
    @Autowired
    private PmsSkuStockMapper skuStockMapper;
    
    @Autowired
    private OmsStoreAddressMapper storeAddressMapper;
    
    @Autowired
    private UmsAdminService adminService;
    
    @Autowired(required = false)
    private AdminNotificationService adminNotificationService;

    @Override
    @Transactional
    public Long submitTransferApproval(PmsStockTransferApplyParam param) {
        LOGGER.info("提交调货申请，从门店 {} 到门店 {}", param.getFromStoreId(), param.getToStoreId());
        
        // 1. 验证参数
        if (param.getFromStoreId() == null || param.getToStoreId() == null) {
            throw new RuntimeException("调货操作必须指定源门店和目标门店");
        }
        if (param.getFromStoreId().equals(param.getToStoreId())) {
            throw new RuntimeException("源门店和目标门店不能相同");
        }
        if (param.getItems() == null || param.getItems().isEmpty()) {
            throw new RuntimeException("调货明细不能为空");
        }
        
        // 2. 验证门店是否存在
        OmsStoreAddress fromStore = storeAddressMapper.selectByPrimaryKey(param.getFromStoreId());
        OmsStoreAddress toStore = storeAddressMapper.selectByPrimaryKey(param.getToStoreId());
        if (fromStore == null || toStore == null) {
            throw new RuntimeException("门店不存在");
        }
        
        // 3. 获取当前操作人信息
        UmsAdmin currentAdmin = adminService.getCurrentAdmin();
        String operatorName = currentAdmin != null ? currentAdmin.getNickName() : "未知用户";
        Long operatorId = currentAdmin != null ? currentAdmin.getId() : 0L;
        String operatorIp = getCurrentOperatorIp();
        
        // 4. 生成操作单号
        String operationNo = generateOperationNo();
        
        // 5. 创建审核记录
        PmsStockOperationApproval approval = new PmsStockOperationApproval();
        approval.setOperationNo(operationNo);
        approval.setOperationType(OPERATION_TYPE_TRANSFER);
        approval.setOperationSubtype(param.getOperationSubtype() != null ? param.getOperationSubtype().byteValue() : null);
        approval.setFromStoreId(param.getFromStoreId());
        approval.setToStoreId(param.getToStoreId());
        approval.setTotalItems(param.getItems().size());
        approval.setApprovalStatus(STATUS_APPLIED);
        approval.setOperatorId(operatorId);
        approval.setOperatorName(operatorName);
        approval.setOperatorIp(operatorIp);
        approval.setIsExecuted((byte) 0);
        approval.setOperationReason(param.getReason());
        approval.setOperationDescription(param.getDescription());
        approval.setCreatedAt(new Date());
        approval.setUpdatedAt(new Date());
        
        approvalMapper.insertSelective(approval);
        Long approvalId = approval.getId();
        
        // 6. 创建审核明细记录
        for (PmsStockTransferApplyParam.TransferItem item : param.getItems()) {
            // 验证供货门店库存是否充足
            PmsStoreSkuStock fromStoreStock = getStoreSkuStock(param.getFromStoreId(), item.getSkuId());
            if (fromStoreStock == null || fromStoreStock.getStock() < item.getOperationQuantity()) {
                throw new RuntimeException("供货门店库存不足，SKU: " + item.getSkuCode());
            }
            
            // 获取收货门店当前库存
            PmsStoreSkuStock toStoreStock = getStoreSkuStock(param.getToStoreId(), item.getSkuId());
            int toStoreCurrentStock = toStoreStock != null ? toStoreStock.getStock() : 0;
            
            PmsStockOperationApprovalItem approvalItem = new PmsStockOperationApprovalItem();
            approvalItem.setApprovalId(approvalId);
            approvalItem.setOperationNo(operationNo);
            approvalItem.setProductId(param.getProductId());
            approvalItem.setProductName(item.getProductName());
            approvalItem.setProductSn(item.getProductSn());
            approvalItem.setSkuId(item.getSkuId());
            approvalItem.setSkuCode(item.getSkuCode());
            approvalItem.setOperationQuantity(item.getOperationQuantity());
            approvalItem.setBeforeStock(fromStoreStock.getStock());  // 供货门店当前库存
            approvalItem.setAfterStock(toStoreCurrentStock + item.getOperationQuantity());  // 收货门店预期库存
            approvalItem.setStoreId(param.getFromStoreId());  // 记录供货门店ID
            approvalItem.setCreatedAt(new Date());
            
            approvalItemMapper.insertSelective(approvalItem);
        }
        
        LOGGER.info("调货申请提交成功，审核记录ID：{}，操作单号：{}", approvalId, operationNo);
        
        // 发送出库单新申请通知
        try {
            if (adminNotificationService != null) {
                adminNotificationService.notifyStockOutNewApplication(
                    operationNo,
                    new Date(),
                    toStore.getAddressName(),  // 申请门店（收货门店）
                    fromStore.getId()          // 被申请调货的门店（供货门店）
                );
            }
        } catch (Exception e) {
            LOGGER.error("发送出库单新申请通知失败，操作单号：{}", operationNo, e);
        }
        
        return approvalId;
    }

    @Override
    @Transactional
    public int confirmShipment(Long approvalId) {
        LOGGER.info("确认发货，审核记录ID：{}", approvalId);
        
        // 1. 获取审核记录
        PmsStockOperationApproval approval = approvalMapper.selectByPrimaryKey(approvalId);
        if (approval == null) {
            throw new RuntimeException("审核记录不存在");
        }
        
        // 2. 验证状态
        if (approval.getApprovalStatus() != STATUS_APPLIED) {
            throw new RuntimeException("当前状态不允许确认发货，只有已申请状态才能确认发货");
        }
        
        // 3. 验证权限
        if (!canConfirmShipment(approval)) {
            throw new RuntimeException("您没有权限确认发货");
        }
        
        // 4. 获取当前操作人信息
        UmsAdmin currentAdmin = adminService.getCurrentAdmin();
        String shipperName = currentAdmin != null ? currentAdmin.getNickName() : "未知用户";
        Long shipperId = currentAdmin != null ? currentAdmin.getId() : 0L;
        String operatorIp = getCurrentOperatorIp();
        
        // 5. 获取门店信息
        OmsStoreAddress fromStore = storeAddressMapper.selectByPrimaryKey(approval.getFromStoreId());
        OmsStoreAddress toStore = storeAddressMapper.selectByPrimaryKey(approval.getToStoreId());
        
        // 6. 获取审核明细
        PmsStockOperationApprovalItemExample itemExample = new PmsStockOperationApprovalItemExample();
        itemExample.createCriteria().andApprovalIdEqualTo(approvalId);
        List<PmsStockOperationApprovalItem> items = approvalItemMapper.selectByExample(itemExample);
        
        // 7. 扣减供货门店库存并记录出库日志
        for (PmsStockOperationApprovalItem item : items) {
            // 获取供货门店库存
            PmsStoreSkuStock fromStoreStock = getStoreSkuStock(approval.getFromStoreId(), item.getSkuId());
            if (fromStoreStock == null) {
                throw new RuntimeException("供货门店SKU库存不存在，SKU: " + item.getSkuCode());
            }
            
            // 再次验证库存是否充足
            if (fromStoreStock.getStock() < item.getOperationQuantity()) {
                throw new RuntimeException("供货门店库存不足，SKU: " + item.getSkuCode() + 
                    "，当前库存：" + fromStoreStock.getStock() + "，需要：" + item.getOperationQuantity());
            }
            
            int beforeStock = fromStoreStock.getStock();
            int afterStock = beforeStock - item.getOperationQuantity();
            
            // 扣减供货门店库存
            PmsStoreSkuStock updateStock = new PmsStoreSkuStock();
            updateStock.setId(fromStoreStock.getId());
            updateStock.setStock(afterStock);
            updateStock.setUpdateTime(new Date());
            storeSkuStockMapper.updateByPrimaryKeySelective(updateStock);
            
            // 同步更新总库存
            updateTotalStock(fromStoreStock.getSkuId(), -item.getOperationQuantity());
            
            // 记录出库日志
            recordStockOperationLog(
                approval.getOperationNo(),
                item.getProductId(),
                item.getProductName(),
                item.getProductSn(),
                item.getSkuId(),
                item.getSkuCode(),
                (byte) 2,  // 出库
                approval.getOperationSubtype(),
                approval.getOperationReason() + "（调出到门店：" + toStore.getAddressName() + "）",
                -item.getOperationQuantity(),
                beforeStock,
                afterStock,
                shipperId,
                shipperName,
                operatorIp,
                approval.getFromStoreId()
            );
            
            LOGGER.info("发货扣减库存成功：SKU {} 从门店 {} 扣减 {}，库存 {} -> {}", 
                item.getSkuCode(), fromStore.getAddressName(), item.getOperationQuantity(), beforeStock, afterStock);
        }
        
        // 8. 更新审核记录状态
        PmsStockOperationApproval updateApproval = new PmsStockOperationApproval();
        updateApproval.setId(approvalId);
        updateApproval.setApprovalStatus(STATUS_SHIPPED);
        updateApproval.setShipperId(shipperId);
        updateApproval.setShipperName(shipperName);
        updateApproval.setShipTime(new Date());
        updateApproval.setUpdatedAt(new Date());
        
        int result = approvalMapper.updateByPrimaryKeySelective(updateApproval);
        
        LOGGER.info("确认发货成功，审核记录ID：{}", approvalId);
        
        // 发送出库单发货通知给申请门店
        try {
            if (adminNotificationService != null) {
                adminNotificationService.notifyStockOutShipped(
                    approval.getOperationNo(),
                    new Date(),
                    fromStore.getAddressName(),  // 供货门店
                    approval.getToStoreId()      // 申请门店（收货门店）
                );
            }
        } catch (Exception e) {
            LOGGER.error("发送出库单发货通知失败，操作单号：{}", approval.getOperationNo(), e);
        }
        
        return result;
    }

    @Override
    @Transactional
    public int confirmReceipt(PmsStockTransferConfirmParam param) {
        LOGGER.info("确认收货，审核记录ID：{}", param.getApprovalId());
        
        // 1. 获取审核记录
        PmsStockOperationApproval approval = approvalMapper.selectByPrimaryKey(param.getApprovalId());
        if (approval == null) {
            throw new RuntimeException("审核记录不存在");
        }
        
        // 2. 验证状态
        if (approval.getApprovalStatus() != STATUS_SHIPPED) {
            throw new RuntimeException("当前状态不允许确认收货，只有已发货状态才能确认收货");
        }
        
        // 3. 验证权限
        if (!canConfirmReceipt(approval)) {
            throw new RuntimeException("您没有权限确认收货");
        }
        
        // 4. 获取当前操作人信息
        UmsAdmin currentAdmin = adminService.getCurrentAdmin();
        String confirmerName = currentAdmin != null ? currentAdmin.getNickName() : "未知用户";
        Long confirmerId = currentAdmin != null ? currentAdmin.getId() : 0L;
        String operatorIp = getCurrentOperatorIp();
        
        // 5. 获取门店信息
        OmsStoreAddress fromStore = storeAddressMapper.selectByPrimaryKey(approval.getFromStoreId());
        OmsStoreAddress toStore = storeAddressMapper.selectByPrimaryKey(approval.getToStoreId());
        
        // 6. 构建确认明细Map
        Map<Long, PmsStockTransferConfirmParam.ConfirmItem> confirmItemMap = new HashMap<>();
        if (param.getItems() != null) {
            for (PmsStockTransferConfirmParam.ConfirmItem item : param.getItems()) {
                confirmItemMap.put(item.getItemId(), item);
            }
        }
        
        // 7. 获取审核明细
        PmsStockOperationApprovalItemExample itemExample = new PmsStockOperationApprovalItemExample();
        itemExample.createCriteria().andApprovalIdEqualTo(param.getApprovalId());
        List<PmsStockOperationApprovalItem> items = approvalItemMapper.selectByExample(itemExample);
        
        // 8. 增加收货门店库存并记录入库日志
        for (PmsStockOperationApprovalItem item : items) {
            // 获取确认的实际数量
            PmsStockTransferConfirmParam.ConfirmItem confirmItem = confirmItemMap.get(item.getId());
            int actualQuantity = confirmItem != null && confirmItem.getActualQuantity() != null 
                ? confirmItem.getActualQuantity() 
                : item.getOperationQuantity();  // 默认使用申请数量
            String diffReason = confirmItem != null ? confirmItem.getDiffReason() : null;
            
            // 计算数量差异
            int quantityDiff = actualQuantity - item.getOperationQuantity();
            
            // 获取或创建收货门店库存
            PmsStoreSkuStock toStoreStock = getStoreSkuStock(approval.getToStoreId(), item.getSkuId());
            if (toStoreStock == null) {
                toStoreStock = createStoreSkuStock(approval.getToStoreId(), item.getSkuId(), 
                    item.getProductId(), item.getSkuCode());
            }
            
            int beforeStock = toStoreStock.getStock();
            int afterStock = beforeStock + actualQuantity;
            
            // 增加收货门店库存
            PmsStoreSkuStock updateStock = new PmsStoreSkuStock();
            updateStock.setId(toStoreStock.getId());
            updateStock.setStock(afterStock);
            updateStock.setUpdateTime(new Date());
            storeSkuStockMapper.updateByPrimaryKeySelective(updateStock);
            
            // 同步更新总库存
            updateTotalStock(toStoreStock.getSkuId(), actualQuantity);
            
            // 更新审核明细记录
            PmsStockOperationApprovalItem updateItem = new PmsStockOperationApprovalItem();
            updateItem.setId(item.getId());
            updateItem.setActualQuantity(actualQuantity);
            updateItem.setQuantityDiff(quantityDiff);
            updateItem.setDiffReason(diffReason);
            updateItem.setActualAfterStock(afterStock);
            approvalItemMapper.updateByPrimaryKeySelective(updateItem);
            
            // 记录入库日志
            String logReason = approval.getOperationReason() + "（从门店调入：" + fromStore.getAddressName() + "）";
            if (quantityDiff != 0) {
                logReason += "，实际收货数量：" + actualQuantity + "，差异：" + quantityDiff;
                if (diffReason != null && !diffReason.isEmpty()) {
                    logReason += "，原因：" + diffReason;
                }
            }
            
            recordStockOperationLog(
                approval.getOperationNo(),
                item.getProductId(),
                item.getProductName(),
                item.getProductSn(),
                item.getSkuId(),
                item.getSkuCode(),
                (byte) 1,  // 入库
                approval.getOperationSubtype(),
                logReason,
                actualQuantity,
                beforeStock,
                afterStock,
                confirmerId,
                confirmerName,
                operatorIp,
                approval.getToStoreId()
            );
            
            LOGGER.info("收货增加库存成功：SKU {} 到门店 {} 增加 {}，库存 {} -> {}", 
                item.getSkuCode(), toStore.getAddressName(), actualQuantity, beforeStock, afterStock);
        }
        
        // 9. 更新审核记录状态
        PmsStockOperationApproval updateApproval = new PmsStockOperationApproval();
        updateApproval.setId(param.getApprovalId());
        updateApproval.setApprovalStatus(STATUS_CONFIRMED);
        updateApproval.setConfirmerId(confirmerId);
        updateApproval.setConfirmerName(confirmerName);
        updateApproval.setConfirmTime(new Date());
        updateApproval.setConfirmRemark(param.getConfirmRemark());
        updateApproval.setIsExecuted((byte) 1);
        updateApproval.setExecutedTime(new Date());
        updateApproval.setUpdatedAt(new Date());
        
        int result = approvalMapper.updateByPrimaryKeySelective(updateApproval);
        
        LOGGER.info("确认收货成功，审核记录ID：{}", param.getApprovalId());
        
        // 发送出库单收货通知给供货门店
        try {
            if (adminNotificationService != null) {
                adminNotificationService.notifyStockOutReceived(
                    approval.getOperationNo(),
                    new Date(),
                    toStore.getAddressName(),    // 收货门店
                    approval.getFromStoreId()    // 供货门店
                );
            }
        } catch (Exception e) {
            LOGGER.error("发送出库单收货通知失败，操作单号：{}", approval.getOperationNo(), e);
        }
        
        return result;
    }

    @Override
    @Transactional
    public int rejectTransfer(Long approvalId, String rejectReason) {
        LOGGER.info("驳回调货申请，审核记录ID：{}", approvalId);
        
        // 1. 获取审核记录
        PmsStockOperationApproval approval = approvalMapper.selectByPrimaryKey(approvalId);
        if (approval == null) {
            throw new RuntimeException("审核记录不存在");
        }
        
        // 2. 验证状态
        if (approval.getApprovalStatus() != STATUS_APPLIED && approval.getApprovalStatus() != STATUS_SHIPPED) {
            throw new RuntimeException("当前状态不允许驳回");
        }
        
        // 3. 验证权限
        if (!canReject(approval)) {
            throw new RuntimeException("您没有权限驳回");
        }
        
        // 4. 获取当前操作人信息
        UmsAdmin currentAdmin = adminService.getCurrentAdmin();
        String approverName = currentAdmin != null ? currentAdmin.getNickName() : "未知用户";
        Long approverId = currentAdmin != null ? currentAdmin.getId() : 0L;
        String operatorIp = getCurrentOperatorIp();
        
        // 5. 如果已发货状态被驳回，需要回滚供货门店库存
        if (approval.getApprovalStatus() == STATUS_SHIPPED) {
            LOGGER.info("已发货状态驳回，需要回滚供货门店库存");
            
            OmsStoreAddress fromStore = storeAddressMapper.selectByPrimaryKey(approval.getFromStoreId());
            
            // 获取审核明细
            PmsStockOperationApprovalItemExample itemExample = new PmsStockOperationApprovalItemExample();
            itemExample.createCriteria().andApprovalIdEqualTo(approvalId);
            List<PmsStockOperationApprovalItem> items = approvalItemMapper.selectByExample(itemExample);
            
            // 回滚库存
            for (PmsStockOperationApprovalItem item : items) {
                PmsStoreSkuStock fromStoreStock = getStoreSkuStock(approval.getFromStoreId(), item.getSkuId());
                if (fromStoreStock != null) {
                    int beforeStock = fromStoreStock.getStock();
                    int afterStock = beforeStock + item.getOperationQuantity();
                    
                    // 回滚供货门店库存
                    PmsStoreSkuStock updateStock = new PmsStoreSkuStock();
                    updateStock.setId(fromStoreStock.getId());
                    updateStock.setStock(afterStock);
                    updateStock.setUpdateTime(new Date());
                    storeSkuStockMapper.updateByPrimaryKeySelective(updateStock);
                    
                    // 同步更新总库存
                    updateTotalStock(fromStoreStock.getSkuId(), item.getOperationQuantity());
                    
                    // 记录回滚日志
                    recordStockOperationLog(
                        approval.getOperationNo() + "-ROLLBACK",
                        item.getProductId(),
                        item.getProductName(),
                        item.getProductSn(),
                        item.getSkuId(),
                        item.getSkuCode(),
                        (byte) 1,  // 入库（回滚）
                        approval.getOperationSubtype(),
                        "调货驳回回滚：" + rejectReason,
                        item.getOperationQuantity(),
                        beforeStock,
                        afterStock,
                        approverId,
                        approverName,
                        operatorIp,
                        approval.getFromStoreId()
                    );
                    
                    LOGGER.info("回滚库存成功：SKU {} 门店 {} 回滚 {}，库存 {} -> {}", 
                        item.getSkuCode(), fromStore.getAddressName(), item.getOperationQuantity(), beforeStock, afterStock);
                }
            }
        }
        
        // 6. 更新审核记录状态
        PmsStockOperationApproval updateApproval = new PmsStockOperationApproval();
        updateApproval.setId(approvalId);
        updateApproval.setApprovalStatus(STATUS_REJECTED);
        updateApproval.setApproverId(approverId);
        updateApproval.setApproverName(approverName);
        updateApproval.setApprovalTime(new Date());
        updateApproval.setApprovalReason(rejectReason);
        updateApproval.setUpdatedAt(new Date());
        
        int result = approvalMapper.updateByPrimaryKeySelective(updateApproval);
        
        LOGGER.info("驳回调货申请成功，审核记录ID：{}", approvalId);
        return result;
    }
    
    @Override
    public List<PmsStockTransferListVO> getTransferApprovalList(PmsStockTransferQueryParam param) {
        LOGGER.info("查询调货申请列表，参数：{}", param);
        
        // 分页
        if (param.getPageNum() != null && param.getPageSize() != null) {
            PageHelper.startPage(param.getPageNum(), param.getPageSize());
        }
        
        // 构建查询条件
        PmsStockOperationApprovalExample example = new PmsStockOperationApprovalExample();
        PmsStockOperationApprovalExample.Criteria criteria = example.createCriteria();
        
        // 只查询调货类型
        criteria.andOperationTypeEqualTo(OPERATION_TYPE_TRANSFER);
        
        if (param.getOperationNo() != null && !param.getOperationNo().isEmpty()) {
            criteria.andOperationNoLike("%" + param.getOperationNo() + "%");
        }
        if (param.getApprovalStatus() != null) {
            criteria.andApprovalStatusEqualTo(param.getApprovalStatus());
        }
        if (param.getFromStoreId() != null) {
            criteria.andFromStoreIdEqualTo(param.getFromStoreId());
        }
        if (param.getToStoreId() != null) {
            criteria.andToStoreIdEqualTo(param.getToStoreId());
        }
        if (param.getOperatorId() != null) {
            criteria.andOperatorIdEqualTo(param.getOperatorId());
        }
        
        // 解析时间参数
        Date startTime = null;
        Date endTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (param.getStartTime() != null && !param.getStartTime().isEmpty()) {
            try {
                startTime = sdf.parse(param.getStartTime());
            } catch (Exception e) {
                LOGGER.warn("开始时间格式错误：{}", param.getStartTime());
            }
        }
        if (param.getEndTime() != null && !param.getEndTime().isEmpty()) {
            try {
                endTime = sdf.parse(param.getEndTime());
            } catch (Exception e) {
                LOGGER.warn("结束时间格式错误：{}", param.getEndTime());
            }
        }
        
        // 添加时间条件到主criteria
        if (startTime != null) {
            criteria.andCreatedAtGreaterThanOrEqualTo(startTime);
        }
        if (endTime != null) {
            criteria.andCreatedAtLessThanOrEqualTo(endTime);
        }
        
        // 关联门店筛选：筛选fromStoreId或toStoreId等于relatedStoreId的记录
        if (param.getRelatedStoreId() != null) {
            // 使用自定义条件实现 OR 逻辑
            criteria.andFromStoreIdEqualTo(param.getRelatedStoreId());
            // 创建第二个条件用于 OR
            PmsStockOperationApprovalExample.Criteria criteria2 = example.createCriteria();
            criteria2.andOperationTypeEqualTo(OPERATION_TYPE_TRANSFER);
            criteria2.andToStoreIdEqualTo(param.getRelatedStoreId());
            if (param.getOperationNo() != null && !param.getOperationNo().isEmpty()) {
                criteria2.andOperationNoLike("%" + param.getOperationNo() + "%");
            }
            if (param.getApprovalStatus() != null) {
                criteria2.andApprovalStatusEqualTo(param.getApprovalStatus());
            }
            // 添加时间条件到criteria2
            if (startTime != null) {
                criteria2.andCreatedAtGreaterThanOrEqualTo(startTime);
            }
            if (endTime != null) {
                criteria2.andCreatedAtLessThanOrEqualTo(endTime);
            }
            example.or(criteria2);
        }
        
        example.setOrderByClause("created_at desc");
        
        List<PmsStockOperationApproval> approvals = approvalMapper.selectByExample(example);
        
        // 获取所有门店信息
        Map<Long, String> storeNameMap = getStoreNameMap();
        
        // 转换为VO
        return approvals.stream().map(approval -> {
            PmsStockTransferListVO vo = new PmsStockTransferListVO();
            vo.setId(approval.getId());
            vo.setOperationNo(approval.getOperationNo());
            vo.setFromStoreId(approval.getFromStoreId());
            vo.setFromStoreName(storeNameMap.getOrDefault(approval.getFromStoreId(), "未知门店"));
            vo.setToStoreId(approval.getToStoreId());
            vo.setToStoreName(storeNameMap.getOrDefault(approval.getToStoreId(), "未知门店"));
            vo.setTotalItems(approval.getTotalItems());
            vo.setApprovalStatus(approval.getApprovalStatus());
            vo.setApprovalStatusText(getApprovalStatusText(approval.getApprovalStatus()));
            vo.setOperatorId(approval.getOperatorId());
            vo.setOperatorName(approval.getOperatorName());
            vo.setShipperName(approval.getShipperName());
            vo.setShipTime(approval.getShipTime());
            vo.setConfirmerName(approval.getConfirmerName());
            vo.setConfirmTime(approval.getConfirmTime());
            vo.setOperationReason(approval.getOperationReason());
            vo.setCreatedAt(approval.getCreatedAt());
            vo.setCanShip(canConfirmShipment(approval));
            vo.setCanConfirm(canConfirmReceipt(approval));
            vo.setCanReject(canReject(approval));
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public PmsStockTransferDetailVO getTransferApprovalDetail(Long approvalId) {
        LOGGER.info("获取调货申请详情，审核记录ID：{}", approvalId);
        
        // 获取审核记录
        PmsStockOperationApproval approval = approvalMapper.selectByPrimaryKey(approvalId);
        if (approval == null) {
            throw new RuntimeException("审核记录不存在");
        }
        
        // 获取审核明细
        PmsStockOperationApprovalItemExample itemExample = new PmsStockOperationApprovalItemExample();
        itemExample.createCriteria().andApprovalIdEqualTo(approvalId);
        List<PmsStockOperationApprovalItem> items = approvalItemMapper.selectByExample(itemExample);
        
        // 获取门店名称
        Map<Long, String> storeNameMap = getStoreNameMap();
        
        // 构建返回结果
        PmsStockTransferDetailVO vo = new PmsStockTransferDetailVO();
        vo.setApproval(approval);
        vo.setItems(items);
        vo.setFromStoreName(storeNameMap.getOrDefault(approval.getFromStoreId(), "未知门店"));
        vo.setToStoreName(storeNameMap.getOrDefault(approval.getToStoreId(), "未知门店"));
        vo.setCanShip(canConfirmShipment(approval));
        vo.setCanConfirm(canConfirmReceipt(approval));
        vo.setCanReject(canReject(approval));
        
        return vo;
    }
    
    @Override
    public PmsStockOperationApproval getApprovalById(Long approvalId) {
        return approvalMapper.selectByPrimaryKey(approvalId);
    }
    
    @Override
    public boolean canConfirmShipment(PmsStockOperationApproval approval) {
        // 只有已申请状态才能确认发货
        if (approval.getApprovalStatus() != STATUS_APPLIED) {
            return false;
        }
        
        UmsAdmin currentAdmin = adminService.getCurrentAdmin();
        if (currentAdmin == null) {
            return false;
        }
        
        // 管理员可以操作
        if (isAdmin(currentAdmin)) {
            return true;
        }
        
        // 供货门店店长可以确认发货
        if (isStoreManager(currentAdmin) && 
            currentAdmin.getStoreId() != null && 
            currentAdmin.getStoreId().equals(approval.getFromStoreId())) {
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean canConfirmReceipt(PmsStockOperationApproval approval) {
        // 只有已发货状态才能确认收货
        if (approval.getApprovalStatus() != STATUS_SHIPPED) {
            return false;
        }
        
        UmsAdmin currentAdmin = adminService.getCurrentAdmin();
        if (currentAdmin == null) {
            return false;
        }
        
        // 管理员可以操作
        if (isAdmin(currentAdmin)) {
            return true;
        }
        
        // 收货门店店长可以确认收货
        if (isStoreManager(currentAdmin) && 
            currentAdmin.getStoreId() != null && 
            currentAdmin.getStoreId().equals(approval.getToStoreId())) {
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean canReject(PmsStockOperationApproval approval) {
        // 只有已申请或已发货状态才能驳回
        if (approval.getApprovalStatus() != STATUS_APPLIED && approval.getApprovalStatus() != STATUS_SHIPPED) {
            return false;
        }
        
        UmsAdmin currentAdmin = adminService.getCurrentAdmin();
        if (currentAdmin == null) {
            return false;
        }
        
        // 管理员可以驳回
        if (isAdmin(currentAdmin)) {
            return true;
        }
        
        // 供货门店店长可以驳回（已申请状态）
        if (approval.getApprovalStatus() == STATUS_APPLIED && 
            isStoreManager(currentAdmin) && 
            currentAdmin.getStoreId() != null && 
            currentAdmin.getStoreId().equals(approval.getFromStoreId())) {
            return true;
        }
        
        // 收货门店店长可以驳回（已发货状态）
        if (approval.getApprovalStatus() == STATUS_SHIPPED && 
            isStoreManager(currentAdmin) && 
            currentAdmin.getStoreId() != null && 
            currentAdmin.getStoreId().equals(approval.getToStoreId())) {
            return true;
        }
        
        return false;
    }
    
    // ==================== 私有方法 ====================
    
    /**
     * 判断是否为管理员账号
     */
    private boolean isAdmin(UmsAdmin admin) {
        // adminType: 0=管理账号，1=门店账号
        return admin.getAdminType() == null || !admin.getAdminType();
    }
    
    /**
     * 判断是否为门店店长
     */
    private boolean isStoreManager(UmsAdmin admin) {
        // adminType: 1=门店账号
        return admin.getAdminType() != null && admin.getAdminType() && admin.getStoreId() != null;
    }
    
    /**
     * 生成操作单号
     */
    private String generateOperationNo() {
        String dateStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String timeStr = new SimpleDateFormat("HHmmss").format(new Date());
        String random = String.format("%04d", new Random().nextInt(10000));
        return "TRF" + dateStr + timeStr + random;
    }
    
    /**
     * 获取当前操作人IP
     */
    private String getCurrentOperatorIp() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("Proxy-Client-IP");
                }
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("WL-Proxy-Client-IP");
                }
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getRemoteAddr();
                }
                return ip;
            }
            return "127.0.0.1";
        } catch (Exception e) {
            LOGGER.warn("获取操作人IP失败", e);
            return "127.0.0.1";
        }
    }

    /**
     * 获取门店SKU库存
     */
    private PmsStoreSkuStock getStoreSkuStock(Long storeId, Long skuId) {
        PmsStoreSkuStockExample example = new PmsStoreSkuStockExample();
        example.createCriteria().andStoreIdEqualTo(storeId).andSkuIdEqualTo(skuId);
        List<PmsStoreSkuStock> list = storeSkuStockMapper.selectByExample(example);
        return list.isEmpty() ? null : list.get(0);
    }
    
    /**
     * 创建门店SKU库存记录
     */
    private PmsStoreSkuStock createStoreSkuStock(Long storeId, Long skuId, Long productId, String skuCode) {
        PmsStoreSkuStock storeStock = new PmsStoreSkuStock();
        storeStock.setStoreId(storeId);
        storeStock.setProductId(productId);
        storeStock.setSkuId(skuId);
        storeStock.setSkuCode(skuCode);
        storeStock.setStock(0);
        storeStock.setLowStock(0);
        storeStock.setLockedStock(0);
        storeStock.setSaleCount(0);
        storeStock.setStatus(1);
        storeStock.setCreateTime(new Date());
        storeStock.setUpdateTime(new Date());
        storeSkuStockMapper.insertSelective(storeStock);
        return storeStock;
    }
    
    /**
     * 更新总库存
     */
    private void updateTotalStock(Long skuId, Integer quantityChange) {
        if (skuId == null || quantityChange == null) {
            return;
        }
        
        PmsSkuStock currentSku = skuStockMapper.selectByPrimaryKey(skuId);
        if (currentSku == null) {
            LOGGER.warn("SKU不存在，ID：{}", skuId);
            return;
        }
        
        int newStock = currentSku.getStock() + quantityChange;
        if (newStock < 0) {
            newStock = 0;
        }
        
        PmsSkuStock updateSku = new PmsSkuStock();
        updateSku.setId(skuId);
        updateSku.setStock(newStock);
        skuStockMapper.updateByPrimaryKeySelective(updateSku);
        
        LOGGER.info("总库存更新：SKU ID {} {} -> {}", skuId, currentSku.getStock(), newStock);
    }
    
    /**
     * 记录库存操作日志
     */
    private void recordStockOperationLog(String operationNo, Long productId, String productName, String productSn,
                                        Long skuId, String skuCode, Byte operationType, Byte operationSubtype,
                                        String reason, Integer quantityChange, Integer beforeStock, Integer afterStock,
                                        Long operatorId, String operatorName, String operatorIp, Long storeId) {
        PmsStockOperationLog operationLog = new PmsStockOperationLog();
        operationLog.setOperationNo(operationNo);
        operationLog.setProductId(productId);
        operationLog.setProductName(productName);
        operationLog.setProductSn(productSn);
        operationLog.setSkuId(skuId);
        operationLog.setSkuCode(skuCode);
        operationLog.setOperationType(operationType);
        operationLog.setOperationSubtype(operationSubtype);
        operationLog.setOperationReason(reason);
        operationLog.setOperationQuantity(quantityChange);
        operationLog.setBeforeStock(beforeStock);
        operationLog.setAfterStock(afterStock);
        operationLog.setOperatorId(operatorId);
        operationLog.setOperatorName(operatorName);
        operationLog.setOperatorIp(operatorIp);
        operationLog.setStoreId(storeId);
        operationLog.setCreatedAt(new Date());
        stockOperationLogMapper.insert(operationLog);
    }
    
    /**
     * 获取所有门店名称Map
     */
    private Map<Long, String> getStoreNameMap() {
        List<OmsStoreAddress> stores = storeAddressMapper.selectByExample(new OmsStoreAddressExample());
        return stores.stream().collect(Collectors.toMap(
            OmsStoreAddress::getId, 
            OmsStoreAddress::getAddressName,
            (v1, v2) -> v1
        ));
    }
    
    /**
     * 获取审核状态文本
     */
    private String getApprovalStatusText(Byte status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case STATUS_APPLIED:
                return "已申请";
            case STATUS_SHIPPED:
                return "已发货";
            case STATUS_REJECTED:
                return "已驳回";
            case STATUS_CONFIRMED:
                return "已确认";
            default:
                return "未知";
        }
    }
}
