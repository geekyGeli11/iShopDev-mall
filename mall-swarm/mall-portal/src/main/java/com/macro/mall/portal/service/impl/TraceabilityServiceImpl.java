package com.macro.mall.portal.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.portal.dao.TraceabilityProductRelationDao;
import com.macro.mall.portal.dto.TraceabilityProduct;
import com.macro.mall.mapper.*;
import com.macro.mall.model.CmsTraceabilityList;
import com.macro.mall.model.CmsTraceabilityListExample;
import com.macro.mall.portal.domain.TraceabilityDetail;
import com.macro.mall.portal.service.TraceabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TraceabilityServiceImpl implements TraceabilityService {

    @Autowired
    private CmsTraceabilityListMapper traceabilityListMapper;

    @Autowired
    private TraceabilityProductRelationDao relationDao;
    @Override
    public List<CmsTraceabilityList> getTop3ByCategory(String category) {
        // 使用 Example 构建查询条件
        CmsTraceabilityListExample example = new CmsTraceabilityListExample();
        example.createCriteria().andCategoryEqualTo(category);
        example.setOrderByClause("create_time DESC LIMIT 4");
        return traceabilityListMapper.selectByExample(example);
    }
    @Override
    public List<CmsTraceabilityList> listByCategory(String category, int pageNum, int pageSize) {
        // 设置分页参数
        PageHelper.startPage(pageNum, pageSize);

        // 使用 Example 构建查询条件
        CmsTraceabilityListExample example = new CmsTraceabilityListExample();
        example.createCriteria().andCategoryEqualTo(category);
        example.setOrderByClause("create_time DESC");

        return traceabilityListMapper.selectByExample(example);
    }

    @Override
    public TraceabilityDetail getDetailWithProducts(Integer traceabilityId, int pageNum, int pageSize) {
        // 查询溯源记录
        CmsTraceabilityList traceability = traceabilityListMapper.selectByPrimaryKey(traceabilityId);
        if (traceability == null) {
            return null;
        }

        // 分页查询关联商品
        PageHelper.startPage(pageNum, pageSize);
        List<TraceabilityProduct> productRelations = relationDao.getList(Long.valueOf(traceabilityId));
        CommonPage<TraceabilityProduct> pagedRelations = CommonPage.restPage(productRelations);

        // 构建返回对象
        TraceabilityDetail detail = new TraceabilityDetail();
        detail.setTraceability(traceability);
        detail.setProductRelations(productRelations);
        detail.setProductRelationsPageInfo(pagedRelations);

        return detail;
    }

}
