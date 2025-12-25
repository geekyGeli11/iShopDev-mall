package com.macro.mall.dto;

import com.macro.mall.model.PmsSkuStock;
import com.macro.mall.model.PmsStoreSkuStock;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.List;

/**
 * SKU库存及门店库存DTO
 * Created by macro on 2025-11-27.
 */
@Schema(title = "SKU库存及门店库存信息")
public class PmsSkuStockWithStoreDTO extends PmsSkuStock implements Serializable {
    
    @Schema(title = "门店库存列表")
    private List<PmsStoreSkuStock> storeStocks;

    private static final long serialVersionUID = 1L;

    public List<PmsStoreSkuStock> getStoreStocks() {
        return storeStocks;
    }

    public void setStoreStocks(List<PmsStoreSkuStock> storeStocks) {
        this.storeStocks = storeStocks;
    }
}
