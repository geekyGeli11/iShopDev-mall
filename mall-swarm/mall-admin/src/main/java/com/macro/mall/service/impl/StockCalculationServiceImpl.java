package com.macro.mall.service.impl;

import com.macro.mall.dto.StockDistributionDto;
import com.macro.mall.dto.StockInconsistencyDto;
import com.macro.mall.mapper.PmsSkuStockMapper;
import com.macro.mall.mapper.PmsStoreSkuStockMapper;
import com.macro.mall.model.PmsSkuStock;
import com.macro.mall.model.PmsStoreSkuStock;
import com.macro.mall.model.PmsStoreSkuStockExample;
import com.macro.mall.service.OmsStoreAddressService;
import com.macro.mall.service.StockCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 库存计算服务实现类
 */
@Service
public class StockCalculationServiceImpl implements StockCalculationService {

    @Autowired
    private PmsSkuStockMapper skuStockMapper;

    @Autowired
    private PmsStoreSkuStockMapper storeSkuStockMapper;

    @Autowired
    private OmsStoreAddressService storeAddressService;

    @Override
    public Integer calculateTotalStock(Long skuId) {
        if (skuId == null) {
            return 0;
        }

        // 查询所有门店（包含地库）的库存
        PmsStoreSkuStockExample example = new PmsStoreSkuStockExample();
        example.createCriteria().andSkuIdEqualTo(skuId);
        List<PmsStoreSkuStock> storeStocks = storeSkuStockMapper.selectByExample(example);

        // 计算总库存
        int totalStock = 0;
        for (PmsStoreSkuStock storeStock : storeStocks) {
            if (storeStock.getStock() != null) {
                totalStock += storeStock.getStock();
            }
        }

        return totalStock;
    }

    @Override
    public boolean validateStockConsistency(Long skuId) {
        if (skuId == null) {
            return false;
        }

        // 获取记录的总库存
        PmsSkuStock skuStock = skuStockMapper.selectByPrimaryKey(skuId);
        if (skuStock == null) {
            return false;
        }

        // 计算实际总库存
        Integer calculatedStock = calculateTotalStock(skuId);

        // 比较是否一致
        Integer recordedStock = skuStock.getStock() != null ? skuStock.getStock() : 0;
        return recordedStock.equals(calculatedStock);
    }

    @Override
    public StockDistributionDto getStockDistribution(Long skuId) {
        if (skuId == null) {
            return null;
        }

        StockDistributionDto distribution = new StockDistributionDto();
        distribution.setSkuId(skuId);

        // 获取SKU信息
        PmsSkuStock skuStock = skuStockMapper.selectByPrimaryKey(skuId);
        if (skuStock != null) {
            distribution.setSkuCode(skuStock.getSkuCode());
            distribution.setTotalStock(skuStock.getStock() != null ? skuStock.getStock() : 0);
        }

        // 获取所有门店库存
        PmsStoreSkuStockExample example = new PmsStoreSkuStockExample();
        example.createCriteria().andSkuIdEqualTo(skuId);
        List<PmsStoreSkuStock> storeStocks = storeSkuStockMapper.selectByExample(example);

        List<StockDistributionDto.StoreStockDetail> storeStockDetails = new ArrayList<>();
        int warehouseStock = 0;
        int storesStockSum = 0;

        for (PmsStoreSkuStock storeStock : storeStocks) {
            StockDistributionDto.StoreStockDetail detail = new StockDistributionDto.StoreStockDetail();
            detail.setStoreId(storeStock.getStoreId());
            detail.setStock(storeStock.getStock() != null ? storeStock.getStock() : 0);
            detail.setLowStock(storeStock.getLowStock() != null ? storeStock.getLowStock() : 0);

            // 获取门店类型
            String storeType = storeAddressService.getStoreType(storeStock.getStoreId());
            detail.setStoreType(storeType);
            
            if ("WAREHOUSE".equals(storeType)) {
                detail.setStoreName("总仓库（地库）");
                warehouseStock += detail.getStock();
            } else if ("CENTRAL_WAREHOUSE".equals(storeType)) {
                detail.setStoreName("中央仓库");
                warehouseStock += detail.getStock();  // 中央仓库也计入仓库库存
            } else {
                // 这里可以查询门店名称，暂时使用门店ID
                detail.setStoreName("门店-" + storeStock.getStoreId());
                storesStockSum += detail.getStock();
            }

            // 判断库存状态
            if (detail.getStock() < detail.getLowStock()) {
                detail.setStatus("库存不足");
            } else {
                detail.setStatus("库存充足");
            }

            storeStockDetails.add(detail);
        }

        distribution.setWarehouseStock(warehouseStock);
        distribution.setStoresStockSum(storesStockSum);
        distribution.setStoreStocks(storeStockDetails);

        return distribution;
    }

    @Override
    public List<StockInconsistencyDto> batchValidateStock(List<Long> skuIds) {
        List<StockInconsistencyDto> inconsistencies = new ArrayList<>();

        if (skuIds == null || skuIds.isEmpty()) {
            return inconsistencies;
        }

        for (Long skuId : skuIds) {
            // 获取记录的总库存
            PmsSkuStock skuStock = skuStockMapper.selectByPrimaryKey(skuId);
            if (skuStock == null) {
                continue;
            }

            // 计算实际总库存
            Integer calculatedStock = calculateTotalStock(skuId);
            Integer recordedStock = skuStock.getStock() != null ? skuStock.getStock() : 0;

            // 如果不一致，添加到结果列表
            if (!recordedStock.equals(calculatedStock)) {
                StockInconsistencyDto inconsistency = new StockInconsistencyDto();
                inconsistency.setSkuId(skuId);
                inconsistency.setSkuCode(skuStock.getSkuCode());
                inconsistency.setTotalStock(recordedStock);
                inconsistency.setCalculatedStock(calculatedStock);
                inconsistency.setDifference(calculatedStock - recordedStock);
                inconsistencies.add(inconsistency);
            }
        }

        return inconsistencies;
    }
}
