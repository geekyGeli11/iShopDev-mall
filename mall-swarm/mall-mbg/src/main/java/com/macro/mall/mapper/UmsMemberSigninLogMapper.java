package com.macro.mall.mapper;

import com.macro.mall.model.UmsMemberSigninLog;
import com.macro.mall.model.UmsMemberSigninLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UmsMemberSigninLogMapper {
    long countByExample(UmsMemberSigninLogExample example);

    int deleteByExample(UmsMemberSigninLogExample example);

    int deleteByPrimaryKey(Long id);

    int insert(UmsMemberSigninLog row);

    int insertSelective(UmsMemberSigninLog row);

    List<UmsMemberSigninLog> selectByExample(UmsMemberSigninLogExample example);

    UmsMemberSigninLog selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") UmsMemberSigninLog row, @Param("example") UmsMemberSigninLogExample example);

    int updateByExample(@Param("row") UmsMemberSigninLog row, @Param("example") UmsMemberSigninLogExample example);

    int updateByPrimaryKeySelective(UmsMemberSigninLog row);

    int updateByPrimaryKey(UmsMemberSigninLog row);
}