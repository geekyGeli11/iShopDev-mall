package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.mapper.OmsSchoolMapper;
import com.macro.mall.model.OmsSchool;
import com.macro.mall.model.OmsSchoolExample;
import com.macro.mall.service.OmsSchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 学校管理Service实现类
 */
@Service
public class OmsSchoolServiceImpl implements OmsSchoolService {

    @Autowired
    private OmsSchoolMapper schoolMapper;

    @Override
    public OmsSchool create(OmsSchool school) {
        // 检查学校名称是否已存在
        OmsSchoolExample example = new OmsSchoolExample();
        example.createCriteria().andSchoolNameEqualTo(school.getSchoolName());
        List<OmsSchool> existingSchools = schoolMapper.selectByExample(example);
        if (!existingSchools.isEmpty()) {
            throw new RuntimeException("学校名称已存在");
        }

        school.setCreateTime(new Date());
        school.setUpdateTime(new Date());
        if (school.getStatus() == null) {
            school.setStatus(true); // 默认启用
        }
        schoolMapper.insert(school);
        return school;
    }

    @Override
    public OmsSchool update(Long id, OmsSchool school) {
        OmsSchool existing = schoolMapper.selectByPrimaryKey(id);
        if (existing == null) {
            return null;
        }

        // 检查学校名称是否已存在（排除当前学校）
        if (StringUtils.hasText(school.getSchoolName()) && 
            !school.getSchoolName().equals(existing.getSchoolName())) {
            OmsSchoolExample example = new OmsSchoolExample();
            example.createCriteria()
                .andSchoolNameEqualTo(school.getSchoolName())
                .andIdNotEqualTo(id);
            List<OmsSchool> existingSchools = schoolMapper.selectByExample(example);
            if (!existingSchools.isEmpty()) {
                throw new RuntimeException("学校名称已存在");
            }
        }

        school.setId(id);
        school.setUpdateTime(new Date());
        schoolMapper.updateByPrimaryKeySelective(school);
        return school;
    }

    @Override
    public boolean delete(Long id) {
        // 这里可以添加检查是否有门店关联该学校的逻辑
        // 如果有关联，可以选择阻止删除或者将关联的门店的school_id设为null
        int count = schoolMapper.deleteByPrimaryKey(id);
        return count > 0;
    }

    @Override
    public OmsSchool getById(Long id) {
        return schoolMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<OmsSchool> list(String schoolName, Boolean status, int pageNum, int pageSize) {
        // 设置分页信息
        PageHelper.startPage(pageNum, pageSize);

        // 构建查询条件
        OmsSchoolExample example = new OmsSchoolExample();
        OmsSchoolExample.Criteria criteria = example.createCriteria();

        if (StringUtils.hasText(schoolName)) {
            criteria.andSchoolNameLike("%" + schoolName + "%");
        }
        if (status != null) {
            criteria.andStatusEqualTo(status);
        }

        // 排序条件：按创建时间倒序
        example.setOrderByClause("create_time DESC");

        return schoolMapper.selectByExample(example);
    }

    @Override
    public List<OmsSchool> listEnabled() {
        OmsSchoolExample example = new OmsSchoolExample();
        example.createCriteria().andStatusEqualTo(true);
        example.setOrderByClause("school_name ASC");
        return schoolMapper.selectByExample(example);
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        OmsSchool school = new OmsSchool();
        school.setId(id);
        school.setStatus(status == 1); // 转换为Boolean类型
        school.setUpdateTime(new Date());
        int count = schoolMapper.updateByPrimaryKeySelective(school);
        return count > 0;
    }
}
