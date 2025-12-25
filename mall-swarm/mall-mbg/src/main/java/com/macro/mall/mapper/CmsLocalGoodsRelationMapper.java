package com.macro.mall.mapper;

import com.macro.mall.model.CmsLocalGoodsRelation;
import com.macro.mall.model.CmsLocalGoodsRelationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CmsLocalGoodsRelationMapper {
    long countByExample(CmsLocalGoodsRelationExample example);

    int deleteByExample(CmsLocalGoodsRelationExample example);

    int deleteByPrimaryKey(Long id);

    int insert(CmsLocalGoodsRelation row);

    int insertSelective(CmsLocalGoodsRelation row);

    List<CmsLocalGoodsRelation> selectByExample(CmsLocalGoodsRelationExample example);

    CmsLocalGoodsRelation selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") CmsLocalGoodsRelation row, @Param("example") CmsLocalGoodsRelationExample example);

    int updateByExample(@Param("row") CmsLocalGoodsRelation row, @Param("example") CmsLocalGoodsRelationExample example);

    int updateByPrimaryKeySelective(CmsLocalGoodsRelation row);

    int updateByPrimaryKey(CmsLocalGoodsRelation row);
}