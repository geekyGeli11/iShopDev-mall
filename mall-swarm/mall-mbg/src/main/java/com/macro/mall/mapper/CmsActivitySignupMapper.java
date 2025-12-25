package com.macro.mall.mapper;

import com.macro.mall.model.CmsActivitySignup;
import com.macro.mall.model.CmsActivitySignupExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CmsActivitySignupMapper {
    long countByExample(CmsActivitySignupExample example);

    int deleteByExample(CmsActivitySignupExample example);

    int deleteByPrimaryKey(Long id);

    int insert(CmsActivitySignup row);

    int insertSelective(CmsActivitySignup row);

    List<CmsActivitySignup> selectByExample(CmsActivitySignupExample example);

    CmsActivitySignup selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") CmsActivitySignup row, @Param("example") CmsActivitySignupExample example);

    int updateByExample(@Param("row") CmsActivitySignup row, @Param("example") CmsActivitySignupExample example);

    int updateByPrimaryKeySelective(CmsActivitySignup row);

    int updateByPrimaryKey(CmsActivitySignup row);
}