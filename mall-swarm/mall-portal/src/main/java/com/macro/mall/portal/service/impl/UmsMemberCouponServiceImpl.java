package com.macro.mall.portal.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.macro.mall.common.exception.Asserts;
import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import com.macro.mall.portal.dao.SmsCouponHistoryDao;
import com.macro.mall.portal.domain.CartPromotionItem;
import com.macro.mall.portal.domain.SmsCouponHistoryDetail;
import com.macro.mall.portal.service.SmsCouponSchoolRelationService;
import com.macro.mall.portal.service.UmsMemberCouponService;
import com.macro.mall.portal.service.UmsMemberService;
import com.macro.mall.portal.util.CouponApplicabilityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 会员优惠券管理Service实现类
 * Created by macro on 2018/8/29.
 */
@Service
public class UmsMemberCouponServiceImpl implements UmsMemberCouponService {
    @Autowired
    private UmsMemberService memberService;
    @Autowired
    private SmsCouponMapper couponMapper;
    @Autowired
    private SmsCouponHistoryMapper couponHistoryMapper;
    @Autowired
    private SmsCouponHistoryDao couponHistoryDao;
    @Autowired
    private SmsCouponProductRelationMapper couponProductRelationMapper;
    @Autowired
    private SmsCouponProductCategoryRelationMapper couponProductCategoryRelationMapper;
    @Autowired
    private PmsProductMapper productMapper;
    @Autowired
    private SmsCouponSchoolRelationService couponSchoolRelationService;
    @Autowired
    private CouponApplicabilityUtil couponApplicabilityUtil;
    @Override
    public void add(Long couponId) {
        UmsMember currentMember = memberService.getCurrentMember();
        //获取优惠券信息，判断数量
        SmsCoupon coupon = couponMapper.selectByPrimaryKey(couponId);
        if(coupon==null){
            Asserts.fail("优惠券不存在");
        }
        if(coupon.getCount()<=0){
            Asserts.fail("优惠券已经领完了");
        }
        Date now = new Date();
        if(now.before(coupon.getStartTime())){
            Asserts.fail("优惠券活动还未开始");
        }
        if(now.after(coupon.getEndTime())){
            Asserts.fail("优惠券活动已经结束");
        }
        if(now.before(coupon.getEnableTime())){
            Asserts.fail("优惠券还没到领取时间");
        }
        //判断用户领取的优惠券数量是否超过限制
        SmsCouponHistoryExample couponHistoryExample = new SmsCouponHistoryExample();
        couponHistoryExample.createCriteria().andCouponIdEqualTo(couponId).andMemberIdEqualTo(currentMember.getId());
        long count = couponHistoryMapper.countByExample(couponHistoryExample);
        if(count>=coupon.getPerLimit()){
            Asserts.fail("您已经达到领取次数上限");
        }
        //生成领取优惠券历史
        SmsCouponHistory couponHistory = new SmsCouponHistory();
        couponHistory.setCouponId(couponId);
        couponHistory.setCouponCode(generateCouponCode(currentMember.getId()));
        couponHistory.setCreateTime(now);
        couponHistory.setMemberId(currentMember.getId());
        couponHistory.setMemberNickname(currentMember.getNickname());
        //主动领取
        couponHistory.setGetType(1);
        //未使用
        couponHistory.setUseStatus(0);
        couponHistoryMapper.insert(couponHistory);
        //修改优惠券表的数量、领取数量
        coupon.setCount(coupon.getCount()-1);
        coupon.setReceiveCount(coupon.getReceiveCount()==null?1:coupon.getReceiveCount()+1);
        couponMapper.updateByPrimaryKey(coupon);
    }

