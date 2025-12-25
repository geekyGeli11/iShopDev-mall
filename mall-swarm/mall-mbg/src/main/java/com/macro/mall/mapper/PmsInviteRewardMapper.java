package com.macro.mall.mapper;

import com.macro.mall.model.PmsInviteReward;
import com.macro.mall.model.PmsInviteRewardExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PmsInviteRewardMapper {
    long countByExample(PmsInviteRewardExample example);

    int deleteByExample(PmsInviteRewardExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsInviteReward row);

    int insertSelective(PmsInviteReward row);

    List<PmsInviteReward> selectByExample(PmsInviteRewardExample example);

    PmsInviteReward selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") PmsInviteReward row, @Param("example") PmsInviteRewardExample example);

    int updateByExample(@Param("row") PmsInviteReward row, @Param("example") PmsInviteRewardExample example);

    int updateByPrimaryKeySelective(PmsInviteReward row);

    int updateByPrimaryKey(PmsInviteReward row);
}