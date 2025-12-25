package com.macro.mall.service.impl;

import com.macro.mall.mapper.UmsMemberLevelMapper;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.mapper.SmsCouponMapper;
import com.macro.mall.model.UmsMemberLevel;
import com.macro.mall.model.UmsMemberLevelExample;
import com.macro.mall.model.UmsMemberExample;
import com.macro.mall.model.SmsCoupon;
import com.macro.mall.dto.UmsMemberLevelVO;
import com.macro.mall.service.UmsMemberLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.ArrayList;

/**
 * 会员等级管理Service实现类
 * Created by macro on 2018/4/26.
 */
@Service
public class UmsMemberLevelServiceImpl implements UmsMemberLevelService{
    @Autowired
    private UmsMemberLevelMapper memberLevelMapper;
    @Autowired
    private UmsMemberMapper memberMapper;
    @Autowired
    private SmsCouponMapper couponMapper;

    @Override
    public List<UmsMemberLevelVO> list(Integer defaultStatus) {
        UmsMemberLevelExample example = new UmsMemberLevelExample();
        if (defaultStatus != null) {
            example.createCriteria().andDefaultStatusEqualTo(defaultStatus);
        }
        example.setOrderByClause("growth_point ASC");
        List<UmsMemberLevel> memberLevels = memberLevelMapper.selectByExample(example);

        List<UmsMemberLevelVO> result = new ArrayList<>();
        for (UmsMemberLevel memberLevel : memberLevels) {
            UmsMemberLevelVO vo = new UmsMemberLevelVO();
            BeanUtils.copyProperties(memberLevel, vo);

            // 如果有关联的优惠券，查询优惠券名称
            if (memberLevel.getGiftCouponId() != null) {
                SmsCoupon coupon = couponMapper.selectByPrimaryKey(memberLevel.getGiftCouponId());
                if (coupon != null) {
                    vo.setGiftCouponName(coupon.getName());
                }
            }

            result.add(vo);
        }

        return result;
    }

    @Override
    public int create(UmsMemberLevel memberLevel) {
        // 验证会员等级名称不能重复
        UmsMemberLevelExample example = new UmsMemberLevelExample();
        example.createCriteria().andNameEqualTo(memberLevel.getName());
        List<UmsMemberLevel> existingLevels = memberLevelMapper.selectByExample(example);
        if (!existingLevels.isEmpty()) {
            throw new RuntimeException("会员等级名称已存在");
        }

        // 如果设置为默认等级，需要将其他等级的默认状态设为0
        if (memberLevel.getDefaultStatus() != null && memberLevel.getDefaultStatus() == 1) {
            resetDefaultStatus();
        }

        return memberLevelMapper.insert(memberLevel);
    }

    @Override
    public int update(Long id, UmsMemberLevel memberLevel) {
        memberLevel.setId(id);

        // 验证会员等级名称不能重复（排除自己）
        UmsMemberLevelExample example = new UmsMemberLevelExample();
        example.createCriteria().andNameEqualTo(memberLevel.getName()).andIdNotEqualTo(id);
        List<UmsMemberLevel> existingLevels = memberLevelMapper.selectByExample(example);
        if (!existingLevels.isEmpty()) {
            throw new RuntimeException("会员等级名称已存在");
        }

        // 如果设置为默认等级，需要将其他等级的默认状态设为0
        if (memberLevel.getDefaultStatus() != null && memberLevel.getDefaultStatus() == 1) {
            resetDefaultStatus();
        }

        return memberLevelMapper.updateByPrimaryKeySelective(memberLevel);
    }

    @Override
    public int delete(Long id) {
        // 检查是否有会员使用该等级
        UmsMemberExample memberExample = new UmsMemberExample();
        memberExample.createCriteria().andMemberLevelIdEqualTo(id);
        long memberCount = memberMapper.countByExample(memberExample);
        if (memberCount > 0) {
            throw new RuntimeException("该等级下还有会员，无法删除");
        }

        // 检查是否为默认等级
        UmsMemberLevel memberLevel = memberLevelMapper.selectByPrimaryKey(id);
        if (memberLevel != null && memberLevel.getDefaultStatus() != null && memberLevel.getDefaultStatus() == 1) {
            throw new RuntimeException("默认等级无法删除");
        }

        return memberLevelMapper.deleteByPrimaryKey(id);
    }

    @Override
    public UmsMemberLevel getItem(Long id) {
        return memberLevelMapper.selectByPrimaryKey(id);
    }

    /**
     * 重置所有等级的默认状态为0
     */
    private void resetDefaultStatus() {
        UmsMemberLevel updateLevel = new UmsMemberLevel();
        updateLevel.setDefaultStatus(0);
        UmsMemberLevelExample example = new UmsMemberLevelExample();
        example.createCriteria().andDefaultStatusEqualTo(1);
        memberLevelMapper.updateByExampleSelective(updateLevel, example);
    }
}
