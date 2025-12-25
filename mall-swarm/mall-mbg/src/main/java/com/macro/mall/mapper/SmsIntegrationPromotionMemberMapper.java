package com.macro.mall.mapper;

import com.macro.mall.model.SmsIntegrationPromotionMember;
import com.macro.mall.model.SmsIntegrationPromotionMemberExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SmsIntegrationPromotionMemberMapper {
    long countByExample(SmsIntegrationPromotionMemberExample example);

    int deleteByExample(SmsIntegrationPromotionMemberExample example);

    int deleteByPrimaryKey(Long id);

    int insert(SmsIntegrationPromotionMember row);

    int insertSelective(SmsIntegrationPromotionMember row);

    List<SmsIntegrationPromotionMember> selectByExample(SmsIntegrationPromotionMemberExample example);

    SmsIntegrationPromotionMember selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") SmsIntegrationPromotionMember row, @Param("example") SmsIntegrationPromotionMemberExample example);

    int updateByExample(@Param("row") SmsIntegrationPromotionMember row, @Param("example") SmsIntegrationPromotionMemberExample example);

    int updateByPrimaryKeySelective(SmsIntegrationPromotionMember row);

    int updateByPrimaryKey(SmsIntegrationPromotionMember row);
}