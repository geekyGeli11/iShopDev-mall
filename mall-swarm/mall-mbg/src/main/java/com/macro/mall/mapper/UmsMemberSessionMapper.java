package com.macro.mall.mapper;

import com.macro.mall.model.UmsMemberSession;
import com.macro.mall.model.UmsMemberSessionExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UmsMemberSessionMapper {
    long countByExample(UmsMemberSessionExample example);

    int deleteByExample(UmsMemberSessionExample example);

    int deleteByPrimaryKey(Long id);

    int insert(UmsMemberSession row);

    int insertSelective(UmsMemberSession row);

    List<UmsMemberSession> selectByExample(UmsMemberSessionExample example);

    UmsMemberSession selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") UmsMemberSession row, @Param("example") UmsMemberSessionExample example);

    int updateByExample(@Param("row") UmsMemberSession row, @Param("example") UmsMemberSessionExample example);

    int updateByPrimaryKeySelective(UmsMemberSession row);

    int updateByPrimaryKey(UmsMemberSession row);
}