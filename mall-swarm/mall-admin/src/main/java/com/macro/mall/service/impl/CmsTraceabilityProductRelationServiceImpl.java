package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.dao.CmsTraceabilityProductRelationDao;
import com.macro.mall.dto.CmsTraceabilityProduct;
import com.macro.mall.mapper.CmsTraceabilityProductRelationMapper;
import com.macro.mall.model.CmsTraceabilityProductRelation;
import com.macro.mall.model.CmsTraceabilityProductRelationExample;
import com.macro.mall.service.CmsTraceabilityProductRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 追溯与产品关系管理Service实现类
 */
@Service
public class CmsTraceabilityProductRelationServiceImpl implements CmsTraceabilityProductRelationService {
    @Autowired
    private CmsTraceabilityProductRelationMapper relationMapper;
    @Autowired
    private CmsTraceabilityProductRelationDao relationDao;
    @Override
    public int create(List<CmsTraceabilityProductRelation> relationList) {
        for (CmsTraceabilityProductRelation relation : relationList) {
            relationMapper.insert(relation);
        }
        return relationList.size();
    }

    @Override
    public int update(Long id, CmsTraceabilityProductRelation relation) {
        relation.setId(id);
        return relationMapper.updateByPrimaryKey(relation);
    }

    @Override
    public int delete(Long id) {
        return relationMapper.deleteByPrimaryKey(id);
    }

    @Override
    public CmsTraceabilityProductRelation getItem(Long id) {
        return relationMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<CmsTraceabilityProduct> list(Long traceabilityId, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        return relationDao.getList(traceabilityId);  // 使用自定义的 DAO 方法获取包含商品信息的列表
    }


    @Override
    public long getCount(Long traceabilityId) {
        CmsTraceabilityProductRelationExample example = new CmsTraceabilityProductRelationExample();
        if (traceabilityId != null) {
            example.createCriteria().andTraceabilityIdEqualTo(traceabilityId);
        }
        return relationMapper.countByExample(example);
    }
}
