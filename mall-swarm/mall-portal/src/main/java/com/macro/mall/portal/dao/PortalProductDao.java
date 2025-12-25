package com.macro.mall.portal.dao;

import com.macro.mall.model.SmsCoupon;
import com.macro.mall.portal.domain.CartProduct;
import com.macro.mall.portal.domain.PromotionProduct;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 前台系统自定义商品Dao
 * Created by macro on 2018/8/2.
 */
public interface PortalProductDao {
    CartProduct getCartProduct(@Param("id") Long id);
    List<PromotionProduct> getPromotionProductList(@Param("ids") List<Long> ids);
    PromotionProduct getPromotionProduct(@Param("productId") Long productId);
    List<SmsCoupon> getAvailableCouponList(@Param("productId") Long productId,@Param("productCategoryId")Long productCategoryId);
    
    /**
     * 查询商品运费信息
     */
    ProductFreightInfo getProductFreightInfo(@Param("productId") Long productId);

    /**
     * 根据学校ID查询关联的商品ID列表
     */
    List<Long> getProductIdsBySchoolId(@Param("schoolId") Long schoolId);
    
    /**
     * 商品运费信息DTO
     */
    public static class ProductFreightInfo {
        private Long productId;
        private BigDecimal weight; // 商品重量（克）
        private Long freightTemplateId; // 运费模板ID
        
        public Long getProductId() {
            return productId;
        }
        
        public void setProductId(Long productId) {
            this.productId = productId;
        }
        
        public BigDecimal getWeight() {
            return weight;
        }
        
        public void setWeight(BigDecimal weight) {
            this.weight = weight;
        }
        
        public Long getFreightTemplateId() {
            return freightTemplateId;
        }
        
        public void setFreightTemplateId(Long freightTemplateId) {
            this.freightTemplateId = freightTemplateId;
        }
    }
}
