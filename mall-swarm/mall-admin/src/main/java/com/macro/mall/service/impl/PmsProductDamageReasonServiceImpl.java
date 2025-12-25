package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.mapper.PmsProductDamageReasonMapper;
import com.macro.mall.model.PmsProductDamageReason;
import com.macro.mall.model.PmsProductDamageReasonExample;
import com.macro.mall.service.PmsProductDamageReasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 报损原因配置Service实现类
 */
@Service
public class PmsProductDamageReasonServiceImpl implements PmsProductDamageReasonService {
    
    @Autowired
    private PmsProductDamageReasonMapper damageReasonMapper;
    
    @Override
    public int create(PmsProductDamageReason reason) {
        reason.setCreateTime(new Date());
        reason.setUpdateTime(new Date());
        if (reason.getStatus() == null) {
            reason.setStatus((byte) 1);
        }
        if (reason.getSort() == null) {
            reason.setSort(0);
        }
        return damageReasonMapper.insertSelective(reason);
    }
    
    @Override
    public int update(Long id, PmsProductDamageReason reason) {
        reason.setId(id);
        reason.setUpdateTime(new Date());
        return damageReasonMapper.updateByPrimaryKeySelective(reason);
    }
    
    @Override
    public int delete(Long id) {
        return damageReasonMapper.deleteByPrimaryKey(id);
    }
    
    @Override
    public List<PmsProductDamageReason> listAll() {
        PmsProductDamageReasonExample example = new PmsProductDamageReasonExample();
        example.createCriteria().andStatusEqualTo((byte) 1);
        example.setOrderByClause("sort asc, id asc");
        return damageReasonMapper.selectByExample(example);
    }
    
    @Override
    public List<PmsProductDamageReason> listByType(Integer type) {
        PmsProductDamageReasonExample example = new PmsProductDamageReasonExample();
        example.createCriteria()
                .andStatusEqualTo((byte) 1)
                .andTypeEqualTo(type.byteValue());
        example.setOrderByClause("sort asc, id asc");
        return damageReasonMapper.selectByExample(example);
    }
    
    @Override
    public List<PmsProductDamageReason> list(String keyword, Integer type, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        PmsProductDamageReasonExample example = new PmsProductDamageReasonExample();
        PmsProductDamageReasonExample.Criteria criteria = example.createCriteria();
        if (StringUtils.hasText(keyword)) {
            criteria.andNameLike("%" + keyword + "%");
        }
        if (type != null) {
            criteria.andTypeEqualTo(type.byteValue());
        }
        example.setOrderByClause("sort asc, id asc");
        return damageReasonMapper.selectByExample(example);
    }
    
    @Override
    public PmsProductDamageReason getItem(Long id) {
        return damageReasonMapper.selectByPrimaryKey(id);
    }
    
    @Override
    public int updateStatus(Long id, Integer status) {
        PmsProductDamageReason reason = new PmsProductDamageReason();
        reason.setId(id);
        reason.setStatus(status.byteValue());
        reason.setUpdateTime(new Date());
        return damageReasonMapper.updateByPrimaryKeySelective(reason);
    }
}
