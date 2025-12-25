package com.macro.mall.mapper;

import com.macro.mall.model.SmsFreightTemplateRegion;
import com.macro.mall.model.SmsFreightTemplateRegionExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SmsFreightTemplateRegionMapper {
    long countByExample(SmsFreightTemplateRegionExample example);

    int deleteByExample(SmsFreightTemplateRegionExample example);

    int deleteByPrimaryKey(Long id);

    int insert(SmsFreightTemplateRegion row);

    int insertSelective(SmsFreightTemplateRegion row);

    List<SmsFreightTemplateRegion> selectByExampleWithBLOBs(SmsFreightTemplateRegionExample example);

    List<SmsFreightTemplateRegion> selectByExample(SmsFreightTemplateRegionExample example);

    SmsFreightTemplateRegion selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") SmsFreightTemplateRegion row, @Param("example") SmsFreightTemplateRegionExample example);

    int updateByExampleWithBLOBs(@Param("row") SmsFreightTemplateRegion row, @Param("example") SmsFreightTemplateRegionExample example);

    int updateByExample(@Param("row") SmsFreightTemplateRegion row, @Param("example") SmsFreightTemplateRegionExample example);

    int updateByPrimaryKeySelective(SmsFreightTemplateRegion row);

    int updateByPrimaryKeyWithBLOBs(SmsFreightTemplateRegion row);

    int updateByPrimaryKey(SmsFreightTemplateRegion row);
}