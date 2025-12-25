package com.macro.mall.mapper;

import com.macro.mall.model.OmsSchool;
import com.macro.mall.model.OmsSchoolExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OmsSchoolMapper {
    long countByExample(OmsSchoolExample example);

    int deleteByExample(OmsSchoolExample example);

    int deleteByPrimaryKey(Long id);

    int insert(OmsSchool row);

    int insertSelective(OmsSchool row);

    List<OmsSchool> selectByExample(OmsSchoolExample example);

    OmsSchool selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") OmsSchool row, @Param("example") OmsSchoolExample example);

    int updateByExample(@Param("row") OmsSchool row, @Param("example") OmsSchoolExample example);

    int updateByPrimaryKeySelective(OmsSchool row);

    int updateByPrimaryKey(OmsSchool row);
}