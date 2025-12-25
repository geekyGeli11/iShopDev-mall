package com.macro.mall.mapper;

import com.macro.mall.model.UmsSigninRewardConfig;
import com.macro.mall.model.UmsSigninRewardConfigExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UmsSigninRewardConfigMapper {
    long countByExample(UmsSigninRewardConfigExample example);

    int deleteByExample(UmsSigninRewardConfigExample example);

    int deleteByPrimaryKey(Long id);

    int insert(UmsSigninRewardConfig row);

    int insertSelective(UmsSigninRewardConfig row);

    List<UmsSigninRewardConfig> selectByExample(UmsSigninRewardConfigExample example);

    UmsSigninRewardConfig selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") UmsSigninRewardConfig row, @Param("example") UmsSigninRewardConfigExample example);

    int updateByExample(@Param("row") UmsSigninRewardConfig row, @Param("example") UmsSigninRewardConfigExample example);

    int updateByPrimaryKeySelective(UmsSigninRewardConfig row);

    int updateByPrimaryKey(UmsSigninRewardConfig row);
}