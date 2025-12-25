package com.macro.mall.service.impl;

import com.macro.mall.dto.PmsStoreSkuStockParam;
import com.macro.mall.dto.PmsStoreSkuStockExtendDto;
import com.macro.mall.mapper.PmsStoreSkuStockMapper;
import com.macro.mall.mapper.PmsSkuStockMapper;
import com.macro.mall.mapper.OmsStoreAddressMapper;
import com.macro.mall.model.*;
import com.macro.mall.service.PmsStoreSkuStockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * 门店SKU库存管理Service实现类
 * Created by macro on 2024-01-20.
 */
@Service
public class PmsStoreSkuStockServiceImpl implements PmsStoreSkuStockService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PmsStoreSkuStockServiceImpl.class);
    
    @Autowired
    private PmsStoreSkuStockMapper storeSkuStockMapper;
    
    @Autowired
    private PmsSkuStockMapper skuStockMapper;
    
    @Autowired
    private OmsStoreAddressMapper storeAddressMapper;
    
    @Override
    public List<PmsStoreSkuStock> getStoreSkuStockByProductId(Long productId) {
        PmsStoreSkuStockExample example = new PmsStoreSkuStockExample();
        example.createCriteria().andProductIdEqualTo(productId);
        return storeSkuStockMapper.selectByExample(example);
    }
    
    @Override
    public List<PmsStoreSkuStockExtendDto> getStoreSkuStockWithSpecByProductId(Long productId) {
        // 先初始化门店库存记录（如果不存在）
        initStoreSkuStockForProduct(productId);
        
        // 获取基础的门店SKU库存数据
        List<PmsStoreSkuStock> storeSkuStockList = getStoreSkuStockByProductId(productId);
        List<PmsStoreSkuStockExtendDto> result = new ArrayList<>();
        
        if (CollectionUtils.isEmpty(storeSkuStockList)) {
            return result;
        }
        
        // 为每个门店SKU库存记录添加规格信息和总库存
        for (PmsStoreSkuStock storeSkuStock : storeSkuStockList) {
            try {
                // 创建扩展DTO
                PmsStoreSkuStockExtendDto extendDto = PmsStoreSkuStockExtendDto.fromStoreSkuStock(storeSkuStock);
                
                // 查询对应的SKU信息获取规格数据和总库存
                PmsSkuStock skuStock = skuStockMapper.selectByPrimaryKey(storeSkuStock.getSkuId());
                if (skuStock != null) {
                    if (skuStock.getSpData() != null) {
                        extendDto.setSpData(skuStock.getSpData());
                    } else {
                        extendDto.setSpData("暂无规格");
                    }
                    // 设置SKU总库存（从pms_sku_stock表获取，而不是累加门店库存）
                    extendDto.setTotalStock(skuStock.getStock() != null ? skuStock.getStock() : 0);
                } else {
                    extendDto.setSpData("暂无规格");
                    extendDto.setTotalStock(0);
                }
                
                result.add(extendDto);
            } catch (Exception e) {
                LOGGER.warn("获取SKU规格信息失败，SKU ID: {}", storeSkuStock.getSkuId(), e);
                // 即使获取规格失败，也添加基础信息
                PmsStoreSkuStockExtendDto extendDto = PmsStoreSkuStockExtendDto.fromStoreSkuStock(storeSkuStock);
                extendDto.setSpData("获取规格失败");
                extendDto.setTotalStock(0);
                result.add(extendDto);
            }
        }
        
        return result;
    }
    
    @Override
    public List<PmsStoreSkuStock> getStoreSkuStockBySkuId(Long skuId) {
        PmsStoreSkuStockExample example = new PmsStoreSkuStockExample();
        example.createCriteria().andSkuIdEqualTo(skuId);
        return storeSkuStockMapper.selectByExample(example);
    }
    
    @Override
    public PmsStoreSkuStock getStoreSkuStock(Long storeId, Long skuId) {
        PmsStoreSkuStockExample example = new PmsStoreSkuStockExample();
        example.createCriteria()
                .andStoreIdEqualTo(storeId)
                .andSkuIdEqualTo(skuId);
        List<PmsStoreSkuStock> list = storeSkuStockMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(list)) {
            return list.get(0);
        }
        return null;
    }
    
    @Override
    @Transactional
    public int batchSaveStoreSkuStock(List<PmsStoreSkuStock> storeSkuStockList) {
        if (CollectionUtils.isEmpty(storeSkuStockList)) {
            return 0;
        }
        
        int count = 0;
        for (PmsStoreSkuStock storeSkuStock : storeSkuStockList) {
            PmsStoreSkuStock result = saveStoreSkuStock(storeSkuStock);
            if (result != null) {
                count++;
            }
        }
        return count;
    }
    
    @Override
    @Transactional
    public PmsStoreSkuStock saveStoreSkuStock(PmsStoreSkuStock storeSkuStock) {
        try {
            // 检查是否已存在记录
            PmsStoreSkuStock existing = getStoreSkuStock(storeSkuStock.getStoreId(), storeSkuStock.getSkuId());
            
            Date now = new Date();
            if (existing != null) {
                // 更新现有记录
                storeSkuStock.setId(existing.getId());
                storeSkuStock.setUpdateTime(now);
                if (storeSkuStock.getCreateTime() == null) {
                    storeSkuStock.setCreateTime(existing.getCreateTime());
                }
                storeSkuStockMapper.updateByPrimaryKeySelective(storeSkuStock);
                return storeSkuStock;
            } else {
                // 新增记录
                storeSkuStock.setId(null);
                storeSkuStock.setCreateTime(now);
                storeSkuStock.setUpdateTime(now);
                if (storeSkuStock.getStatus() == null) {
                    storeSkuStock.setStatus(1); // 默认启用
                }
                storeSkuStockMapper.insertSelective(storeSkuStock);
                return storeSkuStock;
            }
        } catch (Exception e) {
            LOGGER.error("保存门店SKU库存失败：{}", e.getMessage(), e);
            return null;
        }
    }
    
    @Override
    @Transactional
    public boolean deleteStoreSkuStock(Long storeId, Long skuId) {
        PmsStoreSkuStockExample example = new PmsStoreSkuStockExample();
        example.createCriteria()
                .andStoreIdEqualTo(storeId)
                .andSkuIdEqualTo(skuId);
        int count = storeSkuStockMapper.deleteByExample(example);
        return count > 0;
    }
    
    @Override
    public boolean validateStoreStockSum(Long skuId, Integer skuTotalStock) {
        List<PmsStoreSkuStock> storeStocks = getStoreSkuStockBySkuId(skuId);
        if (CollectionUtils.isEmpty(storeStocks)) {
            return skuTotalStock == null || skuTotalStock == 0;
        }
        
        int totalStoreStock = storeStocks.stream()
                .mapToInt(stock -> stock.getStock() != null ? stock.getStock() : 0)
                .sum();
        
        return totalStoreStock == (skuTotalStock != null ? skuTotalStock : 0);
    }
    
    @Override
    @Transactional
    public List<PmsStoreSkuStock> autoDistributeStock(Long skuId, Integer totalStock) {
        if (totalStock == null || totalStock <= 0) {
            LOGGER.warn("总库存为空或小于等于0，无法分配");
            return new ArrayList<>();
        }
        
        // 获取SKU信息
        PmsSkuStock skuStock = skuStockMapper.selectByPrimaryKey(skuId);
        if (skuStock == null) {
            LOGGER.error("SKU不存在，ID：{}", skuId);
            return new ArrayList<>();
        }
        
        // 获取所有启用的门店
        OmsStoreAddressExample storeExample = new OmsStoreAddressExample();
        storeExample.setOrderByClause("id ASC");
        List<OmsStoreAddress> stores = storeAddressMapper.selectByExample(storeExample);
        
        if (CollectionUtils.isEmpty(stores)) {
            LOGGER.warn("没有可用的门店进行库存分配");
            return new ArrayList<>();
        }
        
        List<PmsStoreSkuStock> result = new ArrayList<>();
        int storeCount = stores.size();
        int stockPerStore = totalStock / storeCount;
        int remainingStock = totalStock % storeCount;
        
        for (int i = 0; i < stores.size(); i++) {
            OmsStoreAddress store = stores.get(i);
            PmsStoreSkuStock storeSkuStock = new PmsStoreSkuStock();
            storeSkuStock.setStoreId(store.getId());
            storeSkuStock.setProductId(skuStock.getProductId());
            storeSkuStock.setSkuId(skuId);
            storeSkuStock.setSkuCode(skuStock.getSkuCode());
            
            // 分配库存，前几个门店分配余数
            int allocatedStock = stockPerStore;
            if (i < remainingStock) {
                allocatedStock += 1;
            }
            storeSkuStock.setStock(allocatedStock);
            storeSkuStock.setLowStock(Math.max(1, allocatedStock / 10)); // 预警值设为库存的10%，最少1
            storeSkuStock.setLockedStock(0);
            storeSkuStock.setSaleCount(0);
            storeSkuStock.setStatus(1);
            
            PmsStoreSkuStock saved = saveStoreSkuStock(storeSkuStock);
            if (saved != null) {
                result.add(saved);
            }
        }
        
        LOGGER.info("自动分配库存完成，SKU ID：{}，总库存：{}，分配给{}个门店", skuId, totalStock, result.size());
        return result;
    }
    
    @Override
    @Transactional
    public boolean batchSaveStoreSkuStockByProduct(Long productId, List<PmsStoreSkuStockParam> storeStockParams) {
        if (CollectionUtils.isEmpty(storeStockParams)) {
            return true;
        }
        
        try {
            for (PmsStoreSkuStockParam param : storeStockParams) {
                if (CollectionUtils.isEmpty(param.getStoreAllocations())) {
                    continue;
                }
                
                for (PmsStoreSkuStockParam.StoreStockAllocation allocation : param.getStoreAllocations()) {
                    PmsStoreSkuStock storeSkuStock = new PmsStoreSkuStock();
                    storeSkuStock.setStoreId(allocation.getStoreId());
                    storeSkuStock.setProductId(productId);
                    storeSkuStock.setSkuId(param.getSkuId());
                    storeSkuStock.setSkuCode(param.getSkuCode());
                    storeSkuStock.setStock(allocation.getStock());
                    storeSkuStock.setLowStock(allocation.getLowStock());
                    storeSkuStock.setLockedStock(0);
                    storeSkuStock.setSaleCount(0);
                    storeSkuStock.setStatus(allocation.getStatus() != null ? allocation.getStatus() : 1);
                    
                    saveStoreSkuStock(storeSkuStock);
                }
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("批量保存门店SKU库存失败：{}", e.getMessage(), e);
            throw new RuntimeException("保存门店库存失败", e);
        }
    }
    
    @Override
    @Transactional
    public int deleteStoreSkuStockByProductId(Long productId) {
        PmsStoreSkuStockExample example = new PmsStoreSkuStockExample();
        example.createCriteria().andProductIdEqualTo(productId);
        return storeSkuStockMapper.deleteByExample(example);
    }
    
    @Override
    @Transactional
    public void initStoreSkuStockForProduct(Long productId) {
        try {
            // 获取商品的所有SKU
            PmsSkuStockExample skuExample = new PmsSkuStockExample();
            skuExample.createCriteria().andProductIdEqualTo(productId);
            List<PmsSkuStock> skuList = skuStockMapper.selectByExample(skuExample);
            
            if (CollectionUtils.isEmpty(skuList)) {
                LOGGER.info("商品{}没有SKU信息，跳过门店库存初始化", productId);
                return;
            }
            
            // 获取所有门店
            OmsStoreAddressExample storeExample = new OmsStoreAddressExample();
            List<OmsStoreAddress> stores = storeAddressMapper.selectByExample(storeExample);
            
            if (CollectionUtils.isEmpty(stores)) {
                LOGGER.info("没有门店信息，跳过门店库存初始化");
                return;
            }
            
            // 为每个SKU和每个门店创建初始库存记录（库存为0）
            Date now = new Date();
            for (PmsSkuStock sku : skuList) {
                for (OmsStoreAddress store : stores) {
                    // 检查是否已存在记录
                    PmsStoreSkuStock existing = getStoreSkuStock(store.getId(), sku.getId());
                    if (existing == null) {
                        PmsStoreSkuStock storeSkuStock = new PmsStoreSkuStock();
                        storeSkuStock.setStoreId(store.getId());
                        storeSkuStock.setProductId(productId);
                        storeSkuStock.setSkuId(sku.getId());
                        storeSkuStock.setSkuCode(sku.getSkuCode());
                        storeSkuStock.setStock(0); // 初始库存为0
                        storeSkuStock.setLowStock(0);
                        storeSkuStock.setLockedStock(0);
                        storeSkuStock.setSaleCount(0);
                        storeSkuStock.setStatus(1); // 默认启用
                        storeSkuStock.setCreateTime(now);
                        storeSkuStock.setUpdateTime(now);
                        
                        storeSkuStockMapper.insertSelective(storeSkuStock);
                    }
                }
            }
            
            LOGGER.info("商品{}的门店库存初始化完成，创建了{}个SKU在{}个门店的库存记录", 
                productId, skuList.size(), stores.size());
            
        } catch (Exception e) {
            LOGGER.error("初始化商品{}的门店库存失败：{}", productId, e.getMessage(), e);
            throw new RuntimeException("初始化门店库存失败", e);
        }
    }
    
    @Override
    @Transactional
    public void syncStoreSkuStockForProduct(Long productId, List<Long> currentSkuIds) {
        try {
            // 删除不存在的SKU对应的门店库存记录
            if (!CollectionUtils.isEmpty(currentSkuIds)) {
                PmsStoreSkuStockExample deleteExample = new PmsStoreSkuStockExample();
                deleteExample.createCriteria()
                    .andProductIdEqualTo(productId)
                    .andSkuIdNotIn(currentSkuIds);
                int deletedCount = storeSkuStockMapper.deleteByExample(deleteExample);
                if (deletedCount > 0) {
                    LOGGER.info("删除了{}条过期的门店库存记录", deletedCount);
                }
            } else {
                // 如果没有当前SKU，删除所有门店库存
                deleteStoreSkuStockByProductId(productId);
                return;
            }
            
            // 获取所有门店
            OmsStoreAddressExample storeExample = new OmsStoreAddressExample();
            List<OmsStoreAddress> stores = storeAddressMapper.selectByExample(storeExample);
            
            if (CollectionUtils.isEmpty(stores)) {
                LOGGER.info("没有门店信息，跳过门店库存同步");
                return;
            }
            
            // 为新的SKU创建门店库存记录
            Date now = new Date();
            int newRecordCount = 0;
            
            for (Long skuId : currentSkuIds) {
                // 获取SKU信息
                PmsSkuStock sku = skuStockMapper.selectByPrimaryKey(skuId);
                if (sku == null) {
                    continue;
                }
                
                for (OmsStoreAddress store : stores) {
                    // 检查是否已存在记录
                    PmsStoreSkuStock existing = getStoreSkuStock(store.getId(), skuId);
                    if (existing == null) {
                        PmsStoreSkuStock storeSkuStock = new PmsStoreSkuStock();
                        storeSkuStock.setStoreId(store.getId());
                        storeSkuStock.setProductId(productId);
                        storeSkuStock.setSkuId(skuId);
                        storeSkuStock.setSkuCode(sku.getSkuCode());
                        storeSkuStock.setStock(0); // 新SKU的初始库存为0
                        storeSkuStock.setLowStock(0);
                        storeSkuStock.setLockedStock(0);
                        storeSkuStock.setSaleCount(0);
                        storeSkuStock.setStatus(1); // 默认启用
                        storeSkuStock.setCreateTime(now);
                        storeSkuStock.setUpdateTime(now);
                        
                        storeSkuStockMapper.insertSelective(storeSkuStock);
                        newRecordCount++;
                    }
                }
            }
            
            LOGGER.info("商品{}的门店库存同步完成，新增了{}条门店库存记录", productId, newRecordCount);
            
        } catch (Exception e) {
            LOGGER.error("同步商品{}的门店库存失败：{}", productId, e.getMessage(), e);
            throw new RuntimeException("同步门店库存失败", e);
        }
    }
    
    @Override
    public List<PmsStoreSkuStockExtendDto> getStoreSkuStockByStoreId(Long storeId) {
        // 根据门店ID查询所有SKU库存
        PmsStoreSkuStockExample example = new PmsStoreSkuStockExample();
        example.createCriteria().andStoreIdEqualTo(storeId);
        List<PmsStoreSkuStock> storeSkuStockList = storeSkuStockMapper.selectByExample(example);
        
        List<PmsStoreSkuStockExtendDto> result = new ArrayList<>();
        
        if (CollectionUtils.isEmpty(storeSkuStockList)) {
            return result;
        }
        
        // 为每个门店SKU库存记录添加规格信息和商品名称
        for (PmsStoreSkuStock storeSkuStock : storeSkuStockList) {
            try {
                PmsStoreSkuStockExtendDto extendDto = PmsStoreSkuStockExtendDto.fromStoreSkuStock(storeSkuStock);
                
                // 查询对应的SKU信息获取规格数据
                PmsSkuStock skuStock = skuStockMapper.selectByPrimaryKey(storeSkuStock.getSkuId());
                if (skuStock != null) {
                    extendDto.setSpData(skuStock.getSpData() != null ? skuStock.getSpData() : "暂无规格");
                    extendDto.setTotalStock(skuStock.getStock() != null ? skuStock.getStock() : 0);
                } else {
                    extendDto.setSpData("暂无规格");
                    extendDto.setTotalStock(0);
                }
                
                result.add(extendDto);
            } catch (Exception e) {
                LOGGER.warn("获取SKU规格信息失败，SKU ID: {}", storeSkuStock.getSkuId(), e);
                PmsStoreSkuStockExtendDto extendDto = PmsStoreSkuStockExtendDto.fromStoreSkuStock(storeSkuStock);
                extendDto.setSpData("获取规格失败");
                extendDto.setTotalStock(0);
                result.add(extendDto);
            }
        }
        
        return result;
    }
} 