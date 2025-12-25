package com.macro.mall.service.impl;

import com.macro.mall.dto.PmsStockOperationParam;
import com.macro.mall.dto.PmsStockOperationLogQueryParam;
import com.macro.mall.mapper.PmsStockOperationLogMapper;
import com.macro.mall.mapper.PmsSkuStockMapper;
import com.macro.mall.mapper.PmsProductMapper;
import com.macro.mall.mapper.PmsStoreSkuStockMapper;
import com.macro.mall.mapper.OmsStoreAddressMapper;
import com.macro.mall.model.PmsStockOperationLog;
import com.macro.mall.model.PmsStockOperationLogExample;
import com.macro.mall.model.PmsSkuStock;
import com.macro.mall.model.PmsSkuStockExample;
import com.macro.mall.model.PmsProduct;
import com.macro.mall.model.PmsStoreSkuStock;
import com.macro.mall.model.PmsStoreSkuStockExample;
import com.macro.mall.model.OmsStoreAddress;
import com.macro.mall.service.PmsStockService;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 库存管理Service实现类
 * Created by macro on 2024-01-20.
 */
@Service
public class PmsStockServiceImpl implements PmsStockService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PmsStockServiceImpl.class);
    
    @Autowired
    private PmsStockOperationLogMapper stockOperationLogMapper;
    
    @Autowired
    private PmsSkuStockMapper skuStockMapper;
    
    @Autowired
    private PmsProductMapper productMapper;
    
    @Autowired
    private PmsStoreSkuStockMapper storeSkuStockMapper;
    
    @Autowired
    private OmsStoreAddressMapper storeAddressMapper;
    
    @Override
    @Transactional
    public int executeStockOperation(PmsStockOperationParam param) {
        LOGGER.info("开始执行库存操作，操作类型：{}", param.getOperationType());
        
        // 1. 生成操作单号
        String operationNo = generateOperationNo(param.getOperationType());
        
        // 2. 获取当前操作人信息
        String operatorName = getCurrentOperatorName();
        Long operatorId = getCurrentOperatorId();
        String operatorIp = getCurrentOperatorIp();
        
        // 3. 获取商品信息
        PmsProduct product = productMapper.selectByPrimaryKey(param.getProductId());
        if (product == null) {
            throw new RuntimeException("商品不存在，ID：" + param.getProductId());
        }
        String productName = product.getName();
        String productSn = product.getProductSn();
        
        int totalOperations = 0;
        
        // 4. 根据操作类型执行不同的处理逻辑
        if ("transfer".equals(param.getOperationType())) {
            // 调货操作
            totalOperations = executeTransferOperation(param, operationNo, operatorName, operatorId, operatorIp, productName, productSn);
        } else {
            // 普通库存操作（入库、出库、调整）
            totalOperations = executeNormalStockOperation(param, operationNo, operatorName, operatorId, operatorIp, productName, productSn);
        }
        
        LOGGER.info("库存操作完成，操作单号：{}，成功操作SKU数量：{}", operationNo, totalOperations);
        return totalOperations;
    }
    
    @Override
    public List<PmsStockOperationLog> getStockOperationLogs(PmsStockOperationLogQueryParam queryParam) {
        LOGGER.info("查询库存操作记录，参数：{}", queryParam);
        
        PmsStockOperationLogExample example = new PmsStockOperationLogExample();
        PmsStockOperationLogExample.Criteria criteria = example.createCriteria();
        
        // 构建查询条件
        if (queryParam.getProductId() != null) {
            criteria.andProductIdEqualTo(queryParam.getProductId());
        }
        if (queryParam.getOperationType() != null && !queryParam.getOperationType().trim().isEmpty()) {
            criteria.andOperationTypeEqualTo(convertOperationType(queryParam.getOperationType()));
        }
        if (queryParam.getOperatorId() != null) {
            criteria.andOperatorIdEqualTo(queryParam.getOperatorId());
        }
        if (queryParam.getOperatorName() != null && !queryParam.getOperatorName().trim().isEmpty()) {
            criteria.andOperatorNameLike("%" + queryParam.getOperatorName() + "%");
        }
        // 门店筛选
        if (queryParam.getStoreId() != null) {
            criteria.andStoreIdEqualTo(queryParam.getStoreId());
        }
        
        // 关键字搜索（匹配商品名称或SKU编码）
        if (queryParam.getKeyword() != null && !queryParam.getKeyword().trim().isEmpty()) {
            String keyword = "%" + queryParam.getKeyword().trim() + "%";
            // 使用 OR 条件匹配 productName 或 skuCode
            PmsStockOperationLogExample.Criteria keywordCriteria1 = example.createCriteria();
            PmsStockOperationLogExample.Criteria keywordCriteria2 = example.createCriteria();
            
            // 复制其他条件到两个 criteria
            if (queryParam.getProductId() != null) {
                keywordCriteria1.andProductIdEqualTo(queryParam.getProductId());
                keywordCriteria2.andProductIdEqualTo(queryParam.getProductId());
            }
            if (queryParam.getOperationType() != null && !queryParam.getOperationType().trim().isEmpty()) {
                keywordCriteria1.andOperationTypeEqualTo(convertOperationType(queryParam.getOperationType()));
                keywordCriteria2.andOperationTypeEqualTo(convertOperationType(queryParam.getOperationType()));
            }
            if (queryParam.getStoreId() != null) {
                keywordCriteria1.andStoreIdEqualTo(queryParam.getStoreId());
                keywordCriteria2.andStoreIdEqualTo(queryParam.getStoreId());
            }
            
            // 添加关键字条件
            keywordCriteria1.andProductNameLike(keyword);
            keywordCriteria2.andSkuCodeLike(keyword);
            
            // 清空原有 criteria，使用新的 OR 条件
            example.clear();
            example.or(keywordCriteria1);
            example.or(keywordCriteria2);
            
            // 时间范围需要在两个 criteria 中都添加
            if (queryParam.getStartTime() != null && !queryParam.getStartTime().trim().isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date startTime = sdf.parse(queryParam.getStartTime());
                    keywordCriteria1.andCreatedAtGreaterThanOrEqualTo(startTime);
                    keywordCriteria2.andCreatedAtGreaterThanOrEqualTo(startTime);
                } catch (Exception e) {
                    LOGGER.warn("开始时间格式错误：{}", queryParam.getStartTime());
                }
            }
            if (queryParam.getEndTime() != null && !queryParam.getEndTime().trim().isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date endTime = sdf.parse(queryParam.getEndTime());
                    keywordCriteria1.andCreatedAtLessThanOrEqualTo(endTime);
                    keywordCriteria2.andCreatedAtLessThanOrEqualTo(endTime);
                } catch (Exception e) {
                    LOGGER.warn("结束时间格式错误：{}", queryParam.getEndTime());
                }
            }
        } else {
            // 没有关键字时，正常添加时间范围条件
            if (queryParam.getStartTime() != null && !queryParam.getStartTime().trim().isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date startTime = sdf.parse(queryParam.getStartTime());
                    criteria.andCreatedAtGreaterThanOrEqualTo(startTime);
                } catch (Exception e) {
                    LOGGER.warn("开始时间格式错误：{}", queryParam.getStartTime());
                }
            }
            if (queryParam.getEndTime() != null && !queryParam.getEndTime().trim().isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date endTime = sdf.parse(queryParam.getEndTime());
                    criteria.andCreatedAtLessThanOrEqualTo(endTime);
                } catch (Exception e) {
                    LOGGER.warn("结束时间格式错误：{}", queryParam.getEndTime());
                }
            }
        }
        
        // 按创建时间倒序排序
        example.setOrderByClause("created_at desc");
        
        // 分页处理
        if (queryParam.getPageNum() != null && queryParam.getPageSize() != null) {
            PageHelper.startPage(queryParam.getPageNum(), queryParam.getPageSize());
        }
        
        List<PmsStockOperationLog> logs = stockOperationLogMapper.selectByExample(example);
        LOGGER.info("查询到库存操作记录数量：{}", logs.size());
        
        return logs;
    }
    
    @Override
    public boolean checkStockAvailable(Long productId, Long skuId, Integer quantity) {
        if (skuId == null || quantity == null || quantity <= 0) {
            return false;
        }
        
        PmsSkuStock skuStock = skuStockMapper.selectByPrimaryKey(skuId);
        if (skuStock == null) {
            return false;
        }
        
        return skuStock.getStock() >= quantity;
    }
    
    @Override
    public boolean validateStockAvailability(Long storeId, Long skuId, Integer quantity) {
        if (storeId == null || skuId == null || quantity == null || quantity <= 0) {
            return false;
        }
        
        // 获取门店（或地库）的库存
        PmsStoreSkuStock storeStock = getStoreSkuStock(storeId, skuId);
        if (storeStock == null) {
            return false;
        }
        
        return storeStock.getStock() >= quantity;
    }
    
    @Override
    public String generateOperationNo(String operationType) {
        String prefix = getOperationPrefix(operationType);
        String dateStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String timeStr = new SimpleDateFormat("HHmmss").format(new Date());
        return prefix + dateStr + timeStr;
    }
    
    /**
     * 获取操作类型前缀
     */
    private String getOperationPrefix(String operationType) {
        switch (operationType) {
            case "in":
                return "IN";
            case "out":
                return "OUT";
            case "adjust":
                return "ADJ";
            case "transfer":
                return "TRF";
            default:
                return "OPT";
        }
    }
    
    /**
     * 验证库存操作
     */
    private void validateStockOperation(String operationType, PmsSkuStock currentSku, PmsStockOperationParam.PmsStockOperationItem item) {
        if ("out".equals(operationType)) {
            // 出库操作需要验证库存是否充足
            if (currentSku.getStock() < item.getOperationQuantity()) {
                throw new RuntimeException("库存不足，当前库存：" + currentSku.getStock() + "，需要出库：" + item.getOperationQuantity());
            }
        }
        
        if (item.getOperationQuantity() <= 0) {
            throw new RuntimeException("操作数量必须大于0");
        }
    }
    
    /**
     * 计算新库存
     */
    private Integer calculateNewStock(String operationType, Integer currentStock, Integer operationQuantity) {
        switch (operationType) {
            case "in":
                return currentStock + operationQuantity;
            case "out":
                return currentStock - operationQuantity;
            case "adjust":
                return operationQuantity; // 调整直接设为新值
            default:
                return currentStock;
        }
    }
    
    /**
     * 计算数量变化（用于日志记录）
     */
    private Integer calculateQuantityChange(String operationType, Integer operationQuantity) {
        switch (operationType) {
            case "in":
                return operationQuantity;
            case "out":
                return -operationQuantity;
            case "adjust":
                return operationQuantity;
            default:
                return 0;
        }
    }
    
    /**
     * 获取当前操作人姓名
     */
    private String getCurrentOperatorName() {
        try {
            // TODO: 从当前登录用户获取真实姓名
            return "系统管理员";
        } catch (Exception e) {
            LOGGER.warn("获取操作人姓名失败", e);
            return "未知用户";
        }
    }
    
    /**
     * 获取当前操作人ID
     */
    private Long getCurrentOperatorId() {
        try {
            // TODO: 从当前登录用户获取用户ID
            return 1L;
        } catch (Exception e) {
            LOGGER.warn("获取操作人ID失败", e);
            return 0L;
        }
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
     * 转换操作类型（字符串 -> 数字）
     */
    private Byte convertOperationType(String operationType) {
        switch (operationType) {
            case "in":
                return 1;
            case "out":
                return 2;
            case "adjust":
                return 3;
            case "transfer":
                return 4;
            default:
                return 0;
        }
    }

    /**
     * 执行调货操作
     */
    private int executeTransferOperation(PmsStockOperationParam param, String operationNo, 
                                       String operatorName, Long operatorId, String operatorIp,
                                       String productName, String productSn) {
        if (param.getFromStoreId() == null || param.getToStoreId() == null) {
            throw new RuntimeException("调货操作必须指定源门店和目标门店");
        }
        
        if (param.getFromStoreId().equals(param.getToStoreId())) {
            throw new RuntimeException("源门店和目标门店不能相同");
        }
        
        // 验证门店是否存在
        OmsStoreAddress fromStore = storeAddressMapper.selectByPrimaryKey(param.getFromStoreId());
        OmsStoreAddress toStore = storeAddressMapper.selectByPrimaryKey(param.getToStoreId());
        if (fromStore == null || toStore == null) {
            throw new RuntimeException("门店不存在");
        }
        
        int totalOperations = 0;
        
        for (PmsStockOperationParam.PmsStockOperationItem item : param.getItems()) {
            try {
                // 获取源门店库存
                PmsStoreSkuStock fromStoreStock = null;
                if (item.getStoreSkuStockId() != null) {
                    fromStoreStock = storeSkuStockMapper.selectByPrimaryKey(item.getStoreSkuStockId());
                } else {
                    fromStoreStock = getStoreSkuStock(param.getFromStoreId(), item.getSkuId());
                }
                
                if (fromStoreStock == null) {
                    throw new RuntimeException("源门店SKU库存不存在，门店ID：" + param.getFromStoreId() + "，SKU ID：" + item.getSkuId());
                }
                
                // 验证源门店库存是否充足
                if (fromStoreStock.getStock() < item.getOperationQuantity()) {
                    throw new RuntimeException("源门店库存不足，当前库存：" + fromStoreStock.getStock() + "，需要调出：" + item.getOperationQuantity());
                }
                
                // 获取或创建目标门店库存
                PmsStoreSkuStock toStoreStock = getStoreSkuStock(param.getToStoreId(), fromStoreStock.getSkuId());
                if (toStoreStock == null) {
                    toStoreStock = createStoreSkuStock(param.getToStoreId(), fromStoreStock.getSkuId(), param.getProductId(), item.getSkuCode());
                }
                
                // 更新源门店库存（减少）
                PmsStoreSkuStock updateFromStore = new PmsStoreSkuStock();
                updateFromStore.setId(fromStoreStock.getId());
                updateFromStore.setStock(fromStoreStock.getStock() - item.getOperationQuantity());
                updateFromStore.setUpdateTime(new Date());
                storeSkuStockMapper.updateByPrimaryKeySelective(updateFromStore);
                
                // 更新目标门店库存（增加）
                PmsStoreSkuStock updateToStore = new PmsStoreSkuStock();
                updateToStore.setId(toStoreStock.getId());
                updateToStore.setStock(toStoreStock.getStock() + item.getOperationQuantity());
                updateToStore.setUpdateTime(new Date());
                storeSkuStockMapper.updateByPrimaryKeySelective(updateToStore);
                
                // 记录源门店出库日志
                recordStockOperationLog(operationNo, param.getProductId(), productName, productSn,
                        fromStoreStock.getSkuId(), item.getSkuCode(), (byte) 4, param.getOperationSubtype(),
                        param.getReason() + "（调出到门店：" + toStore.getAddressName() + "）",
                        -item.getOperationQuantity(), fromStoreStock.getStock(),
                        fromStoreStock.getStock() - item.getOperationQuantity(),
                        operatorId, operatorName, operatorIp, param.getFromStoreId());
                
                // 记录目标门店入库日志
                recordStockOperationLog(operationNo, param.getProductId(), productName, productSn,
                        toStoreStock.getSkuId(), item.getSkuCode(), (byte) 4, param.getOperationSubtype(),
                        param.getReason() + "（从门店调入：" + fromStore.getAddressName() + "）",
                        item.getOperationQuantity(), toStoreStock.getStock(),
                        toStoreStock.getStock() + item.getOperationQuantity(),
                        operatorId, operatorName, operatorIp, param.getToStoreId());
                
                totalOperations++;
                
                LOGGER.info("调货操作成功：SKU {} 从门店 {} 调出 {} 到门店 {}", 
                    item.getSkuCode(), fromStore.getAddressName(), item.getOperationQuantity(), toStore.getAddressName());
                
            } catch (Exception e) {
                LOGGER.error("调货操作失败，SKU ID：{}，错误：{}", item.getSkuId(), e.getMessage());
                throw new RuntimeException("调货操作失败：" + e.getMessage(), e);
            }
        }
        
        return totalOperations;
    }
    
    /**
     * 执行普通库存操作（入库、出库、调整）
     */
    private int executeNormalStockOperation(PmsStockOperationParam param, String operationNo,
                                          String operatorName, Long operatorId, String operatorIp,
                                          String productName, String productSn) {
        int totalOperations = 0;
        
        // 遍历处理每个SKU的库存操作
        for (PmsStockOperationParam.PmsStockOperationItem item : param.getItems()) {
            try {
                // 如果指定了门店，则操作门店库存
                if (item.getStoreId() != null) {
                    totalOperations += executeStoreStockOperation(param, item, operationNo, operatorName, operatorId, operatorIp, productName, productSn);
                } else {
                    // 操作总库存，需要验证SKU是否存在
                    PmsSkuStock currentSku = skuStockMapper.selectByPrimaryKey(item.getSkuId());
                    if (currentSku == null) {
                        throw new RuntimeException("SKU不存在，ID：" + item.getSkuId());
                    }
                    totalOperations += executeTotalStockOperation(param, item, currentSku, operationNo, operatorName, operatorId, operatorIp, productName, productSn);
                }
                
            } catch (Exception e) {
                LOGGER.error("SKU库存操作失败，SKU ID：{}，错误：{}", item.getSkuId(), e.getMessage());
                throw new RuntimeException("SKU库存操作失败：" + e.getMessage(), e);
            }
        }
        
        return totalOperations;
    }
    
    /**
     * 执行门店库存操作
     */
    private int executeStoreStockOperation(PmsStockOperationParam param, PmsStockOperationParam.PmsStockOperationItem item,
                                         String operationNo, String operatorName, Long operatorId, String operatorIp,
                                         String productName, String productSn) {
        // 如果提供了storeSkuStockId，直接使用；否则根据storeId和skuId查询
        PmsStoreSkuStock storeStock = null;
        if (item.getStoreSkuStockId() != null) {
            storeStock = storeSkuStockMapper.selectByPrimaryKey(item.getStoreSkuStockId());
        } else if (item.getStoreId() != null && item.getSkuId() != null) {
            storeStock = getStoreSkuStock(item.getStoreId(), item.getSkuId());
        }
        
        if (storeStock == null) {
            storeStock = createStoreSkuStock(item.getStoreId(), item.getSkuId(), param.getProductId(), item.getSkuCode());
        }
        
        // 验证门店库存操作
        validateStoreStockOperation(param.getOperationType(), storeStock, item);
        
        // 计算新的门店库存
        Integer newStoreStock = calculateNewStock(param.getOperationType(), storeStock.getStock(), item.getOperationQuantity());
        
        // 更新门店库存
        PmsStoreSkuStock updateStoreStock = new PmsStoreSkuStock();
        updateStoreStock.setId(storeStock.getId());
        updateStoreStock.setStock(newStoreStock);
        updateStoreStock.setUpdateTime(new Date());
        storeSkuStockMapper.updateByPrimaryKeySelective(updateStoreStock);
        
        // 同步更新总库存（根据pms_store_sku_stock中的skuId字段）
        updateTotalStockByStoreOperation(storeStock.getSkuId(), param.getOperationType(), item.getOperationQuantity());
        
        // 记录操作日志
        recordStockOperationLog(operationNo, param.getProductId(), productName, productSn,
                storeStock.getSkuId(), item.getSkuCode(), convertOperationType(param.getOperationType()),
                param.getOperationSubtype(), param.getReason(),
                calculateQuantityChange(param.getOperationType(), item.getOperationQuantity()),
                storeStock.getStock(), newStoreStock,
                operatorId, operatorName, operatorIp, storeStock.getStoreId());
        
        LOGGER.info("门店库存操作成功：门店ID {} SKU {} {} -> {}，操作数量：{}", 
            storeStock.getStoreId(), item.getSkuCode(), storeStock.getStock(), newStoreStock, item.getOperationQuantity());
        
        return 1;
    }
    
    /**
     * 执行总库存操作
     */
    private int executeTotalStockOperation(PmsStockOperationParam param, PmsStockOperationParam.PmsStockOperationItem item,
                                         PmsSkuStock currentSku, String operationNo, String operatorName, Long operatorId, String operatorIp,
                                         String productName, String productSn) {
        // 验证库存操作
        validateStockOperation(param.getOperationType(), currentSku, item);
        
        // 计算新库存
        Integer newStock = calculateNewStock(param.getOperationType(), currentSku.getStock(), item.getOperationQuantity());
        
        // 更新SKU库存
        PmsSkuStock updateSku = new PmsSkuStock();
        updateSku.setId(item.getSkuId());
        updateSku.setStock(newStock);
        skuStockMapper.updateByPrimaryKeySelective(updateSku);
        
        // 记录操作日志
        recordStockOperationLog(operationNo, param.getProductId(), productName, productSn,
                item.getSkuId(), item.getSkuCode(), convertOperationType(param.getOperationType()),
                param.getOperationSubtype(), param.getReason(),
                calculateQuantityChange(param.getOperationType(), item.getOperationQuantity()),
                currentSku.getStock(), newStock,
                operatorId, operatorName, operatorIp, null);
        
        LOGGER.info("总库存操作成功：SKU {} {} -> {}，操作数量：{}", 
            item.getSkuCode(), currentSku.getStock(), newStock, item.getOperationQuantity());
        
        return 1;
    }
    
    /**
     * 获取门店SKU库存
     */
    private PmsStoreSkuStock getStoreSkuStock(Long storeId, Long skuId) {
        PmsStoreSkuStockExample example = new PmsStoreSkuStockExample();
        example.createCriteria().andStoreIdEqualTo(storeId).andSkuIdEqualTo(skuId);
        List<PmsStoreSkuStock> list = storeSkuStockMapper.selectByExample(example);
        LOGGER.debug("查询门店SKU库存：storeId={}, skuId={}，查询结果数量：{}", storeId, skuId, list.size());
        if (!list.isEmpty()) {
            LOGGER.debug("查询到门店SKU库存，ID：{}，库存：{}", list.get(0).getId(), list.get(0).getStock());
        }
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
     * 根据门店操作更新总库存
     * @param skuId pms_sku_stock表的id（来自pms_store_sku_stock表的skuId字段）
     * @param operationType 操作类型
     * @param operationQuantity 操作数量
     */
    private void updateTotalStockByStoreOperation(Long skuId, String operationType, Integer operationQuantity) {
        if (skuId == null) {
            LOGGER.warn("skuId为空，无法更新总库存");
            return;
        }
        
        PmsSkuStock currentSku = skuStockMapper.selectByPrimaryKey(skuId);
        if (currentSku == null) {
            LOGGER.warn("SKU不存在，ID：{}", skuId);
            return;
        }
        
        Integer newTotalStock = calculateNewStock(operationType, currentSku.getStock(), operationQuantity);
        
        PmsSkuStock updateSku = new PmsSkuStock();
        updateSku.setId(skuId);
        updateSku.setStock(newTotalStock);
        skuStockMapper.updateByPrimaryKeySelective(updateSku);
        
        LOGGER.info("总库存更新成功：SKU ID {} {} -> {}，操作数量：{}", 
            skuId, currentSku.getStock(), newTotalStock, operationQuantity);
    }
    
    /**
     * 验证门店库存操作
     */
    private void validateStoreStockOperation(String operationType, PmsStoreSkuStock storeStock, PmsStockOperationParam.PmsStockOperationItem item) {
        if ("out".equals(operationType)) {
            if (storeStock.getStock() < item.getOperationQuantity()) {
                throw new RuntimeException("门店库存不足，当前库存：" + storeStock.getStock() + "，需要出库：" + item.getOperationQuantity());
            }
        }
        
        if (item.getOperationQuantity() <= 0) {
            throw new RuntimeException("操作数量必须大于0");
        }
    }
    
    /**
     * 记录库存操作日志
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
    }
} 