package com.macro.mall.portal.service;

import com.macro.mall.model.OmsSchool;
import com.macro.mall.model.SmsCouponSchoolRelation;

import java.util.List;

/**
 * 优惠券学校关联Service
 * Created by macro on 2024/12/30.
 */
public interface SmsCouponSchoolRelationService {
    
    /**
     * 根据优惠券ID获取关联的学校列表
     * @param couponId 优惠券ID
     * @return 学校列表
     */
    List<OmsSchool> getSchoolsByCouponId(Long couponId);
    
    /**
     * 根据学校ID获取关联的优惠券ID列表
     * @param schoolId 学校ID
     * @return 优惠券ID列表
     */
    List<Long> getCouponIdsBySchoolId(Long schoolId);
    
    /**
     * 检查优惠券是否关联了指定学校
     * @param couponId 优惠券ID
     * @param schoolId 学校ID
     * @return 是否关联
     */
    boolean isCouponAssociatedWithSchool(Long couponId, Long schoolId);
    
    /**
     * 根据商品ID列表获取关联的学校ID列表
     * @param productIds 商品ID列表
     * @return 学校ID列表
     */
    List<Long> getSchoolIdsByProductIds(List<Long> productIds);
    
    /**
     * 根据学校ID列表获取可用的优惠券ID列表
     * @param schoolIds 学校ID列表
     * @return 优惠券ID列表
     */
    List<Long> getAvailableCouponIdsBySchoolIds(List<Long> schoolIds);
}
