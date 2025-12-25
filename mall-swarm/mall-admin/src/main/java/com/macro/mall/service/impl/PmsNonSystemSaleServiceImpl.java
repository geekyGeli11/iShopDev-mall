package com.macro.mall.service.impl;

import com.macro.mall.mapper.PmsSalesTypeMapper;
import com.macro.mall.mapper.PmsNonSystemSaleMapper;
import com.macro.mall.mapper.PmsNonSystemSaleItemMapper;
import com.macro.mall.mapper.PmsSkuStockMapper;
import com.macro.mall.mapper.PmsStockOperationLogMapper;
import com.macro.mall.mapper.PmsProductMapper;
import com.macro.mall.mapper.PmsStoreSkuStockMapper;
import com.macro.mall.mapper.PmsStoreSkuStockExtMapper;
import com.macro.mall.service.OmsStoreAddressService;
import com.macro.mall.model.PmsSalesType;
import com.macro.mall.model.PmsNonSystemSale;
import com.macro.mall.model.PmsNonSystemSaleItem;
import com.macro.mall.model.PmsSkuStock;
import com.macro.mall.model.PmsProduct;
import com.macro.mall.model.PmsStockOperationLog;
import com.macro.mall.model.PmsStoreSkuStock;
import com.macro.mall.model.OmsStoreAddress;
import com.macro.mall.model.PmsSalesTypeExample;
import com.macro.mall.model.PmsNonSystemSaleExample;
import com.macro.mall.model.PmsNonSystemSaleItemExample;
import com.macro.mall.dto.PmsNonSystemSaleCreateRequest;
import com.macro.mall.dto.PmsNonSystemSaleDTO;
import com.macro.mall.service.PmsNonSystemSaleService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.OutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletOutputStream;
import com.alibaba.fastjson.JSON;
import com.macro.mall.dto.PmsNonSystemSaleShareResult;
import com.macro.mall.service.WechatMiniProgramService;
import com.macro.mall.service.AdminNotificationService;

/**
 * 非系统销售服务实现
 * Created by macro on 2025-11-27.
 */
