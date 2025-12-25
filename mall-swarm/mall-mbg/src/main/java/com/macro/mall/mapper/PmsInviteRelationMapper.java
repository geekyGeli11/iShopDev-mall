package com.macro.mall.mapper;

import com.macro.mall.model.PmsInviteRelation;
import com.macro.mall.model.PmsInviteRelationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PmsInviteRelationMapper {
    long countByExample(PmsInviteRelationExample example);

    int deleteByExample(PmsInviteRelationExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsInviteRelation row);

    int insertSelective(PmsInviteRelation row);

    List<PmsInviteRelation> selectByExample(PmsInviteRelationExample example);

    PmsInviteRelation selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") PmsInviteRelation row, @Param("example") PmsInviteRelationExample example);

    int updateByExample(@Param("row") PmsInviteRelation row, @Param("example") PmsInviteRelationExample example);

    int updateByPrimaryKeySelective(PmsInviteRelation row);

    int updateByPrimaryKey(PmsInviteRelation row);
}