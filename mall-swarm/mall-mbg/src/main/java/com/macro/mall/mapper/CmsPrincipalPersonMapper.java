package com.macro.mall.mapper;

import com.macro.mall.model.CmsPrincipalPerson;
import com.macro.mall.model.CmsPrincipalPersonExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CmsPrincipalPersonMapper {
    long countByExample(CmsPrincipalPersonExample example);

    int deleteByExample(CmsPrincipalPersonExample example);

    int deleteByPrimaryKey(Long id);

    int insert(CmsPrincipalPerson row);

    int insertSelective(CmsPrincipalPerson row);

    List<CmsPrincipalPerson> selectByExampleWithBLOBs(CmsPrincipalPersonExample example);

    List<CmsPrincipalPerson> selectByExample(CmsPrincipalPersonExample example);

    CmsPrincipalPerson selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") CmsPrincipalPerson row, @Param("example") CmsPrincipalPersonExample example);

    int updateByExampleWithBLOBs(@Param("row") CmsPrincipalPerson row, @Param("example") CmsPrincipalPersonExample example);

    int updateByExample(@Param("row") CmsPrincipalPerson row, @Param("example") CmsPrincipalPersonExample example);

    int updateByPrimaryKeySelective(CmsPrincipalPerson row);

    int updateByPrimaryKeyWithBLOBs(CmsPrincipalPerson row);

    int updateByPrimaryKey(CmsPrincipalPerson row);
}