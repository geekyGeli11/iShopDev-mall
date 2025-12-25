package com.macro.mall.portal.service.impl;

import com.macro.mall.mapper.OmsSchoolMapper;
import com.macro.mall.mapper.PmsProductSchoolRelationMapper;
import com.macro.mall.mapper.SmsCouponSchoolRelationMapper;
import com.macro.mall.model.*;
import com.macro.mall.portal.service.SmsCouponSchoolRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 优惠券学校关联Service实现类
 * Created by macro on 2024/12/30.
 */
@Service
public class SmsCouponSchoolRelationServiceImpl implements SmsCouponSchoolRelationService {
    
    @Autowired
    private SmsCouponSchoolRelationMapper couponSchoolRelationMapper;
    
    @Autowired
    private OmsSchoolMapper schoolMapper;
    
    @Autowired
    private PmsProductSchoolRelationMapper productSchoolRelationMapper;
    
    @Override
    public List<OmsSchool> getSchoolsByCouponId(Long couponId) {
        // 查询优惠券学校关联关系
        SmsCouponSchoolRelationExample example = new SmsCouponSchoolRelationExample();
        example.createCriteria().andCouponIdEqualTo(couponId);
        List<SmsCouponSchoolRelation> relations = couponSchoolRelationMapper.selectByExample(example);
        
        if (relations.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 获取学校ID列表
        List<Long> schoolIds = relations.stream()
                .map(SmsCouponSchoolRelation::getSchoolId)
                .collect(Collectors.toList());
        
        // 查询学校信息
        OmsSchoolExample schoolExample = new OmsSchoolExample();
        schoolExample.createCriteria().andIdIn(schoolIds).andStatusEqualTo(true);
        return schoolMapper.selectByExample(schoolExample);
    }
    
    @Override
    public List<Long> getCouponIdsBySchoolId(Long schoolId) {
        SmsCouponSchoolRelationExample example = new SmsCouponSchoolRelationExample();
        example.createCriteria().andSchoolIdEqualTo(schoolId);
        List<SmsCouponSchoolRelation> relations = couponSchoolRelationMapper.selectByExample(example);
        
        return relations.stream()
                .map(SmsCouponSchoolRelation::getCouponId)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean isCouponAssociatedWithSchool(Long couponId, Long schoolId) {
        SmsCouponSchoolRelationExample example = new SmsCouponSchoolRelationExample();
        example.createCriteria()
                .andCouponIdEqualTo(couponId)
                .andSchoolIdEqualTo(schoolId);
        long count = couponSchoolRelationMapper.countByExample(example);
        return count > 0;
    }
    
    @Override
    public List<Long> getSchoolIdsByProductIds(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 查询商品关联的学校
        PmsProductSchoolRelationExample example = new PmsProductSchoolRelationExample();
        example.createCriteria().andProductIdIn(productIds);
        List<PmsProductSchoolRelation> relations = productSchoolRelationMapper.selectByExample(example);
        
        return relations.stream()
                .map(PmsProductSchoolRelation::getSchoolId)
                .distinct()
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Long> getAvailableCouponIdsBySchoolIds(List<Long> schoolIds) {
        if (schoolIds == null || schoolIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 查询学校关联的优惠券
        SmsCouponSchoolRelationExample example = new SmsCouponSchoolRelationExample();
        example.createCriteria().andSchoolIdIn(schoolIds);
        List<SmsCouponSchoolRelation> relations = couponSchoolRelationMapper.selectByExample(example);
        
        return relations.stream()
                .map(SmsCouponSchoolRelation::getCouponId)
                .distinct()
                .collect(Collectors.toList());
    }
}
