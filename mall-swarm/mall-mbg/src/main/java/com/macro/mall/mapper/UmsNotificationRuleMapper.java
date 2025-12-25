package com.macro.mall.mapper;

import com.macro.mall.model.UmsNotificationRule;
import com.macro.mall.model.UmsNotificationRuleExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UmsNotificationRuleMapper {
    long countByExample(UmsNotificationRuleExample example);

    int deleteByExample(UmsNotificationRuleExample example);

    int deleteByPrimaryKey(Long id);

    int insert(UmsNotificationRule row);

    int insertSelective(UmsNotificationRule row);

    List<UmsNotificationRule> selectByExample(UmsNotificationRuleExample example);

    UmsNotificationRule selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") UmsNotificationRule row, @Param("example") UmsNotificationRuleExample example);

    int updateByExample(@Param("row") UmsNotificationRule row, @Param("example") UmsNotificationRuleExample example);

    int updateByPrimaryKeySelective(UmsNotificationRule row);

    int updateByPrimaryKey(UmsNotificationRule row);
}