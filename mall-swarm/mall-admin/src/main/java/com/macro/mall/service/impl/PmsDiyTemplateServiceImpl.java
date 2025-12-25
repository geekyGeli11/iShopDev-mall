package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.dto.PmsDiyTemplateDetailVO;
import com.macro.mall.mapper.PmsDiyTemplateMapper;
import com.macro.mall.model.PmsDiyArea;
import com.macro.mall.model.PmsDiyTemplate;
import com.macro.mall.model.PmsDiyTemplateExample;
import com.macro.mall.model.PmsDiyTemplateSurface;
import com.macro.mall.service.PmsDiyAreaService;
import com.macro.mall.service.PmsDiyTemplateService;
import com.macro.mall.service.PmsDiyTemplateSurfaceService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * DIY模板管理Service实现类
 * Created by macro on 2024/12/20.
 */
@Service
public class PmsDiyTemplateServiceImpl implements PmsDiyTemplateService {
    
    @Autowired
    private PmsDiyTemplateMapper templateMapper;
    
    @Autowired
    private PmsDiyTemplateSurfaceService surfaceService;

    @Autowired
    private PmsDiyAreaService areaService;

    @Override
    public int create(PmsDiyTemplate template) {
        template.setCreateTime(new Date());
        template.setUpdateTime(new Date());
        if (template.getStatus() == null) {
            template.setStatus((byte) 1); // 默认启用
        }
        return templateMapper.insertSelective(template);
    }

    @Override
    public int update(Long id, PmsDiyTemplate template) {
        template.setId(id);
        template.setUpdateTime(new Date());
        return templateMapper.updateByPrimaryKeySelective(template);
    }

    @Override
    @Transactional
    public int delete(Long id) {
        // 删除模板关联的所有面和区域
        surfaceService.deleteByTemplateId(id);
        return templateMapper.deleteByPrimaryKey(id);
    }

    @Override
    @Transactional
    public int delete(List<Long> ids) {
        int count = 0;
        for (Long id : ids) {
            count += delete(id);
        }
        return count;
    }

    @Override
    public PmsDiyTemplate getItem(Long id) {
        return templateMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<PmsDiyTemplate> list(String keyword, Long productCategoryId, Byte status, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        PmsDiyTemplateExample example = new PmsDiyTemplateExample();
        PmsDiyTemplateExample.Criteria criteria = example.createCriteria();
        
        if (StringUtils.hasText(keyword)) {
            criteria.andNameLike("%" + keyword + "%");
        }
        if (productCategoryId != null) {
            criteria.andProductCategoryIdEqualTo(productCategoryId);
        }
        if (status != null) {
            criteria.andStatusEqualTo(status);
        }
        
        example.setOrderByClause("create_time desc");
        return templateMapper.selectByExample(example);
    }

    @Override
    public List<PmsDiyTemplate> listEnabled() {
        PmsDiyTemplateExample example = new PmsDiyTemplateExample();
        example.createCriteria().andStatusEqualTo((byte) 1);
        example.setOrderByClause("create_time desc");
        return templateMapper.selectByExample(example);
    }

    @Override
    public int updateStatus(List<Long> ids, Byte status) {
        PmsDiyTemplate template = new PmsDiyTemplate();
        template.setStatus(status);
        template.setUpdateTime(new Date());
        
        PmsDiyTemplateExample example = new PmsDiyTemplateExample();
        example.createCriteria().andIdIn(ids);
        return templateMapper.updateByExampleSelective(template, example);
    }

    @Override
    @Transactional
    public int copyTemplate(Long id, String newName) {
        PmsDiyTemplate sourceTemplate = templateMapper.selectByPrimaryKey(id);
        if (sourceTemplate == null) {
            return 0;
        }
        
        // 创建新模板
        PmsDiyTemplate newTemplate = new PmsDiyTemplate();
        newTemplate.setName(newName);
        newTemplate.setProductCategoryId(sourceTemplate.getProductCategoryId());
        newTemplate.setDescription(sourceTemplate.getDescription());
        newTemplate.setStatus((byte) 1);
        newTemplate.setCreateTime(new Date());
        newTemplate.setUpdateTime(new Date());
        
        int result = templateMapper.insertSelective(newTemplate);
        if (result > 0) {
            // 复制模板面和区域
            surfaceService.copySurfacesToTemplate(id, newTemplate.getId());
        }
        
        return result;
    }

    @Override
    public List<PmsDiyTemplate> listByProductCategory(Long productCategoryId) {
        PmsDiyTemplateExample example = new PmsDiyTemplateExample();
        PmsDiyTemplateExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo((byte) 1);
        if (productCategoryId != null) {
            criteria.andProductCategoryIdEqualTo(productCategoryId);
        }
        example.setOrderByClause("create_time desc");
        return templateMapper.selectByExample(example);
    }

    @Override
    public PmsDiyTemplateDetailVO getTemplateDetail(Long id) {
        PmsDiyTemplate template = templateMapper.selectByPrimaryKey(id);
        if (template == null) {
            return null;
        }

        PmsDiyTemplateDetailVO detailVO = new PmsDiyTemplateDetailVO();
        BeanUtils.copyProperties(template, detailVO);

        // 获取模板面列表
        List<PmsDiyTemplateSurface> surfaces = surfaceService.listByTemplateId(id);
        List<PmsDiyTemplateDetailVO.SurfaceDetailVO> surfaceDetailVOs = new ArrayList<>();

        for (PmsDiyTemplateSurface surface : surfaces) {
            PmsDiyTemplateDetailVO.SurfaceDetailVO surfaceDetailVO = new PmsDiyTemplateDetailVO.SurfaceDetailVO();
            BeanUtils.copyProperties(surface, surfaceDetailVO);

            // 获取面的区域列表
            List<PmsDiyArea> areas = areaService.listBySurfaceId(surface.getId());
            surfaceDetailVO.setAreas(areas);

            surfaceDetailVOs.add(surfaceDetailVO);
        }

        detailVO.setSurfaces(surfaceDetailVOs);
        return detailVO;
    }
}
