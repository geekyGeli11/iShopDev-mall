package com.macro.mall.dto;

import com.macro.mall.model.PmsStoreSkuStock;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 门店SKU库存扩展DTO
 * 用于返回包含规格信息的门店库存数据
 * Created by macro on 2024-01-20.
 */
public class PmsStoreSkuStockExtendDto extends PmsStoreSkuStock {
    
    @Schema(title = "SKU规格信息")
    private String spData;
    
    @Schema(title = "SKU总库存（来自pms_sku_stock表）")
    private Integer totalStock;

    public String getSpData() {
        return spData;
    }

    public void setSpData(String spData) {
        this.spData = spData;
    }

    public Integer getTotalStock() {
        return totalStock;
    }

    public void setTotalStock(Integer totalStock) {
        this.totalStock = totalStock;
    }

    /**
     * 从PmsStoreSkuStock对象创建扩展DTO
     */
    public static PmsStoreSkuStockExtendDto fromStoreSkuStock(PmsStoreSkuStock storeSkuStock) {
        PmsStoreSkuStockExtendDto dto = new PmsStoreSkuStockExtendDto();
        dto.setId(storeSkuStock.getId());
        dto.setStoreId(storeSkuStock.getStoreId());
        dto.setProductId(storeSkuStock.getProductId());
        dto.setSkuId(storeSkuStock.getSkuId());
        dto.setSkuCode(storeSkuStock.getSkuCode());
        dto.setStock(storeSkuStock.getStock());
        dto.setLowStock(storeSkuStock.getLowStock());
        dto.setLockedStock(storeSkuStock.getLockedStock());
        dto.setSaleCount(storeSkuStock.getSaleCount());
        dto.setStatus(storeSkuStock.getStatus());
        dto.setCreateTime(storeSkuStock.getCreateTime());
        dto.setUpdateTime(storeSkuStock.getUpdateTime());
        return dto;
    }
} 