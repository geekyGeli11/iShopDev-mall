package com.macro.mall.service.impl;

import com.macro.mall.mapper.OmsSchoolMapper;
import com.macro.mall.mapper.PmsProductSchoolRelationMapper;
import com.macro.mall.model.OmsSchool;
import com.macro.mall.model.OmsSchoolExample;
import com.macro.mall.model.PmsProductSchoolRelation;
import com.macro.mall.model.PmsProductSchoolRelationExample;
import com.macro.mall.service.PmsProductSchoolRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品学校关联Service实现类
 */
@Service
public class PmsProductSchoolRelationServiceImpl implements PmsProductSchoolRelationService {

    @Autowired
    private PmsProductSchoolRelationMapper relationMapper;

    @Autowired
    private OmsSchoolMapper schoolMapper;

    @Override
    @Transactional
    public boolean updateProductSchools(Long productId, List<Long> schoolIds) {
        // 先删除原有关联
        deleteByProductId(productId);

        // 如果学校ID列表为空，直接返回成功
        if (schoolIds == null || schoolIds.isEmpty()) {
            return true;
        }

        // 批量插入新关联
        Date now = new Date();
        for (Long schoolId : schoolIds) {
            PmsProductSchoolRelation relation = new PmsProductSchoolRelation();
            relation.setProductId(productId);
            relation.setSchoolId(schoolId);
            relation.setCreateTime(now);
            relation.setUpdateTime(now);
            relationMapper.insert(relation);
        }

        return true;
    }

    @Override
    public List<OmsSchool> getSchoolsByProductId(Long productId) {
        // 查询商品关联的学校ID列表
        PmsProductSchoolRelationExample relationExample = new PmsProductSchoolRelationExample();
        relationExample.createCriteria().andProductIdEqualTo(productId);
        List<PmsProductSchoolRelation> relations = relationMapper.selectByExample(relationExample);

        if (relations.isEmpty()) {
            return new ArrayList<>();
        }

        // 提取学校ID列表
        List<Long> schoolIds = relations.stream()
                .map(PmsProductSchoolRelation::getSchoolId)
                .collect(Collectors.toList());

        // 查询学校信息
        OmsSchoolExample schoolExample = new OmsSchoolExample();
        schoolExample.createCriteria().andIdIn(schoolIds);
        return schoolMapper.selectByExample(schoolExample);
    }

    @Override
    public List<Long> getProductIdsBySchoolId(Long schoolId) {
        PmsProductSchoolRelationExample example = new PmsProductSchoolRelationExample();
        example.createCriteria().andSchoolIdEqualTo(schoolId);
        List<PmsProductSchoolRelation> relations = relationMapper.selectByExample(example);

        return relations.stream()
                .map(PmsProductSchoolRelation::getProductId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean deleteByProductId(Long productId) {
        PmsProductSchoolRelationExample example = new PmsProductSchoolRelationExample();
        example.createCriteria().andProductIdEqualTo(productId);
        relationMapper.deleteByExample(example);
        return true;
    }

    @Override
    @Transactional
    public boolean deleteBySchoolId(Long schoolId) {
        PmsProductSchoolRelationExample example = new PmsProductSchoolRelationExample();
        example.createCriteria().andSchoolIdEqualTo(schoolId);
        relationMapper.deleteByExample(example);
        return true;
    }

    @Override
    public boolean isProductAssociatedWithSchool(Long productId, Long schoolId) {
        PmsProductSchoolRelationExample example = new PmsProductSchoolRelationExample();
        example.createCriteria()
                .andProductIdEqualTo(productId)
                .andSchoolIdEqualTo(schoolId);
        long count = relationMapper.countByExample(example);
        return count > 0;
    }
}
