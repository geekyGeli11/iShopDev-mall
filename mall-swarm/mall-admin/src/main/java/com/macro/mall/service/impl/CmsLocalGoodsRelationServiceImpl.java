package com.macro.mall.service.impl;

import com.macro.mall.mapper.CmsLocalGoodsRelationMapper;
import com.macro.mall.model.CmsLocalGoodsRelation;
import com.macro.mall.model.CmsLocalGoodsRelationExample;
import com.macro.mall.service.CmsLocalGoodsRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 本地好物与商品关联管理Service实现类
 */
@Service
public class CmsLocalGoodsRelationServiceImpl implements CmsLocalGoodsRelationService {
    
    @Autowired
    private CmsLocalGoodsRelationMapper relationMapper;

    @Override
    public int create(List<CmsLocalGoodsRelation> relationList) {
        int count = 0;
        for (CmsLocalGoodsRelation relation : relationList) {
            relation.setCreateTime(new Date());
            count += relationMapper.insert(relation);
        }
        return count;
    }

    @Override
    public int create(Long localGoodsId, List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return 0;
        }
        
        List<CmsLocalGoodsRelation> relationList = new ArrayList<>();
        for (int i = 0; i < productIds.size(); i++) {
            CmsLocalGoodsRelation relation = new CmsLocalGoodsRelation();
            relation.setLocalGoodsId(localGoodsId);
            relation.setProductId(productIds.get(i));
            relation.setSort(i);
            relation.setCreateTime(new Date());
            relationList.add(relation);
        }
        return create(relationList);
    }

    @Override
    public int update(Long id, CmsLocalGoodsRelation relation) {
        relation.setId(id);
        return relationMapper.updateByPrimaryKeySelective(relation);
    }

    @Override
    public int delete(Long id) {
        return relationMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int deleteByLocalGoodsId(Long localGoodsId) {
        CmsLocalGoodsRelationExample example = new CmsLocalGoodsRelationExample();
        example.createCriteria().andLocalGoodsIdEqualTo(localGoodsId);
        return relationMapper.deleteByExample(example);
    }

    @Override
    public List<CmsLocalGoodsRelation> listByLocalGoodsId(Long localGoodsId) {
        CmsLocalGoodsRelationExample example = new CmsLocalGoodsRelationExample();
        example.createCriteria().andLocalGoodsIdEqualTo(localGoodsId);
        example.setOrderByClause("sort asc");
        return relationMapper.selectByExample(example);
    }
} 