    @Override
    public void addForMember(Long memberId, Long couponId) {
        // 获取会员信息
        UmsMember member = memberService.getById(memberId);
        if(member == null){
            Asserts.fail("会员不存在");
        }
        
        //获取优惠券信息，判断数量
        SmsCoupon coupon = couponMapper.selectByPrimaryKey(couponId);
        if(coupon==null){
            Asserts.fail("优惠券不存在");
        }
        if(coupon.getCount()<=0){
            Asserts.fail("优惠券已经领完了");
        }
        Date now = new Date();
        if(now.before(coupon.getStartTime())){
            Asserts.fail("优惠券活动还未开始");
        }
        if(now.after(coupon.getEndTime())){
            Asserts.fail("优惠券活动已经结束");
        }
        if(now.before(coupon.getEnableTime())){
            Asserts.fail("优惠券还没到领取时间");
        }
        //判断用户领取的优惠券数量是否超过限制
        SmsCouponHistoryExample couponHistoryExample = new SmsCouponHistoryExample();
        couponHistoryExample.createCriteria().andCouponIdEqualTo(couponId).andMemberIdEqualTo(memberId);
        long count = couponHistoryMapper.countByExample(couponHistoryExample);
        if(count>=coupon.getPerLimit()){
            Asserts.fail("该会员已经达到领取次数上限");
        }
        //生成领取优惠券历史
        SmsCouponHistory couponHistory = new SmsCouponHistory();
        couponHistory.setCouponId(couponId);
        couponHistory.setCouponCode(generateCouponCode(memberId));
        couponHistory.setCreateTime(now);
        couponHistory.setMemberId(memberId);
        couponHistory.setMemberNickname(member.getNickname());
        //系统发放（签到奖励等）
        couponHistory.setGetType(2);
        //未使用
        couponHistory.setUseStatus(0);
        couponHistoryMapper.insert(couponHistory);
        //修改优惠券表的数量、领取数量
        coupon.setCount(coupon.getCount()-1);
        coupon.setReceiveCount(coupon.getReceiveCount()==null?1:coupon.getReceiveCount()+1);
        couponMapper.updateByPrimaryKey(coupon);
    }

    /**
     * 16位优惠码生成：时间戳后8位+4位随机数+用户id后4位
     */
    private String generateCouponCode(Long memberId) {
        StringBuilder sb = new StringBuilder();
        Long currentTimeMillis = System.currentTimeMillis();
        String timeMillisStr = currentTimeMillis.toString();
        sb.append(timeMillisStr.substring(timeMillisStr.length() - 8));
        for (int i = 0; i < 4; i++) {
            sb.append(new Random().nextInt(10));
        }
        String memberIdStr = memberId.toString();
        if (memberIdStr.length() <= 4) {
            sb.append(String.format("%04d", memberId));
        } else {
            sb.append(memberIdStr.substring(memberIdStr.length()-4));
        }
        return sb.toString();
    }

    @Override
    public List<SmsCouponHistory> listHistory(Integer useStatus) {
        UmsMember currentMember = memberService.getCurrentMember();
        SmsCouponHistoryExample couponHistoryExample=new SmsCouponHistoryExample();
        SmsCouponHistoryExample.Criteria criteria = couponHistoryExample.createCriteria();
        criteria.andMemberIdEqualTo(currentMember.getId());
        if(useStatus!=null && useStatus!=-1){
            criteria.andUseStatusEqualTo(useStatus);
        }
        return couponHistoryMapper.selectByExample(couponHistoryExample);
    }

    @Override
    public List<SmsCouponHistoryDetail> listCart(List<CartPromotionItem> cartItemList, Integer type) {
        return listCart(cartItemList, type, false);
    }

