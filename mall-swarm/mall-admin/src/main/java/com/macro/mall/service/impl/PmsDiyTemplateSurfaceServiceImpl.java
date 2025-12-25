package com.macro.mall.service.impl;

import com.macro.mall.mapper.PmsDiyTemplateSurfaceMapper;
import com.macro.mall.model.PmsDiyTemplateSurface;
import com.macro.mall.model.PmsDiyTemplateSurfaceExample;
import com.macro.mall.service.PmsDiyAreaService;
import com.macro.mall.service.PmsDiyTemplateSurfaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * DIY模板面管理Service实现类
 * Created by macro on 2024/12/20.
 */
@Service
public class PmsDiyTemplateSurfaceServiceImpl implements PmsDiyTemplateSurfaceService {
    
    @Autowired
    private PmsDiyTemplateSurfaceMapper surfaceMapper;
    
    @Autowired
    private PmsDiyAreaService areaService;

    @Override
    public int create(PmsDiyTemplateSurface surface) {
        surface.setCreateTime(new Date());
        if (surface.getSort() == null) {
            surface.setSort(0);
        }
        return surfaceMapper.insertSelective(surface);
    }

    @Override
    public int update(Long id, PmsDiyTemplateSurface surface) {
        surface.setId(id);
        return surfaceMapper.updateByPrimaryKeySelective(surface);
    }

    @Override
    @Transactional
    public int delete(Long id) {
        // 删除面关联的所有区域
        areaService.deleteBySurfaceId(id);
        return surfaceMapper.deleteByPrimaryKey(id);
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
    public PmsDiyTemplateSurface getItem(Long id) {
        return surfaceMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<PmsDiyTemplateSurface> listByTemplateId(Long templateId) {
        PmsDiyTemplateSurfaceExample example = new PmsDiyTemplateSurfaceExample();
        example.createCriteria().andTemplateIdEqualTo(templateId);
        example.setOrderByClause("sort asc, create_time asc");
        return surfaceMapper.selectByExample(example);
    }

    @Override
    @Transactional
    public int deleteByTemplateId(Long templateId) {
        // 先获取所有面，然后删除每个面的区域
        List<PmsDiyTemplateSurface> surfaces = listByTemplateId(templateId);
        for (PmsDiyTemplateSurface surface : surfaces) {
            areaService.deleteBySurfaceId(surface.getId());
        }
        
        // 删除所有面
        PmsDiyTemplateSurfaceExample example = new PmsDiyTemplateSurfaceExample();
        example.createCriteria().andTemplateIdEqualTo(templateId);
        return surfaceMapper.deleteByExample(example);
    }

    @Override
    @Transactional
    public int copySurfacesToTemplate(Long sourceTemplateId, Long targetTemplateId) {
        List<PmsDiyTemplateSurface> sourceSurfaces = listByTemplateId(sourceTemplateId);
        int count = 0;
        
        for (PmsDiyTemplateSurface sourceSurface : sourceSurfaces) {
            // 创建新面
            PmsDiyTemplateSurface newSurface = new PmsDiyTemplateSurface();
            newSurface.setTemplateId(targetTemplateId);
            newSurface.setName(sourceSurface.getName());
            newSurface.setExampleImage(sourceSurface.getExampleImage());
            newSurface.setSort(sourceSurface.getSort());
            newSurface.setCreateTime(new Date());
            
            int result = surfaceMapper.insertSelective(newSurface);
            if (result > 0) {
                count++;
                // 复制区域
                areaService.copyAreasToSurface(sourceSurface.getId(), newSurface.getId());
            }
        }
        
        return count;
    }
}
