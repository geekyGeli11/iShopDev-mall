package com.macro.mall.mapper;

import com.macro.mall.model.PmsInviteParamLog;
import com.macro.mall.model.PmsInviteParamLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PmsInviteParamLogMapper {
    long countByExample(PmsInviteParamLogExample example);

    int deleteByExample(PmsInviteParamLogExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsInviteParamLog row);

    int insertSelective(PmsInviteParamLog row);

    List<PmsInviteParamLog> selectByExample(PmsInviteParamLogExample example);

    PmsInviteParamLog selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") PmsInviteParamLog row, @Param("example") PmsInviteParamLogExample example);

    int updateByExample(@Param("row") PmsInviteParamLog row, @Param("example") PmsInviteParamLogExample example);

    int updateByPrimaryKeySelective(PmsInviteParamLog row);

    int updateByPrimaryKey(PmsInviteParamLog row);
}