package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.dao.CmsLocalGoodsRelationDao;
import com.macro.mall.dto.CmsLocalGoodsDetail;
import com.macro.mall.dto.CmsLocalGoodsParam;
import com.macro.mall.mapper.CmsLocalGoodsMapper;
import com.macro.mall.model.CmsLocalGoods;
import com.macro.mall.model.CmsLocalGoodsExample;
import com.macro.mall.service.CmsLocalGoodsRelationService;
import com.macro.mall.service.CmsLocalGoodsService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 本地好物管理Service实现类
 */
@Service
public class CmsLocalGoodsServiceImpl implements CmsLocalGoodsService {

    @Autowired
    private CmsLocalGoodsMapper localGoodsMapper;
    
    @Autowired
    private CmsLocalGoodsRelationService relationService;
    
    @Autowired
    private CmsLocalGoodsRelationDao relationDao;

    @Override
    public CmsLocalGoods create(CmsLocalGoods localGoods) {
        localGoods.setCreateTime(new Date());
        localGoods.setUpdateTime(new Date());
        localGoodsMapper.insert(localGoods);
        return localGoods;
    }
    
    @Override
    @Transactional
    public CmsLocalGoods create(CmsLocalGoodsParam localGoodsParam) {
        // 创建本地好物基本信息
        CmsLocalGoods localGoods = new CmsLocalGoods();
        BeanUtils.copyProperties(localGoodsParam, localGoods);
        localGoods.setCreateTime(new Date());
        localGoods.setUpdateTime(new Date());
        localGoodsMapper.insert(localGoods);
        
        // 处理商品关联
        if (localGoodsParam.getProductIds() != null && !localGoodsParam.getProductIds().isEmpty()) {
            relationService.create(localGoods.getId(), localGoodsParam.getProductIds());
        }
        
        return localGoods;
    }

    @Override
    public CmsLocalGoods update(Long id, CmsLocalGoods localGoods) {
        CmsLocalGoods existing = localGoodsMapper.selectByPrimaryKey(id);
        if (existing == null) {
            return null;
        }
        localGoods.setId(id);
        localGoods.setUpdateTime(new Date());
        localGoodsMapper.updateByPrimaryKeyWithBLOBs(localGoods);
        return localGoods;
    }
    
    @Override
    @Transactional
    public CmsLocalGoods update(Long id, CmsLocalGoodsParam localGoodsParam) {
        // 更新本地好物基本信息
        CmsLocalGoods localGoods = new CmsLocalGoods();
        BeanUtils.copyProperties(localGoodsParam, localGoods);
        localGoods.setId(id);
        localGoods.setUpdateTime(new Date());
        localGoodsMapper.updateByPrimaryKeyWithBLOBs(localGoods);
        
        // 先删除原有关联
        relationService.deleteByLocalGoodsId(id);
        
        // 添加新的关联
        if (localGoodsParam.getProductIds() != null && !localGoodsParam.getProductIds().isEmpty()) {
            relationService.create(id, localGoodsParam.getProductIds());
        }
        
        return localGoods;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        // 先删除商品关联
        relationService.deleteByLocalGoodsId(id);
        // 再删除好物本身
        int count = localGoodsMapper.deleteByPrimaryKey(id);
        return count > 0;
    }

    @Override
    public CmsLocalGoods getById(Long id) {
        return localGoodsMapper.selectByPrimaryKey(id);
    }
    
    @Override
    public CmsLocalGoodsDetail getDetailById(Long id) {
        CmsLocalGoods localGoods = localGoodsMapper.selectByPrimaryKey(id);
        if (localGoods == null) {
            return null;
        }
        
        CmsLocalGoodsDetail detail = new CmsLocalGoodsDetail();
        BeanUtils.copyProperties(localGoods, detail);
        
        // 查询关联商品信息
        List<CmsLocalGoodsDetail.RelatedProduct> productList = relationDao.getRelatedProducts(id);
        detail.setProductList(productList);
        
        return detail;
    }

    @Override
    public List<CmsLocalGoods> listByFilters(String name, Integer type, Boolean status, Date startTime, Date endTime, int pageNum, int pageSize) {
        // 设置分页信息
        PageHelper.startPage(pageNum, pageSize);

        // 构建查询条件
        CmsLocalGoodsExample example = new CmsLocalGoodsExample();
        CmsLocalGoodsExample.Criteria criteria = example.createCriteria();

        if (StringUtils.hasText(name)) {
            criteria.andNameLike("%" + name + "%");
        }
        if (type != null) {
            criteria.andTypeEqualTo(type);
        }
        if (status != null) {
            criteria.andStatusEqualTo(status);
        }
        if (startTime != null) {
            criteria.andCreateTimeGreaterThanOrEqualTo(startTime);
        }
        if (endTime != null) {
            criteria.andCreateTimeLessThanOrEqualTo(endTime);
        }

        // 排序条件
        example.setOrderByClause("create_time DESC");

        return localGoodsMapper.selectByExampleWithBLOBs(example);
    }
    
    @Override
    public List<CmsLocalGoodsDetail> listDetailByFilters(String name, Integer type, Boolean status, Date startTime, Date endTime, int pageNum, int pageSize) {
        // 先获取基础列表
        List<CmsLocalGoods> basicList = listByFilters(name, type, status, startTime, endTime, pageNum, pageSize);
        
        // 转换为包含详情的列表
        List<CmsLocalGoodsDetail> detailList = new ArrayList<>(basicList.size());
        for (CmsLocalGoods item : basicList) {
            CmsLocalGoodsDetail detail = new CmsLocalGoodsDetail();
            BeanUtils.copyProperties(item, detail);
            
            // 获取商品关联信息
            List<CmsLocalGoodsDetail.RelatedProduct> productList = relationDao.getRelatedProducts(item.getId());
            detail.setProductList(productList);
            
            detailList.add(detail);
        }
        
        return detailList;
    }
} 