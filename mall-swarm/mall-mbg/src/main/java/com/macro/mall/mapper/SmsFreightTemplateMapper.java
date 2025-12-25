package com.macro.mall.mapper;

import com.macro.mall.model.SmsFreightTemplate;
import com.macro.mall.model.SmsFreightTemplateExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SmsFreightTemplateMapper {
    long countByExample(SmsFreightTemplateExample example);

    int deleteByExample(SmsFreightTemplateExample example);

    int deleteByPrimaryKey(Long id);

    int insert(SmsFreightTemplate row);

    int insertSelective(SmsFreightTemplate row);

    List<SmsFreightTemplate> selectByExample(SmsFreightTemplateExample example);

    SmsFreightTemplate selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") SmsFreightTemplate row, @Param("example") SmsFreightTemplateExample example);

    int updateByExample(@Param("row") SmsFreightTemplate row, @Param("example") SmsFreightTemplateExample example);

    int updateByPrimaryKeySelective(SmsFreightTemplate row);

    int updateByPrimaryKey(SmsFreightTemplate row);
}