    @Override
    public List<SmsCouponHistoryDetail> listCart(List<CartPromotionItem> cartItemList, Integer type, boolean filterBySchool) {
        UmsMember currentMember = memberService.getCurrentMember();
        Date now = new Date();
        //获取该用户所有优惠券
        List<SmsCouponHistoryDetail> allList = couponHistoryDao.getDetailList(currentMember.getId());

        // 如果需要按学校过滤，先获取购物车商品关联的学校ID列表
        List<Long> availableSchoolIds = null;
        if (filterBySchool && cartItemList != null && !cartItemList.isEmpty()) {
            List<Long> productIds = cartItemList.stream()
                    .map(CartPromotionItem::getProductId)
                    .collect(Collectors.toList());
            availableSchoolIds = couponSchoolRelationService.getSchoolIdsByProductIds(productIds);
        }
        //根据优惠券使用类型和新的适用性逻辑来判断优惠券是否可用
        List<SmsCouponHistoryDetail> enableList = new ArrayList<>();
        List<SmsCouponHistoryDetail> disableList = new ArrayList<>();

        for (SmsCouponHistoryDetail couponHistoryDetail : allList) {
            // 添加空指针检查
            if (couponHistoryDetail.getCoupon() == null) {
                continue;
            }

            SmsCoupon coupon = couponHistoryDetail.getCoupon();
            Date endTime = coupon.getEndTime();

            // 检查优惠券是否过期
            if (now.after(endTime)) {
                disableList.add(couponHistoryDetail);
                continue;
            }

            // 计算购物车商品的总价
            BigDecimal totalAmount = calcTotalAmount(cartItemList);

            // 检查是否满足使用门槛
            if (coupon.getMinPoint() != null && totalAmount.compareTo(coupon.getMinPoint()) < 0) {
                disableList.add(couponHistoryDetail);
                continue;
            }

            // 使用新的适用性检查逻辑
            boolean isApplicable = couponApplicabilityUtil.isCouponApplicableToCart(coupon, cartItemList);

            // 如果需要按学校过滤，检查优惠券是否关联了购物车商品的学校
            boolean isSchoolValid = true;
            if (filterBySchool && availableSchoolIds != null && !availableSchoolIds.isEmpty()) {
                // 获取优惠券关联的学校列表
                List<OmsSchool> couponSchools = couponSchoolRelationService.getSchoolsByCouponId(coupon.getId());
                if (couponSchools.isEmpty()) {
                    // 如果优惠券没有关联任何学校，则认为是全局可用的
                    isSchoolValid = true;
                } else {
                    // 检查优惠券关联的学校是否与购物车商品的学校有交集
                    List<Long> couponSchoolIds = couponSchools.stream().map(OmsSchool::getId).collect(Collectors.toList());
                    isSchoolValid = availableSchoolIds.stream().anyMatch(couponSchoolIds::contains);
                }
            }

            if (isApplicable && isSchoolValid) {
                enableList.add(couponHistoryDetail);
            } else {
                disableList.add(couponHistoryDetail);
            }

        }
        if(type.equals(1)){
            return enableList;
        }else{
            return disableList;
        }
    }

