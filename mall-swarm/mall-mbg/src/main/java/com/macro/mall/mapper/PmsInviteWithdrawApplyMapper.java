package com.macro.mall.mapper;

import com.macro.mall.model.PmsInviteWithdrawApply;
import com.macro.mall.model.PmsInviteWithdrawApplyExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PmsInviteWithdrawApplyMapper {
    long countByExample(PmsInviteWithdrawApplyExample example);

    int deleteByExample(PmsInviteWithdrawApplyExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsInviteWithdrawApply row);

    int insertSelective(PmsInviteWithdrawApply row);

    List<PmsInviteWithdrawApply> selectByExample(PmsInviteWithdrawApplyExample example);

    PmsInviteWithdrawApply selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") PmsInviteWithdrawApply row, @Param("example") PmsInviteWithdrawApplyExample example);

    int updateByExample(@Param("row") PmsInviteWithdrawApply row, @Param("example") PmsInviteWithdrawApplyExample example);

    int updateByPrimaryKeySelective(PmsInviteWithdrawApply row);

    int updateByPrimaryKey(PmsInviteWithdrawApply row);
}