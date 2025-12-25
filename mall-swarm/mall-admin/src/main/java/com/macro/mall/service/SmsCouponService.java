package com.macro.mall.service;

import com.macro.mall.dto.SmsCouponListVO;
import com.macro.mall.dto.SmsCouponParam;
import com.macro.mall.dto.SmsCouponShareResult;
import com.macro.mall.model.SmsCoupon;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 优惠券管理Service
 * Created by macro on 2018/8/28.
 */
public interface SmsCouponService {
    /**
     * 添加优惠券
     */
    @Transactional
    int create(SmsCouponParam couponParam);

    /**
     * 根据优惠券id删除优惠券
     */
    @Transactional
    int delete(Long id);

    /**
     * 根据优惠券id更新优惠券信息
     */
    @Transactional
    int update(Long id, SmsCouponParam couponParam);

    /**
     * 分页获取优惠券列表
     */
    List<SmsCoupon> list(String name, Integer type, Long schoolId, Integer pageSize, Integer pageNum);

    /**
     * 分页获取优惠券列表（包含学校信息）
     */
    List<SmsCouponListVO> listWithSchools(String name, Integer type, Long schoolId, Integer pageSize, Integer pageNum);

    /**
     * 获取优惠券详情
     * @param id 优惠券表id
     */
    SmsCouponParam getItem(Long id);

    /** 简易查询，用于装修选择
     * @param keyword 关键字
     * @param limit 行数
     */
    List<SmsCoupon> simpleList(String keyword, Integer limit);

    /**
     * 生成优惠券分享信息
     * @param couponId 优惠券ID
     * @return 分享信息
     */
    SmsCouponShareResult generateShareInfo(Long couponId);

    /**
     * 批量更新优惠券关联的学校
     * @param couponId 优惠券ID
     * @param schoolIds 学校ID列表
     * @return 操作结果
     */
    boolean updateCouponSchools(Long couponId, List<Long> schoolIds);
}
