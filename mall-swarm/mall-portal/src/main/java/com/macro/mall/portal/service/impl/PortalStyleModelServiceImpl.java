package com.macro.mall.portal.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.mapper.PmsStyleModelMapper;
import com.macro.mall.mapper.PmsStyleModelProductRelationMapper;
import com.macro.mall.mapper.PmsProductMapper;
import com.macro.mall.model.*;
import com.macro.mall.portal.domain.StyleModelCardResult;
import com.macro.mall.portal.service.PortalStyleModelService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 小程序端风格模型Service实现类
 * Created by macro on 2024/8/25.
 */
@Service
public class PortalStyleModelServiceImpl implements PortalStyleModelService {
    @Autowired
    private PmsStyleModelMapper styleModelMapper;
    @Autowired
    private PmsStyleModelProductRelationMapper relationMapper;
    @Autowired
    private PmsProductMapper productMapper;

    @Override
    public List<StyleModelCardResult> getStyleModelCards() {
        // 获取启用状态的风格模型
        PmsStyleModelExample example = new PmsStyleModelExample();
        example.createCriteria().andStatusEqualTo((byte) 1);
        example.setOrderByClause("sort asc, create_time desc");
        List<PmsStyleModel> styleModels = styleModelMapper.selectByExample(example);

        // 转换为卡片结果
        return styleModels.stream().map(styleModel -> {
            StyleModelCardResult card = new StyleModelCardResult();
            BeanUtils.copyProperties(styleModel, card);
            
            // 获取关联商品数量
            PmsStyleModelProductRelationExample relationExample = new PmsStyleModelProductRelationExample();
            relationExample.createCriteria().andStyleModelIdEqualTo(styleModel.getId());
            long productCount = relationMapper.countByExample(relationExample);
            card.setProductCount((int) productCount);
            
            return card;
        }).collect(Collectors.toList());
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
        
        // 查询商品信息，只返回上架状态的商品
        PmsProductExample productExample = new PmsProductExample();
        productExample.createCriteria()
                .andIdIn(productIds)
                .andPublishStatusEqualTo(1)  // 只返回已上架的商品
                .andDeleteStatusEqualTo(0);  // 只返回未删除的商品
        
        return productMapper.selectByExample(productExample);
    }

    @Override
    public List<PmsProduct> getStyleModelProductList(Long styleModelId, Integer page, Integer pageSize, String category) {
        PageHelper.startPage(page, pageSize);

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

        // 查询商品信息，只返回上架状态的商品
        PmsProductExample productExample = new PmsProductExample();
        PmsProductExample.Criteria criteria = productExample.createCriteria()
                .andIdIn(productIds)
                .andPublishStatusEqualTo(1)  // 只返回已上架的商品
                .andDeleteStatusEqualTo(0);  // 只返回未删除的商品

        // 如果指定了分类，添加分类筛选条件
        if (category != null && !category.trim().isEmpty()) {
            // 根据分类名称查找分类ID
            Long categoryId = getCategoryIdByName(category);
            if (categoryId != null) {
                criteria.andProductCategoryIdEqualTo(categoryId);
            }
        }

        return productMapper.selectByExample(productExample);
    }

    @Override
    public PmsStyleModel getStyleModelDetail(Long styleModelId) {
        return styleModelMapper.selectByPrimaryKey(styleModelId);
    }

    /**
     * 根据分类名称获取分类ID
     */
    private Long getCategoryIdByName(String categoryName) {
        // 这里可以根据实际的分类表结构来实现
        // 暂时返回null，表示不进行分类筛选
        switch (categoryName) {
            case "clothing":
                return 1L; // 衣帽服饰
            case "stationery":
                return 2L; // 文具办公
            case "lifestyle":
                return 3L; // 生活用品
            case "digital":
                return 4L; // 数码产品
            default:
                return null;
        }
    }
}
