package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.dto.PmsStyleModelParam;
import com.macro.mall.dto.PmsStyleModelQueryParam;
import com.macro.mall.mapper.PmsStyleModelMapper;
import com.macro.mall.mapper.PmsStyleModelProductRelationMapper;
import com.macro.mall.mapper.PmsProductMapper;
import com.macro.mall.model.*;
import com.macro.mall.service.PmsStyleModelService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 风格模型Service实现类
 * Created by macro on 2024/8/25.
 */
@Service
public class PmsStyleModelServiceImpl implements PmsStyleModelService {
    @Autowired
    private PmsStyleModelMapper styleModelMapper;
    @Autowired
    private PmsStyleModelProductRelationMapper relationMapper;
    @Autowired
    private PmsProductMapper productMapper;

    @Override
    public List<PmsStyleModel> listAllStyleModel() {
        PmsStyleModelExample example = new PmsStyleModelExample();
        example.setOrderByClause("sort asc, create_time desc");
        return styleModelMapper.selectByExample(example);
    }

    @Override
    public List<PmsStyleModel> list(PmsStyleModelQueryParam queryParam, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        PmsStyleModelExample example = new PmsStyleModelExample();
        PmsStyleModelExample.Criteria criteria = example.createCriteria();
        
        if (!StringUtils.isEmpty(queryParam.getKeyword())) {
            criteria.andNameLike("%" + queryParam.getKeyword() + "%");
        }
        if (queryParam.getStatus() != null) {
            criteria.andStatusEqualTo(queryParam.getStatus().byteValue());
        }
        
        example.setOrderByClause("sort asc, create_time desc");
        return styleModelMapper.selectByExample(example);
    }

    @Override
    public int createStyleModel(PmsStyleModelParam styleModelParam) {
        PmsStyleModel styleModel = new PmsStyleModel();
        BeanUtils.copyProperties(styleModelParam, styleModel);
        styleModel.setCreateTime(new Date());
        styleModel.setUpdateTime(new Date());
        if (styleModel.getSort() == null) {
            styleModel.setSort(0);
        }
        if (styleModel.getStatus() == null) {
            styleModel.setStatus((byte) 1);
        }
        return styleModelMapper.insertSelective(styleModel);
    }

    @Override
    public int updateStyleModel(Long id, PmsStyleModelParam styleModelParam) {
        PmsStyleModel styleModel = new PmsStyleModel();
        BeanUtils.copyProperties(styleModelParam, styleModel);
        styleModel.setId(id);
        styleModel.setUpdateTime(new Date());
        return styleModelMapper.updateByPrimaryKeySelective(styleModel);
    }

    @Override
    public int deleteStyleModel(Long id) {
        // 先删除关联关系
        PmsStyleModelProductRelationExample relationExample = new PmsStyleModelProductRelationExample();
        relationExample.createCriteria().andStyleModelIdEqualTo(id);
        relationMapper.deleteByExample(relationExample);
        
        // 再删除风格模型
        return styleModelMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int deleteStyleModel(List<Long> ids) {
        int count = 0;
        for (Long id : ids) {
            count += deleteStyleModel(id);
        }
        return count;
    }

    @Override
    public PmsStyleModel getStyleModel(Long id) {
        return styleModelMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateStatus(Long id, Integer status) {
        PmsStyleModel styleModel = new PmsStyleModel();
        styleModel.setId(id);
        styleModel.setStatus(status.byteValue());
        styleModel.setUpdateTime(new Date());
        return styleModelMapper.updateByPrimaryKeySelective(styleModel);
    }

    @Override
    public int updateStatus(List<Long> ids, Integer status) {
        int count = 0;
        for (Long id : ids) {
            count += updateStatus(id, status);
        }
        return count;
    }

    @Override
    public List<PmsProduct> getStyleModelProducts(Long styleModelId, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        
        // 获取关联的商品ID列表
        PmsStyleModelProductRelationExample relationExample = new PmsStyleModelProductRelationExample();
        relationExample.createCriteria().andStyleModelIdEqualTo(styleModelId);
        relationExample.setOrderByClause("sort asc, create_time desc");
        List<PmsStyleModelProductRelation> relations = relationMapper.selectByExample(relationExample);
        
        if (relations.isEmpty()) {
            return List.of();
        }
        
        List<Long> productIds = relations.stream()
                .map(PmsStyleModelProductRelation::getProductId)
                .collect(Collectors.toList());
        
        // 查询商品信息
        PmsProductExample productExample = new PmsProductExample();
        productExample.createCriteria().andIdIn(productIds);
        return productMapper.selectByExample(productExample);
    }

    @Override
    public int addProductsToStyleModel(Long styleModelId, List<Long> productIds) {
        // 先删除已存在的关联
        PmsStyleModelProductRelationExample example = new PmsStyleModelProductRelationExample();
        example.createCriteria().andStyleModelIdEqualTo(styleModelId).andProductIdIn(productIds);
        relationMapper.deleteByExample(example);
        
        // 添加新的关联
        int count = 0;
        for (int i = 0; i < productIds.size(); i++) {
            PmsStyleModelProductRelation relation = new PmsStyleModelProductRelation();
            relation.setStyleModelId(styleModelId);
            relation.setProductId(productIds.get(i));
            relation.setSort(i + 1);
            relation.setCreateTime(new Date());
            count += relationMapper.insertSelective(relation);
        }
        return count;
    }

    @Override
    public int removeProductsFromStyleModel(Long styleModelId, List<Long> productIds) {
        PmsStyleModelProductRelationExample example = new PmsStyleModelProductRelationExample();
        example.createCriteria().andStyleModelIdEqualTo(styleModelId).andProductIdIn(productIds);
        return relationMapper.deleteByExample(example);
    }

    @Override
    public int batchOperate(List<Long> ids, String operateType) {
        switch (operateType) {
            case "enableStyleModel":
                return updateStatus(ids, Integer.valueOf(1));
            case "disableStyleModel":
                return updateStatus(ids, Integer.valueOf(0));
            default:
                return 0;
        }
    }
}
