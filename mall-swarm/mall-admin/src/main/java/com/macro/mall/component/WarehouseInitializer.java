package com.macro.mall.component;

import com.macro.mall.mapper.PmsSkuStockMapper;
import com.macro.mall.mapper.PmsStoreSkuStockMapper;
import com.macro.mall.model.OmsStoreAddress;
import com.macro.mall.model.PmsSkuStock;
import com.macro.mall.model.PmsSkuStockExample;
import com.macro.mall.model.PmsStoreSkuStock;
import com.macro.mall.model.PmsStoreSkuStockExample;
import com.macro.mall.service.OmsStoreAddressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 地库初始化组件
 * 在应用启动时自动初始化地库
 */
@Component
public class WarehouseInitializer implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(WarehouseInitializer.class);

    @Autowired
    private OmsStoreAddressService storeAddressService;

    @Autowired
    private PmsSkuStockMapper skuStockMapper;

    @Autowired
    private PmsStoreSkuStockMapper storeSkuStockMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        LOGGER.info("开始初始化地库...");

        try {
            // 1. 检查地库是否已存在
            OmsStoreAddress warehouse = storeAddressService.getWarehouse();
            
            if (warehouse == null) {
                LOGGER.info("地库不存在，开始创建地库...");
                storeAddressService.initializeWarehouse();
                warehouse = storeAddressService.getWarehouse();
                LOGGER.info("地库创建成功，ID: {}", warehouse.getId());
            } else {
                LOGGER.info("地库已存在，ID: {}", warehouse.getId());
            }

            // 2. 为所有 SKU 创建地库库存记录（如果不存在）
            initializeWarehouseStockForAllSkus(warehouse.getId());

            LOGGER.info("地库初始化完成");

        } catch (Exception e) {
            LOGGER.error("地库初始化失败", e);
            // 不抛出异常，避免影响应用启动
        }
    }

    /**
     * 为所有 SKU 初始化地库库存记录
     */
    private void initializeWarehouseStockForAllSkus(Long warehouseId) {
        LOGGER.info("开始为所有 SKU 初始化地库库存记录...");

        // 获取所有 SKU
        PmsSkuStockExample skuExample = new PmsSkuStockExample();
        List<PmsSkuStock> allSkus = skuStockMapper.selectByExample(skuExample);
        LOGGER.info("共有 {} 个 SKU", allSkus.size());

        int createdCount = 0;
        int existingCount = 0;

        for (PmsSkuStock sku : allSkus) {
            // 检查该 SKU 是否已有地库库存记录
            PmsStoreSkuStockExample storeSkuExample = new PmsStoreSkuStockExample();
            storeSkuExample.createCriteria()
                .andStoreIdEqualTo(warehouseId)
                .andSkuIdEqualTo(sku.getId());
            
            List<PmsStoreSkuStock> existingStocks = storeSkuStockMapper.selectByExample(storeSkuExample);
            
            if (existingStocks.isEmpty()) {
                // 创建地库库存记录
                PmsStoreSkuStock warehouseStock = new PmsStoreSkuStock();
                warehouseStock.setStoreId(warehouseId);
                warehouseStock.setProductId(sku.getProductId());
                warehouseStock.setSkuId(sku.getId());
                warehouseStock.setSkuCode(sku.getSkuCode());
                warehouseStock.setStock(0); // 初始库存为 0
                warehouseStock.setLowStock(0);
                warehouseStock.setLockedStock(0);
                warehouseStock.setSaleCount(0);
                warehouseStock.setStatus(1);
                warehouseStock.setCreateTime(new Date());
                warehouseStock.setUpdateTime(new Date());
                
                storeSkuStockMapper.insertSelective(warehouseStock);
                createdCount++;
            } else {
                existingCount++;
            }
        }

        LOGGER.info("地库库存记录初始化完成：新创建 {} 条，已存在 {} 条", createdCount, existingCount);
    }
}
