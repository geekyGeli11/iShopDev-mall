package com.macro.mall.service.impl;

import com.macro.mall.mapper.PmsDiyAreaMapper;
import com.macro.mall.model.PmsDiyArea;
import com.macro.mall.model.PmsDiyAreaExample;
import com.macro.mall.service.PmsDiyAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * DIY区域管理Service实现类
 * Created by macro on 2024/12/20.
 */
@Service
public class PmsDiyAreaServiceImpl implements PmsDiyAreaService {
    
    @Autowired
    private PmsDiyAreaMapper areaMapper;

    @Override
    public int create(PmsDiyArea area) {
        area.setCreateTime(new Date());
        return areaMapper.insertSelective(area);
    }

    @Override
    public int update(Long id, PmsDiyArea area) {
        area.setId(id);
        return areaMapper.updateByPrimaryKeySelective(area);
    }

    @Override
    public int delete(Long id) {
        return areaMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int delete(List<Long> ids) {
        PmsDiyAreaExample example = new PmsDiyAreaExample();
        example.createCriteria().andIdIn(ids);
        return areaMapper.deleteByExample(example);
    }

    @Override
    public PmsDiyArea getItem(Long id) {
        return areaMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<PmsDiyArea> listBySurfaceId(Long surfaceId) {
        PmsDiyAreaExample example = new PmsDiyAreaExample();
        example.createCriteria().andSurfaceIdEqualTo(surfaceId);
        example.setOrderByClause("create_time asc");
        return areaMapper.selectByExampleWithBLOBs(example);
    }

    @Override
    public int deleteBySurfaceId(Long surfaceId) {
        PmsDiyAreaExample example = new PmsDiyAreaExample();
        example.createCriteria().andSurfaceIdEqualTo(surfaceId);
        return areaMapper.deleteByExample(example);
    }

    @Override
    public int copyAreasToSurface(Long sourceSurfaceId, Long targetSurfaceId) {
        List<PmsDiyArea> sourceAreas = listBySurfaceId(sourceSurfaceId);
        int count = 0;
        
        for (PmsDiyArea sourceArea : sourceAreas) {
            PmsDiyArea newArea = new PmsDiyArea();
            newArea.setSurfaceId(targetSurfaceId);
            newArea.setName(sourceArea.getName());
            newArea.setPathData(sourceArea.getPathData());
            newArea.setBounds(sourceArea.getBounds());
            newArea.setCreateTime(new Date());
            
            count += areaMapper.insertSelective(newArea);
        }
        
        return count;
    }
}
