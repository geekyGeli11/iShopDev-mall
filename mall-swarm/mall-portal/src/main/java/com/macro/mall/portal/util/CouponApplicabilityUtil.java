package com.macro.mall.portal.util;

import com.macro.mall.model.*;
import com.macro.mall.mapper.*;
import com.macro.mall.portal.domain.CartPromotionItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * 优惠券适用性检查工具类（Portal端）
 * Created by macro on 2025/09/01.
 */
@Component
public class CouponApplicabilityUtil {

    @Autowired
    private SmsCouponProductRelationMapper productRelationMapper;
    @Autowired
    private SmsCouponProductCategoryRelationMapper productCategoryRelationMapper;
    @Autowired
    private SmsCouponProductExcludeRelationMapper productExcludeRelationMapper;
    @Autowired
    private SmsCouponProductCategoryExcludeRelationMapper productCategoryExcludeRelationMapper;
    @Autowired
    private PmsProductCategoryMapper productCategoryMapper;

    /**
     * 检查优惠券是否适用于购物车商品
     * @param coupon 优惠券信息
     * @param cartItems 购物车商品列表
     * @return 是否适用
     */
    public boolean isCouponApplicableToCart(SmsCoupon coupon, List<CartPromotionItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            return false;
        }
        
        // 检查每个购物车商品是否都适用
        for (CartPromotionItem item : cartItems) {
            if (!isCouponApplicableToProduct(coupon, item.getProductId(), item.getProductCategoryId())) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * 检查优惠券是否适用于指定商品
     * @param coupon 优惠券信息
     * @param productId 商品ID
     * @param productCategoryId 商品分类ID
     * @return 是否适用
     */
    public boolean isCouponApplicableToProduct(SmsCoupon coupon, Long productId, Long productCategoryId) {
        // 第一步：基础适用范围检查
        boolean basicApplicable = checkBasicApplicability(coupon, productId, productCategoryId);
        
        if (!basicApplicable) {
            return false;
        }
        
        // 第二步：排除逻辑检查
        if (coupon.getEnableExclude() != null && coupon.getEnableExclude()) {
            return !isProductExcluded(coupon.getId(), productId, productCategoryId);
        }
        
        return true;
    }

    /**
     * 计算优惠金额
     * @param coupon 优惠券信息
     * @param orderAmount 订单金额
     * @return 优惠金额
     */
    public BigDecimal calculateDiscountAmount(SmsCoupon coupon, BigDecimal orderAmount) {
        // 检查使用门槛
        if (coupon.getMinPoint() != null && orderAmount.compareTo(coupon.getMinPoint()) < 0) {
            return BigDecimal.ZERO;
        }
        
        if (coupon.getCouponType() != null && coupon.getCouponType() == 1) {
            // 打折券
            if (coupon.getDiscountRate() != null) {
                BigDecimal discountAmount = orderAmount.multiply(BigDecimal.ONE.subtract(coupon.getDiscountRate()));
                return discountAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
            }
        } else {
            // 满减券
            if (coupon.getAmount() != null) {
                return coupon.getAmount();
            }
        }
        
        return BigDecimal.ZERO;
    }

    /**
     * 获取优惠券描述信息
     */
    public String getCouponDescription(SmsCoupon coupon) {
        StringBuilder description = new StringBuilder();
        
        // 优惠类型描述
        if (coupon.getCouponType() != null && coupon.getCouponType() == 1) {
            // 打折券
            if (coupon.getDiscountRate() != null) {
                int discount = (int) (coupon.getDiscountRate().doubleValue() * 10);
                description.append(discount).append("折");
            }
        } else {
            // 满减券
            if (coupon.getAmount() != null) {
                description.append("减").append(coupon.getAmount().intValue()).append("元");
            }
        }
        
        // 使用门槛描述
        if (coupon.getMinPoint() != null && coupon.getMinPoint().compareTo(BigDecimal.ZERO) > 0) {
            description.append("（满").append(coupon.getMinPoint().intValue()).append("元可用）");
        } else {
            description.append("（无门槛）");
        }
        
        return description.toString();
    }

    /**
     * 基础适用范围检查
     */
    private boolean checkBasicApplicability(SmsCoupon coupon, Long productId, Long productCategoryId) {
        Integer useType = coupon.getUseType();
        if (useType == null) {
            return false;
        }
        
        switch (useType) {
            case 0: // 全场通用
                return true;
            case 1: // 指定分类
                return isProductCategoryIncluded(coupon.getId(), productCategoryId);
            case 2: // 指定商品
                return isProductIncluded(coupon.getId(), productId);
            default:
                return false;
        }
    }

    /**
     * 检查商品是否在可用商品列表中
     */
    private boolean isProductIncluded(Long couponId, Long productId) {
        SmsCouponProductRelationExample example = new SmsCouponProductRelationExample();
        example.createCriteria().andCouponIdEqualTo(couponId).andProductIdEqualTo(productId);
        return productRelationMapper.countByExample(example) > 0;
    }

    /**
     * 检查商品分类是否在可用分类列表中（支持一级分类）
     */
    private boolean isProductCategoryIncluded(Long couponId, Long productCategoryId) {
        // 直接检查该分类是否在可用列表中
        SmsCouponProductCategoryRelationExample example = new SmsCouponProductCategoryRelationExample();
        example.createCriteria().andCouponIdEqualTo(couponId).andProductCategoryIdEqualTo(productCategoryId);
        if (productCategoryRelationMapper.countByExample(example) > 0) {
            return true;
        }

        // 如果当前分类是二级分类，检查其父分类是否在可用列表中
        PmsProductCategory currentCategory = productCategoryMapper.selectByPrimaryKey(productCategoryId);
        if (currentCategory != null && currentCategory.getParentId() != null && currentCategory.getParentId() != 0) {
            // 这是二级分类，检查父分类
            SmsCouponProductCategoryRelationExample parentExample = new SmsCouponProductCategoryRelationExample();
            parentExample.createCriteria().andCouponIdEqualTo(couponId).andProductCategoryIdEqualTo(currentCategory.getParentId());
            return productCategoryRelationMapper.countByExample(parentExample) > 0;
        }

        return false;
    }

    /**
     * 检查商品是否被排除
     */
    private boolean isProductExcluded(Long couponId, Long productId, Long productCategoryId) {
        // 检查是否在排除商品列表中
        SmsCouponProductExcludeRelationExample productExample = new SmsCouponProductExcludeRelationExample();
        productExample.createCriteria().andCouponIdEqualTo(couponId).andProductIdEqualTo(productId);
        if (productExcludeRelationMapper.countByExample(productExample) > 0) {
            return true;
        }
        
        // 检查是否在排除分类列表中（支持一级分类）
        // 直接检查该分类是否在排除列表中
        SmsCouponProductCategoryExcludeRelationExample categoryExample = new SmsCouponProductCategoryExcludeRelationExample();
        categoryExample.createCriteria().andCouponIdEqualTo(couponId).andProductCategoryIdEqualTo(productCategoryId);
        if (productCategoryExcludeRelationMapper.countByExample(categoryExample) > 0) {
            return true;
        }

        // 如果当前分类是二级分类，检查其父分类是否在排除列表中
        PmsProductCategory currentCategory = productCategoryMapper.selectByPrimaryKey(productCategoryId);
        if (currentCategory != null && currentCategory.getParentId() != null && currentCategory.getParentId() != 0) {
            // 这是二级分类，检查父分类
            SmsCouponProductCategoryExcludeRelationExample parentExample = new SmsCouponProductCategoryExcludeRelationExample();
            parentExample.createCriteria().andCouponIdEqualTo(couponId).andProductCategoryIdEqualTo(currentCategory.getParentId());
            return productCategoryExcludeRelationMapper.countByExample(parentExample) > 0;
        }

        return false;
    }
}
