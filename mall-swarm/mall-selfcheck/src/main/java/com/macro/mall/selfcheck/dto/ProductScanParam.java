package com.macro.mall.selfcheck.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 商品扫码查询参数
 * 
 * @author macro
 * @since 1.0.0
 */
@Schema(title = "商品扫码查询参数")
public class ProductScanParam {

    @Schema(title = "条码", description = "商品条码或SKU编码", example = "6901234567890")
    @NotBlank(message = "条码不能为空")
    private String barcode;

    @Schema(title = "扫码类型", description = "扫码类型：BARCODE-商品条码，SKU_CODE-SKU编码，PRODUCT_SN-货号", example = "BARCODE")
    private String scanType = "BARCODE";

    @Schema(title = "需要库存检查", description = "是否需要检查库存状态", example = "true")
    private Boolean needStockCheck = true;

    @Schema(title = "需要促销信息", description = "是否需要返回促销信息", example = "true")
    private Boolean needPromotionInfo = true;

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getScanType() {
        return scanType;
    }

    public void setScanType(String scanType) {
        this.scanType = scanType;
    }

    public Boolean getNeedStockCheck() {
        return needStockCheck;
    }

    public void setNeedStockCheck(Boolean needStockCheck) {
        this.needStockCheck = needStockCheck;
    }

    public Boolean getNeedPromotionInfo() {
        return needPromotionInfo;
    }

    public void setNeedPromotionInfo(Boolean needPromotionInfo) {
        this.needPromotionInfo = needPromotionInfo;
    }

    @Override
    public String toString() {
        return "ProductScanParam{" +
                "barcode='" + barcode + '\'' +
                ", scanType='" + scanType + '\'' +
                ", needStockCheck=" + needStockCheck +
                ", needPromotionInfo=" + needPromotionInfo +
                '}';
    }
} 