package com.macro.mall.mapper;

import com.macro.mall.model.VSelfcheckStats;
import com.macro.mall.model.VSelfcheckStatsExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface VSelfcheckStatsMapper {
    long countByExample(VSelfcheckStatsExample example);

    int deleteByExample(VSelfcheckStatsExample example);

    int insert(VSelfcheckStats row);

    int insertSelective(VSelfcheckStats row);

    List<VSelfcheckStats> selectByExample(VSelfcheckStatsExample example);

    int updateByExampleSelective(@Param("row") VSelfcheckStats row, @Param("example") VSelfcheckStatsExample example);

    int updateByExample(@Param("row") VSelfcheckStats row, @Param("example") VSelfcheckStatsExample example);
}