package com.macro.mall.mapper;

import com.macro.mall.model.PmsDiyTemplate;
import com.macro.mall.model.PmsDiyTemplateExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PmsDiyTemplateMapper {
    long countByExample(PmsDiyTemplateExample example);

    int deleteByExample(PmsDiyTemplateExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsDiyTemplate row);

    int insertSelective(PmsDiyTemplate row);

    List<PmsDiyTemplate> selectByExample(PmsDiyTemplateExample example);

    PmsDiyTemplate selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") PmsDiyTemplate row, @Param("example") PmsDiyTemplateExample example);

    int updateByExample(@Param("row") PmsDiyTemplate row, @Param("example") PmsDiyTemplateExample example);

    int updateByPrimaryKeySelective(PmsDiyTemplate row);

    int updateByPrimaryKey(PmsDiyTemplate row);
}