    @Override
    public List<SmsCoupon> listByProduct(Long productId) {
        UmsMember currentMember = memberService.getCurrentMember();
        List<Long> allCouponIds = new ArrayList<>();
        //获取指定商品优惠券
        SmsCouponProductRelationExample cprExample = new SmsCouponProductRelationExample();
        cprExample.createCriteria().andProductIdEqualTo(productId);
        List<SmsCouponProductRelation> cprList = couponProductRelationMapper.selectByExample(cprExample);
        if(CollUtil.isNotEmpty(cprList)){
            List<Long> couponIds = cprList.stream().map(SmsCouponProductRelation::getCouponId).collect(Collectors.toList());
            allCouponIds.addAll(couponIds);
        }
        //获取指定分类优惠券
        PmsProduct product = productMapper.selectByPrimaryKey(productId);
        SmsCouponProductCategoryRelationExample cpcrExample = new SmsCouponProductCategoryRelationExample();
        cpcrExample.createCriteria().andProductCategoryIdEqualTo(product.getProductCategoryId());
        List<SmsCouponProductCategoryRelation> cpcrList = couponProductCategoryRelationMapper.selectByExample(cpcrExample);
        if(CollUtil.isNotEmpty(cpcrList)){
            List<Long> couponIds = cpcrList.stream().map(SmsCouponProductCategoryRelation::getCouponId).collect(Collectors.toList());
            allCouponIds.addAll(couponIds);
        }
        if(CollUtil.isEmpty(allCouponIds)){
            return new ArrayList<>();
        }
        // 查询所有优惠券，包括全场赠券、会员赠券、购物赠券、注册赠券等所有类型
        // 同时支持满减券和打折券两种折扣类型
        Date now = new Date();
        SmsCouponExample couponExample = new SmsCouponExample();
        couponExample.createCriteria().andEndTimeGreaterThan(now)
                .andStartTimeLessThan(now)
                .andUseTypeEqualTo(0)
                .andEnableTimeLessThanOrEqualTo(now)  // 可领取时间已到
                .andCountGreaterThan(0);              // 剩余数量大于0
                // 移除 type 限制，支持所有发放类型
        couponExample.or(couponExample.createCriteria()
                .andEndTimeGreaterThan(now)
                .andStartTimeLessThan(now)
                .andUseTypeNotEqualTo(0)
                .andEnableTimeLessThanOrEqualTo(now)  // 可领取时间已到
                .andCountGreaterThan(0)               // 剩余数量大于0
                .andIdIn(allCouponIds));
                // 移除 type 限制，支持所有发放类型
        List<SmsCoupon> allCoupons = couponMapper.selectByExample(couponExample);
        
        if (allCoupons.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 过滤掉已经领取过的或超过领取限制的优惠券，并检查适用性
        List<SmsCoupon> availableCoupons = new ArrayList<>();
        for (SmsCoupon coupon : allCoupons) {
            // 查询用户已领取的该优惠券数量
            SmsCouponHistoryExample historyExample = new SmsCouponHistoryExample();
            historyExample.createCriteria()
                    .andCouponIdEqualTo(coupon.getId())
                    .andMemberIdEqualTo(currentMember.getId());
            long count = couponHistoryMapper.countByExample(historyExample);

            // 如果领取数量小于每人限领数量，则检查适用性
            if (count < coupon.getPerLimit()) {
                // 使用新的适用性检查逻辑
                boolean isApplicable = couponApplicabilityUtil.isCouponApplicableToProduct(
                    coupon, productId, product.getProductCategoryId());

                if (isApplicable) {
                    availableCoupons.add(coupon);
                }
            }
        }
        
        return availableCoupons;
    }

    @Override
    public List<SmsCoupon> list(Integer useStatus) {
        return list(useStatus, null);
    }

    @Override
    public List<SmsCoupon> list(Integer useStatus, Long schoolId) {
        UmsMember member = memberService.getCurrentMember();
        List<SmsCoupon> allCoupons = couponHistoryDao.getCouponList(member.getId(), useStatus);

        // 如果没有指定学校ID，直接返回所有优惠券
        if (schoolId == null) {
            return allCoupons;
        }

        // 过滤出该学校可用的优惠券
        List<SmsCoupon> schoolCoupons = new ArrayList<>();
        for (SmsCoupon coupon : allCoupons) {
            // 检查优惠券是否关联了学校
            List<OmsSchool> couponSchools = couponSchoolRelationService.getSchoolsByCouponId(coupon.getId());
            if (couponSchools.isEmpty()) {
                // 优惠券没有关联任何学校，认为是全局可用的
                schoolCoupons.add(coupon);
            } else {
                // 检查是否关联了指定学校
                boolean isAssociated = couponSchoolRelationService.isCouponAssociatedWithSchool(coupon.getId(), schoolId);
                if (isAssociated) {
                    schoolCoupons.add(coupon);
                }
            }
        }

        return schoolCoupons;
    }

    @Override
    public List<SmsCoupon> listAvailable() {
        return listAvailable(null);
    }

    @Override
    public List<SmsCoupon> listAvailable(Long schoolId) {
        UmsMember currentMember = memberService.getCurrentMember();
        Date now = new Date();

        // 查找可领取的优惠券，只返回type=0（全场赠券）类型
        SmsCouponExample couponExample = new SmsCouponExample();
        SmsCouponExample.Criteria criteria = couponExample.createCriteria()
                .andTypeEqualTo(0)                 // 只返回全场赠券类型
                .andEndTimeGreaterThan(now)        // 未过期
                .andStartTimeLessThanOrEqualTo(now) // 已开始
                .andEnableTimeLessThanOrEqualTo(now) // 可领取时间已到
                .andCountGreaterThan(0);           // 剩余数量大于0

        // 如果指定了学校ID，需要过滤出该学校可用的优惠券
        List<SmsCoupon> allCoupons;
        if (schoolId != null) {
            // 获取该学校关联的优惠券ID列表
            List<Long> schoolCouponIds = couponSchoolRelationService.getCouponIdsBySchoolId(schoolId);
            if (schoolCouponIds.isEmpty()) {
                // 如果该学校没有关联任何优惠券，返回空列表
                return new ArrayList<>();
            }
            criteria.andIdIn(schoolCouponIds);
        }

        allCoupons = couponMapper.selectByExample(couponExample);

        if (allCoupons.isEmpty()) {
            return new ArrayList<>();
        }

        // 过滤掉已经领取过的或超过领取限制的优惠券
        List<SmsCoupon> availableCoupons = new ArrayList<>();
        for (SmsCoupon coupon : allCoupons) {
            // 查询用户已领取的该优惠券数量
            SmsCouponHistoryExample historyExample = new SmsCouponHistoryExample();
            historyExample.createCriteria()
                    .andCouponIdEqualTo(coupon.getId())
                    .andMemberIdEqualTo(currentMember.getId());
            long count = couponHistoryMapper.countByExample(historyExample);

            // 如果领取数量小于每人限领数量，则可领取
            if (count < coupon.getPerLimit()) {
                // 如果没有指定学校ID，或者优惠券没有学校限制，或者优惠券关联了指定学校，则可领取
                if (schoolId == null) {
                    // 没有指定学校，直接添加
                    availableCoupons.add(coupon);
                } else {
                    // 检查优惠券是否关联了学校
                    List<OmsSchool> couponSchools = couponSchoolRelationService.getSchoolsByCouponId(coupon.getId());
                    if (couponSchools.isEmpty()) {
                        // 优惠券没有关联任何学校，认为是全局可用的
                        availableCoupons.add(coupon);
                    } else {
                        // 检查是否关联了指定学校
                        boolean isAssociated = couponSchoolRelationService.isCouponAssociatedWithSchool(coupon.getId(), schoolId);
                        if (isAssociated) {
                            availableCoupons.add(coupon);
                        }
                    }
                }
            }
        }

        return availableCoupons;
    }

    private BigDecimal calcTotalAmount(List<CartPromotionItem> cartItemList) {
        BigDecimal total = new BigDecimal("0");
        for (CartPromotionItem item : cartItemList) {
            BigDecimal realPrice = item.getPrice().subtract(item.getReduceAmount());
            total=total.add(realPrice.multiply(new BigDecimal(item.getQuantity())));
        }
        return total;
    }

    private BigDecimal calcTotalAmountByproductCategoryId(List<CartPromotionItem> cartItemList,List<Long> productCategoryIds) {
        BigDecimal total = new BigDecimal("0");
        for (CartPromotionItem item : cartItemList) {
            if(productCategoryIds.contains(item.getProductCategoryId())){
                BigDecimal realPrice = item.getPrice().subtract(item.getReduceAmount());
                total=total.add(realPrice.multiply(new BigDecimal(item.getQuantity())));
            }
        }
        return total;
    }

    private BigDecimal calcTotalAmountByProductId(List<CartPromotionItem> cartItemList,List<Long> productIds) {
        BigDecimal total = new BigDecimal("0");
        for (CartPromotionItem item : cartItemList) {
            if(productIds.contains(item.getProductId())){
                BigDecimal realPrice = item.getPrice().subtract(item.getReduceAmount());
                total=total.add(realPrice.multiply(new BigDecimal(item.getQuantity())));
            }
        }
        return total;
    }

    @Override
    public SmsCoupon getCouponDetail(Long couponId) {
        // 查询优惠券基本信息
        SmsCoupon coupon = couponMapper.selectByPrimaryKey(couponId);
        if (coupon == null) {
            return null;
        }

        // 检查优惠券是否可用（未删除、已发布、在有效期内）
        Date now = new Date();
        if (coupon.getEnableTime() != null && now.before(coupon.getEnableTime())) {
            return null; // 还未开始
        }
        if (coupon.getEndTime() != null && now.after(coupon.getEndTime())) {
            return null; // 已过期
        }
        if (coupon.getPublishCount() != null && coupon.getReceiveCount() != null
            && coupon.getReceiveCount() >= coupon.getPublishCount()) {
            return null; // 已领完
        }

        return coupon;
    }

}
