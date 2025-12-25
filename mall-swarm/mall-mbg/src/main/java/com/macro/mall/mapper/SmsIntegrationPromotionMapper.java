package com.macro.mall.mapper;

import com.macro.mall.model.SmsIntegrationPromotion;
import com.macro.mall.model.SmsIntegrationPromotionExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SmsIntegrationPromotionMapper {
    long countByExample(SmsIntegrationPromotionExample example);

    int deleteByExample(SmsIntegrationPromotionExample example);

    int deleteByPrimaryKey(Long id);

    int insert(SmsIntegrationPromotion row);

    int insertSelective(SmsIntegrationPromotion row);

    List<SmsIntegrationPromotion> selectByExample(SmsIntegrationPromotionExample example);

    SmsIntegrationPromotion selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") SmsIntegrationPromotion row, @Param("example") SmsIntegrationPromotionExample example);

    int updateByExample(@Param("row") SmsIntegrationPromotion row, @Param("example") SmsIntegrationPromotionExample example);

    int updateByPrimaryKeySelective(SmsIntegrationPromotion row);

    int updateByPrimaryKey(SmsIntegrationPromotion row);
}