@Service
public class PmsNonSystemSaleServiceImpl implements PmsNonSystemSaleService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PmsNonSystemSaleServiceImpl.class);
    
    @Autowired
    private PmsSalesTypeMapper salesTypeMapper;
    
    @Autowired
    private PmsNonSystemSaleMapper nonSystemSaleMapper;
    
    @Autowired
    private PmsNonSystemSaleItemMapper nonSystemSaleItemMapper;
    
    @Autowired
    private PmsSkuStockMapper skuStockMapper;
    
    @Autowired
    private PmsStockOperationLogMapper stockOperationLogMapper;
    
    @Autowired
    private PmsProductMapper productMapper;
    
    @Autowired
    private PmsStoreSkuStockMapper storeSkuStockMapper;
    
    @Autowired
    private PmsStoreSkuStockExtMapper storeSkuStockExtMapper;
    
    @Autowired
    private OmsStoreAddressService storeAddressService;
    
    @Autowired
    private WechatMiniProgramService wechatMiniProgramService;
    
    @Autowired
    private AdminNotificationService adminNotificationService;
    
    /**
     * 获取所有启用的销售类型列表
     */
    @Override
    public List<PmsSalesType> getAllSalesTypes() {
        PmsSalesTypeExample example = new PmsSalesTypeExample();
        example.createCriteria().andStatusEqualTo((byte) 1);
        example.setOrderByClause("sort ASC");
        return salesTypeMapper.selectByExample(example);
    }
    
    /**
     * 获取SKU库存信息
     */
    @Override
    public PmsSkuStock getSkuStock(Long skuId) {
        return skuStockMapper.selectByPrimaryKey(skuId);
    }
    
    /**
     * 提交非系统销售单
     * 包括：保存销售单信息、保存销售明细、扣减库存、记录操作日志
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PmsNonSystemSaleDTO submitSale(PmsNonSystemSaleCreateRequest request, Long operatorId, String operatorName) {
        try {
            // 1. 校验销售类型
            PmsSalesType saleType = salesTypeMapper.selectByPrimaryKey(request.getSaleTypeId());
            if (saleType == null || saleType.getStatus() == 0) {
                throw new IllegalArgumentException("销售类型不存在或已禁用");
            }
            
            // 2. 校验销售明细
            List<PmsNonSystemSaleCreateRequest.PmsNonSystemSaleItemRequest> items = request.getItems();
            if (items == null || items.isEmpty()) {
                throw new IllegalArgumentException("销售明细不能为空");
            }
            
            // 3. 校验库存并计算总金额
            BigDecimal totalAmount = BigDecimal.ZERO;
            Integer totalQuantity = 0;
            
            for (PmsNonSystemSaleCreateRequest.PmsNonSystemSaleItemRequest item : items) {
                // ✓ 校验SKU存在性
                PmsSkuStock skuStock = skuStockMapper.selectByPrimaryKey(item.getSkuId());
                if (skuStock == null) {
                    throw new IllegalArgumentException("SKU不存在：" + item.getSkuCode());
                }
                
                // ✓ 校验sku_code一致性（同一商品的不同SKU可以有相同编码，但必须与pms_sku_stock中的编码一致）
                if (item.getSkuCode() != null && !item.getSkuCode().equals(skuStock.getSkuCode())) {
                    throw new IllegalArgumentException("SKU编码不匹配，SKU ID：" + item.getSkuId() + 
                        "，期望编码：" + skuStock.getSkuCode() + "，实际编码：" + item.getSkuCode());
                }
                
                // 校验SKU库存
                if (skuStock.getStock() < item.getQuantity()) {
                    throw new IllegalArgumentException("SKU库存不足：" + item.getSkuCode());
                }
                
                // 计算行金额
                BigDecimal lineAmount = item.getSalePrice().multiply(new BigDecimal(item.getQuantity()));
                totalAmount = totalAmount.add(lineAmount);
                totalQuantity += item.getQuantity();
            }
            
            // 4. 创建销售单
            PmsNonSystemSale sale = new PmsNonSystemSale();
            sale.setSaleNo(generateSaleNo());
            sale.setSaleTypeId(request.getSaleTypeId());
            sale.setSaleTypeName(saleType.getName());
            sale.setStoreId(request.getStoreId());
            // 获取门店名称
            sale.setStoreName(request.getStoreName());
            sale.setSaleDate(request.getSaleDate() != null ? request.getSaleDate() : new Date());
            sale.setTotalAmount(totalAmount);
            sale.setTotalQuantity(totalQuantity);
            sale.setRemark(request.getRemark());
            sale.setCustomerName(request.getCustomerName());
            sale.setCustomerPhone(request.getCustomerPhone());
            sale.setOperatorId(operatorId);
            sale.setOperatorName(operatorName);
            sale.setStatus((byte) 1);  // 已提交
            sale.setCreateTime(new Date());
            sale.setUpdateTime(new Date());
            
            nonSystemSaleMapper.insertSelective(sale);
            
            // 5. 保存销售明细并扣减库存
            for (PmsNonSystemSaleCreateRequest.PmsNonSystemSaleItemRequest itemRequest : items) {
                PmsNonSystemSaleItem saleItem = new PmsNonSystemSaleItem();
                saleItem.setSaleId(sale.getId());
                saleItem.setProductId(itemRequest.getProductId());
                saleItem.setProductName(itemRequest.getProductName());
                saleItem.setSkuId(itemRequest.getSkuId());
                saleItem.setSkuCode(itemRequest.getSkuCode());
                saleItem.setStoreId(request.getStoreId());
                saleItem.setStoreName(request.getStoreName());
                saleItem.setQuantity(itemRequest.getQuantity());
                saleItem.setSystemPrice(itemRequest.getSystemPrice());
                saleItem.setSalePrice(itemRequest.getSalePrice());
                saleItem.setLineAmount(itemRequest.getSalePrice().multiply(new BigDecimal(itemRequest.getQuantity())));
                saleItem.setCreateTime(new Date());
                
                // 获取总库存扣减前的值
                PmsSkuStock skuStock = skuStockMapper.selectByPrimaryKey(itemRequest.getSkuId());
                Integer beforeStock = skuStock.getStock();
                Integer newStock = beforeStock - itemRequest.getQuantity();
                
                // 如果前端传来了库存扣减方案，则按照方案执行；否则重新计算
                if (request.getStockDeductionPlans() != null && !request.getStockDeductionPlans().isEmpty()) {
                    // 按照前端传来的库存扣减方案执行
                    deductStockByPlan(request.getStoreId(), itemRequest, saleItem, request.getStockDeductionPlans());
                } else {
                    // 重新计算库存扣减方案
                    deductStockFromMultipleStores(request.getStoreId(), itemRequest, saleItem);
                }
                
                // 扣除总库存
                PmsSkuStock updateSku = new PmsSkuStock();
                updateSku.setId(itemRequest.getSkuId());
                updateSku.setStock(newStock);
                skuStockMapper.updateByPrimaryKeySelective(updateSku);
                
                // 保存销售明细
                nonSystemSaleItemMapper.insertSelective(saleItem);
                
                // 获取商品信息用于记录日志
                PmsProduct product = productMapper.selectByPrimaryKey(itemRequest.getProductId());
                String productSn = product != null ? product.getProductSn() : "";
                
                // 记录库存操作日志
                String operationReason = "非系统销售出库（" + saleType.getName() + "），门店：" + request.getStoreName();
                recordStockOperationLog(sale.getSaleNo(), itemRequest.getProductId(), itemRequest.getProductName(), 
                    productSn, itemRequest.getSkuId(), itemRequest.getSkuCode(), (byte) 2, 
                    saleType.getId().intValue(), operationReason, itemRequest.getQuantity(), 
                    beforeStock, newStock, operatorId, operatorName, null, request.getStoreId());
                
                LOGGER.info("扣减SKU库存：skuId={}, skuCode={}, quantity={}, beforeStock={}, afterStock={}", 
                    itemRequest.getSkuId(), itemRequest.getSkuCode(), itemRequest.getQuantity(), 
                    beforeStock, newStock);
            }
            
            // 6. 发送微信通知给管理员（新申请通知）
            try {
                adminNotificationService.notifySaleNewApplication(
                    sale.getSaleNo(),
                    sale.getCreateTime()
                );
            } catch (Exception e) {
                // 通知失败不影响主流程
                LOGGER.warn("发送销售单新申请通知失败：{}", e.getMessage());
            }
            
            // 7. 返回DTO
            return convertToDTO(sale);
            
        } catch (Exception e) {
            LOGGER.error("提交非系统销售单失败", e);
            throw new RuntimeException("提交销售单失败：" + e.getMessage(), e);
        }
    }
    
    /**
     * 获取销售单列表
     */
    @Override
    public PageInfo<PmsNonSystemSaleDTO> getSaleList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        PmsNonSystemSaleExample example = new PmsNonSystemSaleExample();
        example.setOrderByClause("create_time DESC");
        List<PmsNonSystemSale> saleList = nonSystemSaleMapper.selectByExample(example);
        
        List<PmsNonSystemSaleDTO> dtoList = saleList.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        return new PageInfo<>(dtoList);
    }
    
    /**
     * 获取销售单详情
     */
    @Override
    public PmsNonSystemSaleDTO getSaleDetail(Long saleId) {
        PmsNonSystemSale sale = nonSystemSaleMapper.selectByPrimaryKey(saleId);
        if (sale == null) {
            throw new IllegalArgumentException("销售单不存在");
        }
        
        PmsNonSystemSaleDTO dto = convertToDTO(sale);
        
        // 获取销售明细（包含BLOB字段）
        PmsNonSystemSaleItemExample example = new PmsNonSystemSaleItemExample();
        example.createCriteria().andSaleIdEqualTo(saleId);
        List<PmsNonSystemSaleItem> items = nonSystemSaleItemMapper.selectByExampleWithBLOBs(example);
        
        // 批量查询商品图片和SKU规格信息
        Set<Long> productIds = items.stream().map(PmsNonSystemSaleItem::getProductId).collect(Collectors.toSet());
        Set<Long> skuIds = items.stream().map(PmsNonSystemSaleItem::getSkuId).collect(Collectors.toSet());
        
        // 查询商品图片
        Map<Long, String> productPicMap = new HashMap<>();
        if (!productIds.isEmpty()) {
            for (Long productId : productIds) {
                PmsProduct product = productMapper.selectByPrimaryKey(productId);
                if (product != null && product.getPic() != null) {
                    productPicMap.put(productId, product.getPic());
                }
            }
        }
        
        // 查询SKU规格信息
        Map<Long, String> skuSpecsMap = new HashMap<>();
        if (!skuIds.isEmpty()) {
            for (Long skuId : skuIds) {
                PmsSkuStock skuStock = skuStockMapper.selectByPrimaryKey(skuId);
                if (skuStock != null && skuStock.getSpData() != null) {
                    skuSpecsMap.put(skuId, formatSpecs(skuStock.getSpData()));
                }
            }
        }
        
        List<PmsNonSystemSaleDTO.PmsNonSystemSaleItemDTO> itemDtos = items.stream()
            .map(item -> {
                PmsNonSystemSaleDTO.PmsNonSystemSaleItemDTO itemDto = convertItemToDTO(item);
                // 设置商品图片
                itemDto.setProductPic(productPicMap.get(item.getProductId()));
                // 设置规格信息
                itemDto.setSpecs(skuSpecsMap.get(item.getSkuId()));
                return itemDto;
            })
            .collect(Collectors.toList());
        
        dto.setItems(itemDtos);
        return dto;
    }
    
    /**
     * 格式化规格信息
     * 将JSON格式的spData转换为可读的规格字符串
     */
    @SuppressWarnings("unchecked")
    private String formatSpecs(String spData) {
        if (spData == null || spData.isEmpty()) {
            return null;
        }
        try {
            // spData格式: [{"key":"重量","value":"500克"},{"key":"颜色","value":"红色"}]
            List<Map> specList = JSON.parseArray(spData, Map.class);
            if (specList == null || specList.isEmpty()) {
                return null;
            }
            return specList.stream()
                .map(spec -> spec.get("key") + "：" + spec.get("value"))
                .collect(Collectors.joining("，"));
        } catch (Exception e) {
            LOGGER.warn("解析规格信息失败: {}", spData, e);
            return spData;
        }
    }
    
    /**
     * 审核销售单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveSale(Long saleId) {
        PmsNonSystemSale sale = nonSystemSaleMapper.selectByPrimaryKey(saleId);
        if (sale == null) {
            throw new IllegalArgumentException("销售单不存在");
        }
        
        if (sale.getStatus() != 1) {
            throw new IllegalArgumentException("只能审核已提交的销售单");
        }
        
        PmsNonSystemSale updateSale = new PmsNonSystemSale();
        updateSale.setId(saleId);
        updateSale.setStatus((byte) 2);  // 已审核
        updateSale.setUpdateTime(new Date());
        nonSystemSaleMapper.updateByPrimaryKeySelective(updateSale);
        
        LOGGER.info("审核销售单成功：saleId={}, saleNo={}", saleId, sale.getSaleNo());
        
        // 发送销售单审核结果通知给申请人
        try {
            if (adminNotificationService != null && sale.getOperatorId() != null) {
                adminNotificationService.notifySaleApprovalResult(
                    sale.getSaleNo(),
                    "通过",
                    new Date(),
                    sale.getOperatorId()
                );
            }
        } catch (Exception e) {
            LOGGER.error("发送销售单审核通知失败，saleNo：{}", sale.getSaleNo(), e);
        }
    }
    
    /**
     * 驳回销售单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectSale(Long saleId, String reason) {
        PmsNonSystemSale sale = nonSystemSaleMapper.selectByPrimaryKey(saleId);
        if (sale == null) {
            throw new IllegalArgumentException("销售单不存在");
        }
        
        if (sale.getStatus() != 1) {
            throw new IllegalArgumentException("只能驳回已提交的销售单");
        }
        
        // 恢复库存
        PmsNonSystemSaleItemExample example = new PmsNonSystemSaleItemExample();
        example.createCriteria().andSaleIdEqualTo(saleId);
        List<PmsNonSystemSaleItem> items = nonSystemSaleItemMapper.selectByExample(example);
        
        for (PmsNonSystemSaleItem item : items) {
            // 恢复门店库存
            if (item.getStoreStockDeducted() != null && item.getStoreStockDeducted() > 0) {
                PmsStoreSkuStock storeSkuStock = storeSkuStockExtMapper.selectByStoreIdAndSkuId(item.getStoreId(), item.getSkuId());
                if (storeSkuStock != null) {
                    storeSkuStock.setStock(storeSkuStock.getStock() + item.getStoreStockDeducted());
                    storeSkuStockMapper.updateByPrimaryKeySelective(storeSkuStock);
                    LOGGER.info("恢复门店库存（驳回销售单）：storeId={}, skuId={}, skuCode={}, quantity={}", 
                        item.getStoreId(), item.getSkuId(), item.getSkuCode(), item.getStoreStockDeducted());
                }
            }
            
            // 恢复总库存
            PmsSkuStock skuStock = skuStockMapper.selectByPrimaryKey(item.getSkuId());
            Integer beforeStock = skuStock.getStock();
            Integer newStock = beforeStock + item.getQuantity();
            
            PmsSkuStock updateSku = new PmsSkuStock();
            updateSku.setId(item.getSkuId());
            updateSku.setStock(newStock);
            skuStockMapper.updateByPrimaryKeySelective(updateSku);
            
            // 获取商品信息用于记录日志
            PmsProduct product = productMapper.selectByPrimaryKey(item.getProductId());
            String productSn = product != null ? product.getProductSn() : "";
            
            // 记录库存操作日志（入库）
            String operationReason = "非系统销售驳回入库（" + sale.getSaleTypeName() + "），门店：" + item.getStoreName() + "，原因：" + reason;
            recordStockOperationLog(sale.getSaleNo(), item.getProductId(), item.getProductName(), 
                productSn, item.getSkuId(), item.getSkuCode(), (byte) 1, 
                2, operationReason, item.getQuantity(), 
                beforeStock, newStock, sale.getOperatorId(), sale.getOperatorName(), null, item.getStoreId());
            
            LOGGER.info("恢复SKU库存（驳回销售单）：skuId={}, skuCode={}, quantity={}", 
                item.getSkuId(), item.getSkuCode(), item.getQuantity());
        }
        
        // 更新销售单状态
        PmsNonSystemSale updateSale = new PmsNonSystemSale();
        updateSale.setId(saleId);
        updateSale.setStatus((byte) 3);  // 已驳回
        updateSale.setRemark(updateSale.getRemark() != null ? 
            updateSale.getRemark() + "；驳回原因：" + reason : "驳回原因：" + reason);
        updateSale.setUpdateTime(new Date());
        nonSystemSaleMapper.updateByPrimaryKeySelective(updateSale);
        
        LOGGER.info("驳回销售单成功：saleId={}, saleNo={}, reason={}", saleId, sale.getSaleNo(), reason);
        
        // 发送销售单审核结果通知给申请人
        try {
            if (adminNotificationService != null && sale.getOperatorId() != null) {
                adminNotificationService.notifySaleApprovalResult(
                    sale.getSaleNo(),
                    "驳回",
                    new Date(),
                    sale.getOperatorId()
                );
            }
        } catch (Exception e) {
            LOGGER.error("发送销售单驳回通知失败，saleNo：{}", sale.getSaleNo(), e);
        }
    }
    
    /**
     * 生成销售单号
     */
    private String generateSaleNo() {
        return "SALE" + System.currentTimeMillis();
    }
    
    /**
     * 转换为销售单DTO
     */
    private PmsNonSystemSaleDTO convertToDTO(PmsNonSystemSale sale) {
        PmsNonSystemSaleDTO dto = new PmsNonSystemSaleDTO();
        dto.setId(sale.getId());
        dto.setSaleNo(sale.getSaleNo());
        dto.setSaleTypeId(sale.getSaleTypeId());
        dto.setSaleTypeName(sale.getSaleTypeName());
        dto.setSaleDate(sale.getSaleDate());
        dto.setTotalAmount(sale.getTotalAmount());
        dto.setTotalQuantity(sale.getTotalQuantity());
        dto.setRemark(sale.getRemark());
        dto.setCustomerName(sale.getCustomerName());
        dto.setCustomerPhone(sale.getCustomerPhone());
        dto.setOperatorId(sale.getOperatorId());
        dto.setOperatorName(sale.getOperatorName());
        dto.setStatus(sale.getStatus());
        dto.setStatusName(getStatusName(sale.getStatus()));
        dto.setCreateTime(sale.getCreateTime());
        dto.setUpdateTime(sale.getUpdateTime());
        return dto;
    }
    
    /**
     * 转换销售明细为DTO
     */
    private PmsNonSystemSaleDTO.PmsNonSystemSaleItemDTO convertItemToDTO(PmsNonSystemSaleItem item) {
        PmsNonSystemSaleDTO.PmsNonSystemSaleItemDTO dto = new PmsNonSystemSaleDTO.PmsNonSystemSaleItemDTO();
        dto.setId(item.getId());
        dto.setProductId(item.getProductId());
        dto.setProductName(item.getProductName());
        dto.setSkuId(item.getSkuId());
        dto.setSkuCode(item.getSkuCode());
        dto.setQuantity(item.getQuantity());
        dto.setSystemPrice(item.getSystemPrice());
        dto.setSalePrice(item.getSalePrice());
        dto.setLineAmount(item.getLineAmount());
        dto.setStoreStockDeducted(item.getStoreStockDeducted());
        dto.setTotalStockDeducted(item.getTotalStockDeducted());
        dto.setStockDeductionDetail(item.getStockDeductionDetail());
        dto.setCreateTime(item.getCreateTime());
        return dto;
    }
    
    /**
     * 获取状态名称
     */
    private String getStatusName(Byte status) {
        if (status == null) return "";
        switch (status) {
            case 1:
                return "已提交";
            case 2:
                return "已审核";
            case 3:
                return "已驳回";
            default:
                return "未知";
        }
    }
    
    /**
     * 记录库存操作日志
     * @param operationNo 操作单号
     * @param productId 商品ID
     * @param productName 商品名称
     * @param productSn 商品编码
     * @param skuId SKU ID
     * @param skuCode SKU编码
     * @param operationType 操作类型（3=入库，4=出库）
     * @param operationSubtype 操作子类型
     * @param reason 操作原因
     * @param quantityChange 数量变化
     * @param beforeStock 操作前库存
     * @param afterStock 操作后库存
     * @param operatorId 操作人ID
     * @param operatorName 操作人名称
     * @param operatorIp 操作人IP
     * @param storeId 门店ID
     */
    private void recordStockOperationLog(String operationNo, Long productId, String productName, String productSn,
                                       Long skuId, String skuCode, Byte operationType, Integer operationSubtype,
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
        operationLog.setOperationSubtype(operationSubtype != null ? operationSubtype.byteValue() : null);
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
        
        LOGGER.info("记录库存操作日志：operationNo={}, skuCode={}, operationType={}, quantity={}, beforeStock={}, afterStock={}", 
            operationNo, skuCode, operationType, quantityChange, beforeStock, afterStock);
    }
    
    /**
     * 获取地库（仓库）的门店ID
     */
    private Long getWarehouseStoreId() {
        try {
            Long warehouseId = storeAddressService.getWarehouseId();
            return warehouseId != null ? warehouseId : null;
        } catch (Exception e) {
            LOGGER.error("获取地库ID失败", e);
            return null;
        }
    }
    
    /**
     * 按照前端传来的库存扣减方案执行库存扣减
     */
    private void deductStockByPlan(Long storeId, PmsNonSystemSaleCreateRequest.PmsNonSystemSaleItemRequest itemRequest, 
                                   PmsNonSystemSaleItem saleItem, List<com.macro.mall.dto.PmsStockDeductionPlan> plans) {
        int totalOtherStoresDeducted = 0;
        int storeDeducted = 0;
        
        // ✓ 验证SKU存在性
        PmsSkuStock skuStock = skuStockMapper.selectByPrimaryKey(itemRequest.getSkuId());
        if (skuStock == null) {
            LOGGER.error("SKU不存在，ID：{}", itemRequest.getSkuId());
            throw new RuntimeException("SKU不存在，ID：" + itemRequest.getSkuId());
        }
        
        // ✓ 验证sku_code一致性
        if (itemRequest.getSkuCode() != null && !itemRequest.getSkuCode().equals(skuStock.getSkuCode())) {
            LOGGER.error("SKU编码不匹配，SKU ID：{}，期望编码：{}，实际编码：{}", 
                itemRequest.getSkuId(), skuStock.getSkuCode(), itemRequest.getSkuCode());
            throw new RuntimeException("SKU编码不匹配，SKU ID：" + itemRequest.getSkuId());
        }
        
        // 查找该SKU对应的扣减方案
        com.macro.mall.dto.PmsStockDeductionPlan plan = plans.stream()
            .filter(p -> p.getSkuId().equals(itemRequest.getSkuId()))
            .findFirst()
            .orElse(null);
        
        if (plan == null || plan.getStoreDeductions() == null || plan.getStoreDeductions().isEmpty()) {
            LOGGER.warn("未找到SKU的库存扣减方案：skuId={}, skuCode={}", itemRequest.getSkuId(), itemRequest.getSkuCode());
            return;
        }
        
        // 按照方案执行库存扣减
        for (com.macro.mall.dto.PmsStockDeductionPlan.StoreDeductionDetail detail : plan.getStoreDeductions()) {
            if (detail.getDeductionQuantity() == null || detail.getDeductionQuantity() <= 0) {
                continue;
            }
            
            PmsStoreSkuStock storeSkuStock = storeSkuStockExtMapper.selectByStoreIdAndSkuId(detail.getStoreId(), itemRequest.getSkuId());
            if (storeSkuStock != null) {
                storeSkuStock.setStock(storeSkuStock.getStock() - detail.getDeductionQuantity());
                storeSkuStockMapper.updateByPrimaryKeySelective(storeSkuStock);
                
                if (detail.getStoreId().equals(storeId)) {
                    storeDeducted += detail.getDeductionQuantity();
                } else {
                    totalOtherStoresDeducted += detail.getDeductionQuantity();
                }
                
                String storeName = detail.getIsWarehouse() != null && detail.getIsWarehouse() ? "地库" : "门店";
                LOGGER.info("按方案扣减{}库存：storeId={}, skuId={}, skuCode={}, quantity={}", 
                    storeName, detail.getStoreId(), itemRequest.getSkuId(), itemRequest.getSkuCode(), detail.getDeductionQuantity());
            }
        }
        
        // 记录库存扣除情况
        saleItem.setStoreStockDeducted(storeDeducted);
        saleItem.setTotalStockDeducted(totalOtherStoresDeducted);
        
        // 保存库存扣减详情为 JSON
        try {
            String deductionDetail = com.alibaba.fastjson.JSON.toJSONString(plan.getStoreDeductions());
            saleItem.setStockDeductionDetail(deductionDetail);
        } catch (Exception e) {
            LOGGER.error("序列化库存扣减详情失败", e);
        }
    }
    
    /**
     * 从多个门店扣减库存
     * 按照 calculateStockDeductionPlan 的方案执行库存扣减
     * 优先从销售门店扣减，不足时按库存从多到少从其他门店调动
     */
    private void deductStockFromMultipleStores(Long storeId, PmsNonSystemSaleCreateRequest.PmsNonSystemSaleItemRequest itemRequest, PmsNonSystemSaleItem saleItem) {
        int remainingQuantity = itemRequest.getQuantity();
        int storeDeducted = 0;
        int totalOtherStoresDeducted = 0;
        List<com.macro.mall.dto.PmsStockDeductionPlan.StoreDeductionDetail> storeDeductions = new ArrayList<>();
        
        // ✓ 验证SKU存在性
        PmsSkuStock skuStock = skuStockMapper.selectByPrimaryKey(itemRequest.getSkuId());
        if (skuStock == null) {
            LOGGER.error("SKU不存在，ID：{}", itemRequest.getSkuId());
            throw new RuntimeException("SKU不存在，ID：" + itemRequest.getSkuId());
        }
        
        // ✓ 验证sku_code一致性
        if (itemRequest.getSkuCode() != null && !itemRequest.getSkuCode().equals(skuStock.getSkuCode())) {
            LOGGER.error("SKU编码不匹配，SKU ID：{}，期望编码：{}，实际编码：{}", 
                itemRequest.getSkuId(), skuStock.getSkuCode(), itemRequest.getSkuCode());
            throw new RuntimeException("SKU编码不匹配，SKU ID：" + itemRequest.getSkuId());
        }
        
        // 1. 优先从销售门店扣减
        PmsStoreSkuStock storeSkuStock = storeSkuStockExtMapper.selectByStoreIdAndSkuId(storeId, itemRequest.getSkuId());
        int storeAvailable = storeSkuStock != null ? storeSkuStock.getStock() : 0;
        storeDeducted = Math.min(remainingQuantity, storeAvailable);
        
        // 获取销售门店的名称
        OmsStoreAddress saleStore = storeAddressService.getById(storeId);
        String saleStoreName = saleStore != null ? saleStore.getAddressName() : "销售门店";
        Boolean saleStoreIsWarehouse = saleStore != null && saleStore.getIsWarehouse() != null && saleStore.getIsWarehouse();
        
        com.macro.mall.dto.PmsStockDeductionPlan.StoreDeductionDetail storeDetail = new com.macro.mall.dto.PmsStockDeductionPlan.StoreDeductionDetail();
        storeDetail.setStoreId(storeId);
        storeDetail.setStoreName(saleStoreName);
        storeDetail.setStoreStock(storeAvailable);
        storeDetail.setDeductionQuantity(storeDeducted);
        storeDetail.setIsWarehouse(saleStoreIsWarehouse);
        storeDeductions.add(storeDetail);
        
        if (storeDeducted > 0 && storeSkuStock != null) {
            storeSkuStock.setStock(storeSkuStock.getStock() - storeDeducted);
            storeSkuStockMapper.updateByPrimaryKeySelective(storeSkuStock);
            LOGGER.info("扣减销售门店库存：storeId={}, skuId={}, skuCode={}, quantity={}", 
                storeId, itemRequest.getSkuId(), itemRequest.getSkuCode(), storeDeducted);
        }
        
        remainingQuantity -= storeDeducted;
        
        // 2. 如果销售门店库存不足，从其他门店调动库存（按库存从多到少排序）
        if (remainingQuantity > 0) {
            List<PmsStoreSkuStock> allStoresStock = storeSkuStockExtMapper.selectBySkuId(itemRequest.getSkuId());
            
            if (allStoresStock != null && !allStoresStock.isEmpty()) {
                // 按库存从多到少排序
                allStoresStock.sort((a, b) -> {
                    int stockA = a.getStock() != null ? a.getStock() : 0;
                    int stockB = b.getStock() != null ? b.getStock() : 0;
                    return Integer.compare(stockB, stockA);
                });
                
                for (PmsStoreSkuStock otherStore : allStoresStock) {
                    if (remainingQuantity <= 0) break;
                    
                    // 跳过销售门店本身
                    if (otherStore.getStoreId().equals(storeId)) {
                        continue;
                    }
                    
                    int otherAvailable = otherStore.getStock() != null ? otherStore.getStock() : 0;
                    int otherDeducted = Math.min(remainingQuantity, otherAvailable);
                    
                    if (otherDeducted > 0) {
                        otherStore.setStock(otherAvailable - otherDeducted);
                        storeSkuStockMapper.updateByPrimaryKeySelective(otherStore);
                        totalOtherStoresDeducted += otherDeducted;
                        remainingQuantity -= otherDeducted;
                        
                        // 获取其他门店的名称
                        OmsStoreAddress otherStoreAddress = storeAddressService.getById(otherStore.getStoreId());
                        String otherStoreName = otherStoreAddress != null ? otherStoreAddress.getAddressName() : "门店";
                        Boolean otherStoreIsWarehouse = otherStoreAddress != null && otherStoreAddress.getIsWarehouse() != null && otherStoreAddress.getIsWarehouse();
                        
                        com.macro.mall.dto.PmsStockDeductionPlan.StoreDeductionDetail otherDetail = new com.macro.mall.dto.PmsStockDeductionPlan.StoreDeductionDetail();
                        otherDetail.setStoreId(otherStore.getStoreId());
                        otherDetail.setStoreName(otherStoreName);
                        otherDetail.setStoreStock(otherAvailable);
                        otherDetail.setDeductionQuantity(otherDeducted);
                        otherDetail.setIsWarehouse(otherStoreIsWarehouse);
                        storeDeductions.add(otherDetail);
                        
                        LOGGER.info("从{}调动库存：storeId={}, skuId={}, skuCode={}, quantity={}", 
                            otherStoreIsWarehouse ? "地库" : "门店", otherStore.getStoreId(), itemRequest.getSkuId(), itemRequest.getSkuCode(), otherDeducted);
                    }
                }
            }
        }
        
        // 如果仍然不足，记录警告
        if (remainingQuantity > 0) {
            LOGGER.warn("库存严重不足，无法完全满足销售需求：skuCode={}, remainingQuantity={}", 
                itemRequest.getSkuCode(), remainingQuantity);
        }
        
        // 记录库存扣除情况
        saleItem.setStoreStockDeducted(storeDeducted);
        saleItem.setTotalStockDeducted(totalOtherStoresDeducted);
        
        // 保存库存扣减详情为 JSON
        try {
            String deductionDetail = JSON.toJSONString(storeDeductions);
            saleItem.setStockDeductionDetail(deductionDetail);
        } catch (Exception e) {
            LOGGER.error("序列化库存扣减详情失败", e);
        }
    }
    
    /**
     * 计算库存扣减方案
     * 当销售门店库存不足时，从其他门店调动库存
     */
    @Override
    public List<com.macro.mall.dto.PmsStockDeductionPlan> calculateStockDeductionPlan(Long schoolId, Long storeId, List<PmsNonSystemSaleCreateRequest.PmsNonSystemSaleItemRequest> items) {
        List<com.macro.mall.dto.PmsStockDeductionPlan> plans = new ArrayList<>();
        
        for (PmsNonSystemSaleCreateRequest.PmsNonSystemSaleItemRequest item : items) {
            // ✓ 验证SKU存在性
            PmsSkuStock skuStock = skuStockMapper.selectByPrimaryKey(item.getSkuId());
            if (skuStock == null) {
                LOGGER.error("SKU不存在，ID：{}", item.getSkuId());
                throw new RuntimeException("SKU不存在，ID：" + item.getSkuId());
            }
            
            // ✓ 验证sku_code一致性
            if (item.getSkuCode() != null && !item.getSkuCode().equals(skuStock.getSkuCode())) {
                LOGGER.error("SKU编码不匹配，SKU ID：{}，期望编码：{}，实际编码：{}", 
                    item.getSkuId(), skuStock.getSkuCode(), item.getSkuCode());
                throw new RuntimeException("SKU编码不匹配，SKU ID：" + item.getSkuId());
            }
            
            com.macro.mall.dto.PmsStockDeductionPlan plan = new com.macro.mall.dto.PmsStockDeductionPlan();
            plan.setSkuId(item.getSkuId());
            plan.setSkuCode(item.getSkuCode());
            plan.setProductName(item.getProductName());
            plan.setTotalQuantity(item.getQuantity());
            
            List<com.macro.mall.dto.PmsStockDeductionPlan.StoreDeductionDetail> storeDeductions = new ArrayList<>();
            int remainingQuantity = item.getQuantity();
            
            // 1. 从销售门店扣减
            PmsStoreSkuStock storeSkuStock = storeSkuStockExtMapper.selectByStoreIdAndSkuId(storeId, item.getSkuId());
            int storeAvailable = storeSkuStock != null ? storeSkuStock.getStock() : 0;
            int storeDeducted = Math.min(remainingQuantity, storeAvailable);
            
            // 获取销售门店的名称
            OmsStoreAddress saleStore = storeAddressService.getById(storeId);
            String saleStoreName = saleStore != null ? saleStore.getAddressName() : "销售门店";
            Boolean saleStoreIsWarehouse = saleStore != null && saleStore.getIsWarehouse() != null && saleStore.getIsWarehouse();
            
            com.macro.mall.dto.PmsStockDeductionPlan.StoreDeductionDetail storeDetail = new com.macro.mall.dto.PmsStockDeductionPlan.StoreDeductionDetail();
            storeDetail.setStoreId(storeId);
            storeDetail.setStoreName(saleStoreName);
            storeDetail.setStoreStock(storeAvailable);
            storeDetail.setDeductionQuantity(storeDeducted);
            storeDetail.setIsWarehouse(saleStoreIsWarehouse);
            storeDeductions.add(storeDetail);
            
            remainingQuantity -= storeDeducted;
            
            // 2. 如果销售门店库存不足，从其他门店调动库存
            if (remainingQuantity > 0) {
                List<PmsStoreSkuStock> allStoresStock = storeSkuStockExtMapper.selectBySkuId(item.getSkuId());
                
                if (allStoresStock != null && !allStoresStock.isEmpty()) {
                    // 按库存从多到少排序，优先从库存多的门店调动
                    allStoresStock.sort((a, b) -> {
                        int stockA = a.getStock() != null ? a.getStock() : 0;
                        int stockB = b.getStock() != null ? b.getStock() : 0;
                        return Integer.compare(stockB, stockA);
                    });
                    
                    for (PmsStoreSkuStock otherStore : allStoresStock) {
                        if (remainingQuantity <= 0) break;
                        
                        // 跳过销售门店本身
                        if (otherStore.getStoreId().equals(storeId)) {
                            continue;
                        }
                        
                        int otherAvailable = otherStore.getStock() != null ? otherStore.getStock() : 0;
                        int otherDeducted = Math.min(remainingQuantity, otherAvailable);
                        
                        if (otherDeducted > 0) {
                            // 获取其他门店的名称
                            OmsStoreAddress otherStoreAddress = storeAddressService.getById(otherStore.getStoreId());
                            String otherStoreName = otherStoreAddress != null ? otherStoreAddress.getAddressName() : "门店";
                            Boolean otherStoreIsWarehouse = otherStoreAddress != null && otherStoreAddress.getIsWarehouse() != null && otherStoreAddress.getIsWarehouse();
                            
                            com.macro.mall.dto.PmsStockDeductionPlan.StoreDeductionDetail otherDetail = new com.macro.mall.dto.PmsStockDeductionPlan.StoreDeductionDetail();
                            otherDetail.setStoreId(otherStore.getStoreId());
                            otherDetail.setStoreName(otherStoreName);
                            otherDetail.setStoreStock(otherAvailable);
                            otherDetail.setDeductionQuantity(otherDeducted);
                            otherDetail.setIsWarehouse(otherStoreIsWarehouse);
                            storeDeductions.add(otherDetail);
                            
                            remainingQuantity -= otherDeducted;
                        }
                    }
                }
            }
            
            plan.setStoreDeductions(storeDeductions);
            plans.add(plan);
        }
        
        return plans;
    }
    
    /**
     * 导出销售单为 Excel
     */
    @Override
    public void exportSaleToExcel(Long saleId, HttpServletResponse response) throws Exception {
        // 获取销售单详情
        PmsNonSystemSaleDTO saleDetail = getSaleDetail(saleId);
        if (saleDetail == null) {
            throw new IllegalArgumentException("销售单不存在");
        }
        
        // 创建 Excel 工作簿
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("销售单");
        
        // 设置列宽
        sheet.setColumnWidth(0, 20 * 256);
        sheet.setColumnWidth(1, 20 * 256);
        sheet.setColumnWidth(2, 15 * 256);
        sheet.setColumnWidth(3, 15 * 256);
        sheet.setColumnWidth(4, 15 * 256);
        sheet.setColumnWidth(5, 15 * 256);
        
        // 创建标题行
        Row titleRow = sheet.createRow(0);
        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 14);
        titleStyle.setFont(titleFont);
        
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("销售单详情 - " + saleDetail.getSaleNo());
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 5));
        
        // 基本信息
        int rowNum = 2;
        Row infoRow1 = sheet.createRow(rowNum++);
        infoRow1.createCell(0).setCellValue("销售单号：");
        infoRow1.createCell(1).setCellValue(saleDetail.getSaleNo());
        infoRow1.createCell(2).setCellValue("销售类型：");
        infoRow1.createCell(3).setCellValue(saleDetail.getSaleTypeName());
        
        Row infoRow2 = sheet.createRow(rowNum++);
        infoRow2.createCell(0).setCellValue("销售日期：");
        infoRow2.createCell(1).setCellValue(formatDate(saleDetail.getSaleDate()));
        infoRow2.createCell(2).setCellValue("状态：");
        infoRow2.createCell(3).setCellValue(saleDetail.getStatusName());
        
        Row infoRow3 = sheet.createRow(rowNum++);
        infoRow3.createCell(0).setCellValue("操作人：");
        infoRow3.createCell(1).setCellValue(saleDetail.getOperatorName());
        infoRow3.createCell(2).setCellValue("创建时间：");
        infoRow3.createCell(3).setCellValue(formatDateTime(saleDetail.getCreateTime()));
        
        // 商品明细表头
        rowNum += 2;
        Row headerRow = sheet.createRow(rowNum++);
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        String[] headers = {"商品名称", "SKU编码", "销售数量", "系统价格", "销售价格", "行金额"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // 商品数据
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setAlignment(HorizontalAlignment.CENTER);
        
        if (saleDetail.getItems() != null && !saleDetail.getItems().isEmpty()) {
            for (PmsNonSystemSaleDTO.PmsNonSystemSaleItemDTO item : saleDetail.getItems()) {
                Row dataRow = sheet.createRow(rowNum++);
                dataRow.createCell(0).setCellValue(item.getProductName());
                dataRow.createCell(1).setCellValue(item.getSkuCode());
                dataRow.createCell(2).setCellValue(item.getQuantity());
                dataRow.createCell(3).setCellValue(item.getSystemPrice() != null ? item.getSystemPrice().doubleValue() : 0);
                dataRow.createCell(4).setCellValue(item.getSalePrice() != null ? item.getSalePrice().doubleValue() : 0);
                dataRow.createCell(5).setCellValue(item.getLineAmount() != null ? item.getLineAmount().doubleValue() : 0);
            }
        }
        
        // 合计行
        rowNum += 1;
        Row totalRow = sheet.createRow(rowNum);
        CellStyle totalStyle = workbook.createCellStyle();
        totalStyle.setAlignment(HorizontalAlignment.RIGHT);
        Font totalFont = workbook.createFont();
        totalFont.setBold(true);
        totalStyle.setFont(totalFont);
        
        Cell totalLabelCell = totalRow.createCell(4);
        totalLabelCell.setCellValue("合计：");
        totalLabelCell.setCellStyle(totalStyle);
        
        Cell totalAmountCell = totalRow.createCell(5);
        totalAmountCell.setCellValue(saleDetail.getTotalAmount() != null ? saleDetail.getTotalAmount().doubleValue() : 0);
        totalAmountCell.setCellStyle(totalStyle);
        
        // 库存扣减详情
        if (saleDetail.getItems() != null && !saleDetail.getItems().isEmpty()) {
            rowNum += 3;
            Row deductionTitleRow = sheet.createRow(rowNum++);
            CellStyle deductionTitleStyle = workbook.createCellStyle();
            deductionTitleStyle.setAlignment(HorizontalAlignment.LEFT);
            Font deductionTitleFont = workbook.createFont();
            deductionTitleFont.setBold(true);
            deductionTitleFont.setFontHeightInPoints((short) 12);
            deductionTitleStyle.setFont(deductionTitleFont);
            
            Cell deductionTitleCell = deductionTitleRow.createCell(0);
            deductionTitleCell.setCellValue("库存扣减详情");
            deductionTitleCell.setCellStyle(deductionTitleStyle);
            
            // 库存扣减详情表头
            rowNum += 1;
            Row deductionHeaderRow = sheet.createRow(rowNum++);
            String[] deductionHeaders = {"商品名称", "SKU编码", "销售数量", "门店名称", "库存", "调动数量", "调动后库存"};
            for (int i = 0; i < deductionHeaders.length; i++) {
                Cell cell = deductionHeaderRow.createCell(i);
                cell.setCellValue(deductionHeaders[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // 库存扣减详情数据
            for (PmsNonSystemSaleDTO.PmsNonSystemSaleItemDTO item : saleDetail.getItems()) {
                if (item.getStockDeductionDetail() != null && !item.getStockDeductionDetail().isEmpty()) {
                    try {
                        java.util.List<?> deductionList = JSON.parseArray(item.getStockDeductionDetail());
                        boolean isFirstRow = true;
                        for (Object obj : deductionList) {
                            java.util.Map<String, Object> deduction = (java.util.Map<String, Object>) obj;
                            Row deductionRow = sheet.createRow(rowNum++);
                            
                            if (isFirstRow) {
                                deductionRow.createCell(0).setCellValue(item.getProductName());
                                deductionRow.createCell(1).setCellValue(item.getSkuCode());
                                deductionRow.createCell(2).setCellValue(item.getQuantity());
                                isFirstRow = false;
                            }
                            
                            String storeName = (String) deduction.get("storeName");
                            Boolean isWarehouse = (Boolean) deduction.get("isWarehouse");
                            if (isWarehouse != null && isWarehouse) {
                                storeName = "[地库] " + storeName;
                            }
                            
                            deductionRow.createCell(3).setCellValue(storeName);
                            deductionRow.createCell(4).setCellValue(((Number) deduction.get("storeStock")).intValue());
                            deductionRow.createCell(5).setCellValue(((Number) deduction.get("deductionQuantity")).intValue());
                            
                            int storeStock = ((Number) deduction.get("storeStock")).intValue();
                            int deductionQuantity = ((Number) deduction.get("deductionQuantity")).intValue();
                            deductionRow.createCell(6).setCellValue(storeStock - deductionQuantity);
                        }
                    } catch (Exception e) {
                        LOGGER.warn("解析库存扣减详情失败：{}", e.getMessage());
                    }
                }
            }
        }
        
        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"销售单_" + saleDetail.getSaleNo() + ".xlsx\"");
        
        // 写入响应
        OutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
        workbook.close();
        
        LOGGER.info("导出销售单成功：saleId={}, saleNo={}", saleId, saleDetail.getSaleNo());
    }
    
    /**
     * 格式化日期为 yyyy-MM-dd 格式
     */
    private String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }
    
    /**
     * 格式化日期时间为 yyyy-MM-dd HH:mm:ss 格式
     */
    private String formatDateTime(Date date) {
        if (date == null) {
            return "";
        }
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }
    
    /**
     * 生成销售单分享信息（小程序短链接和小程序码）
     */
    @Override
    public PmsNonSystemSaleShareResult generateShareInfo(Long saleId) {
        // 验证销售单是否存在
        PmsNonSystemSale sale = nonSystemSaleMapper.selectByPrimaryKey(saleId);
        if (sale == null) {
            throw new RuntimeException("销售单不存在");
        }
        
        PmsNonSystemSaleShareResult result = new PmsNonSystemSaleShareResult();
        result.setSaleId(saleId);
        result.setSaleNo(sale.getSaleNo());
        
        // 生成分享标题和描述
        String shareTitle = "销售单 - " + sale.getSaleNo();
        String shareDescription = String.format("销售类型：%s，总金额：￥%.2f", 
            sale.getSaleTypeName(), sale.getTotalAmount());
        
        result.setShareTitle(shareTitle);
        result.setShareDescription(shareDescription);
        
        // 检查微信小程序配置是否可用
        if (wechatMiniProgramService.isConfigAvailable()) {
            // 生成小程序短链接
            // 注意：分包页面路径格式为 subpackages/sale/saleDetail
            String path = "subpackages/sale/saleDetail";
            String query = "saleId=" + saleId;
            String urlLink = wechatMiniProgramService.generateUrlLink(path, query);
            
            if (urlLink != null) {
                result.setShareLink(urlLink);
            } else {
                result.setShareLink("小程序短链接生成失败，请检查微信小程序配置");
            }
            
            // 生成小程序码（base64格式）
            // scene参数限制32字符，直接使用销售单ID
            String scene = saleId.toString();
            String page = "subpackages/sale/saleDetail";
            String miniProgramCodeBase64 = wechatMiniProgramService.generateMiniProgramCode(scene, page, 430);
            
            if (miniProgramCodeBase64 != null) {
                result.setMiniProgramCodeBase64(miniProgramCodeBase64);
                result.setMiniProgramCodeUrl("小程序码已生成（base64格式）");
            } else {
                result.setMiniProgramCodeBase64(null);
                result.setMiniProgramCodeUrl("小程序码生成失败，请检查微信小程序配置");
            }
        } else {
            // 微信配置不可用时的降级方案
            result.setShareLink("请配置微信小程序AppID和Secret后重试");
            result.setMiniProgramCodeUrl("请配置微信小程序AppID和Secret后重试");
            result.setMiniProgramCodeBase64(null);
        }
        
        // 设置默认分享图片
        result.setShareImageUrl("/static/images/sale-share-default.png");
        
        return result;
    }
}
