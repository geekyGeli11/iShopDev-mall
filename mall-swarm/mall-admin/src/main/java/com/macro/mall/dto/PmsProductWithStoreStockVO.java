package com.macro.mall.dto;

import com.macro.mall.model.PmsProduct;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 商品信息VO（包含门店SKU库存）
 * 用于库存管理页面显示商品及其在指定门店的SKU库存
 */
public class PmsProductWithStoreStockVO extends PmsProduct {

    @Schema(title = "门店SKU库存列表")
    private List<StoreSkuStockInfo> storeSkuStocks;

    @Schema(title = "门店库存汇总（所有SKU库存之和）")
    private Integer storeStockTotal;

    @Schema(title = "门店库存显示文本")
    private String storeStockDisplay;

    public List<StoreSkuStockInfo> getStoreSkuStocks() {
        return storeSkuStocks;
    }

    public void setStoreSkuStocks(List<StoreSkuStockInfo> storeSkuStocks) {
        this.storeSkuStocks = storeSkuStocks;
    }

    public Integer getStoreStockTotal() {
        return storeStockTotal;
    }

    public void setStoreStockTotal(Integer storeStockTotal) {
        this.storeStockTotal = storeStockTotal;
    }

    public String getStoreStockDisplay() {
        return storeStockDisplay;
    }

    public void setStoreStockDisplay(String storeStockDisplay) {
        this.storeStockDisplay = storeStockDisplay;
    }

    /**
     * 门店SKU库存信息
     */
    public static class StoreSkuStockInfo {
        @Schema(title = "SKU ID")
        private Long skuId;

        @Schema(title = "SKU编码")
        private String skuCode;

        @Schema(title = "规格信息")
        private String spData;

        @Schema(title = "门店库存")
        private Integer stock;

        @Schema(title = "预警库存")
        private Integer lowStock;

        public Long getSkuId() {
            return skuId;
        }

        public void setSkuId(Long skuId) {
            this.skuId = skuId;
        }

        public String getSkuCode() {
            return skuCode;
        }

        public void setSkuCode(String skuCode) {
            this.skuCode = skuCode;
        }

        public String getSpData() {
            return spData;
        }

        public void setSpData(String spData) {
            this.spData = spData;
        }

        public Integer getStock() {
            return stock;
        }

        public void setStock(Integer stock) {
            this.stock = stock;
        }

        public Integer getLowStock() {
            return lowStock;
        }

        public void setLowStock(Integer lowStock) {
            this.lowStock = lowStock;
        }
    }

    /**
     * 从PmsProduct创建VO
     */
    public static PmsProductWithStoreStockVO fromProduct(PmsProduct product) {
        PmsProductWithStoreStockVO vo = new PmsProductWithStoreStockVO();
        vo.setId(product.getId());
        vo.setBrandId(product.getBrandId());
        vo.setProductCategoryId(product.getProductCategoryId());
        vo.setProductAttributeCategoryId(product.getProductAttributeCategoryId());
        vo.setName(product.getName());
        vo.setPic(product.getPic());
        vo.setProductSn(product.getProductSn());
        vo.setDeleteStatus(product.getDeleteStatus());
        vo.setPublishStatus(product.getPublishStatus());
        vo.setNewStatus(product.getNewStatus());
        vo.setRecommandStatus(product.getRecommandStatus());
        vo.setVerifyStatus(product.getVerifyStatus());
        vo.setSort(product.getSort());
        vo.setSale(product.getSale());
        vo.setPrice(product.getPrice());
        vo.setPromotionPrice(product.getPromotionPrice());
        vo.setGiftGrowth(product.getGiftGrowth());
        vo.setGiftPoint(product.getGiftPoint());
        vo.setUsePointLimit(product.getUsePointLimit());
        vo.setSubTitle(product.getSubTitle());
        vo.setOriginalPrice(product.getOriginalPrice());
        vo.setStock(product.getStock());
        vo.setLowStock(product.getLowStock());
        vo.setUnit(product.getUnit());
        vo.setWeight(product.getWeight());
        vo.setPreviewStatus(product.getPreviewStatus());
        vo.setServiceIds(product.getServiceIds());
        vo.setKeywords(product.getKeywords());
        vo.setNote(product.getNote());
        vo.setAlbumPics(product.getAlbumPics());
        vo.setDetailTitle(product.getDetailTitle());
        vo.setPromotionStartTime(product.getPromotionStartTime());
        vo.setPromotionEndTime(product.getPromotionEndTime());
        vo.setPromotionPerLimit(product.getPromotionPerLimit());
        vo.setPromotionType(product.getPromotionType());
        vo.setBrandName(product.getBrandName());
        vo.setProductCategoryName(product.getProductCategoryName());
        vo.setPurchaseType(product.getPurchaseType());
        vo.setPaybackTargetQuantity(product.getPaybackTargetQuantity());
        vo.setPaybackTargetAmount(product.getPaybackTargetAmount());
        vo.setPaybackStartDate(product.getPaybackStartDate());
        vo.setEnablePaybackAnalysis(product.getEnablePaybackAnalysis());
        vo.setFreightTemplateId(product.getFreightTemplateId());
        vo.setDiyEnabled(product.getDiyEnabled());
        vo.setDiyTemplateId(product.getDiyTemplateId());
        vo.setDescription(product.getDescription());
        vo.setDetailDesc(product.getDetailDesc());
        vo.setDetailHtml(product.getDetailHtml());
        vo.setDetailMobileHtml(product.getDetailMobileHtml());
        return vo;
    }
}
