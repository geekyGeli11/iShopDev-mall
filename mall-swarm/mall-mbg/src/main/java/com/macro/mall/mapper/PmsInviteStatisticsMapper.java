package com.macro.mall.mapper;

import com.macro.mall.model.PmsInviteStatistics;
import com.macro.mall.model.PmsInviteStatisticsExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PmsInviteStatisticsMapper {
    long countByExample(PmsInviteStatisticsExample example);

    int deleteByExample(PmsInviteStatisticsExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsInviteStatistics row);

    int insertSelective(PmsInviteStatistics row);

    List<PmsInviteStatistics> selectByExample(PmsInviteStatisticsExample example);

    PmsInviteStatistics selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") PmsInviteStatistics row, @Param("example") PmsInviteStatisticsExample example);

    int updateByExample(@Param("row") PmsInviteStatistics row, @Param("example") PmsInviteStatisticsExample example);

    int updateByPrimaryKeySelective(PmsInviteStatistics row);

    int updateByPrimaryKey(PmsInviteStatistics row);
}