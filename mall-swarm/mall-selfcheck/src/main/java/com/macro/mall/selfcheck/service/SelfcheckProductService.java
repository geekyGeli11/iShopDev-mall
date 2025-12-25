package com.macro.mall.selfcheck.service;

import com.macro.mall.selfcheck.dto.ProductScanParam;
import com.macro.mall.selfcheck.dto.ProductScanVO;

/**
 * 自助收银商品服务接口
 * 
 * @author macro
 * @since 1.0.0
 */
public interface SelfcheckProductService {

    /**
     * 扫码查询商品信息
     * @param param 扫码查询参数
     * @return 商品信息
     */
    ProductScanVO scanProduct(ProductScanParam param);

    /**
     * 根据商品ID获取商品详情
     * @param productId 商品ID
     * @param needStockCheck 是否需要库存检查
     * @param needPromotionInfo 是否需要促销信息
     * @return 商品详情
     */
    ProductScanVO getProductDetail(Long productId, Boolean needStockCheck, Boolean needPromotionInfo);

    /**
     * 根据SKU ID获取SKU详情
     * @param skuId SKU ID
     * @param needStockCheck 是否需要库存检查
     * @param needPromotionInfo 是否需要促销信息
     * @return SKU详情
     */
    ProductScanVO getSkuDetail(Long skuId, Boolean needStockCheck, Boolean needPromotionInfo);

    /**
     * 检查商品是否可以销售
     * @param productId 商品ID
     * @param skuId SKU ID（可选）
     * @param quantity 购买数量
     * @return 是否可以销售
     */
    boolean checkProductSaleable(Long productId, Long skuId, Integer quantity);

    /**
     * 计算商品实际价格
     * @param productId 商品ID
     * @param skuId SKU ID（可选）
     * @param memberId 会员ID（可选，用于会员价计算）
     * @param quantity 购买数量
     * @return 实际价格信息
     */
    ProductScanVO calculateProductPrice(Long productId, Long skuId, Long memberId, Integer quantity);

    /**
     * 批量扫码查询商品
     * @param barcodes 条码列表
     * @return 商品信息列表
     */
    java.util.List<ProductScanVO> batchScanProducts(java.util.List<String> barcodes);

    /**
     * 验证条码格式
     * @param barcode 条码
     * @return 条码类型：BARCODE-商品条码，SKU_CODE-SKU编码，PRODUCT_SN-货号，UNKNOWN-未知
     */
    String validateBarcodeFormat(String barcode);
} 