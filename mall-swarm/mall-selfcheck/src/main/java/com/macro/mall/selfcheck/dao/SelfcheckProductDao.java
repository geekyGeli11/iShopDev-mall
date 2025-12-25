package com.macro.mall.selfcheck.dao;

import com.macro.mall.selfcheck.dto.ProductScanVO;
import org.apache.ibatis.annotations.Param;

/**
 * 自助收银商品数据访问层
 * 
 * @author macro
 * @since 1.0.0
 */
public interface SelfcheckProductDao {

    /**
     * 根据商品条码查询商品信息
     * @param barcode 商品条码
     * @return 商品信息
     */
    ProductScanVO getProductByBarcode(@Param("barcode") String barcode);

    /**
     * 根据SKU编码查询商品信息
     * @param skuCode SKU编码
     * @return 商品信息
     */
    ProductScanVO getProductBySkuCode(@Param("skuCode") String skuCode);

    /**
     * 根据商品货号查询商品信息
     * @param productSn 商品货号
     * @return 商品信息
     */
    ProductScanVO getProductByProductSn(@Param("productSn") String productSn);

    /**
     * 根据商品ID查询商品详细信息
     * @param productId 商品ID
     * @return 商品信息
     */
    ProductScanVO getProductDetailById(@Param("productId") Long productId);

    /**
     * 根据SKU ID查询SKU详细信息
     * @param skuId SKU ID
     * @return 商品SKU信息
     */
    ProductScanVO getSkuDetailById(@Param("skuId") Long skuId);

    /**
     * 检查商品库存状态
     * @param productId 商品ID
     * @param skuId SKU ID（可选）
     * @return 库存信息
     */
    ProductScanVO checkProductStock(@Param("productId") Long productId, 
                                   @Param("skuId") Long skuId);

    /**
     * 获取商品促销信息
     * @param productId 商品ID
     * @return 促销信息
     */
    ProductScanVO getProductPromotionInfo(@Param("productId") Long productId);

    /**
     * 根据SKU编码精确搜索商品
     * @param keyword SKU编码（精确匹配）
     * @return 商品信息
     */
    ProductScanVO getProductByKeyword(@Param("keyword") String keyword);
}