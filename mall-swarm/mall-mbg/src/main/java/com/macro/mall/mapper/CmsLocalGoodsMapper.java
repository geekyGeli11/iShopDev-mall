package com.macro.mall.mapper;

import com.macro.mall.model.CmsLocalGoods;
import com.macro.mall.model.CmsLocalGoodsExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CmsLocalGoodsMapper {
    long countByExample(CmsLocalGoodsExample example);

    int deleteByExample(CmsLocalGoodsExample example);

    int deleteByPrimaryKey(Long id);

    int insert(CmsLocalGoods row);

    int insertSelective(CmsLocalGoods row);

    List<CmsLocalGoods> selectByExampleWithBLOBs(CmsLocalGoodsExample example);

    List<CmsLocalGoods> selectByExample(CmsLocalGoodsExample example);

    CmsLocalGoods selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") CmsLocalGoods row, @Param("example") CmsLocalGoodsExample example);

    int updateByExampleWithBLOBs(@Param("row") CmsLocalGoods row, @Param("example") CmsLocalGoodsExample example);

    int updateByExample(@Param("row") CmsLocalGoods row, @Param("example") CmsLocalGoodsExample example);

    int updateByPrimaryKeySelective(CmsLocalGoods row);

    int updateByPrimaryKeyWithBLOBs(CmsLocalGoods row);

    int updateByPrimaryKey(CmsLocalGoods row);
}