package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.mapper.PmsDiyMaterialMapper;
import com.macro.mall.model.PmsDiyMaterial;
import com.macro.mall.model.PmsDiyMaterialExample;
import com.macro.mall.service.PmsDiyMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * DIY素材管理Service实现类
 * Created by macro on 2024/12/20.
 */
@Service
public class PmsDiyMaterialServiceImpl implements PmsDiyMaterialService {
    
    @Autowired
    private PmsDiyMaterialMapper materialMapper;

    @Override
    public int create(PmsDiyMaterial material) {
        material.setCreateTime(new Date());
        material.setUpdateTime(new Date());
        if (material.getStatus() == null) {
            material.setStatus((byte) 1); // 默认启用
        }
        if (material.getUsageCount() == null) {
            material.setUsageCount(0);
        }
        // 确保fileUrl不为空
        if (material.getFileUrl() == null || material.getFileUrl().trim().isEmpty()) {
            material.setFileUrl(""); // 设置为空字符串而不是null
        }
        // 确保fileType不为空，根据fileUrl自动推断文件类型
        if (material.getFileType() == null || material.getFileType().trim().isEmpty()) {
            String fileType = "unknown";
            if (material.getFileUrl() != null && !material.getFileUrl().isEmpty()) {
                String url = material.getFileUrl().toLowerCase();
                if (url.contains(".png")) fileType = "png";
                else if (url.contains(".jpg") || url.contains(".jpeg")) fileType = "jpg";
                else if (url.contains(".gif")) fileType = "gif";
                else if (url.contains(".svg")) fileType = "svg";
                else if (url.contains(".ttf")) fileType = "ttf";
                else if (url.contains(".otf")) fileType = "otf";
                else if (url.contains(".woff")) fileType = "woff";
            }
            material.setFileType(fileType);
        }
        return materialMapper.insertSelective(material);
    }

    @Override
    public int update(Long id, PmsDiyMaterial material) {
        material.setId(id);
        material.setUpdateTime(new Date());
        return materialMapper.updateByPrimaryKeySelective(material);
    }

    @Override
    public int delete(Long id) {
        return materialMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int delete(List<Long> ids) {
        PmsDiyMaterialExample example = new PmsDiyMaterialExample();
        example.createCriteria().andIdIn(ids);
        return materialMapper.deleteByExample(example);
    }

    @Override
    public PmsDiyMaterial getById(Long id) {
        return materialMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<PmsDiyMaterial> list(String keyword, Long categoryId, String fileType, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        PmsDiyMaterialExample example = new PmsDiyMaterialExample();
        example.setOrderByClause("create_time desc");
        PmsDiyMaterialExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(keyword)) {
            criteria.andNameLike("%" + keyword + "%");
        }
        if (categoryId != null) {
            criteria.andCategoryIdEqualTo(categoryId);
        }
        if (!StringUtils.isEmpty(fileType)) {
            criteria.andFileTypeEqualTo(fileType);
        }

        return materialMapper.selectByExample(example);
    }

    @Override
    public List<PmsDiyMaterial> listByCategory(Long categoryId) {
        PmsDiyMaterialExample example = new PmsDiyMaterialExample();
        example.setOrderByClause("create_time desc");
        PmsDiyMaterialExample.Criteria criteria = example.createCriteria();
        criteria.andCategoryIdEqualTo(categoryId);
        criteria.andStatusEqualTo((byte) 1); // 只返回启用的素材
        return materialMapper.selectByExample(example);
    }

    @Override
    public int updateStatus(List<Long> ids, Byte status) {
        PmsDiyMaterial material = new PmsDiyMaterial();
        material.setStatus(status);
        material.setUpdateTime(new Date());
        PmsDiyMaterialExample example = new PmsDiyMaterialExample();
        example.createCriteria().andIdIn(ids);
        return materialMapper.updateByExampleSelective(material, example);
    }

    @Override
    public int incrementUsageCount(Long id) {
        PmsDiyMaterial material = materialMapper.selectByPrimaryKey(id);
        if (material != null) {
            material.setUsageCount(material.getUsageCount() + 1);
            material.setUpdateTime(new Date());
            return materialMapper.updateByPrimaryKeySelective(material);
        }
        return 0;
    }

    @Override
    public int deleteBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }

        PmsDiyMaterialExample example = new PmsDiyMaterialExample();
        example.createCriteria().andIdIn(ids);
        return materialMapper.deleteByExample(example);
    }
}