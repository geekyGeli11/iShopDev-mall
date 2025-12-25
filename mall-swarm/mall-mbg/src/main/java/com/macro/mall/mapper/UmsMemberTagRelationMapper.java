package com.macro.mall.mapper;

import com.macro.mall.model.UmsMemberTagRelation;
import com.macro.mall.model.UmsMemberTagRelationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UmsMemberTagRelationMapper {
    long countByExample(UmsMemberTagRelationExample example);

    int deleteByExample(UmsMemberTagRelationExample example);

    int deleteByPrimaryKey(Long id);

    int insert(UmsMemberTagRelation row);

    int insertSelective(UmsMemberTagRelation row);

    List<UmsMemberTagRelation> selectByExample(UmsMemberTagRelationExample example);

    UmsMemberTagRelation selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") UmsMemberTagRelation row, @Param("example") UmsMemberTagRelationExample example);

    int updateByExample(@Param("row") UmsMemberTagRelation row, @Param("example") UmsMemberTagRelationExample example);

    int updateByPrimaryKeySelective(UmsMemberTagRelation row);

    int updateByPrimaryKey(UmsMemberTagRelation row);
}